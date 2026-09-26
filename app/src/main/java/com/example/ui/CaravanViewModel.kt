package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.audio.SilkRoadSoundEngine
import com.example.data.campaign.CampaignLevels
import com.example.data.local.CaravanDatabase
import com.example.data.local.CaravanRepository
import com.example.data.local.PlayerProgressEntity
import com.example.data.model.BiomeType
import com.example.data.model.DecorationType
import com.example.data.model.EnemyType
import com.example.data.model.GridPos
import com.example.data.model.LevelBlueprint
import com.example.data.model.WaveEntry
import com.example.data.model.WaveSpec
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class MainTab(val titleFa: String) {
    SILK_ROAD_MAP("نقشهٔ جاده"),
    BATTLE("نبرد کاروان"),
    OASIS_CAMP("اردوگاه و متا"),
    LEVEL_EDITOR("مرحله‌ساز بصری"),
    SPECIAL_MODES("طوفان و باس")
}

class CaravanViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: CaravanRepository =
        CaravanRepository(CaravanDatabase.getInstance(application).caravanDao())

    val customLevels: StateFlow<List<LevelBlueprint>> = repository.customLevelsFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val playerProgress: StateFlow<PlayerProgressEntity> = repository.playerProgressFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = PlayerProgressEntity()
        )

    private val _currentTab = MutableStateFlow(MainTab.SILK_ROAD_MAP)
    val currentTab: StateFlow<MainTab> = _currentTab.asStateFlow()

    private val _activeBattleLevel = MutableStateFlow(CampaignLevels.allLevels.first())
    val activeBattleLevel: StateFlow<LevelBlueprint> = _activeBattleLevel.asStateFlow()

    private val _battleSessionId = MutableStateFlow(1L)
    val battleSessionId: StateFlow<Long> = _battleSessionId.asStateFlow()

    private val _editorDraftLevel = MutableStateFlow(createFreshEditorTemplate())
    val editorDraftLevel: StateFlow<LevelBlueprint> = _editorDraftLevel.asStateFlow()

    private val _statusBanner = MutableStateFlow<String?>(null)
    val statusBanner: StateFlow<String?> = _statusBanner.asStateFlow()

    init {
        viewModelScope.launch {
            repository.ensureInitialized()
        }
        viewModelScope.launch {
            playerProgress.collect { progress ->
                SilkRoadSoundEngine.isSoundEnabled = progress.soundEnabled
            }
        }
    }

    fun selectTab(tab: MainTab) {
        _currentTab.value = tab
    }

    fun clearStatusBanner() {
        _statusBanner.value = null
    }

    fun showStatus(message: String) {
        _statusBanner.value = message
    }

    fun startBattleForLevel(level: LevelBlueprint) {
        _activeBattleLevel.value = level
        _battleSessionId.value = System.currentTimeMillis()
        _currentTab.value = MainTab.BATTLE
        SilkRoadSoundEngine.playCamelBell()
    }

    fun startEndlessSandstorm() {
        val endless = CampaignLevels.generateEndlessSandstormLevel()
        startBattleForLevel(endless)
    }

    fun startWeeklyBossRaid() {
        val raid = CampaignLevels.generateWeeklyBossRaidLevel()
        startBattleForLevel(raid)
    }

    fun openInLevelEditor(level: LevelBlueprint? = null) {
        _editorDraftLevel.value = level?.copy(isCustom = true) ?: createFreshEditorTemplate()
        _currentTab.value = MainTab.LEVEL_EDITOR
    }

    fun updateEditorDraft(updated: LevelBlueprint) {
        _editorDraftLevel.value = updated
    }

    fun saveEditorLevelAndNotify(blueprint: LevelBlueprint, playImmediately: Boolean = false) {
        viewModelScope.launch {
            val savedId = repository.saveCustomLevel(blueprint)
            val savedBlueprint = blueprint.copy(id = savedId, isCustom = true)
            _editorDraftLevel.value = savedBlueprint
            SilkRoadSoundEngine.playGoldChime()
            if (playImmediately) {
                startBattleForLevel(savedBlueprint)
            } else {
                _statusBanner.value = "مرحلهٔ «${blueprint.title}» با موفقیت در کارگاه ذخیره شد!"
            }
        }
    }

    fun deleteCustomLevel(id: Long) {
        viewModelScope.launch {
            repository.deleteCustomLevel(id)
            _statusBanner.value = "مرحلهٔ سفارشی حذف شد."
        }
    }

    fun onBattleVictory(
        level: LevelBlueprint,
        stars: Int,
        bonusGold: Int,
        bonusSilk: Int,
        bonusSaffron: Int,
        wavesCleared: Int
    ) {
        viewModelScope.launch {
            repository.recordVictory(
                level = level,
                stars = stars,
                bonusGold = bonusGold,
                bonusSilk = bonusSilk,
                bonusSaffron = bonusSaffron,
                wavesCleared = wavesCleared
            )
            SilkRoadSoundEngine.playGoldChime()
        }
    }

    fun toggleSound() {
        viewModelScope.launch {
            repository.updateProgress { current ->
                val next = !current.soundEnabled
                SilkRoadSoundEngine.isSoundEnabled = next
                current.copy(soundEnabled = next)
            }
        }
    }

    fun upgradeCaravanMeta(upgradeType: CaravanMetaUpgradeType) {
        viewModelScope.launch {
            repository.updateProgress { p ->
                val currentLv = when (upgradeType) {
                    CaravanMetaUpgradeType.WAGON_ARMOR -> p.wagonArmorLevel
                    CaravanMetaUpgradeType.CAMEL_SPEED_RELOCATE -> p.camelEquipLevel
                    CaravanMetaUpgradeType.PERMANENT_GUARD -> p.permanentGuardLevel
                    CaravanMetaUpgradeType.COMPANION_HERO -> p.heroRankLevel
                }
                val goldCost = 90 + currentLv * 65
                val silkCost = 10 + currentLv * 6
                if (p.goldCoins >= goldCost && p.silkCount >= silkCost && currentLv < 10) {
                    SilkRoadSoundEngine.playGoldChime()
                    _statusBanner.value = "ارتقای «${upgradeType.titleFa}» به سطح ${currentLv + 1} انجام شد!"
                    when (upgradeType) {
                        CaravanMetaUpgradeType.WAGON_ARMOR -> p.copy(
                            goldCoins = p.goldCoins - goldCost,
                            silkCount = p.silkCount - silkCost,
                            wagonArmorLevel = p.wagonArmorLevel + 1
                        )
                        CaravanMetaUpgradeType.CAMEL_SPEED_RELOCATE -> p.copy(
                            goldCoins = p.goldCoins - goldCost,
                            silkCount = p.silkCount - silkCost,
                            camelEquipLevel = p.camelEquipLevel + 1
                        )
                        CaravanMetaUpgradeType.PERMANENT_GUARD -> p.copy(
                            goldCoins = p.goldCoins - goldCost,
                            silkCount = p.silkCount - silkCost,
                            permanentGuardLevel = p.permanentGuardLevel + 1
                        )
                        CaravanMetaUpgradeType.COMPANION_HERO -> p.copy(
                            goldCoins = p.goldCoins - goldCost,
                            silkCount = p.silkCount - silkCost,
                            heroRankLevel = p.heroRankLevel + 1
                        )
                    }
                } else {
                    _statusBanner.value = "منابع کافی نیست! به $goldCost سکه و $silkCost ابریشم نیاز دارید."
                    p
                }
            }
        }
    }

    fun tradeSaffronForGoldAndSilk() {
        viewModelScope.launch {
            repository.updateProgress { p ->
                if (p.saffronCount >= 10) {
                    SilkRoadSoundEngine.playGoldChime()
                    _statusBanner.value = "۱۰ زعفران در بازار کاروانسرا با ۱۲۰ سکه و ۸ ابریشم معامله شد!"
                    p.copy(
                        saffronCount = p.saffronCount - 10,
                        goldCoins = p.goldCoins + 120,
                        silkCount = p.silkCount + 8
                    )
                } else {
                    _statusBanner.value = "برای این معامله حداقل ۱۰ زعفران نیاز دارید."
                    p
                }
            }
        }
    }

    fun tradeSilkForGold() {
        viewModelScope.launch {
            repository.updateProgress { p ->
                if (p.silkCount >= 15) {
                    SilkRoadSoundEngine.playGoldChime()
                    _statusBanner.value = "۱۵ طاقه ابریشم به ارزش ۱۵۰ سکهٔ طلا فروخته شد!"
                    p.copy(
                        silkCount = p.silkCount - 15,
                        goldCoins = p.goldCoins + 150
                    )
                } else {
                    _statusBanner.value = "حداقل ۱۵ ابریشم برای فروش در حجره لازم است."
                    p
                }
            }
        }
    }

    companion object {
        fun createFreshEditorTemplate(): LevelBlueprint {
            return LevelBlueprint(
                id = 0L,
                levelNumber = 100,
                title = "گذرگاه کاروان من",
                originCity = "شیراز",
                destinationCity = "سمرقند",
                storyIntro = "کاروان در درهٔ دست‌ساز شما حرکت می‌کند. برج‌های قابل‌حمل را جابه‌جا کنید و راهزنان را شکست دهید!",
                merchantOfferText = "بازرگان شهر مقصد: «خوش آمدی ای معمار و سردار جادهٔ ابریشم!»",
                biome = BiomeType.GOLDEN_DESERT,
                isCustom = true,
                caravanPath = listOf(
                    GridPos(0, 4), GridPos(1, 4), GridPos(2, 3), GridPos(3, 3),
                    GridPos(4, 3), GridPos(5, 4), GridPos(6, 4), GridPos(7, 4),
                    GridPos(8, 3), GridPos(9, 3), GridPos(10, 4), GridPos(11, 4)
                ),
                towerSlots = listOf(
                    GridPos(2, 1), GridPos(3, 5), GridPos(5, 2),
                    GridPos(6, 6), GridPos(8, 1), GridPos(9, 5)
                ),
                enemySpawns = listOf(
                    GridPos(11, 1), GridPos(11, 6)
                ),
                decorations = mapOf(
                    GridPos(1, 1) to DecorationType.PALM_TREES,
                    GridPos(7, 1) to DecorationType.OASIS_POOL
                ),
                waves = listOf(
                    WaveSpec(
                        waveNumber = 1,
                        title = "موج ۱: پیش‌قراولان کویر",
                        entries = listOf(
                            WaveEntry(EnemyType.DESERT_BANDIT, count = 6, spawnIntervalSec = 1.4f, spawnIndex = 0),
                            WaveEntry(EnemyType.SAND_WOLF, count = 4, spawnIntervalSec = 1.2f, spawnIndex = 1)
                        ),
                        rewardGold = 60
                    ),
                    WaveSpec(
                        waveNumber = 2,
                        title = "موج ۲: یورش شترسواران",
                        entries = listOf(
                            WaveEntry(EnemyType.CAMEL_ARCHER, count = 5, spawnIntervalSec = 1.5f, spawnIndex = 0),
                            WaveEntry(EnemyType.SOGDIAN_ARMORED, count = 3, spawnIntervalSec = 1.8f, spawnIndex = 1)
                        ),
                        rewardGold = 85
                    )
                ),
                startingGold = 230,
                caravanMaxHp = 110,
                maxRelocateCharges = 4,
                rewardSilk = 25,
                rewardSaffron = 18
            )
        }
    }
}

enum class CaravanMetaUpgradeType(
    val titleFa: String,
    val subtitleFa: String,
    val effectPerLevelFa: String
) {
    WAGON_ARMOR(
        titleFa = "ارابه‌های زره‌پوش کاروان",
        subtitleFa = "چوب بلوط و صفحات مفرغی روی ارابه‌های ابریشم و زعفران",
        effectPerLevelFa = "+۲۰ جان کاروان و کاهش آسیب راهزنان"
    ),
    CAMEL_SPEED_RELOCATE(
        titleFa = "تجهیز شترها و چرخ‌های برج",
        subtitleFa = "مهارهای ابریشمی و قرقره‌های سریع برای برج‌های قابل‌حمل",
        effectPerLevelFa = "+۱ شارژ جابه‌جایی برج و ۲۰٪ بازیابی سریع‌تر جابه‌جایی"
    ),
    PERMANENT_GUARD(
        titleFa = "نگهبان دائمی کاروان (کماندار کاروان)",
        subtitleFa = "تیرانداز نخبه که از فراز شتر پیشرو به راهزنان نزدیک شلیک می‌کند",
        effectPerLevelFa = "+۱۵ قدرت تیراندازی خودکار خودِ کاروان"
    ),
    COMPANION_HERO(
        titleFa = "قهرمان همراه: سردار جادهٔ ابریشم",
        subtitleFa = "تقویت مهارت‌های فعال: مه کویر، طبل جنگ و عقاب دیده‌بان",
        effectPerLevelFa = "+۲۰٪ قدرت مهارت‌ها و کاهش زمان انتظار (Cooldown)"
    )
}

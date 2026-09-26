package com.example.data.local

import com.example.data.model.BiomeType
import com.example.data.model.DecorationType
import com.example.data.model.EnemyType
import com.example.data.model.GridPos
import com.example.data.model.LevelBlueprint
import com.example.data.model.WaveEntry
import com.example.data.model.WaveSpec
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.json.JSONObject

class CaravanRepository(private val dao: CaravanDao) {

    val customLevelsFlow: Flow<List<LevelBlueprint>> = dao.observeCustomLevels().map { list ->
        list.mapNotNull { entity ->
            LevelBlueprint.fromJsonString(entity.blueprintJson)?.copy(
                id = entity.id,
                isCustom = true
            )
        }
    }

    val playerProgressFlow: Flow<PlayerProgressEntity> = dao.observePlayerProgress().map {
        it ?: PlayerProgressEntity()
    }

    suspend fun ensureInitialized() {
        val current = dao.getPlayerProgressOnce()
        if (current == null) {
            dao.upsertPlayerProgress(PlayerProgressEntity())
        }
        if (dao.getCustomLevelCount() == 0) {
            val sampleBlueprint = LevelBlueprint(
                id = 1L,
                levelNumber = 101,
                title = "نمونهٔ کارگاه: کمینگاه واحهٔ طبس",
                originCity = "طبس",
                destinationCity = "بیرجند",
                storyIntro = "این مرحله با ویرایشگر بصری ساخته شده است! می‌توانید در کارگاه مرحله‌ساز مسیر، برج‌ها و موج‌های آن را تغییر دهید یا مراحل جدید بسازید.",
                merchantOfferText = "بازرگان طبس: «خرما و زعفران خراسان آمادهٔ بارگیری است!»",
                biome = BiomeType.MOONLIT_OASIS,
                isCustom = true,
                caravanPath = listOf(
                    GridPos(0, 3), GridPos(1, 3), GridPos(2, 4), GridPos(3, 5),
                    GridPos(4, 5), GridPos(5, 4), GridPos(6, 3), GridPos(7, 2),
                    GridPos(8, 2), GridPos(9, 3), GridPos(10, 4), GridPos(11, 4)
                ),
                towerSlots = listOf(
                    GridPos(1, 1), GridPos(2, 6), GridPos(4, 3), GridPos(5, 6),
                    GridPos(6, 1), GridPos(8, 4), GridPos(9, 1), GridPos(10, 6)
                ),
                enemySpawns = listOf(GridPos(11, 1), GridPos(11, 7)),
                decorations = mapOf(
                    GridPos(3, 1) to DecorationType.OASIS_POOL,
                    GridPos(4, 1) to DecorationType.PALM_TREES,
                    GridPos(7, 6) to DecorationType.CARAVANSERAI_RUIN
                ),
                waves = listOf(
                    WaveSpec(
                        waveNumber = 1,
                        title = "موج ۱: راهزنان واحه",
                        entries = listOf(
                            WaveEntry(EnemyType.DESERT_BANDIT, count = 6, spawnIntervalSec = 1.4f, spawnIndex = 0),
                            WaveEntry(EnemyType.SAND_WOLF, count = 5, spawnIntervalSec = 1.1f, spawnIndex = 1)
                        ),
                        rewardGold = 60
                    ),
                    WaveSpec(
                        waveNumber = 2,
                        title = "موج ۲: یورش شترسواران و نقب‌زن",
                        entries = listOf(
                            WaveEntry(EnemyType.CAMEL_ARCHER, count = 5, spawnIntervalSec = 1.5f, spawnIndex = 0),
                            WaveEntry(EnemyType.TUNNEL_SABOTEUR, count = 4, spawnIntervalSec = 1.3f, spawnIndex = 1)
                        ),
                        rewardGold = 85
                    )
                ),
                startingGold = 240,
                caravanMaxHp = 115,
                maxRelocateCharges = 4,
                rewardSilk = 25,
                rewardSaffron = 20
            )
            saveCustomLevel(sampleBlueprint.copy(id = 0L))
        }
    }

    suspend fun saveCustomLevel(blueprint: LevelBlueprint): Long {
        val cleanBlueprint = blueprint.copy(isCustom = true)
        val entity = CustomLevelEntity(
            id = if (cleanBlueprint.id > 0L) cleanBlueprint.id else 0L,
            title = cleanBlueprint.title,
            originCity = cleanBlueprint.originCity,
            destinationCity = cleanBlueprint.destinationCity,
            biomeName = cleanBlueprint.biome.name,
            wavesCount = cleanBlueprint.waves.size,
            blueprintJson = cleanBlueprint.toJsonString(),
            updatedAt = System.currentTimeMillis()
        )
        return dao.upsertCustomLevel(entity)
    }

    suspend fun deleteCustomLevel(id: Long) {
        dao.deleteCustomLevel(id)
    }

    suspend fun recordVictory(
        level: LevelBlueprint,
        stars: Int,
        bonusGold: Int,
        bonusSilk: Int,
        bonusSaffron: Int,
        wavesCleared: Int
    ) {
        val current = dao.getPlayerProgressOnce() ?: PlayerProgressEntity()
        val starsObj = runCatching { JSONObject(current.completedStarsJson) }.getOrDefault(JSONObject())
        val key = if (level.isCustom) "custom_${level.id}" else "camp_${level.levelNumber}"
        val prevStars = starsObj.optInt(key, 0)
        if (stars > prevStars) {
            starsObj.put(key, stars)
        }

        val nextUnlocked = if (!level.isCustom && level.levelNumber in 1..11) {
            maxOf(current.highestUnlockedLevel, level.levelNumber + 1)
        } else {
            current.highestUnlockedLevel
        }

        val newEndlessBest = if (level.levelNumber == 99) {
            maxOf(current.endlessBestWave, wavesCleared)
        } else current.endlessBestWave

        val newBossClears = if (level.levelNumber == 88) {
            current.bossRaidClears + 1
        } else current.bossRaidClears

        dao.upsertPlayerProgress(
            current.copy(
                goldCoins = current.goldCoins + bonusGold,
                silkCount = current.silkCount + level.rewardSilk + bonusSilk,
                saffronCount = current.saffronCount + level.rewardSaffron + bonusSaffron,
                highestUnlockedLevel = nextUnlocked,
                completedStarsJson = starsObj.toString(),
                endlessBestWave = newEndlessBest,
                bossRaidClears = newBossClears
            )
        )
    }

    suspend fun updateProgress(transform: (PlayerProgressEntity) -> PlayerProgressEntity) {
        val current = dao.getPlayerProgressOnce() ?: PlayerProgressEntity()
        dao.upsertPlayerProgress(transform(current))
    }
}

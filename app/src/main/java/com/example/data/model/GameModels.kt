package com.example.data.model

import androidx.compose.ui.graphics.Color
import org.json.JSONArray
import org.json.JSONObject

data class GridPos(
    val col: Int,
    val row: Int
) {
    fun distanceTo(other: GridPos): Float {
        val dx = (col - other.col).toFloat()
        val dy = (row - other.row).toFloat()
        return kotlin.math.sqrt(dx * dx + dy * dy)
    }
}

enum class BiomeType(
    val titleFa: String,
    val subtitleFa: String,
    val dayTilePrimary: Color,
    val dayTileSecondary: Color,
    val nightTilePrimary: Color,
    val nightTileSecondary: Color,
    val roadColor: Color
) {
    GOLDEN_DESERT(
        titleFa = "کویر زرین لوت",
        subtitleFa = "تپه‌های شنی گرم با بادهای موسمی",
        dayTilePrimary = Color(0xFFD6A354),
        dayTileSecondary = Color(0xFFC89446),
        nightTilePrimary = Color(0xFF231D2E),
        nightTileSecondary = Color(0xFF1C1726),
        roadColor = Color(0xFF8B5A2B)
    ),
    RED_CANYON(
        titleFa = "درهٔ سرخ هرات",
        subtitleFa = "صخره‌های اخرایی و گذرگاه‌های باریک",
        dayTilePrimary = Color(0xFFC46D4E),
        dayTileSecondary = Color(0xFFB55F40),
        nightTilePrimary = Color(0xFF2C191A),
        nightTileSecondary = Color(0xFF231314),
        roadColor = Color(0xFF6E3524)
    ),
    MOONLIT_OASIS(
        titleFa = "واحهٔ فیروزه‌ای سمرقند",
        subtitleFa = "نخلستان‌های خنک و قنات‌های باستانی",
        dayTilePrimary = Color(0xFF9AB87A),
        dayTileSecondary = Color(0xFF8AA86A),
        nightTilePrimary = Color(0xFF13292A),
        nightTileSecondary = Color(0xFF0E2021),
        roadColor = Color(0xFF7A6348)
    ),
    SANDSTORM_VALLEY(
        titleFa = "گردنهٔ طوفان تکله‌مکان",
        subtitleFa = "شن‌های روان و دید محدود در غبار",
        dayTilePrimary = Color(0xFFC29B61),
        dayTileSecondary = Color(0xFFB38B52),
        nightTilePrimary = Color(0xFF292017),
        nightTileSecondary = Color(0xFF201810),
        roadColor = Color(0xFF705335)
    ),
    PAMIR_PASS(
        titleFa = "گذرگاه برفی پامیر",
        subtitleFa = "بلندی‌های سردسیر در مرز کاشغر",
        dayTilePrimary = Color(0xFFB8C7D4),
        dayTileSecondary = Color(0xFFA7B8C6),
        nightTilePrimary = Color(0xFF182232),
        nightTileSecondary = Color(0xFF121A28),
        roadColor = Color(0xFF5E6973)
    )
}

enum class TowerEvolution(
    val titleFa: String,
    val descFa: String,
    val bonusDamageMultiplier: Float,
    val bonusSpeedMultiplier: Float,
    val bonusRange: Float,
    val specialEffectFa: String,
    val accentColor: Color
) {
    NONE("پایه", "نسخهٔ استاندارد", 1f, 1f, 0f, "بدون تکامل", Color(0xFFF4A261)),

    // Archer Evolutions
    FIRE_ARCHER(
        "تیرانداز نفت‌انداز",
        "تیرهای آغشته به نفت سفید که دشمن را به آتش می‌کشند",
        1.45f, 1.1f, 0.4f, "آتش‌سوزی مستمر", Color(0xFFFF5722)
    ),
    PARTHIAN_RAPID(
        "کماندار تندرو اشکانی",
        "شلیک رگباری دوگانه با سرعت بسیار بالا",
        1.15f, 1.75f, 0.6f, "رگبار اشکانی", Color(0xFF4FC3F7)
    ),

    // Catapult Evolutions
    TAR_MORTAR(
        "منجنیق دیگ قیر",
        "پرتاب بشکه‌های قیر داغ با انفجار وسیع و کندکنندگی",
        1.55f, 1.0f, 0.5f, "انفجار قیر سوزان", Color(0xFFFF7043)
    ),
    HEAVY_BALLISTA(
        "خرکمان ضدزره ساسانی",
        "نیزهٔ غول‌پیکر که زره دشمنان سنگین و ارابه‌ها را می‌شکافد",
        2.1f, 1.25f, 0.8f, "شکافتن زره", Color(0xFFFFD54F)
    ),

    // Wind Catcher Evolutions
    BLINDING_VORTEX(
        "گردباد شن کورکننده",
        "طوفان موضعی که راهزنان را تا ۶۰٪ کند و آسیب‌پذیر می‌کند",
        1.35f, 1.3f, 0.5f, "کندی شدید ۶۰٪", Color(0xFF80DEEA)
    ),
    MIRAGE_SHRINE(
        "منارهٔ سراب کویر",
        "ایجاد سراب که راهزنان را گیج و از کاروان دور می‌کند",
        1.5f, 1.4f, 0.6f, "گیج‌کنندهٔ سراب", Color(0xFFCE93D8)
    ),

    // Spear Guard Evolutions
    IMMORTAL_PHALANX(
        "گارد جاویدان پارسی",
        "نیزه‌داران نخبه با زوبین‌های سنگین و سپر نفوذناپذیر",
        1.7f, 1.3f, 0.3f, "ضربهٔ بحرانی دوبرابر", Color(0xFFFFB300)
    ),
    SCORPION_HUNTER(
        "شکارچی عقرب‌انداز",
        "پرتاب زوبین‌های زهرآگین که زره و جان دشمن را می‌فرساید",
        1.4f, 1.55f, 0.5f, "زهر عقرب سیاه", Color(0xFFAED581)
    ),

    // Alchemist Evolutions
    FIRE_TEMPLE(
        "آتشکدهٔ الهام‌بخش",
        "افزایش ۴۰٪ سرعت شلیک تمام برج‌های نزدیک و شلیک گوی آذرخش",
        1.5f, 1.4f, 0.7f, "باف سرعت برج‌ها", Color(0xFFFF8A65)
    ),
    SILK_MINT(
        "ضرابخانهٔ کاروانسرا",
        "تولید سکهٔ طلا در هر موج و پرتاب کیسهٔ کیمیا به دشمنان",
        1.25f, 1.35f, 0.4f, "تولید طلای خودکار", Color(0xFFFFEE58)
    )
}

enum class TowerType(
    val titleFa: String,
    val shortTitleFa: String,
    val roleFa: String,
    val baseCost: Int,
    val baseDamage: Float,
    val fireIntervalSec: Float,
    val rangeCells: Float,
    val splashRadiusCells: Float,
    val slowFactor: Float,
    val goldPerShot: Int,
    val primaryColor: Color,
    val secondaryColor: Color,
    val evolutions: List<TowerEvolution>
) {
    ARCHER(
        titleFa = "برج کماندار پارسی",
        shortTitleFa = "کماندار",
        roleFa = "تک‌هدف سریع · قابل‌حمل سبک",
        baseCost = 60,
        baseDamage = 24f,
        fireIntervalSec = 0.75f,
        rangeCells = 3.1f,
        splashRadiusCells = 0f,
        slowFactor = 0f,
        goldPerShot = 0,
        primaryColor = Color(0xFFE9C46A),
        secondaryColor = Color(0xFF8D5B34),
        evolutions = listOf(TowerEvolution.FIRE_ARCHER, TowerEvolution.PARTHIAN_RAPID)
    ),
    CATAPULT(
        titleFa = "منجنیق بیابانی",
        shortTitleFa = "منجنیق",
        roleFa = "آسیب گروهی (AoE) · ضد لشکر",
        baseCost = 95,
        baseDamage = 58f,
        fireIntervalSec = 1.85f,
        rangeCells = 3.6f,
        splashRadiusCells = 1.25f,
        slowFactor = 0.1f,
        goldPerShot = 0,
        primaryColor = Color(0xFFE76F51),
        secondaryColor = Color(0xFF5D2A1A),
        evolutions = listOf(TowerEvolution.TAR_MORTAR, TowerEvolution.HEAVY_BALLISTA)
    ),
    WIND_CATCHER(
        titleFa = "بادگیر طوفان‌ساز",
        shortTitleFa = "بادگیر",
        roleFa = "کندکنندهٔ موج شن + آشکارساز نقب‌زن",
        baseCost = 75,
        baseDamage = 16f,
        fireIntervalSec = 1.0f,
        rangeCells = 2.7f,
        splashRadiusCells = 1.4f,
        slowFactor = 0.42f,
        goldPerShot = 0,
        primaryColor = Color(0xFF2A9D8F),
        secondaryColor = Color(0xFF144E47),
        evolutions = listOf(TowerEvolution.BLINDING_VORTEX, TowerEvolution.MIRAGE_SHRINE)
    ),
    SPEAR_GUARD(
        titleFa = "نگهبانی زوبین‌انداز",
        shortTitleFa = "زوبین‌دار",
        roleFa = "ضدزره سنگین · برد متوسط",
        baseCost = 80,
        baseDamage = 42f,
        fireIntervalSec = 1.05f,
        rangeCells = 2.5f,
        splashRadiusCells = 0f,
        slowFactor = 0.15f,
        goldPerShot = 0,
        primaryColor = Color(0xFFF4A261),
        secondaryColor = Color(0xFF6B3E26),
        evolutions = listOf(TowerEvolution.IMMORTAL_PHALANX, TowerEvolution.SCORPION_HUNTER)
    ),
    ALCHEMIST(
        titleFa = "کیمیاگر کاروانسرا",
        shortTitleFa = "کیمیاگر",
        roleFa = "پشتیبانی + تولید سکه + تقویت برج‌ها",
        baseCost = 85,
        baseDamage = 20f,
        fireIntervalSec = 1.15f,
        rangeCells = 2.8f,
        splashRadiusCells = 0.8f,
        slowFactor = 0f,
        goldPerShot = 3,
        primaryColor = Color(0xFFAB47BC),
        secondaryColor = Color(0xFF4A148C),
        evolutions = listOf(TowerEvolution.FIRE_TEMPLE, TowerEvolution.SILK_MINT)
    )
}

enum class EnemyType(
    val titleFa: String,
    val shortNameFa: String,
    val descFa: String,
    val baseHp: Float,
    val speedCellsPerSec: Float,
    val armor: Float,
    val caravanDamage: Int,
    val goldReward: Int,
    val isBoss: Boolean = false,
    val isStealthUnderSand: Boolean = false,
    val isSpeedBuffer: Boolean = false,
    val badgeColor: Color
) {
    DESERT_BANDIT(
        titleFa = "راهزن پیادهٔ کویر",
        shortNameFa = "راهزن کویر",
        descFa = "غارتگران چابک با شمشیرهای خمیده که به کاروان یورش می‌برند",
        baseHp = 75f,
        speedCellsPerSec = 1.25f,
        armor = 0f,
        caravanDamage = 8,
        goldReward = 14,
        badgeColor = Color(0xFFE76F51)
    ),
    CAMEL_ARCHER(
        titleFa = "کماندار شترسوار",
        shortNameFa = "شترسوار",
        descFa = "سوارکاران بیابانی با جان بالا و حرکت پیوسته",
        baseHp = 145f,
        speedCellsPerSec = 1.05f,
        armor = 0.15f,
        caravanDamage = 14,
        goldReward = 22,
        badgeColor = Color(0xFFF4A261)
    ),
    SOGDIAN_ARMORED(
        titleFa = "غارتگر زره‌پوش سغدی",
        shortNameFa = "زره‌پوش",
        descFa = "مبارزان سنگین‌اسلحه که در برابر تیرهای معمولی مقاوم‌اند",
        baseHp = 260f,
        speedCellsPerSec = 0.72f,
        armor = 0.40f,
        caravanDamage = 20,
        goldReward = 30,
        badgeColor = Color(0xFF90A4AE)
    ),
    SAND_WOLF(
        titleFa = "گرگ‌های شنی بیابان",
        shortNameFa = "گرگ شنی",
        descFa = "بسیار سریع و خطرناک؛ شترهای باربر را هدف می‌گیرند",
        baseHp = 55f,
        speedCellsPerSec = 1.85f,
        armor = 0f,
        caravanDamage = 7,
        goldReward = 12,
        badgeColor = Color(0xFFFFB74D)
    ),
    TUNNEL_SABOTEUR(
        titleFa = "نقب‌زن تونل‌رو",
        shortNameFa = "نقب‌زن",
        descFa = "زیر شن حرکت می‌کند؛ فقط نزدیک کاروان یا با بادگیر/عقاب دیده می‌شود",
        baseHp = 110f,
        speedCellsPerSec = 1.15f,
        armor = 0.1f,
        caravanDamage = 18,
        goldReward = 25,
        isStealthUnderSand = true,
        badgeColor = Color(0xFFBA68C8)
    ),
    WAR_DRUMMER(
        titleFa = "طبل‌زن قبیلهٔ راهزنان",
        shortNameFa = "طبل‌زن",
        descFa = "با کوبیدن طبل، سرعت راهزنان اطراف خود را ۳۵٪ افزایش می‌دهد",
        baseHp = 190f,
        speedCellsPerSec = 0.90f,
        armor = 0.2f,
        caravanDamage = 15,
        goldReward = 28,
        isSpeedBuffer = true,
        badgeColor = Color(0xFF4DD0E1)
    ),
    STEPPE_CHARIOT(
        titleFa = "ارابهٔ جنگی دشت",
        shortNameFa = "ارابه جنگی",
        descFa = "ماشین جنگی سنگین با چرخ‌های داس‌دار؛ آسیب شدید به کاروان",
        baseHp = 420f,
        speedCellsPerSec = 0.82f,
        armor = 0.35f,
        caravanDamage = 35,
        goldReward = 48,
        badgeColor = Color(0xFFFF8A65)
    ),
    BOSS_RED_STORM_KHAN(
        titleFa = "باس: خانِ طوفانِ سرخ",
        shortNameFa = "خان طوفان",
        descFa = "فرماندهٔ افسانه‌ای راهزنان جادهٔ ابریشم با زره پولادین و طوفان شن",
        baseHp = 1350f,
        speedCellsPerSec = 0.62f,
        armor = 0.35f,
        caravanDamage = 75,
        goldReward = 150,
        isBoss = true,
        badgeColor = Color(0xFFD62828)
    )
}

enum class DecorationType(val titleFa: String, val color: Color) {
    OASIS_POOL("برکهٔ واحه", Color(0xFF1D84B5)),
    PALM_TREES("نخلستان", Color(0xFF2E7D32)),
    CANYON_ROCK("صخرهٔ کویری", Color(0xFF6D4C41)),
    CARAVANSERAI_RUIN("کاروانسرا", Color(0xFFD4A359)),
    ANCIENT_STATUE("تندیس باستانی", Color(0xFF80CBC4))
}

data class WaveEntry(
    val enemyType: EnemyType,
    val count: Int,
    val spawnIntervalSec: Float = 1.4f,
    val spawnIndex: Int = 0,
    val hpMultiplier: Float = 1.0f
)

data class WaveSpec(
    val waveNumber: Int,
    val title: String,
    val entries: List<WaveEntry>,
    val rewardGold: Int = 45
)

data class LevelBlueprint(
    val id: Long = 0L,
    val levelNumber: Int = 1,
    val title: String,
    val originCity: String,
    val destinationCity: String,
    val storyIntro: String,
    val merchantOfferText: String,
    val biome: BiomeType = BiomeType.GOLDEN_DESERT,
    val isCustom: Boolean = false,
    val gridCols: Int = 12,
    val gridRows: Int = 8,
    val caravanPath: List<GridPos>,
    val towerSlots: List<GridPos>,
    val enemySpawns: List<GridPos>,
    val decorations: Map<GridPos, DecorationType> = emptyMap(),
    val waves: List<WaveSpec>,
    val startingGold: Int = 200,
    val caravanMaxHp: Int = 100,
    val maxRelocateCharges: Int = 3,
    val rewardSilk: Int = 15,
    val rewardSaffron: Int = 10
) {
    fun toJsonString(): String {
        val root = JSONObject()
        root.put("id", id)
        root.put("levelNumber", levelNumber)
        root.put("title", title)
        root.put("originCity", originCity)
        root.put("destinationCity", destinationCity)
        root.put("storyIntro", storyIntro)
        root.put("merchantOfferText", merchantOfferText)
        root.put("biome", biome.name)
        root.put("isCustom", isCustom)
        root.put("gridCols", gridCols)
        root.put("gridRows", gridRows)
        root.put("startingGold", startingGold)
        root.put("caravanMaxHp", caravanMaxHp)
        root.put("maxRelocateCharges", maxRelocateCharges)
        root.put("rewardSilk", rewardSilk)
        root.put("rewardSaffron", rewardSaffron)

        val pathArr = JSONArray()
        caravanPath.forEach { pos ->
            pathArr.put(JSONObject().put("c", pos.col).put("r", pos.row))
        }
        root.put("caravanPath", pathArr)

        val slotsArr = JSONArray()
        towerSlots.forEach { pos ->
            slotsArr.put(JSONObject().put("c", pos.col).put("r", pos.row))
        }
        root.put("towerSlots", slotsArr)

        val spawnsArr = JSONArray()
        enemySpawns.forEach { pos ->
            spawnsArr.put(JSONObject().put("c", pos.col).put("r", pos.row))
        }
        root.put("enemySpawns", spawnsArr)

        val decArr = JSONArray()
        decorations.forEach { (pos, dec) ->
            decArr.put(
                JSONObject()
                    .put("c", pos.col)
                    .put("r", pos.row)
                    .put("type", dec.name)
            )
        }
        root.put("decorations", decArr)

        val wavesArr = JSONArray()
        waves.forEach { wave ->
            val wObj = JSONObject()
            wObj.put("waveNumber", wave.waveNumber)
            wObj.put("title", wave.title)
            wObj.put("rewardGold", wave.rewardGold)
            val entriesArr = JSONArray()
            wave.entries.forEach { entry ->
                entriesArr.put(
                    JSONObject()
                        .put("enemyType", entry.enemyType.name)
                        .put("count", entry.count)
                        .put("spawnIntervalSec", entry.spawnIntervalSec.toDouble())
                        .put("spawnIndex", entry.spawnIndex)
                        .put("hpMultiplier", entry.hpMultiplier.toDouble())
                )
            }
            wObj.put("entries", entriesArr)
            wavesArr.put(wObj)
        }
        root.put("waves", wavesArr)
        return root.toString()
    }

    companion object {
        fun fromJsonString(json: String): LevelBlueprint? {
            return try {
                val root = JSONObject(json)
                val pathArr = root.getJSONArray("caravanPath")
                val path = mutableListOf<GridPos>()
                for (i in 0 until pathArr.length()) {
                    val o = pathArr.getJSONObject(i)
                    path.add(GridPos(o.getInt("c"), o.getInt("r")))
                }

                val slotsArr = root.getJSONArray("towerSlots")
                val slots = mutableListOf<GridPos>()
                for (i in 0 until slotsArr.length()) {
                    val o = slotsArr.getJSONObject(i)
                    slots.add(GridPos(o.getInt("c"), o.getInt("r")))
                }

                val spawnsArr = root.getJSONArray("enemySpawns")
                val spawns = mutableListOf<GridPos>()
                for (i in 0 until spawnsArr.length()) {
                    val o = spawnsArr.getJSONObject(i)
                    spawns.add(GridPos(o.getInt("c"), o.getInt("r")))
                }

                val decorations = mutableMapOf<GridPos, DecorationType>()
                val decArr = root.optJSONArray("decorations")
                if (decArr != null) {
                    for (i in 0 until decArr.length()) {
                        val o = decArr.getJSONObject(i)
                        val type = runCatching {
                            DecorationType.valueOf(o.getString("type"))
                        }.getOrDefault(DecorationType.PALM_TREES)
                        decorations[GridPos(o.getInt("c"), o.getInt("r"))] = type
                    }
                }

                val wavesArr = root.getJSONArray("waves")
                val waves = mutableListOf<WaveSpec>()
                for (i in 0 until wavesArr.length()) {
                    val wObj = wavesArr.getJSONObject(i)
                    val entriesArr = wObj.getJSONArray("entries")
                    val entries = mutableListOf<WaveEntry>()
                    for (j in 0 until entriesArr.length()) {
                        val eObj = entriesArr.getJSONObject(j)
                        val enemyType = runCatching {
                            EnemyType.valueOf(eObj.getString("enemyType"))
                        }.getOrDefault(EnemyType.DESERT_BANDIT)
                        entries.add(
                            WaveEntry(
                                enemyType = enemyType,
                                count = eObj.getInt("count"),
                                spawnIntervalSec = eObj.optDouble("spawnIntervalSec", 1.4).toFloat(),
                                spawnIndex = eObj.optInt("spawnIndex", 0),
                                hpMultiplier = eObj.optDouble("hpMultiplier", 1.0).toFloat()
                            )
                        )
                    }
                    waves.add(
                        WaveSpec(
                            waveNumber = wObj.optInt("waveNumber", i + 1),
                            title = wObj.optString("title", "موج ${i + 1}"),
                            entries = entries,
                            rewardGold = wObj.optInt("rewardGold", 45)
                        )
                    )
                }

                val biome = runCatching {
                    BiomeType.valueOf(root.optString("biome", BiomeType.GOLDEN_DESERT.name))
                }.getOrDefault(BiomeType.GOLDEN_DESERT)

                LevelBlueprint(
                    id = root.optLong("id", 0L),
                    levelNumber = root.optInt("levelNumber", 1),
                    title = root.optString("title", "مرحلهٔ دست‌ساز"),
                    originCity = root.optString("originCity", "پرسپولیس"),
                    destinationCity = root.optString("destinationCity", "سمرقند"),
                    storyIntro = root.optString("storyIntro", "کاروان آمادهٔ حرکت در کویر است."),
                    merchantOfferText = root.optString("merchantOfferText", "بازرگان کاروانسرا پیشنهاد معاملهٔ ابریشم دارد."),
                    biome = biome,
                    isCustom = root.optBoolean("isCustom", true),
                    gridCols = root.optInt("gridCols", 12),
                    gridRows = root.optInt("gridRows", 8),
                    caravanPath = path,
                    towerSlots = slots,
                    enemySpawns = spawns,
                    decorations = decorations,
                    waves = waves,
                    startingGold = root.optInt("startingGold", 200),
                    caravanMaxHp = root.optInt("caravanMaxHp", 100),
                    maxRelocateCharges = root.optInt("maxRelocateCharges", 3),
                    rewardSilk = root.optInt("rewardSilk", 15),
                    rewardSaffron = root.optInt("rewardSaffron", 10)
                )
            } catch (e: Exception) {
                null
            }
        }
    }
}

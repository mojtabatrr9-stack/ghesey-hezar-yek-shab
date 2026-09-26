package com.example.data.campaign

import com.example.data.model.BiomeType
import com.example.data.model.DecorationType
import com.example.data.model.EnemyType
import com.example.data.model.GridPos
import com.example.data.model.LevelBlueprint
import com.example.data.model.WaveEntry
import com.example.data.model.WaveSpec
import kotlin.random.Random

object CampaignLevels {

    val allLevels: List<LevelBlueprint> by lazy {
        listOf(
            level1PersepolisToYazd(),
            level2YazdToRayy(),
            level3RayyToNishapur(),
            level4NishapurToHerat(),
            level5HeratToMerv(),
            level6MervToBukhara(),
            level7BukharaToSamarkand(),
            level8SamarkandToFergana(),
            level9FerganaToKashgar(),
            level10KashgarToTurfan(),
            level11TurfanToDunhuang(),
            level12DunhuangToChangan()
        )
    }

    private fun level1PersepolisToYazd() = LevelBlueprint(
        id = -1L,
        levelNumber = 1,
        title = "مرحله ۱: سپیده‌دم از پرسپولیس",
        originCity = "پرسپولیس",
        destinationCity = "یزد",
        storyIntro = "کاروان جوان تو با بار ابریشم و زعفران از پله‌های سنگی پارسه حرکت می‌کند. جاسوسان خبر داده‌اند که راهزنان کویر در تنگهٔ مهریز کمین کرده‌اند. برج‌های کماندار قابل‌حمل خود را آماده کن!",
        merchantOfferText = "بازرگان زرتشتی در دروازهٔ یزد: «زعفران ناب آورده‌ای ای سردار! آیا حاضری بخشی از غنیمت را با شمش‌های طلا و تقویت چرخ‌های ارابه معاوضه کنی؟»",
        biome = BiomeType.GOLDEN_DESERT,
        caravanPath = listOf(
            GridPos(0, 4), GridPos(1, 4), GridPos(2, 4), GridPos(3, 3),
            GridPos(4, 3), GridPos(5, 3), GridPos(6, 4), GridPos(7, 4),
            GridPos(8, 4), GridPos(9, 3), GridPos(10, 3), GridPos(11, 3)
        ),
        towerSlots = listOf(
            GridPos(2, 2), GridPos(3, 5), GridPos(5, 1), GridPos(6, 6),
            GridPos(7, 2), GridPos(9, 5), GridPos(10, 1)
        ),
        enemySpawns = listOf(GridPos(11, 1), GridPos(11, 6)),
        decorations = mapOf(
            GridPos(1, 1) to DecorationType.ANCIENT_STATUE,
            GridPos(4, 6) to DecorationType.PALM_TREES,
            GridPos(8, 1) to DecorationType.CANYON_ROCK
        ),
        waves = listOf(
            WaveSpec(1, "پیش‌قراولان کویر", listOf(
                WaveEntry(EnemyType.DESERT_BANDIT, count = 5, spawnIntervalSec = 1.6f, spawnIndex = 0)
            ), rewardGold = 50),
            WaveSpec(2, "یورش از دو جناح", listOf(
                WaveEntry(EnemyType.DESERT_BANDIT, count = 6, spawnIntervalSec = 1.4f, spawnIndex = 0),
                WaveEntry(EnemyType.SAND_WOLF, count = 4, spawnIntervalSec = 1.2f, spawnIndex = 1)
            ), rewardGold = 65),
            WaveSpec(3, "سواران شترسوار", listOf(
                WaveEntry(EnemyType.CAMEL_ARCHER, count = 4, spawnIntervalSec = 1.8f, spawnIndex = 0),
                WaveEntry(EnemyType.DESERT_BANDIT, count = 6, spawnIntervalSec = 1.3f, spawnIndex = 1)
            ), rewardGold = 80)
        ),
        startingGold = 210,
        caravanMaxHp = 100,
        maxRelocateCharges = 3,
        rewardSilk = 18,
        rewardSaffron = 12
    )

    private fun level2YazdToRayy() = LevelBlueprint(
        id = -2L,
        levelNumber = 2,
        title = "مرحله ۲: بادگیرهای یزد تا ری",
        originCity = "یزد",
        destinationCity = "ری",
        storyIntro = "در حاشیهٔ دشت کویر، خبرچین کاروانسرا به راهزنان علامت داده است. گرگ‌های شنی و شترسواران از تپه‌های شمالی سرازیر می‌شوند. در میانهٔ حرکت کاروان، برج‌های خود را به جلو جابه‌جا کن!",
        merchantOfferText = "کاروان‌سالار ری: «پارچه‌های ابریشمی تو در بازار ری خریدار فراوان دارد. می‌توانیم کمانداران اشکانی را برای ادامهٔ راه تجهیز کنیم.»",
        biome = BiomeType.GOLDEN_DESERT,
        caravanPath = listOf(
            GridPos(0, 2), GridPos(1, 2), GridPos(2, 3), GridPos(3, 4),
            GridPos(4, 5), GridPos(5, 5), GridPos(6, 4), GridPos(7, 3),
            GridPos(8, 2), GridPos(9, 2), GridPos(10, 3), GridPos(11, 4)
        ),
        towerSlots = listOf(
            GridPos(2, 1), GridPos(2, 5), GridPos(4, 3), GridPos(5, 6),
            GridPos(7, 5), GridPos(8, 4), GridPos(10, 1), GridPos(10, 5)
        ),
        enemySpawns = listOf(GridPos(6, 0), GridPos(11, 7)),
        decorations = mapOf(
            GridPos(0, 6) to DecorationType.CARAVANSERAI_RUIN,
            GridPos(5, 1) to DecorationType.OASIS_POOL,
            GridPos(9, 6) to DecorationType.PALM_TREES
        ),
        waves = listOf(
            WaveSpec(1, "گلهٔ گرگ‌های شنی", listOf(
                WaveEntry(EnemyType.SAND_WOLF, count = 7, spawnIntervalSec = 1.1f, spawnIndex = 0),
                WaveEntry(EnemyType.DESERT_BANDIT, count = 4, spawnIntervalSec = 1.5f, spawnIndex = 1)
            ), rewardGold = 55),
            WaveSpec(2, "کمین شترسواران", listOf(
                WaveEntry(EnemyType.CAMEL_ARCHER, count = 5, spawnIntervalSec = 1.6f, spawnIndex = 1),
                WaveEntry(EnemyType.DESERT_BANDIT, count = 6, spawnIntervalSec = 1.2f, spawnIndex = 0)
            ), rewardGold = 70),
            WaveSpec(3, "محاصرهٔ کاروانسرا", listOf(
                WaveEntry(EnemyType.SOGDIAN_ARMORED, count = 3, spawnIntervalSec = 2.2f, spawnIndex = 0),
                WaveEntry(EnemyType.SAND_WOLF, count = 8, spawnIntervalSec = 1.0f, spawnIndex = 1)
            ), rewardGold = 90)
        ),
        startingGold = 220,
        caravanMaxHp = 110,
        maxRelocateCharges = 3,
        rewardSilk = 22,
        rewardSaffron = 15
    )

    private fun level3RayyToNishapur() = LevelBlueprint(
        id = -3L,
        levelNumber = 3,
        title = "مرحله ۳: معادن فیروزهٔ نیشابور",
        originCity = "ری",
        destinationCity = "نیشابور",
        storyIntro = "راه نیشابور از کنار واحه‌های قدیمی می‌گذرد. این بار غارتگران زره‌پوش به طمع فیروزه و زعفران آمده‌اند. از منجنیق و نگهبان زوبین‌انداز برای شکستن زره آن‌ها بهره بگیر.",
        merchantOfferText = "استادکار فیروزه‌تراش نیشابور: «در ازای یک صندوق ابریشم، نگین‌های فیروزه و نقشهٔ کمینگاه‌های هرات را به تو می‌سپارم.»",
        biome = BiomeType.MOONLIT_OASIS,
        caravanPath = listOf(
            GridPos(0, 5), GridPos(1, 5), GridPos(2, 4), GridPos(3, 3),
            GridPos(4, 2), GridPos(5, 2), GridPos(6, 3), GridPos(7, 4),
            GridPos(8, 5), GridPos(9, 5), GridPos(10, 4), GridPos(11, 4)
        ),
        towerSlots = listOf(
            GridPos(1, 3), GridPos(3, 5), GridPos(4, 4), GridPos(5, 0),
            GridPos(7, 2), GridPos(8, 3), GridPos(9, 6), GridPos(10, 2)
        ),
        enemySpawns = listOf(GridPos(11, 1), GridPos(11, 6), GridPos(5, 7)),
        decorations = mapOf(
            GridPos(2, 1) to DecorationType.OASIS_POOL,
            GridPos(3, 1) to DecorationType.PALM_TREES,
            GridPos(8, 1) to DecorationType.CARAVANSERAI_RUIN
        ),
        waves = listOf(
            WaveSpec(1, "دیده‌بانان واحه", listOf(
                WaveEntry(EnemyType.DESERT_BANDIT, count = 7, spawnIntervalSec = 1.2f, spawnIndex = 0),
                WaveEntry(EnemyType.CAMEL_ARCHER, count = 4, spawnIntervalSec = 1.6f, spawnIndex = 1)
            ), rewardGold = 60),
            WaveSpec(2, "سپرهای آهنین", listOf(
                WaveEntry(EnemyType.SOGDIAN_ARMORED, count = 4, spawnIntervalSec = 2.0f, spawnIndex = 0),
                WaveEntry(EnemyType.SAND_WOLF, count = 7, spawnIntervalSec = 1.0f, spawnIndex = 2)
            ), rewardGold = 75),
            WaveSpec(3, "یورش سه دروازه", listOf(
                WaveEntry(EnemyType.SOGDIAN_ARMORED, count = 4, spawnIntervalSec = 1.8f, spawnIndex = 1),
                WaveEntry(EnemyType.CAMEL_ARCHER, count = 6, spawnIntervalSec = 1.4f, spawnIndex = 0),
                WaveEntry(EnemyType.DESERT_BANDIT, count = 8, spawnIntervalSec = 1.1f, spawnIndex = 2)
            ), rewardGold = 95)
        ),
        startingGold = 240,
        caravanMaxHp = 115,
        maxRelocateCharges = 3,
        rewardSilk = 26,
        rewardSaffron = 18
    )

    private fun level4NishapurToHerat() = LevelBlueprint(
        id = -4L,
        levelNumber = 4,
        title = "مرحله ۴: درهٔ سرخ هرات و نقب‌زن‌ها",
        originCity = "نیشابور",
        destinationCity = "هرات",
        storyIntro = "صخره‌های سرخ هرات پر از تونل‌های زیرزمینی است! نقب‌زن‌های راهزن زیر شن مخفی می‌شوند تا ناگهان کنار شترها سر برآورند. از برج بادگیر یا مهارت «عقاب دیده‌بان» برای آشکارسازی آن‌ها استفاده کن!",
        merchantOfferText = "حکیم کاروانسرای هرات: «نفت سفید و قیر داغ برای منجنیق‌هایت فراهم کرده‌ام. با کدام کاروان معامله می‌کنی؟»",
        biome = BiomeType.RED_CANYON,
        caravanPath = listOf(
            GridPos(0, 3), GridPos(1, 3), GridPos(2, 3), GridPos(3, 4),
            GridPos(4, 5), GridPos(5, 5), GridPos(6, 5), GridPos(7, 4),
            GridPos(8, 3), GridPos(9, 2), GridPos(10, 2), GridPos(11, 2)
        ),
        towerSlots = listOf(
            GridPos(1, 1), GridPos(2, 5), GridPos(4, 3), GridPos(5, 6),
            GridPos(6, 3), GridPos(8, 5), GridPos(9, 4), GridPos(10, 0)
        ),
        enemySpawns = listOf(GridPos(11, 5), GridPos(7, 0), GridPos(3, 7)),
        decorations = mapOf(
            GridPos(0, 6) to DecorationType.CANYON_ROCK,
            GridPos(5, 1) to DecorationType.CANYON_ROCK,
            GridPos(10, 6) to DecorationType.CARAVANSERAI_RUIN
        ),
        waves = listOf(
            WaveSpec(1, "اشباح زیر شن", listOf(
                WaveEntry(EnemyType.TUNNEL_SABOTEUR, count = 5, spawnIntervalSec = 1.5f, spawnIndex = 0),
                WaveEntry(EnemyType.DESERT_BANDIT, count = 6, spawnIntervalSec = 1.2f, spawnIndex = 1)
            ), rewardGold = 65),
            WaveSpec(2, "تلهٔ درهٔ سرخ", listOf(
                WaveEntry(EnemyType.TUNNEL_SABOTEUR, count = 6, spawnIntervalSec = 1.4f, spawnIndex = 2),
                WaveEntry(EnemyType.SOGDIAN_ARMORED, count = 4, spawnIntervalSec = 1.9f, spawnIndex = 0)
            ), rewardGold = 80),
            WaveSpec(3, "طوفان کمینگاه", listOf(
                WaveEntry(EnemyType.WAR_DRUMMER, count = 2, spawnIntervalSec = 2.5f, spawnIndex = 1),
                WaveEntry(EnemyType.TUNNEL_SABOTEUR, count = 7, spawnIntervalSec = 1.2f, spawnIndex = 0),
                WaveEntry(EnemyType.CAMEL_ARCHER, count = 6, spawnIntervalSec = 1.3f, spawnIndex = 2)
            ), rewardGold = 105)
        ),
        startingGold = 255,
        caravanMaxHp = 120,
        maxRelocateCharges = 4,
        rewardSilk = 30,
        rewardSaffron = 22
    )

    private fun level5HeratToMerv() = LevelBlueprint(
        id = -5L,
        levelNumber = 5,
        title = "مرحله ۵: طبل‌های جنگ در دروازهٔ مرو",
        originCity = "هرات",
        destinationCity = "مرو",
        storyIntro = "در بیابان قره‌قوم پیش از مرو، قبایل راهزن با طبل‌زن‌های جنگی هم‌پیمان شده‌اند. صدای طبل آن‌ها سرعت یورش دشمنان را دوچندان می‌کند. طبل‌زن‌ها را پیش از رسیدن به کاروان هدف بگیر!",
        merchantOfferText = "بازرگان ابریشم مرو: «در کتابخانه‌های مرو نقشه‌های کهن دفاعی یافته‌ایم که ظرفیت ارابه‌های کاروان را ارتقا می‌دهد.»",
        biome = BiomeType.GOLDEN_DESERT,
        caravanPath = listOf(
            GridPos(0, 6), GridPos(1, 6), GridPos(2, 5), GridPos(3, 4),
            GridPos(4, 3), GridPos(5, 2), GridPos(6, 2), GridPos(7, 3),
            GridPos(8, 4), GridPos(9, 5), GridPos(10, 5), GridPos(11, 5)
        ),
        towerSlots = listOf(
            GridPos(1, 4), GridPos(3, 6), GridPos(4, 1), GridPos(5, 4),
            GridPos(6, 0), GridPos(7, 5), GridPos(9, 3), GridPos(10, 6)
        ),
        enemySpawns = listOf(GridPos(11, 1), GridPos(11, 7), GridPos(0, 1)),
        decorations = mapOf(
            GridPos(2, 1) to DecorationType.ANCIENT_STATUE,
            GridPos(8, 1) to DecorationType.OASIS_POOL,
            GridPos(6, 6) to DecorationType.PALM_TREES
        ),
        waves = listOf(
            WaveSpec(1, "طبل‌زنان قره‌قوم", listOf(
                WaveEntry(EnemyType.WAR_DRUMMER, count = 3, spawnIntervalSec = 2.0f, spawnIndex = 0),
                WaveEntry(EnemyType.DESERT_BANDIT, count = 8, spawnIntervalSec = 1.0f, spawnIndex = 0)
            ), rewardGold = 70),
            WaveSpec(2, "حملهٔ گازانبری", listOf(
                WaveEntry(EnemyType.WAR_DRUMMER, count = 3, spawnIntervalSec = 2.2f, spawnIndex = 1),
                WaveEntry(EnemyType.SAND_WOLF, count = 9, spawnIntervalSec = 0.9f, spawnIndex = 2),
                WaveEntry(EnemyType.CAMEL_ARCHER, count = 5, spawnIntervalSec = 1.4f, spawnIndex = 1)
            ), rewardGold = 85),
            WaveSpec(3, "سواران زره‌پوش مرو", listOf(
                WaveEntry(EnemyType.STEPPE_CHARIOT, count = 2, spawnIntervalSec = 3.0f, spawnIndex = 0),
                WaveEntry(EnemyType.SOGDIAN_ARMORED, count = 5, spawnIntervalSec = 1.7f, spawnIndex = 1),
                WaveEntry(EnemyType.WAR_DRUMMER, count = 3, spawnIntervalSec = 2.0f, spawnIndex = 2)
            ), rewardGold = 110)
        ),
        startingGold = 270,
        caravanMaxHp = 120,
        maxRelocateCharges = 4,
        rewardSilk = 34,
        rewardSaffron = 25
    )

    private fun level6MervToBukhara() = LevelBlueprint(
        id = -6L,
        levelNumber = 6,
        title = "مرحله ۶: گذر از جیحون به بخارا",
        originCity = "مرو",
        destinationCity = "بخارا",
        storyIntro = "کاروان از رود جیحون گذشته و به واحه‌های بخارا نزدیک می‌شود. ارابه‌های جنگی دشت با چرخ‌های داس‌دار در کمین‌اند. برج‌های خود را به تکامل سطح ۳ برسان تا ارابه‌ها را متوقف کنی!",
        merchantOfferText = "صراف بزرگ بخارا: «سکه‌های نقرهٔ بخارا و ادویهٔ هند در اختیار توست، سردار!»",
        biome = BiomeType.MOONLIT_OASIS,
        caravanPath = listOf(
            GridPos(0, 1), GridPos(1, 2), GridPos(2, 3), GridPos(3, 3),
            GridPos(4, 3), GridPos(5, 4), GridPos(6, 5), GridPos(7, 5),
            GridPos(8, 5), GridPos(9, 4), GridPos(10, 3), GridPos(11, 3)
        ),
        towerSlots = listOf(
            GridPos(1, 4), GridPos(3, 1), GridPos(4, 5), GridPos(5, 2),
            GridPos(6, 6), GridPos(7, 3), GridPos(9, 6), GridPos(10, 1)
        ),
        enemySpawns = listOf(GridPos(11, 0), GridPos(11, 6), GridPos(4, 7)),
        decorations = mapOf(
            GridPos(2, 6) to DecorationType.OASIS_POOL,
            GridPos(8, 1) to DecorationType.PALM_TREES,
            GridPos(0, 5) to DecorationType.CARAVANSERAI_RUIN
        ),
        waves = listOf(
            WaveSpec(1, "ارابه‌های دشت قبچاق", listOf(
                WaveEntry(EnemyType.STEPPE_CHARIOT, count = 2, spawnIntervalSec = 2.8f, spawnIndex = 0),
                WaveEntry(EnemyType.CAMEL_ARCHER, count = 6, spawnIntervalSec = 1.3f, spawnIndex = 1)
            ), rewardGold = 75),
            WaveSpec(2, "نقب‌زنان جیحون", listOf(
                WaveEntry(EnemyType.TUNNEL_SABOTEUR, count = 8, spawnIntervalSec = 1.1f, spawnIndex = 2),
                WaveEntry(EnemyType.SOGDIAN_ARMORED, count = 5, spawnIntervalSec = 1.6f, spawnIndex = 0)
            ), rewardGold = 90),
            WaveSpec(3, "محاصرهٔ ارگ بخارا", listOf(
                WaveEntry(EnemyType.STEPPE_CHARIOT, count = 4, spawnIntervalSec = 2.4f, spawnIndex = 1),
                WaveEntry(EnemyType.WAR_DRUMMER, count = 3, spawnIntervalSec = 1.8f, spawnIndex = 0),
                WaveEntry(EnemyType.SAND_WOLF, count = 10, spawnIntervalSec = 0.8f, spawnIndex = 2)
            ), rewardGold = 120)
        ),
        startingGold = 285,
        caravanMaxHp = 125,
        maxRelocateCharges = 4,
        rewardSilk = 38,
        rewardSaffron = 28
    )

    private fun level7BukharaToSamarkand() = LevelBlueprint(
        id = -7L,
        levelNumber = 7,
        title = "مرحله ۷: نگین جادهٔ ابریشم، سمرقند",
        originCity = "بخارا",
        destinationCity = "سمرقند",
        storyIntro = "در نیمهٔ راهِ هزار شهر به سمرقند رسیده‌ایم! سردار یاغیِ سغد با یک لشکر کامل و اولین فرماندهٔ طوفان سرخ راه را بسته است. جابه‌جایی به‌موقع برج‌ها کلید پیروزی در این محاصره است.",
        merchantOfferText = "امیر بازار ریگستان سمرقند: «کاغذ سمرقندی و ابریشم زربفت! نام کاروان تو در سراسر ماوراءالنهر پیچیده است.»",
        biome = BiomeType.MOONLIT_OASIS,
        caravanPath = listOf(
            GridPos(0, 4), GridPos(1, 4), GridPos(2, 2), GridPos(3, 2),
            GridPos(4, 4), GridPos(5, 6), GridPos(6, 6), GridPos(7, 4),
            GridPos(8, 2), GridPos(9, 2), GridPos(10, 4), GridPos(11, 4)
        ),
        towerSlots = listOf(
            GridPos(1, 2), GridPos(2, 4), GridPos(3, 0), GridPos(4, 6),
            GridPos(5, 4), GridPos(6, 4), GridPos(7, 6), GridPos(8, 4),
            GridPos(9, 0), GridPos(10, 6)
        ),
        enemySpawns = listOf(GridPos(11, 1), GridPos(11, 7), GridPos(5, 0)),
        decorations = mapOf(
            GridPos(0, 1) to DecorationType.ANCIENT_STATUE,
            GridPos(3, 6) to DecorationType.OASIS_POOL,
            GridPos(8, 6) to DecorationType.PALM_TREES
        ),
        waves = listOf(
            WaveSpec(1, "سپاه سغدی", listOf(
                WaveEntry(EnemyType.SOGDIAN_ARMORED, count = 6, spawnIntervalSec = 1.5f, spawnIndex = 0),
                WaveEntry(EnemyType.CAMEL_ARCHER, count = 6, spawnIntervalSec = 1.3f, spawnIndex = 1)
            ), rewardGold = 80),
            WaveSpec(2, "ارابه‌ها و نقب‌زن‌ها", listOf(
                WaveEntry(EnemyType.STEPPE_CHARIOT, count = 3, spawnIntervalSec = 2.2f, spawnIndex = 2),
                WaveEntry(EnemyType.TUNNEL_SABOTEUR, count = 8, spawnIntervalSec = 1.0f, spawnIndex = 0)
            ), rewardGold = 100),
            WaveSpec(3, "فرماندهٔ طوفان سمرقند (باس)", listOf(
                WaveEntry(EnemyType.BOSS_RED_STORM_KHAN, count = 1, spawnIntervalSec = 3.0f, spawnIndex = 0, hpMultiplier = 0.85f),
                WaveEntry(EnemyType.WAR_DRUMMER, count = 3, spawnIntervalSec = 1.8f, spawnIndex = 1),
                WaveEntry(EnemyType.DESERT_BANDIT, count = 10, spawnIntervalSec = 0.9f, spawnIndex = 2)
            ), rewardGold = 150)
        ),
        startingGold = 310,
        caravanMaxHp = 130,
        maxRelocateCharges = 4,
        rewardSilk = 45,
        rewardSaffron = 35
    )

    private fun level8SamarkandToFergana() = LevelBlueprint(
        id = -8L,
        levelNumber = 8,
        title = "مرحله ۸: تنگهٔ اسب‌های فرغانه",
        originCity = "سمرقند",
        destinationCity = "فرغانه",
        storyIntro = "درهٔ فرغانه به اسب‌های بادپای آسمانی شهرت دارد؛ اما ارابه‌رانان دشت نیز با سرعت سرسام‌آور از دو سوی دره به کاروان حمله می‌کنند!",
        merchantOfferText = "نگهبان اصطبل فرغانه: «شترها و ارابه‌هایت را با زین و برگ فرغانه تجهیز کن تا در گذرگاه پامیر از پا نیفتند.»",
        biome = BiomeType.RED_CANYON,
        caravanPath = listOf(
            GridPos(0, 3), GridPos(1, 3), GridPos(2, 4), GridPos(3, 5),
            GridPos(4, 4), GridPos(5, 3), GridPos(6, 2), GridPos(7, 3),
            GridPos(8, 4), GridPos(9, 5), GridPos(10, 4), GridPos(11, 3)
        ),
        towerSlots = listOf(
            GridPos(1, 5), GridPos(2, 2), GridPos(3, 3), GridPos(4, 6),
            GridPos(5, 1), GridPos(6, 4), GridPos(7, 1), GridPos(8, 6),
            GridPos(9, 3), GridPos(10, 2)
        ),
        enemySpawns = listOf(GridPos(11, 0), GridPos(11, 7), GridPos(0, 7)),
        decorations = mapOf(
            GridPos(1, 0) to DecorationType.CANYON_ROCK,
            GridPos(6, 6) to DecorationType.CARAVANSERAI_RUIN
        ),
        waves = listOf(
            WaveSpec(1, "تازندگان دره", listOf(
                WaveEntry(EnemyType.SAND_WOLF, count = 10, spawnIntervalSec = 0.85f, spawnIndex = 0),
                WaveEntry(EnemyType.STEPPE_CHARIOT, count = 3, spawnIntervalSec = 2.2f, spawnIndex = 1)
            ), rewardGold = 85),
            WaveSpec(2, "طبل و زره", listOf(
                WaveEntry(EnemyType.WAR_DRUMMER, count = 4, spawnIntervalSec = 1.7f, spawnIndex = 2),
                WaveEntry(EnemyType.SOGDIAN_ARMORED, count = 7, spawnIntervalSec = 1.4f, spawnIndex = 0)
            ), rewardGold = 105),
            WaveSpec(3, "یورش ارابه‌های فرغانه", listOf(
                WaveEntry(EnemyType.STEPPE_CHARIOT, count = 5, spawnIntervalSec = 1.9f, spawnIndex = 0),
                WaveEntry(EnemyType.CAMEL_ARCHER, count = 8, spawnIntervalSec = 1.1f, spawnIndex = 1),
                WaveEntry(EnemyType.TUNNEL_SABOTEUR, count = 7, spawnIntervalSec = 1.1f, spawnIndex = 2)
            ), rewardGold = 135)
        ),
        startingGold = 320,
        caravanMaxHp = 130,
        maxRelocateCharges = 4,
        rewardSilk = 48,
        rewardSaffron = 38
    )

    private fun level9FerganaToKashgar() = LevelBlueprint(
        id = -9L,
        levelNumber = 9,
        title = "مرحله ۹: بام جهان، گذرگاه پامیر تا کاشغر",
        originCity = "فرغانه",
        destinationCity = "کاشغر",
        storyIntro = "سرمای استخوان‌سوز کوهستان پامیر! راه باریک است و راهزنان کوهستانی از پرتگاه‌های برفی به شترهای حامل زعفران حمله می‌کنند.",
        merchantOfferText = "تاجر یشم کاشغر: «در کاشغر دو شاخهٔ جادهٔ ابریشم به هم می‌رسند. سنگ یشم ختن را با زعفران پارس تاخت بزنیم!»",
        biome = BiomeType.PAMIR_PASS,
        caravanPath = listOf(
            GridPos(0, 6), GridPos(1, 5), GridPos(2, 4), GridPos(3, 3),
            GridPos(4, 2), GridPos(5, 2), GridPos(6, 3), GridPos(7, 4),
            GridPos(8, 4), GridPos(9, 3), GridPos(10, 2), GridPos(11, 1)
        ),
        towerSlots = listOf(
            GridPos(1, 3), GridPos(2, 6), GridPos(3, 1), GridPos(4, 4),
            GridPos(5, 0), GridPos(6, 5), GridPos(7, 2), GridPos(8, 6),
            GridPos(9, 1), GridPos(10, 4)
        ),
        enemySpawns = listOf(GridPos(11, 5), GridPos(6, 7), GridPos(0, 1)),
        decorations = mapOf(
            GridPos(1, 1) to DecorationType.CANYON_ROCK,
            GridPos(4, 6) to DecorationType.ANCIENT_STATUE,
            GridPos(10, 6) to DecorationType.CANYON_ROCK
        ),
        waves = listOf(
            WaveSpec(1, "کمینگاه برفی", listOf(
                WaveEntry(EnemyType.SOGDIAN_ARMORED, count = 7, spawnIntervalSec = 1.4f, spawnIndex = 0, hpMultiplier = 1.15f),
                WaveEntry(EnemyType.SAND_WOLF, count = 10, spawnIntervalSec = 0.8f, spawnIndex = 1)
            ), rewardGold = 90),
            WaveSpec(2, "نقب‌زنان یخچال", listOf(
                WaveEntry(EnemyType.TUNNEL_SABOTEUR, count = 9, spawnIntervalSec = 1.0f, spawnIndex = 2, hpMultiplier = 1.15f),
                WaveEntry(EnemyType.STEPPE_CHARIOT, count = 4, spawnIntervalSec = 2.0f, spawnIndex = 0)
            ), rewardGold = 115),
            WaveSpec(3, "بهمنِ آهن و پولاد", listOf(
                WaveEntry(EnemyType.STEPPE_CHARIOT, count = 5, spawnIntervalSec = 1.8f, spawnIndex = 0, hpMultiplier = 1.2f),
                WaveEntry(EnemyType.WAR_DRUMMER, count = 4, spawnIntervalSec = 1.6f, spawnIndex = 1),
                WaveEntry(EnemyType.SOGDIAN_ARMORED, count = 8, spawnIntervalSec = 1.3f, spawnIndex = 2, hpMultiplier = 1.2f)
            ), rewardGold = 145)
        ),
        startingGold = 340,
        caravanMaxHp = 135,
        maxRelocateCharges = 5,
        rewardSilk = 52,
        rewardSaffron = 42
    )

    private fun level10KashgarToTurfan() = LevelBlueprint(
        id = -10L,
        levelNumber = 10,
        title = "مرحله ۱۰: کویر مرگ، تکله‌مکان تا تورفان",
        originCity = "کاشغر",
        destinationCity = "تورفان",
        storyIntro = "تکله‌مکان؛ جایی که می‌گویند «هر که وارد شود بازنمی‌گردد»! طوفان شن از چهار سو می‌وزد و راهزنان از دل گردوغبار بیرون می‌جهند.",
        merchantOfferText = "نگهبان قنات‌های کاریز تورفان: «آب گوارا و انگور تورفان جان تازه به کاروان تو می‌بخشد.»",
        biome = BiomeType.SANDSTORM_VALLEY,
        caravanPath = listOf(
            GridPos(0, 2), GridPos(1, 3), GridPos(2, 4), GridPos(3, 4),
            GridPos(4, 3), GridPos(5, 2), GridPos(6, 2), GridPos(7, 3),
            GridPos(8, 4), GridPos(9, 4), GridPos(10, 3), GridPos(11, 2)
        ),
        towerSlots = listOf(
            GridPos(1, 1), GridPos(2, 6), GridPos(3, 2), GridPos(4, 5),
            GridPos(5, 4), GridPos(6, 0), GridPos(7, 5), GridPos(8, 2),
            GridPos(9, 6), GridPos(10, 1)
        ),
        enemySpawns = listOf(GridPos(11, 6), GridPos(5, 7), GridPos(11, 0), GridPos(0, 6)),
        decorations = mapOf(
            GridPos(3, 0) to DecorationType.CARAVANSERAI_RUIN,
            GridPos(7, 7) to DecorationType.CANYON_ROCK
        ),
        waves = listOf(
            WaveSpec(1, "ارواح تکله‌مکان", listOf(
                WaveEntry(EnemyType.TUNNEL_SABOTEUR, count = 10, spawnIntervalSec = 0.95f, spawnIndex = 0, hpMultiplier = 1.2f),
                WaveEntry(EnemyType.SAND_WOLF, count = 12, spawnIntervalSec = 0.75f, spawnIndex = 1, hpMultiplier = 1.2f)
            ), rewardGold = 95),
            WaveSpec(2, "چهار بادِ غارتگر", listOf(
                WaveEntry(EnemyType.CAMEL_ARCHER, count = 9, spawnIntervalSec = 1.1f, spawnIndex = 2, hpMultiplier = 1.25f),
                WaveEntry(EnemyType.WAR_DRUMMER, count = 4, spawnIntervalSec = 1.6f, spawnIndex = 3),
                WaveEntry(EnemyType.STEPPE_CHARIOT, count = 4, spawnIntervalSec = 2.0f, spawnIndex = 0, hpMultiplier = 1.2f)
            ), rewardGold = 120),
            WaveSpec(3, "طوفان سرخ تکله‌مکان", listOf(
                WaveEntry(EnemyType.BOSS_RED_STORM_KHAN, count = 1, spawnIntervalSec = 3.0f, spawnIndex = 0, hpMultiplier = 1.1f),
                WaveEntry(EnemyType.TUNNEL_SABOTEUR, count = 10, spawnIntervalSec = 0.9f, spawnIndex = 1, hpMultiplier = 1.25f),
                WaveEntry(EnemyType.STEPPE_CHARIOT, count = 4, spawnIntervalSec = 1.8f, spawnIndex = 2, hpMultiplier = 1.25f)
            ), rewardGold = 165)
        ),
        startingGold = 360,
        caravanMaxHp = 140,
        maxRelocateCharges = 5,
        rewardSilk = 60,
        rewardSaffron = 48
    )

    private fun level11TurfanToDunhuang() = LevelBlueprint(
        id = -11L,
        levelNumber = 11,
        title = "مرحله ۱۱: غارهای هزار بودا در دونهوانگ",
        originCity = "تورفان",
        destinationCity = "دونهوانگ",
        storyIntro = "پیش از رسیدن به دیوار بزرگ و دروازهٔ یومن، اتحاد بزرگ راهزنان بیابان گُبی تمام ارابه‌ها و زره‌پوشان خود را به میدان آورده است!",
        merchantOfferText = "راهب و خطاط دونهوانگ: «طومارهای کهن دونهوانگ راز آتش یونانی و باروت را به برج‌های تو می‌آموزند!»",
        biome = BiomeType.GOLDEN_DESERT,
        caravanPath = listOf(
            GridPos(0, 4), GridPos(1, 5), GridPos(2, 6), GridPos(3, 5),
            GridPos(4, 3), GridPos(5, 1), GridPos(6, 1), GridPos(7, 3),
            GridPos(8, 5), GridPos(9, 6), GridPos(10, 5), GridPos(11, 4)
        ),
        towerSlots = listOf(
            GridPos(1, 3), GridPos(2, 4), GridPos(3, 7), GridPos(4, 1),
            GridPos(5, 3), GridPos(6, 3), GridPos(7, 1), GridPos(8, 7),
            GridPos(9, 4), GridPos(10, 3)
        ),
        enemySpawns = listOf(GridPos(11, 1), GridPos(11, 7), GridPos(5, 7), GridPos(0, 1)),
        decorations = mapOf(
            GridPos(0, 7) to DecorationType.ANCIENT_STATUE,
            GridPos(5, 5) to DecorationType.OASIS_POOL,
            GridPos(6, 5) to DecorationType.PALM_TREES,
            GridPos(11, 0) to DecorationType.ANCIENT_STATUE
        ),
        waves = listOf(
            WaveSpec(1, "محاصرهٔ دروازهٔ یشم", listOf(
                WaveEntry(EnemyType.SOGDIAN_ARMORED, count = 9, spawnIntervalSec = 1.2f, spawnIndex = 0, hpMultiplier = 1.3f),
                WaveEntry(EnemyType.STEPPE_CHARIOT, count = 5, spawnIntervalSec = 1.8f, spawnIndex = 1, hpMultiplier = 1.3f)
            ), rewardGold = 105),
            WaveSpec(2, "لشکر بیابان گُبی", listOf(
                WaveEntry(EnemyType.WAR_DRUMMER, count = 5, spawnIntervalSec = 1.5f, spawnIndex = 2, hpMultiplier = 1.3f),
                WaveEntry(EnemyType.TUNNEL_SABOTEUR, count = 12, spawnIntervalSec = 0.85f, spawnIndex = 3, hpMultiplier = 1.3f),
                WaveEntry(EnemyType.CAMEL_ARCHER, count = 10, spawnIntervalSec = 1.0f, spawnIndex = 0, hpMultiplier = 1.3f)
            ), rewardGold = 135),
            WaveSpec(3, "پیش‌قراولان خان بزرگ", listOf(
                WaveEntry(EnemyType.STEPPE_CHARIOT, count = 7, spawnIntervalSec = 1.5f, spawnIndex = 0, hpMultiplier = 1.35f),
                WaveEntry(EnemyType.SOGDIAN_ARMORED, count = 10, spawnIntervalSec = 1.1f, spawnIndex = 1, hpMultiplier = 1.35f),
                WaveEntry(EnemyType.SAND_WOLF, count = 14, spawnIntervalSec = 0.7f, spawnIndex = 2, hpMultiplier = 1.35f)
            ), rewardGold = 175)
        ),
        startingGold = 385,
        caravanMaxHp = 145,
        maxRelocateCharges = 5,
        rewardSilk = 68,
        rewardSaffron = 54
    )

    private fun level12DunhuangToChangan() = LevelBlueprint(
        id = -12L,
        levelNumber = 12,
        title = "مرحله ۱۲: دروازهٔ باشکوه چانگ‌آن (نبرد نهایی)",
        originCity = "دونهوانگ",
        destinationCity = "چانگ‌آن",
        storyIntro = "پس از هزار فرسنگ از پرسپولیس، برج‌های طلایی چانگ‌آن در افق پیداست! اما «خانِ طوفانِ سرخ» با تمام قوا برای آخرین نبرد جادهٔ ابریشم به میدان آمده است. کاروان خود را به پیروزی ابدی برسان!",
        merchantOfferText = "فرستادهٔ امپراتور در چانگ‌آن: «درود بر سردار بزرگ پارس! کاروان تو از هزار شهر گذشت و افسانهٔ جادهٔ ابریشم را جاودانه ساخت!»",
        biome = BiomeType.RED_CANYON,
        caravanPath = listOf(
            GridPos(0, 3), GridPos(1, 2), GridPos(2, 2), GridPos(3, 4),
            GridPos(4, 6), GridPos(5, 5), GridPos(6, 3), GridPos(7, 1),
            GridPos(8, 2), GridPos(9, 4), GridPos(10, 4), GridPos(11, 4)
        ),
        towerSlots = listOf(
            GridPos(1, 4), GridPos(2, 0), GridPos(3, 2), GridPos(3, 6),
            GridPos(5, 3), GridPos(5, 7), GridPos(6, 1), GridPos(7, 3),
            GridPos(8, 4), GridPos(9, 2), GridPos(10, 6), GridPos(10, 2)
        ),
        enemySpawns = listOf(GridPos(11, 1), GridPos(11, 7), GridPos(6, 7), GridPos(0, 7)),
        decorations = mapOf(
            GridPos(0, 0) to DecorationType.ANCIENT_STATUE,
            GridPos(11, 2) to DecorationType.CARAVANSERAI_RUIN,
            GridPos(11, 6) to DecorationType.CARAVANSERAI_RUIN
        ),
        waves = listOf(
            WaveSpec(1, "محاصرهٔ دروازهٔ امپراتوری", listOf(
                WaveEntry(EnemyType.STEPPE_CHARIOT, count = 6, spawnIntervalSec = 1.6f, spawnIndex = 0, hpMultiplier = 1.4f),
                WaveEntry(EnemyType.WAR_DRUMMER, count = 5, spawnIntervalSec = 1.5f, spawnIndex = 1, hpMultiplier = 1.4f),
                WaveEntry(EnemyType.TUNNEL_SABOTEUR, count = 10, spawnIntervalSec = 0.9f, spawnIndex = 2, hpMultiplier = 1.4f)
            ), rewardGold = 120),
            WaveSpec(2, "طوفان پولاد و آتش", listOf(
                WaveEntry(EnemyType.SOGDIAN_ARMORED, count = 12, spawnIntervalSec = 1.0f, spawnIndex = 0, hpMultiplier = 1.45f),
                WaveEntry(EnemyType.STEPPE_CHARIOT, count = 6, spawnIntervalSec = 1.5f, spawnIndex = 1, hpMultiplier = 1.45f),
                WaveEntry(EnemyType.SAND_WOLF, count = 15, spawnIntervalSec = 0.65f, spawnIndex = 3, hpMultiplier = 1.45f)
            ), rewardGold = 150),
            WaveSpec(3, "نبرد نهایی: خانِ طوفانِ سرخ", listOf(
                WaveEntry(EnemyType.BOSS_RED_STORM_KHAN, count = 2, spawnIntervalSec = 5.0f, spawnIndex = 0, hpMultiplier = 1.5f),
                WaveEntry(EnemyType.STEPPE_CHARIOT, count = 6, spawnIntervalSec = 1.6f, spawnIndex = 1, hpMultiplier = 1.5f),
                WaveEntry(EnemyType.WAR_DRUMMER, count = 5, spawnIntervalSec = 1.5f, spawnIndex = 2, hpMultiplier = 1.5f),
                WaveEntry(EnemyType.TUNNEL_SABOTEUR, count = 10, spawnIntervalSec = 0.9f, spawnIndex = 3, hpMultiplier = 1.5f)
            ), rewardGold = 250)
        ),
        startingGold = 430,
        caravanMaxHp = 160,
        maxRelocateCharges = 6,
        rewardSilk = 100,
        rewardSaffron = 85
    )

    /**
     * Parametric generator for the "Sandstorm Endless Mode" (حالت بی‌پایان طوفان شن)
     */
    fun generateEndlessSandstormLevel(seed: Long = System.currentTimeMillis()): LevelBlueprint {
        val rng = Random(seed)
        val path = mutableListOf<GridPos>()
        var currentRow = rng.nextInt(2, 6)
        for (col in 0 until 12) {
            path.add(GridPos(col, currentRow))
            if (col < 11) {
                val delta = listOf(-1, 0, 1).random(rng)
                val nextRow = (currentRow + delta).coerceIn(1, 6)
                if (nextRow != currentRow) {
                    currentRow = nextRow
                }
            }
        }

        val pathSet = path.toSet()
        val towerSlots = mutableListOf<GridPos>()
        for (col in 1..10) {
            for (row in 0..7) {
                val pos = GridPos(col, row)
                if (pos !in pathSet && path.any { it.distanceTo(pos) <= 1.5f }) {
                    if (towerSlots.none { it.distanceTo(pos) < 1.8f } && towerSlots.size < 10) {
                        towerSlots.add(pos)
                    }
                }
            }
        }

        val spawns = listOf(
            GridPos(11, 0),
            GridPos(11, 7),
            GridPos(6, 0),
            GridPos(6, 7)
        )

        val waves = (1..8).map { w ->
            val mult = 1f + (w - 1) * 0.28f
            val entries = mutableListOf(
                WaveEntry(EnemyType.DESERT_BANDIT, count = 5 + w * 2, spawnIntervalSec = 1.1f, spawnIndex = w % 4, hpMultiplier = mult),
                WaveEntry(EnemyType.SAND_WOLF, count = 4 + w * 2, spawnIntervalSec = 0.9f, spawnIndex = (w + 1) % 4, hpMultiplier = mult)
            )
            if (w >= 2) {
                entries.add(WaveEntry(EnemyType.CAMEL_ARCHER, count = 3 + w, spawnIntervalSec = 1.3f, spawnIndex = (w + 2) % 4, hpMultiplier = mult))
            }
            if (w >= 3) {
                entries.add(WaveEntry(EnemyType.SOGDIAN_ARMORED, count = 2 + w, spawnIntervalSec = 1.6f, spawnIndex = w % 4, hpMultiplier = mult))
                entries.add(WaveEntry(EnemyType.TUNNEL_SABOTEUR, count = 3 + w, spawnIntervalSec = 1.2f, spawnIndex = (w + 3) % 4, hpMultiplier = mult))
            }
            if (w >= 4) {
                entries.add(WaveEntry(EnemyType.WAR_DRUMMER, count = 2 + w / 2, spawnIntervalSec = 2.0f, spawnIndex = 1, hpMultiplier = mult))
                entries.add(WaveEntry(EnemyType.STEPPE_CHARIOT, count = 1 + w / 2, spawnIntervalSec = 2.2f, spawnIndex = 0, hpMultiplier = mult))
            }
            if (w % 4 == 0) {
                entries.add(WaveEntry(EnemyType.BOSS_RED_STORM_KHAN, count = w / 4, spawnIntervalSec = 4.0f, spawnIndex = 0, hpMultiplier = mult))
            }
            WaveSpec(
                waveNumber = w,
                title = "طوفان شن · موج $w",
                entries = entries,
                rewardGold = 65 + w * 20
            )
        }

        return LevelBlueprint(
            id = -999L,
            levelNumber = 99,
            title = "حالت بی‌پایان: طوفان شن",
            originCity = "واحهٔ گمشده",
            destinationCity = "سراب ابدی",
            storyIntro = "ژنراتور پارامتریک طوفان شن: ۸ موج سنگین و پیوسته با مسیر تصادفی! تا کجا می‌توانی کاروان را زنده نگه داری؟",
            merchantOfferText = "بازرگان طوفان: «هر موج که در طوفان شن دوام بیاوری، نامت در کتیبهٔ سرداران جادهٔ ابریشم بالاتر می‌رود!»",
            biome = BiomeType.SANDSTORM_VALLEY,
            caravanPath = path,
            towerSlots = towerSlots,
            enemySpawns = spawns,
            waves = waves,
            startingGold = 320,
            caravanMaxHp = 140,
            maxRelocateCharges = 5,
            rewardSilk = 75,
            rewardSaffron = 60
        )
    }

    /**
     * Weekly Boss Raid Level (رید باس هفتگی: خانِ طوفانِ سرخ)
     */
    fun generateWeeklyBossRaidLevel(): LevelBlueprint {
        return LevelBlueprint(
            id = -888L,
            levelNumber = 88,
            title = "رید باس هفتگی: محاصرهٔ خانِ طوفانِ سرخ",
            originCity = "کاروانسرای مرزی",
            destinationCity = "دژ طوفان سرخ",
            storyIntro = "رید باس ویژه: خانِ طوفانِ سرخ همراه با گارد ارابه‌رانان و طبل‌زنان قبیله به کاروان سلطنتی حمله کرده است! از جابه‌جایی برج‌ها و هر ۳ قابلیت قهرمان استفاده کن!",
            merchantOfferText = "خزانه‌دار شاهی: «شکست دادن خانِ طوفانِ سرخ پاداش افسانه‌ای ابریشم و زعفران به همراه دارد!»",
            biome = BiomeType.RED_CANYON,
            caravanPath = listOf(
                GridPos(0, 4), GridPos(1, 4), GridPos(2, 3), GridPos(3, 2),
                GridPos(4, 2), GridPos(5, 4), GridPos(6, 5), GridPos(7, 5),
                GridPos(8, 3), GridPos(9, 2), GridPos(10, 3), GridPos(11, 4)
            ),
            towerSlots = listOf(
                GridPos(1, 2), GridPos(2, 5), GridPos(3, 4), GridPos(4, 0),
                GridPos(5, 2), GridPos(6, 3), GridPos(7, 7), GridPos(8, 5),
                GridPos(9, 4), GridPos(10, 1)
            ),
            enemySpawns = listOf(GridPos(11, 1), GridPos(11, 7), GridPos(5, 7)),
            waves = listOf(
                WaveSpec(1, "گارد پیشرو خان", listOf(
                    WaveEntry(EnemyType.BOSS_RED_STORM_KHAN, count = 1, spawnIntervalSec = 3.0f, spawnIndex = 0, hpMultiplier = 1.1f),
                    WaveEntry(EnemyType.WAR_DRUMMER, count = 4, spawnIntervalSec = 1.6f, spawnIndex = 1),
                    WaveEntry(EnemyType.STEPPE_CHARIOT, count = 4, spawnIntervalSec = 1.8f, spawnIndex = 2)
                ), rewardGold = 140),
                WaveSpec(2, "خشم نهایی خانِ طوفانِ سرخ", listOf(
                    WaveEntry(EnemyType.BOSS_RED_STORM_KHAN, count = 2, spawnIntervalSec = 4.5f, spawnIndex = 0, hpMultiplier = 1.45f),
                    WaveEntry(EnemyType.SOGDIAN_ARMORED, count = 10, spawnIntervalSec = 1.1f, spawnIndex = 1, hpMultiplier = 1.3f),
                    WaveEntry(EnemyType.TUNNEL_SABOTEUR, count = 10, spawnIntervalSec = 0.95f, spawnIndex = 2, hpMultiplier = 1.3f)
                ), rewardGold = 240)
            ),
            startingGold = 420,
            caravanMaxHp = 150,
            maxRelocateCharges = 6,
            rewardSilk = 90,
            rewardSaffron = 75
        )
    }
}

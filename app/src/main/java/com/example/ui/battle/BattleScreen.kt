package com.example.ui.battle

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FastForward
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.Nightlight
import androidx.compose.material.icons.filled.OpenWith
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.audio.SilkRoadSoundEngine
import com.example.data.local.PlayerProgressEntity
import com.example.data.model.EnemyType
import com.example.data.model.GridPos
import com.example.data.model.LevelBlueprint
import com.example.data.model.TowerEvolution
import com.example.data.model.TowerType
import com.example.ui.theme.BrightAmber
import com.example.ui.theme.CrimsonSilk
import com.example.ui.theme.OasisCyan
import com.example.ui.theme.PersianTurquoise
import com.example.ui.theme.SaffronGold
import kotlinx.coroutines.delay
import kotlin.math.hypot

private data class SpawnQueueItem(
    val enemyType: EnemyType,
    val spawnPos: GridPos,
    val delayAfterSec: Float,
    val hpMultiplier: Float
)

@Composable
fun BattleScreen(
    level: LevelBlueprint,
    sessionId: Long,
    playerProgress: PlayerProgressEntity,
    onVictoryRecorded: (stars: Int, bonusGold: Int, bonusSilk: Int, bonusSaffron: Int, wavesCleared: Int) -> Unit,
    onEditThisLevel: (LevelBlueprint) -> Unit,
    onBackToMap: () -> Unit,
    onNextCampaignLevel: () -> Unit
) {
    BackHandler {
        onBackToMap()
    }

    val maxHpWithMeta = remember(level, sessionId, playerProgress.wagonArmorLevel) {
        level.caravanMaxHp + (playerProgress.wagonArmorLevel - 1) * 20
    }
    val maxRelocateWithMeta = remember(level, sessionId, playerProgress.camelEquipLevel) {
        level.maxRelocateCharges + (playerProgress.camelEquipLevel - 1)
    }

    var phase by remember(level, sessionId) { mutableStateOf(BattlePhase.NIGHT_CAMP) }
    var currentWaveIndex by remember(level, sessionId) { mutableIntStateOf(0) }
    var gold by remember(level, sessionId) { mutableIntStateOf(level.startingGold) }
    var caravanHp by remember(level, sessionId) { mutableIntStateOf(maxHpWithMeta) }
    var relocateCharges by remember(level, sessionId) { mutableIntStateOf(maxRelocateWithMeta) }
    var relocateRechargeProgress by remember(level, sessionId) { mutableFloatStateOf(0f) }

    var caravanProgressIndex by remember(level, sessionId) { mutableFloatStateOf(0f) }
    var selectedSlot by remember(level, sessionId) { mutableStateOf<GridPos?>(level.towerSlots.firstOrNull()) }
    var isRelocateModeActive by remember(level, sessionId) { mutableStateOf(false) }
    var speedMultiplier by remember(level, sessionId) { mutableFloatStateOf(1f) }
    var combatToast by remember(level, sessionId) {
        mutableStateOf("فاز شب (اردوگاه واحه): روی یک پایگاه فیروزه‌ای بزنید، برج بسازید و سپس حرکت کاروان را آغاز کنید!")
    }

    val towers = remember(level, sessionId) { mutableStateListOf<PlacedTower>() }
    val enemies = remember(level, sessionId) { mutableStateListOf<ActiveEnemy>() }
    val projectiles = remember(level, sessionId) { mutableStateListOf<ActiveProjectile>() }
    val relocationTrails = remember(level, sessionId) { mutableStateListOf<RelocationTrailEffect>() }
    val floatingTexts = remember(level, sessionId) { mutableStateListOf<FloatingBattleText>() }
    val spawnQueue = remember(level, sessionId) { mutableStateListOf<SpawnQueueItem>() }

    var spawnTimerSec by remember(level, sessionId) { mutableFloatStateOf(0f) }
    var guardShotTimerSec by remember(level, sessionId) { mutableFloatStateOf(0f) }
    var nextEntityId by remember(level, sessionId) { mutableLongStateOf(10L) }

    // Active Hero Abilities Cooldowns & Durations
    val heroCooldownReduction = ((playerProgress.heroRankLevel - 1) * 0.08f).coerceAtMost(0.45f)
    var mistCooldown by remember(level, sessionId) { mutableFloatStateOf(0f) }
    var drumCooldown by remember(level, sessionId) { mutableFloatStateOf(0f) }
    var eagleCooldown by remember(level, sessionId) { mutableFloatStateOf(0f) }

    var desertMistRemaining by remember(level, sessionId) { mutableFloatStateOf(0f) }
    var warDrumRemaining by remember(level, sessionId) { mutableFloatStateOf(0f) }
    var eagleAnimRemaining by remember(level, sessionId) { mutableFloatStateOf(0f) }

    var merchantChoiceApplied by remember(level, sessionId) { mutableStateOf(false) }
    var merchantBonusText by remember(level, sessionId) { mutableStateOf<String?>(null) }

    fun handleTowerRelocation(fromPos: GridPos, toPos: GridPos) {
        val towerIdx = towers.indexOfFirst { it.pos == fromPos }
        if (towerIdx == -1) return
        if (toPos !in level.towerSlots) {
            combatToast = "برج فقط روی پایگاه‌های تاکتیکی (مربع‌های فیروزه‌ای) قابل قرارگیری است!"
            return
        }
        if (towers.any { it.pos == toPos }) {
            combatToast = "این پایگاه در حال حاضر پر است!"
            return
        }

        if (phase == BattlePhase.DAY_MARCH && relocateCharges <= 0) {
            combatToast = "شارژ جابه‌جایی برج تمام شده! چند ثانیه صبر کنید یا طبل جنگ بزنید."
            return
        }

        val movingTower = towers[towerIdx]
        if (phase == BattlePhase.DAY_MARCH) {
            relocateCharges = (relocateCharges - 1).coerceAtLeast(0)
        }
        towers[towerIdx] = movingTower.copy(
            pos = toPos,
            relocateBoostRemaining = if (phase == BattlePhase.DAY_MARCH) 6.0f else 0f,
            totalRelocations = movingTower.totalRelocations + 1
        )
        relocationTrails.add(
            RelocationTrailEffect(
                fromPos = fromPos,
                toPos = toPos,
                alpha = 1.0f,
                color = movingTower.type.primaryColor
            )
        )
        selectedSlot = toPos
        isRelocateModeActive = false
        SilkRoadSoundEngine.playTowerRelocate()
        combatToast = if (phase == BattlePhase.DAY_MARCH) {
            "جابه‌جایی تاکتیکی انجام شد! +۴۵٪ سرعت شلیک به مدت ۶ ثانیه!"
        } else {
            "برج در اردوگاه شبانه به پایگاه جدید منتقل شد."
        }
    }

    fun startDayWave() {
        val wave = level.waves.getOrNull(currentWaveIndex) ?: return
        spawnQueue.clear()
        val spawns = level.enemySpawns.ifEmpty { listOf(GridPos(level.gridCols - 1, 0)) }
        wave.entries.forEach { entry ->
            val spPos = spawns[entry.spawnIndex.coerceIn(0, spawns.size - 1)]
            repeat(entry.count) {
                spawnQueue.add(
                    SpawnQueueItem(
                        enemyType = entry.enemyType,
                        spawnPos = spPos,
                        delayAfterSec = entry.spawnIntervalSec,
                        hpMultiplier = entry.hpMultiplier
                    )
                )
            }
        }
        spawnTimerSec = 0.25f
        phase = BattlePhase.DAY_MARCH
        isRelocateModeActive = false
        SilkRoadSoundEngine.playWarDrum()
        combatToast = "فاز روز آغاز شد! کاروان در حرکت است؛ در صورت نیاز برج‌ها را به جلو جابه‌جا کنید!"
    }

    // 60 FPS Tactical Game Loop during DAY_MARCH
    LaunchedEffect(phase, currentWaveIndex, speedMultiplier, level, sessionId) {
        if (phase != BattlePhase.DAY_MARCH) return@LaunchedEffect
        val baseTickMs = 33L
        while (phase == BattlePhase.DAY_MARCH) {
            delay(baseTickMs)
            val dt = (baseTickMs / 1000f) * speedMultiplier

            // 1. Update Hero Timers & Relocation Recharge
            desertMistRemaining = (desertMistRemaining - dt).coerceAtLeast(0f)
            warDrumRemaining = (warDrumRemaining - dt).coerceAtLeast(0f)
            eagleAnimRemaining = (eagleAnimRemaining - dt).coerceAtLeast(0f)
            mistCooldown = (mistCooldown - dt).coerceAtLeast(0f)
            drumCooldown = (drumCooldown - dt).coerceAtLeast(0f)
            eagleCooldown = (eagleCooldown - dt).coerceAtLeast(0f)

            if (relocateCharges < maxRelocateWithMeta) {
                val rechargeRate = 0.14f * (1f + (playerProgress.camelEquipLevel - 1) * 0.15f)
                relocateRechargeProgress += dt * rechargeRate
                if (relocateRechargeProgress >= 1f) {
                    relocateCharges += 1
                    relocateRechargeProgress = 0f
                }
            } else {
                relocateRechargeProgress = 0f
            }

            // 2. Advance Caravan along Silk Road path
            val pathMaxIdx = (level.caravanPath.size - 1).coerceAtLeast(1).toFloat()
            val totalWaves = level.waves.size.coerceAtLeast(1)
            val targetMaxForThisWave = pathMaxIdx * ((currentWaveIndex + 1).toFloat() / totalWaves)
            val caravanSpeed = 0.26f * (1f + (playerProgress.camelEquipLevel - 1) * 0.05f)
            if (caravanProgressIndex < targetMaxForThisWave) {
                caravanProgressIndex = (caravanProgressIndex + dt * caravanSpeed).coerceAtMost(targetMaxForThisWave)
            }

            // Sample Caravan current world grid position
            val cIdx = caravanProgressIndex.coerceIn(0f, pathMaxIdx)
            val c0 = level.caravanPath[cIdx.toInt()]
            val c1 = level.caravanPath[(cIdx.toInt() + 1).coerceAtMost(level.caravanPath.size - 1)]
            val cFrac = cIdx - cIdx.toInt()
            val caravanX = c0.col + (c1.col - c0.col) * cFrac
            val caravanY = c0.row + (c1.row - c0.row) * cFrac

            // 3. Spawn enemies from Queue
            if (spawnQueue.isNotEmpty()) {
                spawnTimerSec -= dt
                if (spawnTimerSec <= 0f) {
                    val nextItem = spawnQueue.removeAt(0)
                    val maxHp = nextItem.enemyType.baseHp * nextItem.hpMultiplier
                    enemies.add(
                        ActiveEnemy(
                            id = nextEntityId++,
                            type = nextItem.enemyType,
                            x = nextItem.spawnPos.col.toFloat(),
                            y = nextItem.spawnPos.row.toFloat(),
                            hp = maxHp,
                            maxHp = maxHp,
                            targetPathIndex = (level.caravanPath.size - 1).coerceAtLeast(0)
                        )
                    )
                    spawnTimerSec = nextItem.delayAfterSec
                }
            }

            // 4. Move Enemies toward the Caravan & apply DOT (Burn/Poison)
            val drummers = enemies.filter { it.type.isSpeedBuffer }
            val windCatchers = towers.filter { it.type == TowerType.WIND_CATCHER }

            val survivingEnemies = mutableListOf<ActiveEnemy>()
            for (enemy in enemies) {
                var hpNow = enemy.hp
                val burnLeft = (enemy.burnTimer - dt).coerceAtLeast(0f)
                val poisonLeft = (enemy.poisonTimer - dt).coerceAtLeast(0f)
                val slowLeft = (enemy.slowTimer - dt).coerceAtLeast(0f)
                var revealLeft = (enemy.revealedTimer - dt).coerceAtLeast(0f)

                if (enemy.burnTimer > 0f) hpNow -= 14f * dt
                if (enemy.poisonTimer > 0f) hpNow -= 18f * dt

                // Reveal stealth saboteurs if close to caravan or inside Wind Catcher range
                val distToCaravan = hypot(enemy.x - caravanX, enemy.y - caravanY)
                val nearWindCatcher = windCatchers.any {
                    hypot(enemy.x - it.pos.col, enemy.y - it.pos.row) <= it.effectiveRange()
                }
                if (distToCaravan <= 1.85f || nearWindCatcher) {
                    revealLeft = maxOf(revealLeft, 2.5f)
                }

                if (hpNow <= 0f) {
                    gold += enemy.type.goldReward
                    continue
                }

                val hasDrummerBuff = drummers.any {
                    it.id != enemy.id && hypot(it.x - enemy.x, it.y - enemy.y) <= 2.4f
                }
                var speed = enemy.type.speedCellsPerSec
                if (hasDrummerBuff) speed *= 1.35f
                if (slowLeft > 0f) speed *= 0.55f
                if (desertMistRemaining > 0f) speed *= 0.45f

                val dx = caravanX - enemy.x
                val dy = caravanY - enemy.y
                val dist = hypot(dx, dy)
                if (dist <= 0.48f) {
                    // Enemy strikes the caravan!
                    val mistMitigation = if (desertMistRemaining > 0f) 0.5f else 1.0f
                    val dmg = (enemy.type.caravanDamage * mistMitigation).toInt().coerceAtLeast(1)
                    caravanHp = (caravanHp - dmg).coerceAtLeast(0)
                    SilkRoadSoundEngine.playCatapultBoom()
                    if (caravanHp <= 0) {
                        phase = BattlePhase.CARAVAN_DEFEAT
                        break
                    }
                } else {
                    val step = (speed * dt).coerceAtMost(dist)
                    val nx = enemy.x + (dx / dist) * step
                    val ny = enemy.y + (dy / dist) * step
                    survivingEnemies.add(
                        enemy.copy(
                            x = nx,
                            y = ny,
                            hp = hpNow,
                            burnTimer = burnLeft,
                            poisonTimer = poisonLeft,
                            slowTimer = slowLeft,
                            revealedTimer = revealLeft
                        )
                    )
                }
            }
            enemies.clear()
            enemies.addAll(survivingEnemies)

            if (phase == BattlePhase.CARAVAN_DEFEAT) break

            // 5. Permanent Caravan Guard Auto-Turret
            guardShotTimerSec -= dt
            if (guardShotTimerSec <= 0f && enemies.isNotEmpty()) {
                val target = enemies
                    .filter { !it.isCurrentlyStealthed }
                    .minByOrNull { hypot(it.x - caravanX, it.y - caravanY) }
                if (target != null && hypot(target.x - caravanX, target.y - caravanY) <= 3.2f) {
                    val guardDmg = 14f + (playerProgress.permanentGuardLevel - 1) * 12f
                    val idx = enemies.indexOfFirst { it.id == target.id }
                    if (idx != -1) {
                        val updatedHp = enemies[idx].hp - guardDmg
                        projectiles.add(
                            ActiveProjectile(
                                id = nextEntityId++,
                                fromX = caravanX,
                                fromY = caravanY,
                                toX = target.x,
                                toY = target.y,
                                progress = 0f,
                                color = Color(0xFF48CAE4)
                            )
                        )
                        if (updatedHp <= 0f) {
                            gold += enemies[idx].type.goldReward
                            enemies.removeAt(idx)
                        } else {
                            enemies[idx] = enemies[idx].copy(hp = updatedHp)
                        }
                    }
                    guardShotTimerSec = 1.15f
                }
            }

            // 6. Towers Fire at Enemies in Range
            val fireTemples = towers.filter { it.evolution == TowerEvolution.FIRE_TEMPLE }
            for (tIdx in towers.indices) {
                val tower = towers[tIdx]
                val newRelocateBoost = (tower.relocateBoostRemaining - dt).coerceAtLeast(0f)
                val newCooldown = (tower.cooldownRemaining - dt).coerceAtLeast(0f)

                if (newCooldown <= 0f && enemies.isNotEmpty()) {
                    val range = tower.effectiveRange()
                    val validTargets = enemies.filter { e ->
                        (!e.isCurrentlyStealthed || tower.type == TowerType.WIND_CATCHER) &&
                            hypot(e.x - tower.pos.col, e.y - tower.pos.row) <= range
                    }
                    // Prioritize closest to caravan
                    val primaryTarget = validTargets.minByOrNull { e ->
                        hypot(e.x - caravanX, e.y - caravanY)
                    }

                    if (primaryTarget != null) {
                        val nearbyTemple = fireTemples.any {
                            it.id != tower.id && it.pos.distanceTo(tower.pos) <= 2.8f
                        }
                        val baseDmg = tower.effectiveDamage(warDrumRemaining > 0f)
                        val splashRadius = tower.type.splashRadiusCells

                        projectiles.add(
                            ActiveProjectile(
                                id = nextEntityId++,
                                fromX = tower.pos.col.toFloat(),
                                fromY = tower.pos.row.toFloat(),
                                toX = primaryTarget.x,
                                toY = primaryTarget.y,
                                progress = 0f,
                                color = tower.type.primaryColor,
                                isSplash = splashRadius > 0f
                            )
                        )

                        if (tower.type == TowerType.CATAPULT) {
                            SilkRoadSoundEngine.playCatapultBoom()
                        } else {
                            SilkRoadSoundEngine.playArrowShot()
                        }

                        // Alchemist / Silk Mint gold generation
                        if (tower.type.goldPerShot > 0) {
                            val bonusCoin = if (tower.evolution == TowerEvolution.SILK_MINT) 6 else tower.type.goldPerShot
                            gold += bonusCoin
                        }

                        // Apply damage (single target or AoE splash)
                        val afterHit = mutableListOf<ActiveEnemy>()
                        for (e in enemies) {
                            val distToImpact = hypot(e.x - primaryTarget.x, e.y - primaryTarget.y)
                            val isHit = if (splashRadius > 0f) {
                                distToImpact <= splashRadius
                            } else {
                                e.id == primaryTarget.id
                            }

                            if (isHit) {
                                val armorPen = if (tower.evolution == TowerEvolution.HEAVY_BALLISTA || tower.type == TowerType.SPEAR_GUARD) {
                                    0.1f
                                } else {
                                    e.type.armor
                                }
                                val finalDmg = baseDmg * (1f - armorPen.coerceIn(0f, 0.7f))
                                val nextHp = e.hp - finalDmg
                                if (nextHp <= 0f) {
                                    gold += e.type.goldReward
                                } else {
                                    val addBurn = if (tower.evolution == TowerEvolution.FIRE_ARCHER || tower.evolution == TowerEvolution.TAR_MORTAR) 3.5f else e.burnTimer
                                    val addPoison = if (tower.evolution == TowerEvolution.SCORPION_HUNTER) 4.0f else e.poisonTimer
                                    val addSlow = if (tower.type.slowFactor > 0f || tower.evolution == TowerEvolution.BLINDING_VORTEX) 2.6f else e.slowTimer
                                    val addReveal = if (tower.type == TowerType.WIND_CATCHER) 4.0f else e.revealedTimer
                                    afterHit.add(
                                        e.copy(
                                            hp = nextHp,
                                            burnTimer = addBurn,
                                            poisonTimer = addPoison,
                                            slowTimer = addSlow,
                                            revealedTimer = addReveal
                                        )
                                    )
                                }
                            } else {
                                afterHit.add(e)
                            }
                        }
                        enemies.clear()
                        enemies.addAll(afterHit)

                        val nextInterval = tower.effectiveFireInterval(warDrumRemaining > 0f, nearbyTemple)
                        towers[tIdx] = tower.copy(
                            cooldownRemaining = nextInterval,
                            relocateBoostRemaining = newRelocateBoost
                        )
                    } else {
                        towers[tIdx] = tower.copy(
                            cooldownRemaining = 0f,
                            relocateBoostRemaining = newRelocateBoost
                        )
                    }
                } else {
                    towers[tIdx] = tower.copy(
                        cooldownRemaining = newCooldown,
                        relocateBoostRemaining = newRelocateBoost
                    )
                }
            }

            // 7. Animate Projectiles & Relocation Trails
            val nextProjectiles = projectiles.mapNotNull { p ->
                val np = p.progress + dt * 4.5f
                if (np >= 1f) null else p.copy(progress = np)
            }
            projectiles.clear()
            projectiles.addAll(nextProjectiles)

            val nextTrails = relocationTrails.mapNotNull { tr ->
                val na = tr.alpha - dt * 1.4f
                if (na <= 0f) null else tr.copy(alpha = na)
            }
            relocationTrails.clear()
            relocationTrails.addAll(nextTrails)

            // 8. Check Wave Clear Condition
            if (spawnQueue.isEmpty() && enemies.isEmpty() && caravanProgressIndex >= targetMaxForThisWave - 0.05f) {
                val waveReward = level.waves.getOrNull(currentWaveIndex)?.rewardGold ?: 50
                gold += waveReward
                if (currentWaveIndex + 1 < level.waves.size) {
                    currentWaveIndex += 1
                    phase = BattlePhase.NIGHT_CAMP
                    relocateCharges = (relocateCharges + 1).coerceAtMost(maxRelocateWithMeta)
                    SilkRoadSoundEngine.playCamelBell()
                    combatToast = "موج ${currentWaveIndex} دفع شد! (+$waveReward سکه). اردوگاه شبانه برپا شد: برج‌ها را ارتقا دهید یا جابه‌جا کنید."
                } else {
                    // All waves cleared! Caravan arrived at Destination City!
                    caravanProgressIndex = pathMaxIdx
                    phase = BattlePhase.CITY_VICTORY
                    val stars = when {
                        caravanHp >= maxHpWithMeta * 0.8f -> 3
                        caravanHp >= maxHpWithMeta * 0.45f -> 2
                        else -> 1
                    }
                    onVictoryRecorded(stars, gold / 2, 0, 0, level.waves.size)
                }
            }
        }
    }

    val selectedTower = towers.find { it.pos == selectedSlot }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 10.dp, vertical = 6.dp)
    ) {
        // Top Tactical Header Bar
        Card(
            colors = CardDefaults.cardColors(
                containerColor = if (phase == BattlePhase.NIGHT_CAMP) Color(0xFF1A1E2E) else Color(0xFF2B1C12)
            ),
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = if (phase == BattlePhase.NIGHT_CAMP) Icons.Default.Nightlight else Icons.Default.WbSunny,
                            contentDescription = "فاز بازی",
                            tint = if (phase == BattlePhase.NIGHT_CAMP) OasisCyan else SaffronGold,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "${level.originCity} ⬅ ${level.destinationCity} (${level.title})",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurface,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        if (level.isCustom) {
                            Surface(
                                color = PersianTurquoise.copy(alpha = 0.25f),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier
                                    .testTag("edit_custom_level_button")
                                    .clickable { onEditThisLevel(level) }
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        Icons.Default.Edit,
                                        contentDescription = "ویرایش مرحله",
                                        tint = OasisCyan,
                                        modifier = Modifier.size(15.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("ویرایش", fontSize = 11.sp, color = OasisCyan)
                                }
                            }
                        }
                        Surface(
                            color = if (speedMultiplier > 1f) SaffronGold else Color(0xFF3A281B),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .testTag("battle_speed_toggle")
                                .clickable {
                                    speedMultiplier = if (speedMultiplier == 1f) 2f else 1f
                                }
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    Icons.Default.FastForward,
                                    contentDescription = "سرعت بازی",
                                    tint = if (speedMultiplier > 1f) Color.Black else SaffronGold,
                                    modifier = Modifier.size(15.dp)
                                )
                                Text(
                                    text = if (speedMultiplier > 1f) "2x" else "1x",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (speedMultiplier > 1f) Color.Black else SaffronGold
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                // Vital Stats Row: Caravan HP, Gold, Wave, and Portable Tower Relocation Charges!
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Caravan HP
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.Favorite,
                            contentDescription = "جان کاروان",
                            tint = CrimsonSilk,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "کاروان: $caravanHp/$maxHpWithMeta",
                            style = MaterialTheme.typography.labelLarge,
                            color = Color.White
                        )
                    }

                    // Gold
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.MonetizationOn,
                            contentDescription = "سکه",
                            tint = BrightAmber,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "$gold سکه",
                            style = MaterialTheme.typography.labelLarge,
                            color = BrightAmber
                        )
                    }

                    // Wave Counter
                    Text(
                        text = "موج ${currentWaveIndex + 1} از ${level.waves.size}",
                        style = MaterialTheme.typography.labelLarge,
                        color = OasisCyan
                    )

                    // Portable Tower Relocation Charges (Signature Twist!)
                    Surface(
                        color = if (relocateCharges > 0) PersianTurquoise.copy(alpha = 0.25f) else Color(0xFF3A2424),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.OpenWith,
                                contentDescription = "شارژ جابه‌جایی برج",
                                tint = OasisCyan,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "جابه‌جایی: $relocateCharges/$maxRelocateWithMeta",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = OasisCyan
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        // Live Tactical Tip / Phase Banner
        Surface(
            color = if (isRelocateModeActive) PersianTurquoise.copy(alpha = 0.28f) else Color(0xFF241811),
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = combatToast,
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (isRelocateModeActive) OasisCyan else BrightAmber,
                    modifier = Modifier.weight(1f),
                    maxLines = 2
                )

                if (phase == BattlePhase.NIGHT_CAMP) {
                    Button(
                        onClick = { startDayWave() },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = SaffronGold,
                            contentColor = Color(0xFF1B1108)
                        ),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .padding(start = 8.dp)
                            .testTag("start_day_wave_button")
                    ) {
                        Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("حرکت کاروان (فاز روز)", fontWeight = FontWeight.ExtraBold, fontSize = 12.sp)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        // Center Interactive Desert Canyon Canvas
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .clip(RoundedCornerShape(16.dp))
                .border(
                    width = 2.dp,
                    color = if (phase == BattlePhase.NIGHT_CAMP) Color(0xFF3949AB) else SaffronGold.copy(alpha = 0.65f),
                    shape = RoundedCornerShape(16.dp)
                )
        ) {
            BattleTacticalCanvas(
                level = level,
                phase = phase,
                towers = towers,
                enemies = enemies,
                projectiles = projectiles,
                relocationTrails = relocationTrails,
                floatingTexts = floatingTexts,
                caravanProgressIndex = caravanProgressIndex,
                selectedSlot = selectedSlot,
                isRelocateModeActive = isRelocateModeActive,
                desertMistRemaining = desertMistRemaining,
                warDrumRemaining = warDrumRemaining,
                eagleAnimRemaining = eagleAnimRemaining,
                onCellTapped = { tappedPos ->
                    if (isRelocateModeActive && selectedTower != null) {
                        handleTowerRelocation(selectedTower.pos, tappedPos)
                    } else if (tappedPos in level.towerSlots) {
                        // If a tower is already selected and user taps an empty slot while in Day March, offer direct relocation if relocate mode is on, otherwise select the slot
                        selectedSlot = tappedPos
                        val existing = towers.find { it.pos == tappedPos }
                        combatToast = if (existing != null) {
                            "برج «${existing.type.titleFa}» (سطح ${existing.level}) انتخاب شد. می‌توانید ارتقا دهید یا به پایگاه دیگر بکشید!"
                        } else {
                            "پایگاه خالی انتخاب شد. یکی از ۵ برج زیر را برای ساخت انتخاب کنید."
                        }
                    } else {
                        combatToast = "برای ساخت یا جابه‌جایی برج، روی مربع‌های فیروزه‌ای (پایگاه‌های برج) بزنید یا برج را بکشید."
                    }
                },
                onTowerDraggedToSlot = { fromPos, toPos ->
                    handleTowerRelocation(fromPos, toPos)
                }
            )
        }

        Spacer(modifier = Modifier.height(6.dp))

        // Bottom Control Deck: Tower Build / Upgrade / Relocate + Active Companion Hero Skills
        Card(
            colors = CardDefaults.cardColors(containerColor = Color(0xFF211711)),
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(10.dp)) {
                // Row 1: Companion Hero Active Abilities + Caravan Repair
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "مهارت قهرمان:",
                        style = MaterialTheme.typography.labelMedium,
                        color = SaffronGold
                    )

                    // 1. Desert Mist
                    HeroSkillChip(
                        title = "مه کویر",
                        subtitle = if (mistCooldown > 0f) "${mistCooldown.toInt()}s" else "کندی ۵۵٪ + سپر",
                        icon = Icons.Default.Cloud,
                        accentColor = OasisCyan,
                        enabled = mistCooldown <= 0f && phase == BattlePhase.DAY_MARCH,
                        testTag = "hero_skill_mist",
                        onClick = {
                            desertMistRemaining = 6.0f + (playerProgress.heroRankLevel - 1) * 0.8f
                            mistCooldown = 16f * (1f - heroCooldownReduction)
                            SilkRoadSoundEngine.playTowerRelocate()
                            combatToast = "مه کویر فعال شد! کاروان در پوشش مه قرار گرفت و راهزنان ۵۵٪ کند شدند."
                        }
                    )

                    // 2. War Drum
                    HeroSkillChip(
                        title = "طبل جنگ",
                        subtitle = if (drumCooldown > 0f) "${drumCooldown.toInt()}s" else "+۶۰٪ سرعت +۱ جابه‌جایی",
                        icon = Icons.Default.Campaign,
                        accentColor = CrimsonSilk,
                        enabled = drumCooldown <= 0f && phase == BattlePhase.DAY_MARCH,
                        testTag = "hero_skill_drum",
                        onClick = {
                            warDrumRemaining = 6.0f + (playerProgress.heroRankLevel - 1) * 0.8f
                            relocateCharges = (relocateCharges + 1).coerceAtMost(maxRelocateWithMeta)
                            drumCooldown = 18f * (1f - heroCooldownReduction)
                            SilkRoadSoundEngine.playWarDrum()
                            combatToast = "طبل جنگ به صدا درآمد! سرعت تمام برج‌ها +۶۰٪ شد و ۱ شارژ جابه‌جایی برج اضافه شد!"
                        }
                    )

                    // 3. Scout Eagle
                    HeroSkillChip(
                        title = "عقاب دیده‌بان",
                        subtitle = if (eagleCooldown > 0f) "${eagleCooldown.toInt()}s" else "کشف نقب‌زن + ضربه + طلا",
                        icon = Icons.Default.Visibility,
                        accentColor = BrightAmber,
                        enabled = eagleCooldown <= 0f && phase == BattlePhase.DAY_MARCH,
                        testTag = "hero_skill_eagle",
                        onClick = {
                            eagleAnimRemaining = 2.0f
                            eagleCooldown = 20f * (1f - heroCooldownReduction)
                            val eagleDamage = 75f + (playerProgress.heroRankLevel - 1) * 25f
                            val updated = enemies.mapNotNull { e ->
                                val nextHp = e.hp - eagleDamage
                                if (nextHp <= 0f) {
                                    gold += e.type.goldReward
                                    null
                                } else {
                                    e.copy(hp = nextHp, revealedTimer = 8.0f)
                                }
                            }
                            enemies.clear()
                            enemies.addAll(updated)
                            gold += 35
                            SilkRoadSoundEngine.playEagleCry()
                            combatToast = "عقاب دیده‌بان پرواز کرد! تمام نقب‌زن‌ها آشکار شدند، به دشمنان آسیب وارد شد و +۳۵ سکه یافتید!"
                        }
                    )

                    // Repair Caravan in Night Camp
                    if (phase == BattlePhase.NIGHT_CAMP && caravanHp < maxHpWithMeta) {
                        HeroSkillChip(
                            title = "تعمیر ارابه",
                            subtitle = "۴۰ سکه (+۳۰ جان)",
                            icon = Icons.Default.Build,
                            accentColor = Color(0xFF66BB6A),
                            enabled = gold >= 40,
                            testTag = "repair_caravan_button",
                            onClick = {
                                gold -= 40
                                caravanHp = (caravanHp + 30).coerceAtMost(maxHpWithMeta)
                                SilkRoadSoundEngine.playGoldChime()
                                combatToast = "ارابه‌های کاروان در اردوگاه شبانه تعمیر شدند (+۳۰ جان)."
                            }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Row 2: Contextual Tower Slot Inspector (Build 5 Towers OR Upgrade/Evolve/Relocate Selected Tower)
                if (selectedTower == null) {
                    Text(
                        text = if (selectedSlot != null) {
                            "ساخت برج قابل‌حمل در پایگاه (${selectedSlot!!.col}, ${selectedSlot!!.row}):"
                        } else {
                            "یک پایگاه برج (مربع فیروزه‌ای) روی نقشه انتخاب کنید:"
                        },
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        TowerType.entries.forEach { towerType ->
                            val canAfford = gold >= towerType.baseCost && selectedSlot != null
                            Surface(
                                color = if (canAfford) Color(0xFF2E2018) else Color(0xFF1B130E),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .border(
                                        width = 1.dp,
                                        color = if (canAfford) towerType.primaryColor else Color(0xFF3E2C22),
                                        shape = RoundedCornerShape(12.dp)
                                    )
                                    .testTag("build_tower_${towerType.name.lowercase()}")
                                    .clickable(enabled = selectedSlot != null) {
                                        val slot = selectedSlot ?: return@clickable
                                        if (gold >= towerType.baseCost) {
                                            gold -= towerType.baseCost
                                            towers.add(
                                                PlacedTower(
                                                    id = nextEntityId++,
                                                    type = towerType,
                                                    pos = slot,
                                                    level = 1
                                                )
                                            )
                                            SilkRoadSoundEngine.playCamelBell()
                                            combatToast = "«${towerType.titleFa}» ساخته شد! می‌توانید در حین نبرد آن را به پایگاه‌های جلوتر بکشید."
                                        } else {
                                            combatToast = "سکه کافی نیست! به ${towerType.baseCost} سکه نیاز دارید."
                                        }
                                    }
                            ) {
                                Column(
                                    modifier = Modifier
                                        .width(125.dp)
                                        .padding(8.dp)
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(12.dp)
                                                .clip(CircleShape)
                                                .background(towerType.primaryColor)
                                        )
                                        Text(
                                            text = "${towerType.baseCost} سکه",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (canAfford) BrightAmber else Color.Gray
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(3.dp))
                                    Text(
                                        text = towerType.shortTitleFa,
                                        style = MaterialTheme.typography.labelLarge,
                                        color = Color.White
                                    )
                                    Text(
                                        text = towerType.roleFa,
                                        fontSize = 10.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                            }
                        }
                    }
                } else {
                    // Selected Tower Actions: Portable Relocation + Level 2 Upgrade + Level 3 Evolution Branches!
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.padding(end = 6.dp)) {
                            Text(
                                text = "${selectedTower.type.titleFa} (سطح ${selectedTower.level})",
                                style = MaterialTheme.typography.labelLarge,
                                color = selectedTower.type.primaryColor
                            )
                            Text(
                                text = if (selectedTower.evolution != TowerEvolution.NONE) {
                                    "تکامل: ${selectedTower.evolution.titleFa}"
                                } else {
                                    "قدرت: ${selectedTower.effectiveDamage(warDrumRemaining > 0f).toInt()} · جابه‌جایی: ${selectedTower.totalRelocations} بار"
                                },
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        // Portable Tower Relocate Action (The Signature Twist!)
                        Button(
                            onClick = {
                                isRelocateModeActive = !isRelocateModeActive
                                combatToast = if (isRelocateModeActive) {
                                    "حالت جابه‌جایی فعال شد: حالا روی هر پایگاه خالی (مربع فیروزه‌ای روشن) بزنید تا برج به آنجا منتقل شود!"
                                } else {
                                    "حالت جابه‌جایی لغو شد."
                                }
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isRelocateModeActive) OasisCyan else PersianTurquoise,
                                contentColor = Color(0xFF061E1B)
                            ),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.testTag("relocate_tower_button")
                        ) {
                            Icon(Icons.Default.OpenWith, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (isRelocateModeActive) "انتخاب مقصد..." else "جابه‌جایی برج",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        // Upgrade to Level 2
                        if (selectedTower.level == 1) {
                            val upgCost = selectedTower.upgradeCost()
                            FilledTonalButton(
                                onClick = {
                                    if (gold >= upgCost) {
                                        gold -= upgCost
                                        val idx = towers.indexOfFirst { it.id == selectedTower.id }
                                        if (idx != -1) {
                                            towers[idx] = selectedTower.copy(level = 2)
                                            SilkRoadSoundEngine.playGoldChime()
                                            combatToast = "برج به سطح ۲ ارتقا یافت! در سطح بعد می‌توانید شاخهٔ تکامل تخصصی انتخاب کنید."
                                        }
                                    } else {
                                        combatToast = "برای ارتقا به $upgCost سکه نیاز دارید."
                                    }
                                },
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.testTag("upgrade_tower_lv2_button")
                            ) {
                                Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(15.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("ارتقا سطح ۲ ($upgCost سکه)", fontSize = 12.sp)
                            }
                        }

                        // Evolution to Level 3 (2 Distinct Tactical Branches per Tower!)
                        if (selectedTower.level == 2) {
                            val evoCost = selectedTower.upgradeCost()
                            selectedTower.type.evolutions.forEach { evo ->
                                FilledTonalButton(
                                    onClick = {
                                        if (gold >= evoCost) {
                                            gold -= evoCost
                                            val idx = towers.indexOfFirst { it.id == selectedTower.id }
                                            if (idx != -1) {
                                                towers[idx] = selectedTower.copy(level = 3, evolution = evo)
                                                SilkRoadSoundEngine.playGoldChime()
                                                combatToast = "تکامل برج به «${evo.titleFa}» (${evo.specialEffectFa}) کامل شد!"
                                            }
                                        } else {
                                            combatToast = "برای تکامل به $evoCost سکه نیاز دارید."
                                        }
                                    },
                                    shape = RoundedCornerShape(10.dp)
                                ) {
                                    Text("تکامل: ${evo.titleFa} ($evoCost سکه)", fontSize = 11.sp)
                                }
                            }
                        }

                        // Sell / Dismantle Tower
                        OutlinedButton(
                            onClick = {
                                val refund = (selectedTower.type.baseCost * 0.7f * selectedTower.level).toInt()
                                gold += refund
                                towers.removeAll { it.id == selectedTower.id }
                                isRelocateModeActive = false
                                combatToast = "برج جمع‌آوری شد (+$refund سکه بازگشت)."
                            },
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(Icons.Default.Delete, contentDescription = "فروش برج", modifier = Modifier.size(15.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("جمع‌آوری", fontSize = 11.sp)
                        }
                    }
                }
            }
        }
    }

    // Victory / City Arrival & Caravanserai Merchant Modal
    AnimatedVisibility(visible = phase == BattlePhase.CITY_VICTORY) {
        val stars = when {
            caravanHp >= maxHpWithMeta * 0.8f -> 3
            caravanHp >= maxHpWithMeta * 0.45f -> 2
            else -> 1
        }
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.82f))
                .padding(20.dp),
            contentAlignment = Alignment.Center
        ) {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF241811)),
                shape = RoundedCornerShape(22.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(2.dp, SaffronGold, RoundedCornerShape(22.dp))
            ) {
                Column(
                    modifier = Modifier
                        .padding(20.dp)
                        .verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "ورود پیروزمندانه به ${level.destinationCity}!",
                        style = MaterialTheme.typography.headlineMedium,
                        color = SaffronGold
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        repeat(3) { idx ->
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = null,
                                tint = if (idx < stars) BrightAmber else Color.DarkGray,
                                modifier = Modifier.size(30.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = level.merchantOfferText,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    Surface(
                        color = Color(0xFF19110C),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            Text("پاداش ابریشم: +${level.rewardSilk}", color = OasisCyan, fontWeight = FontWeight.Bold)
                            Text("پاداش زعفران: +${level.rewardSaffron}", color = CrimsonSilk, fontWeight = FontWeight.Bold)
                            Text("سکه: +${gold / 2}", color = BrightAmber, fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Interactive City Merchant Choice
                    if (!merchantChoiceApplied) {
                        Text(
                            text = "انتخاب معامله در بازار ${level.destinationCity}:",
                            style = MaterialTheme.typography.labelLarge,
                            color = BrightAmber
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            FilledTonalButton(
                                onClick = {
                                    merchantChoiceApplied = true
                                    merchantBonusText = "معامله با صراف شهر: +۹۰ سکهٔ طلای اضافی به خزانهٔ کاروان افزوده شد!"
                                    onVictoryRecorded(stars, 90, 0, 0, level.waves.size)
                                },
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("معاملهٔ طلا (+۹۰ سکه)", fontSize = 11.sp)
                            }
                            FilledTonalButton(
                                onClick = {
                                    merchantChoiceApplied = true
                                    merchantBonusText = "معامله با تاجر ابریشم: +۱۵ ابریشم و +۱۲ زعفران ممتاز دریافت شد!"
                                    onVictoryRecorded(stars, 0, 15, 12, level.waves.size)
                                },
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("بار ابریشم و زعفران", fontSize = 11.sp)
                            }
                        }
                    } else if (merchantBonusText != null) {
                        Text(
                            text = merchantBonusText!!,
                            color = OasisCyan,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedButton(
                            onClick = onBackToMap,
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("نقشهٔ جادهٔ ابریشم")
                        }
                        if (!level.isCustom && level.levelNumber in 1..11) {
                            Button(
                                onClick = onNextCampaignLevel,
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = SaffronGold,
                                    contentColor = Color.Black
                                ),
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("شهر بعدی", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }

    // Caravan Defeat Modal (with Instant Revive option as mentioned in game design doc)
    AnimatedVisibility(visible = phase == BattlePhase.CARAVAN_DEFEAT) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.85f))
                .padding(20.dp),
            contentAlignment = Alignment.Center
        ) {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF2B1414)),
                shape = RoundedCornerShape(22.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(2.dp, CrimsonSilk, RoundedCornerShape(22.dp))
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "کاروان در محاصره شکست خورد!",
                        style = MaterialTheme.typography.headlineMedium,
                        color = CrimsonSilk
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "راهزنان به بار ابریشم و زعفران رسیدند. می‌توانید کاروان را در همین موج احیا کنید یا دوباره از اردوگاه شبانه تلاش کنید.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Button(
                            onClick = {
                                caravanHp = maxHpWithMeta
                                relocateCharges = maxRelocateWithMeta
                                enemies.clear()
                                gold += 100
                                phase = BattlePhase.DAY_MARCH
                                SilkRoadSoundEngine.playWarDrum()
                                combatToast = "کاروان با نیروی کمکی احیا شد (+۱۰۰ سکه و جان کامل)!"
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = SaffronGold,
                                contentColor = Color.Black
                            ),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("احیای فوری کاروان", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                        OutlinedButton(
                            onClick = onBackToMap,
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("بازگشت به نقشه")
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun HeroSkillChip(
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    accentColor: Color,
    enabled: Boolean,
    testTag: String,
    onClick: () -> Unit
) {
    Surface(
        color = if (enabled) accentColor.copy(alpha = 0.20f) else Color(0xFF1A1410),
        shape = RoundedCornerShape(10.dp),
        modifier = Modifier
            .border(
                width = 1.dp,
                color = if (enabled) accentColor else Color(0xFF3E2D23),
                shape = RoundedCornerShape(10.dp)
            )
            .testTag(testTag)
            .clickable(enabled = enabled, onClick = onClick)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = if (enabled) accentColor else Color.Gray,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Column {
                Text(
                    text = title,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (enabled) Color.White else Color.Gray
                )
                Text(
                    text = subtitle,
                    fontSize = 9.sp,
                    color = if (enabled) accentColor else Color.DarkGray
                )
            }
        }
    }
}

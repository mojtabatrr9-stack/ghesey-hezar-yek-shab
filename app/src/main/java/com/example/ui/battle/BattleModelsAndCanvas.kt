package com.example.ui.battle

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import com.example.data.model.DecorationType
import com.example.data.model.EnemyType
import com.example.data.model.GridPos
import com.example.data.model.LevelBlueprint
import com.example.data.model.TowerEvolution
import com.example.data.model.TowerType
import kotlin.math.cos
import kotlin.math.sin

enum class BattlePhase {
    NIGHT_CAMP,
    DAY_MARCH,
    CITY_VICTORY,
    CARAVAN_DEFEAT
}

data class PlacedTower(
    val id: Long,
    val type: TowerType,
    val pos: GridPos,
    val level: Int = 1,
    val evolution: TowerEvolution = TowerEvolution.NONE,
    val cooldownRemaining: Float = 0f,
    val relocateBoostRemaining: Float = 0f,
    val totalRelocations: Int = 0
) {
    fun effectiveDamage(warDrumActive: Boolean): Float {
        val lvMult = when (level) {
            1 -> 1.0f
            2 -> 1.45f
            else -> 1.95f * evolution.bonusDamageMultiplier
        }
        val drumMult = if (warDrumActive) 1.25f else 1.0f
        return type.baseDamage * lvMult * drumMult
    }

    fun effectiveFireInterval(warDrumActive: Boolean, nearbyFireTemple: Boolean): Float {
        val lvSpeed = when (level) {
            1 -> 1.0f
            2 -> 1.2f
            else -> 1.35f * evolution.bonusSpeedMultiplier
        }
        val relocateMult = if (relocateBoostRemaining > 0f) 1.45f else 1.0f
        val drumMult = if (warDrumActive) 1.6f else 1.0f
        val templeMult = if (nearbyFireTemple) 1.35f else 1.0f
        return (type.fireIntervalSec / (lvSpeed * relocateMult * drumMult * templeMult)).coerceAtLeast(0.18f)
    }

    fun effectiveRange(): Float {
        val lvBonus = (level - 1) * 0.25f + evolution.bonusRange
        return type.rangeCells + lvBonus
    }

    fun upgradeCost(): Int = (type.baseCost * 0.75f * level).toInt()
}

data class ActiveEnemy(
    val id: Long,
    val type: EnemyType,
    val x: Float,
    val y: Float,
    val hp: Float,
    val maxHp: Float,
    val slowTimer: Float = 0f,
    val burnTimer: Float = 0f,
    val poisonTimer: Float = 0f,
    val revealedTimer: Float = 0f,
    val targetPathIndex: Int = 0
) {
    val isCurrentlyStealthed: Boolean
        get() = type.isStealthUnderSand && revealedTimer <= 0f
}

data class ActiveProjectile(
    val id: Long,
    val fromX: Float,
    val fromY: Float,
    val toX: Float,
    val toY: Float,
    val progress: Float,
    val color: Color,
    val isSplash: Boolean = false
)

data class RelocationTrailEffect(
    val fromPos: GridPos,
    val toPos: GridPos,
    val alpha: Float,
    val color: Color
)

data class FloatingBattleText(
    val id: Long,
    val text: String,
    val col: Float,
    val row: Float,
    val color: Color,
    val lifeRemaining: Float = 1.0f
)

@Composable
fun BattleTacticalCanvas(
    level: LevelBlueprint,
    phase: BattlePhase,
    towers: List<PlacedTower>,
    enemies: List<ActiveEnemy>,
    projectiles: List<ActiveProjectile>,
    relocationTrails: List<RelocationTrailEffect>,
    floatingTexts: List<FloatingBattleText>,
    caravanProgressIndex: Float,
    selectedSlot: GridPos?,
    isRelocateModeActive: Boolean,
    desertMistRemaining: Float,
    warDrumRemaining: Float,
    eagleAnimRemaining: Float,
    onCellTapped: (GridPos) -> Unit,
    onTowerDraggedToSlot: (fromPos: GridPos, toPos: GridPos) -> Unit,
    modifier: Modifier = Modifier
) {
    var dragStartGrid by remember { mutableStateOf<GridPos?>(null) }
    var dragCurrentPixel by remember { mutableStateOf<Offset?>(null) }
    var canvasSize by remember { mutableStateOf(Size.Zero) }

    val currentTowers by rememberUpdatedState(towers)
    val currentLevel by rememberUpdatedState(level)
    val currentOnCellTapped by rememberUpdatedState(onCellTapped)
    val currentOnTowerDragged by rememberUpdatedState(onTowerDraggedToSlot)

    Canvas(
        modifier = modifier
            .fillMaxSize()
            .testTag("battle_tactical_canvas")
            .pointerInput(level.id) {
                detectTapGestures { offset ->
                    if (canvasSize.width > 0f && canvasSize.height > 0f) {
                        val cellW = canvasSize.width / currentLevel.gridCols
                        val cellH = canvasSize.height / currentLevel.gridRows
                        // Note: Canvas coordinates inside RTL layout still have x=0 on left
                        val col = (offset.x / cellW).toInt().coerceIn(0, currentLevel.gridCols - 1)
                        val row = (offset.y / cellH).toInt().coerceIn(0, currentLevel.gridRows - 1)
                        currentOnCellTapped(GridPos(col, row))
                    }
                }
            }
            .pointerInput(level.id) {
                detectDragGestures(
                    onDragStart = { startOffset ->
                        if (canvasSize.width > 0f && canvasSize.height > 0f) {
                            val cellW = canvasSize.width / currentLevel.gridCols
                            val cellH = canvasSize.height / currentLevel.gridRows
                            val col = (startOffset.x / cellW).toInt().coerceIn(0, currentLevel.gridCols - 1)
                            val row = (startOffset.y / cellH).toInt().coerceIn(0, currentLevel.gridRows - 1)
                            val gridPos = GridPos(col, row)
                            if (currentTowers.any { it.pos == gridPos }) {
                                dragStartGrid = gridPos
                                dragCurrentPixel = startOffset
                            }
                        }
                    },
                    onDrag = { change, _ ->
                        if (dragStartGrid != null) {
                            change.consume()
                            dragCurrentPixel = change.position
                        }
                    },
                    onDragEnd = {
                        val from = dragStartGrid
                        val endPx = dragCurrentPixel
                        if (from != null && endPx != null && canvasSize.width > 0f && canvasSize.height > 0f) {
                            val cellW = canvasSize.width / currentLevel.gridCols
                            val cellH = canvasSize.height / currentLevel.gridRows
                            val col = (endPx.x / cellW).toInt().coerceIn(0, currentLevel.gridCols - 1)
                            val row = (endPx.y / cellH).toInt().coerceIn(0, currentLevel.gridRows - 1)
                            val target = GridPos(col, row)
                            if (target != from) {
                                currentOnTowerDragged(from, target)
                            }
                        }
                        dragStartGrid = null
                        dragCurrentPixel = null
                    },
                    onDragCancel = {
                        dragStartGrid = null
                        dragCurrentPixel = null
                    }
                )
            }
    ) {
        canvasSize = size
        val cols = level.gridCols
        val rows = level.gridRows
        val cellW = size.width / cols
        val cellH = size.height / rows
        val isNight = phase == BattlePhase.NIGHT_CAMP

        // 1. Draw Biome Terrain Grid
        for (r in 0 until rows) {
            for (c in 0 until cols) {
                val isEven = (r + c) % 2 == 0
                val tileColor = if (isNight) {
                    if (isEven) level.biome.nightTilePrimary else level.biome.nightTileSecondary
                } else {
                    if (isEven) level.biome.dayTilePrimary else level.biome.dayTileSecondary
                }
                drawRect(
                    color = tileColor,
                    topLeft = Offset(c * cellW, r * cellH),
                    size = Size(cellW + 1f, cellH + 1f)
                )
            }
        }

        // 2. Draw Decorations (Oasis pools, Palms, Rocks, Caravanserai ruins)
        level.decorations.forEach { (pos, dec) ->
            val cx = (pos.col + 0.5f) * cellW
            val cy = (pos.row + 0.5f) * cellH
            val radius = minOf(cellW, cellH) * 0.36f
            when (dec) {
                DecorationType.OASIS_POOL -> {
                    drawCircle(
                        color = Color(0xFF1D84B5),
                        radius = radius * 1.1f,
                        center = Offset(cx, cy)
                    )
                    drawCircle(
                        color = Color(0xFF80DEEA).copy(alpha = 0.6f),
                        radius = radius * 0.65f,
                        center = Offset(cx - radius * 0.15f, cy - radius * 0.15f)
                    )
                }
                DecorationType.PALM_TREES -> {
                    drawCircle(
                        color = Color(0xFF2E7D32),
                        radius = radius,
                        center = Offset(cx, cy)
                    )
                    drawCircle(
                        color = Color(0xFF66BB6A),
                        radius = radius * 0.55f,
                        center = Offset(cx, cy - radius * 0.15f)
                    )
                }
                DecorationType.CANYON_ROCK -> {
                    drawRoundRect(
                        color = Color(0xFF5D4037),
                        topLeft = Offset(cx - radius, cy - radius * 0.8f),
                        size = Size(radius * 2f, radius * 1.6f),
                        cornerRadius = CornerRadius(8f, 8f)
                    )
                }
                DecorationType.CARAVANSERAI_RUIN, DecorationType.ANCIENT_STATUE -> {
                    drawRoundRect(
                        color = dec.color,
                        topLeft = Offset(cx - radius * 0.9f, cy - radius * 0.9f),
                        size = Size(radius * 1.8f, radius * 1.8f),
                        cornerRadius = CornerRadius(6f, 6f)
                    )
                    drawCircle(
                        color = Color(0xFF3E2723),
                        radius = radius * 0.45f,
                        center = Offset(cx, cy)
                    )
                }
            }
        }

        // 3. Draw Silk Road Caravan Path
        if (level.caravanPath.isNotEmpty()) {
            val roadPath = Path()
            level.caravanPath.forEachIndexed { idx, pos ->
                val px = (pos.col + 0.5f) * cellW
                val py = (pos.row + 0.5f) * cellH
                if (idx == 0) roadPath.moveTo(px, py) else roadPath.lineTo(px, py)
            }
            // Outer road border
            drawPath(
                path = roadPath,
                color = Color(0xFF3E2412).copy(alpha = 0.85f),
                style = Stroke(width = minOf(cellW, cellH) * 0.68f, cap = StrokeCap.Round)
            )
            // Inner sandy silk road
            drawPath(
                path = roadPath,
                color = level.biome.roadColor,
                style = Stroke(width = minOf(cellW, cellH) * 0.50f, cap = StrokeCap.Round)
            )
            // Dashed caravan footprints line
            drawPath(
                path = roadPath,
                color = Color(0xFFF4A261).copy(alpha = 0.55f),
                style = Stroke(
                    width = 3.5f,
                    cap = StrokeCap.Round,
                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(12f, 10f), 0f)
                )
            )

            // Draw Origin Gate & Destination City Gate
            val startPos = level.caravanPath.first()
            val endPos = level.caravanPath.last()
            drawCircle(
                color = Color(0xFF2A9D8F),
                radius = minOf(cellW, cellH) * 0.36f,
                center = Offset((startPos.col + 0.5f) * cellW, (startPos.row + 0.5f) * cellH)
            )
            // Destination City Archway (Golden Dome)
            val endCx = (endPos.col + 0.5f) * cellW
            val endCy = (endPos.row + 0.5f) * cellH
            drawCircle(
                color = Color(0xFFF4A261),
                radius = minOf(cellW, cellH) * 0.44f,
                center = Offset(endCx, endCy)
            )
            drawCircle(
                color = Color(0xFF1B1108),
                radius = minOf(cellW, cellH) * 0.22f,
                center = Offset(endCx, endCy)
            )
        }

        // 4. Draw Enemy Ambush Portals (Red Dunes)
        level.enemySpawns.forEach { spawn ->
            val sx = (spawn.col + 0.5f) * cellW
            val sy = (spawn.row + 0.5f) * cellH
            val r = minOf(cellW, cellH) * 0.40f
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(Color(0xFFFF5252), Color(0xFF7F0000), Color.Transparent),
                    center = Offset(sx, sy),
                    radius = r * 1.3f
                ),
                radius = r * 1.3f,
                center = Offset(sx, sy)
            )
            drawCircle(
                color = Color(0xFFD62828),
                radius = r * 0.65f,
                center = Offset(sx, sy),
                style = Stroke(width = 3.5f)
            )
        }

        // 5. Draw Tactical Tower Slots (Highlight empty slots when relocating or dragging!)
        val highlightEmptySlots = isRelocateModeActive || dragStartGrid != null
        level.towerSlots.forEach { slot ->
            val sx = (slot.col + 0.5f) * cellW
            val sy = (slot.row + 0.5f) * cellH
            val r = minOf(cellW, cellH) * 0.38f
            val isOccupied = towers.any { it.pos == slot }
            val isSelected = selectedSlot == slot

            val slotColor = when {
                isSelected -> Color(0xFFFFEE58)
                highlightEmptySlots && !isOccupied -> Color(0xFF48CAE4)
                else -> Color(0xFF2A9D8F).copy(alpha = 0.75f)
            }

            drawRoundRect(
                color = Color(0xFF1D140E).copy(alpha = 0.75f),
                topLeft = Offset(sx - r, sy - r),
                size = Size(r * 2f, r * 2f),
                cornerRadius = CornerRadius(10f, 10f)
            )
            drawRoundRect(
                color = slotColor,
                topLeft = Offset(sx - r, sy - r),
                size = Size(r * 2f, r * 2f),
                cornerRadius = CornerRadius(10f, 10f),
                style = Stroke(width = if (isSelected || (highlightEmptySlots && !isOccupied)) 4.5f else 2.2f)
            )
        }

        // 6. Draw Relocation Sand Trails (Signature Twist Visual Feedback!)
        relocationTrails.forEach { trail ->
            val fx = (trail.fromPos.col + 0.5f) * cellW
            val fy = (trail.fromPos.row + 0.5f) * cellH
            val tx = (trail.toPos.col + 0.5f) * cellW
            val ty = (trail.toPos.row + 0.5f) * cellH
            drawLine(
                color = trail.color.copy(alpha = trail.alpha),
                start = Offset(fx, fy),
                end = Offset(tx, ty),
                strokeWidth = 10f * trail.alpha,
                cap = StrokeCap.Round
            )
        }

        // 7. Draw Selected Tower Range Ring
        if (selectedSlot != null) {
            val tower = towers.find { it.pos == selectedSlot }
            if (tower != null) {
                val tx = (tower.pos.col + 0.5f) * cellW
                val ty = (tower.pos.row + 0.5f) * cellH
                val rangePx = tower.effectiveRange() * minOf(cellW, cellH)
                drawCircle(
                    color = tower.type.primaryColor.copy(alpha = 0.16f),
                    radius = rangePx,
                    center = Offset(tx, ty)
                )
                drawCircle(
                    color = tower.type.primaryColor.copy(alpha = 0.75f),
                    radius = rangePx,
                    center = Offset(tx, ty),
                    style = Stroke(
                        width = 2.5f,
                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 8f), 0f)
                    )
                )
            }
        }

        // 8. Draw Placed Portable Towers
        towers.forEach { tower ->
            val tx = (tower.pos.col + 0.5f) * cellW
            val ty = (tower.pos.row + 0.5f) * cellH
            val r = minOf(cellW, cellH) * 0.34f

            // If tower has relocation boost or war drum active, draw glowing aura
            if (tower.relocateBoostRemaining > 0f || warDrumRemaining > 0f) {
                drawCircle(
                    color = Color(0xFFFFD54F).copy(alpha = 0.40f),
                    radius = r * 1.45f,
                    center = Offset(tx, ty)
                )
            }

            // Wooden wheeled base (Portable Tower signature look!)
            drawCircle(
                color = tower.type.secondaryColor,
                radius = r,
                center = Offset(tx, ty)
            )
            // Tower turret core
            val coreColor = if (tower.evolution != TowerEvolution.NONE) {
                tower.evolution.accentColor
            } else {
                tower.type.primaryColor
            }
            drawCircle(
                color = coreColor,
                radius = r * 0.72f,
                center = Offset(tx, ty)
            )

            // Level stars / chevrons at top of tower
            for (lv in 0 until tower.level) {
                val starOffset = (lv - (tower.level - 1) / 2f) * (r * 0.55f)
                drawCircle(
                    color = Color.White,
                    radius = 3.8f,
                    center = Offset(tx + starOffset, ty - r * 0.85f)
                )
            }
        }

        // 9. Draw The Moving Silk Road Caravan (Lead Camel + Silk Wagon + Guard Camel)
        if (level.caravanPath.isNotEmpty()) {
            fun samplePathPosition(indexFloat: Float): Offset {
                val clamped = indexFloat.coerceIn(0f, (level.caravanPath.size - 1).toFloat())
                val i0 = clamped.toInt()
                val i1 = (i0 + 1).coerceAtMost(level.caravanPath.size - 1)
                val frac = clamped - i0
                val p0 = level.caravanPath[i0]
                val p1 = level.caravanPath[i1]
                val col = p0.col + (p1.col - p0.col) * frac
                val row = p0.row + (p1.row - p0.row) * frac
                return Offset((col + 0.5f) * cellW, (row + 0.5f) * cellH)
            }

            val caravanUnits = listOf(
                Triple(caravanProgressIndex, Color(0xFFFFD54F), 0.36f),          // Lead Camel & Hero Banner
                Triple((caravanProgressIndex - 0.55f).coerceAtLeast(0f), Color(0xFFE76F51), 0.33f), // Silk & Saffron Wagon
                Triple((caravanProgressIndex - 1.10f).coerceAtLeast(0f), Color(0xFF48CAE4), 0.30f)  // Guard Camel
            )

            caravanUnits.forEach { (idx, color, sizeFactor) ->
                val center = samplePathPosition(idx)
                val r = minOf(cellW, cellH) * sizeFactor
                if (desertMistRemaining > 0f) {
                    drawCircle(
                        color = Color(0xFF80DEEA).copy(alpha = 0.38f),
                        radius = r * 1.6f,
                        center = center
                    )
                }
                drawRoundRect(
                    color = Color(0xFF2B190E),
                    topLeft = Offset(center.x - r, center.y - r * 0.8f),
                    size = Size(r * 2f, r * 1.6f),
                    cornerRadius = CornerRadius(12f, 12f)
                )
                drawRoundRect(
                    color = color,
                    topLeft = Offset(center.x - r * 0.8f, center.y - r * 0.6f),
                    size = Size(r * 1.6f, r * 1.2f),
                    cornerRadius = CornerRadius(10f, 10f)
                )
            }
        }

        // 10. Draw Active Enemies + Health Bars
        enemies.forEach { enemy ->
            val ex = (enemy.x + 0.5f) * cellW
            val ey = (enemy.y + 0.5f) * cellH
            val baseR = minOf(cellW, cellH) * (if (enemy.type.isBoss) 0.44f else 0.28f)

            if (enemy.isCurrentlyStealthed) {
                // Draw subtle sand ripple for stealthed tunnel saboteur
                drawCircle(
                    color = Color(0xFFBA68C8).copy(alpha = 0.35f),
                    radius = baseR * 0.9f,
                    center = Offset(ex, ey),
                    style = Stroke(width = 2.5f, pathEffect = PathEffect.dashPathEffect(floatArrayOf(6f, 6f), 0f))
                )
            } else {
                if (enemy.type.isSpeedBuffer) {
                    drawCircle(
                        color = Color(0xFF4DD0E1).copy(alpha = 0.22f),
                        radius = baseR * 2.2f,
                        center = Offset(ex, ey)
                    )
                }
                drawCircle(
                    color = Color(0xFF1A0F0A),
                    radius = baseR * 1.12f,
                    center = Offset(ex, ey)
                )
                drawCircle(
                    color = enemy.type.badgeColor,
                    radius = baseR,
                    center = Offset(ex, ey)
                )
                if (enemy.slowTimer > 0f) {
                    drawCircle(
                        color = Color(0xFF80DEEA).copy(alpha = 0.65f),
                        radius = baseR * 1.15f,
                        center = Offset(ex, ey),
                        style = Stroke(width = 3f)
                    )
                }
                // Health bar
                val barW = baseR * 2.2f
                val barH = if (enemy.type.isBoss) 7f else 4.5f
                val hpRatio = (enemy.hp / enemy.maxHp).coerceIn(0f, 1f)
                drawRect(
                    color = Color(0xFF2B1212),
                    topLeft = Offset(ex - barW / 2f, ey - baseR - 10f),
                    size = Size(barW, barH)
                )
                drawRect(
                    color = if (enemy.type.isBoss) Color(0xFFFF5252) else Color(0xFF66BB6A),
                    topLeft = Offset(ex - barW / 2f, ey - baseR - 10f),
                    size = Size(barW * hpRatio, barH)
                )
            }
        }

        // 11. Draw Projectiles
        projectiles.forEach { proj ->
            val px = (proj.fromX + (proj.toX - proj.fromX) * proj.progress + 0.5f) * cellW
            val py = (proj.fromY + (proj.toY - proj.fromY) * proj.progress + 0.5f) * cellH
            drawCircle(
                color = proj.color,
                radius = if (proj.isSplash) 7.5f else 4.8f,
                center = Offset(px, py)
            )
        }

        // 12. Draw Active Hero Weather Overlay (Desert Mist / Scout Eagle)
        if (desertMistRemaining > 0f) {
            drawRect(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF48CAE4).copy(alpha = 0.18f),
                        Color(0xFF2A9D8F).copy(alpha = 0.25f),
                        Color(0xFF48CAE4).copy(alpha = 0.15f)
                    )
                ),
                size = size
            )
        }

        if (eagleAnimRemaining > 0f) {
            val progress = 1f - (eagleAnimRemaining / 2.0f).coerceIn(0f, 1f)
            val eagleX = size.width * progress
            val eagleY = size.height * (0.35f + 0.15f * sin(progress * 6f))
            drawCircle(
                color = Color(0xFFFFD54F).copy(alpha = 0.85f),
                radius = 22f,
                center = Offset(eagleX, eagleY)
            )
            drawCircle(
                color = Color(0xFFFFF59D).copy(alpha = 0.35f),
                radius = 48f,
                center = Offset(eagleX, eagleY),
                style = Stroke(width = 3f)
            )
        }

        // 13. Draw Dragged Tower Preview Line (When dragging a Portable Tower mid-battle!)
        val fromGrid = dragStartGrid
        val curPx = dragCurrentPixel
        if (fromGrid != null && curPx != null) {
            val startOffset = Offset((fromGrid.col + 0.5f) * cellW, (fromGrid.row + 0.5f) * cellH)
            drawLine(
                color = Color(0xFF48CAE4),
                start = startOffset,
                end = curPx,
                strokeWidth = 6f,
                cap = StrokeCap.Round,
                pathEffect = PathEffect.dashPathEffect(floatArrayOf(14f, 10f), 0f)
            )
            drawCircle(
                color = Color(0xFF48CAE4).copy(alpha = 0.55f),
                radius = minOf(cellW, cellH) * 0.42f,
                center = curPx
            )
        }
    }
}

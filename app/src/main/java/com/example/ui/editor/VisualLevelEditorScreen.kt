package com.example.ui.editor

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoFixHigh
import androidx.compose.material.icons.filled.Backspace
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.EditRoad
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material.icons.filled.Landscape
import androidx.compose.material.icons.filled.Park
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.boundsInWindow
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.audio.SilkRoadSoundEngine
import com.example.data.campaign.CampaignLevels
import com.example.data.model.BiomeType
import com.example.data.model.DecorationType
import com.example.data.model.EnemyType
import com.example.data.model.GridPos
import com.example.data.model.LevelBlueprint
import com.example.data.model.WaveEntry
import com.example.data.model.WaveSpec
import com.example.ui.CaravanViewModel
import com.example.ui.theme.BrightAmber
import com.example.ui.theme.CrimsonSilk
import com.example.ui.theme.OasisCyan
import com.example.ui.theme.PersianTurquoise
import com.example.ui.theme.SaffronGold

enum class EditorBrushTool(val titleFa: String, val hintFa: String) {
    PATH_DRAG("مسیر کاروان (کشیدن)", "انگشت خود را روی شبکه بکشید تا جادهٔ حرکت کاروان ترسیم شود"),
    TOWER_SLOT("پایگاه برج", "روی خانه‌های کنار جاده بزنید یا بکشید تا جایگاه برج‌های قابل‌حمل مشخص شود"),
    ENEMY_SPAWN("دروازهٔ دشمن", "محل کمینگاه و ورود راهزنان را روی نقشه قرار دهید یا دشمن را از پایین بکشید و رها کنید"),
    DECORATION("تزیینات واحه", "برکهٔ واحه، نخلستان، صخره یا کاروانسرا به نقشه اضافه کنید"),
    ERASER("پاک‌کن", "روی هر خانه بزنید یا بکشید تا مسیر، پایگاه یا کمینگاه آن پاک شود")
}

@Composable
fun VisualLevelEditorScreen(
    draft: LevelBlueprint,
    savedCustomLevels: List<LevelBlueprint>,
    onUpdateDraft: (LevelBlueprint) -> Unit,
    onSaveLevel: (LevelBlueprint, Boolean) -> Unit,
    onDeleteCustomLevel: (Long) -> Unit,
    onBack: () -> Unit
) {
    BackHandler {
        onBack()
    }

    var selectedTool by remember { mutableStateOf(EditorBrushTool.PATH_DRAG) }
    var selectedDecoration by remember { mutableStateOf(DecorationType.OASIS_POOL) }
    var selectedWaveIdx by remember { mutableIntStateOf(0) }
    var editorHint by remember {
        mutableStateOf("انگشت خود را روی شبکه بکشید تا مسیر بسازید، یا آیکون دشمنان را بکشید و مستقیم روی نقشه رها کنید!")
    }

    // State for Cross-Component Drag & Drop of Enemies onto the Grid Canvas!
    var canvasWindowBounds by remember { mutableStateOf<Rect?>(null) }
    var draggingEnemyType by remember { mutableStateOf<EnemyType?>(null) }
    var draggingEnemyWindowPos by remember { mutableStateOf<Offset?>(null) }

    var showSavedLevelsSheet by remember { mutableStateOf(false) }
    var showJsonDialog by remember { mutableStateOf(false) }
    var jsonInputText by remember { mutableStateOf("") }

    val currentDraft by rememberUpdatedState(draft)
    val safeWaveIdx = selectedWaveIdx.coerceIn(0, (draft.waves.size - 1).coerceAtLeast(0))
    val activeWave = draft.waves.getOrNull(safeWaveIdx)

    fun applyCellAction(pos: GridPos, isDraggingStroke: Boolean) {
        val d = currentDraft
        when (selectedTool) {
            EditorBrushTool.PATH_DRAG -> {
                val existingIdx = d.caravanPath.indexOf(pos)
                val newPath = if (existingIdx == -1) {
                    val last = d.caravanPath.lastOrNull()
                    if (last == null) {
                        listOf(pos)
                    } else {
                        // Interpolate intermediate cells if user drags fast
                        val interpolated = mutableListOf<GridPos>()
                        var cx = last.col
                        var cy = last.row
                        while (cx != pos.col || cy != pos.row) {
                            if (cx < pos.col) cx++ else if (cx > pos.col) cx--
                            else if (cy < pos.row) cy++ else if (cy > pos.row) cy--
                            val stepPos = GridPos(cx, cy)
                            if (stepPos !in d.caravanPath && stepPos !in interpolated) {
                                interpolated.add(stepPos)
                            }
                        }
                        d.caravanPath + interpolated
                    }
                } else if (!isDraggingStroke && existingIdx == d.caravanPath.lastIndex && d.caravanPath.size > 2) {
                    d.caravanPath.dropLast(1)
                } else {
                    d.caravanPath
                }
                onUpdateDraft(
                    d.copy(
                        caravanPath = newPath,
                        towerSlots = d.towerSlots - newPath.toSet()
                    )
                )
            }

            EditorBrushTool.TOWER_SLOT -> {
                if (pos in d.caravanPath) {
                    editorHint = "روی جادهٔ کاروان نمی‌توان پایگاه برج گذاشت؛ کنار جاده را انتخاب کنید."
                    return
                }
                val newSlots = if (pos in d.towerSlots) {
                    if (isDraggingStroke) d.towerSlots else d.towerSlots - pos
                } else {
                    d.towerSlots + pos
                }
                onUpdateDraft(d.copy(towerSlots = newSlots.distinct()))
            }

            EditorBrushTool.ENEMY_SPAWN -> {
                val newSpawns = if (pos in d.enemySpawns) {
                    if (isDraggingStroke || d.enemySpawns.size <= 1) d.enemySpawns else d.enemySpawns - pos
                } else {
                    d.enemySpawns + pos
                }
                onUpdateDraft(d.copy(enemySpawns = newSpawns.distinct()))
            }

            EditorBrushTool.DECORATION -> {
                if (pos in d.caravanPath || pos in d.towerSlots) return
                val mutableDec = d.decorations.toMutableMap()
                if (!isDraggingStroke && mutableDec[pos] == selectedDecoration) {
                    mutableDec.remove(pos)
                } else {
                    mutableDec[pos] = selectedDecoration
                }
                onUpdateDraft(d.copy(decorations = mutableDec))
            }

            EditorBrushTool.ERASER -> {
                val mutableDec = d.decorations.toMutableMap().apply { remove(pos) }
                val newPath = if (d.caravanPath.size > 2) d.caravanPath - pos else d.caravanPath
                val newSlots = d.towerSlots - pos
                val newSpawns = if (d.enemySpawns.size > 1) d.enemySpawns - pos else d.enemySpawns
                onUpdateDraft(
                    d.copy(
                        caravanPath = newPath,
                        towerSlots = newSlots,
                        enemySpawns = newSpawns,
                        decorations = mutableDec
                    )
                )
            }
        }
    }

    fun dropEnemyOntoGridCell(enemyType: EnemyType, targetCell: GridPos) {
        val d = currentDraft
        val updatedSpawns = if (targetCell in d.enemySpawns) {
            d.enemySpawns
        } else {
            d.enemySpawns + targetCell
        }
        val spawnIdx = updatedSpawns.indexOf(targetCell).coerceAtLeast(0)

        val wavesList = d.waves.toMutableList()
        if (wavesList.isEmpty()) {
            wavesList.add(WaveSpec(1, "موج ۱", emptyList(), 60))
        }
        val wIdx = safeWaveIdx.coerceIn(0, wavesList.lastIndex)
        val currentWave = wavesList[wIdx]
        val newEntries = currentWave.entries.toMutableList()
        val existingIndex = newEntries.indexOfFirst {
            it.enemyType == enemyType && it.spawnIndex == spawnIdx
        }
        if (existingIndex != -1) {
            val old = newEntries[existingIndex]
            newEntries[existingIndex] = old.copy(count = old.count + 3)
        } else {
            newEntries.add(
                WaveEntry(
                    enemyType = enemyType,
                    count = if (enemyType.isBoss) 1 else 4,
                    spawnIntervalSec = if (enemyType.isBoss) 3.5f else 1.3f,
                    spawnIndex = spawnIdx,
                    hpMultiplier = 1.0f
                )
            )
        }
        wavesList[wIdx] = currentWave.copy(entries = newEntries)

        onUpdateDraft(
            d.copy(
                enemySpawns = updatedSpawns,
                waves = wavesList
            )
        )
        SilkRoadSoundEngine.playWarDrum()
        editorHint = "«${enemyType.titleFa}» در کمینگاه (${targetCell.col}, ${targetCell.row}) به ${currentWave.title} اضافه شد!"
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(12.dp)
    ) {
        // Top Header & Action Bar
        Card(
            colors = CardDefaults.cardColors(containerColor = Color(0xFF231811)),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "کارگاه مرحله‌ساز بصری (بدون کدنویسی)",
                            style = MaterialTheme.typography.titleLarge,
                            color = SaffronGold
                        )
                        Text(
                            text = "مسیر کاروان، پایگاه‌های برج و دشمنان را با کشیدن و رها کردن (Drag & Drop) بسازید",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        IconButton(
                            onClick = { showSavedLevelsSheet = true },
                            modifier = Modifier.testTag("editor_open_saved_levels")
                        ) {
                            Icon(Icons.Default.FolderOpen, contentDescription = "مراحل ذخیره‌شده", tint = OasisCyan)
                        }
                        IconButton(
                            onClick = {
                                jsonInputText = draft.toJsonString()
                                showJsonDialog = true
                            },
                            modifier = Modifier.testTag("editor_json_button")
                        ) {
                            Icon(Icons.Default.Code, contentDescription = "کد مرحله", tint = BrightAmber)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Primary Action Buttons: Playtest Immediately, Save Level, Smart Generate, New Clear
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = {
                            if (draft.caravanPath.size < 3 || draft.towerSlots.isEmpty()) {
                                editorHint = "لطفاً حداقل ۳ خانه برای مسیر کاروان و ۱ پایگاه برج قرار دهید!"
                            } else {
                                onSaveLevel(draft, true)
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = SaffronGold,
                            contentColor = Color.Black
                        ),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.testTag("editor_playtest_button")
                    ) {
                        Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("تست و بازی فوری مرحله!", fontWeight = FontWeight.ExtraBold)
                    }

                    FilledTonalButton(
                        onClick = {
                            onSaveLevel(draft, false)
                            editorHint = "مرحلهٔ «${draft.title}» در لیست مراحل شما ذخیره شد!"
                        },
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.testTag("editor_save_button")
                    ) {
                        Icon(Icons.Default.Save, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("ذخیرهٔ مرحله")
                    }

                    OutlinedButton(
                        onClick = {
                            val generated = CampaignLevels.generateEndlessSandstormLevel()
                            onUpdateDraft(
                                generated.copy(
                                    id = draft.id,
                                    isCustom = true,
                                    title = "درهٔ تصادفی ${(10..99).random()}",
                                    originCity = draft.originCity,
                                    destinationCity = draft.destinationCity,
                                    waves = generated.waves.take(3)
                                )
                            )
                            SilkRoadSoundEngine.playCamelBell()
                            editorHint = "یک نقشه و موج هوشمند جدید تولید شد! اکنون می‌توانید آن را ویرایش کنید."
                        },
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.testTag("editor_smart_generate_button")
                    ) {
                        Icon(Icons.Default.AutoFixHigh, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("تولید هوشمند نقشه")
                    }

                    OutlinedButton(
                        onClick = {
                            onUpdateDraft(CaravanViewModel.createFreshEditorTemplate())
                            editorHint = "قالب جدید آماده شد."
                        },
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("قالب نو")
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Tool Brush Selector Bar
        Text(
            text = "۱. انتخاب ابزار طراحی روی بوم شبکه‌ای (۱۲×۸):",
            style = MaterialTheme.typography.labelLarge,
            color = BrightAmber
        )
        Spacer(modifier = Modifier.height(6.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            EditorBrushTool.entries.forEach { tool ->
                val isSelected = selectedTool == tool
                val icon = when (tool) {
                    EditorBrushTool.PATH_DRAG -> Icons.Default.EditRoad
                    EditorBrushTool.TOWER_SLOT -> Icons.Default.Security
                    EditorBrushTool.ENEMY_SPAWN -> Icons.Default.Warning
                    EditorBrushTool.DECORATION -> Icons.Default.Park
                    EditorBrushTool.ERASER -> Icons.Default.Backspace
                }
                Surface(
                    color = if (isSelected) SaffronGold else Color(0xFF281C14),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .border(
                            width = 1.dp,
                            color = if (isSelected) BrightAmber else Color(0xFF4A3525),
                            shape = RoundedCornerShape(12.dp)
                        )
                        .testTag("editor_tool_${tool.name.lowercase()}")
                        .clickable {
                            selectedTool = tool
                            editorHint = tool.hintFa
                        }
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = tool.titleFa,
                            tint = if (isSelected) Color.Black else SaffronGold,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = tool.titleFa,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isSelected) Color.Black else Color.White
                        )
                    }
                }
            }
        }

        if (selectedTool == EditorBrushTool.DECORATION) {
            Spacer(modifier = Modifier.height(6.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                DecorationType.entries.forEach { dec ->
                    val active = selectedDecoration == dec
                    Surface(
                        color = if (active) dec.color else Color(0xFF241A14),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.clickable { selectedDecoration = dec }
                    ) {
                        Text(
                            text = dec.titleFa,
                            fontSize = 11.sp,
                            color = Color.White,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        // Live Editor Guidance Banner
        Surface(
            color = Color(0xFF1D2A28),
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = editorHint,
                    fontSize = 12.sp,
                    color = OasisCyan,
                    modifier = Modifier.weight(1f)
                )
                if (selectedTool == EditorBrushTool.PATH_DRAG && draft.caravanPath.size > 1) {
                    Text(
                        text = "پاک کردن مسیر",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = CrimsonSilk,
                        modifier = Modifier
                            .padding(start = 8.dp)
                            .clickable {
                                onUpdateDraft(draft.copy(caravanPath = listOf(GridPos(0, 4))))
                                editorHint = "مسیر پاک شد. از خانهٔ شروع انگشت خود را بکشید تا مسیر جدید رسم شود!"
                            }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Interactive 12x8 Visual Level Editor Canvas
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(265.dp)
                .clip(RoundedCornerShape(16.dp))
                .border(2.dp, SaffronGold, RoundedCornerShape(16.dp))
                .onGloballyPositioned { coords ->
                    canvasWindowBounds = coords.boundsInWindow()
                }
        ) {
            var canvasSize by remember { mutableStateOf(Size.Zero) }
            var lastDraggedCell by remember { mutableStateOf<GridPos?>(null) }

            Canvas(
                modifier = Modifier
                    .fillMaxSize()
                    .testTag("visual_level_editor_canvas")
                    .pointerInput(selectedTool, selectedDecoration) {
                        detectTapGestures { offset ->
                            if (canvasSize.width > 0f && canvasSize.height > 0f) {
                                val cellW = canvasSize.width / currentDraft.gridCols
                                val cellH = canvasSize.height / currentDraft.gridRows
                                val col = (offset.x / cellW).toInt().coerceIn(0, currentDraft.gridCols - 1)
                                val row = (offset.y / cellH).toInt().coerceIn(0, currentDraft.gridRows - 1)
                                applyCellAction(GridPos(col, row), isDraggingStroke = false)
                            }
                        }
                    }
                    .pointerInput(selectedTool, selectedDecoration) {
                        detectDragGestures(
                            onDragStart = { startOffset ->
                                if (canvasSize.width > 0f && canvasSize.height > 0f) {
                                    val cellW = canvasSize.width / currentDraft.gridCols
                                    val cellH = canvasSize.height / currentDraft.gridRows
                                    val col = (startOffset.x / cellW).toInt().coerceIn(0, currentDraft.gridCols - 1)
                                    val row = (startOffset.y / cellH).toInt().coerceIn(0, currentDraft.gridRows - 1)
                                    val pos = GridPos(col, row)
                                    lastDraggedCell = pos
                                    applyCellAction(pos, isDraggingStroke = true)
                                }
                            },
                            onDrag = { change, _ ->
                                change.consume()
                                if (canvasSize.width > 0f && canvasSize.height > 0f) {
                                    val cellW = canvasSize.width / currentDraft.gridCols
                                    val cellH = canvasSize.height / currentDraft.gridRows
                                    val col = (change.position.x / cellW).toInt().coerceIn(0, currentDraft.gridCols - 1)
                                    val row = (change.position.y / cellH).toInt().coerceIn(0, currentDraft.gridRows - 1)
                                    val pos = GridPos(col, row)
                                    if (pos != lastDraggedCell) {
                                        lastDraggedCell = pos
                                        applyCellAction(pos, isDraggingStroke = true)
                                    }
                                }
                            },
                            onDragEnd = { lastDraggedCell = null },
                            onDragCancel = { lastDraggedCell = null }
                        )
                    }
            ) {
                canvasSize = size
                val cols = draft.gridCols
                val rows = draft.gridRows
                val cellW = size.width / cols
                val cellH = size.height / rows

                // Draw Grid Cells
                for (r in 0 until rows) {
                    for (c in 0 until cols) {
                        val isEven = (r + c) % 2 == 0
                        val color = if (isEven) draft.biome.dayTilePrimary else draft.biome.dayTileSecondary
                        drawRect(
                            color = color,
                            topLeft = Offset(c * cellW, r * cellH),
                            size = Size(cellW, cellH)
                        )
                        drawRect(
                            color = Color.Black.copy(alpha = 0.14f),
                            topLeft = Offset(c * cellW, r * cellH),
                            size = Size(cellW, cellH),
                            style = Stroke(width = 1f)
                        )
                    }
                }

                // Draw Decorations
                draft.decorations.forEach { (pos, dec) ->
                    val cx = (pos.col + 0.5f) * cellW
                    val cy = (pos.row + 0.5f) * cellH
                    drawCircle(
                        color = dec.color,
                        radius = minOf(cellW, cellH) * 0.35f,
                        center = Offset(cx, cy)
                    )
                }

                // Draw Caravan Path with Waypoint Sequence Dots
                if (draft.caravanPath.isNotEmpty()) {
                    val path = Path()
                    draft.caravanPath.forEachIndexed { idx, pos ->
                        val px = (pos.col + 0.5f) * cellW
                        val py = (pos.row + 0.5f) * cellH
                        if (idx == 0) path.moveTo(px, py) else path.lineTo(px, py)
                    }
                    drawPath(
                        path = path,
                        color = draft.biome.roadColor,
                        style = Stroke(width = minOf(cellW, cellH) * 0.52f, cap = StrokeCap.Round)
                    )
                    drawPath(
                        path = path,
                        color = Color(0xFFFFEE58),
                        style = Stroke(
                            width = 3f,
                            cap = StrokeCap.Round,
                            pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 8f), 0f)
                        )
                    )
                    draft.caravanPath.forEachIndexed { idx, pos ->
                        val px = (pos.col + 0.5f) * cellW
                        val py = (pos.row + 0.5f) * cellH
                        val nodeColor = when (idx) {
                            0 -> Color(0xFF00E676)
                            draft.caravanPath.lastIndex -> Color(0xFFFFD54F)
                            else -> Color.White
                        }
                        drawCircle(
                            color = nodeColor,
                            radius = if (idx == 0 || idx == draft.caravanPath.lastIndex) 9f else 5f,
                            center = Offset(px, py)
                        )
                    }
                }

                // Draw Tower Slots
                draft.towerSlots.forEach { slot ->
                    val sx = (slot.col + 0.5f) * cellW
                    val sy = (slot.row + 0.5f) * cellH
                    val r = minOf(cellW, cellH) * 0.36f
                    drawRoundRect(
                        color = Color(0xFF102A27),
                        topLeft = Offset(sx - r, sy - r),
                        size = Size(r * 2f, r * 2f),
                        cornerRadius = CornerRadius(8f, 8f)
                    )
                    drawRoundRect(
                        color = Color(0xFF48CAE4),
                        topLeft = Offset(sx - r, sy - r),
                        size = Size(r * 2f, r * 2f),
                        cornerRadius = CornerRadius(8f, 8f),
                        style = Stroke(width = 3f)
                    )
                }

                // Draw Enemy Ambush Spawners
                draft.enemySpawns.forEachIndexed { index, sp ->
                    val sx = (sp.col + 0.5f) * cellW
                    val sy = (sp.row + 0.5f) * cellH
                    val r = minOf(cellW, cellH) * 0.38f
                    drawCircle(
                        color = Color(0xFFD62828),
                        radius = r,
                        center = Offset(sx, sy)
                    )
                    drawCircle(
                        color = Color.White,
                        radius = r * 0.45f,
                        center = Offset(sx, sy)
                    )
                }

                // Highlight Drop Cell if user is currently dragging an Enemy over the Canvas!
                val bounds = canvasWindowBounds
                val winPos = draggingEnemyWindowPos
                if (draggingEnemyType != null && bounds != null && winPos != null && bounds.contains(winPos)) {
                    val localX = winPos.x - bounds.left
                    val localY = winPos.y - bounds.top
                    val hoverCol = (localX / cellW).toInt().coerceIn(0, cols - 1)
                    val hoverRow = (localY / cellH).toInt().coerceIn(0, rows - 1)
                    drawRoundRect(
                        color = Color(0xFFFF5252).copy(alpha = 0.45f),
                        topLeft = Offset(hoverCol * cellW, hoverRow * cellH),
                        size = Size(cellW, cellH),
                        cornerRadius = CornerRadius(8f, 8f)
                    )
                    drawRoundRect(
                        color = Color.White,
                        topLeft = Offset(hoverCol * cellW, hoverRow * cellH),
                        size = Size(cellW, cellH),
                        cornerRadius = CornerRadius(8f, 8f),
                        style = Stroke(width = 3.5f)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Section 2: Drag-and-Drop Enemy Palette (کشیدن و رها کردن دشمنان روی نقشه یا موج!)
        Card(
            colors = CardDefaults.cardColors(containerColor = Color(0xFF231711)),
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text = "۲. نوار دشمنان: هر دشمن را بکشید و روی نقشه رها کنید (یا روی آن بزنید تا به موج اضافه شود):",
                    style = MaterialTheme.typography.labelLarge,
                    color = SaffronGold
                )
                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    EnemyType.entries.forEach { enemyType ->
                        var chipBounds by remember { mutableStateOf<Rect?>(null) }
                        Surface(
                            color = Color(0xFF2E1E16),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .border(1.5.dp, enemyType.badgeColor, RoundedCornerShape(12.dp))
                                .onGloballyPositioned { coords ->
                                    chipBounds = coords.boundsInWindow()
                                }
                                .testTag("editor_enemy_chip_${enemyType.name.lowercase()}")
                                .pointerInput(enemyType) {
                                    detectDragGestures(
                                        onDragStart = { startOffset ->
                                            draggingEnemyType = enemyType
                                            val b = chipBounds
                                            if (b != null) {
                                                draggingEnemyWindowPos = Offset(b.left + startOffset.x, b.top + startOffset.y)
                                            }
                                            editorHint = "در حال کشیدن «${enemyType.titleFa}»... روی هر خانهٔ نقشه رها کنید!"
                                        },
                                        onDrag = { change, _ ->
                                            change.consume()
                                            val b = chipBounds
                                            if (b != null) {
                                                draggingEnemyWindowPos = Offset(
                                                    b.left + change.position.x,
                                                    b.top + change.position.y
                                                )
                                            }
                                        },
                                        onDragEnd = {
                                            val bounds = canvasWindowBounds
                                            val winPos = draggingEnemyWindowPos
                                            if (bounds != null && winPos != null && bounds.contains(winPos)) {
                                                val localX = winPos.x - bounds.left
                                                val localY = winPos.y - bounds.top
                                                val col = ((localX / bounds.width) * currentDraft.gridCols)
                                                    .toInt()
                                                    .coerceIn(0, currentDraft.gridCols - 1)
                                                val row = ((localY / bounds.height) * currentDraft.gridRows)
                                                    .toInt()
                                                    .coerceIn(0, currentDraft.gridRows - 1)
                                                dropEnemyOntoGridCell(enemyType, GridPos(col, row))
                                            } else {
                                                // If dropped outside the grid canvas, add directly to active wave!
                                                val defaultSpawn = currentDraft.enemySpawns.firstOrNull()
                                                    ?: GridPos(currentDraft.gridCols - 1, 1)
                                                dropEnemyOntoGridCell(enemyType, defaultSpawn)
                                            }
                                            draggingEnemyType = null
                                            draggingEnemyWindowPos = null
                                        },
                                        onDragCancel = {
                                            draggingEnemyType = null
                                            draggingEnemyWindowPos = null
                                        }
                                    )
                                }
                                .clickable {
                                    val defaultSpawn = currentDraft.enemySpawns.firstOrNull()
                                        ?: GridPos(currentDraft.gridCols - 1, 1)
                                    dropEnemyOntoGridCell(enemyType, defaultSpawn)
                                }
                        ) {
                            Column(
                                modifier = Modifier
                                    .width(115.dp)
                                    .padding(8.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(22.dp)
                                        .clip(CircleShape)
                                        .background(enemyType.badgeColor),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = if (enemyType.isBoss) "👑" else "⚔",
                                        fontSize = 11.sp
                                    )
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = enemyType.shortNameFa,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    maxLines = 1
                                )
                                Text(
                                    text = "جان: ${enemyType.baseHp.toInt()}",
                                    fontSize = 10.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = "بکشید یا بزنید +",
                                    fontSize = 9.sp,
                                    color = enemyType.badgeColor,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Section 3: Visual Wave Timeline & Enemy Customizer
        Card(
            colors = CardDefaults.cardColors(containerColor = Color(0xFF231711)),
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "۳. مدیریت موج‌های حمله (${draft.waves.size} موج):",
                        style = MaterialTheme.typography.labelLarge,
                        color = BrightAmber
                    )
                    FilledTonalButton(
                        onClick = {
                            val nextNum = draft.waves.size + 1
                            val newWave = WaveSpec(
                                waveNumber = nextNum,
                                title = "موج $nextNum",
                                entries = listOf(
                                    WaveEntry(EnemyType.DESERT_BANDIT, count = 5 + nextNum, spawnIndex = 0)
                                ),
                                rewardGold = 55 + nextNum * 15
                            )
                            onUpdateDraft(draft.copy(waves = draft.waves + newWave))
                            selectedWaveIdx = draft.waves.size
                        },
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.testTag("editor_add_wave_button")
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("موج جدید", fontSize = 11.sp)
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Wave selector tabs
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    draft.waves.forEachIndexed { idx, wave ->
                        val active = idx == safeWaveIdx
                        Surface(
                            color = if (active) PersianTurquoise else Color(0xFF2F2119),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.clickable { selectedWaveIdx = idx }
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "موج ${idx + 1} (${wave.entries.sumOf { it.count }} دشمن)",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (active) Color.Black else Color.White
                                )
                                if (draft.waves.size > 1 && active) {
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Icon(
                                        Icons.Default.Delete,
                                        contentDescription = "حذف موج",
                                        tint = Color.Black,
                                        modifier = Modifier
                                            .size(15.dp)
                                            .clickable {
                                                val updated = draft.waves.toMutableList().apply { removeAt(idx) }
                                                onUpdateDraft(draft.copy(waves = updated))
                                                selectedWaveIdx = (idx - 1).coerceAtLeast(0)
                                            }
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                if (activeWave != null) {
                    if (activeWave.entries.isEmpty()) {
                        Text(
                            text = "این موج خالی است. از نوار بالا یکی از دشمنان را بکشید و رها کنید!",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    } else {
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            activeWave.entries.forEachIndexed { entryIdx, entry ->
                                Surface(
                                    color = Color(0xFF18110D),
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(horizontal = 10.dp, vertical = 6.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Box(
                                                modifier = Modifier
                                                    .size(12.dp)
                                                    .clip(CircleShape)
                                                    .background(entry.enemyType.badgeColor)
                                            )
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Column {
                                                Text(
                                                    text = entry.enemyType.titleFa,
                                                    fontSize = 12.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = Color.White
                                                )
                                                Text(
                                                    text = "دروازهٔ ورودی: ${(entry.spawnIndex % draft.enemySpawns.size.coerceAtLeast(1)) + 1} · ضریب جان: ${entry.hpMultiplier}x",
                                                    fontSize = 10.sp,
                                                    color = OasisCyan,
                                                    modifier = Modifier.clickable {
                                                        val nextSpawn = (entry.spawnIndex + 1) % draft.enemySpawns.size.coerceAtLeast(1)
                                                        val nextMult = when (entry.hpMultiplier) {
                                                            1.0f -> 1.3f
                                                            1.3f -> 1.7f
                                                            else -> 1.0f
                                                        }
                                                        val newEntries = activeWave.entries.toMutableList()
                                                        newEntries[entryIdx] = entry.copy(spawnIndex = nextSpawn, hpMultiplier = nextMult)
                                                        val newWaves = draft.waves.toMutableList()
                                                        newWaves[safeWaveIdx] = activeWave.copy(entries = newEntries)
                                                        onUpdateDraft(draft.copy(waves = newWaves))
                                                    }
                                                )
                                            }
                                        }

                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            IconButton(
                                                onClick = {
                                                    val newEntries = activeWave.entries.toMutableList()
                                                    if (entry.count > 1) {
                                                        newEntries[entryIdx] = entry.copy(count = entry.count - 1)
                                                    } else {
                                                        newEntries.removeAt(entryIdx)
                                                    }
                                                    val newWaves = draft.waves.toMutableList()
                                                    newWaves[safeWaveIdx] = activeWave.copy(entries = newEntries)
                                                    onUpdateDraft(draft.copy(waves = newWaves))
                                                },
                                                modifier = Modifier.size(28.dp)
                                            ) {
                                                Icon(Icons.Default.Remove, contentDescription = "کاهش", tint = CrimsonSilk)
                                            }
                                            Text(
                                                text = "${entry.count} عدد",
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = BrightAmber,
                                                modifier = Modifier.padding(horizontal = 6.dp)
                                            )
                                            IconButton(
                                                onClick = {
                                                    val newEntries = activeWave.entries.toMutableList()
                                                    newEntries[entryIdx] = entry.copy(count = entry.count + 1)
                                                    val newWaves = draft.waves.toMutableList()
                                                    newWaves[safeWaveIdx] = activeWave.copy(entries = newEntries)
                                                    onUpdateDraft(draft.copy(waves = newWaves))
                                                },
                                                modifier = Modifier.size(28.dp)
                                            ) {
                                                Icon(Icons.Default.Add, contentDescription = "افزایش", tint = OasisCyan)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Section 4: No-Code Level Story, Biome & Economy Settings
        Card(
            colors = CardDefaults.cardColors(containerColor = Color(0xFF231711)),
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text = "۴. مشخصات داستانی، اقلیم و منابع مرحله:",
                    style = MaterialTheme.typography.labelLarge,
                    color = SaffronGold
                )
                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = draft.title,
                        onValueChange = { onUpdateDraft(draft.copy(title = it)) },
                        label = { Text("نام مرحله") },
                        singleLine = true,
                        modifier = Modifier
                            .weight(1.2f)
                            .testTag("editor_level_title_input")
                    )
                    OutlinedTextField(
                        value = draft.originCity,
                        onValueChange = { onUpdateDraft(draft.copy(originCity = it)) },
                        label = { Text("شهر مبدا") },
                        singleLine = true,
                        modifier = Modifier.weight(0.9f)
                    )
                    OutlinedTextField(
                        value = draft.destinationCity,
                        onValueChange = { onUpdateDraft(draft.copy(destinationCity = it)) },
                        label = { Text("شهر مقصد") },
                        singleLine = true,
                        modifier = Modifier.weight(0.9f)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = draft.storyIntro,
                    onValueChange = { onUpdateDraft(draft.copy(storyIntro = it)) },
                    label = { Text("روایت داستانی کاروان در این مرحله") },
                    maxLines = 2,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Biome Selector
                Text("اقلیم جغرافیایی:", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    BiomeType.entries.forEach { biome ->
                        val selected = draft.biome == biome
                        Surface(
                            color = if (selected) SaffronGold else Color(0xFF2D2018),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.clickable {
                                onUpdateDraft(draft.copy(biome = biome))
                            }
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    Icons.Default.Landscape,
                                    contentDescription = null,
                                    tint = if (selected) Color.Black else biome.dayTilePrimary,
                                    modifier = Modifier.size(15.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = biome.titleFa,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (selected) Color.Black else Color.White
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Numeric Resource Steppers (Starting Gold, Caravan HP, Relocation Charges)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    ParameterStepper(
                        label = "سکهٔ شروع",
                        valueText = "${draft.startingGold}",
                        onDecrement = { onUpdateDraft(draft.copy(startingGold = (draft.startingGold - 25).coerceAtLeast(100))) },
                        onIncrement = { onUpdateDraft(draft.copy(startingGold = (draft.startingGold + 25).coerceAtMost(900))) }
                    )
                    ParameterStepper(
                        label = "جان کاروان",
                        valueText = "${draft.caravanMaxHp}",
                        onDecrement = { onUpdateDraft(draft.copy(caravanMaxHp = (draft.caravanMaxHp - 15).coerceAtLeast(50))) },
                        onIncrement = { onUpdateDraft(draft.copy(caravanMaxHp = (draft.caravanMaxHp + 15).coerceAtMost(500))) }
                    )
                    ParameterStepper(
                        label = "شارژ جابه‌جایی",
                        valueText = "${draft.maxRelocateCharges}",
                        onDecrement = { onUpdateDraft(draft.copy(maxRelocateCharges = (draft.maxRelocateCharges - 1).coerceAtLeast(1))) },
                        onIncrement = { onUpdateDraft(draft.copy(maxRelocateCharges = (draft.maxRelocateCharges + 1).coerceAtMost(12))) }
                    )
                }
            }
        }
    }

    // Saved Custom Levels Modal Dialog
    if (showSavedLevelsSheet) {
        AlertDialog(
            onDismissRequest = { showSavedLevelsSheet = false },
            containerColor = Color(0xFF231711),
            title = {
                Text("مراحل ساخته‌شدهٔ شما (${savedCustomLevels.size})", color = SaffronGold)
            },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (savedCustomLevels.isEmpty()) {
                        Text("هنوز مرحله‌ای ذخیره نکرده‌اید.", color = Color.White)
                    } else {
                        savedCustomLevels.forEach { item ->
                            Surface(
                                color = Color(0xFF170F0B),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(10.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(item.title, fontWeight = FontWeight.Bold, color = BrightAmber)
                                        Text(
                                            "${item.originCity} ⬅ ${item.destinationCity} · ${item.waves.size} موج",
                                            fontSize = 11.sp,
                                            color = OasisCyan
                                        )
                                    }
                                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                        FilledTonalButton(
                                            onClick = {
                                                onUpdateDraft(item)
                                                showSavedLevelsSheet = false
                                            }
                                        ) {
                                            Text("ویرایش", fontSize = 11.sp)
                                        }
                                        Button(
                                            onClick = {
                                                showSavedLevelsSheet = false
                                                onSaveLevel(item, true)
                                            },
                                            colors = ButtonDefaults.buttonColors(
                                                containerColor = SaffronGold,
                                                contentColor = Color.Black
                                            )
                                        ) {
                                            Text("بازی", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                        }
                                        IconButton(onClick = { onDeleteCustomLevel(item.id) }) {
                                            Icon(Icons.Default.Delete, contentDescription = "حذف", tint = CrimsonSilk)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(onClick = { showSavedLevelsSheet = false }) {
                    Text("بستن")
                }
            }
        )
    }

    // JSON Export / Import Modal Dialog
    if (showJsonDialog) {
        AlertDialog(
            onDismissRequest = { showJsonDialog = false },
            containerColor = Color(0xFF231711),
            title = {
                Text("کد دادهٔ مرحله (خروجی / ورود JSON)", color = SaffronGold)
            },
            text = {
                Column {
                    Text(
                        "می‌توانید کد زیر را کپی کنید یا کد یک مرحلهٔ دیگر را جایگذاری و بارگذاری نمایید:",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = jsonInputText,
                        onValueChange = { jsonInputText = it },
                        maxLines = 7,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val parsed = LevelBlueprint.fromJsonString(jsonInputText)
                        if (parsed != null) {
                            onUpdateDraft(parsed.copy(isCustom = true))
                            showJsonDialog = false
                            editorHint = "مرحله از روی کد JSON با موفقیت بارگذاری شد!"
                        } else {
                            editorHint = "فرمت کد JSON معتبر نبود."
                        }
                    }
                ) {
                    Text("بارگذاری کد")
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { showJsonDialog = false }) {
                    Text("انصراف")
                }
            }
        )
    }
}

@Composable
private fun ParameterStepper(
    label: String,
    valueText: String,
    onDecrement: () -> Unit,
    onIncrement: () -> Unit
) {
    Surface(
        color = Color(0xFF18110C),
        shape = RoundedCornerShape(10.dp)
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(label, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onDecrement, modifier = Modifier.size(24.dp)) {
                    Icon(Icons.Default.Remove, contentDescription = null, tint = CrimsonSilk, modifier = Modifier.size(14.dp))
                }
                Text(
                    text = valueText,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = BrightAmber,
                    modifier = Modifier.padding(horizontal = 4.dp)
                )
                IconButton(onClick = onIncrement, modifier = Modifier.size(24.dp)) {
                    Icon(Icons.Default.Add, contentDescription = null, tint = OasisCyan, modifier = Modifier.size(14.dp))
                }
            }
        }
    }
}

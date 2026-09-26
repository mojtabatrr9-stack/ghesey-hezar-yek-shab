package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AllInclusive
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Fort
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.VolumeOff
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.campaign.CampaignLevels
import com.example.ui.CaravanViewModel
import com.example.ui.MainTab
import com.example.ui.battle.BattleScreen
import com.example.ui.camp.OasisMetaCampScreen
import com.example.ui.editor.VisualLevelEditorScreen
import com.example.ui.map.SilkRoadMapScreen
import com.example.ui.modes.SpecialModesScreen
import com.example.ui.theme.BrightAmber
import com.example.ui.theme.CrimsonSilk
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.OasisCyan
import com.example.ui.theme.PersianTurquoise
import com.example.ui.theme.SaffronGold

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                CaravanSilkRoadApp()
            }
        }
    }
}

@Composable
fun CaravanSilkRoadApp(
    caravanViewModel: CaravanViewModel = viewModel()
) {
    val currentTab by caravanViewModel.currentTab.collectAsStateWithLifecycle()
    val playerProgress by caravanViewModel.playerProgress.collectAsStateWithLifecycle()
    val customLevels by caravanViewModel.customLevels.collectAsStateWithLifecycle()
    val activeBattleLevel by caravanViewModel.activeBattleLevel.collectAsStateWithLifecycle()
    val battleSessionId by caravanViewModel.battleSessionId.collectAsStateWithLifecycle()
    val editorDraft by caravanViewModel.editorDraftLevel.collectAsStateWithLifecycle()
    val statusBanner by caravanViewModel.statusBanner.collectAsStateWithLifecycle()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        contentWindowInsets = WindowInsets.safeDrawing,
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF1A120C))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Brand Title
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "🐪 کاروان: جادهٔ هزار شهر",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.ExtraBold,
                            color = SaffronGold
                        )
                    }

                    // Caravan Resources Bar (Gold, Silk, Saffron) + Sound Toggle
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        ResourcePill(label = "سکه", value = "${playerProgress.goldCoins}", color = BrightAmber)
                        ResourcePill(label = "ابریشم", value = "${playerProgress.silkCount}", color = OasisCyan)
                        ResourcePill(label = "زعفران", value = "${playerProgress.saffronCount}", color = CrimsonSilk)

                        IconButton(
                            onClick = { caravanViewModel.toggleSound() },
                            modifier = Modifier
                                .size(32.dp)
                                .testTag("sound_toggle_button")
                        ) {
                            Icon(
                                imageVector = if (playerProgress.soundEnabled) Icons.Default.VolumeUp else Icons.Default.VolumeOff,
                                contentDescription = "صدا",
                                tint = if (playerProgress.soundEnabled) SaffronGold else Color.Gray,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }

                AnimatedVisibility(visible = statusBanner != null) {
                    statusBanner?.let { msg ->
                        Surface(
                            color = PersianTurquoise,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { caravanViewModel.clearStatusBanner() }
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = msg,
                                    color = Color(0xFF061E1B),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    modifier = Modifier.weight(1f)
                                )
                                Text(
                                    text = "✕",
                                    color = Color(0xFF061E1B),
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 13.sp
                                )
                            }
                        }
                    }
                }
            }
        },
        bottomBar = {
            NavigationBar(
                containerColor = Color(0xFF1A120C),
                contentColor = SaffronGold
            ) {
                MainTab.entries.forEach { tab ->
                    val icon = when (tab) {
                        MainTab.SILK_ROAD_MAP -> Icons.Default.Explore
                        MainTab.BATTLE -> Icons.Default.Shield
                        MainTab.OASIS_CAMP -> Icons.Default.Fort
                        MainTab.LEVEL_EDITOR -> Icons.Default.Build
                        MainTab.SPECIAL_MODES -> Icons.Default.AllInclusive
                    }
                    NavigationBarItem(
                        selected = currentTab == tab,
                        onClick = { caravanViewModel.selectTab(tab) },
                        icon = {
                            Icon(imageVector = icon, contentDescription = tab.titleFa)
                        },
                        label = {
                            Text(text = tab.titleFa, fontSize = 10.sp, maxLines = 1)
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color(0xFF1B1108),
                            selectedTextColor = SaffronGold,
                            indicatorColor = SaffronGold,
                            unselectedIconColor = Color(0xFFB09780),
                            unselectedTextColor = Color(0xFFB09780)
                        ),
                        modifier = Modifier.testTag("nav_tab_${tab.name.lowercase()}")
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (currentTab) {
                MainTab.SILK_ROAD_MAP -> {
                    SilkRoadMapScreen(
                        playerProgress = playerProgress,
                        customLevels = customLevels,
                        onPlayLevel = { level ->
                            caravanViewModel.startBattleForLevel(level)
                        },
                        onOpenLevelEditor = { levelToEdit ->
                            caravanViewModel.openInLevelEditor(levelToEdit)
                        },
                        onDeleteCustomLevel = { id ->
                            caravanViewModel.deleteCustomLevel(id)
                        }
                    )
                }

                MainTab.BATTLE -> {
                    BattleScreen(
                        level = activeBattleLevel,
                        sessionId = battleSessionId,
                        playerProgress = playerProgress,
                        onVictoryRecorded = { stars, bonusGold, bonusSilk, bonusSaffron, wavesCleared ->
                            caravanViewModel.onBattleVictory(
                                level = activeBattleLevel,
                                stars = stars,
                                bonusGold = bonusGold,
                                bonusSilk = bonusSilk,
                                bonusSaffron = bonusSaffron,
                                wavesCleared = wavesCleared
                            )
                        },
                        onEditThisLevel = { lvl ->
                            caravanViewModel.openInLevelEditor(lvl)
                        },
                        onBackToMap = {
                            caravanViewModel.selectTab(MainTab.SILK_ROAD_MAP)
                        },
                        onNextCampaignLevel = {
                            val nextIdx = activeBattleLevel.levelNumber.coerceIn(1, 11)
                            val nextStage = CampaignLevels.allLevels.getOrNull(nextIdx)
                                ?: CampaignLevels.allLevels.last()
                            caravanViewModel.startBattleForLevel(nextStage)
                        }
                    )
                }

                MainTab.OASIS_CAMP -> {
                    OasisMetaCampScreen(
                        playerProgress = playerProgress,
                        onUpgradeMeta = { upg ->
                            caravanViewModel.upgradeCaravanMeta(upg)
                        },
                        onTradeSaffron = {
                            caravanViewModel.tradeSaffronForGoldAndSilk()
                        },
                        onTradeSilk = {
                            caravanViewModel.tradeSilkForGold()
                        },
                        onBack = {
                            caravanViewModel.selectTab(MainTab.SILK_ROAD_MAP)
                        }
                    )
                }

                MainTab.LEVEL_EDITOR -> {
                    VisualLevelEditorScreen(
                        draft = editorDraft,
                        savedCustomLevels = customLevels,
                        onUpdateDraft = { updated ->
                            caravanViewModel.updateEditorDraft(updated)
                        },
                        onSaveLevel = { blueprint, playNow ->
                            caravanViewModel.saveEditorLevelAndNotify(blueprint, playImmediately = playNow)
                        },
                        onDeleteCustomLevel = { id ->
                            caravanViewModel.deleteCustomLevel(id)
                        },
                        onBack = {
                            caravanViewModel.selectTab(MainTab.SILK_ROAD_MAP)
                        }
                    )
                }

                MainTab.SPECIAL_MODES -> {
                    SpecialModesScreen(
                        playerProgress = playerProgress,
                        onStartEndlessSandstorm = {
                            caravanViewModel.startEndlessSandstorm()
                        },
                        onStartWeeklyBossRaid = {
                            caravanViewModel.startWeeklyBossRaid()
                        },
                        onBack = {
                            caravanViewModel.selectTab(MainTab.SILK_ROAD_MAP)
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun ResourcePill(label: String, value: String, color: Color) {
    Surface(
        color = Color(0xFF2B1E15),
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "$value ",
                fontSize = 11.sp,
                fontWeight = FontWeight.ExtraBold,
                color = color
            )
            Text(
                text = label,
                fontSize = 10.sp,
                color = Color(0xFFD0B89C)
            )
        }
    }
}

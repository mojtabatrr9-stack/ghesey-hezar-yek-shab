package com.example.ui.modes

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AllInclusive
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.PlayerProgressEntity
import com.example.data.model.EnemyType
import com.example.data.model.TowerType
import com.example.ui.theme.BrightAmber
import com.example.ui.theme.CrimsonSilk
import com.example.ui.theme.OasisCyan
import com.example.ui.theme.PersianTurquoise
import com.example.ui.theme.SaffronGold

@Composable
fun SpecialModesScreen(
    playerProgress: PlayerProgressEntity,
    onStartEndlessSandstorm: () -> Unit,
    onStartWeeklyBossRaid: () -> Unit,
    onBack: () -> Unit
) {
    BackHandler {
        onBack()
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .testTag("special_modes_screen"),
        contentPadding = PaddingValues(14.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // 1. Endless Mode "Sandstorm" (حالت بی‌پایان طوفان شن با ژنراتور پارامتریک)
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF281D14)),
                shape = RoundedCornerShape(18.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.5.dp, SaffronGold, RoundedCornerShape(18.dp))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.AllInclusive, contentDescription = null, tint = SaffronGold)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "حالت بی‌پایان: «طوفان شن» (ژنراتور پارامتریک)",
                            style = MaterialTheme.typography.titleLarge,
                            color = SaffronGold
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "هر بار که وارد طوفان شن شوید، ژنراتور پارامتریک یک مسیر کویری جدید، پایگاه‌های برج تصادفی و ۸ موج فزاینده می‌سازد!",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "بهترین رکورد شما: ${playerProgress.endlessBestWave} موج",
                            fontWeight = FontWeight.Bold,
                            color = OasisCyan
                        )
                        Button(
                            onClick = onStartEndlessSandstorm,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = SaffronGold,
                                contentColor = Color.Black
                            ),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.testTag("start_endless_sandstorm_button")
                        ) {
                            Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("ورود به طوفان شن", fontWeight = FontWeight.ExtraBold)
                        }
                    }
                }
            }
        }

        // 2. Weekly Boss Raid (رید باس هفتگی: محاصرهٔ خانِ طوفانِ سرخ)
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF2B1414)),
                shape = RoundedCornerShape(18.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.5.dp, CrimsonSilk, RoundedCornerShape(18.dp))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.LocalFireDepartment, contentDescription = null, tint = CrimsonSilk)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "رید باس هفتگی: «محاصرهٔ خانِ طوفانِ سرخ»",
                            style = MaterialTheme.typography.titleLarge,
                            color = CrimsonSilk
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "نبرد سنگین در برابر خانِ طوفانِ سرخ، ارابه‌های داس‌دار و طبل‌زنان قبیله! نیازمند جابه‌جایی سریع برج‌ها در میانهٔ نبرد.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "تعداد پیروزی در رید: ${playerProgress.bossRaidClears} بار",
                            fontWeight = FontWeight.Bold,
                            color = BrightAmber
                        )
                        Button(
                            onClick = onStartWeeklyBossRaid,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = CrimsonSilk,
                                contentColor = Color.White
                            ),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.testTag("start_weekly_boss_raid_button")
                        ) {
                            Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("نبرد با باس هفتگی", fontWeight = FontWeight.ExtraBold)
                        }
                    }
                }
            }
        }

        // 3. Codex of 5 Portable Towers (+2 Evolutions each)
        item {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.MenuBook, contentDescription = null, tint = OasisCyan)
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "طومار برج‌های قابل‌حمل (۵ برج + ۱۰ تکامل)",
                    style = MaterialTheme.typography.titleLarge,
                    color = SaffronGold
                )
            }
        }

        items(TowerType.entries) { tower ->
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF211711)),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(14.dp)
                                    .clip(CircleShape)
                                    .background(tower.primaryColor)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(tower.titleFa, fontWeight = FontWeight.Bold, color = BrightAmber)
                        }
                        Text("هزینه: ${tower.baseCost} سکه", fontSize = 11.sp, color = OasisCyan)
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(tower.roleFa, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        tower.evolutions.forEach { evo ->
                            Surface(
                                color = Color(0xFF160F0B),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Column(modifier = Modifier.padding(8.dp)) {
                                    Text(
                                        text = "تکامل: ${evo.titleFa}",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = evo.accentColor
                                    )
                                    Text(
                                        text = evo.descFa,
                                        fontSize = 10.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // 4. Codex of 7 Enemies + 1 Boss
        item {
            Text(
                text = "طومار راهزنان و دشمنان جادهٔ ابریشم (۷ دشمن + ۱ باس)",
                style = MaterialTheme.typography.titleLarge,
                color = CrimsonSilk
            )
        }

        items(EnemyType.entries) { enemy ->
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF211711)),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(enemy.titleFa, fontWeight = FontWeight.Bold, color = enemy.badgeColor)
                        Text(enemy.descFa, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text("جان: ${enemy.baseHp.toInt()}", fontSize = 11.sp, color = BrightAmber)
                        Text("زره: ${(enemy.armor * 100).toInt()}٪", fontSize = 11.sp, color = OasisCyan)
                    }
                }
            }
        }
    }
}

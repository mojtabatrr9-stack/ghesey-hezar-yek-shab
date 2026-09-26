package com.example.ui.camp

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.CurrencyExchange
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.local.PlayerProgressEntity
import com.example.ui.CaravanMetaUpgradeType
import com.example.ui.theme.BrightAmber
import com.example.ui.theme.CrimsonSilk
import com.example.ui.theme.OasisCyan
import com.example.ui.theme.PersianTurquoise
import com.example.ui.theme.SaffronGold

@Composable
fun OasisMetaCampScreen(
    playerProgress: PlayerProgressEntity,
    onUpgradeMeta: (CaravanMetaUpgradeType) -> Unit,
    onTradeSaffron: () -> Unit,
    onTradeSilk: () -> Unit,
    onBack: () -> Unit
) {
    BackHandler {
        onBack()
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .testTag("oasis_meta_camp_screen"),
        contentPadding = PaddingValues(14.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // 1. Night Oasis Camp Banner
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1A1410)),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.5.dp, PersianTurquoise, RoundedCornerShape(20.dp))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(185.dp)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.img_oasis_camp_1790428561545),
                        contentDescription = "اردوگاه شبانه در واحه",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(
                                        Color.Transparent,
                                        Color(0xFF140E0A).copy(alpha = 0.88f),
                                        Color(0xFF140E0A)
                                    )
                                )
                            )
                    )
                    Column(
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(16.dp)
                    ) {
                        Text(
                            text = "اردوگاه شبانهٔ واحه و ارتقای کاروان",
                            style = MaterialTheme.typography.headlineMedium,
                            color = SaffronGold
                        )
                        Text(
                            text = "ارابه‌ها، شترها، نگهبان دائمی و قهرمان همراه را برای گذر از هزار شهر تقویت کنید.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color(0xFFEAE0D5)
                        )
                    }
                }
            }
        }

        // 2. Caravanserai Bazaar (معاملهٔ ابریشم و زعفران در بازار جادهٔ ابریشم)
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF241811)),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, SaffronGold.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Storefront, contentDescription = null, tint = SaffronGold)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "حجرهٔ بازرگانان کاروانسرا (معاملهٔ کالا)",
                            style = MaterialTheme.typography.titleMedium,
                            color = BrightAmber
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "موجودی خزانهٔ کاروان: ${playerProgress.goldCoins} سکهٔ طلا · ${playerProgress.silkCount} طاقه ابریشم · ${playerProgress.saffronCount} مثقال زعفران",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = OasisCyan
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        FilledTonalButton(
                            onClick = onTradeSaffron,
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("trade_saffron_button")
                        ) {
                            Icon(Icons.Default.CurrencyExchange, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("فروش ۱۰ زعفران ⬅ +۱۲۰ سکه و +۸ ابریشم", fontSize = 11.sp)
                        }

                        FilledTonalButton(
                            onClick = onTradeSilk,
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("trade_silk_button")
                        ) {
                            Icon(Icons.Default.CurrencyExchange, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("فروش ۱۵ ابریشم ⬅ +۱۵۰ سکهٔ طلا", fontSize = 11.sp)
                        }
                    }
                }
            }
        }

        // 3. Permanent Caravan Meta Upgrades
        item {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Shield, contentDescription = null, tint = OasisCyan)
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "ارتقاهای دائمی کاروان و قهرمان (متا)",
                    style = MaterialTheme.typography.titleLarge,
                    color = SaffronGold
                )
            }
        }

        items(CaravanMetaUpgradeType.entries) { upg ->
            val currentLv = when (upg) {
                CaravanMetaUpgradeType.WAGON_ARMOR -> playerProgress.wagonArmorLevel
                CaravanMetaUpgradeType.CAMEL_SPEED_RELOCATE -> playerProgress.camelEquipLevel
                CaravanMetaUpgradeType.PERMANENT_GUARD -> playerProgress.permanentGuardLevel
                CaravanMetaUpgradeType.COMPANION_HERO -> playerProgress.heroRankLevel
            }
            val goldCost = 90 + currentLv * 65
            val silkCost = 10 + currentLv * 6

            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF211711)),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, Color(0xFF473224), RoundedCornerShape(16.dp))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = upg.titleFa,
                                style = MaterialTheme.typography.titleMedium,
                                color = BrightAmber
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Surface(
                                color = PersianTurquoise.copy(alpha = 0.25f),
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Text(
                                    text = "سطح $currentLv/10",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = OasisCyan,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = upg.subtitleFa,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "اثر هر سطح: ${upg.effectPerLevelFa}",
                            fontSize = 11.sp,
                            color = OasisCyan,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Button(
                        onClick = { onUpgradeMeta(upg) },
                        enabled = currentLv < 10,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = SaffronGold,
                            contentColor = Color.Black
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.testTag("upgrade_meta_${upg.name.lowercase()}")
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.ArrowUpward, contentDescription = null, modifier = Modifier.size(15.dp))
                                Text("ارتقا", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            }
                            Text(
                                text = if (currentLv < 10) "$goldCost سکه + $silkCost ابریشم" else "حداکثر سطح",
                                fontSize = 10.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

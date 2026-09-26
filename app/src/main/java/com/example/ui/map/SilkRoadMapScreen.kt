package com.example.ui.map

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.campaign.CampaignLevels
import com.example.data.local.PlayerProgressEntity
import com.example.data.model.LevelBlueprint
import com.example.ui.theme.BrightAmber
import com.example.ui.theme.CrimsonSilk
import com.example.ui.theme.OasisCyan
import com.example.ui.theme.PersianTurquoise
import com.example.ui.theme.SaffronGold
import org.json.JSONObject

@Composable
fun SilkRoadMapScreen(
    playerProgress: PlayerProgressEntity,
    customLevels: List<LevelBlueprint>,
    onPlayLevel: (LevelBlueprint) -> Unit,
    onOpenLevelEditor: (LevelBlueprint?) -> Unit,
    onDeleteCustomLevel: (Long) -> Unit
) {
    val starsMap = remember(playerProgress.completedStarsJson) {
        runCatching { JSONObject(playerProgress.completedStarsJson) }.getOrDefault(JSONObject())
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .testTag("silk_road_map_list"),
        contentPadding = PaddingValues(14.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // 1. Painterly Hero Banner with Title, Quote, and Quick-Action CTA
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF211610)),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.5.dp, SaffronGold.copy(alpha = 0.6f), RoundedCornerShape(20.dp))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(215.dp)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.img_silk_road_hero_1790428549559),
                        contentDescription = "کاروان جاده ابریشم",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(
                                        Color.Black.copy(alpha = 0.25f),
                                        Color(0xFF140E0A).copy(alpha = 0.85f),
                                        Color(0xFF140E0A).copy(alpha = 0.96f)
                                    )
                                )
                            )
                    )
                    Column(
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(16.dp)
                    ) {
                        Surface(
                            color = PersianTurquoise.copy(alpha = 0.85f),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = "تاور دیفنس هیبریدی · برج‌های قابل‌حمل",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "کاروان: جادهٔ هزار شهر",
                            style = MaterialTheme.typography.headlineLarge,
                            color = SaffronGold
                        )
                        Text(
                            text = "«کاروان تو از پرسپولیس تا چانگ‌آن راه درازی دارد؛ و کویر، تنها نیست.»",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color(0xFFF7EBD4)
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Button(
                                onClick = {
                                    val currentStage = CampaignLevels.allLevels.getOrNull(
                                        (playerProgress.highestUnlockedLevel - 1).coerceIn(0, 11)
                                    ) ?: CampaignLevels.allLevels.first()
                                    onPlayLevel(currentStage)
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = SaffronGold,
                                    contentColor = Color.Black
                                ),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.testTag("hero_continue_campaign_button")
                            ) {
                                Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("ادامهٔ سفر کاروان", fontWeight = FontWeight.ExtraBold)
                            }

                            FilledTonalButton(
                                onClick = { onOpenLevelEditor(null) },
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.testTag("hero_open_editor_button")
                            ) {
                                Icon(Icons.Default.Build, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("ساخت مرحله (بدون کد)")
                            }
                        }
                    }
                }
            }
        }

        // 2. Highlighted Custom Level Editor Card & User-Created Levels Section
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF192826)),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.5.dp, PersianTurquoise, RoundedCornerShape(16.dp))
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "کارگاه مرحله‌ساز بصری (مراحل دست‌ساز شما)",
                                style = MaterialTheme.typography.titleMedium,
                                color = OasisCyan
                            )
                            Text(
                                text = "با کشیدن و رها کردن (Drag & Drop) مسیر کاروان و دشمنان، هر چند مرحله که می‌خواهید بسازید!",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color(0xFFD0F0EC)
                            )
                        }
                        Button(
                            onClick = { onOpenLevelEditor(null) },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = OasisCyan,
                                contentColor = Color.Black
                            ),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.testTag("create_new_custom_level_button")
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("مرحلهٔ جدید", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                    }

                    if (customLevels.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            customLevels.forEach { customLevel ->
                                val stars = starsMap.optInt("custom_${customLevel.id}", 0)
                                Surface(
                                    color = Color(0xFF101B1A),
                                    shape = RoundedCornerShape(12.dp),
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
                                            Text(
                                                text = customLevel.title,
                                                fontWeight = FontWeight.Bold,
                                                color = BrightAmber
                                            )
                                            Text(
                                                text = "${customLevel.originCity} ⬅ ${customLevel.destinationCity} · ${customLevel.waves.size} موج · ${customLevel.biome.titleFa}",
                                                fontSize = 11.sp,
                                                color = OasisCyan
                                            )
                                        }
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                                        ) {
                                            if (stars > 0) {
                                                Text("★$stars", color = BrightAmber, fontWeight = FontWeight.Bold)
                                            }
                                            IconButton(
                                                onClick = { onOpenLevelEditor(customLevel) },
                                                modifier = Modifier.size(32.dp)
                                            ) {
                                                Icon(Icons.Default.Edit, contentDescription = "ویرایش", tint = OasisCyan, modifier = Modifier.size(18.dp))
                                            }
                                            IconButton(
                                                onClick = { onDeleteCustomLevel(customLevel.id) },
                                                modifier = Modifier.size(32.dp)
                                            ) {
                                                Icon(Icons.Default.Delete, contentDescription = "حذف", tint = CrimsonSilk, modifier = Modifier.size(18.dp))
                                            }
                                            Button(
                                                onClick = { onPlayLevel(customLevel) },
                                                colors = ButtonDefaults.buttonColors(
                                                    containerColor = SaffronGold,
                                                    contentColor = Color.Black
                                                ),
                                                shape = RoundedCornerShape(8.dp)
                                            ) {
                                                Text("بازی", fontSize = 12.sp, fontWeight = FontWeight.Bold)
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

        // 3. Section Header: 12 Handcrafted Silk Road Campaign Stages
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Explore, contentDescription = null, tint = SaffronGold)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "کمپین ۱۲ مرحله‌ای: از پرسپولیس تا چانگ‌آن",
                        style = MaterialTheme.typography.titleLarge,
                        color = SaffronGold
                    )
                }
                Text(
                    text = "۱۲ شهر تاریخی",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        items(CampaignLevels.allLevels, key = { it.levelNumber }) { stage ->
            val stars = starsMap.optInt("camp_${stage.levelNumber}", 0)
            val isRecommendedCurrent = stage.levelNumber == playerProgress.highestUnlockedLevel

            Card(
                colors = CardDefaults.cardColors(
                    containerColor = if (isRecommendedCurrent) Color(0xFF2F2015) else Color(0xFF211711)
                ),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(
                        width = if (isRecommendedCurrent) 2.dp else 1.dp,
                        color = if (isRecommendedCurrent) SaffronGold else Color(0xFF422F22),
                        shape = RoundedCornerShape(16.dp)
                    )
                    .testTag("campaign_level_card_${stage.levelNumber}")
                    .clickable { onPlayLevel(stage) }
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(stage.biome.dayTilePrimary),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "${stage.levelNumber}",
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color(0xFF1B1108)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = stage.title,
                                    style = MaterialTheme.typography.titleMedium,
                                    color = BrightAmber
                                )
                                Text(
                                    text = "${stage.originCity} ⬅ ${stage.destinationCity} · ${stage.biome.titleFa}",
                                    fontSize = 12.sp,
                                    color = OasisCyan
                                )
                            }
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            repeat(3) { sIdx ->
                                Icon(
                                    imageVector = Icons.Default.Star,
                                    contentDescription = null,
                                    tint = if (sIdx < stars) BrightAmber else Color(0xFF4A3A2E),
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = stage.storyIntro,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "پاداش: +${stage.rewardSilk} ابریشم · +${stage.rewardSaffron} زعفران · ${stage.waves.size} موج",
                            fontSize = 11.sp,
                            color = SaffronGold
                        )

                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedButton(
                                onClick = {
                                    onOpenLevelEditor(
                                        stage.copy(
                                            id = 0L,
                                            isCustom = true,
                                            title = "${stage.title} (نسخهٔ من)"
                                        )
                                    )
                                },
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("شخصی‌سازی در ادیتور", fontSize = 11.sp)
                            }

                            Button(
                                onClick = { onPlayLevel(stage) },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = SaffronGold,
                                    contentColor = Color.Black
                                ),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.testTag("play_campaign_level_${stage.levelNumber}")
                            ) {
                                Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("حرکت کاروان", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }
}

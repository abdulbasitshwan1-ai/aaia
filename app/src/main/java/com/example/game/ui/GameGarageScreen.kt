package com.example.game.ui

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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material.icons.filled.Healing
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.game.GameCar
import com.example.data.model.game.GamePlayerProfile
import com.example.data.model.game.PaintFinish
import com.example.data.model.game.SetupPreset
import com.example.data.model.game.TransmissionType
import com.example.ui.theme.GoldReward
import com.example.ui.theme.ObsidianBg
import com.example.ui.theme.PrimaryBlue
import com.example.ui.theme.RacingRed
import com.example.ui.theme.StatusSuccess
import com.example.ui.theme.SurfaceBorder
import com.example.ui.theme.SurfaceCard
import com.example.ui.theme.SurfaceDark
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun GameGarageScreen(
    activeCar: GameCar?,
    profile: GamePlayerProfile?,
    garageSection: Int,
    onSectionSelected: (Int) -> Unit,
    onUpgradeEngine: (GameCar) -> Unit,
    onUpgradeTurbo: (GameCar) -> Unit,
    onSetSuspension: (GameCar, SetupPreset) -> Unit,
    onSetTransmission: (GameCar, TransmissionType) -> Unit,
    onUpdatePaint: (GameCar, Long, PaintFinish) -> Unit,
    onRepairCar: (GameCar) -> Unit,
    onBack: () -> Unit
) {
    val sections = listOf("مۆتۆر و تۆربۆ", "هایدرۆلیک و گێڕ", "ڕەنگ و بۆیە", "چاککردنەوە")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ObsidianBg)
            .testTag("game_garage_screen")
    ) {
        // Top Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier.background(SurfaceCard, CircleShape)
                ) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = TextPrimary)
                }
                Spacer(Modifier.width(12.dp))
                Column {
                    Text("گەراجی BASOKA", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = TextPrimary)
                    Text(activeCar?.name ?: "سەیارەی دیاریکراو", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                }
            }

            Card(
                colors = CardDefaults.cardColors(containerColor = SurfaceCard),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Star, contentDescription = null, tint = GoldReward, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(4.dp))
                    Text("${profile?.money ?: 0} IQD", color = GoldReward, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }
            }
        }

        // Section Tabs
        TabRow(
            selectedTabIndex = garageSection,
            containerColor = SurfaceDark,
            contentColor = PrimaryBlue,
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    Modifier.tabIndicatorOffset(tabPositions[garageSection]),
                    color = PrimaryBlue
                )
            }
        ) {
            sections.forEachIndexed { index, title ->
                Tab(
                    selected = garageSection == index,
                    onClick = { onSectionSelected(index) },
                    text = {
                        Text(
                            text = title,
                            fontSize = 12.sp,
                            fontWeight = if (garageSection == index) FontWeight.Bold else FontWeight.Normal,
                            color = if (garageSection == index) PrimaryBlue else TextSecondary
                        )
                    }
                )
            }
        }

        if (activeCar == null) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("هیچ سەیارەیەک هەڵنەبژێردراوە", color = TextSecondary)
            }
            return
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Live Car Telemetry Specs Card
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = SurfaceCard),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, SurfaceBorder, RoundedCornerShape(16.dp))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("هێزی ئەسپ", fontSize = 11.sp, color = TextMuted)
                            Text("${activeCar.currentHp} HP", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("خێرایی لوتکە", fontSize = 11.sp, color = TextMuted)
                            Text("${activeCar.currentTopSpeed} کلم/ک", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = PrimaryBlue)
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("0-100", fontSize = 11.sp, color = TextMuted)
                            Text("${String.format("%.1f", activeCar.currentAcceleration)}s", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = GoldReward)
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("تەندروستی", fontSize = 11.sp, color = TextMuted)
                            Text("${activeCar.conditionPercent}%", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = if (activeCar.conditionPercent > 70) StatusSuccess else RacingRed)
                        }
                    }
                }
            }

            when (garageSection) {
                0 -> {
                    // Engine & Turbo Upgrades
                    item {
                        UpgradeCard(
                            title = "نوێکردنەوەی مۆتۆر (ئاستی ${activeCar.engineLevel} لە 5)",
                            description = "+45 هێزی ئەسپ و +12 کلم/کاتژمێر لە خێرایی لوتکە",
                            cost = activeCar.engineLevel * 4500,
                            isMaxed = activeCar.engineLevel >= 5,
                            onUpgrade = { onUpgradeEngine(activeCar) }
                        )
                    }

                    item {
                        val turboLabel = when (activeCar.turboLevel) {
                            0 -> "بێ تۆربۆ (Natural Aspirated)"
                            1 -> "Single Turbo"
                            2 -> "Twin Turbo"
                            else -> "Quad Turbo Pro"
                        }
                        UpgradeCard(
                            title = "سیستەمی تۆربۆ ($turboLabel)",
                            description = "+85 هێزی ئەسپ و خێرابوونی خێراتری 0-100",
                            cost = (activeCar.turboLevel + 1) * 7000,
                            isMaxed = activeCar.turboLevel >= 3,
                            onUpgrade = { onUpgradeTurbo(activeCar) }
                        )
                    }
                }

                1 -> {
                    // Suspension & Transmission
                    item {
                        Text("ڕێکخستنی هایدرۆلیک و مامەڵە (Suspension Preset)", fontWeight = FontWeight.Bold, color = TextPrimary, fontSize = 14.sp)
                    }

                    item {
                        val presets = listOf(
                            SetupPreset.BALANCED to "هاوسەنگ (شەقامی ئاسایی)",
                            SetupPreset.SPORT to "سپۆرت (شەقامی خێرا)",
                            SetupPreset.RACE to "پێشبڕکێ (نزم و توند)",
                            SetupPreset.DRIFT to "تایبەت بە درێفت (زاویەی فراوان)",
                            SetupPreset.OFFROAD to "بەردەڵان و دەرەوەی شار"
                        )

                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            presets.forEach { (preset, label) ->
                                val isSelected = activeCar.suspensionPreset == preset
                                Card(
                                    colors = CardDefaults.cardColors(containerColor = if (isSelected) PrimaryBlue.copy(alpha = 0.2f) else SurfaceCard),
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .border(1.dp, if (isSelected) PrimaryBlue else SurfaceBorder, RoundedCornerShape(12.dp))
                                        .clickable { onSetSuspension(activeCar, preset) }
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(14.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(label, color = TextPrimary, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal)
                                        if (isSelected) {
                                            Text("چالاکە ✓", color = PrimaryBlue, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }
                            }
                        }
                    }

                    item {
                        Spacer(Modifier.height(8.dp))
                        Text("جۆری گێڕ (Transmission)", fontWeight = FontWeight.Bold, color = TextPrimary, fontSize = 14.sp)
                    }

                    item {
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            listOf(
                                TransmissionType.AUTOMATIC to "ئۆتۆماتیک",
                                TransmissionType.MANUAL to "دەستی (Manual)",
                                TransmissionType.SEQUENTIAL to "پەیڕەوی (Sequential)"
                            ).forEach { (type, label) ->
                                val isSelected = activeCar.transmissionType == type
                                Button(
                                    onClick = { onSetTransmission(activeCar, type) },
                                    colors = ButtonDefaults.buttonColors(containerColor = if (isSelected) PrimaryBlue else SurfaceCard),
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text(label, fontSize = 11.sp, color = if (isSelected) Color.White else TextSecondary)
                                }
                            }
                        }
                    }
                }

                2 -> {
                    // Paint & Finishes
                    item {
                        Text("هەڵبژاردنی ڕەنگی سەیارە (تێچوو: 1,200 IQD)", fontWeight = FontWeight.Bold, color = TextPrimary, fontSize = 14.sp)
                    }

                    item {
                        val colors = listOf(
                            0xFF1E88E5 to "شینی ئاسمانی",
                            0xFFFF1744 to "سووری پێشبڕکێ",
                            0xFFFFD600 to "زێڕینی تایبەت",
                            0xFF00E676 to "سەوزی ئەڵماس",
                            0xFF7C4DFF to "مۆری شاهانە",
                            0xFF12151D to "ڕەشی ماتی تاریک",
                            0xFFECEFF1 to "سپی بەفرین"
                        )

                        LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            items(colors) { (hex, name) ->
                                val isSelected = activeCar.primaryColorHex == hex
                                Box(
                                    modifier = Modifier
                                        .size(46.dp)
                                        .background(Color(hex), CircleShape)
                                        .border(3.dp, if (isSelected) Color.White else SurfaceBorder, CircleShape)
                                        .clickable {
                                            onUpdatePaint(activeCar, hex, activeCar.paintFinish)
                                        }
                                )
                            }
                        }
                    }

                    item {
                        Spacer(Modifier.height(8.dp))
                        Text("ڕووکاری بۆیە (Paint Finish)", fontWeight = FontWeight.Bold, color = TextPrimary, fontSize = 14.sp)
                    }

                    item {
                        val finishes = listOf(
                            PaintFinish.METALLIC to "میتالیک",
                            PaintFinish.MATTE to "مات",
                            PaintFinish.GLOSS to "بریقەدار",
                            PaintFinish.PEARL to "مرواری",
                            PaintFinish.CARBON to "کاربۆن",
                            PaintFinish.CHROME to "کڕۆم"
                        )

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            finishes.take(3).forEach { (finish, label) ->
                                val isSelected = activeCar.paintFinish == finish
                                Button(
                                    onClick = { onUpdatePaint(activeCar, activeCar.primaryColorHex, finish) },
                                    colors = ButtonDefaults.buttonColors(containerColor = if (isSelected) PrimaryBlue else SurfaceCard),
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text(label, fontSize = 12.sp, color = if (isSelected) Color.White else TextSecondary)
                                }
                            }
                        }
                    }
                    item {
                        val finishes = listOf(
                            PaintFinish.PEARL to "مرواری",
                            PaintFinish.CARBON to "کاربۆن",
                            PaintFinish.CHROME to "کڕۆم"
                        )
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            finishes.forEach { (finish, label) ->
                                val isSelected = activeCar.paintFinish == finish
                                Button(
                                    onClick = { onUpdatePaint(activeCar, activeCar.primaryColorHex, finish) },
                                    colors = ButtonDefaults.buttonColors(containerColor = if (isSelected) PrimaryBlue else SurfaceCard),
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text(label, fontSize = 12.sp, color = if (isSelected) Color.White else TextSecondary)
                                }
                            }
                        }
                    }
                }

                3 -> {
                    // Repair & Maintenance
                    item {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = SurfaceCard),
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(1.dp, SurfaceBorder, RoundedCornerShape(16.dp))
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.Healing, contentDescription = null, tint = StatusSuccess)
                                        Spacer(Modifier.width(8.dp))
                                        Text("پشکنین و چاککردنەوەی گشتی", fontWeight = FontWeight.Bold, color = TextPrimary)
                                    }
                                    Text("${activeCar.conditionPercent}%", fontWeight = FontWeight.Bold, color = StatusSuccess)
                                }

                                Spacer(Modifier.height(8.dp))
                                Text(
                                    "تەندروستی مۆتۆر، بۆدی، سووکان و تایەرەکان لە کاتی کێبڕکێ و درێفتی توند کەم دەبێتەوە. نوێکردنەوەی سەیارەکەت هێزی ڕاستەقینەی دەگەڕێنێتەوە.",
                                    fontSize = 12.sp,
                                    color = TextSecondary
                                )

                                Spacer(Modifier.height(14.dp))
                                val repairCost = ((100 - activeCar.conditionPercent) * 65)

                                Button(
                                    onClick = { onRepairCar(activeCar) },
                                    enabled = activeCar.conditionPercent < 100,
                                    colors = ButtonDefaults.buttonColors(containerColor = StatusSuccess),
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(
                                        if (activeCar.conditionPercent == 100) "سەیارەکە 100% تەندروستە" else "چاککردنەوەی هەموو زیانەکان ($repairCost IQD)",
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun UpgradeCard(
    title: String,
    description: String,
    cost: Int,
    isMaxed: Boolean,
    onUpgrade: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = SurfaceCard),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, SurfaceBorder, RoundedCornerShape(16.dp))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(title, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = TextPrimary)
                if (!isMaxed) {
                    Text("$cost IQD", fontWeight = FontWeight.Bold, color = GoldReward, fontSize = 13.sp)
                }
            }

            Spacer(Modifier.height(6.dp))
            Text(description, fontSize = 12.sp, color = TextSecondary)
            Spacer(Modifier.height(12.dp))

            Button(
                onClick = onUpgrade,
                enabled = !isMaxed,
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    if (isMaxed) "گەیشتووەتە بەرزترین ئاست (MAX)" else "نوێکردنەوە و بەرزکردنەوە",
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }
    }
}

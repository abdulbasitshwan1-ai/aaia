package com.example.ui.screens

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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.BasokaMemory
import com.example.ui.theme.AccentPurple
import com.example.ui.theme.ObsidianBg
import com.example.ui.theme.PrimaryBlue
import com.example.ui.theme.RacingRed
import com.example.ui.theme.StatusSuccess
import com.example.ui.theme.SurfaceBorder
import com.example.ui.theme.SurfaceCard
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun SettingsScreen(
    memories: List<BasokaMemory>,
    onAddMemory: (String, String, String) -> Unit,
    onDeleteMemory: (BasokaMemory) -> Unit,
    onClearAllMemories: () -> Unit
) {
    var showAddMemoryDialog by remember { mutableStateOf(false) }
    var memoryKey by remember { mutableStateOf("") }
    var memoryValue by remember { mutableStateOf("") }
    var showClearConfirm by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(ObsidianBg)
            .testTag("settings_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = "ڕێکخستنەکان و تایبەتمەندی",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = "بەڕێوەبردنی یادەوەرییەکان، ئاسایش و دۆخی کارکردن",
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary
            )
        }

        // Section: Memory Manager ("یادەوەرییەکانم")
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
                            Icon(Icons.Default.Psychology, contentDescription = null, tint = AccentPurple)
                            Spacer(Modifier.width(8.dp))
                            Text("یادەوەرییەکانم (Memory)", fontWeight = FontWeight.Bold, color = TextPrimary, fontSize = 15.sp)
                        }

                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Button(
                                onClick = { showAddMemoryDialog = true },
                                colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                            ) {
                                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(Modifier.width(4.dp))
                                Text("زیادکردن", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }

                            if (memories.isNotEmpty()) {
                                Button(
                                    onClick = { showClearConfirm = true },
                                    colors = ButtonDefaults.buttonColors(containerColor = RacingRed.copy(alpha = 0.2f)),
                                    shape = RoundedCornerShape(8.dp),
                                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Text("سڕینەوەی گشتی", fontSize = 11.sp, color = RacingRed, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }

                    Spacer(Modifier.height(10.dp))
                    Text(
                        "ئەم زانیارییانە یارمەتی BASOKA دەدەن تا لە دڵخوازەکانت تێبگات. دەتوانیت هەرکات بیەوێت بیسڕیتەوە.",
                        fontSize = 12.sp,
                        color = TextSecondary
                    )

                    Spacer(Modifier.height(12.dp))

                    if (memories.isEmpty()) {
                        Text("هیچ یادەوەرییەک تۆمار نەکراوە.", fontSize = 12.sp, color = TextMuted)
                    } else {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            memories.forEach { memory ->
                                Card(
                                    colors = CardDefaults.cardColors(containerColor = ObsidianBg),
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .border(1.dp, SurfaceBorder, RoundedCornerShape(10.dp))
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(horizontal = 12.dp, vertical = 8.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column {
                                            Text(memory.key, fontWeight = FontWeight.Bold, color = TextPrimary, fontSize = 13.sp)
                                            Text(memory.value, color = TextSecondary, fontSize = 12.sp)
                                        }

                                        IconButton(
                                            onClick = { onDeleteMemory(memory) },
                                            modifier = Modifier.size(24.dp)
                                        ) {
                                            Icon(Icons.Default.Delete, contentDescription = "Delete", tint = RacingRed.copy(alpha = 0.7f), modifier = Modifier.size(16.dp))
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Section: System & Security Config Cards
        item {
            SettingsInfoCard(
                title = "زمانی سەرەکی: کوردیی سۆرانی",
                description = "تەواوی ڕووکار، ئاگادارییەکان، وەڵام و شیکارییەکان بە زمانی کوردیی سۆرانیی ڕەسەنە.",
                icon = Icons.Default.Translate,
                accentColor = PrimaryBlue
            )
        }

        item {
            SettingsInfoCard(
                title = "ڕووکاری Dark Premium",
                description = "شێوازی تاریكی تایبەت بۆ پاراستنی چاو و خێرایی زۆر لە بەکارهێناندا.",
                icon = Icons.Default.Palette,
                accentColor = Color(0xFFFFB300)
            )
        }

        item {
            SettingsInfoCard(
                title = "ئاسایشی مایکرۆفۆن و دەنگ",
                description = "مایکرۆفۆن بە هیچ شێوەیەک بە خۆکاری ناکرێتەوە؛ دەنگ تەنها کاتێک کار دەکات کە خۆت داگیرسێنی.",
                icon = Icons.Default.MicOff,
                accentColor = StatusSuccess
            )
        }

        item {
            SettingsInfoCard(
                title = "پاراستنی پەسەندکردن (Confirmation)",
                description = "کردارە هەستیارەکان (وەک سڕینەوە و بەڕێوەبردنی داتا) هەمیشە پێویستیان بە ڕەزامەندی ڕاستەوخۆی تۆیە.",
                icon = Icons.Default.Security,
                accentColor = AccentPurple
            )
        }

        // About BASOKA
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = SurfaceCard),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, SurfaceBorder, RoundedCornerShape(16.dp))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("دەربارەی BASOKA", fontWeight = FontWeight.Bold, color = TextPrimary, fontSize = 15.sp)
                    Spacer(Modifier.height(6.dp))
                    Text(
                        "BASOKA یاریدەدەری زیرەکی دەستکردی کەسی و گەورەترین سیستەمی یاری سەیارەیە. دروستکراوە بۆ ئەوەی سادە بێت لە دەرەوە، و زۆر بەهێز لە ناوەوە.\nوەشانی 1.0.0 • هەموو مافەکان پارێزراون",
                        fontSize = 12.sp,
                        color = TextSecondary,
                        lineHeight = 18.sp
                    )
                }
            }
        }
    }

    if (showAddMemoryDialog) {
        AlertDialog(
            onDismissRequest = { showAddMemoryDialog = false },
            title = { Text("زیادکردنی یادەوەری نوێ", fontWeight = FontWeight.Bold, color = TextPrimary) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = memoryKey,
                        onValueChange = { memoryKey = it },
                        label = { Text("ناونیشان (بۆ نموونە: تیمی دڵخواز)") },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = memoryValue,
                        onValueChange = { memoryValue = it },
                        label = { Text("ناوەڕۆک (بۆ نموونە: یانەی ڕیاڵ مەدرید)") },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (memoryKey.isNotBlank() && memoryValue.isNotBlank()) {
                            onAddMemory(memoryKey, memoryValue, "تایبەت")
                            memoryKey = ""
                            memoryValue = ""
                            showAddMemoryDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
                ) {
                    Text("تۆمارکردن", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                Button(
                    onClick = { showAddMemoryDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = SurfaceBorder)
                ) {
                    Text("داخستن", color = TextSecondary)
                }
            },
            containerColor = SurfaceCard
        )
    }

    if (showClearConfirm) {
        AlertDialog(
            onDismissRequest = { showClearConfirm = false },
            title = { Text("سڕینەوەی تەواوی یادەوەرییەکان؟", fontWeight = FontWeight.Bold, color = RacingRed) },
            text = {
                Text(
                    "دڵنیایت دەتەوێت هەموو یادەوەرییە هەڵگیراوەکان بسڕیتەوە؟ ئەم کردارە ناگەڕێتەوە.",
                    color = TextSecondary,
                    fontSize = 13.sp
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        onClearAllMemories()
                        showClearConfirm = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = RacingRed)
                ) {
                    Text("بەڵێ، بیسڕەوە", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                Button(
                    onClick = { showClearConfirm = false },
                    colors = ButtonDefaults.buttonColors(containerColor = SurfaceBorder)
                ) {
                    Text("پاشگەزبوونەوە", color = TextSecondary)
                }
            },
            containerColor = SurfaceCard
        )
    }
}

@Composable
private fun SettingsInfoCard(
    title: String,
    description: String,
    icon: ImageVector,
    accentColor: Color
) {
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
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(accentColor.copy(alpha = 0.15f), RoundedCornerShape(10.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = accentColor, modifier = Modifier.size(22.dp))
            }

            Spacer(Modifier.width(14.dp))

            Column {
                Text(title, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = TextPrimary)
                Spacer(Modifier.height(2.dp))
                Text(description, fontSize = 11.sp, color = TextSecondary, lineHeight = 16.sp)
            }
        }
    }
}

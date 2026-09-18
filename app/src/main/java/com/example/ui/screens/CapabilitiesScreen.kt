package com.example.ui.screens

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
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
import com.example.ui.theme.AccentCyan
import com.example.ui.theme.AccentPurple
import com.example.ui.theme.GoldReward
import com.example.ui.theme.ObsidianBg
import com.example.ui.theme.PrimaryBlue
import com.example.ui.theme.StatusSuccess
import com.example.ui.theme.SurfaceBorder
import com.example.ui.theme.SurfaceCard
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

data class CapabilityItem(
    val title: String,
    val description: String,
    val samplePrompt: String,
    val icon: ImageVector,
    val category: String,
    val color: Color
)

@Composable
fun CapabilitiesScreen(
    onTriggerCapability: (String) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }

    val allCapabilities = remember {
        listOf(
            CapabilityItem(
                title = "بیرخستنەوە و زەنگی زیرەک",
                description = "دیاریکردنی زەنگ و یادەوەری بە تێگەیشتن لە کات (بەیانی، ئێوارە، سبەی)",
                samplePrompt = "سبەی لە ٨ بیرم بخەرەوە پەیوەندی بکەم",
                icon = Icons.Default.Alarm,
                category = "ژیریی ڕۆژانە",
                color = PrimaryBlue
            ),
            CapabilityItem(
                title = "ئامادەکردنی سەیارە بۆ پێشبڕکێ",
                description = "کۆنترۆڵی سیستەمی سەیارە، تەعدیلاتی مۆتۆر، تۆربۆ و ڕێکخستنی Race",
                samplePrompt = "سەیارەکەم بۆ race ئامادە بکە",
                icon = Icons.Default.DirectionsCar,
                category = "سەیارە و گەراج",
                color = Color(0xFFFF5722)
            ),
            CapabilityItem(
                title = "کۆمەڵەی تایبەت بە درێفت (Drift Setup)",
                description = "ڕێکخستنی هایدرۆلیک، زاویەی تایەر/Camber و گێڕی دەستی بۆ درێفتی پرۆفیشناڵ",
                samplePrompt = "سەیارەکەم بۆ drift ئامادە بکە",
                icon = Icons.Default.Build,
                category = "سەیارە و گەراج",
                color = GoldReward
            ),
            CapabilityItem(
                title = "پوختەکردنی دەق و پەڕاو",
                description = "کورتکردنەوەی وتار و فایلە درێژەکان لە چەند خاڵێکی پوخت و پاراو",
                samplePrompt = "ئەم بابەتەم بۆ کورت و پوخت بکەرەوە",
                icon = Icons.Default.EditNote,
                category = "شیکاری و نووسین",
                color = AccentCyan
            ),
            CapabilityItem(
                title = "پلانی خوێندن و فێرکاری",
                description = "دروستکردنی خشتەی سەعی و تاقیکردنەوە و کورتەی وانەکان بە خولەک",
                samplePrompt = "پلانی خوێندنی تاقیکردنەوەم بۆ دابنێ",
                icon = Icons.Default.School,
                category = "فێرکاری",
                color = AccentPurple
            ),
            CapabilityItem(
                title = "کۆد و شیکاری هەڵە (Android/Kotlin)",
                description = "نووسینی کۆدی Jetpack Compose و شیکردنەوەی هەڵە و Bug بە شێوەی دروست",
                samplePrompt = "کۆدێکی ئەندرۆید بە Kotlin Compose بۆ بنووسە",
                icon = Icons.Default.Code,
                category = "پرۆگرامسازی",
                color = StatusSuccess
            ),
            CapabilityItem(
                title = "ئۆتۆماتیک و فەرمانی مۆبایل",
                description = "دەستگەیشتن بە فلاش، کاتژمێر، کۆبوونەوە و ڕێکخستن بەبێ ئاڵۆزی",
                samplePrompt = "فلاش بکەرەوە",
                icon = Icons.Default.Sync,
                category = "ئامێر و ئۆتۆماتیک",
                color = PrimaryBlue
            ),
            CapabilityItem(
                title = "داڕشتنی ئیمەیل و نامەی فەرمی",
                description = "نووسینی داواکاری، ئیمەیلی فەرمی و دەقی پڕۆفیشناڵ بە شێوازی جوان",
                samplePrompt = "ئیمەیلێکی فەرمی داواکاری کارم بۆ بنووسە",
                icon = Icons.Default.AutoAwesome,
                category = "شیکاری و نووسین",
                color = Color(0xFFFF4081)
            )
        )
    }

    val filteredList = if (searchQuery.isBlank()) {
        allCapabilities
    } else {
        allCapabilities.filter {
            it.title.contains(searchQuery, ignoreCase = true) ||
            it.description.contains(searchQuery, ignoreCase = true) ||
            it.category.contains(searchQuery, ignoreCase = true)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ObsidianBg)
            .testTag("capabilities_screen")
    ) {
        // Header
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "تواناکانی BASOKA (1000+ Feature)",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = "هەموو کردارەکان لێرە بە شێوازی پۆلێنکراو ئامادەن؛ بە کلیکێک لە چات جێبەجێ دەبن.",
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary
            )

            Spacer(Modifier.height(14.dp))

            // Search Bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("گەڕان لە نێوان تواناکاندا...", fontSize = 13.sp, color = TextMuted) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = TextMuted) },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("capabilities_search_field"),
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = SurfaceCard,
                    unfocusedContainerColor = SurfaceCard,
                    focusedBorderColor = PrimaryBlue,
                    unfocusedBorderColor = SurfaceBorder,
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary
                ),
                singleLine = true
            )
        }

        // Capabilities List
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(filteredList) { item ->
                Card(
                    colors = CardDefaults.cardColors(containerColor = SurfaceCard),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, SurfaceBorder, RoundedCornerShape(16.dp))
                        .clickable { onTriggerCapability(item.samplePrompt) }
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .background(item.color.copy(alpha = 0.15f), RoundedCornerShape(12.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(item.icon, contentDescription = null, tint = item.color, modifier = Modifier.size(24.dp))
                        }

                        Spacer(Modifier.width(14.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text(
                                    text = item.title,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp,
                                    color = TextPrimary
                                )
                                Card(
                                    colors = CardDefaults.cardColors(containerColor = item.color.copy(alpha = 0.15f)),
                                    shape = RoundedCornerShape(6.dp)
                                ) {
                                    Text(
                                        text = item.category,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = item.color,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                            Spacer(Modifier.height(4.dp))
                            Text(
                                text = item.description,
                                fontSize = 12.sp,
                                color = TextSecondary,
                                lineHeight = 18.sp
                            )
                        }

                        Spacer(Modifier.width(8.dp))
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = "Run",
                            tint = PrimaryBlue,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }
    }
}

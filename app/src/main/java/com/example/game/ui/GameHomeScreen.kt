package com.example.game.ui

import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.LocalGasStation
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.game.GameCar
import com.example.data.model.game.GamePlayerProfile
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
import com.example.ui.viewmodel.GameScreenTab

@Composable
fun GameHomeScreen(
    profile: GamePlayerProfile?,
    activeCar: GameCar?,
    onNavigate: (GameScreenTab) -> Unit,
    onBackToAssistant: () -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(ObsidianBg)
            .testTag("game_home_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Top Bar: Back to AI Assistant & Economy Status
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    onClick = onBackToAssistant,
                    colors = ButtonDefaults.buttonColors(containerColor = SurfaceCard),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.testTag("back_to_assistant_btn")
                ) {
                    Text("← گەڕانەوە بۆ BASOKA", color = PrimaryBlue, fontWeight = FontWeight.Bold)
                }

                // Economy Badges
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
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

                    Card(
                        colors = CardDefaults.cardColors(containerColor = SurfaceCard),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.EmojiEvents, contentDescription = null, tint = PrimaryBlue, modifier = Modifier.size(16.dp))
                            Spacer(Modifier.width(4.dp))
                            Text("${profile?.reputation ?: 0} Rep", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }
                    }
                }
            }
        }

        // Active Car Hero Showcase Card
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = SurfaceDark),
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, SurfaceBorder, RoundedCornerShape(20.dp))
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = activeCar?.name ?: "BASOKA Hawk GT",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Text(
                                text = "${activeCar?.brand ?: "BASOKA"} • مۆدێلی ${activeCar?.modelYear ?: 2024}",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextSecondary
                            )
                        }

                        Box(
                            modifier = Modifier
                                .background(Color(activeCar?.primaryColorHex ?: 0xFF1E88E5), CircleShape)
                                .size(24.dp)
                                .border(2.dp, Color.White.copy(alpha = 0.4f), CircleShape)
                        )
                    }

                    Spacer(Modifier.height(14.dp))

                    // Car Canvas Graphic Representation
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(130.dp)
                            .background(
                                Brush.verticalGradient(
                                    listOf(Color(0xFF161A26), Color(0xFF0F121C))
                                ),
                                RoundedCornerShape(14.dp)
                            )
                            .border(1.dp, SurfaceBorder.copy(alpha = 0.5f), RoundedCornerShape(14.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            val carColor = Color(activeCar?.primaryColorHex ?: 0xFF1E88E5)
                            val w = size.width
                            val h = size.height

                            // Ground shadow
                            drawOval(
                                color = Color.Black.copy(alpha = 0.5f),
                                topLeft = Offset(w * 0.15f, h * 0.72f),
                                size = Size(w * 0.7f, 22.dp.toPx())
                            )

                            // Car Body
                            drawRoundRect(
                                color = carColor,
                                topLeft = Offset(w * 0.2f, h * 0.45f),
                                size = Size(w * 0.6f, h * 0.3f),
                                cornerRadius = CornerRadius(14.dp.toPx(), 14.dp.toPx())
                            )

                            // Cabin / Windows
                            drawRoundRect(
                                color = Color(0xFF10141D),
                                topLeft = Offset(w * 0.32f, h * 0.25f),
                                size = Size(w * 0.36f, h * 0.26f),
                                cornerRadius = CornerRadius(10.dp.toPx(), 10.dp.toPx())
                            )

                            // Windshield highlight
                            drawRoundRect(
                                color = Color.White.copy(alpha = 0.25f),
                                topLeft = Offset(w * 0.35f, h * 0.27f),
                                size = Size(w * 0.12f, h * 0.2f),
                                cornerRadius = CornerRadius(6.dp.toPx(), 6.dp.toPx())
                            )

                            // Wheels
                            drawCircle(
                                color = Color(0xFF1A1A1A),
                                radius = 18.dp.toPx(),
                                center = Offset(w * 0.30f, h * 0.75f)
                            )
                            drawCircle(
                                color = Color(0xFF888888),
                                radius = 9.dp.toPx(),
                                center = Offset(w * 0.30f, h * 0.75f)
                            )
                            drawCircle(
                                color = Color(0xFF1A1A1A),
                                radius = 18.dp.toPx(),
                                center = Offset(w * 0.70f, h * 0.75f)
                            )
                            drawCircle(
                                color = Color(0xFF888888),
                                radius = 9.dp.toPx(),
                                center = Offset(w * 0.70f, h * 0.75f)
                            )

                            // Headlight glow
                            drawCircle(
                                color = Color(0xFF00E5FF),
                                radius = 5.dp.toPx(),
                                center = Offset(w * 0.78f, h * 0.52f)
                            )
                            // Taillight
                            drawCircle(
                                color = RacingRed,
                                radius = 5.dp.toPx(),
                                center = Offset(w * 0.22f, h * 0.52f)
                            )
                        }
                    }

                    Spacer(Modifier.height(14.dp))

                    // Car Telemetry Preview
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        StatBadge(icon = Icons.Default.Speed, label = "هێزی ئەسپ", value = "${activeCar?.currentHp ?: 280} HP")
                        StatBadge(icon = Icons.Default.Speed, label = "خێرایی لوتکە", value = "${activeCar?.currentTopSpeed ?: 245} کلم/ک")
                        StatBadge(icon = Icons.Default.Speed, label = "0-100 خێرابوون", value = "${String.format("%.1f", activeCar?.currentAcceleration ?: 5.2f)} چرکە")
                        StatBadge(icon = Icons.Default.LocalGasStation, label = "باری تەندروستی", value = "${activeCar?.conditionPercent ?: 100}%")
                    }

                    Spacer(Modifier.height(16.dp))

                    // Launch Drive Button
                    Button(
                        onClick = { onNavigate(GameScreenTab.DRIVE) },
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .testTag("launch_drive_btn")
                    ) {
                        Icon(Icons.Default.PlayArrow, contentDescription = null, tint = Color.White)
                        Spacer(Modifier.width(8.dp))
                        Text("دەستپێکردنی لێخوڕین لە BASOKA CAR WORLD", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = Color.White)
                    }
                }
            }
        }

        // Section: Game Menu Grid
        item {
            Text(
                text = "بەشەکانی یاری",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                GameMenuCard(
                    title = "گەراج و مۆتۆر",
                    subtitle = "تەعدیلات، تۆربۆ و ڕەنگ",
                    icon = Icons.Default.Build,
                    accentColor = Color(0xFFFF9800),
                    modifier = Modifier.weight(1f),
                    onClick = { onNavigate(GameScreenTab.GARAGE) }
                )

                GameMenuCard(
                    title = "بازاڕی سەیارە",
                    subtitle = "کڕین و فرۆشتنی کریدت",
                    icon = Icons.Default.ShoppingCart,
                    accentColor = StatusSuccess,
                    modifier = Modifier.weight(1f),
                    onClick = { onNavigate(GameScreenTab.MARKET) }
                )
            }
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                GameMenuCard(
                    title = "ئەرک و پێشبڕکێ",
                    subtitle = "تەحەدا و بەدەستهێنانی پارە",
                    icon = Icons.Default.EmojiEvents,
                    accentColor = GoldReward,
                    modifier = Modifier.weight(1f),
                    onClick = { onNavigate(GameScreenTab.MISSIONS) }
                )

                GameMenuCard(
                    title = "پێشانگای سەیارەکان",
                    subtitle = "${profile?.garageCapacity ?: 6} سەیارەی خاوەندارێتی",
                    icon = Icons.Default.DirectionsCar,
                    accentColor = PrimaryBlue,
                    modifier = Modifier.weight(1f),
                    onClick = { onNavigate(GameScreenTab.COLLECTION) }
                )
            }
        }
    }
}

@Composable
private fun StatBadge(icon: androidx.compose.ui.graphics.vector.ImageVector, label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = label, style = MaterialTheme.typography.bodySmall, color = TextMuted, fontSize = 11.sp)
        Spacer(Modifier.height(2.dp))
        Text(text = value, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, color = TextPrimary, fontSize = 12.sp)
    }
}

@Composable
private fun GameMenuCard(
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    accentColor: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = SurfaceCard),
        shape = RoundedCornerShape(16.dp),
        modifier = modifier
            .border(1.dp, SurfaceBorder, RoundedCornerShape(16.dp))
            .clickable { onClick() }
    ) {
        Column(
            modifier = Modifier.padding(14.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(accentColor.copy(alpha = 0.15f), RoundedCornerShape(10.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = accentColor, modifier = Modifier.size(22.dp))
            }
            Spacer(Modifier.height(10.dp))
            Text(title, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = TextPrimary)
            Spacer(Modifier.height(2.dp))
            Text(subtitle, fontSize = 11.sp, color = TextSecondary, maxLines = 1)
        }
    }
}

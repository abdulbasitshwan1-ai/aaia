package com.example.game.ui

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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.game.CarRarity
import com.example.data.model.game.GameCar
import com.example.data.model.game.GamePlayerProfile
import com.example.ui.theme.GoldReward
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
fun GameMarketScreen(
    allCars: List<GameCar>,
    profile: GamePlayerProfile?,
    onBuyCar: (GameCar) -> Unit,
    onBack: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ObsidianBg)
            .testTag("game_market_screen")
    ) {
        // Header
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
                    Text("بازاڕی سەیارەی BASOKA", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = TextPrimary)
                    Text("کڕین و فرۆشتنی مۆدێلە جیاوازەکان", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
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

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            items(allCars) { car ->
                val rarityColor = when (car.rarity) {
                    CarRarity.COMMON -> TextSecondary
                    CarRarity.RARE -> PrimaryBlue
                    CarRarity.VERY_RARE -> Color(0xFFAB47BC)
                    CarRarity.SPECIAL -> GoldReward
                    CarRarity.LEGENDARY -> RacingRed
                }

                val rarityName = when (car.rarity) {
                    CarRarity.COMMON -> "ئاسایی"
                    CarRarity.RARE -> "دەگمەن"
                    CarRarity.VERY_RARE -> "زۆر دەگمەن"
                    CarRarity.SPECIAL -> "تایبەت / کلاسیک"
                    CarRarity.LEGENDARY -> "ئەفسانەیی / Hyper"
                }

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
                            Column {
                                Text(car.name, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = TextPrimary)
                                Text("${car.brand} • ${car.modelYear}", fontSize = 12.sp, color = TextSecondary)
                            }

                            Card(
                                colors = CardDefaults.cardColors(containerColor = rarityColor.copy(alpha = 0.15f)),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(
                                    text = rarityName,
                                    color = rarityColor,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }

                        Spacer(Modifier.height(12.dp))

                        // Specs Row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Speed, contentDescription = null, tint = TextMuted, modifier = Modifier.size(16.dp))
                                Spacer(Modifier.width(4.dp))
                                Text("${car.baseHp} HP", fontSize = 12.sp, color = TextPrimary, fontWeight = FontWeight.Bold)
                            }
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Speed, contentDescription = null, tint = TextMuted, modifier = Modifier.size(16.dp))
                                Spacer(Modifier.width(4.dp))
                                Text("${car.baseTopSpeed} کلم/ک", fontSize = 12.sp, color = TextPrimary, fontWeight = FontWeight.Bold)
                            }
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.DirectionsCar, contentDescription = null, tint = TextMuted, modifier = Modifier.size(16.dp))
                                Spacer(Modifier.width(4.dp))
                                Text("${String.format("%.1f", car.baseAcceleration)}s", fontSize = 12.sp, color = TextPrimary, fontWeight = FontWeight.Bold)
                            }
                            Text(
                                text = "${car.mileageKm} KM",
                                fontSize = 12.sp,
                                color = TextMuted
                            )
                        }

                        Spacer(Modifier.height(14.dp))

                        // Purchase / Status Row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "${car.price} IQD",
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 16.sp,
                                color = GoldReward
                            )

                            if (car.isOwned) {
                                Button(
                                    onClick = {},
                                    enabled = false,
                                    colors = ButtonDefaults.buttonColors(disabledContainerColor = SurfaceBorder),
                                    shape = RoundedCornerShape(10.dp)
                                ) {
                                    Text("خاوەنداری لێ دەکەیت ✓", color = StatusSuccess, fontWeight = FontWeight.Bold)
                                }
                            } else {
                                val canAfford = (profile?.money ?: 0) >= car.price
                                Button(
                                    onClick = { onBuyCar(car) },
                                    enabled = canAfford,
                                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                                    shape = RoundedCornerShape(10.dp)
                                ) {
                                    Icon(Icons.Default.ShoppingCart, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(Modifier.width(6.dp))
                                    Text(if (canAfford) "کڕینی سەیارە" else "پارە بەس نییە", fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

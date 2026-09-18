package com.example.game.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.VolumeDown
import androidx.compose.material.icons.filled.VolumeMute
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.game.GameCar
import com.example.game.engine.CameraView
import com.example.game.engine.CityZone
import com.example.game.engine.TelemetryData
import com.example.game.engine.WeatherCondition
import com.example.ui.theme.AsphaltGray
import com.example.ui.theme.GoldReward
import com.example.ui.theme.NitroCyan
import com.example.ui.theme.ObsidianBg
import com.example.ui.theme.PrimaryBlue
import com.example.ui.theme.RacingRed
import com.example.ui.theme.StatusSuccess
import com.example.ui.theme.SurfaceBorder
import com.example.ui.theme.SurfaceCard
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import kotlin.math.sin

@Composable
fun GameDriveScreen(
    activeCar: GameCar?,
    telemetry: TelemetryData,
    soundEnabled: Boolean,
    onInputsChanged: (throttle: Float, brake: Float, steering: Float, handbrake: Boolean, nitro: Boolean) -> Unit,
    onCycleWeather: () -> Unit,
    onCycleCamera: () -> Unit,
    onSetZone: (CityZone) -> Unit,
    onToggleSound: () -> Unit,
    onExitDrive: () -> Unit
) {
    var throttle by remember { mutableFloatStateOf(0f) }
    var brake by remember { mutableFloatStateOf(0f) }
    var steering by remember { mutableFloatStateOf(0f) }
    var handbrake by remember { mutableStateOf(false) }
    var nitro by remember { mutableStateOf(false) }

    fun updateInputs() {
        onInputsChanged(throttle, brake, steering, handbrake, nitro)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(ObsidianBg)
            .testTag("game_drive_screen")
    ) {
        // Road and Car 2D Physics Canvas
        Canvas(modifier = Modifier.fillMaxSize()) {
            val canvasW = size.width
            val canvasH = size.height

            // Background terrain based on zone and weather
            val groundColor = when (telemetry.weather) {
                WeatherCondition.NIGHT -> Color(0xFF090A0F)
                WeatherCondition.RAIN -> Color(0xFF141A24)
                WeatherCondition.FOG -> Color(0xFF1C222B)
                WeatherCondition.SUNNY -> Color(0xFF1A1F2B)
            }
            drawRect(color = groundColor)

            // Dynamic moving road perspective
            val roadWidth = canvasW * 0.72f
            val roadLeft = (canvasW - roadWidth) / 2f
            drawRect(
                color = AsphaltGray,
                topLeft = Offset(roadLeft, 0f),
                size = Size(roadWidth, canvasH)
            )

            // Road borders
            drawRect(
                color = Color.White,
                topLeft = Offset(roadLeft, 0f),
                size = Size(6.dp.toPx(), canvasH)
            )
            drawRect(
                color = Color.White,
                topLeft = Offset(roadLeft + roadWidth - 6.dp.toPx(), 0f),
                size = Size(6.dp.toPx(), canvasH)
            )

            // Road center dashes moving downward based on speed
            val dashOffset = (telemetry.posY * 3f) % 80f
            var dashY = -80f + dashOffset
            while (dashY < canvasH) {
                drawRect(
                    color = Color(0xFFFFD54F),
                    topLeft = Offset(canvasW / 2f - 3.dp.toPx(), dashY),
                    size = Size(6.dp.toPx(), 36.dp.toPx())
                )
                dashY += 75.dp.toPx()
            }

            // Tire skid marks when drifting
            if (telemetry.isDrifting) {
                val skidX = canvasW / 2f + (telemetry.steeringAngle * 2.2f)
                val skidY = canvasH * 0.65f
                drawCircle(
                    color = Color.Black.copy(alpha = 0.45f),
                    radius = 24.dp.toPx(),
                    center = Offset(skidX, skidY)
                )
            }

            // Car rendering (Top-down/Third-person)
            val carCenter = Offset(canvasW / 2f + (telemetry.steeringAngle * 2.5f), canvasH * 0.62f)
            val carColor = Color(activeCar?.primaryColorHex ?: 0xFF1E88E5)
            val carW = 60.dp.toPx()
            val carL = 110.dp.toPx()

            rotate(
                degrees = telemetry.steeringAngle * 0.75f,
                pivot = carCenter
            ) {
                // Car shadow
                drawRoundRect(
                    color = Color.Black.copy(alpha = 0.5f),
                    topLeft = Offset(carCenter.x - carW / 2f + 4f, carCenter.y - carL / 2f + 8f),
                    size = Size(carW, carL),
                    cornerRadius = CornerRadius(14.dp.toPx(), 14.dp.toPx())
                )

                // Car Body
                drawRoundRect(
                    color = carColor,
                    topLeft = Offset(carCenter.x - carW / 2f, carCenter.y - carL / 2f),
                    size = Size(carW, carL),
                    cornerRadius = CornerRadius(12.dp.toPx(), 12.dp.toPx())
                )

                // Roof / Windshield
                drawRoundRect(
                    color = Color(0xFF151922),
                    topLeft = Offset(carCenter.x - carW * 0.38f, carCenter.y - carL * 0.28f),
                    size = Size(carW * 0.76f, carL * 0.52f),
                    cornerRadius = CornerRadius(8.dp.toPx(), 8.dp.toPx())
                )

                // Headlights
                drawCircle(
                    color = if (telemetry.weather == WeatherCondition.NIGHT) Color(0xFF80D8FF) else Color(0xFFE0F7FA),
                    radius = 6.dp.toPx(),
                    center = Offset(carCenter.x - carW * 0.35f, carCenter.y - carL / 2f + 6.dp.toPx())
                )
                drawCircle(
                    color = if (telemetry.weather == WeatherCondition.NIGHT) Color(0xFF80D8FF) else Color(0xFFE0F7FA),
                    radius = 6.dp.toPx(),
                    center = Offset(carCenter.x + carW * 0.35f, carCenter.y - carL / 2f + 6.dp.toPx())
                )

                // Taillights
                val tailColor = if (telemetry.brake > 0.1f) Color(0xFFFF1744) else Color(0xFFB71C1C)
                drawCircle(
                    color = tailColor,
                    radius = 5.dp.toPx(),
                    center = Offset(carCenter.x - carW * 0.35f, carCenter.y + carL / 2f - 6.dp.toPx())
                )
                drawCircle(
                    color = tailColor,
                    radius = 5.dp.toPx(),
                    center = Offset(carCenter.x + carW * 0.35f, carCenter.y + carL / 2f - 6.dp.toPx())
                )

                // Nitro exhaust flame
                if (telemetry.isNitroActive) {
                    drawOval(
                        color = NitroCyan,
                        topLeft = Offset(carCenter.x - 8.dp.toPx(), carCenter.y + carL / 2f),
                        size = Size(16.dp.toPx(), 26.dp.toPx())
                    )
                }
            }

            // Weather effects on screen
            if (telemetry.weather == WeatherCondition.RAIN) {
                // Rain drops
                for (i in 0..40) {
                    val rx = (canvasW * ((i * 37) % 100) / 100f)
                    val ry = ((telemetry.posY * 5f + i * 45) % canvasH)
                    drawLine(
                        color = Color.White.copy(alpha = 0.35f),
                        start = Offset(rx, ry),
                        end = Offset(rx - 8f, ry + 24f),
                        strokeWidth = 2f
                    )
                }
            }
        }

        // Top Control Overlay: Back, Weather, Camera, Sound, Zone
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
                .align(Alignment.TopCenter)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onExitDrive,
                    modifier = Modifier
                        .background(SurfaceCard.copy(alpha = 0.85f), CircleShape)
                        .testTag("exit_drive_btn")
                ) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Exit", tint = TextPrimary)
                }

                // Zone Badge
                Card(
                    colors = CardDefaults.cardColors(containerColor = SurfaceCard.copy(alpha = 0.85f)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.LocationOn, contentDescription = null, tint = PrimaryBlue, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(4.dp))
                        Text(telemetry.zone.labelKurdish, color = TextPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }

                // Quick Mode Toggles
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    IconButton(
                        onClick = onCycleWeather,
                        modifier = Modifier.background(SurfaceCard.copy(alpha = 0.85f), CircleShape)
                    ) {
                        Icon(Icons.Default.WbSunny, contentDescription = "Weather", tint = GoldReward, modifier = Modifier.size(20.dp))
                    }
                    IconButton(
                        onClick = onCycleCamera,
                        modifier = Modifier.background(SurfaceCard.copy(alpha = 0.85f), CircleShape)
                    ) {
                        Icon(Icons.Default.CameraAlt, contentDescription = "Camera", tint = PrimaryBlue, modifier = Modifier.size(20.dp))
                    }
                    IconButton(
                        onClick = onToggleSound,
                        modifier = Modifier.background(SurfaceCard.copy(alpha = 0.85f), CircleShape)
                    ) {
                        Icon(
                            if (soundEnabled) Icons.Default.VolumeDown else Icons.Default.VolumeMute,
                            contentDescription = "Sound",
                            tint = if (soundEnabled) StatusSuccess else TextMuted,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            Spacer(Modifier.height(8.dp))

            // Cockpit Telemetry Hub (Speedometer, RPM, Drift, Nitro)
            Card(
                colors = CardDefaults.cardColors(containerColor = SurfaceCard.copy(alpha = 0.90f)),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, SurfaceBorder, RoundedCornerShape(16.dp))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Speed Display
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "${telemetry.speedKmh.toInt()}",
                            fontSize = 32.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = if (telemetry.speedKmh > 200) RacingRed else TextPrimary
                        )
                        Text("کلم/کاتژمێر", fontSize = 10.sp, color = TextMuted)
                    }

                    // Gear & RPM
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "گێڕ ${telemetry.gear}",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = PrimaryBlue
                        )
                        Text(
                            text = "${telemetry.rpm} RPM",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = if (telemetry.rpm > 6800) RacingRed else TextSecondary
                        )
                    }

                    // Drift Points & Multiplier
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        if (telemetry.isDrifting || telemetry.currentDriftPoints > 0) {
                            Text(
                                text = "DRIFT: +${telemetry.currentDriftPoints}",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = GoldReward
                            )
                            Text(
                                text = "x${String.format("%.1f", telemetry.driftMultiplier)}",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = RacingRed
                            )
                        } else {
                            Text(
                                text = "خاڵی درێفت",
                                fontSize = 11.sp,
                                color = TextMuted
                            )
                            Text(
                                text = "${telemetry.totalDriftScore}",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                        }
                    }

                    // Nitro Gauge
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            Icons.Default.LocalFireDepartment,
                            contentDescription = null,
                            tint = if (telemetry.isNitroActive) NitroCyan else PrimaryBlue,
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = "${telemetry.nitroPercent.toInt()}%",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = NitroCyan
                        )
                    }
                }
            }
        }

        // Bottom Driving Controls: Steering (Left/Right) & Pedals (Gas, Brake, Handbrake, Nitro)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .align(Alignment.BottomCenter),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Bottom
        ) {
            // Steering Controls (Left & Right Buttons)
            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Steer Left Button
                Box(
                    modifier = Modifier
                        .size(68.dp)
                        .background(SurfaceCard.copy(alpha = 0.85f), CircleShape)
                        .border(2.dp, if (steering < -0.1f) PrimaryBlue else SurfaceBorder, CircleShape)
                        .pointerInput(Unit) {
                            detectTapGestures(
                                onPress = {
                                    steering = -1f
                                    updateInputs()
                                    tryAwaitRelease()
                                    steering = 0f
                                    updateInputs()
                                }
                            )
                        }
                        .testTag("steer_left_btn"),
                    contentAlignment = Alignment.Center
                ) {
                    Text("◀", fontSize = 24.sp, color = TextPrimary, fontWeight = FontWeight.Bold)
                }

                // Steer Right Button
                Box(
                    modifier = Modifier
                        .size(68.dp)
                        .background(SurfaceCard.copy(alpha = 0.85f), CircleShape)
                        .border(2.dp, if (steering > 0.1f) PrimaryBlue else SurfaceBorder, CircleShape)
                        .pointerInput(Unit) {
                            detectTapGestures(
                                onPress = {
                                    steering = 1f
                                    updateInputs()
                                    tryAwaitRelease()
                                    steering = 0f
                                    updateInputs()
                                }
                            )
                        }
                        .testTag("steer_right_btn"),
                    contentAlignment = Alignment.Center
                ) {
                    Text("▶", fontSize = 24.sp, color = TextPrimary, fontWeight = FontWeight.Bold)
                }
            }

            // Central Actions: Handbrake (Drift) and Nitro
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Nitro Button
                Box(
                    modifier = Modifier
                        .width(76.dp)
                        .height(44.dp)
                        .background(
                            if (nitro) NitroCyan else SurfaceCard.copy(alpha = 0.85f),
                            RoundedCornerShape(12.dp)
                        )
                        .border(1.dp, NitroCyan, RoundedCornerShape(12.dp))
                        .pointerInput(Unit) {
                            detectTapGestures(
                                onPress = {
                                    nitro = true
                                    updateInputs()
                                    tryAwaitRelease()
                                    nitro = false
                                    updateInputs()
                                }
                            )
                        }
                        .testTag("nitro_btn"),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "NITRO",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = if (nitro) Color.Black else NitroCyan
                    )
                }

                // Handbrake (Drift) Button
                Box(
                    modifier = Modifier
                        .width(76.dp)
                        .height(44.dp)
                        .background(
                            if (handbrake) RacingRed else SurfaceCard.copy(alpha = 0.85f),
                            RoundedCornerShape(12.dp)
                        )
                        .border(1.dp, RacingRed, RoundedCornerShape(12.dp))
                        .pointerInput(Unit) {
                            detectTapGestures(
                                onPress = {
                                    handbrake = true
                                    updateInputs()
                                    tryAwaitRelease()
                                    handbrake = false
                                    updateInputs()
                                }
                            )
                        }
                        .testTag("handbrake_btn"),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "DRIFT",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = if (handbrake) Color.White else RacingRed
                    )
                }
            }

            // Pedals: Brake & Gas (Accelerator)
            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.Bottom
            ) {
                // Brake Pedal
                Box(
                    modifier = Modifier
                        .width(54.dp)
                        .height(84.dp)
                        .background(
                            if (brake > 0.1f) RacingRed.copy(alpha = 0.8f) else SurfaceCard.copy(alpha = 0.85f),
                            RoundedCornerShape(14.dp)
                        )
                        .border(2.dp, RacingRed, RoundedCornerShape(14.dp))
                        .pointerInput(Unit) {
                            detectTapGestures(
                                onPress = {
                                    brake = 1f
                                    updateInputs()
                                    tryAwaitRelease()
                                    brake = 0f
                                    updateInputs()
                                }
                            )
                        }
                        .testTag("brake_pedal"),
                    contentAlignment = Alignment.Center
                ) {
                    Text("BRAKE", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                }

                // Gas Pedal (Accelerator)
                Box(
                    modifier = Modifier
                        .width(62.dp)
                        .height(106.dp)
                        .background(
                            if (throttle > 0.1f) StatusSuccess.copy(alpha = 0.8f) else SurfaceCard.copy(alpha = 0.85f),
                            RoundedCornerShape(14.dp)
                        )
                        .border(2.dp, StatusSuccess, RoundedCornerShape(14.dp))
                        .pointerInput(Unit) {
                            detectTapGestures(
                                onPress = {
                                    throttle = 1f
                                    updateInputs()
                                    tryAwaitRelease()
                                    throttle = 0f
                                    updateInputs()
                                }
                            )
                        }
                        .testTag("gas_pedal"),
                    contentAlignment = Alignment.Center
                ) {
                    Text("GAS", fontSize = 14.sp, fontWeight = FontWeight.ExtraBold, color = TextPrimary)
                }
            }
        }
    }
}

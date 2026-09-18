package com.example.game.engine

import com.example.data.model.game.GameCar
import com.example.data.model.game.SetupPreset
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sin

enum class WeatherCondition(val labelKurdish: String) {
    SUNNY("هەتاو و وشک"),
    RAIN("باراناوی و خلیسک"),
    NIGHT("شەوی شار بە چرای نیۆن"),
    FOG("تەماوی و کەمبینین")
}

enum class CameraView(val labelKurdish: String) {
    THIRD_PERSON("دواوەی سەیارە"),
    COCKPIT("ناوەوەی دەشبۆرد"),
    TOP_DOWN("سەروو / نەخشە")
}

enum class CityZone(val labelKurdish: String) {
    DOWNTOWN("شەقامی سەرەکی بازرگانی"),
    MOUNTAIN_PASS("شاخی درێفت و پێچەکان"),
    HIGHWAY("جادەی خێرای دەرەوەی شار"),
    INDUSTRIAL_ZONE("ناوچەی پیشەسازی و گەراجەکان")
}

data class TelemetryData(
    val speedKmh: Float = 0f,
    val rpm: Int = 800,
    val gear: Int = 1, // -1: R, 0: N, 1..6
    val throttle: Float = 0f,
    val brake: Float = 0f,
    val handbrake: Boolean = false,
    val steeringAngle: Float = 0f,
    val isDrifting: Boolean = false,
    val driftAngle: Float = 0f,
    val currentDriftPoints: Int = 0,
    val driftMultiplier: Float = 1.0f,
    val totalDriftScore: Int = 0,
    val nitroPercent: Float = 100f,
    val isNitroActive: Boolean = false,
    val posX: Float = 250f,
    val posY: Float = 250f,
    val heading: Float = 0f, // in radians
    val weather: WeatherCondition = WeatherCondition.SUNNY,
    val camera: CameraView = CameraView.THIRD_PERSON,
    val zone: CityZone = CityZone.DOWNTOWN,
    val odoMeterKm: Float = 0f,
    val fuelLevel: Float = 100f,
    val carCondition: Int = 100
)

class CarPhysicsSimulation(private var activeCar: GameCar) {

    var state = TelemetryData()
        private set

    private var driftComboTimer: Long = 0
    private var lastStepTime = System.currentTimeMillis()

    fun updateCar(car: GameCar) {
        activeCar = car
    }

    fun setInputs(
        throttle: Float,
        brake: Float,
        steering: Float, // -1.0 (left) to +1.0 (right)
        handbrake: Boolean,
        nitro: Boolean
    ) {
        val now = System.currentTimeMillis()
        val dt = ((now - lastStepTime) / 1000f).coerceIn(0.01f, 0.1f)
        lastStepTime = now

        // Steering
        val maxSteer = if (activeCar.suspensionPreset == SetupPreset.DRIFT) 42f else 32f
        val currentSteer = steering * maxSteer

        // Nitro
        val canUseNitro = nitro && state.nitroPercent > 5f
        var newNitro = state.nitroPercent
        if (canUseNitro) {
            newNitro = (newNitro - 25f * dt).coerceAtLeast(0f)
        } else {
            newNitro = (newNitro + 6f * dt).coerceAtMost(100f) // Slowly recharge
        }

        // Power & Acceleration
        val hpFactor = activeCar.currentHp / 200f
        val nitroBoost = if (canUseNitro) 1.55f else 1.0f
        val roadGrip = when (state.weather) {
            WeatherCondition.RAIN -> 0.75f
            WeatherCondition.FOG -> 0.90f
            else -> 1.0f
        } * activeCar.tireGrip

        // Speed calculation
        var newSpeed = state.speedKmh
        val topSpeed = activeCar.currentTopSpeed * (if (canUseNitro) 1.15f else 1.0f)

        if (throttle > 0.05f) {
            val accelRate = (35f * hpFactor * nitroBoost * throttle * roadGrip) / (1f + (newSpeed / topSpeed) * 2f)
            newSpeed += accelRate * dt
        } else {
            // Natural engine braking & drag
            newSpeed -= (8f + (newSpeed * 0.05f)) * dt
        }

        // Braking & Handbrake
        if (brake > 0.05f) {
            val brakePower = (60f + activeCar.brakeLevel * 18f) * brake
            newSpeed -= brakePower * dt
        }
        if (handbrake) {
            newSpeed -= 40f * dt
        }
        newSpeed = newSpeed.coerceIn(0f, topSpeed)

        // Gear & RPM
        val gearRatios = floatArrayOf(45f, 90f, 140f, 195f, 255f, 360f)
        var gear = 1
        for (i in gearRatios.indices) {
            if (newSpeed <= gearRatios[i]) {
                gear = i + 1
                break
            }
            gear = 6
        }

        val prevGearMax = if (gear == 1) 0f else gearRatios[gear - 2]
        val currGearMax = gearRatios[gear - 1]
        val gearProgress = ((newSpeed - prevGearMax) / (currGearMax - prevGearMax)).coerceIn(0f, 1f)
        val rpm = (900 + (gearProgress * 6600)).toInt().coerceIn(800, 8200)

        // Drift Physics
        val isDrifting = (handbrake || (abs(steering) > 0.6f && newSpeed > 35f)) && (newSpeed > 15f)
        val driftAngle = if (isDrifting) abs(currentSteer) * 1.3f else 0f
        var driftPoints = state.currentDriftPoints
        var driftMultiplier = state.driftMultiplier
        var totalDrift = state.totalDriftScore

        if (isDrifting) {
            driftComboTimer = now
            val pointsEarned = (newSpeed * 0.8f * (driftAngle / 20f) * dt * 10f).toInt()
            driftPoints += pointsEarned
            driftMultiplier = (driftMultiplier + 0.35f * dt).coerceAtMost(5.0f)
        } else if (now - driftComboTimer > 1500 && driftPoints > 0) {
            // Bank drift points
            totalDrift += (driftPoints * driftMultiplier).toInt()
            driftPoints = 0
            driftMultiplier = 1.0f
        }

        // 2D Canvas Position & Heading Simulation
        val turnRate = (currentSteer * (PI.toFloat() / 180f) * (newSpeed / 100f).coerceIn(0.2f, 1.2f)) * dt
        val newHeading = state.heading + (if (isDrifting) turnRate * 1.6f else turnRate)
        val distanceTraveled = (newSpeed * 1000f / 3600f) * dt // meters
        val pixelScale = 1.8f // pixels per meter
        var newX = state.posX + cos(newHeading) * (distanceTraveled * pixelScale)
        var newY = state.posY + sin(newHeading) * (distanceTraveled * pixelScale)

        // Keep inside 2000x2000 arena bounds with wrapping
        if (newX < 50f) newX = 1950f
        if (newX > 1950f) newX = 50f
        if (newY < 50f) newY = 1950f
        if (newY > 1950f) newY = 50f

        val newOdo = state.odoMeterKm + (distanceTraveled / 1000f)
        val newFuel = (state.fuelLevel - (throttle * 0.004f * dt)).coerceAtLeast(0f)

        state = state.copy(
            speedKmh = newSpeed,
            rpm = rpm,
            gear = gear,
            throttle = throttle,
            brake = brake,
            handbrake = handbrake,
            steeringAngle = currentSteer,
            isDrifting = isDrifting,
            driftAngle = driftAngle,
            currentDriftPoints = driftPoints,
            driftMultiplier = driftMultiplier,
            totalDriftScore = totalDrift,
            nitroPercent = newNitro,
            isNitroActive = canUseNitro,
            posX = newX,
            posY = newY,
            heading = newHeading,
            odoMeterKm = newOdo,
            fuelLevel = newFuel
        )
    }

    fun cycleWeather() {
        val nextWeather = when (state.weather) {
            WeatherCondition.SUNNY -> WeatherCondition.RAIN
            WeatherCondition.RAIN -> WeatherCondition.NIGHT
            WeatherCondition.NIGHT -> WeatherCondition.FOG
            WeatherCondition.FOG -> WeatherCondition.SUNNY
        }
        state = state.copy(weather = nextWeather)
    }

    fun cycleCamera() {
        val nextCam = when (state.camera) {
            CameraView.THIRD_PERSON -> CameraView.COCKPIT
            CameraView.COCKPIT -> CameraView.TOP_DOWN
            CameraView.TOP_DOWN -> CameraView.THIRD_PERSON
        }
        state = state.copy(camera = nextCam)
    }

    fun setZone(zone: CityZone) {
        state = state.copy(zone = zone)
    }
}

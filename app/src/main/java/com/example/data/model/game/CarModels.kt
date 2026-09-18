package com.example.data.model.game

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class CarRarity {
    COMMON,       // ئاسایی
    RARE,         // دەگمەن
    VERY_RARE,    // زۆر دەگمەن
    SPECIAL,      // تایبەت
    LEGENDARY     // دەگمەنترین
}

enum class PaintFinish {
    GLOSS,        // بریقەدار
    METALLIC,     // میتالیک
    MATTE,        // مات
    PEARL,        // مرواری
    CHROME,       // کڕۆم
    CARBON        // کاربۆن
}

enum class TransmissionType {
    AUTOMATIC,    // ئۆتۆماتیک
    MANUAL,       // دەستی
    SEQUENTIAL    // پەیڕەوی
}

enum class SetupPreset {
    BALANCED,     // هاوسەنگ
    STREET,       // شەقام
    SPORT,        // سپۆرت
    RACE,         // پێشبڕکێ
    DRIFT,        // درێفت
    DRAG,         // دراگ
    OFFROAD       // بەردەڵان و دەرەوەی شار
}

@Entity(tableName = "game_cars")
data class GameCar(
    @PrimaryKey
    val id: String,
    val name: String,
    val brand: String,
    val modelYear: Int,
    val rarity: CarRarity = CarRarity.COMMON,
    val isOwned: Boolean = false,
    val price: Int,
    val resaleValue: Int,
    val mileageKm: Int = 0,
    val conditionPercent: Int = 100, // 0 - 100
    
    // Performance stats
    val baseHp: Int,
    val baseTorque: Int,
    val baseTopSpeed: Int,
    val baseAcceleration: Float, // 0-100 km/h in seconds
    val weightKg: Int,

    // Upgrades
    val engineLevel: Int = 1, // 1 - 5
    val turboLevel: Int = 0, // 0 - 3 (0 = Natural Aspirated, 1 = Single Turbo, 2 = Twin Turbo, 3 = Quad Turbo)
    val transmissionType: TransmissionType = TransmissionType.AUTOMATIC,
    val transmissionLevel: Int = 1, // 1 - 4
    val suspensionPreset: SetupPreset = SetupPreset.BALANCED,
    val rideHeightMm: Int = 140, // 80 - 180 mm
    val camberDegrees: Float = -1.0f, // -5.0 to 0.0
    val brakeLevel: Int = 1, // 1 - 4
    val tirePreset: SetupPreset = SetupPreset.STREET,
    val tireGrip: Float = 1.0f,

    // Visuals & Body
    val primaryColorHex: Long = 0xFF2A52BE, // Default blue
    val secondaryColorHex: Long = 0xFF111111,
    val paintFinish: PaintFinish = PaintFinish.METALLIC,
    val spoilerInstalled: Boolean = false,
    val bodyKitLevel: Int = 0, // 0 - 3
    val wheelStyle: Int = 1, // 1 - 6
    val windowTintPercent: Int = 20,

    // Maintenance
    val fuelPercent: Int = 100,
    val oilPercent: Int = 100
) {
    val currentHp: Int
        get() = baseHp + (engineLevel - 1) * 45 + turboLevel * 85

    val currentTopSpeed: Int
        get() = baseTopSpeed + (engineLevel - 1) * 12 + turboLevel * 20 + (transmissionLevel - 1) * 8

    val currentAcceleration: Float
        get() = (baseAcceleration - (engineLevel - 1) * 0.35f - turboLevel * 0.45f).coerceAtLeast(1.8f)
}

@Entity(tableName = "player_profile")
data class GamePlayerProfile(
    @PrimaryKey
    val id: Int = 1,
    val money: Int = 45000, // Starting currency: 45,000 IQD/Credit
    val reputation: Int = 120,
    val activeCarId: String = "car_hawk_gt",
    val garageLevel: Int = 1,
    val garageCapacity: Int = 6,
    val racesWon: Int = 0,
    val totalRaces: Int = 0,
    val driftHighScore: Int = 0,
    val garageThemeName: String = "Classic Obsidian"
)

enum class MissionType {
    STREET_RACE,
    DRIFT_TRIAL,
    HIGHWAY_SPRINT,
    DELIVERY,
    RESTORATION
}

@Entity(tableName = "game_missions")
data class GameMission(
    @PrimaryKey
    val id: String,
    val titleKurdish: String,
    val type: MissionType,
    val descriptionKurdish: String,
    val rewardMoney: Int,
    val rewardReputation: Int,
    val targetGoal: Int, // e.g., drift points or time in seconds
    val isCompleted: Boolean = false,
    val difficultyKurdish: String = "مامناوەند"
)

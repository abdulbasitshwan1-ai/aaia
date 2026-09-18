package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.model.BasokaFile
import com.example.data.model.BasokaMemory
import com.example.data.model.BasokaTask
import com.example.data.model.ChatMessage
import com.example.data.model.game.CarRarity
import com.example.data.model.game.GameCar
import com.example.data.model.game.GameMission
import com.example.data.model.game.GamePlayerProfile
import com.example.data.model.game.MissionType
import com.example.data.model.game.PaintFinish
import com.example.data.model.game.SetupPreset
import com.example.data.model.game.TransmissionType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        ChatMessage::class,
        BasokaTask::class,
        BasokaMemory::class,
        BasokaFile::class,
        GameCar::class,
        GamePlayerProfile::class,
        GameMission::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class BasokaDatabase : RoomDatabase() {
    abstract fun chatDao(): ChatDao
    abstract fun taskDao(): TaskDao
    abstract fun memoryDao(): MemoryDao
    abstract fun fileDao(): FileDao
    abstract fun gameDao(): GameDao

    companion object {
        @Volatile
        private var INSTANCE: BasokaDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): BasokaDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    BasokaDatabase::class.java,
                    "basoka_master_db"
                ).addCallback(object : Callback() {
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        super.onCreate(db)
                        INSTANCE?.let { database ->
                            scope.launch(Dispatchers.IO) {
                                populateInitialGameData(database.gameDao())
                                populateInitialMemories(database.memoryDao())
                            }
                        }
                    }
                }).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }

        suspend fun populateInitialMemories(memoryDao: MemoryDao) {
            memoryDao.insertMemory(
                BasokaMemory(
                    key = "زمانی دڵخواز",
                    value = "کوردیی سۆرانی",
                    category = "ڕێکخستنەکان"
                )
            )
            memoryDao.insertMemory(
                BasokaMemory(
                    key = "ڕووکار",
                    value = "Dark Premium مۆدێرن",
                    category = "ڕوخسار"
                )
            )
        }

        suspend fun populateInitialGameData(gameDao: GameDao) {
            // Player Profile
            gameDao.savePlayerProfile(GamePlayerProfile())

            // Default Cars catalog
            val defaultCars = listOf(
                GameCar(
                    id = "car_hawk_gt",
                    name = "BASOKA Hawk GT",
                    brand = "BASOKA Motors",
                    modelYear = 2024,
                    rarity = CarRarity.COMMON,
                    isOwned = true, // Starter car!
                    price = 35000,
                    resaleValue = 28000,
                    mileageKm = 1240,
                    conditionPercent = 100,
                    baseHp = 280,
                    baseTorque = 360,
                    baseTopSpeed = 245,
                    baseAcceleration = 5.2f,
                    weightKg = 1380,
                    primaryColorHex = 0xFF1E88E5,
                    paintFinish = PaintFinish.METALLIC
                ),
                GameCar(
                    id = "car_spectre_v8",
                    name = "Spectre Drift Spec",
                    brand = "Apex Works",
                    modelYear = 2023,
                    rarity = CarRarity.RARE,
                    isOwned = false,
                    price = 55000,
                    resaleValue = 44000,
                    mileageKm = 6800,
                    conditionPercent = 94,
                    baseHp = 420,
                    baseTorque = 510,
                    baseTopSpeed = 280,
                    baseAcceleration = 4.1f,
                    weightKg = 1450,
                    suspensionPreset = SetupPreset.DRIFT,
                    primaryColorHex = 0xFFFF5722,
                    paintFinish = PaintFinish.MATTE
                ),
                GameCar(
                    id = "car_kurd_hyper",
                    name = "Zagros Hyperline",
                    brand = "Basoka Zagros",
                    modelYear = 2025,
                    rarity = CarRarity.LEGENDARY,
                    isOwned = false,
                    price = 140000,
                    resaleValue = 120000,
                    mileageKm = 210,
                    conditionPercent = 100,
                    baseHp = 750,
                    baseTorque = 880,
                    baseTopSpeed = 345,
                    baseAcceleration = 2.7f,
                    weightKg = 1290,
                    suspensionPreset = SetupPreset.RACE,
                    primaryColorHex = 0xFF7C4DFF,
                    paintFinish = PaintFinish.CARBON
                ),
                GameCar(
                    id = "car_vintage_classic",
                    name = "Retro Falcon 1978",
                    brand = "Heritage",
                    modelYear = 1978,
                    rarity = CarRarity.SPECIAL,
                    isOwned = false,
                    price = 22000,
                    resaleValue = 18000,
                    mileageKm = 142000,
                    conditionPercent = 65, // Used & Needs restoration!
                    baseHp = 180,
                    baseTorque = 240,
                    baseTopSpeed = 190,
                    baseAcceleration = 8.5f,
                    weightKg = 1220,
                    primaryColorHex = 0xFFFFB300,
                    paintFinish = PaintFinish.GLOSS
                ),
                GameCar(
                    id = "car_dune_crawler",
                    name = "Dune Conqueror 4x4",
                    brand = "Taurus",
                    modelYear = 2024,
                    rarity = CarRarity.VERY_RARE,
                    isOwned = false,
                    price = 72000,
                    resaleValue = 60000,
                    mileageKm = 4300,
                    conditionPercent = 98,
                    baseHp = 390,
                    baseTorque = 600,
                    baseTopSpeed = 210,
                    baseAcceleration = 6.4f,
                    weightKg = 2100,
                    suspensionPreset = SetupPreset.OFFROAD,
                    primaryColorHex = 0xFF4CAF50,
                    paintFinish = PaintFinish.MATTE
                )
            )
            gameDao.insertCars(defaultCars)

            // Default Missions
            val defaultMissions = listOf(
                GameMission(
                    id = "mission_city_sprint",
                    titleKurdish = "پێشبڕکێی شەقامەکانی ناوەند",
                    type = MissionType.STREET_RACE,
                    descriptionKurdish = "پێشبڕکێ بکە لە شەقامی بازرگانی و پلەی یەکەم بەدەست بهێنە",
                    rewardMoney = 8500,
                    rewardReputation = 45,
                    targetGoal = 1,
                    difficultyKurdish = "ئاسان"
                ),
                GameMission(
                    id = "mission_drift_king",
                    titleKurdish = "تەحەدای درێفت لە شاخ",
                    type = MissionType.DRIFT_TRIAL,
                    descriptionKurdish = "بە درێفتێکی جوان زیاتر لە ٢,٥٠٠ خاڵ تۆمار بکە",
                    rewardMoney = 14000,
                    rewardReputation = 75,
                    targetGoal = 2500,
                    difficultyKurdish = "مامناوەند"
                ),
                GameMission(
                    id = "mission_highway_speed",
                    titleKurdish = "تاقیکردنەوەی خێرایی جادەی گشتی",
                    type = MissionType.HIGHWAY_SPRINT,
                    descriptionKurdish = "بگەرە خێرایی سەرووی ٢٢٠ کلم/کاتژمێر",
                    rewardMoney = 12000,
                    rewardReputation = 60,
                    targetGoal = 220,
                    difficultyKurdish = "مامناوەند"
                ),
                GameMission(
                    id = "mission_restore_classic",
                    titleKurdish = "بوژاندنەوەی سەیارەی کلاسیک",
                    type = MissionType.RESTORATION,
                    descriptionKurdish = "مۆتۆر و بۆدی سەیارەی Retro Falcon نوێ بکەرەوە بۆ ڕێژەی ١٠٠٪",
                    rewardMoney = 25000,
                    rewardReputation = 150,
                    targetGoal = 100,
                    difficultyKurdish = "قورس"
                )
            )
            gameDao.insertMissions(defaultMissions)
        }
    }
}

package com.example.data.repository

import com.example.data.local.GameDao
import com.example.data.model.game.GameCar
import com.example.data.model.game.GameMission
import com.example.data.model.game.GamePlayerProfile
import kotlinx.coroutines.flow.Flow

class GameRepository(
    private val gameDao: GameDao
) {
    val playerProfile: Flow<GamePlayerProfile?> = gameDao.getPlayerProfile()
    val allCars: Flow<List<GameCar>> = gameDao.getAllCars()
    val ownedCars: Flow<List<GameCar>> = gameDao.getOwnedCars()
    val allMissions: Flow<List<GameMission>> = gameDao.getAllMissions()

    suspend fun getCarById(carId: String): GameCar? = gameDao.getCarById(carId)
    suspend fun updateCar(car: GameCar) = gameDao.updateCar(car)
    suspend fun savePlayerProfile(profile: GamePlayerProfile) = gameDao.savePlayerProfile(profile)
    suspend fun updateMission(mission: GameMission) = gameDao.updateMission(mission)
}

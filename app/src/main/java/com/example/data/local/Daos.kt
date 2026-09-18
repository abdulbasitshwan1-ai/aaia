package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.BasokaFile
import com.example.data.model.BasokaMemory
import com.example.data.model.BasokaTask
import com.example.data.model.ChatMessage
import com.example.data.model.game.GameCar
import com.example.data.model.game.GameMission
import com.example.data.model.game.GamePlayerProfile
import kotlinx.coroutines.flow.Flow

@Dao
interface ChatDao {
    @Query("SELECT * FROM chat_messages ORDER BY timestamp ASC")
    fun getAllMessages(): Flow<List<ChatMessage>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: ChatMessage): Long

    @Update
    suspend fun updateMessage(message: ChatMessage)

    @Query("DELETE FROM chat_messages")
    suspend fun clearChat()
}

@Dao
interface TaskDao {
    @Query("SELECT * FROM tasks ORDER BY targetTimeMillis ASC")
    fun getAllTasks(): Flow<List<BasokaTask>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: BasokaTask): Long

    @Update
    suspend fun updateTask(task: BasokaTask)

    @Delete
    suspend fun deleteTask(task: BasokaTask)
}

@Dao
interface MemoryDao {
    @Query("SELECT * FROM memories ORDER BY timestamp DESC")
    fun getAllMemories(): Flow<List<BasokaMemory>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMemory(memory: BasokaMemory): Long

    @Update
    suspend fun updateMemory(memory: BasokaMemory)

    @Delete
    suspend fun deleteMemory(memory: BasokaMemory)

    @Query("DELETE FROM memories")
    suspend fun clearAll()
}

@Dao
interface FileDao {
    @Query("SELECT * FROM files ORDER BY timestamp DESC")
    fun getAllFiles(): Flow<List<BasokaFile>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFile(file: BasokaFile): Long

    @Delete
    suspend fun deleteFile(file: BasokaFile)
}

@Dao
interface GameDao {
    @Query("SELECT * FROM player_profile WHERE id = 1")
    fun getPlayerProfile(): Flow<GamePlayerProfile?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun savePlayerProfile(profile: GamePlayerProfile)

    @Query("SELECT * FROM game_cars")
    fun getAllCars(): Flow<List<GameCar>>

    @Query("SELECT * FROM game_cars WHERE isOwned = 1")
    fun getOwnedCars(): Flow<List<GameCar>>

    @Query("SELECT * FROM game_cars WHERE id = :carId LIMIT 1")
    suspend fun getCarById(carId: String): GameCar?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCars(cars: List<GameCar>)

    @Update
    suspend fun updateCar(car: GameCar)

    @Query("SELECT * FROM game_missions")
    fun getAllMissions(): Flow<List<GameMission>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMissions(missions: List<GameMission>)

    @Update
    suspend fun updateMission(mission: GameMission)
}

package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.BasokaDatabase
import com.example.data.model.game.CarRarity
import com.example.data.model.game.GameCar
import com.example.data.model.game.GameMission
import com.example.data.model.game.GamePlayerProfile
import com.example.data.model.game.PaintFinish
import com.example.data.model.game.SetupPreset
import com.example.data.model.game.TransmissionType
import com.example.data.repository.GameRepository
import com.example.game.engine.AudioSynthesizer
import com.example.game.engine.CameraView
import com.example.game.engine.CarPhysicsSimulation
import com.example.game.engine.CityZone
import com.example.game.engine.TelemetryData
import com.example.game.engine.WeatherCondition
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

enum class GameScreenTab {
    HOME,
    DRIVE,
    GARAGE,
    MARKET,
    MISSIONS,
    COLLECTION
}

data class GameUiState(
    val currentTab: GameScreenTab = GameScreenTab.HOME,
    val isDriving: Boolean = false,
    val telemetry: TelemetryData = TelemetryData(),
    val soundEnabled: Boolean = true,
    val garageSection: Int = 0, // 0: Engine/Turbo, 1: Suspension, 2: Transmission, 3: Paint/Body, 4: Wheels
    val selectedMarketCar: GameCar? = null,
    val gameNotice: String? = null
)

class GameViewModel(application: Application) : AndroidViewModel(application) {

    private val db = BasokaDatabase.getDatabase(application, viewModelScope)
    private val repository = GameRepository(db.gameDao())

    val profile: StateFlow<GamePlayerProfile?> = repository.playerProfile.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        GamePlayerProfile()
    )

    val allCars: StateFlow<List<GameCar>> = repository.allCars.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )

    val ownedCars: StateFlow<List<GameCar>> = repository.ownedCars.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )

    val missions: StateFlow<List<GameMission>> = repository.allMissions.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )

    private val _uiState = MutableStateFlow(GameUiState())
    val uiState: StateFlow<GameUiState> = _uiState.asStateFlow()

    private var simulation: CarPhysicsSimulation? = null
    private val audioSynth = AudioSynthesizer()
    private var driveLoopJob: Job? = null

    init {
        // Initial setup for simulation once cars are loaded
        viewModelScope.launch {
            ownedCars.collect { cars ->
                val activeId = profile.value?.activeCarId ?: "car_hawk_gt"
                val activeCar = cars.firstOrNull { it.id == activeId } ?: cars.firstOrNull()
                if (activeCar != null) {
                    if (simulation == null) {
                        simulation = CarPhysicsSimulation(activeCar)
                    } else {
                        simulation?.updateCar(activeCar)
                    }
                }
            }
        }
    }

    fun selectTab(tab: GameScreenTab) {
        if (_uiState.value.currentTab == GameScreenTab.DRIVE && tab != GameScreenTab.DRIVE) {
            stopDriveSimulation()
        }
        _uiState.value = _uiState.value.copy(currentTab = tab)
        if (tab == GameScreenTab.DRIVE) {
            startDriveSimulation()
        }
    }

    fun setGarageSection(section: Int) {
        _uiState.value = _uiState.value.copy(garageSection = section)
    }

    fun setActiveCar(carId: String) {
        viewModelScope.launch {
            val currentProfile = profile.value ?: GamePlayerProfile()
            repository.savePlayerProfile(currentProfile.copy(activeCarId = carId))
            val car = repository.getCarById(carId)
            if (car != null) {
                simulation?.updateCar(car)
                _uiState.value = _uiState.value.copy(gameNotice = "سەیارەی چالاک گۆڕدرا بۆ: ${car.name}")
            }
        }
    }

    fun clearGameNotice() {
        _uiState.value = _uiState.value.copy(gameNotice = null)
    }

    // Driving Controls
    fun startDriveSimulation() {
        if (_uiState.value.isDriving) return
        _uiState.value = _uiState.value.copy(isDriving = true)

        if (_uiState.value.soundEnabled) {
            audioSynth.start(viewModelScope)
        }

        driveLoopJob?.cancel()
        driveLoopJob = viewModelScope.launch {
            while (isActive && _uiState.value.isDriving) {
                simulation?.let { sim ->
                    val tele = sim.state
                    _uiState.value = _uiState.value.copy(telemetry = tele)
                    if (_uiState.value.soundEnabled) {
                        audioSynth.updateEngineSound(tele.rpm, tele.throttle, tele.isNitroActive)
                    }
                }
                delay(20) // 50 FPS physics update
            }
        }
    }

    fun stopDriveSimulation() {
        _uiState.value = _uiState.value.copy(isDriving = false)
        driveLoopJob?.cancel()
        driveLoopJob = null
        audioSynth.stop()

        // Bank total drift points into player profile as currency / stats
        val totalDrift = _uiState.value.telemetry.totalDriftScore
        if (totalDrift > 0) {
            viewModelScope.launch {
                val p = profile.value ?: GamePlayerProfile()
                val earnedCash = (totalDrift * 0.15f).toInt().coerceAtLeast(100)
                val newHigh = totalDrift.coerceAtLeast(p.driftHighScore)
                repository.savePlayerProfile(p.copy(
                    money = p.money + earnedCash,
                    driftHighScore = newHigh
                ))
                _uiState.value = _uiState.value.copy(
                    gameNotice = "پاداشتی درێفت: +$earnedCash IQD وەرگیرا!"
                )
            }
        }
    }

    fun updateDrivingInputs(throttle: Float, brake: Float, steering: Float, handbrake: Boolean, nitro: Boolean) {
        simulation?.setInputs(throttle, brake, steering, handbrake, nitro)
    }

    fun cycleWeather() {
        simulation?.cycleWeather()
    }

    fun cycleCamera() {
        simulation?.cycleCamera()
    }

    fun setCityZone(zone: CityZone) {
        simulation?.setZone(zone)
    }

    fun toggleSound() {
        val newSound = !_uiState.value.soundEnabled
        _uiState.value = _uiState.value.copy(soundEnabled = newSound)
        if (!newSound) {
            audioSynth.stop()
        } else if (_uiState.value.isDriving) {
            audioSynth.start(viewModelScope)
        }
    }

    // Garage Tuning
    fun upgradeEngine(car: GameCar) {
        val currentMoney = profile.value?.money ?: 0
        val cost = car.engineLevel * 4500
        if (currentMoney >= cost && car.engineLevel < 5) {
            viewModelScope.launch {
                val updatedCar = car.copy(engineLevel = car.engineLevel + 1)
                repository.updateCar(updatedCar)
                val p = profile.value ?: GamePlayerProfile()
                repository.savePlayerProfile(p.copy(money = currentMoney - cost, reputation = p.reputation + 25))
                simulation?.updateCar(updatedCar)
                _uiState.value = _uiState.value.copy(gameNotice = "مۆتۆر بەرزکرایەوە بۆ ئاستی ${updatedCar.engineLevel}!")
            }
        } else {
            _uiState.value = _uiState.value.copy(gameNotice = "پارەی پێویستت نییە (پێویستە: $cost IQD)")
        }
    }

    fun upgradeTurbo(car: GameCar) {
        val currentMoney = profile.value?.money ?: 0
        val cost = (car.turboLevel + 1) * 7000
        if (currentMoney >= cost && car.turboLevel < 3) {
            viewModelScope.launch {
                val updatedCar = car.copy(turboLevel = car.turboLevel + 1)
                repository.updateCar(updatedCar)
                val p = profile.value ?: GamePlayerProfile()
                repository.savePlayerProfile(p.copy(money = currentMoney - cost, reputation = p.reputation + 35))
                simulation?.updateCar(updatedCar)
                _uiState.value = _uiState.value.copy(gameNotice = "تۆربۆ بەرزکرایەوە بۆ ئاستی ${updatedCar.turboLevel}!")
            }
        } else {
            _uiState.value = _uiState.value.copy(gameNotice = "پارەی پێویستت نییە (پێویستە: $cost IQD)")
        }
    }

    fun setSuspensionPreset(car: GameCar, preset: SetupPreset) {
        viewModelScope.launch {
            val updatedCar = car.copy(suspensionPreset = preset)
            repository.updateCar(updatedCar)
            simulation?.updateCar(updatedCar)
            _uiState.value = _uiState.value.copy(gameNotice = "ڕێکخستنی هایدرۆلیک گۆڕدرا بۆ: ${preset.name}")
        }
    }

    fun setTransmission(car: GameCar, type: TransmissionType) {
        viewModelScope.launch {
            val updatedCar = car.copy(transmissionType = type)
            repository.updateCar(updatedCar)
            simulation?.updateCar(updatedCar)
            _uiState.value = _uiState.value.copy(gameNotice = "جۆری گێڕ گۆڕدرا بۆ: ${type.name}")
        }
    }

    fun updateCarPaint(car: GameCar, colorHex: Long, finish: PaintFinish) {
        val currentMoney = profile.value?.money ?: 0
        val cost = 1200
        if (currentMoney >= cost) {
            viewModelScope.launch {
                val updatedCar = car.copy(primaryColorHex = colorHex, paintFinish = finish)
                repository.updateCar(updatedCar)
                val p = profile.value ?: GamePlayerProfile()
                repository.savePlayerProfile(p.copy(money = currentMoney - cost))
                simulation?.updateCar(updatedCar)
                _uiState.value = _uiState.value.copy(gameNotice = "ڕەنگ و ڕووکاری بۆیە نوێکرایەوە!")
            }
        } else {
            _uiState.value = _uiState.value.copy(gameNotice = "پارەی پێویستت نییە بۆ بۆیە (1,200 IQD)")
        }
    }

    fun repairCar(car: GameCar) {
        val cost = ((100 - car.conditionPercent) * 65)
        val currentMoney = profile.value?.money ?: 0
        if (currentMoney >= cost) {
            viewModelScope.launch {
                val updatedCar = car.copy(conditionPercent = 100)
                repository.updateCar(updatedCar)
                val p = profile.value ?: GamePlayerProfile()
                repository.savePlayerProfile(p.copy(money = currentMoney - cost))
                simulation?.updateCar(updatedCar)
                _uiState.value = _uiState.value.copy(gameNotice = "سەیارەکە بە تەواوی چاککرایەوە (100%)")
            }
        } else {
            _uiState.value = _uiState.value.copy(gameNotice = "پارەی پێویستت نییە بۆ چاککردنەوە ($cost IQD)")
        }
    }

    // Market / Dealership
    fun buyCar(car: GameCar) {
        val currentMoney = profile.value?.money ?: 0
        if (currentMoney >= car.price) {
            viewModelScope.launch {
                val updatedCar = car.copy(isOwned = true)
                repository.updateCar(updatedCar)
                val p = profile.value ?: GamePlayerProfile()
                repository.savePlayerProfile(p.copy(
                    money = currentMoney - car.price,
                    activeCarId = car.id,
                    reputation = p.reputation + 50
                ))
                simulation?.updateCar(updatedCar)
                _uiState.value = _uiState.value.copy(gameNotice = "پیرۆزە! سەیارەی ${car.name} کڕدرا!")
            }
        } else {
            _uiState.value = _uiState.value.copy(gameNotice = "پارەی پێویستت نییە بۆ کڕینی ئەم سەیارەیە")
        }
    }

    fun completeMission(mission: GameMission) {
        viewModelScope.launch {
            repository.updateMission(mission.copy(isCompleted = true))
            val p = profile.value ?: GamePlayerProfile()
            repository.savePlayerProfile(p.copy(
                money = p.money + mission.rewardMoney,
                reputation = p.reputation + mission.rewardReputation,
                racesWon = p.racesWon + 1,
                totalRaces = p.totalRaces + 1
            ))
            _uiState.value = _uiState.value.copy(
                gameNotice = "ئەرکی \"${mission.titleKurdish}\" بە سەرکەوتوویی تەواوکرا! پاداشت: +${mission.rewardMoney} IQD"
            )
        }
    }

    override fun onCleared() {
        super.onCleared()
        stopDriveSimulation()
    }
}

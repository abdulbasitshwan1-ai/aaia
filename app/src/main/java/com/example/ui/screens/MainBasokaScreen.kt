package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.ChatBubble
import androidx.compose.material.icons.filled.Checklist
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.game.ui.GameCollectionScreen
import com.example.game.ui.GameDriveScreen
import com.example.game.ui.GameGarageScreen
import com.example.game.ui.GameHomeScreen
import com.example.game.ui.GameMarketScreen
import com.example.game.ui.GameMissionsScreen
import com.example.ui.theme.AccentCyan
import com.example.ui.theme.AccentPurple
import com.example.ui.theme.ObsidianBg
import com.example.ui.theme.PrimaryBlue
import com.example.ui.theme.SurfaceBorder
import com.example.ui.theme.SurfaceCard
import com.example.ui.theme.SurfaceDark
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.viewmodel.BasokaViewModel
import com.example.ui.viewmodel.GameScreenTab
import com.example.ui.viewmodel.GameViewModel

@Composable
fun MainBasokaScreen(
    basokaVm: BasokaViewModel,
    gameVm: GameViewModel
) {
    val basokaUiState by basokaVm.uiState.collectAsState()
    val gameUiState by gameVm.uiState.collectAsState()
    val messages by basokaVm.messages.collectAsState()
    val tasks by basokaVm.tasks.collectAsState()
    val memories by basokaVm.memories.collectAsState()
    val files by basokaVm.files.collectAsState()

    val profile by gameVm.profile.collectAsState()
    val allCars by gameVm.allCars.collectAsState()
    val ownedCars by gameVm.ownedCars.collectAsState()
    val missions by gameVm.missions.collectAsState()

    val activeCar = ownedCars.firstOrNull { it.id == profile?.activeCarId } ?: ownedCars.firstOrNull()

    // Mode: false = Main BASOKA AI, true = BASOKA CAR WORLD
    var isGameModeActive by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(basokaUiState.userNoticeMessage) {
        basokaUiState.userNoticeMessage?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            basokaVm.clearNotice()
        }
    }

    LaunchedEffect(gameUiState.gameNotice) {
        gameUiState.gameNotice?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            gameVm.clearGameNotice()
        }
    }

    if (isGameModeActive) {
        // BASOKA CAR WORLD Experience
        when (gameUiState.currentTab) {
            GameScreenTab.HOME -> {
                GameHomeScreen(
                    profile = profile,
                    activeCar = activeCar,
                    onNavigate = { tab -> gameVm.selectTab(tab) },
                    onBackToAssistant = { isGameModeActive = false }
                )
            }
            GameScreenTab.DRIVE -> {
                GameDriveScreen(
                    activeCar = activeCar,
                    telemetry = gameUiState.telemetry,
                    soundEnabled = gameUiState.soundEnabled,
                    onInputsChanged = { throttle, brake, steer, hb, nitro ->
                        gameVm.updateDrivingInputs(throttle, brake, steer, hb, nitro)
                    },
                    onCycleWeather = { gameVm.cycleWeather() },
                    onCycleCamera = { gameVm.cycleCamera() },
                    onSetZone = { zone -> gameVm.setCityZone(zone) },
                    onToggleSound = { gameVm.toggleSound() },
                    onExitDrive = { gameVm.selectTab(GameScreenTab.HOME) }
                )
            }
            GameScreenTab.GARAGE -> {
                GameGarageScreen(
                    activeCar = activeCar,
                    profile = profile,
                    garageSection = gameUiState.garageSection,
                    onSectionSelected = { section -> gameVm.setGarageSection(section) },
                    onUpgradeEngine = { car -> gameVm.upgradeEngine(car) },
                    onUpgradeTurbo = { car -> gameVm.upgradeTurbo(car) },
                    onSetSuspension = { car, preset -> gameVm.setSuspensionPreset(car, preset) },
                    onSetTransmission = { car, type -> gameVm.setTransmission(car, type) },
                    onUpdatePaint = { car, color, finish -> gameVm.updateCarPaint(car, color, finish) },
                    onRepairCar = { car -> gameVm.repairCar(car) },
                    onBack = { gameVm.selectTab(GameScreenTab.HOME) }
                )
            }
            GameScreenTab.MARKET -> {
                GameMarketScreen(
                    allCars = allCars,
                    profile = profile,
                    onBuyCar = { car -> gameVm.buyCar(car) },
                    onBack = { gameVm.selectTab(GameScreenTab.HOME) }
                )
            }
            GameScreenTab.MISSIONS -> {
                GameMissionsScreen(
                    missions = missions,
                    profile = profile,
                    onCompleteMission = { mission -> gameVm.completeMission(mission) },
                    onBack = { gameVm.selectTab(GameScreenTab.HOME) }
                )
            }
            GameScreenTab.COLLECTION -> {
                GameCollectionScreen(
                    ownedCars = ownedCars,
                    profile = profile,
                    onSelectActiveCar = { carId -> gameVm.setActiveCar(carId) },
                    onBack = { gameVm.selectTab(GameScreenTab.HOME) }
                )
            }
        }
    } else {
        // Main BASOKA AI Assistant
        Scaffold(
            snackbarHost = { SnackbarHost(snackbarHostState) },
            topBar = {
                // Main Header
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(SurfaceDark)
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Logo & App Name
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .background(
                                    Brush.linearGradient(listOf(PrimaryBlue, AccentPurple)),
                                    CircleShape
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("B", fontWeight = FontWeight.ExtraBold, fontSize = 20.sp, color = Color.White)
                        }
                        Spacer(Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "BASOKA",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.ExtraBold,
                                color = TextPrimary
                            )
                            Text(
                                text = "سادە لە دەرەوە، زۆر بەهێز لە ناوەوە",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextSecondary,
                                fontSize = 10.sp
                            )
                        }
                    }

                    // Direct Launcher into BASOKA CAR WORLD
                    Button(
                        onClick = { isGameModeActive = true },
                        colors = ButtonDefaults.buttonColors(containerColor = SurfaceCard),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .border(1.dp, PrimaryBlue.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                            .testTag("launch_car_world_btn")
                    ) {
                        Icon(Icons.Default.DirectionsCar, contentDescription = null, tint = AccentCyan, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(6.dp))
                        Text("یاری سەیارە", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                    }
                }
            },
            bottomBar = {
                NavigationBar(
                    containerColor = SurfaceDark,
                    contentColor = PrimaryBlue
                ) {
                    val tabs = listOf(
                        Triple(0, "چات", Icons.Default.ChatBubble),
                        Triple(1, "تواناکان", Icons.Default.AutoAwesome),
                        Triple(2, "کارەکان", Icons.Default.Checklist),
                        Triple(3, "فایلەکان", Icons.Default.Folder),
                        Triple(4, "ڕێکخستن", Icons.Default.Settings)
                    )

                    tabs.forEach { (index, title, icon) ->
                        val isSelected = basokaUiState.selectedTab == index
                        NavigationBarItem(
                            selected = isSelected,
                            onClick = { basokaVm.selectTab(index) },
                            icon = { Icon(icon, contentDescription = title) },
                            label = {
                                Text(
                                    text = title,
                                    fontSize = 11.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                )
                            },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = Color.White,
                                selectedTextColor = PrimaryBlue,
                                unselectedIconColor = TextMuted,
                                unselectedTextColor = TextMuted,
                                indicatorColor = PrimaryBlue
                            ),
                            modifier = Modifier.testTag("nav_tab_$index")
                        )
                    }
                }
            }
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                when (basokaUiState.selectedTab) {
                    0 -> ChatScreen(
                        messages = messages,
                        isStreaming = basokaUiState.isStreaming,
                        voiceModeEnabled = basokaUiState.voiceModeEnabled,
                        onSendMessage = { text -> basokaVm.sendMessage(text) },
                        onConfirmAction = { msg, confirmed -> basokaVm.confirmAction(msg, confirmed) },
                        onToggleVoiceMode = { basokaVm.toggleVoiceMode() }
                    )
                    1 -> CapabilitiesScreen(
                        onTriggerCapability = { prompt ->
                            basokaVm.selectTab(0)
                            basokaVm.sendMessage(prompt)
                        }
                    )
                    2 -> TasksScreen(
                        tasks = tasks,
                        onToggleTask = { task -> basokaVm.toggleTask(task) },
                        onDeleteTask = { task -> basokaVm.deleteTask(task) },
                        onAddTask = { title, type, time, date -> basokaVm.addManualTask(title, type, time, date) }
                    )
                    3 -> FilesScreen(
                        files = files,
                        onAddDocument = { title, content -> basokaVm.uploadSampleDocument(title, content) },
                        onDeleteFile = { file -> basokaVm.deleteFile(file) }
                    )
                    4 -> SettingsScreen(
                        memories = memories,
                        onAddMemory = { k, v, cat -> basokaVm.addMemory(k, v, cat) },
                        onDeleteMemory = { m -> basokaVm.deleteMemory(m) },
                        onClearAllMemories = { basokaVm.clearAllMemories() }
                    )
                }
            }
        }
    }
}

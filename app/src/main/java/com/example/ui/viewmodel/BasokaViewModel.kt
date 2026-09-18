package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.ai.GeminiService
import com.example.data.local.BasokaDatabase
import com.example.data.model.ActionType
import com.example.data.model.BasokaFile
import com.example.data.model.BasokaMemory
import com.example.data.model.BasokaTask
import com.example.data.model.ChatMessage
import com.example.data.model.MessageSender
import com.example.data.model.TaskType
import com.example.data.repository.BasokaRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class BasokaUiState(
    val isStreaming: Boolean = false,
    val pendingConfirmationAction: ActionType = ActionType.NONE,
    val pendingActionPayload: String? = null,
    val voiceModeEnabled: Boolean = false, // strictly off by default
    val selectedTab: Int = 0, // 0: Chat, 1: Capabilities, 2: Tasks, 3: Files, 4: Settings
    val activeTimerSecondsLeft: Int = 0,
    val isTimerRunning: Boolean = false,
    val userNoticeMessage: String? = null
)

class BasokaViewModel(application: Application) : AndroidViewModel(application) {

    private val db = BasokaDatabase.getDatabase(application, viewModelScope)
    private val repository = BasokaRepository(
        db.chatDao(),
        db.taskDao(),
        db.memoryDao(),
        db.fileDao()
    )
    private val geminiService = GeminiService()

    val messages: StateFlow<List<ChatMessage>> = repository.allMessages.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )

    val tasks: StateFlow<List<BasokaTask>> = repository.allTasks.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )

    val memories: StateFlow<List<BasokaMemory>> = repository.allMemories.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )

    val files: StateFlow<List<BasokaFile>> = repository.allFiles.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )

    private val _uiState = MutableStateFlow(BasokaUiState())
    val uiState: StateFlow<BasokaUiState> = _uiState.asStateFlow()

    init {
        // Insert initial greeting if chat is empty
        viewModelScope.launch {
            repository.allMessages.collect { list ->
                if (list.isEmpty()) {
                    repository.insertMessage(
                        ChatMessage(
                            text = "سڵاو! من BASOKA ـم؛ یاریدەدەری زیرەکی دەستکردی کەسیی تۆ.\nبەبێ دوگمەی ئاڵۆز، هەموو شتێک بە کوردیی پاراو لێرەدا ئامادەیە: زەنگ و یادخەرەوە، کارەکانت، شیکاری فایل و کۆد، و یاری پێشبڕکێ و گەراجی BASOKA CAR WORLD.\nچۆن هاوکاریت بکەم ئەمڕۆ؟",
                            sender = MessageSender.BASOKA
                        )
                    )
                }
            }
        }
    }

    fun selectTab(index: Int) {
        _uiState.value = _uiState.value.copy(selectedTab = index)
    }

    fun toggleVoiceMode() {
        // Strict user manual toggle - never activates microphone on its own
        _uiState.value = _uiState.value.copy(
            voiceModeEnabled = !_uiState.value.voiceModeEnabled,
            userNoticeMessage = if (!_uiState.value.voiceModeEnabled) "دۆخی دەنگی چالاککرا" else "دۆخی دەنگی کوژایەوە"
        )
    }

    fun clearNotice() {
        _uiState.value = _uiState.value.copy(userNoticeMessage = null)
    }

    fun sendMessage(userText: String) {
        if (userText.isBlank()) return
        val trimmed = userText.trim()

        viewModelScope.launch {
            // Save user message
            repository.insertMessage(
                ChatMessage(
                    text = trimmed,
                    sender = MessageSender.USER
                )
            )

            _uiState.value = _uiState.value.copy(isStreaming = true)

            // Context from recent message
            val lastMsg = messages.value.lastOrNull { it.sender == MessageSender.BASOKA }?.text

            // Process through AI / NLP
            val nlpResult = geminiService.processUserPrompt(trimmed, lastMsg)

            // Create appropriate task if specified
            if (nlpResult.actionType in listOf(ActionType.REMINDER, ActionType.ALARM, ActionType.TIMER, ActionType.CALENDAR)) {
                if (nlpResult.taskType != null && !nlpResult.requiresConfirmation) {
                    repository.insertTask(
                        BasokaTask(
                            title = trimmed,
                            type = nlpResult.taskType,
                            targetTimeMillis = if (nlpResult.targetTimeMillis > 0) nlpResult.targetTimeMillis else System.currentTimeMillis() + 3600000,
                            timeLabel = nlpResult.timeLabel.ifBlank { "08:00" },
                            dateLabel = nlpResult.dateLabel.ifBlank { "ئەمڕۆ" },
                            repeatInterval = nlpResult.repeatInterval
                        )
                    )
                }
            }

            // Save assistant reply
            repository.insertMessage(
                ChatMessage(
                    text = nlpResult.replyText,
                    sender = MessageSender.BASOKA,
                    actionType = nlpResult.actionType,
                    actionPayload = nlpResult.actionPayload,
                    requiresConfirmation = nlpResult.requiresConfirmation,
                    confirmationPrompt = nlpResult.confirmationPrompt,
                    agentName = nlpResult.agentType
                )
            )

            _uiState.value = _uiState.value.copy(
                isStreaming = false,
                pendingConfirmationAction = if (nlpResult.requiresConfirmation) nlpResult.actionType else ActionType.NONE,
                pendingActionPayload = if (nlpResult.requiresConfirmation) nlpResult.actionPayload else null
            )
        }
    }

    fun confirmAction(message: ChatMessage, confirmed: Boolean) {
        viewModelScope.launch {
            repository.updateMessage(message.copy(isConfirmed = confirmed))
            if (confirmed) {
                when (message.actionPayload) {
                    "CLEAR_DATA" -> {
                        repository.clearChat()
                        repository.clearMemories()
                        _uiState.value = _uiState.value.copy(userNoticeMessage = "تەواوی یادەوەرییەکان بە سەرکەوتوویی سڕانەوە.")
                    }
                    else -> {
                        if (message.actionType == ActionType.CALENDAR) {
                            repository.insertTask(
                                BasokaTask(
                                    title = "کۆبوونەوەی دیاریکراو",
                                    type = TaskType.CALENDAR,
                                    targetTimeMillis = System.currentTimeMillis() + 86400000,
                                    timeLabel = "04:00 PM",
                                    dateLabel = "سبەی"
                                )
                            )
                        }
                        repository.insertMessage(
                            ChatMessage(
                                text = "کردارەکە بە سەرکەوتوویی پەسەندکرا و جێبەجێکرا.",
                                sender = MessageSender.BASOKA
                            )
                        )
                    }
                }
            } else {
                repository.insertMessage(
                    ChatMessage(
                        text = "کردارەکە هەڵوەشێنرایەوە لەسەر داوای تۆ.",
                        sender = MessageSender.BASOKA
                    )
                )
            }
        }
    }

    fun deleteTask(task: BasokaTask) {
        viewModelScope.launch {
            repository.deleteTask(task)
        }
    }

    fun toggleTask(task: BasokaTask) {
        viewModelScope.launch {
            repository.updateTask(task.copy(isCompleted = !task.isCompleted))
        }
    }

    fun addManualTask(title: String, type: TaskType, time: String, date: String) {
        viewModelScope.launch {
            repository.insertTask(
                BasokaTask(
                    title = title,
                    type = type,
                    targetTimeMillis = System.currentTimeMillis() + 3600000,
                    timeLabel = time,
                    dateLabel = date
                )
            )
        }
    }

    fun deleteMemory(memory: BasokaMemory) {
        viewModelScope.launch {
            repository.deleteMemory(memory)
        }
    }

    fun addMemory(key: String, value: String, category: String) {
        viewModelScope.launch {
            repository.insertMemory(
                BasokaMemory(key = key, value = value, category = category)
            )
        }
    }

    fun clearAllMemories() {
        viewModelScope.launch {
            repository.clearMemories()
            _uiState.value = _uiState.value.copy(userNoticeMessage = "تەواوی یادەوەرییەکان پاککرانەوە.")
        }
    }

    fun uploadSampleDocument(title: String, content: String) {
        viewModelScope.launch {
            val words = content.split("\\s+".toRegex()).size
            repository.insertFile(
                BasokaFile(
                    title = title,
                    fileType = "TXT",
                    contentPreview = content.take(160),
                    summaryKurdish = "پوختەی دەقی \"$title\": دەقەکە لە $words وشە پێکدێت و باس لە پێداویستی و ڕێکخستنی کارەکان دەکات.",
                    wordCount = words
                )
            )
        }
    }

    fun deleteFile(file: BasokaFile) {
        viewModelScope.launch {
            repository.deleteFile(file)
        }
    }
}

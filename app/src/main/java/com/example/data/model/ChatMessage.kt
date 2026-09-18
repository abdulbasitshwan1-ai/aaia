package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class MessageSender {
    USER,
    BASOKA,
    SYSTEM
}

enum class ActionType {
    NONE,
    REMINDER,
    ALARM,
    TIMER,
    CALENDAR,
    DEVICE_CONTROL,
    FILE_ACTION,
    CODE_GEN,
    CAR_TUNING,
    CAR_PURCHASE
}

@Entity(tableName = "chat_messages")
data class ChatMessage(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val text: String,
    val sender: MessageSender,
    val timestamp: Long = System.currentTimeMillis(),
    val actionType: ActionType = ActionType.NONE,
    val actionPayload: String? = null,
    val requiresConfirmation: Boolean = false,
    val isConfirmed: Boolean? = null,
    val confirmationPrompt: String? = null,
    val isError: Boolean = false,
    val agentName: String = "BASOKA"
)

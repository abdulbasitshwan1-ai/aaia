package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class TaskType {
    REMINDER,
    ALARM,
    TIMER,
    CALENDAR,
    AUTOMATION
}

@Entity(tableName = "tasks")
data class BasokaTask(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val type: TaskType,
    val targetTimeMillis: Long,
    val timeLabel: String,
    val dateLabel: String,
    val repeatInterval: String = "تاک", // تاک، ڕۆژانە، هەفتانە، مانگانە
    val isCompleted: Boolean = false,
    val isEnabled: Boolean = true,
    val extraNotes: String = ""
)

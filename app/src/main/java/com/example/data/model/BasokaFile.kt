package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "files")
data class BasokaFile(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val fileType: String, // PDF, TXT, DOCX, IMG
    val contentPreview: String,
    val summaryKurdish: String,
    val wordCount: Int,
    val timestamp: Long = System.currentTimeMillis()
)

package com.example.data.repository

import com.example.data.local.ChatDao
import com.example.data.local.FileDao
import com.example.data.local.MemoryDao
import com.example.data.local.TaskDao
import com.example.data.model.BasokaFile
import com.example.data.model.BasokaMemory
import com.example.data.model.BasokaTask
import com.example.data.model.ChatMessage
import kotlinx.coroutines.flow.Flow

class BasokaRepository(
    private val chatDao: ChatDao,
    private val taskDao: TaskDao,
    private val memoryDao: MemoryDao,
    private val fileDao: FileDao
) {
    // Chat
    val allMessages: Flow<List<ChatMessage>> = chatDao.getAllMessages()
    suspend fun insertMessage(message: ChatMessage): Long = chatDao.insertMessage(message)
    suspend fun updateMessage(message: ChatMessage) = chatDao.updateMessage(message)
    suspend fun clearChat() = chatDao.clearChat()

    // Tasks
    val allTasks: Flow<List<BasokaTask>> = taskDao.getAllTasks()
    suspend fun insertTask(task: BasokaTask): Long = taskDao.insertTask(task)
    suspend fun updateTask(task: BasokaTask) = taskDao.updateTask(task)
    suspend fun deleteTask(task: BasokaTask) = taskDao.deleteTask(task)

    // Memories
    val allMemories: Flow<List<BasokaMemory>> = memoryDao.getAllMemories()
    suspend fun insertMemory(memory: BasokaMemory): Long = memoryDao.insertMemory(memory)
    suspend fun updateMemory(memory: BasokaMemory) = memoryDao.updateMemory(memory)
    suspend fun deleteMemory(memory: BasokaMemory) = memoryDao.deleteMemory(memory)
    suspend fun clearMemories() = memoryDao.clearAll()

    // Files
    val allFiles: Flow<List<BasokaFile>> = fileDao.getAllFiles()
    suspend fun insertFile(file: BasokaFile): Long = fileDao.insertFile(file)
    suspend fun deleteFile(file: BasokaFile) = fileDao.deleteFile(file)
}

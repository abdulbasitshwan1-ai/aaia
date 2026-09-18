package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.HourglassBottom
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.BasokaTask
import com.example.data.model.TaskType
import com.example.ui.theme.AccentPurple
import com.example.ui.theme.GoldReward
import com.example.ui.theme.ObsidianBg
import com.example.ui.theme.PrimaryBlue
import com.example.ui.theme.RacingRed
import com.example.ui.theme.StatusSuccess
import com.example.ui.theme.SurfaceBorder
import com.example.ui.theme.SurfaceCard
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun TasksScreen(
    tasks: List<BasokaTask>,
    onToggleTask: (BasokaTask) -> Unit,
    onDeleteTask: (BasokaTask) -> Unit,
    onAddTask: (String, TaskType, String, String) -> Unit
) {
    var showAddDialog by remember { mutableStateOf(false) }
    var newTaskTitle by remember { mutableStateOf("") }
    var newTaskTime by remember { mutableStateOf("08:00") }
    var newTaskDate by remember { mutableStateOf("سبەی") }
    var selectedType by remember { mutableStateOf(TaskType.REMINDER) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(ObsidianBg)
            .testTag("tasks_screen")
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Header
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "کارەکان و کاتی دیاریکراو",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = "یادخەرەوەکان، زەنگەکان، تایمەر و ڕۆژژمێر بە شێوازی ڕێکخراو",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary
                )
            }

            if (tasks.isEmpty()) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            Icons.Default.Notifications,
                            contentDescription = null,
                            tint = TextMuted,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(Modifier.height(12.dp))
                        Text("هیچ کارێکی دیاریکراو بوونی نییە", color = TextSecondary, fontSize = 14.sp)
                        Text("لە چات بڵێ: «سبەی لە ٨ بیرم بخەرەوە...»", color = TextMuted, fontSize = 12.sp)
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(tasks) { task ->
                        val icon = when (task.type) {
                            TaskType.REMINDER -> Icons.Default.Notifications
                            TaskType.ALARM -> Icons.Default.Alarm
                            TaskType.TIMER -> Icons.Default.HourglassBottom
                            TaskType.CALENDAR -> Icons.Default.CalendarMonth
                            TaskType.AUTOMATION -> Icons.Default.CheckCircle
                        }

                        val accentColor = when (task.type) {
                            TaskType.REMINDER -> PrimaryBlue
                            TaskType.ALARM -> GoldReward
                            TaskType.TIMER -> Color(0xFF00E5FF)
                            TaskType.CALENDAR -> AccentPurple
                            TaskType.AUTOMATION -> StatusSuccess
                        }

                        val typeLabel = when (task.type) {
                            TaskType.REMINDER -> "بیرخستنەوە"
                            TaskType.ALARM -> "زەنگ"
                            TaskType.TIMER -> "تایمەر"
                            TaskType.CALENDAR -> "ڕۆژژمێر"
                            TaskType.AUTOMATION -> "ئۆتۆماتیک"
                        }

                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = if (task.isCompleted) SurfaceCard.copy(alpha = 0.6f) else SurfaceCard
                            ),
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(1.dp, SurfaceBorder, RoundedCornerShape(14.dp))
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                IconButton(
                                    onClick = { onToggleTask(task) },
                                    modifier = Modifier.size(28.dp)
                                ) {
                                    Icon(
                                        if (task.isCompleted) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
                                        contentDescription = "Toggle",
                                        tint = if (task.isCompleted) StatusSuccess else TextMuted
                                    )
                                }

                                Spacer(Modifier.width(10.dp))

                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .background(accentColor.copy(alpha = 0.15f), CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(icon, contentDescription = null, tint = accentColor, modifier = Modifier.size(20.dp))
                                }

                                Spacer(Modifier.width(12.dp))

                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = task.title,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp,
                                        color = if (task.isCompleted) TextMuted else TextPrimary,
                                        textDecoration = if (task.isCompleted) TextDecoration.LineThrough else TextDecoration.None
                                    )
                                    Spacer(Modifier.height(2.dp))
                                    Text(
                                        text = "$typeLabel • ${task.dateLabel} ${task.timeLabel} (${task.repeatInterval})",
                                        fontSize = 11.sp,
                                        color = TextSecondary
                                    )
                                }

                                IconButton(
                                    onClick = { onDeleteTask(task) },
                                    modifier = Modifier.size(28.dp)
                                ) {
                                    Icon(Icons.Default.Delete, contentDescription = "Delete", tint = RacingRed.copy(alpha = 0.8f), modifier = Modifier.size(18.dp))
                                }
                            }
                        }
                    }
                }
            }
        }

        // Floating Action Button to add Task
        FloatingActionButton(
            onClick = { showAddDialog = true },
            containerColor = PrimaryBlue,
            contentColor = Color.White,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(20.dp)
                .testTag("add_task_fab")
        ) {
            Icon(Icons.Default.Add, contentDescription = "Add Task")
        }

        if (showAddDialog) {
            AlertDialog(
                onDismissRequest = { showAddDialog = false },
                title = { Text("زیادکردنی کاری نوێ", fontWeight = FontWeight.Bold, color = TextPrimary) },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        OutlinedTextField(
                            value = newTaskTitle,
                            onValueChange = { newTaskTitle = it },
                            label = { Text("ناوی کارەکە") },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )

                        OutlinedTextField(
                            value = newTaskTime,
                            onValueChange = { newTaskTime = it },
                            label = { Text("کاتژمێر (بۆ نموونە 08:00)") },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )

                        OutlinedTextField(
                            value = newTaskDate,
                            onValueChange = { newTaskDate = it },
                            label = { Text("ڕۆژ (بۆ نموونە: سبەی، ڕۆژانە)") },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            if (newTaskTitle.isNotBlank()) {
                                onAddTask(newTaskTitle, selectedType, newTaskTime, newTaskDate)
                                newTaskTitle = ""
                                showAddDialog = false
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
                    ) {
                        Text("زیادکردن", fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    Button(
                        onClick = { showAddDialog = false },
                        colors = ButtonDefaults.buttonColors(containerColor = SurfaceBorder)
                    ) {
                        Text("داخستن", color = TextSecondary)
                    }
                },
                containerColor = SurfaceCard
            )
        }
    }
}

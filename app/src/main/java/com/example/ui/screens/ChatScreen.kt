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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ChatMessage
import com.example.data.model.MessageSender
import com.example.ui.theme.AccentPurple
import com.example.ui.theme.ObsidianBg
import com.example.ui.theme.PrimaryBlue
import com.example.ui.theme.RacingRed
import com.example.ui.theme.StatusSuccess
import com.example.ui.theme.SurfaceBorder
import com.example.ui.theme.SurfaceCard
import com.example.ui.theme.SurfaceDark
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ChatScreen(
    messages: List<ChatMessage>,
    isStreaming: Boolean,
    voiceModeEnabled: Boolean,
    onSendMessage: (String) -> Unit,
    onConfirmAction: (ChatMessage, Boolean) -> Unit,
    onToggleVoiceMode: () -> Unit
) {
    var inputText by remember { mutableStateOf("") }
    val listState = rememberLazyListState()

    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    val sampleSuggestions = listOf(
        "سبەی لە ٨ بیرم بخەرەوە",
        "زەنگم بۆ دابنێ",
        "سەیارەکەم بۆ race ئامادە بکە",
        "تایمەرێکی ١٥ خولەکی دابنێ",
        "پوختەی ئەم وتارەم بۆ بنووسە",
        "پلانی خوێندن بۆ تاقیکردنەوە"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ObsidianBg)
            .testTag("chat_screen")
    ) {
        // Subtle status bar: Voice mode indicator (strictly manual)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .background(if (voiceModeEnabled) StatusSuccess else TextMuted, CircleShape)
                )
                Spacer(Modifier.width(6.dp))
                Text(
                    text = if (voiceModeEnabled) "دەنگ: چالاککراوە (تەنها بە دەستی)" else "دەنگ: ناچالاکە (تەنها دەق)",
                    fontSize = 11.sp,
                    color = TextMuted
                )
            }

            IconButton(
                onClick = onToggleVoiceMode,
                modifier = Modifier.size(28.dp)
            ) {
                Icon(
                    if (voiceModeEnabled) Icons.Default.Mic else Icons.Default.MicOff,
                    contentDescription = "Voice Mode Toggle",
                    tint = if (voiceModeEnabled) PrimaryBlue else TextMuted,
                    modifier = Modifier.size(16.dp)
                )
            }
        }

        // Messages List
        LazyColumn(
            state = listState,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(messages) { message ->
                ChatMessageItem(
                    message = message,
                    onConfirm = { onConfirmAction(message, true) },
                    onCancel = { onConfirmAction(message, false) }
                )
            }

            if (isStreaming) {
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(18.dp),
                            color = PrimaryBlue,
                            strokeWidth = 2.dp
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(
                            text = "BASOKA بیر دەکاتەوە...",
                            fontSize = 12.sp,
                            color = TextSecondary
                        )
                    }
                }
            }
        }

        // Quick Suggestion Chips
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(sampleSuggestions) { suggestion ->
                FilterChip(
                    selected = false,
                    onClick = { onSendMessage(suggestion) },
                    label = { Text(suggestion, fontSize = 12.sp, color = TextSecondary) },
                    colors = FilterChipDefaults.filterChipColors(
                        containerColor = SurfaceCard
                    ),
                    border = FilterChipDefaults.filterChipBorder(
                        borderColor = SurfaceBorder,
                        enabled = true,
                        selected = false
                    )
                )
            }
        }

        // Bottom Input Row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(SurfaceDark)
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = inputText,
                onValueChange = { inputText = it },
                placeholder = {
                    Text(
                        "فەرمانێک بنووسە... (بۆ نموونە: سبەی لە ٨ بیرم بخەرەوە)",
                        fontSize = 13.sp,
                        color = TextMuted
                    )
                },
                modifier = Modifier
                    .weight(1f)
                    .testTag("chat_input_field"),
                shape = RoundedCornerShape(20.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = SurfaceCard,
                    unfocusedContainerColor = SurfaceCard,
                    focusedBorderColor = PrimaryBlue,
                    unfocusedBorderColor = SurfaceBorder,
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary
                ),
                maxLines = 4
            )

            Spacer(Modifier.width(8.dp))

            IconButton(
                onClick = {
                    if (inputText.isNotBlank()) {
                        onSendMessage(inputText)
                        inputText = ""
                    }
                },
                modifier = Modifier
                    .size(48.dp)
                    .background(PrimaryBlue, CircleShape)
                    .testTag("chat_send_button")
            ) {
                Icon(
                    Icons.AutoMirrored.Filled.Send,
                    contentDescription = "Send",
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

@Composable
fun ChatMessageItem(
    message: ChatMessage,
    onConfirm: () -> Unit,
    onCancel: () -> Unit
) {
    val isUser = message.sender == MessageSender.USER
    val timeFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())
    val timeString = timeFormat.format(Date(message.timestamp))

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = if (isUser) Alignment.End else Alignment.Start
    ) {
        Row(
            verticalAlignment = Alignment.Top,
            horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start,
            modifier = Modifier.fillMaxWidth(0.92f)
        ) {
            if (!isUser) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .background(PrimaryBlue.copy(alpha = 0.2f), CircleShape)
                        .border(1.dp, PrimaryBlue, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = PrimaryBlue, modifier = Modifier.size(16.dp))
                }
                Spacer(Modifier.width(8.dp))
            }

            Column {
                if (!isUser) {
                    Text(
                        text = message.agentName,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = AccentPurple,
                        modifier = Modifier.padding(bottom = 2.dp)
                    )
                }

                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = if (isUser) PrimaryBlue else SurfaceCard
                    ),
                    shape = RoundedCornerShape(
                        topStart = 16.dp,
                        topEnd = 16.dp,
                        bottomStart = if (isUser) 16.dp else 4.dp,
                        bottomEnd = if (isUser) 4.dp else 16.dp
                    ),
                    modifier = Modifier.border(
                        1.dp,
                        if (isUser) Color.Transparent else SurfaceBorder,
                        RoundedCornerShape(16.dp)
                    )
                ) {
                    Column(modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)) {
                        Text(
                            text = message.text,
                            style = MaterialTheme.typography.bodyMedium,
                            color = if (isUser) Color.White else TextPrimary,
                            lineHeight = 22.sp
                        )

                        // Confirmation card if required and pending
                        if (message.requiresConfirmation && message.isConfirmed == null) {
                            Spacer(Modifier.height(10.dp))
                            Card(
                                colors = CardDefaults.cardColors(containerColor = SurfaceDark),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .border(1.dp, SurfaceBorder, RoundedCornerShape(10.dp))
                            ) {
                                Column(modifier = Modifier.padding(10.dp)) {
                                    Text(
                                        text = message.confirmationPrompt ?: "دڵنیایت دەتەوێت ئەم کردارە جێبەجێ بکەم؟",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = TextPrimary
                                    )
                                    Spacer(Modifier.height(8.dp))
                                    Row(
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Button(
                                            onClick = onConfirm,
                                            colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                                            shape = RoundedCornerShape(8.dp),
                                            modifier = Modifier.weight(1f)
                                        ) {
                                            Text("بەڵێ، جێبەجێی بکە", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                        }
                                        Button(
                                            onClick = onCancel,
                                            colors = ButtonDefaults.buttonColors(containerColor = SurfaceBorder),
                                            shape = RoundedCornerShape(8.dp),
                                            modifier = Modifier.weight(1f)
                                        ) {
                                            Text("نەخێر", fontSize = 11.sp, color = RacingRed)
                                        }
                                    }
                                }
                            }
                        }

                        // Confirmed Status Badge
                        if (message.isConfirmed != null) {
                            Spacer(Modifier.height(4.dp))
                            Text(
                                text = if (message.isConfirmed == true) "✓ پەسەندکرا" else "✗ هەڵوەشێنرایەوە",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (message.isConfirmed == true) StatusSuccess else RacingRed
                            )
                        }

                        Spacer(Modifier.height(4.dp))
                        Text(
                            text = timeString,
                            fontSize = 9.sp,
                            color = if (isUser) Color.White.copy(alpha = 0.7f) else TextMuted,
                            modifier = Modifier.align(Alignment.End)
                        )
                    }
                }
            }

            if (isUser) {
                Spacer(Modifier.width(8.dp))
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .background(SurfaceCard, CircleShape)
                        .border(1.dp, SurfaceBorder, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Person, contentDescription = null, tint = TextSecondary, modifier = Modifier.size(16.dp))
                }
            }
        }
    }
}

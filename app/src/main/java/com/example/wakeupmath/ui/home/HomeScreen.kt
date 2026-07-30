package com.example.wakeupmath.ui.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.wakeupmath.R
import com.example.wakeupmath.data.local.AlarmEntity
import com.example.wakeupmath.theme.AccentRed
import com.example.wakeupmath.theme.CardSurface
import com.example.wakeupmath.theme.Charcoal
import com.example.wakeupmath.theme.DarkSurface
import com.example.wakeupmath.theme.DeepBlue
import com.example.wakeupmath.theme.ErrorRed
import com.example.wakeupmath.theme.OutfitFamily
import com.example.wakeupmath.theme.TextPrimary
import com.example.wakeupmath.theme.TextSecondary
import java.util.Locale

@Composable
fun HomeScreen(
    viewModel: AlarmViewModel,
    onAddAlarmClick: () -> Unit,
    onPracticeClick: () -> Unit = {}
) {
    val alarms by viewModel.alarms.collectAsState()
    val totalSolved by viewModel.totalSolvedCount.collectAsState()
    val avgTime by viewModel.averageSolveTime.collectAsState()

    Scaffold(
        containerColor = Charcoal,
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddAlarmClick,
                containerColor = AccentRed,
                contentColor = Color.White,
                shape = CircleShape
            ) {
                Text(text = "+", fontSize = 28.sp, fontWeight = FontWeight.Bold)
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 20.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // App Header with Math Gym Launcher
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "WakeUp Math",
                        style = androidx.compose.material3.MaterialTheme.typography.headlineLarge.copy(
                            fontFamily = OutfitFamily,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                    )
                    Text(
                        text = "No snooze. Solve to wake up.",
                        style = androidx.compose.material3.MaterialTheme.typography.bodyMedium.copy(
                            color = TextSecondary
                        )
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(DeepBlue)
                        .clickable { onPracticeClick() }
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "🧠 MATH GYM",
                            style = androidx.compose.material3.MaterialTheme.typography.labelSmall.copy(
                                color = AccentRed,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Brain Stats & Streaks Dashboard Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = DarkSurface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "BRAIN STATS & STREAK",
                            style = androidx.compose.material3.MaterialTheme.typography.labelMedium.copy(
                                color = TextSecondary,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.2.sp
                            )
                        )
                        Text(
                            text = "🔥 ${totalSolved} SOLVED",
                            style = androidx.compose.material3.MaterialTheme.typography.labelSmall.copy(
                                color = AccentRed,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text("Avg Solve Speed", style = androidx.compose.material3.MaterialTheme.typography.labelSmall.copy(color = TextSecondary))
                            Text(
                                text = if (avgTime != null) String.format(Locale.getDefault(), "%.1f sec", avgTime) else "N/A",
                                style = androidx.compose.material3.MaterialTheme.typography.titleMedium.copy(
                                    color = TextPrimary,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = OutfitFamily
                                )
                            )
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text("Achievement", style = androidx.compose.material3.MaterialTheme.typography.labelSmall.copy(color = TextSecondary))
                            Text(
                                text = when {
                                    totalSolved >= 10 -> "🏆 Math Genius"
                                    totalSolved >= 5 -> "⚡ Early Bird"
                                    else -> "🌱 Beginner"
                                },
                                style = androidx.compose.material3.MaterialTheme.typography.titleMedium.copy(
                                    color = AccentRed,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Sleep Cycle Bedtime Recommendation Tip Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = CardSurface.copy(alpha = 0.6f))
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("🌙 ", fontSize = 16.sp)
                    Text(
                        text = "Sleep Tip: Aim for 7.5 hrs (5 sleep cycles) for peak morning math solve speed!",
                        style = androidx.compose.material3.MaterialTheme.typography.labelSmall.copy(
                            color = TextSecondary,
                            fontWeight = FontWeight.Medium
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (alarms.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(bottom = 60.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_alarm),
                            contentDescription = null,
                            tint = CardSurface,
                            modifier = Modifier.size(72.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "No alarms set",
                            style = androidx.compose.material3.MaterialTheme.typography.titleLarge.copy(
                                color = TextSecondary
                            )
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Tap + to create a math alarm",
                            style = androidx.compose.material3.MaterialTheme.typography.bodyMedium.copy(
                                color = TextSecondary.copy(alpha = 0.7f)
                            )
                        )
                    }
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    items(alarms, key = { it.id }) { alarm ->
                        AlarmItemCard(
                            alarm = alarm,
                            onToggle = { viewModel.toggleAlarm(alarm) },
                            onDelete = { viewModel.deleteAlarm(alarm) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun AlarmItemCard(
    alarm: AlarmEntity,
    onToggle: () -> Unit,
    onDelete: () -> Unit
) {
    val timeFormatted = String.format(Locale.getDefault(), "%02d:%02d", alarm.hour, alarm.minute)

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (alarm.enabled) DarkSurface else DarkSurface.copy(alpha = 0.5f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.Bottom) {
                    Text(
                        text = timeFormatted,
                        style = androidx.compose.material3.MaterialTheme.typography.displaySmall.copy(
                            fontFamily = OutfitFamily,
                            fontWeight = FontWeight.Bold,
                            color = if (alarm.enabled) TextPrimary else TextSecondary
                        )
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = alarm.label.ifBlank { "Alarm" },
                        style = androidx.compose.material3.MaterialTheme.typography.bodyMedium.copy(
                            color = if (alarm.enabled) TextPrimary else TextSecondary,
                            fontWeight = FontWeight.Medium
                        )
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(DeepBlue)
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = alarm.difficulty,
                            style = androidx.compose.material3.MaterialTheme.typography.labelSmall.copy(
                                color = AccentRed,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(CardSurface)
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        val soundDisplay = if (alarm.sound.startsWith("CUSTOM:")) "🎵 " + alarm.sound.substringAfter("|") else "🎵 ${alarm.sound}"
                        Text(
                            text = soundDisplay,
                            style = androidx.compose.material3.MaterialTheme.typography.labelSmall.copy(
                                color = TextSecondary,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                }

                if (alarm.repeatDays.isNotBlank()) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = formatRepeatDays(alarm.repeatDays),
                        style = androidx.compose.material3.MaterialTheme.typography.labelSmall.copy(
                            color = TextSecondary
                        )
                    )
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onDelete) {
                    Text(
                        text = "✕",
                        color = ErrorRed.copy(alpha = 0.8f),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(modifier = Modifier.width(4.dp))
                Switch(
                    checked = alarm.enabled,
                    onCheckedChange = { onToggle() },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.White,
                        checkedTrackColor = AccentRed,
                        uncheckedThumbColor = TextSecondary,
                        uncheckedTrackColor = CardSurface
                    )
                )
            }
        }
    }
}

fun formatRepeatDays(repeatDays: String): String {
    val dayNames = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
    val days = repeatDays.split(",").mapNotNull { it.trim().toIntOrNull() }
    if (days.size == 7) return "Every day"
    if (days == listOf(0, 1, 2, 3, 4)) return "Weekdays"
    if (days == listOf(5, 6)) return "Weekends"
    return days.mapNotNull { dayNames.getOrNull(it) }.joinToString(", ")
}

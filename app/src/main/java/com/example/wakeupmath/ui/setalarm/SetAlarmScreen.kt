package com.example.wakeupmath.ui.setalarm

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TimePickerDefaults
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.wakeupmath.theme.AccentRed
import com.example.wakeupmath.theme.CardSurface
import com.example.wakeupmath.theme.Charcoal
import com.example.wakeupmath.theme.DarkSurface
import com.example.wakeupmath.theme.DeepBlue
import com.example.wakeupmath.theme.DividerColor
import com.example.wakeupmath.theme.InputSurface
import com.example.wakeupmath.theme.OutfitFamily
import com.example.wakeupmath.theme.TextPrimary
import com.example.wakeupmath.theme.TextSecondary
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun SetAlarmScreen(
    onSaveClick: (hour: Int, minute: Int, label: String, difficulty: String, repeatDays: String, sound: String) -> Unit,
    onBackClick: () -> Unit
) {
    val calendar = remember { Calendar.getInstance() }
    val timePickerState = rememberTimePickerState(
        initialHour = calendar.get(Calendar.HOUR_OF_DAY),
        initialMinute = calendar.get(Calendar.MINUTE),
        is24Hour = true
    )

    var label by remember { mutableStateOf("") }
    var selectedDifficulty by remember { mutableStateOf("MIXED") }
    var selectedSound by remember { mutableStateOf("HARSH SAWTOOTH") }
    val selectedDays = remember { mutableStateListOf<Int>() }

    val difficulties = listOf("TRIGONOMETRY", "ALGEBRA", "CALCULUS", "LOGARITHMS", "MIXED")
    val sounds = listOf("HARSH SAWTOOTH", "SIREN PULSE", "DIGITAL BEEP", "SINE CHIME", "RADAR SWEEP")
    val daysOfWeek = listOf("M", "T", "W", "T", "F", "S", "S")

    Scaffold(
        containerColor = Charcoal
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Cancel",
                    style = androidx.compose.material3.MaterialTheme.typography.titleMedium.copy(
                        color = TextSecondary
                    ),
                    modifier = Modifier.clickable { onBackClick() }
                )
                Text(
                    text = "New Math Alarm",
                    style = androidx.compose.material3.MaterialTheme.typography.titleLarge.copy(
                        fontFamily = OutfitFamily,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                )
                Text(
                    text = "Save",
                    style = androidx.compose.material3.MaterialTheme.typography.titleMedium.copy(
                        color = AccentRed,
                        fontWeight = FontWeight.Bold
                    ),
                    modifier = Modifier.clickable {
                        val repeatString = selectedDays.sorted().joinToString(",")
                        onSaveClick(
                            timePickerState.hour,
                            timePickerState.minute,
                            label,
                            selectedDifficulty,
                            repeatString,
                            selectedSound
                        )
                    }
                )
            }

            Spacer(modifier = Modifier.height(28.dp))

            // Material TimePicker styled with dark theme
            TimePicker(
                state = timePickerState,
                colors = TimePickerDefaults.colors(
                    clockDialColor = DarkSurface,
                    clockDialSelectedContentColor = Color.White,
                    clockDialUnselectedContentColor = TextSecondary,
                    selectorColor = AccentRed,
                    containerColor = DarkSurface,
                    periodSelectorBorderColor = DividerColor,
                    periodSelectorSelectedContainerColor = AccentRed,
                    periodSelectorUnselectedContainerColor = CardSurface,
                    periodSelectorSelectedContentColor = Color.White,
                    periodSelectorUnselectedContentColor = TextSecondary,
                    timeSelectorSelectedContainerColor = AccentRed,
                    timeSelectorUnselectedContainerColor = CardSurface,
                    timeSelectorSelectedContentColor = Color.White,
                    timeSelectorUnselectedContentColor = TextPrimary
                )
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Alarm Label Field
            OutlinedTextField(
                value = label,
                onValueChange = { label = it },
                label = { Text("Alarm Label", color = TextSecondary) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = InputSurface,
                    unfocusedContainerColor = InputSurface,
                    focusedBorderColor = AccentRed,
                    unfocusedBorderColor = DividerColor,
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary
                )
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Difficulty Selector
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "MATH DIFFICULTY",
                    style = androidx.compose.material3.MaterialTheme.typography.labelMedium.copy(
                        color = TextSecondary,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.5.sp
                    )
                )
                Spacer(modifier = Modifier.height(10.dp))
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    difficulties.forEach { diff ->
                        val isSelected = selectedDifficulty == diff
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (isSelected) AccentRed else CardSurface)
                                .border(
                                    width = 1.dp,
                                    color = if (isSelected) AccentRed else DividerColor,
                                    shape = RoundedCornerShape(12.dp)
                                )
                                .clickable { selectedDifficulty = diff }
                                .padding(horizontal = 14.dp, vertical = 10.dp)
                        ) {
                            Text(
                                text = diff,
                                style = androidx.compose.material3.MaterialTheme.typography.labelMedium.copy(
                                    color = if (isSelected) Color.White else TextPrimary,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Alarm Sound Selector
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "ALARM SOUND",
                    style = androidx.compose.material3.MaterialTheme.typography.labelMedium.copy(
                        color = TextSecondary,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.5.sp
                    )
                )
                Spacer(modifier = Modifier.height(10.dp))
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    sounds.forEach { snd ->
                        val isSelected = selectedSound == snd
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (isSelected) DeepBlue else CardSurface)
                                .border(
                                    width = 1.dp,
                                    color = if (isSelected) AccentRed else DividerColor,
                                    shape = RoundedCornerShape(12.dp)
                                )
                                .clickable { selectedSound = snd }
                                .padding(horizontal = 14.dp, vertical = 10.dp)
                        ) {
                            Text(
                                text = snd,
                                style = androidx.compose.material3.MaterialTheme.typography.labelMedium.copy(
                                    color = if (isSelected) AccentRed else TextPrimary,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Repeat Days Selector
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "REPEAT DAYS",
                    style = androidx.compose.material3.MaterialTheme.typography.labelMedium.copy(
                        color = TextSecondary,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.5.sp
                    )
                )
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    daysOfWeek.forEachIndexed { index, day ->
                        val isSelected = selectedDays.contains(index)
                        Box(
                            modifier = Modifier
                                .size(42.dp)
                                .clip(CircleShape)
                                .background(if (isSelected) AccentRed else CardSurface)
                                .border(
                                    width = 1.dp,
                                    color = if (isSelected) AccentRed else DividerColor,
                                    shape = CircleShape
                                )
                                .clickable {
                                    if (isSelected) selectedDays.remove(index)
                                    else selectedDays.add(index)
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = day,
                                style = androidx.compose.material3.MaterialTheme.typography.titleMedium.copy(
                                    color = if (isSelected) Color.White else TextPrimary,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Save Alarm Button
            Button(
                onClick = {
                    val repeatString = selectedDays.sorted().joinToString(",")
                    onSaveClick(
                        timePickerState.hour,
                        timePickerState.minute,
                        label,
                        selectedDifficulty,
                        repeatString,
                        selectedSound
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = AccentRed)
            ) {
                Text(
                    text = "SAVE MATH ALARM",
                    style = androidx.compose.material3.MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

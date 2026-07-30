package com.example.wakeupmath.ui.practice

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.wakeupmath.R
import com.example.wakeupmath.domain.MathQuestion
import com.example.wakeupmath.domain.MathQuestionGenerator
import com.example.wakeupmath.theme.AccentRed
import com.example.wakeupmath.theme.CardSurface
import com.example.wakeupmath.theme.Charcoal
import com.example.wakeupmath.theme.DarkSurface
import com.example.wakeupmath.theme.DeepBlue
import com.example.wakeupmath.theme.DividerColor
import com.example.wakeupmath.theme.ErrorRed
import com.example.wakeupmath.theme.InputSurface
import com.example.wakeupmath.theme.OutfitFamily
import com.example.wakeupmath.theme.SuccessGreen
import com.example.wakeupmath.theme.TextPrimary
import com.example.wakeupmath.theme.TextSecondary
import com.example.wakeupmath.ui.ringing.CustomNumPad

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun PracticeScreen(
    onBackClick: () -> Unit
) {
    var selectedCategory by remember { mutableStateOf("MIXED") }
    var currentQuestion by remember { mutableStateOf(MathQuestionGenerator.generate(selectedCategory)) }
    var userInput by remember { mutableStateOf("") }
    var score by remember { mutableIntStateOf(0) }
    var streak by remember { mutableIntStateOf(0) }
    var statusMessage by remember { mutableStateOf("") }
    var isError by remember { mutableStateOf(false) }

    val categories = listOf("EASY", "BODMAS", "TRICKY", "TRIGONOMETRY", "ALGEBRA", "CALCULUS", "LOGARITHMS", "MIXED")

    Scaffold(
        containerColor = Charcoal
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 20.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // Navigation Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "← Back",
                    style = androidx.compose.material3.MaterialTheme.typography.titleMedium.copy(
                        color = TextSecondary,
                        fontWeight = FontWeight.Bold
                    ),
                    modifier = Modifier.clickable { onBackClick() }
                )
                Text(
                    text = "Speed Math Gym",
                    style = androidx.compose.material3.MaterialTheme.typography.titleLarge.copy(
                        fontFamily = OutfitFamily,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                )
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(DeepBlue)
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "🔥 $streak STREAK",
                        style = androidx.compose.material3.MaterialTheme.typography.labelSmall.copy(
                            color = AccentRed,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Score Dashboard
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = DarkSurface)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("SCORE", style = androidx.compose.material3.MaterialTheme.typography.labelSmall.copy(color = TextSecondary))
                        Text("$score", style = androidx.compose.material3.MaterialTheme.typography.headlineMedium.copy(fontFamily = OutfitFamily, color = AccentRed, fontWeight = FontWeight.Bold))
                    }
                    Box(modifier = Modifier.width(1.dp).height(36.dp).background(DividerColor))
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("CATEGORY", style = androidx.compose.material3.MaterialTheme.typography.labelSmall.copy(color = TextSecondary))
                        Text(selectedCategory, style = androidx.compose.material3.MaterialTheme.typography.titleSmall.copy(color = TextPrimary, fontWeight = FontWeight.Bold))
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Category Selector Chips
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                categories.forEach { cat ->
                    val isSelected = cat == selectedCategory
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (isSelected) AccentRed else CardSurface)
                            .clickable {
                                selectedCategory = cat
                                currentQuestion = MathQuestionGenerator.generate(cat)
                                userInput = ""
                                statusMessage = ""
                            }
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = cat,
                            style = androidx.compose.material3.MaterialTheme.typography.labelSmall.copy(
                                color = if (isSelected) Color.White else TextPrimary,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Practice Question Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, if (isError) ErrorRed else DividerColor, RoundedCornerShape(18.dp)),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = DarkSurface)
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = currentQuestion.displayText,
                        style = androidx.compose.material3.MaterialTheme.typography.headlineMedium.copy(
                            fontFamily = OutfitFamily,
                            color = TextPrimary,
                            textAlign = TextAlign.Center,
                            fontWeight = FontWeight.SemiBold
                        )
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Answer Field
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(InputSurface)
                            .border(1.dp, if (isError) ErrorRed else DividerColor, RoundedCornerShape(12.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (userInput.isEmpty()) "?" else userInput,
                            style = androidx.compose.material3.MaterialTheme.typography.headlineSmall.copy(
                                color = if (userInput.isEmpty()) TextSecondary else TextPrimary,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                }
            }

            if (statusMessage.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = statusMessage,
                    style = androidx.compose.material3.MaterialTheme.typography.bodyMedium.copy(
                        color = if (isError) ErrorRed else SuccessGreen,
                        fontWeight = FontWeight.Bold
                    )
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Numpad & Submit Button
            CustomNumPad(
                onDigitClick = { digit ->
                    if (digit == "." && userInput.contains(".")) return@CustomNumPad
                    if (digit == "-" && userInput.isNotEmpty()) return@CustomNumPad
                    userInput += digit
                    isError = false
                },
                onBackspaceClick = {
                    if (userInput.isNotEmpty()) userInput = userInput.dropLast(1)
                },
                onClearClick = {
                    userInput = ""
                    isError = false
                }
            )

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = {
                    val inputVal = userInput.toDoubleOrNull()
                    if (inputVal != null && MathQuestionGenerator.checkAnswer(currentQuestion, inputVal)) {
                        score += 100
                        streak += 1
                        statusMessage = "✓ Correct! +100 Points"
                        isError = false
                        userInput = ""
                        currentQuestion = MathQuestionGenerator.generate(selectedCategory)
                    } else {
                        streak = 0
                        statusMessage = "✕ Incorrect! Try again."
                        isError = true
                        userInput = ""
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = AccentRed),
                enabled = userInput.isNotEmpty()
            ) {
                Text(
                    text = "CHECK ANSWER",
                    style = androidx.compose.material3.MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                )
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

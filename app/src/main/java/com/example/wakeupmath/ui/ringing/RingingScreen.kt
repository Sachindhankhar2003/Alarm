package com.example.wakeupmath.ui.ringing

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.wakeupmath.R
import com.example.wakeupmath.theme.AccentRed
import com.example.wakeupmath.theme.CardSurface
import com.example.wakeupmath.theme.Charcoal
import com.example.wakeupmath.theme.DarkSurface
import com.example.wakeupmath.theme.DeepBlue
import com.example.wakeupmath.theme.DividerColor
import com.example.wakeupmath.theme.ErrorRed
import com.example.wakeupmath.theme.InputSurface
import com.example.wakeupmath.theme.NumPadButton
import com.example.wakeupmath.theme.OutfitFamily
import com.example.wakeupmath.theme.SuccessGreen
import com.example.wakeupmath.theme.TextPrimary
import com.example.wakeupmath.theme.TextSecondary
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.roundToInt

import androidx.compose.ui.platform.LocalContext

@Composable
fun RingingScreen(
    label: String,
    difficulty: String,
    onShakeDetected: () -> Unit = {},
    onDismissed: () -> Unit,
    viewModel: RingingViewModel = viewModel(factory = object : androidx.lifecycle.ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return RingingViewModel(difficulty) as T
        }
    })
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()
    val currentTime = remember {
        SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())
    }

    val shakeOffset = remember { Animatable(0f) }

    LaunchedEffect(uiState.shakeCounter) {
        if (uiState.shakeCounter > 0) {
            val shakeValues = listOf(-20f, 20f, -15f, 15f, -10f, 10f, -5f, 5f, 0f)
            for (value in shakeValues) {
                shakeOffset.animateTo(
                    targetValue = value,
                    animationSpec = tween(durationMillis = 35)
                )
            }
        }
    }

    LaunchedEffect(uiState.isCorrect) {
        if (uiState.isCorrect) {
            kotlinx.coroutines.delay(1200)
            onDismissed()
        }
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Charcoal
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp, vertical = 12.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Header: Time & Label
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(top = 12.dp)
                ) {
                    Text(
                        text = currentTime,
                        style = androidx.compose.material3.MaterialTheme.typography.displayLarge.copy(
                            fontSize = 54.sp,
                            fontWeight = FontWeight.Bold,
                            color = AccentRed
                        )
                    )
                    Text(
                        text = label.ifBlank { "WAKE UP!" },
                        style = androidx.compose.material3.MaterialTheme.typography.titleLarge.copy(
                            color = TextPrimary,
                            fontWeight = FontWeight.Medium
                        )
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(DeepBlue)
                            .padding(horizontal = 12.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "DIFFICULTY: ${uiState.difficulty}",
                            style = androidx.compose.material3.MaterialTheme.typography.labelMedium.copy(
                                color = TextSecondary,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Math Question Card with Dynamic Font & Responsive Height
                val questionText = uiState.question.displayText
                val dynamicFontSize = when {
                    questionText.contains("\n") -> 22.sp
                    questionText.length > 25 -> 24.sp
                    questionText.length > 15 -> 28.sp
                    else -> 34.sp
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .offset { IntOffset(shakeOffset.value.roundToInt(), 0) }
                ) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(
                                width = 1.5.dp,
                                color = if (uiState.isWrongAnswer) ErrorRed else AccentRed,
                                shape = RoundedCornerShape(20.dp)
                            ),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = DarkSurface)
                    ) {
                        Column(
                            modifier = Modifier.padding(horizontal = 18.dp, vertical = 16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "SOLVE TO DISMISS",
                                style = androidx.compose.material3.MaterialTheme.typography.labelMedium.copy(
                                    color = AccentRed,
                                    letterSpacing = 2.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = questionText,
                                style = androidx.compose.material3.MaterialTheme.typography.headlineLarge.copy(
                                    fontFamily = OutfitFamily,
                                    fontSize = dynamicFontSize,
                                    lineHeight = (dynamicFontSize.value * 1.25f).sp,
                                    color = TextPrimary,
                                    textAlign = TextAlign.Center,
                                    fontWeight = FontWeight.SemiBold
                                )
                            )
                            Spacer(modifier = Modifier.height(14.dp))

                            // Answer Display Field
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(52.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(InputSurface)
                                    .border(
                                        width = 1.dp,
                                        color = when {
                                            uiState.isCorrect -> SuccessGreen
                                            uiState.isWrongAnswer -> ErrorRed
                                            else -> DividerColor
                                        },
                                        shape = RoundedCornerShape(12.dp)
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = if (uiState.userInput.isEmpty()) "?" else uiState.userInput,
                                    style = androidx.compose.material3.MaterialTheme.typography.headlineSmall.copy(
                                        color = when {
                                            uiState.isCorrect -> SuccessGreen
                                            uiState.isWrongAnswer -> ErrorRed
                                            uiState.userInput.isEmpty() -> TextSecondary
                                            else -> TextPrimary
                                        },
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Custom Numpad & Adaptive Submit Button
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CustomNumPad(
                        onDigitClick = { viewModel.onDigitPress(it) },
                        onBackspaceClick = { viewModel.onBackspace() },
                        onClearClick = { viewModel.onClear() }
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Dynamically Scaled Submit Button
                    Button(
                        onClick = { viewModel.onSubmit(context) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(54.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (uiState.isCorrect) SuccessGreen else AccentRed,
                            disabledContainerColor = CardSurface
                        ),
                        enabled = uiState.userInput.isNotEmpty() && !uiState.isCorrect
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_alarm),
                                contentDescription = null,
                                tint = if (uiState.userInput.isNotEmpty()) Color.White else TextSecondary,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = when {
                                    uiState.isCorrect -> "ALARM DISMISSED!"
                                    uiState.userInput.isNotEmpty() -> "SUBMIT ANSWER (${uiState.userInput})"
                                    else -> "ENTER ANSWER TO SUBMIT"
                                },
                                style = androidx.compose.material3.MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = if (uiState.userInput.isNotEmpty()) Color.White else TextSecondary
                                )
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }

            // Solved Confirmation Overlay
            AnimatedVisibility(
                visible = uiState.isCorrect,
                enter = fadeIn() + scaleIn(),
                exit = fadeOut() + scaleOut()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Charcoal.copy(alpha = 0.95f)),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Box(
                            modifier = Modifier
                                .size(80.dp)
                                .clip(CircleShape)
                                .background(SuccessGreen),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "✓",
                                fontSize = 48.sp,
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(modifier = Modifier.height(24.dp))
                        Text(
                            text = "Correct! You are Awake.",
                            style = androidx.compose.material3.MaterialTheme.typography.headlineMedium.copy(
                                color = TextPrimary,
                                fontWeight = FontWeight.Bold
                            )
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Shutting off alarm...",
                            style = androidx.compose.material3.MaterialTheme.typography.bodyLarge.copy(
                                color = TextSecondary
                            )
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun CustomNumPad(
    onDigitClick: (String) -> Unit,
    onBackspaceClick: () -> Unit,
    onClearClick: () -> Unit
) {
    val buttons = listOf(
        listOf("1", "2", "3"),
        listOf("4", "5", "6"),
        listOf("7", "8", "9"),
        listOf(".", "0", "-")
    )

    Column(
        verticalArrangement = Arrangement.spacedBy(10.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        buttons.forEach { row ->
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                row.forEach { label ->
                    NumPadKey(
                        text = label,
                        modifier = Modifier.weight(1f),
                        onClick = { onDigitClick(label) }
                    )
                }
            }
        }
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            NumPadKey(
                text = "CLEAR",
                modifier = Modifier.weight(1f),
                backgroundColor = CardSurface,
                textColor = AccentRed,
                onClick = onClearClick
            )
            NumPadKey(
                text = "⌫",
                modifier = Modifier.weight(1f),
                backgroundColor = CardSurface,
                textColor = TextPrimary,
                onClick = onBackspaceClick
            )
        }
    }
}

@Composable
fun NumPadKey(
    text: String,
    modifier: Modifier = Modifier,
    backgroundColor: Color = NumPadButton,
    textColor: Color = TextPrimary,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .height(52.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(backgroundColor)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            style = androidx.compose.material3.MaterialTheme.typography.titleLarge.copy(
                color = textColor,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp
            )
        )
    }
}

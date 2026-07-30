package com.example.wakeupmath.ui.ringing

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.wakeupmath.data.local.AlarmDatabase
import com.example.wakeupmath.data.local.WakeStatEntity
import com.example.wakeupmath.domain.MathQuestion
import com.example.wakeupmath.domain.MathQuestionGenerator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class RingingUiState(
    val question: MathQuestion = MathQuestionGenerator.generate("MIXED"),
    val userInput: String = "",
    val isCorrect: Boolean = false,
    val isWrongAnswer: Boolean = false,
    val shakeCounter: Int = 0,
    val difficulty: String = "MIXED",
    val phoneShakesCompleted: Int = 0,
    val requiredPhoneShakes: Int = 15,
    val isShakeMissionUnlocked: Boolean = false,
    val wrongAttemptsCount: Int = 0,
    val questionsSolved: Int = 0,
    val requiredQuestionsToSolve: Int = 1,
    val solveTimeSeconds: Long = 0,
)

class RingingViewModel(difficulty: String = "MIXED") : ViewModel() {

    private val startTimeMs = System.currentTimeMillis()

    private val _uiState = MutableStateFlow(
        RingingUiState(
            question = MathQuestionGenerator.generate(difficulty),
            difficulty = difficulty,
        )
    )
    val uiState: StateFlow<RingingUiState> = _uiState.asStateFlow()

    fun onPhoneShake() {
        val state = _uiState.value
        if (state.isShakeMissionUnlocked) return
        val newShakes = state.phoneShakesCompleted + 1
        val unlocked = newShakes >= state.requiredPhoneShakes
        _uiState.value = state.copy(
            phoneShakesCompleted = newShakes,
            isShakeMissionUnlocked = unlocked
        )
    }

    fun onDigitPress(digit: String) {
        if (_uiState.value.isCorrect) return
        val current = _uiState.value.userInput
        // Prevent multiple decimal points
        if (digit == "." && current.contains(".")) return
        // Prevent multiple minus signs or minus not at start
        if (digit == "-" && current.isNotEmpty()) return
        _uiState.value = _uiState.value.copy(
            userInput = current + digit,
            isWrongAnswer = false,
        )
    }

    fun onBackspace() {
        if (_uiState.value.isCorrect) return
        val current = _uiState.value.userInput
        if (current.isNotEmpty()) {
            _uiState.value = _uiState.value.copy(
                userInput = current.dropLast(1),
                isWrongAnswer = false,
            )
        }
    }

    fun onClear() {
        if (_uiState.value.isCorrect) return
        _uiState.value = _uiState.value.copy(
            userInput = "",
            isWrongAnswer = false,
        )
    }

    fun onSubmit(context: Context) {
        val state = _uiState.value
        if (state.isCorrect) return
        val input = state.userInput.toDoubleOrNull()
        if (input == null) {
            _uiState.value = state.copy(
                isWrongAnswer = true,
                shakeCounter = state.shakeCounter + 1,
                wrongAttemptsCount = state.wrongAttemptsCount + 1,
            )
            return
        }
        if (MathQuestionGenerator.checkAnswer(state.question, input)) {
            val newSolved = state.questionsSolved + 1
            val elapsedSec = ((System.currentTimeMillis() - startTimeMs) / 1000).coerceAtLeast(1)
            _uiState.value = state.copy(
                isCorrect = true,
                isWrongAnswer = false,
                questionsSolved = newSolved,
                solveTimeSeconds = elapsedSec
            )
            // Save stat to Room database
            viewModelScope.launch {
                try {
                    val db = AlarmDatabase.getInstance(context)
                    db.wakeStatDao().insertStat(
                        WakeStatEntity(
                            solveTimeSeconds = elapsedSec,
                            difficulty = state.difficulty,
                            success = true
                        )
                    )
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        } else {
            val newWrong = state.wrongAttemptsCount + 1
            _uiState.value = state.copy(
                isWrongAnswer = true,
                shakeCounter = state.shakeCounter + 1,
                wrongAttemptsCount = newWrong,
                userInput = "",
            )
        }
    }

    fun generateNewQuestion() {
        _uiState.value = _uiState.value.copy(
            question = MathQuestionGenerator.generate(_uiState.value.difficulty),
            userInput = "",
            isCorrect = false,
            isWrongAnswer = false,
        )
    }
}

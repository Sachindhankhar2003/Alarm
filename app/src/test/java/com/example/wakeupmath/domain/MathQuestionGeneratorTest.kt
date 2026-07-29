package com.example.wakeupmath.domain

import org.junit.Assert.assertTrue
import org.junit.Test

class MathQuestionGeneratorTest {

    @Test
    fun testGenerateTrigonometryQuestion() {
        val question = MathQuestionGenerator.generate("TRIGONOMETRY")
        assertTrue(question.displayText.isNotEmpty())
        assertTrue(MathQuestionGenerator.checkAnswer(question, question.correctAnswer))
    }

    @Test
    fun testGenerateAlgebraQuestion() {
        val question = MathQuestionGenerator.generate("ALGEBRA")
        assertTrue(question.displayText.isNotEmpty())
        assertTrue(MathQuestionGenerator.checkAnswer(question, question.correctAnswer))
    }

    @Test
    fun testGenerateCalculusQuestion() {
        val question = MathQuestionGenerator.generate("CALCULUS")
        assertTrue(question.displayText.isNotEmpty())
        assertTrue(MathQuestionGenerator.checkAnswer(question, question.correctAnswer))
    }

    @Test
    fun testGenerateLogarithmQuestion() {
        val question = MathQuestionGenerator.generate("LOGARITHMS")
        assertTrue(question.displayText.isNotEmpty())
        assertTrue(MathQuestionGenerator.checkAnswer(question, question.correctAnswer))
    }

    @Test
    fun testGenerateMixedQuestion() {
        val question = MathQuestionGenerator.generate("MIXED")
        assertTrue(question.displayText.isNotEmpty())
        assertTrue(MathQuestionGenerator.checkAnswer(question, question.correctAnswer))
    }

    @Test
    fun testCheckAnswerWithTolerance() {
        val question = MathQuestion(displayText = "Test Question", correctAnswer = 10.0, tolerance = 0.1)
        assertTrue(MathQuestionGenerator.checkAnswer(question, 10.0))
        assertTrue(MathQuestionGenerator.checkAnswer(question, 10.05))
        assertTrue(!MathQuestionGenerator.checkAnswer(question, 10.2))
    }
}

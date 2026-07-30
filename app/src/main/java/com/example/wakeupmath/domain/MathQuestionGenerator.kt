package com.example.wakeupmath.domain

import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.ln
import kotlin.math.log2
import kotlin.math.pow
import kotlin.math.roundToInt
import kotlin.math.sin
import kotlin.math.sqrt
import kotlin.math.tan
import kotlin.random.Random

data class MathQuestion(
    val displayText: String,
    val correctAnswer: Double,
    val tolerance: Double = 0.01,
)

object MathQuestionGenerator {

    fun generate(difficulty: String): MathQuestion {
        return when (difficulty.uppercase()) {
            "EASY" -> generateEasyQuestion()
            "BODMAS" -> generateBodmasQuestion()
            "TRICKY" -> generateTrickyQuestion()
            "TRIGONOMETRY" -> generateTrigQuestion()
            "ALGEBRA" -> generateAlgebraQuestion()
            "CALCULUS" -> generateCalculusQuestion()
            "LOGARITHMS" -> generateLogQuestion()
            else -> { // MIXED
                when (Random.nextInt(7)) {
                    0 -> generateEasyQuestion()
                    1 -> generateBodmasQuestion()
                    2 -> generateTrickyQuestion()
                    3 -> generateTrigQuestion()
                    4 -> generateAlgebraQuestion()
                    5 -> generateCalculusQuestion()
                    else -> generateLogQuestion()
                }
            }
        }
    }

    fun checkAnswer(question: MathQuestion, userAnswer: Double): Boolean {
        return abs(userAnswer - question.correctAnswer) <= question.tolerance
    }

    // ─── Trigonometry ─────────────────────────────────────────────

    private val trigAngles = listOf(0, 30, 45, 60, 90, 120, 135, 150, 180)

    private fun sinDeg(deg: Int): Double = sin(Math.toRadians(deg.toDouble()))
    private fun cosDeg(deg: Int): Double = cos(Math.toRadians(deg.toDouble()))
    private fun tanDeg(deg: Int): Double = tan(Math.toRadians(deg.toDouble()))

    private fun generateTrigQuestion(): MathQuestion {
        return when (Random.nextInt(10)) {
            0 -> {
                // sin(a) + cos(b)
                val a = trigAngles.random()
                val b = trigAngles.random()
                val answer = sinDeg(a) + cosDeg(b)
                MathQuestion("sin(${a}°) + cos(${b}°) = ?", answer, 0.01)
            }
            1 -> {
                // sin²(a) + cos²(a) — should always be 1, but tests awareness
                val a = trigAngles.filter { it != 90 }.random()
                MathQuestion("sin²(${a}°) + cos²(${a}°) = ?", 1.0, 0.01)
            }
            2 -> {
                // tan(a) × cos(a)
                val a = listOf(30, 45, 60).random()
                val answer = tanDeg(a) * cosDeg(a)
                MathQuestion("tan(${a}°) × cos(${a}°) = ?", answer, 0.01)
            }
            3 -> {
                // 2·sin(a)·cos(a) = sin(2a)
                val a = listOf(15, 30, 45).random()
                val answer = sin(Math.toRadians(2.0 * a))
                MathQuestion("2·sin(${a}°)·cos(${a}°) = ?", answer, 0.01)
            }
            4 -> {
                // cos(a) - cos(b)
                val a = trigAngles.random()
                val b = trigAngles.random()
                val answer = cosDeg(a) - cosDeg(b)
                MathQuestion("cos(${a}°) − cos(${b}°) = ?", answer, 0.01)
            }
            5 -> {
                // sin(a) × sin(b) + cos(a) × cos(b) = cos(a - b)
                val a = listOf(30, 45, 60).random()
                val b = listOf(30, 45, 60).random()
                val answer = cosDeg(a - b)
                MathQuestion("sin(${a}°)·sin(${b}°) + cos(${a}°)·cos(${b}°) = ?", answer, 0.01)
            }
            6 -> {
                // Simple: what is sin(a)?
                val a = listOf(0, 30, 45, 60, 90).random()
                MathQuestion("sin(${a}°) = ?", sinDeg(a), 0.01)
            }
            7 -> {
                // What is cos(a)?
                val a = listOf(0, 30, 45, 60, 90).random()
                MathQuestion("cos(${a}°) = ?", cosDeg(a), 0.01)
            }
            8 -> {
                // sin(a) / cos(a)
                val a = listOf(30, 45, 60).random()
                val answer = tanDeg(a)
                MathQuestion("sin(${a}°) / cos(${a}°) = ?", answer, 0.01)
            }
            else -> {
                // cos²(a) - sin²(a) = cos(2a)
                val a = listOf(15, 30, 45).random()
                val answer = cosDeg(2 * a)
                MathQuestion("cos²(${a}°) − sin²(${a}°) = ?", answer, 0.01)
            }
        }
    }

    // ─── Algebra ──────────────────────────────────────────────────

    private fun generateAlgebraQuestion(): MathQuestion {
        return when (Random.nextInt(10)) {
            0 -> {
                // Solve x² - (r1+r2)x + r1*r2 = 0, ask for smaller root
                val r1 = Random.nextInt(-5, 6)
                val r2 = Random.nextInt(r1 + 1, r1 + 8)
                val b = -(r1 + r2)
                val c = r1 * r2
                val bStr = if (b >= 0) "+ $b" else "− ${-b}"
                val cStr = if (c >= 0) "+ $c" else "− ${-c}"
                MathQuestion(
                    "Solve: x² $bStr·x $cStr = 0\n(smaller root)",
                    r1.toDouble(),
                    0.01,
                )
            }
            1 -> {
                // Solve x² - (r1+r2)x + r1*r2 = 0, ask for larger root
                val r1 = Random.nextInt(-5, 6)
                val r2 = Random.nextInt(r1 + 1, r1 + 8)
                val b = -(r1 + r2)
                val c = r1 * r2
                val bStr = if (b >= 0) "+ $b" else "− ${-b}"
                val cStr = if (c >= 0) "+ $c" else "− ${-c}"
                MathQuestion(
                    "Solve: x² $bStr·x $cStr = 0\n(larger root)",
                    r2.toDouble(),
                    0.01,
                )
            }
            2 -> {
                // Power simplification: a^m × a^n
                val a = listOf(2, 3, 5).random()
                val m = Random.nextInt(2, 6)
                val n = Random.nextInt(2, 6)
                val answer = a.toDouble().pow(m + n)
                MathQuestion("${a}^${m} × ${a}^${n} = ?", answer, 0.5)
            }
            3 -> {
                // (a^m)^n
                val a = listOf(2, 3).random()
                val m = Random.nextInt(2, 4)
                val n = Random.nextInt(2, 4)
                val answer = a.toDouble().pow(m * n)
                MathQuestion("(${a}^${m})^${n} = ?", answer, 0.5)
            }
            4 -> {
                // √(n²) = n
                val n = Random.nextInt(4, 20)
                MathQuestion("√(${n * n}) = ?", n.toDouble(), 0.01)
            }
            5 -> {
                // a^m / a^n
                val a = listOf(2, 3, 5).random()
                val m = Random.nextInt(5, 10)
                val n = Random.nextInt(2, m)
                val answer = a.toDouble().pow(m - n)
                MathQuestion("${a}^${m} / ${a}^${n} = ?", answer, 0.5)
            }
            6 -> {
                // Sum of arithmetic sequence: 1 + 2 + ... + n
                val n = Random.nextInt(8, 25)
                val answer = n * (n + 1) / 2.0
                MathQuestion("1 + 2 + 3 + ⋯ + $n = ?", answer, 0.01)
            }
            7 -> {
                // |a - b|
                val a = Random.nextInt(-20, 21)
                val b = Random.nextInt(-20, 21)
                MathQuestion("|$a − ($b)| = ?", abs(a - b).toDouble(), 0.01)
            }
            8 -> {
                // Linear equation: ax + b = c, solve for x
                val a = Random.nextInt(2, 10)
                val x = Random.nextInt(-10, 11)
                val b = Random.nextInt(-20, 21)
                val c = a * x + b
                MathQuestion("Solve: ${a}x + ($b) = $c\nx = ?", x.toDouble(), 0.01)
            }
            else -> {
                // Factorial (small)
                val n = Random.nextInt(4, 8)
                var fact = 1L
                for (i in 2..n) fact *= i
                MathQuestion("$n! = ?", fact.toDouble(), 0.5)
            }
        }
    }

    // ─── Calculus ─────────────────────────────────────────────────

    private fun generateCalculusQuestion(): MathQuestion {
        return when (Random.nextInt(8)) {
            0 -> {
                // d/dx [x^n] at x=a → n·a^(n-1)
                val n = Random.nextInt(2, 6)
                val a = Random.nextInt(1, 5)
                val answer = n * a.toDouble().pow(n - 1)
                MathQuestion("d/dx [x^$n] at x = $a\n= ?", answer, 0.01)
            }
            1 -> {
                // ∫₀ᵃ 2x dx = a²
                val a = Random.nextInt(1, 8)
                MathQuestion("∫₀${superscript(a)} 2x dx = ?", (a * a).toDouble(), 0.01)
            }
            2 -> {
                // ∫₀ᵃ x dx = a²/2
                val a = Random.nextInt(2, 10)
                MathQuestion("∫₀${superscript(a)} x dx = ?", a * a / 2.0, 0.01)
            }
            3 -> {
                // d/dx [ax²+bx] at x=c → 2ac+b
                val a = Random.nextInt(1, 5)
                val b = Random.nextInt(-5, 6)
                val c = Random.nextInt(1, 5)
                val answer = 2.0 * a * c + b
                val bStr = if (b >= 0) "+ $b" else "− ${-b}"
                MathQuestion("d/dx [${a}x² $bStr·x]\nat x = $c = ?", answer, 0.01)
            }
            4 -> {
                // ∫₁ᵃ 1/x dx = ln(a)
                val a = listOf(2, 3, 5, 7).random()
                val answer = ln(a.toDouble())
                MathQuestion("∫₁${superscript(a)} (1/x) dx = ?\n(answer as decimal)", answer, 0.05)
            }
            5 -> {
                // d/dx [x^n + x^m] at x=1 → n + m
                val n = Random.nextInt(2, 6)
                val m = Random.nextInt(2, 6)
                MathQuestion("d/dx [x^$n + x^$m]\nat x = 1 = ?", (n + m).toDouble(), 0.01)
            }
            6 -> {
                // ∫₀ᵃ 3x² dx = a³
                val a = Random.nextInt(1, 6)
                val answer = a.toDouble().pow(3)
                MathQuestion("∫₀${superscript(a)} 3x² dx = ?", answer, 0.01)
            }
            else -> {
                // ∫₁ᵃ 2x dx = a²-1
                val a = Random.nextInt(2, 8)
                val answer = a.toDouble().pow(2) - 1
                MathQuestion("∫₁${superscript(a)} 2x dx = ?", answer, 0.01)
            }
        }
    }

    private fun superscript(n: Int): String {
        val superDigits = mapOf(
            '0' to '⁰', '1' to '¹', '2' to '²', '3' to '³', '4' to '⁴',
            '5' to '⁵', '6' to '⁶', '7' to '⁷', '8' to '⁸', '9' to '⁹',
        )
        return n.toString().map { superDigits[it] ?: it }.joinToString("")
    }

    // ─── Logarithms ───────────────────────────────────────────────

    private fun generateLogQuestion(): MathQuestion {
        return when (Random.nextInt(8)) {
            0 -> {
                // log₂(2^n) = n
                val n = Random.nextInt(2, 11)
                val value = (2.0).pow(n).roundToInt()
                MathQuestion("log₂($value) = ?", n.toDouble(), 0.01)
            }
            1 -> {
                // log₁₀(10^n)
                val n = Random.nextInt(1, 7)
                val value = (10.0).pow(n).toLong()
                MathQuestion("log₁₀($value) = ?", n.toDouble(), 0.01)
            }
            2 -> {
                // log₃(3^n)
                val n = Random.nextInt(2, 7)
                val value = (3.0).pow(n).roundToInt()
                MathQuestion("log₃($value) = ?", n.toDouble(), 0.01)
            }
            3 -> {
                // log₂(a) + log₂(b) = log₂(a*b)
                val a = listOf(2, 4, 8, 16).random()
                val b = listOf(2, 4, 8, 16).random()
                val answer = log2((a * b).toDouble())
                MathQuestion("log₂($a) + log₂($b) = ?", answer, 0.01)
            }
            4 -> {
                // log₅(5^n)
                val n = Random.nextInt(2, 5)
                val value = (5.0).pow(n).roundToInt()
                MathQuestion("log₅($value) = ?", n.toDouble(), 0.01)
            }
            5 -> {
                // log₂(a) - log₂(b) = log₂(a/b)
                val powers = listOf(1, 2, 4, 8, 16, 32, 64)
                val a = powers.filter { it >= 4 }.random()
                val b = powers.filter { it <= a && it >= 1 }.random()
                val answer = log2(a.toDouble()) - log2(b.toDouble())
                MathQuestion("log₂($a) − log₂($b) = ?", answer, 0.01)
            }
            6 -> {
                // n·log₂(2) = n
                val n = Random.nextInt(3, 12)
                MathQuestion("$n · log₂(2) = ?", n.toDouble(), 0.01)
            }
            else -> {
                // log₄(4^n)
                val n = Random.nextInt(2, 6)
                val value = (4.0).pow(n).roundToInt()
                MathQuestion("log₄($value) = ?", n.toDouble(), 0.01)
            }
        }
    }

    // ─── Easy Mode ────────────────────────────────────────────────

    private fun generateEasyQuestion(): MathQuestion {
        return when (Random.nextInt(6)) {
            0 -> {
                val a = Random.nextInt(5, 30)
                val b = Random.nextInt(5, 30)
                MathQuestion("$a + $b = ?", (a + b).toDouble(), 0.01)
            }
            1 -> {
                val a = Random.nextInt(20, 60)
                val b = Random.nextInt(5, 20)
                MathQuestion("$a − $b = ?", (a - b).toDouble(), 0.01)
            }
            2 -> {
                val a = Random.nextInt(2, 10)
                val b = Random.nextInt(2, 10)
                MathQuestion("$a × $b = ?", (a * b).toDouble(), 0.01)
            }
            3 -> {
                val b = Random.nextInt(2, 10)
                val ans = Random.nextInt(2, 10)
                val a = b * ans
                MathQuestion("$a / $b = ?", ans.toDouble(), 0.01)
            }
            4 -> {
                val a = Random.nextInt(10, 25)
                val b = Random.nextInt(5, 15)
                val c = Random.nextInt(3, 12)
                MathQuestion("$a + $b − $c = ?", (a + b - c).toDouble(), 0.01)
            }
            else -> {
                val a = Random.nextInt(12, 35)
                val b = Random.nextInt(10, 25)
                MathQuestion("$a + $b = ?", (a + b).toDouble(), 0.01)
            }
        }
    }

    // ─── BODMAS Mode ──────────────────────────────────────────────

    private fun generateBodmasQuestion(): MathQuestion {
        return when (Random.nextInt(6)) {
            0 -> {
                // a + b × c
                val a = Random.nextInt(2, 15)
                val b = Random.nextInt(2, 9)
                val c = Random.nextInt(2, 9)
                val answer = a + (b * c)
                MathQuestion("$a + $b × $c = ?", answer.toDouble(), 0.01)
            }
            1 -> {
                // a × b - c / d
                val cQuotient = Random.nextInt(2, 8)
                val d = Random.nextInt(2, 6)
                val c = cQuotient * d
                val a = Random.nextInt(3, 8)
                val b = Random.nextInt(3, 8)
                val answer = (a * b) - cQuotient
                MathQuestion("$a × $b − $c / $d = ?", answer.toDouble(), 0.01)
            }
            2 -> {
                // (a + b) × c
                val a = Random.nextInt(2, 10)
                val b = Random.nextInt(2, 10)
                val c = Random.nextInt(2, 7)
                val answer = (a + b) * c
                MathQuestion("($a + $b) × $c = ?", answer.toDouble(), 0.01)
            }
            3 -> {
                // a - b / c + d
                val bQuotient = Random.nextInt(2, 7)
                val c = Random.nextInt(2, 6)
                val b = bQuotient * c
                val a = Random.nextInt(15, 40)
                val d = Random.nextInt(3, 12)
                val answer = a - bQuotient + d
                MathQuestion("$a − $b / $c + $d = ?", answer.toDouble(), 0.01)
            }
            4 -> {
                // a × b + c × d
                val a = Random.nextInt(2, 6)
                val b = Random.nextInt(3, 8)
                val c = Random.nextInt(2, 6)
                val d = Random.nextInt(3, 8)
                val answer = (a * b) + (c * d)
                MathQuestion("$a × $b + $c × $d = ?", answer.toDouble(), 0.01)
            }
            else -> {
                // (a - b) × (c + d)
                val a = Random.nextInt(8, 16)
                val b = Random.nextInt(2, 7)
                val c = Random.nextInt(2, 6)
                val d = Random.nextInt(1, 5)
                val answer = (a - b) * (c + d)
                MathQuestion("($a − $b) × ($c + $d) = ?", answer.toDouble(), 0.01)
            }
        }
    }

    // ─── Tricky Mode ──────────────────────────────────────────────

    private fun generateTrickyQuestion(): MathQuestion {
        val questions = listOf(
            MathQuestion("8 / 2 × (2 + 2) = ?", 16.0, 0.01),
            MathQuestion("5 − 5 × 5 + 5 = ?", -15.0, 0.01),
            MathQuestion("2 + 2 × 2 − 2 / 2 = ?", 5.0, 0.01),
            MathQuestion("7 + 7 / 7 + 7 × 7 − 7 = ?", 50.0, 0.01),
            MathQuestion("(-4) × (-5) − 6 / 2 = ?", 17.0, 0.01),
            MathQuestion("(3 + 3) × 3 − 3 / 3 = ?", 17.0, 0.01),
            MathQuestion("10 − 3 × 4 + 5 = ?", 3.0, 0.01),
            MathQuestion("100 / 5 × 2 = ?", 40.0, 0.01),
            MathQuestion("3 + 3 × 3 − 3 = ?", 9.0, 0.01),
            MathQuestion("6 / 2 × (1 + 2) = ?", 9.0, 0.01),
            MathQuestion("50 − 10 × 4 + 2² = ?", 14.0, 0.01),
            MathQuestion("(-3) × 4 + 20 / (-2) = ?", -22.0, 0.01)
        )
        return questions.random()
    }
}

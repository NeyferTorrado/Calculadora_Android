package com.example.calculadora.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class CalculatorViewModel : ViewModel() {
    private val _displayText = MutableStateFlow("")
    val displayText: StateFlow<String> = _displayText

    private var currentExpression = ""

    fun onButtonClick(value: String) {
        viewModelScope.launch {
            when (value) {
                "C" -> clear()
                "AC" -> clearAll() // Limpiar todo
                "=" -> calculateResult()
                else -> appendToExpression(value)
            }
        }
    }

    private fun clearAll() {
        currentExpression = ""
        _displayText.value = ""
    }

    private fun appendToExpression(value: String) {
        currentExpression += value
        _displayText.value = currentExpression
    }

    private fun clear() {
        currentExpression = ""
        _displayText.value = ""
    }

    private fun calculateResult() {
        try {
            val result = evaluateExpression(currentExpression)
            _displayText.value = result.toString()
            currentExpression = result.toString()
        } catch (e: Exception) {
            _displayText.value = "Error"
        }
    }

    private fun evaluateExpression(expression: String): Double {
        return object : Any() {
            var pos = -1
            var ch = 0

            fun nextChar() {
                ch = if (++pos < expression.length) expression[pos].code else -1
            }

            fun eat(charToEat: Int): Boolean {
                while (ch == ' '.code) nextChar()
                if (ch == charToEat) {
                    nextChar()
                    return true
                }
                return false
            }

            fun parse(): Double {
                nextChar()
                val x = parseExpression()
                if (pos < expression.length) throw RuntimeException("Carácter inesperado: " + ch.toChar())
                return x
            }

            fun parseExpression(): Double {
                var x = parseTerm()
                while (true) {
                    when {
                        eat('+'.code) -> x += parseTerm()
                        eat('-'.code) -> x -= parseTerm()
                        else -> return x
                    }
                }
            }

            fun parseTerm(): Double {
                var x = parseFactor()
                while (true) {
                    when {
                        eat('*'.code) -> x *= parseFactor()
                        eat('/'.code) -> x /= parseFactor()
                        else -> return x
                    }
                }
            }

            fun parseFactor(): Double {
                if (eat('+'.code)) return parseFactor()
                if (eat('-'.code)) return -parseFactor()
                var x: Double
                val startPos = pos
                if (eat('('.code)) {
                    x = parseExpression()
                    eat(')'.code)
                } else if (ch >= '0'.code && ch <= '9'.code || ch == '.'.code) {
                    while (ch >= '0'.code && ch <= '9'.code || ch == '.'.code) nextChar()
                    x = expression.substring(startPos, pos).toDouble()
                } else {
                    throw RuntimeException("Carácter inesperado: " + ch.toChar())
                }
                return x
            }
        }.parse()
    }
}

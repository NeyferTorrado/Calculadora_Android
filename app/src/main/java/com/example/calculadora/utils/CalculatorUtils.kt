package com.example.calculadora.utils

object CalculatorUtils {
    fun isOperator(char: Char): Boolean {
        return char in listOf('+', '-', '×', '÷')
    }

    fun isValidExpression(expression: String): Boolean {
        return expression.isNotEmpty() && !isOperator(expression.last())
    }

    fun formatExpression(expression: String): String {
        return expression.replace("×", "*").replace("÷", "/")
    }
}
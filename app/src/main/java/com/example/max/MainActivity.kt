package com.example.max

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView

class MainActivity : AppCompatActivity() {

    private lateinit var etInput: EditText
    private lateinit var tvOperation: TextView
    private lateinit var tvResult: TextView

    // Состояния калькулятора
    private var currentInput: String = "0"
    private var previousValue: Double = 0.0
    private var currentOperator: String? = null
    private var isNewOperation: Boolean = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        etInput = findViewById(R.id.etInput)
        tvOperation = findViewById(R.id.tvOperation)
        tvResult = findViewById(R.id.tvResult)

        // Изначально показываем 0 в активном поле ввода
        etInput.setText("0")
        etInput.setSelection(etInput.text.length)

        setupClickListeners()
    }

    private fun setupClickListeners() {
        // Цифровые кнопки
        findViewById<Button>(R.id.btnZero).setOnClickListener { appendNumber("0") }
        findViewById<Button>(R.id.btnOne).setOnClickListener { appendNumber("1") }
        findViewById<Button>(R.id.btnTwo).setOnClickListener { appendNumber("2") }
        findViewById<Button>(R.id.btnThree).setOnClickListener { appendNumber("3") }
        findViewById<Button>(R.id.btnFour).setOnClickListener { appendNumber("4") }
        findViewById<Button>(R.id.btnFive).setOnClickListener { appendNumber("5") }
        findViewById<Button>(R.id.btnSix).setOnClickListener { appendNumber("6") }
        findViewById<Button>(R.id.btnSeven).setOnClickListener { appendNumber("7") }
        findViewById<Button>(R.id.btnEight).setOnClickListener { appendNumber("8") }
        findViewById<Button>(R.id.btnNine).setOnClickListener { appendNumber("9") }

        // Кнопки операций
        findViewById<Button>(R.id.btnAdd).setOnClickListener { setOperation("+") }
        findViewById<Button>(R.id.btnSubtract).setOnClickListener { setOperation("-") }
        findViewById<Button>(R.id.btnMultiply).setOnClickListener { setOperation("*") }
        findViewById<Button>(R.id.btnDivide).setOnClickListener { setOperation("/") }

        // Специальные кнопки
        findViewById<Button>(R.id.btnDecimal).setOnClickListener { appendDecimalPoint() }
        findViewById<Button>(R.id.btnClear).setOnClickListener { clearAll() }
        findViewById<Button>(R.id.btnBackspace).setOnClickListener { backspace() }
        findViewById<Button>(R.id.btnEquals).setOnClickListener { calculateResult() }
    }

    // Вспомогательная функция для форматирования числа (убирает .0)
    private fun formatValue(value: Double): String {
        return if (value % 1 == 0.0) value.toLong().toString() else value.toString()
    }

    private fun appendNumber(number: String) {
        currentInput = etInput.text.toString()

        if (isNewOperation) {
            // Если это начало новой операции, заменяем "0" или предыдущий результат
            currentInput = if (number == ".") "0." else number
            isNewOperation = false
        } else if (currentInput == "0" && number != ".") {
            // Заменяем одиночный "0" на первое число (если это не точка)
            currentInput = number
        } else {
            currentInput += number
        }

        etInput.setText(currentInput)
        etInput.setSelection(etInput.text.length)
    }

    private fun appendDecimalPoint() {
        currentInput = etInput.text.toString()

        if (isNewOperation) {
            currentInput = "0"
            isNewOperation = false
        }

        if (!currentInput.contains(".")) {
            currentInput += "."
            etInput.setText(currentInput)
            etInput.setSelection(etInput.text.length)
        }
    }

    private fun setOperation(operation: String) {
        // Используем текущее число из etInput, если оно есть, для начала операции
        val valueToUse = etInput.text.toString().toDoubleOrNull()
            ?: if (tvResult.text.isNotEmpty()) tvResult.text.toString().toDoubleOrNull() else 0.0

        if (valueToUse != null) {
            if (currentOperator != null && !isNewOperation) {
                // Если операция уже идет, сначала вычисляем промежуточный результат
                calculateResult()
            }

            previousValue = valueToUse
            currentOperator = operation

            // Обновляем поля отображения
            tvOperation.text = formatValue(previousValue) + " " + operation
            tvResult.text = ""
            etInput.setText("") // Очищаем активное поле ввода для второго числа
            currentInput = ""
            isNewOperation = true
        }
    }

    private fun calculateResult() {
        if (currentOperator == null) return // Нечего вычислять

        currentInput = etInput.text.toString().removeSuffix(".")

        // Второе число: если etInput пусто, используем previousValue (повтор операции)
        val currentValue = if (currentInput.isEmpty() || currentInput == "0") {
            previousValue
        } else {
            currentInput.toDoubleOrNull() ?: return
        }

        var result: Double = 0.0

        try {
            result = when (currentOperator) {
                "+" -> previousValue + currentValue
                "-" -> previousValue - currentValue
                "*" -> previousValue * currentValue
                "/" -> {
                    if (currentValue == 0.0) throw ArithmeticException()
                    previousValue / currentValue
                }
                else -> return
            }

            val resultText = formatValue(result)

            // Обновляем поля:
            tvOperation.text = formatValue(previousValue) + " " + currentOperator + " " + formatValue(currentValue) + " ="
            tvResult.text = resultText
            etInput.setText(resultText)
            etInput.setSelection(etInput.text.length)

            currentOperator = null
            previousValue = result
            isNewOperation = true
            currentInput = resultText

        } catch (e: ArithmeticException) {
            tvOperation.text = ""
            tvResult.text = "Ошибка"
            etInput.setText("0")
            previousValue = 0.0
            currentOperator = null
            isNewOperation = true
        }
    }

    private fun clearAll() {
        etInput.setText("0")
        tvOperation.text = ""
        tvResult.text = ""
        currentInput = "0"
        previousValue = 0.0
        currentOperator = null
        isNewOperation = true
    }

    private fun backspace() {
        currentInput = etInput.text.toString()
        if (currentInput.length > 1 && currentInput != "0") {
            currentInput = currentInput.dropLast(1)
            etInput.setText(currentInput)
            etInput.setSelection(etInput.text.length)
        } else if (currentInput.length == 1 && currentInput != "0") {
            etInput.setText("0")
            currentInput = "0"
            isNewOperation = true
        }
    }
}
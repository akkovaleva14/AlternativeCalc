package com.example.primenumbers

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.logger.Logger
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone
import kotlin.math.sqrt

class PrimeNumbersViewModel : ViewModel() {

    val inputNumber = MutableLiveData<String>()
    val groupingOrderPosition = MutableLiveData<Int>().apply { value = 0 }
    val result = MutableLiveData<String>()

    init {
        Logger.i(message = "PrimeNumbersViewModel initialized")
    }

    private fun formatTimestamp(timestamp: Long): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.getDefault())
        dateFormat.timeZone =
            TimeZone.getTimeZone("Europe/Moscow")
        return dateFormat.format(timestamp)
    }


    fun checkPrimeNumbers() {
        val startTime = System.currentTimeMillis()
        Logger.i(message = "checkPrimeNumbers called at ${formatTimestamp(startTime)}")

        viewModelScope.launch(Dispatchers.IO) {
            val startTimeCoroutine = System.currentTimeMillis()
            Logger.i(message = "Coroutine started at ${formatTimestamp(startTimeCoroutine)}")

            try {
                val numberString = inputNumber.value?.trim()
                    ?: throw IllegalArgumentException("Invalid input number: inputNumber.value is null")
                Logger.d(message = "Input number string: $numberString")

                val number = numberString.toLongOrNull()
                    ?: throw IllegalArgumentException("Invalid input number: Unable to parse '$numberString'")
                Logger.d(message = "Parsed number: $number")

                val reversed = groupingOrderPosition.value == 1
                Logger.d(message = "Grouping order (reversed): $reversed")

                val output = withContext(Dispatchers.Default) {
                    val result = primeNumbersLogic(number, reversed)
                    Logger.d(message = "Prime numbers logic output: $result")
                    result
                }

                result.postValue(output)
            } catch (e: Exception) {
                Logger.e(message = "Error in checkPrimeNumbers: ${e.message}")
                result.postValue("An error occurred: ${e.message}")
            } finally {
                val endTimeCoroutine = System.currentTimeMillis()
                Logger.i(message = "Coroutine completed at ${formatTimestamp(endTimeCoroutine)}, duration: ${endTimeCoroutine - startTimeCoroutine} ms")

                val endTime = System.currentTimeMillis()
                Logger.i(message = "checkPrimeNumbers completed at ${formatTimestamp(endTime)}, total duration: ${endTime - startTime} ms")
            }
        }
    }

    private fun splitIntoDigits(number: Long, reversed: Boolean): LongArray {
        val numString = if (reversed) number.toString().reversed() else number.toString()
        val digitsArray = numString.map { it.toString().toLong() }.toLongArray()
        Logger.d(message = "Split number into digits (reversed=$reversed): ${digitsArray.joinToString()}")
        return digitsArray
    }

    private fun formatPrimeCheck(number: Long): String {
        val result = if (primeFinder(number)) "$number - prime" else number.toString()
        Logger.d(message = "Formatted prime check result for $number: $result")
        return result
    }

    private fun primeNumbersLogic(input: Long, reversed: Boolean): String {
        if (input <= 0) return "Wrong input! Only positive integers are allowed."

        val digits = splitIntoDigits(input, reversed)
        val results = StringBuilder()
        var currentNumber = 0L

        digits.forEach { digit ->
            currentNumber = currentNumber * 10 + digit
            results.appendLine(formatPrimeCheck(currentNumber))
        }
        val finalResult = results.toString()
        Logger.i(message = "Final result of prime numbers logic: $finalResult")
        return finalResult
    }

    private fun primeFinder(num: Long): Boolean {
        if (num <= 1) return false
        for (i in 2..sqrt(num.toDouble()).toInt()) {
            if (num % i == 0L) {
                Logger.d(message = "Number $num is not prime (divisible by $i)")
                return false
            }
        }
        Logger.d(message = "Number $num is prime")
        return true
    }
}

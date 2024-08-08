package com.example.circles

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.logger.Logger
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.math.abs
import kotlin.math.pow
import kotlin.math.sqrt

class CirclesViewModel : ViewModel() {

    val x1 = MutableLiveData<String>()
    val y1 = MutableLiveData<String>()
    val r1 = MutableLiveData<String>()
    val x2 = MutableLiveData<String>()
    val y2 = MutableLiveData<String>()
    val r2 = MutableLiveData<String>()

    private val _result = MutableLiveData<String>()
    val result: LiveData<String> get() = _result

    init {
        Logger.i(message = "CirclesViewModel initialized")
    }

    private fun parseDouble(input: String?, fieldName: String): Double? {
        return input?.toDoubleOrNull().also {
            if (it == null) Logger.e(message = "Invalid $fieldName value: $input")
        }
    }

    private fun parseRadius(input: String?, fieldName: String): Double? {
        return input?.toDoubleOrNull()?.takeIf { it > 0 }.also {
            if (it == null) Logger.e(message = "Invalid $fieldName value: $input")
        }
    }

    fun checkIntersection() {
        Logger.i(message = "Started checkIntersection")

        viewModelScope.launch {
            val startTime = System.currentTimeMillis()

            val x1 = parseDouble(this@CirclesViewModel.x1.value, "x1")
            val y1 = parseDouble(this@CirclesViewModel.y1.value, "y1")
            val r1 = parseRadius(this@CirclesViewModel.r1.value, "r1")
            val x2 = parseDouble(this@CirclesViewModel.x2.value, "x2")
            val y2 = parseDouble(this@CirclesViewModel.y2.value, "y2")
            val r2 = parseRadius(this@CirclesViewModel.r2.value, "r2")

            if (x1 == null || y1 == null || r1 == null || x2 == null || y2 == null || r2 == null) {
                Logger.e(message = "One or more input values are invalid")
                _result.value = "Invalid input detected. Please, try again."
                return@launch
            }

            Logger.d(message = "Parsed values - x1: $x1, y1: $y1, r1: $r1, x2: $x2, y2: $y2, r2: $r2")

            try {
                val distance = withContext(Dispatchers.Default) {
                    val calculatedDistance = sqrt((x2 - x1).pow(2) + (y2 - y1).pow(2))
                    Logger.d(message = "Calculated distance: $calculatedDistance")
                    calculatedDistance
                }

                val resultText = determineIntersection(distance, r1, r2)
                _result.value = resultText
                Logger.i(message = "Intersection result: $resultText")
            } catch (e: Exception) {
                Logger.e(message = "Error in distance calculation or intersection determination: ${e.message}")
                _result.value = "An error occurred while calculating intersection."
            } finally {
                val endTime = System.currentTimeMillis()
                Logger.i(message = "checkIntersection completed in ${endTime - startTime} ms")
            }
        }
    }

    private fun determineIntersection(distance: Double, r1: Double, r2: Double): String {
        return when {
            abs(distance - (r1 + r2)) < 1e-9 -> "The circles coincide"
            distance <= r1 + r2 -> "The circles intersect"
            abs(r1 - r2) >= distance -> "One circle is inside the other"
            else -> "The circles do not intersect"
        }.also { Logger.i(message = it) }
    }
}

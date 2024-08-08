package com.example.thermometer

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.logger.Logger
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.math.roundToInt
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

class ThermometerViewModel : ViewModel() {

    val inputTemperature = MutableLiveData<String>()
    val season = MutableLiveData<String>()
    val temperatureUnit = MutableLiveData<String>().apply { value = "C" }
    private val _result = MutableLiveData<String>()
    val result: LiveData<String> get() = _result

    fun checkTemperature() {
        val startTime = System.currentTimeMillis()
        Logger.i(message = "Starting temperature check at ${formatTimestamp(startTime)}")

        viewModelScope.launch(Dispatchers.IO) {
            Logger.d(message = "Coroutine started at ${formatTimestamp(startTime)}")

            try {
                val tempStr = inputTemperature.value?.trim()
                val seasonValue = season.value?.trim()?.lowercase()
                val unit = temperatureUnit.value?.uppercase() ?: "C"

                Logger.d(message = "Raw input values - Temperature: '$tempStr', Season: '$seasonValue', Unit: '$unit'")

                val temperature = tempStr?.toDoubleOrNull()
                if (temperature == null || seasonValue == null || (seasonValue != "w" && seasonValue != "s")) {
                    Logger.e(message = "Invalid input or season value. Temperature: '$tempStr', Season: '$seasonValue'")
                    _result.postValue("Incorrect input. Please try again.")
                    return@launch
                }

                Logger.d(message = "Parsed temperature: $temperature")

                val (comfortRangeStart, comfortRangeEnd) = getComfortRange(seasonValue)
                Logger.d(message = "Comfort range (Celsius): $comfortRangeStart to $comfortRangeEnd")

                val (comfortRangeStartUnit, comfortRangeEndUnit) = convertComfortRangeToUnit(
                    comfortRangeStart,
                    comfortRangeEnd,
                    unit
                )
                Logger.d(message = "Comfort range in unit $unit: $comfortRangeStartUnit to $comfortRangeEndUnit")

                val tempInUnit = convertToUnit(temperature, unit)
                Logger.d(message = "Temperature in unit $unit: $tempInUnit")

                val advice = when {
                    tempInUnit < comfortRangeStartUnit -> "Please, make it warmer by ${(comfortRangeStartUnit - tempInUnit).roundToInt()} degrees."
                    tempInUnit > comfortRangeEndUnit -> "Please, make it cooler by ${(tempInUnit - comfortRangeEndUnit).roundToInt()} degrees."
                    else -> "The temperature is comfortable."
                }

                Logger.i(message = "Final result: Temperature is $tempInUnit $unit. Comfort range: $comfortRangeStartUnit to $comfortRangeEndUnit $unit. Advice: $advice")

                _result.postValue(
                    "The temperature is $tempInUnit $unit.\nThe comfortable temperature is from $comfortRangeStartUnit to $comfortRangeEndUnit $unit.\n$advice"
                )
            } catch (e: Exception) {
                Logger.e(message = "Error during temperature check: ${e.message}")
                _result.postValue("An error occurred: ${e.message}")
            } finally {
                val endTime = System.currentTimeMillis()
                Logger.i(message = "Temperature check completed at ${formatTimestamp(endTime)}, duration: ${endTime - startTime} ms")
            }
        }
    }

    private fun getComfortRange(season: String): Pair<Double, Double> {
        val range = if (season == "w") {
            20.0 to 22.0
        } else {
            22.0 to 25.0
        }
        Logger.d(message = "Comfort range for season '$season': $range")
        return range
    }

    private fun convertComfortRangeToUnit(
        start: Double,
        end: Double,
        unit: String
    ): Pair<Double, Double> {
        Logger.d(message = "Converting comfort range from Celsius to $unit")
        val convertedRange = when (unit) {
            "K" -> celsiusToKelvin(start) to celsiusToKelvin(end)
            "F" -> celsiusToFahrenheit(start) to celsiusToFahrenheit(end)
            else -> start to end
        }
        Logger.d(message = "Converted comfort range in $unit: $convertedRange")
        return convertedRange
    }

    private fun convertToUnit(temperature: Double, unit: String): Double {
        Logger.d(message = "Converting temperature $temperature from Celsius to $unit")
        val convertedTemperature = when (unit) {
            "K" -> celsiusToKelvin(temperature)
            "F" -> celsiusToFahrenheit(temperature)
            else -> temperature
        }
        Logger.d(message = "Converted temperature: $convertedTemperature")
        return convertedTemperature
    }

    private fun celsiusToKelvin(celsius: Double): Double {
        val kelvin = celsius + 273.15
        Logger.d(message = "Converted Celsius $celsius to Kelvin $kelvin")
        return kelvin
    }

    private fun celsiusToFahrenheit(celsius: Double): Double {
        val fahrenheit = (celsius * 9 / 5) + 32
        Logger.d(message = "Converted Celsius $celsius to Fahrenheit $fahrenheit")
        return fahrenheit
    }

    private fun formatTimestamp(timestamp: Long): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.getDefault())
        dateFormat.timeZone = TimeZone.getTimeZone("Europe/Moscow")
        return dateFormat.format(timestamp)
    }
}

package com.example.calculator

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.logger.Logger
import kotlinx.coroutines.*
import java.math.BigInteger
import java.util.Random
import kotlin.math.*

enum class CalculatorJob { FACTORIAL, ROOTS, LOGARITHMS, EXP, PRIME, ALL }

class CalculatorViewModel : ViewModel() {

    val inputNumber = MutableLiveData<String>()
    val factorialResult = MutableLiveData<String>()
    val squareRootResult = MutableLiveData<String>()
    val cubeRootResult = MutableLiveData<String>()
    val log10Result = MutableLiveData<String>()
    val naturalLogResult = MutableLiveData<String>()
    val squareResult = MutableLiveData<String>()
    val cubeResult = MutableLiveData<String>()
    val primeTestResult = MutableLiveData<String>()
    val errorMessage = MutableLiveData<String>()

    private val isAllJobsRunning = MutableLiveData(false)

    private val jobStates = MutableLiveData<Map<CalculatorJob, Boolean>>().apply {
        value = mapOf(
            CalculatorJob.FACTORIAL to false,
            CalculatorJob.ROOTS to false,
            CalculatorJob.LOGARITHMS to false,
            CalculatorJob.EXP to false,
            CalculatorJob.PRIME to false,
            CalculatorJob.ALL to false
        )
    }

    private val jobMap = mutableMapOf<CalculatorJob, Job?>()
    private var allJobs: Job? = null

    fun getJobStates(): LiveData<Map<CalculatorJob, Boolean>> = jobStates

    fun runAllCalculations() {
        if (isAllJobsRunning.value == true) {
            cancelAllJobs()
            return
        }

        viewModelScope.launch {
            Logger.i(message = "Starting all calculations")
            isAllJobsRunning.postValue(true)
            updateAllJobsState(true)

            allJobs = launch {
                val startTime = System.currentTimeMillis()
                val jobs = listOf(
                    async { calculateFactorial() },
                    async { calculateSquareAndCubeRoot() },
                    async { calculateLogarithms() },
                    async { calculateSquaringAndCubing() },
                    async { testPrimalityWithTimeout() }
                )

                try {
                    jobs.awaitAll()
                } catch (e: CancellationException) {
                    Logger.i(message = "Calculations were cancelled: ${e.message}")
                } finally {
                    val endTime = System.currentTimeMillis()
                    Logger.i(message = "Finished all calculations in ${endTime - startTime} ms")
                    isAllJobsRunning.postValue(false)
                }
            }
        }
    }


    private fun updateAllJobsState(isActive: Boolean) {
        val newStates = jobStates.value?.mapValues { isActive } ?: mapOf()
        jobStates.postValue(newStates)
        isAllJobsRunning.postValue(isActive)
        Logger.d(message = "Updated all job states to: $newStates")
    }

    private fun runJob(jobType: CalculatorJob, jobAction: suspend () -> Unit) {
        setJobState(jobType, true)
        val job = viewModelScope.launch {
            try {
                jobAction()
            } catch (e: CancellationException) {
                Logger.d(message = "Job $jobType was cancelled.")
            } catch (e: Exception) {
                Logger.e(message = "Error in job $jobType: ${e.message}")
            } finally {
                setJobState(jobType, false)
            }
        }
        jobMap[jobType] = job
    }

    fun calculateFactorial() {
        if (jobStates.value?.get(CalculatorJob.FACTORIAL) == true) {
            cancelJob(CalculatorJob.FACTORIAL)
            return
        }

        runJob(CalculatorJob.FACTORIAL) {
            withContext(Dispatchers.Default) {
                Logger.i(message = "Calculating factorial")
                val number = inputNumber.value?.toBigIntegerOrNull()
                if (number == null || number < BigInteger.ZERO || number > BigInteger.valueOf(5000)) {
                    factorialResult.postValue("Invalid input or too large number")
                    return@withContext
                }
                val result = factorial(number)
                Logger.d(message = "Factorial result: $result")
                factorialResult.postValue(result.toDouble().toString())
            }
        }
    }

    fun calculateSquareAndCubeRoot() {
        if (jobStates.value?.get(CalculatorJob.ROOTS) == true) {
            cancelJob(CalculatorJob.ROOTS)
            return
        }

        runJob(CalculatorJob.ROOTS) {
            withContext(Dispatchers.Default) {
                Logger.i(message = "Calculating square and cube roots")
                val number = inputNumber.value?.toDoubleOrNull() ?: return@withContext
                val squareRoot = if (number >= 0) sqrt(number) else Double.NaN
                val cubeRoot = cbrt(number)

                Logger.d(message = "Square root result: $squareRoot")
                Logger.d(message = "Cube root result: $cubeRoot")
                squareRootResult.postValue("$squareRoot")
                cubeRootResult.postValue("$cubeRoot")
            }
        }
    }

    fun calculateLogarithms() {
        if (jobStates.value?.get(CalculatorJob.LOGARITHMS) == true) {
            cancelJob(CalculatorJob.LOGARITHMS)
            return
        }

        runJob(CalculatorJob.LOGARITHMS) {
            withContext(Dispatchers.Default) {
                Logger.i(message = "Calculating logarithms")
                val number = inputNumber.value?.toDoubleOrNull() ?: return@withContext
                val log10 = if (number > 0) log10(number) else Double.NaN
                val naturalLog = if (number > 0) ln(number) else Double.NaN

                Logger.d(message = "Log10 result: $log10")
                Logger.d(message = "Natural log result: $naturalLog")
                log10Result.postValue("$log10")
                naturalLogResult.postValue("$naturalLog")
            }
        }
    }

    fun calculateSquaringAndCubing() {
        if (jobStates.value?.get(CalculatorJob.EXP) == true) {
            cancelJob(CalculatorJob.EXP)
            return
        }

        runJob(CalculatorJob.EXP) {
            withContext(Dispatchers.Default) {
                Logger.i(message = "Calculating square and cube")
                val number = inputNumber.value?.toDoubleOrNull() ?: return@withContext
                val square = number * number
                val cube = square * number

                Logger.d(message = "Square result: $square")
                Logger.d(message = "Cube result: $cube")
                squareResult.postValue("$square")
                cubeResult.postValue("$cube")
            }
        }
    }

    fun testPrimalityWithTimeout() {
        if (jobStates.value?.get(CalculatorJob.PRIME) == true) {
            cancelJob(CalculatorJob.PRIME)
            return
        }

        runJob(CalculatorJob.PRIME) {
            try {
                withTimeout(1000) {
                    Logger.d(message = "Timeout")
                    testPrimality()
                }
            } catch (e: TimeoutCancellationException) {
                Logger.d(message = "Primality test timed out")
                errorMessage.postValue("An error has occurred. Please try again.")
            }
        }
    }

    private fun testPrimality() {
        Logger.i(message = "Testing primality")
        val number = inputNumber.value?.toBigIntegerOrNull()
        val two = BigInteger.valueOf(2)
        if (number == null || number < two) {
            primeTestResult.postValue("Invalid input or not a prime")
            return
        }
        val isPrime = isProbablePrime(number, 20)
        Logger.d(message = "Prime test result: $isPrime")
        primeTestResult.postValue(if (isPrime) "Prime" else "Not prime")
    }

    private fun setJobState(jobType: CalculatorJob, isActive: Boolean) {
        Logger.d(message = "Setting job state: $jobType to $isActive")
        val newStates = jobStates.value?.toMutableMap()?.apply { this[jobType] = isActive }
        jobStates.postValue(newStates)
        if (jobType == CalculatorJob.ALL) {
            isAllJobsRunning.postValue(isActive)
        }
    }

    private fun cancelJob(jobType: CalculatorJob) {
        jobMap[jobType]?.cancel()
        setJobState(jobType, false)
        Logger.i(message = "Cancelled job: $jobType")
    }

    private fun cancelAllJobs() {
        Logger.i(message = "Cancelling all jobs")
        Logger.d(message = "Current _isAllJobsRunning: ${isAllJobsRunning.value}")
        Logger.d(message = "Current jobStates: ${jobStates.value}")

        allJobs?.cancel()
        jobMap.values.forEach { it?.cancel() }
        allJobs = null
        jobMap.clear()
        isAllJobsRunning.postValue(false)
        updateAllJobsState(false)
        Logger.d(message = "All jobs cancelled and state reset")
    }

    private fun factorial(n: BigInteger): BigInteger {
        var result = BigInteger.ONE
        var i = BigInteger.ONE
        while (i <= n) {
            result *= i
            i += BigInteger.ONE
        }
        return result
    }

    private fun isProbablePrime(n: BigInteger, iterations: Int): Boolean {
        if (n <= BigInteger.ONE) return false
        if (n == BigInteger.valueOf(2) || n == BigInteger.valueOf(3)) return true
        if (n % BigInteger.valueOf(2) == BigInteger.ZERO) return false
        var d = n - BigInteger.ONE
        var s = 0
        while (d % BigInteger.valueOf(2) == BigInteger.ZERO) {
            d /= BigInteger.valueOf(2)
            s += 1
        }
        val random = Random()
        for (i in 0 until iterations) {
            val a = BigInteger(n.bitLength(), random).mod(n.subtract(BigInteger.ONE))
                .add(BigInteger.ONE)
            var x = a.modPow(d, n)
            if (x == BigInteger.ONE || x == n.subtract(BigInteger.ONE)) continue
            var j = 0
            while (j < s - 1) {
                x = x.modPow(BigInteger.valueOf(2), n)
                if (x == BigInteger.ONE) return false
                if (x == n.subtract(BigInteger.ONE)) break
                j += 1
            }
            if (x != n.subtract(BigInteger.ONE)) return false
        }
        return true
    }
}

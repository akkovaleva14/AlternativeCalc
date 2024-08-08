package com.example.calculator

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.example.calculator.databinding.FragmentCalculatorBinding
import com.example.logger.FragmentLogger
import com.example.logger.Logger
import com.example.logger.addLogging

class CalculatorFragment : FragmentLogger() {

    private lateinit var binding: FragmentCalculatorBinding
    private val viewModel: CalculatorViewModel by lazy {
        ViewModelProvider(this)[CalculatorViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Logger.i(message = "CalculatorFragment onCreateView")
        binding = FragmentCalculatorBinding.inflate(inflater, container, false).apply {
            viewModel = this@CalculatorFragment.viewModel
            lifecycleOwner = viewLifecycleOwner
        }
        Logger.d(message = "ViewModel and lifecycleOwner set to binding")
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Logger.i(message = "CalculatorFragment onViewCreated")
        setupLogging()
        setupObservers()
        setupClickListeners()
    }

    private fun setupLogging() {
        Logger.i(message = "Setting up logging and binding for EditText field")
        binding.editTextNumber.addLogging("inputNumber", viewModel.inputNumber)
    }

    private fun setupObservers() {
        Logger.i(message = "Setting up observers")
        viewModel.getJobStates().observe(viewLifecycleOwner) { states ->
            Logger.d(message = "Job states updated: $states")
            updateButtonStates(states)
        }

        viewModel.apply {
            factorialResult.observe(viewLifecycleOwner) { result ->
                updateTextView(binding.textViewFactorial)(result)
            }
            squareRootResult.observe(viewLifecycleOwner) { result ->
                updateTextView(binding.textViewSquareRoot)(result)
            }
            cubeRootResult.observe(viewLifecycleOwner) { result ->
                updateTextView(binding.textViewCubeRoot)(result)
            }
            log10Result.observe(viewLifecycleOwner) { result ->
                updateTextView(binding.textViewLog10)(result)
            }
            naturalLogResult.observe(viewLifecycleOwner) { result ->
                updateTextView(binding.textViewNaturalLog)(result)
            }
            squareResult.observe(viewLifecycleOwner) { result ->
                updateTextView(binding.textViewSquare)(result)
            }
            cubeResult.observe(viewLifecycleOwner) { result ->
                updateTextView(binding.textViewCube)(result)
            }
            primeTestResult.observe(viewLifecycleOwner) { result ->
                updateTextView(binding.textViewPrimeTest)(result)
            }
            errorMessage.observe(viewLifecycleOwner) { message ->
                message?.let {
                    showErrorDialog(it)
                    viewModel.errorMessage.value = null
                }
            }
        }
    }

    private fun updateButtonStates(states: Map<CalculatorJob, Boolean>) {
        binding.buttonFactorial.text =
            if (states[CalculatorJob.FACTORIAL] == true) "Cancel" else "Run"
        binding.buttonSquareCubeRoot.text =
            if (states[CalculatorJob.ROOTS] == true) "Cancel" else "Run"
        binding.buttonLogarithms.text =
            if (states[CalculatorJob.LOGARITHMS] == true) "Cancel" else "Run"
        binding.buttonSquaringCubing.text =
            if (states[CalculatorJob.EXP] == true) "Cancel" else "Run"
        binding.buttonPrimeTest.text =
            if (states[CalculatorJob.PRIME] == true) "Cancel" else "Run"
        binding.buttonRunAll.text =
            if (states[CalculatorJob.ALL] == true) "Cancel" else "Run"
    }

    private fun updateTextView(textView: TextView): (String) -> Unit = { result ->
        Logger.d(message = "${textView.resources.getResourceEntryName(textView.id)} result updated: $result")
        textView.text = result
    }

    private fun showErrorDialog(message: String) {
        Logger.e(message = "Showing error dialog: $message")
        AlertDialog.Builder(requireContext())
            .setTitle("Error")
            .setMessage(message)
            .setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }
            .show()
    }

    private fun setupClickListeners() {
        Logger.i(message = "Setting up click listeners")

        binding.buttonFactorial.setOnClickListener {
            it.hideKeyboard()
            handleButtonClick(CalculatorJob.FACTORIAL, viewModel::calculateFactorial)
        }
        binding.buttonSquareCubeRoot.setOnClickListener {
            it.hideKeyboard()
            handleButtonClick(CalculatorJob.ROOTS, viewModel::calculateSquareAndCubeRoot)
        }
        binding.buttonLogarithms.setOnClickListener {
            it.hideKeyboard()
            handleButtonClick(CalculatorJob.LOGARITHMS, viewModel::calculateLogarithms)
        }
        binding.buttonSquaringCubing.setOnClickListener {
            it.hideKeyboard()
            handleButtonClick(CalculatorJob.EXP, viewModel::calculateSquaringAndCubing)
        }
        binding.buttonPrimeTest.setOnClickListener {
            it.hideKeyboard()
            handleButtonClick(CalculatorJob.PRIME, viewModel::testPrimalityWithTimeout)
        }
        binding.buttonClear.setOnClickListener {
            Logger.i(message = "Clear button clicked")
            viewModel.inputNumber.value = ""
            binding.editTextNumber.text.clear()
            clearResults()
        }
        binding.buttonRunAll.setOnClickListener {
            it.hideKeyboard()
            handleButtonClick(CalculatorJob.ALL, viewModel::runAllCalculations)
        }

    }

    private fun handleButtonClick(job: CalculatorJob, action: () -> Unit) {
        Logger.i(message = "${job.name} button clicked")
        handleInputValidation {
            action()
        }
    }

    private fun handleInputValidation(action: () -> Unit) {
        val input = binding.editTextNumber.text.toString()
        Logger.d(message = "Input validation for: $input")
        if (input.isBlank() || input == "." || input == "-" || input == "-.") {
            Toast.makeText(context, "Please enter a valid number", Toast.LENGTH_SHORT).show()
            Logger.w(message = "Invalid input: $input")
        } else {
            viewModel.inputNumber.value = input
            action()
        }
    }

    private fun clearResults() {
        Logger.i(message = "Clearing results")
        binding.textViewFactorial.text = ""
        binding.textViewSquareRoot.text = ""
        binding.textViewCubeRoot.text = ""
        binding.textViewLog10.text = ""
        binding.textViewNaturalLog.text = ""
        binding.textViewSquare.text = ""
        binding.textViewCube.text = ""
        binding.textViewPrimeTest.text = ""
    }

}

fun View.hideKeyboard() {
    val inputMethodManager =
        context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    inputMethodManager.hideSoftInputFromWindow(windowToken, 0)
}
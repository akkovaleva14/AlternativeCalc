package com.example.primenumbers

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.lifecycle.ViewModelProvider
import com.example.primenumbers.databinding.FragmentPrimeNumbersBinding
import com.example.logger.Logger
import com.example.logger.addLogging
import com.example.logger.FragmentLogger

class PrimeNumbersFragment : FragmentLogger() {

    private lateinit var binding: FragmentPrimeNumbersBinding
    private val viewModel: PrimeNumbersViewModel by lazy {
        ViewModelProvider(this)[PrimeNumbersViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Logger.d(message = "PrimeNumbersFragment onCreateView")
        binding = FragmentPrimeNumbersBinding.inflate(inflater, container, false).apply {
            lifecycleOwner = viewLifecycleOwner
            viewModel = this@PrimeNumbersFragment.viewModel
        }
        Logger.d(message = "ViewModel and lifecycleOwner set to binding")
        setupSpinner()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Logger.i(message = "PrimeNumbersFragment onViewCreated")
        setupLogging()
        binding.buttonCheckPrime.setOnClickListener {
            viewModel.checkPrimeNumbers()
        }
        viewModel.result.observe(viewLifecycleOwner) { result ->
            binding.textViewResult.text = result
        }
    }

    private fun setupLogging() {
        Logger.i(message = "Setting up logging and binding for EditText fields")
        binding.editTextNumber.addLogging("primeInput", viewModel.inputNumber)
    }

    private fun setupSpinner() {
        Logger.d(message = "PrimeNumbersFragment setupSpinner")
        val spinner: Spinner = binding.spinnerOrder
        val items = listOf("Higher", "Lower")
        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            items
        ).apply {
            setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }
        spinner.adapter = adapter

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View?,
                position: Int,
                id: Long
            ) {
                Logger.i(message = "Spinner item selected: ${items[position]}")
                viewModel.groupingOrderPosition.value = position
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                Logger.i(message = "Spinner item deselected")
            }
        }
        Logger.d(message = "PrimeNumbersFragment spinner setup complete")
    }
}

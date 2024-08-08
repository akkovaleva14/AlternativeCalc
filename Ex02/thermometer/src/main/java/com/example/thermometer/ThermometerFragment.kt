package com.example.thermometer

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.lifecycle.ViewModelProvider
import com.example.thermometer.databinding.FragmentThermometerBinding
import com.example.logger.Logger
import com.example.logger.addLogging
import com.example.logger.FragmentLogger

class ThermometerFragment : FragmentLogger() {

    private lateinit var binding: FragmentThermometerBinding
    private val viewModel: ThermometerViewModel by lazy {
        ViewModelProvider(this)[ThermometerViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Logger.i(message = "ThermometerFragment created")
        binding = FragmentThermometerBinding.inflate(inflater, container, false).apply {
            lifecycleOwner = viewLifecycleOwner
            viewModel = this@ThermometerFragment.viewModel
        }
        Logger.d(message = "ViewModel and lifecycleOwner set to binding")
        setupSpinners()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Logger.i(message = "ThermometerFragment onViewCreated")
        setupLogging()
        viewModel.result.observe(viewLifecycleOwner) { result ->
            binding.textViewResult.text = result
        }
    }

    private fun setupLogging() {
        Logger.i(message = "Setting up logging and binding for EditText fields")
        binding.editTextTemperature.addLogging("temperatureInput", viewModel.inputTemperature)
    }

    private fun setupSpinners() {
        Logger.i(message = "Setting up spinners in ThermometerFragment")

        val unitAdapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            listOf("Celsius", "Kelvin", "Fahrenheit")
        ).apply {
            setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }
        binding.spinnerUnit.adapter = unitAdapter
        binding.spinnerUnit.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View?,
                position: Int,
                id: Long
            ) {
                val unit = when (position) {
                    0 -> "C"
                    1 -> "K"
                    2 -> "F"
                    else -> "C"
                }
                Logger.i(message = "Temperature unit selected: $unit")
                viewModel.temperatureUnit.value = unit
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                Logger.i(message = "No temperature unit selected")
            }
        }

        val seasonAdapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            listOf("Summer", "Winter")
        ).apply {
            setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }
        binding.spinnerSeason.adapter = seasonAdapter
        binding.spinnerSeason.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View?,
                position: Int,
                id: Long
            ) {
                val season = when (position) {
                    0 -> "s"
                    1 -> "w"
                    else -> "s"
                }
                Logger.i(message = "Season selected: $season")
                viewModel.season.value = season
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                Logger.i(message = "No season selected")
            }
        }
    }
}
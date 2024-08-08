package com.example.ex02

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.ex02.databinding.FragmentMenuBinding
import com.example.logger.Logger
import com.example.logger.FragmentLogger

class MenuFragment : FragmentLogger() {

    private var _binding: FragmentMenuBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Logger.i(message = "MenuFragment onCreateView")
        _binding = FragmentMenuBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Logger.i(message = "MenuFragment onViewCreated")

        binding.btnCircles.setOnClickListener {
            Logger.i(message = "Circles button tapped")
            findNavController().navigate(R.id.action_menuFragment_to_circlesFragment)
        }
        binding.btnPrime.setOnClickListener {
            Logger.i(message = "Prime Numbers button tapped")
            findNavController().navigate(R.id.action_menuFragment_to_primeNumbersFragment)
        }
        binding.btnThermometer.setOnClickListener {
            Logger.i(message = "Thermometer button tapped")
            findNavController().navigate(R.id.action_menuFragment_to_thermometerFragment)
        }
        binding.btnSpeechModule.setOnClickListener {
            Logger.i(message = "Speech Module button tapped")
            findNavController().navigate(R.id.action_menuFragment_to_speechModuleFragment)
        }
        binding.btnCalculator.setOnClickListener {
            Logger.i(message = "Calculator button tapped")
            findNavController().navigate(R.id.action_menuFragment_to_calculatorFragment)
        }
    }
}
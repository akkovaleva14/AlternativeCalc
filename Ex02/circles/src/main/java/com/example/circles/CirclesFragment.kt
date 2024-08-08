package com.example.circles

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.example.circles.databinding.FragmentCirclesBinding
import com.example.logger.FragmentLogger
import com.example.logger.Logger
import com.example.logger.addLogging

class CirclesFragment : FragmentLogger() {

    private lateinit var binding: FragmentCirclesBinding
    private val viewModel: CirclesViewModel by lazy {
        ViewModelProvider(this)[CirclesViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Logger.i(message = "CirclesFragment onCreateView")
        binding = FragmentCirclesBinding.inflate(inflater, container, false).apply {
            lifecycleOwner = viewLifecycleOwner
            viewModel = this@CirclesFragment.viewModel
        }
        Logger.d(message = "ViewModel and lifecycleOwner set to binding")
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Logger.i(message = "CirclesFragment onViewCreated")
        setupLogging()
        viewModel.result.observe(viewLifecycleOwner) { result ->
            binding.textViewResult.text = result
        }
    }

    private fun setupLogging() {
        Logger.i(message = "Setting up logging and binding for EditText fields")
        binding.editTextX1.addLogging("x1", viewModel.x1)
        binding.editTextY1.addLogging("y1", viewModel.y1)
        binding.editTextR1.addLogging("r1", viewModel.r1)
        binding.editTextX2.addLogging("x2", viewModel.x2)
        binding.editTextY2.addLogging("y2", viewModel.y2)
        binding.editTextR2.addLogging("r2", viewModel.r2)
    }
}

package edu.chapman.monsutauoka.ui.third

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import edu.chapman.monsutauoka.databinding.FragmentGammaBinding
import edu.chapman.monsutauoka.extensions.TAG

class GammaFragment : Fragment() {

    private var _binding: FragmentGammaBinding? = null
    private val binding get() = _binding!!

    private val viewModel: GammaViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Log.d(TAG, ::onCreateView.name)

        _binding = FragmentGammaBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        Log.d(TAG, ::onViewCreated.name)

        binding.buttonDecrement.setOnClickListener {
            viewModel.num.value--
        }

        binding.buttonIncrement.setOnClickListener {
            viewModel.num.value++
        }

        viewModel.num.observe(viewLifecycleOwner) { numValue ->
            binding.textGamma.text = numValue.toString()
        }
    }

    override fun onDestroyView() {
        Log.d(TAG, ::onDestroyView.name)

        super.onDestroyView()
        _binding = null
    }
}
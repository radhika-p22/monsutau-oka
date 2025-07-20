package edu.chapman.monsutauoka.ui.third

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import edu.chapman.monsutauoka.databinding.FragmentGammaBinding
import edu.chapman.monsutauoka.extensions.TAG
import edu.chapman.monsutauoka.ui.GenericViewModelFactory
import edu.chapman.monsutauoka.ui.MainFragmentBase
import edu.chapman.monsutauoka.ui.first.AlphaViewModel

class GammaFragment : MainFragmentBase<FragmentGammaBinding>() {

    private val viewModel: GammaViewModel by viewModels {
        GenericViewModelFactory {
            GammaViewModel()
        }
    }

    override fun createViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentGammaBinding {
        return FragmentGammaBinding.inflate(inflater, container, false)
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
}
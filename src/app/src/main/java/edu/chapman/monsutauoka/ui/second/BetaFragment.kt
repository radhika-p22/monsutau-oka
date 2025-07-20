package edu.chapman.monsutauoka.ui.second

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import edu.chapman.monsutauoka.databinding.FragmentBetaBinding
import edu.chapman.monsutauoka.extensions.TAG
import edu.chapman.monsutauoka.ui.MainFragmentBase

class BetaFragment : MainFragmentBase<FragmentBetaBinding>() {

    override fun createViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentBetaBinding {
        return FragmentBetaBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        Log.d(TAG, ::onViewCreated.name)

        binding.buttonDecrement.setOnClickListener {
            var i = binding.textBeta.text.toString().toIntOrNull() ?: 0
            i--
            binding.textBeta.text = i.toString()
        }

        binding.buttonIncrement.setOnClickListener {
            var i = binding.textBeta.text.toString().toIntOrNull() ?: 0
            i++
            binding.textBeta.text = i.toString()
        }
    }
}
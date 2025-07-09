package edu.chapman.monsutauoka.ui.second

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import edu.chapman.monsutauoka.databinding.FragmentBetaBinding
import edu.chapman.monsutauoka.extensions.TAG

class BetaFragment : Fragment() {

    private var _binding: FragmentBetaBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Log.d(TAG, ::onCreateView.name)

        _binding = FragmentBetaBinding.inflate(inflater, container, false)
        return binding.root
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

    override fun onDestroyView() {
        Log.d(TAG, ::onDestroyView.name)

        super.onDestroyView()
        _binding = null
    }
}
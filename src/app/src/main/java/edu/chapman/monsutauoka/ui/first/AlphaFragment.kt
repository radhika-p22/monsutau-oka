package edu.chapman.monsutauoka.ui.first

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import edu.chapman.monsutauoka.databinding.FragmentAlphaBinding
import edu.chapman.monsutauoka.extensions.TAG
import edu.chapman.monsutauoka.extensions.applySystemBarPadding
import edu.chapman.monsutauoka.ui.GenericViewModelFactory
import edu.chapman.monsutauoka.ui.MainFragmentBase

class AlphaFragment : MainFragmentBase<FragmentAlphaBinding>() {

    private val PREF_NAME = "pet_prefs"
    private val PREF_KEY_TREATS = "treat_count"
    private val PREF_KEY_TREAT_PROGRESS = "treat_progress"
    private val STEPS_PER_TREAT = 5


    private val viewModel: AlphaViewModel by viewModels {
        GenericViewModelFactory { AlphaViewModel(mainActivity.stepCounterService) }
    }

    private var lastSteps: Int? = null

    override fun createViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentAlphaBinding.inflate(inflater, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        Log.d(TAG, ::onViewCreated.name)
        binding.root.applySystemBarPadding()

        viewModel.steps.observe(viewLifecycleOwner) { value: Float ->
            val current = value.toInt()               // convert Float -> Int (whole steps)
            binding.textSteps.text = current.toString()

            val prev = lastSteps
            if (prev != null) {
                val delta = current - prev
                if (delta > 0) {
                    addStepsTowardTreat(requireContext(), delta)
                }
                // if delta <= 0 (sensor reset/duplicate), ignore
            }
            lastSteps = current
        }
    }

    private fun addStepsTowardTreat(context: Context, steps: Int) {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val treats = prefs.getInt(PREF_KEY_TREATS, 0)
        val progress = prefs.getInt(PREF_KEY_TREAT_PROGRESS, 0)

        val total = progress + steps
        val newTreats = treats + (total / STEPS_PER_TREAT)
        val newProgress = total % STEPS_PER_TREAT

        prefs.edit()
            .putInt(PREF_KEY_TREATS, newTreats)
            .putInt(PREF_KEY_TREAT_PROGRESS, newProgress)
            .apply()
    }
}

package edu.chapman.monsutauoka.ui.first

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import edu.chapman.monsutauoka.R
import edu.chapman.monsutauoka.databinding.FragmentAlphaBinding
import edu.chapman.monsutauoka.extensions.TAG
import edu.chapman.monsutauoka.extensions.applySystemBarPadding
import edu.chapman.monsutauoka.ui.GenericViewModelFactory
import edu.chapman.monsutauoka.ui.MainFragmentBase

class AlphaFragment : MainFragmentBase<FragmentAlphaBinding>() {

    private val PREF_NAME = "pet_prefs"
    private val PREF_KEY_TREATS = "treat_count"
    private val PREF_KEY_TREAT_PROGRESS = "treat_progress"
    private val STEPS_PER_TREAT = 60

    private val PREF_KEY_HAPPINESS = "happiness_level"

    private var lastSteps: Int? = null


    private val viewModel: AlphaViewModel by viewModels {
        GenericViewModelFactory { AlphaViewModel(mainActivity.stepCounterService) }
    }

    override fun createViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentAlphaBinding.inflate(inflater, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        Log.d(TAG, ::onViewCreated.name)
        binding.root.applySystemBarPadding()
//        updateMoodImageFromPrefs()

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
//            updateMoodImageFromPrefs()
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

//    private fun updateMoodImageFromPrefs() {
//        val prefs = requireContext().getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
//        val level = prefs.getInt(PREF_KEY_HAPPINESS, 15) // default if not set yet
//        updateMoodImage(level)
//    }

//    private fun updateMoodImage(level: Int) {
//        // thresholds: <=3, <=8, <=12, <=16, else
//        val resId = when {
//            level <= 3  -> R.drawable.pet_mood_1   // very sad
//            level <= 8  -> R.drawable.pet_mood_2   // sad
//            level <= 12 -> R.drawable.pet_mood_3   // neutral
//            level <= 16 -> R.drawable.pet_mood_4   // happy
//            else        -> R.drawable.pet_mood_5   // very happy
//        }
//        binding.imageOverlay.setImageResource(resId)
//    }

//    override fun onResume() {
//        super.onResume()
//        updateMoodImageFromPrefs() // refresh when coming back from Beta
//    }

}

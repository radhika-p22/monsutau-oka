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

    // SharedPreferences file & keys used to persist treats, progress toward next treat, and happiness.
    private val PREF_NAME = "pet_prefs"
    private val PREF_KEY_TREATS = "treat_count"
    private val PREF_KEY_TREAT_PROGRESS = "treat_progress"
    private val STEPS_PER_TREAT = 15                      // every 15 steps -> +1 treat

    private val PREF_KEY_HAPPINESS = "happiness_level"    // read-only here, written in Beta

    // We remember the previous step reading so we can compute how many new steps happened (the delta).
    private var lastSteps: Int? = null

    // ViewModel exposes a LiveData of step counts (Float from the sensor).
    private val viewModel: AlphaViewModel by viewModels {
        GenericViewModelFactory { AlphaViewModel(mainActivity.stepCounterService) }
    }

    /** Inflates the view binding for Alpha (wires this fragment to fragment_alpha.xml). */
    override fun createViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentAlphaBinding.inflate(inflater, container, false)

    /**
     * Sets up the UI padding and starts observing step updates.
     * Each new step value is shown on screen; we compute the delta since the last value and
     * convert those steps into treats (persisted via SharedPreferences).
     */
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

    /**
     * Adds the given number of steps toward earning treats.
     * We accumulate progress and every STEPS_PER_TREAT steps, we add +1 treat and persist both values.
     */
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

    /**
     * Reads the current happiness level from SharedPreferences and updates the monster image.
     * (Happiness is maintained on the Beta screen; Alpha only displays it.)
     */
    private fun updateMoodImageFromPrefs() {
        val prefs = requireContext().getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val level = prefs.getInt(PREF_KEY_HAPPINESS, 15) // default if not set yet
        updateMoodImage(level)
    }

    /**
     * Switches the overlay image based on happiness ranges:
     * <=3 angry, <=8 worried, <=12 normal, <=16 content, else happy.
     */
    private fun updateMoodImage(level: Int) {
        // thresholds: <=3, <=8, <=12, <=16, else
        val resId = when {
            level <= 3  -> R.drawable.pikachu_angry  // very angry
            level <= 8  -> R.drawable.pikachu_worried  // sad
            level <= 12 -> R.drawable.pikachu_normal   // neutral
            level <= 16 -> R.drawable.pikachu_content  // happy
            else        -> R.drawable.pikachu_happy   // very happy
        }
        binding.imageOverlay.setImageResource(resId)
    }

    /**
     * When returning to the Alpha screen, refresh the mood image in case happiness changed in Beta.
     */
    override fun onResume() {
        super.onResume()
        updateMoodImageFromPrefs() // refresh when coming back from Beta
    }
}

package edu.chapman.monsutauoka.ui.third

import android.content.Context
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import edu.chapman.monsutauoka.databinding.FragmentGammaBinding
import edu.chapman.monsutauoka.extensions.TAG
import edu.chapman.monsutauoka.ui.MainFragmentBase
import kotlin.math.min
import kotlin.random.Random

class GammaFragment : MainFragmentBase<FragmentGammaBinding>() {

    // same prefs/keys used in Alpha/Beta
    private val PREF_NAME = "pet_prefs"
    private val PREF_KEY_TREATS = "treat_count"

    // same config
    private val roundMillis = 60_000L
    private var timer: CountDownTimer? = null
    private var running = false
    private var score = 0

    // question
    private var currentText = ""
    private var currentAnswer = false

    override fun createViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentGammaBinding.inflate(inflater, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        Log.d(TAG, ::onViewCreated.name)

        // Left button = Start / True
        binding.buttonDecrement.setOnClickListener {
            if (!running) startGame() else answer(true)
        }

        // Right button = False (disabled until game starts)
        binding.buttonIncrement.setOnClickListener {
            if (running) answer(false)
        }

        idleUI()
        super.onViewCreated(view, savedInstanceState)
    }

    private fun startGame() {
        running = true
        score = 0
        binding.buttonDecrement.text = getString(edu.chapman.monsutauoka.R.string._true)
        binding.buttonIncrement.text = getString(edu.chapman.monsutauoka.R.string._false)
        binding.buttonIncrement.isEnabled = true

        nextQuestion()
        timer?.cancel()
        timer = object : CountDownTimer(roundMillis, 1000L) {
            override fun onTick(ms: Long) {
                binding.textGamma.text = "(${ms / 1000}s)  $currentText"
            }
            override fun onFinish() {
                running = false
                awardTreats()
                idleUI()
            }
        }.start()
    }

    private fun idleUI() {
        timer?.cancel()
        running = false
        binding.buttonDecrement.text = "Start"
        binding.buttonIncrement.text = getString(edu.chapman.monsutauoka.R.string._false)
        binding.buttonIncrement.isEnabled = false
        binding.textGamma.text = "True/False — 60s.\nPress Start."
    }

    private fun nextQuestion() {
        // Random rule among: prime, perfect square, palindrome
        val n = Random.nextInt(2, 199) 
        when (Random.nextInt(3)) {
            0 -> { currentText = "Is $n prime?"; currentAnswer = isPrime(n) }
            1 -> { currentText = "Is $n a perfect square?"; currentAnswer = isPerfectSquare(n) }
            else -> { currentText = "Is $n a palindrome?"; currentAnswer = isPalindrome(n) }
        }
        if (running) binding.textGamma.text = "(…s)  $currentText" // tick will overwrite seconds
    }

    private fun answer(choice: Boolean) {
        if (!running) return
        if (choice == currentAnswer) score++
        nextQuestion()
    }

    private fun awardTreats() {
        val earned = min(8, score / 4)
        val prefs = requireContext().getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val total = prefs.getInt(PREF_KEY_TREATS, 0) + earned
        prefs.edit().putInt(PREF_KEY_TREATS, total).apply()

        binding.textGamma.text = "Score: $score → +$earned treats. Total: $total"
        binding.buttonIncrement.isEnabled = false
        binding.buttonDecrement.text = "Start"
    }

    // helpers
    private fun isPrime(n: Int): Boolean {
        if (n < 2) return false
        if (n % 2 == 0) return n == 2
        var i = 3
        while (i * i <= n) {
            if (n % i == 0) return false
            i += 2
        }
        return true
    }

    private fun isPerfectSquare(n: Int): Boolean {
        val r = kotlin.math.sqrt(n.toFloat()).toInt()
        return r * r == n
    }

    private fun isPalindrome(n: Int): Boolean {
        val s = n.toString()
        return s == s.reversed()
    }

    override fun onDestroyView() {
        timer?.cancel()
        timer = null
        super.onDestroyView()
    }
}

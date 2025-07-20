package edu.chapman.monsutauoka.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding
import edu.chapman.monsutauoka.MainActivity
import edu.chapman.monsutauoka.extensions.TAG

abstract class MainFragmentBase<T : ViewBinding> : Fragment() {

    private var _binding: T? = null
    protected val binding get() = _binding!!

    protected val mainActivity get() = requireActivity() as MainActivity

    protected abstract fun createViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?): T

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d(TAG, ::onCreate.name)
        super.onCreate(savedInstanceState)
    }

    override fun onDestroy() {
        Log.d(TAG, ::onDestroy.name)
        super.onDestroy()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Log.d(TAG, ::onCreateView.name)
        _binding = createViewBinding(inflater, container)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        Log.d(TAG, ::onViewCreated.name)
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onDestroyView() {
        Log.d(TAG, ::onDestroyView.name)
        super.onDestroyView()
        _binding = null
    }
}
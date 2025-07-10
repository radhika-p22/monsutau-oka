package edu.chapman.monsutauoka.ui.first

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import applySystemBarPadding
import edu.chapman.monsutauoka.databinding.FragmentAlphaBinding
import edu.chapman.monsutauoka.extensions.BaseListAdapter
import edu.chapman.monsutauoka.extensions.TAG
import kotlin.random.Random

class AlphaFragment : Fragment() {

    private var _binding: FragmentAlphaBinding? = null
    private val binding get() = _binding!!

    // This is new, 1 of 4!
    private val viewModel: PersonViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Log.d(TAG, ::onCreateView.name)

        _binding = FragmentAlphaBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        Log.d(TAG, ::onViewCreated.name)

        binding.root.applySystemBarPadding()

        // This is new, 2 of 4!
        setupRecycler()

        // This is new, 3 of 4!
        binding.buttonAddPerson.setOnClickListener {
            viewModel.addPerson(
                Person(
                    Random.nextInt('A'.code, 'Z'.code + 1).toChar().toString(),
                    Random.nextInt(1, 100)
                )
            )
        }
    }

    fun setupRecycler() {

        // // This is new, 4 of 4!
        // ...and looks complicated...
        // ...just roll with it for now!

        val adapter = BaseListAdapter(
            layoutRes = android.R.layout.simple_list_item_2,
            diffCallback = object : DiffUtil.ItemCallback<Person>() {
                override fun areItemsTheSame(oldItem: Person, newItem: Person) = oldItem.name == newItem.name
                override fun areContentsTheSame(oldItem: Person, newItem: Person) = oldItem == newItem
            }
        ) { view, person, _ ->
            view.findViewById<TextView>(android.R.id.text1).text = "Name: ${person.name}"
            view.findViewById<TextView>(android.R.id.text2).text = "Age: ${person.age}"
        }

        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = adapter

        viewModel.personList.observe(viewLifecycleOwner) { people ->
            adapter.submitList(people)
        }
    }

    override fun onDestroyView() {
        Log.d(TAG, ::onDestroyView.name)

        super.onDestroyView()
        _binding = null
    }
}
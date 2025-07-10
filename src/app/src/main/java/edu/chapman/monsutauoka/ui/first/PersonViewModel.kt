package edu.chapman.monsutauoka.ui.first

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class PersonViewModel : ViewModel() {
    private val _personList = MutableLiveData<List<Person>>()
    val personList: LiveData<List<Person>> = _personList

    init {
        _personList.value = listOf(
            Person("Alice", 25),
            Person("Bob", 30),
            Person("Charlie", 22)
        )
    }
    fun addPerson(person: Person) {
        _personList.value = _personList.value.orEmpty() + person
    }
}
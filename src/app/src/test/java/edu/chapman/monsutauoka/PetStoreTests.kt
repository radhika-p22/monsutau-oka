package edu.chapman.monsutauoka

import org.junit.Test

import org.junit.Assert.*

abstract class Animal(val name: String) {
    abstract fun speak()

    fun sleep() {
        println("$name is sleeping.")
    }
}

interface Pet {
    fun play(): String
}

class Dog(name: String) : Animal(name), Pet {
    override fun speak() {
        println("$name says: Woof!")
    }

    override fun play(): String {
        return "$name is chasing the ball."
    }
}

class Cat(name: String) : Animal(name), Pet {
    override fun speak() {
        println("$name says: Meow!")
    }

    override fun play(): String {
        return "$name is playing with a ball of yarn."
    }
}

class PetStoreTest {
    @Test
    fun `cats and dogs play differently`() {
        // Arrange
        var pets : List<Pet> = listOf(
            Dog("Taboo"),
            Cat("Tiny Kitty")
        )

        // Act
        pets = pets.shuffled()

        // Assert
        assertNotEquals(pets[0].play(), pets[1].play())
    }
}
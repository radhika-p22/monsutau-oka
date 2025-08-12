package edu.chapman.monsutauoka

import org.junit.Test

import org.junit.Assert.*


/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun `dog says woof`() {
        var sandy = Dog()
        var whatTheDogSaid = sandy.speak()
        assertEquals("Woof!", whatTheDogSaid)
    }
    @Test
    fun `mouse says squeek`() {
        var tiny = Mouse()
        var whatTheMouseSaid = tiny.speak()
        assertEquals("Squeek!", whatTheMouseSaid)
    }
}

abstract class Animal {
    abstract fun speak(): String
}

class Dog : Animal() {
    override fun speak(): String {
        return "Bark!"
    }
}

class Cat : Animal() {
    override fun speak(): String {
        return "Meow?"
    }
}

class Mouse : Animal() {
    override fun speak(): String {
        TODO("Not yet implemented")
    }
}

abstract class Thing {
    abstract fun getValue(): Int
}
open class ThingOne : Thing() {
    override fun getValue(): Int {
        return 1
    }
}
class ThingTwo : ThingOne() {
    override fun getValue(): Int {
        return super.getValue() + 2
    }
}
class MidTermUnitTests {
    @Test
    fun `Thing One's Value Is Correct`() {
        assertEquals(1, ThingOne().getValue())
    }
    @Test
    fun `Thing Two's Value Is Correct`() {
        assertEquals(3, ThingTwo().getValue())
    }
}
package edu.chapman.monsutauoka

import junit.framework.TestCase.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized

fun String.reverseWords1(): String {
    val words = this.trim().split(" ")
    var result = ""

    for (i in words.size - 1 downTo 0) {
        result += words[i]
        if (i != 0) {
            result += " "
        }
    }

    return result
}

fun String.reverseWords2(): String {
    val words = this.trim().split(" ")
    val result = StringBuilder()

    for (i in words.size - 1 downTo 0) {
        result.append(words[i])
        if (i != 0) {
            result.append(" ")
        }
    }

    return result.toString()
}

fun String.reverseWords3(): String {
    val trimmed = this.trim()
    val result = StringBuilder()

    var end = trimmed.length - 1

    // Outer loop moves backwards through the string
    for (i in trimmed.length - 1 downTo 0) {
        if (trimmed[i].isWhitespace()) {
            // Inner loop collects characters of the current word
            if (i < end) {
                for (j in i + 1..end) {
                    result.append(trimmed[j])
                }
                result.append(" ")
            }
            end = i - 1
        } else if (i == 0) {
            // Handle the first word
            for (j in i..end) {
                result.append(trimmed[j])
            }
        }
    }

    return result.toString()
}

fun String.reverseWords4(): String {
    return this
        .trim()
        .split(" ")
        .reversed()
        .joinToString(" ")
}

fun String.reverseWords5(): String {
    return this
        .trim()
        .split("\\s+".toRegex())
        .reversed()
        .joinToString(" ")
}

class ReverseWordsTest {

    @Test
    fun `Pika Activity test`() {
        val planner = PikaActivityPlanner()
    }

    @Test
    fun `reverse empty string`() {
        val input = ""
        val expected = ""
        assertEquals(expected, input.reverseWords5())
    }

    @Test
    fun `reverse single word`() {
        val input = "Hello"
        val expected = "Hello"
        assertEquals(expected, input.reverseWords5())
    }

    @Test
    fun `reverse normal sentence`() {
        val input = "Hello world Kotlin"
        val expected = "Kotlin world Hello"
        assertEquals(expected, input.reverseWords2())
    }

    @Test
    fun `reverse spaces only`() {
        val input = "     "
        val expected = ""
        assertEquals(expected, input.reverseWords5())
    }

    @Test
    fun `reverse with extra spaces`() {
        val input = "  Hello   world   Kotlin  "
        val expected = "Kotlin world Hello"
        assertEquals(expected, input.reverseWords4())
    }
}


@RunWith(Parameterized::class)
class DataDrivenReverseWordsTest(
    private val input: String,
    private val expected: String) {

    companion object {
        @JvmStatic
        @Parameterized.Parameters(name = "{index}: reverseWords({0}) = {1}")
        fun data(): Collection<Array<Any>> {
            return listOf(
                arrayOf("", ""),
                arrayOf("Hello", "Hello"),
                arrayOf("Hello world Kotlin", "Kotlin world Hello"),
                arrayOf("     ", ""),
                arrayOf("  Hello   world   Kotlin  ", "Kotlin world Hello")
            )
        }
    }

    @Test
    fun testReverseWords() {
        assertEquals(expected, input.reverseWords5())
    }
}
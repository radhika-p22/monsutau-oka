package edu.chapman.monsutauoka

import edu.chapman.monsutauoka.services.StepCounterService
import edu.chapman.monsutauoka.services.data.DataStore
import org.junit.Assert
import org.junit.Test

class StepCounterServiceTest {

    class MockDataStore : DataStore {
        private val storage = mutableMapOf<String, String>()

        override fun save(key: String, value: String) {
            storage[key] = value
        }

        override fun load(key: String): String? {
            return storage[key]
        }
    }

    @Test
    fun `should initialize steps from DataStore`() {
        // Arrange
        val mockStore = MockDataStore()
        mockStore.save("StepCounterService.steps", "12.5")

        // Act
        val service = StepCounterService(mockStore)

        // Assert
        Assert.assertEquals(12.5f, service.steps.value)
    }

    @Test
    fun `should accumulate steps correctly`() {
        // Arrange
        val mockStore = MockDataStore()
        val service = StepCounterService(mockStore)

        // Act
        service.updateSteps(100f) // initializes previousCount
        val afterFirstUpdate = service.steps.value

        service.updateSteps(110f) // adds 10
        val afterSecondUpdate = service.steps.value

        service.updateSteps(125f) // adds 15
        val afterThirdUpdate = service.steps.value

        val savedValue = mockStore.load("StepCounterService.steps")?.toFloat()

        // Assert
        Assert.assertEquals(0f, afterFirstUpdate)
        Assert.assertEquals(10f, afterSecondUpdate)
        Assert.assertEquals(25f, afterThirdUpdate)
        Assert.assertEquals(25f, savedValue)
    }
}
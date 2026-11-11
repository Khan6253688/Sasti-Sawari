package com.example

import com.example.data.model.FareCalculator
import com.example.data.model.PakistanCitiesData
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class FareCalculatorTest {

  @Test
  fun testMinimumFareWithinTwoKm() {
    val result1 = FareCalculator.calculate(distanceKm = 1.0, waitingMinutes = 0)
    assertEquals(80.0, result1.totalFare, 0.001)

    val result2 = FareCalculator.calculate(distanceKm = 2.0, waitingMinutes = 0)
    assertEquals(80.0, result2.totalFare, 0.001)
  }

  @Test
  fun testPromptFormulaExamples() {
    // 2 km = Rs. 80
    assertEquals(80.0, FareCalculator.calculate(2.0, 0).totalFare, 0.001)
    // 3 km = Rs. 105
    assertEquals(105.0, FareCalculator.calculate(3.0, 0).totalFare, 0.001)
    // 4 km = Rs. 130
    assertEquals(130.0, FareCalculator.calculate(4.0, 0).totalFare, 0.001)
    // 5 km = Rs. 155
    assertEquals(155.0, FareCalculator.calculate(5.0, 0).totalFare, 0.001)
    // 6 km = Rs. 180
    assertEquals(180.0, FareCalculator.calculate(6.0, 0).totalFare, 0.001)
    // 8 km = Rs. 230
    assertEquals(230.0, FareCalculator.calculate(8.0, 0).totalFare, 0.001)
    // 10 km = Rs. 280
    assertEquals(280.0, FareCalculator.calculate(10.0, 0).totalFare, 0.001)
  }

  @Test
  fun testWaitingCharges() {
    // First 5 minutes free
    val freeWait = FareCalculator.calculate(distanceKm = 2.0, waitingMinutes = 5)
    assertEquals(0.0, freeWait.waitingFare, 0.001)
    assertEquals(80.0, freeWait.totalFare, 0.001)

    // 10 minutes wait: 5 chargeable mins * 5 = Rs. 25
    val paidWait = FareCalculator.calculate(distanceKm = 2.0, waitingMinutes = 10)
    assertEquals(25.0, paidWait.waitingFare, 0.001)
    assertEquals(105.0, paidWait.totalFare, 0.001)
  }

  @Test
  fun testFractionalDistanceRounding() {
    // 3.5 km: 80 + 1.5 * 25 = 117.5 -> rounded to 118
    val fractional = FareCalculator.calculate(distanceKm = 3.5, waitingMinutes = 0)
    assertEquals(118.0, fractional.totalFare, 0.001)
  }

  @Test
  fun testOfflineHaversineDistanceCalculation() {
    val multan = PakistanCitiesData.getCityById("multan")
    val stand = multan.locations.first { it.category == "Stand" }
    val nishtar = multan.locations.first { it.name.contains("Nishtar") }
    val dist = PakistanCitiesData.calculateAccurateDistance(stand, nishtar)
    // Road distance between Ghanta Ghar and Nishtar Hospital is around 3.5 to 4.5 km
    assertTrue("Distance should be realistic: $dist km", dist in 3.0..5.5)
  }
}

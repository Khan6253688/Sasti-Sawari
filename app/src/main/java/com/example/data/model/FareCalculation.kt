package com.example.data.model

import kotlin.math.max
import kotlin.math.round

data class FareBreakdown(
  val distanceKm: Double,
  val waitingMinutes: Int,
  val baseFare: Double,
  val baseKm: Double,
  val additionalKm: Double,
  val distanceFare: Double,
  val freeWaitMinutes: Int,
  val chargeableWaitMinutes: Int,
  val waitingFare: Double,
  val totalFare: Double
)

object FareCalculator {
  fun calculate(
    distanceKm: Double,
    waitingMinutes: Int,
    baseFare: Double = 80.0,
    baseKm: Double = 2.0,
    ratePerKm: Double = 25.0,
    freeWaitMinutes: Int = 5,
    ratePerWaitMinute: Double = 5.0
  ): FareBreakdown {
    val sanitizedDistance = max(0.0, distanceKm)
    val sanitizedWaiting = max(0, waitingMinutes)

    val additionalKm = if (sanitizedDistance > baseKm) sanitizedDistance - baseKm else 0.0
    val distanceFare = additionalKm * ratePerKm

    val chargeableWait = if (sanitizedWaiting > freeWaitMinutes) sanitizedWaiting - freeWaitMinutes else 0
    val waitingFare = chargeableWait * ratePerWaitMinute

    val calculatedTotal = if (sanitizedDistance <= 0.0 && sanitizedWaiting <= 0) {
      0.0
    } else {
      round(baseFare + distanceFare + waitingFare)
    }

    return FareBreakdown(
      distanceKm = sanitizedDistance,
      waitingMinutes = sanitizedWaiting,
      baseFare = if (sanitizedDistance > 0 || sanitizedWaiting > 0) baseFare else 0.0,
      baseKm = baseKm,
      additionalKm = additionalKm,
      distanceFare = distanceFare,
      freeWaitMinutes = freeWaitMinutes,
      chargeableWaitMinutes = chargeableWait,
      waitingFare = waitingFare,
      totalFare = calculatedTotal
    )
  }
}

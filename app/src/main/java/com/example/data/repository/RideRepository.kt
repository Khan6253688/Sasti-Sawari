package com.example.data.repository

import com.example.data.local.AdminDao
import com.example.data.local.CityDao
import com.example.data.local.ExpenseDao
import com.example.data.local.RideDao
import com.example.data.local.RiderAccountDao
import com.example.data.local.SettingsDao
import com.example.data.model.AdminConfig
import com.example.data.model.AppSettings
import com.example.data.model.CityDestination
import com.example.data.model.CustomCity
import com.example.data.model.Expense
import com.example.data.model.Ride
import com.example.data.model.RiderAccount
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class CustomerSummary(
  val phone: String,
  val name: String,
  val totalRides: Int,
  val totalSpent: Double,
  val lastRideDate: Long,
  val destinations: List<String>,
  val notes: String
)

class RideRepository(
  private val rideDao: RideDao,
  private val expenseDao: ExpenseDao,
  private val settingsDao: SettingsDao,
  private val cityDao: CityDao,
  private val riderAccountDao: RiderAccountDao,
  private val adminDao: AdminDao
) {
  val allRides: Flow<List<Ride>> = rideDao.getAllRides()
  val allExpenses: Flow<List<Expense>> = expenseDao.getAllExpenses()
  val settings: Flow<AppSettings?> = settingsDao.getSettings()
  val allDestinations: Flow<List<CityDestination>> = cityDao.getAllDestinations()
  val customCities: Flow<List<CustomCity>> = cityDao.getAllCustomCities()
  val allRiders: Flow<List<RiderAccount>> = riderAccountDao.getAllRiders()
  val adminConfig: Flow<AdminConfig?> = adminDao.getAdminConfig()

  fun getActiveRidersInCity(cityId: String): Flow<List<RiderAccount>> =
    riderAccountDao.getActiveRidersInCity(cityId)

  fun getRiderById(riderId: String): Flow<RiderAccount?> =
    riderAccountDao.getRiderById(riderId)

  suspend fun getRiderByPhone(phone: String): RiderAccount? =
    riderAccountDao.getRiderByPhone(phone)

  suspend fun saveRider(rider: RiderAccount) =
    riderAccountDao.insertOrUpdateRider(rider)

  suspend fun deleteRider(rider: RiderAccount) =
    riderAccountDao.deleteRider(rider)

  suspend fun saveAdminConfig(config: AdminConfig) =
    adminDao.saveAdminConfig(config)


  fun getDestinationsForCity(cityId: String): Flow<List<CityDestination>> =
    cityDao.getDestinationsForCity(cityId)

  suspend fun insertDestination(destination: CityDestination): Long =
    cityDao.insertDestination(destination)

  suspend fun deleteDestination(destination: CityDestination) =
    cityDao.deleteDestination(destination)

  suspend fun deleteDestinationById(id: Long) =
    cityDao.deleteDestinationById(id)

  suspend fun insertCustomCity(city: CustomCity) =
    cityDao.insertCustomCity(city)

  suspend fun deleteCustomCity(city: CustomCity) =
    cityDao.deleteCustomCity(city)


  val customers: Flow<List<CustomerSummary>> = allRides.map { rides ->
    rides
      .filter { it.customerPhone.isNotBlank() || it.customerName.isNotBlank() }
      .groupBy {
        if (it.customerPhone.isNotBlank()) it.customerPhone.trim()
        else it.customerName.trim().lowercase()
      }
      .map { (key, customerRides) ->
        val sorted = customerRides.sortedByDescending { it.timestamp }
        val latest = sorted.first()
        CustomerSummary(
          phone = latest.customerPhone,
          name = latest.customerName.ifBlank { "Customer ($key)" },
          totalRides = customerRides.size,
          totalSpent = customerRides.sumOf { it.totalFare },
          lastRideDate = latest.timestamp,
          destinations = customerRides.map { it.destination }.distinct(),
          notes = sorted.mapNotNull { it.notes.ifBlank { null } }.joinToString("; ")
        )
      }
      .sortedByDescending { it.totalRides }
  }

  suspend fun insertRide(ride: Ride): Long = rideDao.insertRide(ride)
  suspend fun updateRide(ride: Ride) = rideDao.updateRide(ride)
  suspend fun deleteRide(ride: Ride) = rideDao.deleteRide(ride)
  suspend fun deleteRideById(id: Long) = rideDao.deleteRideById(id)

  suspend fun insertExpense(expense: Expense): Long = expenseDao.insertExpense(expense)
  suspend fun deleteExpense(expense: Expense) = expenseDao.deleteExpense(expense)

  suspend fun saveSettings(settings: AppSettings) = settingsDao.saveSettings(settings)

  fun exportRidesToCsv(rides: List<Ride>): String {
    val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
    val sb = StringBuilder()
    sb.append("Ride ID,Date Time,Customer,Phone,Pickup,Destination,Distance (km),Waiting (min),Base Fare (PKR),Distance Fare (PKR),Waiting Fare (PKR),Total Fare (PKR),Payment Method,Notes\n")
    for (r in rides) {
      val dateStr = dateFormat.format(Date(r.timestamp))
      sb.append("\"${r.rideCode}\",")
      sb.append("\"$dateStr\",")
      sb.append("\"${r.customerName.replace("\"", "\"\"")}\",")
      sb.append("\"${r.customerPhone.replace("\"", "\"\"")}\",")
      sb.append("\"${r.pickupLocation.replace("\"", "\"\"")}\",")
      sb.append("\"${r.destination.replace("\"", "\"\"")}\",")
      sb.append("${r.distanceKm},")
      sb.append("${r.waitingMinutes},")
      sb.append("${r.baseFare},")
      sb.append("${r.distanceFare},")
      sb.append("${r.waitingFare},")
      sb.append("${r.totalFare},")
      sb.append("\"${r.paymentMethod}\",")
      sb.append("\"${r.notes.replace("\"", "\"\"")}\"\n")
    }
    return sb.toString()
  }

  fun exportBusinessReportCsv(
    rides: List<Ride>,
    expenses: List<Expense>,
    settings: AppSettings
  ): String {
    val totalRides = rides.size
    val totalRevenue = rides.sumOf { it.totalFare }
    val totalKm = rides.sumOf { it.distanceKm }
    val totalWaitingRevenue = rides.sumOf { it.waitingFare }
    val estFuelCost = if (settings.bikeFuelEconomyKmPerLitre > 0) {
      (totalKm / settings.bikeFuelEconomyKmPerLitre) * settings.fuelPricePerLitre
    } else 0.0
    val actualExpenses = expenses.sumOf { it.amount }
    val netProfit = totalRevenue - actualExpenses

    val sb = StringBuilder()
    sb.append("MULTAN BIKE RIDE SERVICE - BUSINESS SUMMARY REPORT\n")
    sb.append("Generated On,${SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(Date())}\n")
    sb.append("Motorcycle,${settings.motorcycleModel}\n")
    sb.append("Fuel Economy,${settings.bikeFuelEconomyKmPerLitre} km/L\n")
    sb.append("Current Fuel Price,Rs. ${settings.fuelPricePerLitre} / Litre\n")
    sb.append("Stand Location,${settings.standName}\n\n")

    sb.append("METRIC,VALUE\n")
    sb.append("Total Recorded Rides,$totalRides\n")
    sb.append("Total Distance Travelled,${"%.1f".format(totalKm)} km\n")
    sb.append("Total Revenue Collected,PKR ${totalRevenue.toInt()}\n")
    sb.append("Waiting Revenue,PKR ${totalWaitingRevenue.toInt()}\n")
    sb.append("Estimated Fuel Expense (Km-based),PKR ${estFuelCost.toInt()}\n")
    sb.append("Actual Recorded Expenses,PKR ${actualExpenses.toInt()}\n")
    sb.append("Net Operating Profit,PKR ${netProfit.toInt()}\n\n")

    sb.append("EXPENSES BREAKDOWN\n")
    sb.append("Date,Category,Description,Amount (PKR)\n")
    val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    for (e in expenses) {
      sb.append("${dateFormat.format(Date(e.timestamp))},${e.category},\"${e.description.replace("\"", "\"\"")}\",${e.amount}\n")
    }

    return sb.toString()
  }
}

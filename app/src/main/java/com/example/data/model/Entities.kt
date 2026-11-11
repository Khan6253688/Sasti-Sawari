package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "rides")
data class Ride(
  @PrimaryKey(autoGenerate = true)
  val id: Long = 0,
  val rideCode: String,
  val customerName: String = "",
  val customerPhone: String = "",
  val pickupLocation: String = "Ghanta Ghar Stand, Multan",
  val destination: String,
  val distanceKm: Double,
  val waitingMinutes: Int = 0,
  val baseFare: Double = 80.0,
  val distanceFare: Double = 0.0,
  val waitingFare: Double = 0.0,
  val totalFare: Double = 80.0,
  val paymentMethod: String = "Cash", // Cash, JazzCash, Easypaisa
  val timestamp: Long = System.currentTimeMillis(),
  val notes: String = ""
)

@Entity(tableName = "expenses")
data class Expense(
  @PrimaryKey(autoGenerate = true)
  val id: Long = 0,
  val category: String, // Fuel, Maintenance, Stand Fee, Food/Chai, Other
  val amount: Double,
  val description: String,
  val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "app_settings")
data class AppSettings(
  @PrimaryKey
  val id: Int = 1,
  val selectedCityId: String = "multan",
  val operatorName: String = "Muhammad Rashid",
  val operatorPhone: String = "0308-5142950",
  val operatorWhatsApp: String = "923085142950",
  val motorcycleModel: String = "Honda CD 70 (2020)",
  val motorcyclePlate: String = "MNK-4821",
  val riderPhotoUri: String = "",
  val riderTagline: String = "Direct Pickup • Safe Ride • Most Affordable Fares",
  val standName: String = "Ghanta Ghar Stand",
  val standAddress: String = "Ghanta Ghar / Clock Tower, Multan",
  val baseFare: Double = 80.0,
  val baseKm: Double = 2.0,
  val ratePerAdditionalKm: Double = 25.0,
  val freeWaitingMinutes: Int = 5,
  val waitingRatePerMinute: Double = 5.0,
  val fuelPricePerLitre: Double = 275.0, // PKR
  val bikeFuelEconomyKmPerLitre: Double = 40.0 // Honda CD 70 2020 average
)

@Entity(tableName = "city_destinations")
data class CityDestination(
  @PrimaryKey(autoGenerate = true)
  val id: Long = 0,
  val cityId: String,
  val name: String,
  val urduName: String = "",
  val category: String = "Popular", // Transport, Hospital, University, Market, Landmark, Residential, Other
  val lat: Double = 0.0,
  val lng: Double = 0.0,
  val approxDistanceKm: Double = 0.0,
  val isCustom: Boolean = true
)

@Entity(tableName = "custom_cities")
data class CustomCity(
  @PrimaryKey
  val id: String,
  val name: String,
  val urduName: String = "",
  val tagLine: String = "Service City",
  val hallmarkTitle: String = "City Landmark",
  val hallmarkUrdu: String = "شہر کا مرکز",
  val hallmarkDescription: String = "Reliable Sasti Sawari bike ride service.",
  val hallmarkIconType: String = "monument",
  val defaultStandName: String = "Main Adda Stand",
  val defaultStandAddress: String = "Main Adda Chowk",
  val centerLat: Double = 30.1984,
  val centerLng: Double = 71.4687,
  val isEnabled: Boolean = true
)

@Entity(tableName = "rider_accounts")
data class RiderAccount(
  @PrimaryKey
  val riderId: String = "SW-1001",
  val name: String,
  val email: String = "",
  val phone: String,
  val cityId: String = "multan",
  val password: String = "1234",
  val photoUri: String = "",
  val bikeModel: String = "Honda CD 70",
  val plate: String = "MNK-4821",
  val standName: String = "Ghanta Ghar Stand",
  val standAddress: String = "Ghanta Ghar, Multan",
  val tagline: String = "Direct Pickup • Safe Ride",
  val subscriptionStatus: String = "ACTIVE", // ACTIVE, EXPIRED, PENDING, SUSPENDED
  val activatedAt: Long = System.currentTimeMillis(),
  val expiresAt: Long = System.currentTimeMillis() + (30L * 24 * 60 * 60 * 1000), // 30 days
  val lastPaymentTxnId: String = "",
  val lastPaymentMethod: String = "JazzCash", // JazzCash, Easypaisa, Bank
  val lastPaymentProofUri: String = "",
  val paymentSubmittedAt: Long = 0L,
  val isOnline: Boolean = true
)

@Entity(tableName = "admin_config")
data class AdminConfig(
  @PrimaryKey
  val id: Int = 1,
  val adminEmail: String = "shahid.msc2020@gmail.com",
  val adminPassword: String = "Khan@3636",
  val adminPin: String = "1234",
  val monthlySubscriptionFee: Double = 100.0,
  val subscriptionDurationDays: Int = 30,
  val jazzCashNumber: String = "0308-5142950",
  val jazzCashTitle: String = "Shahid (Sasti Sawari Admin)",
  val easypaisaNumber: String = "0308-5142950",
  val easypaisaTitle: String = "Shahid (Sasti Sawari Admin)",
  val bankName: String = "Habib Bank Limited (HBL)",
  val bankAccountTitle: String = "Sasti Sawari Services",
  val bankIban: String = "PK12HABB0000123456789012",
  val isTestModeSimulateExpired: Boolean = false,
  val isVoiceAnnouncementsEnabled: Boolean = true
)

data class PassengerProfile(
  val name: String = "",
  val phone: String = "",
  val cityId: String = "multan",
  val isProfileComplete: Boolean = false
)



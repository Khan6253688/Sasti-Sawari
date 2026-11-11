package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.model.AdminConfig
import com.example.data.model.AppSettings
import com.example.data.model.CityDestination
import com.example.data.model.CityInfo
import com.example.data.model.CustomCity
import com.example.data.model.Expense
import com.example.data.model.FareBreakdown
import com.example.data.model.FareCalculator
import com.example.data.model.LocationPoint
import com.example.data.model.PakistanCitiesData
import com.example.data.model.Ride
import com.example.data.model.RiderAccount
import com.example.data.model.PassengerProfile
import com.example.data.model.FirestorePayment
import com.example.data.model.FirestoreRider
import com.example.data.repository.CustomerSummary
import com.example.data.repository.FirestoreSubscriptionRepository
import com.example.data.repository.RideRepository
import com.example.util.VoiceService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Calendar

data class PeriodStats(
  val rideCount: Int = 0,
  val totalRevenue: Double = 0.0,
  val totalKm: Double = 0.0,
  val averageFare: Double = 0.0,
  val totalWaitingCharges: Double = 0.0,
  val estimatedFuelCost: Double = 0.0,
  val recordedExpenses: Double = 0.0,
  val netIncome: Double = 0.0
)

data class DestinationCount(
  val destination: String,
  val count: Int,
  val totalRevenue: Double
)

class MainViewModel(application: Application) : AndroidViewModel(application) {
  private val repository: RideRepository
  private val firestoreRepository = FirestoreSubscriptionRepository(application)

  val firestorePendingPayments: StateFlow<List<FirestorePayment>> = firestoreRepository.observePendingPayments()
    .stateIn(
      scope = viewModelScope,
      started = SharingStarted.WhileSubscribed(5000),
      initialValue = emptyList()
    )

  val firestoreAllPayments: StateFlow<List<FirestorePayment>> = firestoreRepository.observeAllPayments()
    .stateIn(
      scope = viewModelScope,
      started = SharingStarted.WhileSubscribed(5000),
      initialValue = emptyList()
    )

  val firestoreRiders: StateFlow<List<FirestoreRider>> = firestoreRepository.observeAllRiders()
    .stateIn(
      scope = viewModelScope,
      started = SharingStarted.WhileSubscribed(5000),
      initialValue = emptyList()
    )

  init {
    VoiceService.init(application)
    val db = AppDatabase.getDatabase(application, viewModelScope)
    repository = RideRepository(
      db.rideDao(),
      db.expenseDao(),
      db.settingsDao(),
      db.cityDao(),
      db.riderAccountDao(),
      db.adminDao()
    )

    viewModelScope.launch {
      firestoreRepository.seedInitialDataIfEmpty()
    }
  }

  // Active User Role: "NONE", "RIDER", "PASSENGER", "ADMIN"
  private val _userRole = MutableStateFlow("NONE")
  val userRole = _userRole.asStateFlow()

  fun setUserRole(role: String) {
    _userRole.value = role
  }

  fun resetRoleToSelector() {
    _userRole.value = "NONE"
  }

  // Passenger Profile Protocol State
  private val _currentPassenger = MutableStateFlow(PassengerProfile())
  val currentPassenger = _currentPassenger.asStateFlow()

  private val _isPassengerLoggedIn = MutableStateFlow(false)
  val isPassengerLoggedIn = _isPassengerLoggedIn.asStateFlow()

  fun savePassengerProfile(name: String, phone: String, cityId: String) {
    _currentPassenger.value = PassengerProfile(
      name = name.trim(),
      phone = phone.trim(),
      cityId = cityId.trim().ifBlank { "multan" },
      isProfileComplete = true
    )
    _isPassengerLoggedIn.value = true
    _userRole.value = "PASSENGER"
  }

  fun logoutPassenger() {
    _isPassengerLoggedIn.value = false
    _userRole.value = "NONE"
  }

  // Admin Profile Protocol State (Default: shahid.msc2020@gmail.com / Khan@3636)
  private val _isAdminLoggedIn = MutableStateFlow(false)
  val isAdminLoggedIn = _isAdminLoggedIn.asStateFlow()

  val adminDefaultEmail = "shahid.msc2020@gmail.com"
  val adminDefaultPassword = "Khan@3636"

  fun loginAdmin(email: String, pass: String): Boolean {
    val cleanEmail = email.trim()
    val cleanPass = pass.trim()
    val config = adminConfig.value
    val validEmail = config.adminEmail.ifBlank { adminDefaultEmail }
    val validPass = config.adminPassword.ifBlank { adminDefaultPassword }
    if (cleanEmail.equals(validEmail, ignoreCase = true) && cleanPass == validPass) {
      _isAdminLoggedIn.value = true
      _userRole.value = "ADMIN"
      return true
    }
    return false
  }

  fun logoutAdmin() {
    _isAdminLoggedIn.value = false
    _userRole.value = "NONE"
  }

  // Rider Profile Protocol State
  private val _isRiderLoggedIn = MutableStateFlow(false)
  val isRiderLoggedIn = _isRiderLoggedIn.asStateFlow()

  fun logoutRider() {
    _isRiderLoggedIn.value = false
    _activeRiderId.value = ""
    _userRole.value = "NONE"
  }

  // App Settings
  val settings: StateFlow<AppSettings> = repository.settings
    .combine(MutableStateFlow(AppSettings())) { stored, defaultSettings ->
      stored ?: defaultSettings
    }
    .stateIn(
      scope = viewModelScope,
      started = SharingStarted.WhileSubscribed(5000),
      initialValue = AppSettings()
    )

  // Custom Cities & Destinations
  val customCities: StateFlow<List<CustomCity>> = repository.customCities
    .stateIn(
      scope = viewModelScope,
      started = SharingStarted.WhileSubscribed(5000),
      initialValue = emptyList()
    )

  val allCustomDestinations: StateFlow<List<CityDestination>> = repository.allDestinations
    .stateIn(
      scope = viewModelScope,
      started = SharingStarted.WhileSubscribed(5000),
      initialValue = emptyList()
    )

  // Service Cities (Built-in + Custom Driver Cities)
  val serviceCities: StateFlow<List<CityInfo>> = customCities.map { customList ->
    PakistanCitiesData.getAllCities(customList)
  }.stateIn(
    scope = viewModelScope,
    started = SharingStarted.WhileSubscribed(5000),
    initialValue = PakistanCitiesData.cities
  )

  // Dynamic City Info derived from active settings & custom cities
  val currentCity: StateFlow<CityInfo> = combine(settings, customCities) { s, customs ->
    PakistanCitiesData.getCityById(s.selectedCityId, customs)
  }.stateIn(
    scope = viewModelScope,
    started = SharingStarted.WhileSubscribed(5000),
    initialValue = PakistanCitiesData.getCityById("multan")
  )

  // All locations for current city
  val currentCityLocations: StateFlow<List<LocationPoint>> = combine(
    currentCity,
    allCustomDestinations
  ) { city, customDests ->
    PakistanCitiesData.getCombinedCityLocations(city, customDests)
  }.stateIn(
    scope = viewModelScope,
    started = SharingStarted.WhileSubscribed(5000),
    initialValue = PakistanCitiesData.getCityById("multan").locations
  )

  // Dynamic App Title: "Sasti Sawari <City>"
  val appTitleWithCity: StateFlow<String> = currentCity.map { city ->
    "Sasti Sawari ${city.name}"
  }.stateIn(
    scope = viewModelScope,
    started = SharingStarted.WhileSubscribed(5000),
    initialValue = "Sasti Sawari Multan"
  )

  // Business Data
  val allRides: StateFlow<List<Ride>> = repository.allRides
    .stateIn(
      scope = viewModelScope,
      started = SharingStarted.WhileSubscribed(5000),
      initialValue = emptyList()
    )

  val allExpenses: StateFlow<List<Expense>> = repository.allExpenses
    .stateIn(
      scope = viewModelScope,
      started = SharingStarted.WhileSubscribed(5000),
      initialValue = emptyList()
    )

  val customers: StateFlow<List<CustomerSummary>> = repository.customers
    .stateIn(
      scope = viewModelScope,
      started = SharingStarted.WhileSubscribed(5000),
      initialValue = emptyList()
    )

  // Admin Config
  val adminConfig: StateFlow<AdminConfig> = repository.adminConfig
    .map { it ?: AdminConfig() }
    .stateIn(
      scope = viewModelScope,
      started = SharingStarted.WhileSubscribed(5000),
      initialValue = AdminConfig()
    )

  // All Registered Riders for Admin and Passenger Discovery
  val allRiders: StateFlow<List<RiderAccount>> = repository.allRiders
    .stateIn(
      scope = viewModelScope,
      started = SharingStarted.WhileSubscribed(5000),
      initialValue = emptyList()
    )

  // Active Logged-in Rider Account
  private val _activeRiderId = MutableStateFlow("")
  val currentRider: StateFlow<RiderAccount?> = combine(allRiders, _activeRiderId) { riders, activeId ->
    if (activeId.isNotBlank()) {
      riders.firstOrNull { it.riderId == activeId }
    } else {
      null
    }
  }.stateIn(
    scope = viewModelScope,
    started = SharingStarted.WhileSubscribed(5000),
    initialValue = null
  )

  // Subscription Locking Logic (Auto Lock after 30 days or Test Simulation)
  val isSubscriptionLocked: StateFlow<Boolean> = combine(currentRider, adminConfig) { rider, config ->
    if (config.isTestModeSimulateExpired) {
      true
    } else if (rider != null) {
      val now = System.currentTimeMillis()
      rider.subscriptionStatus != "ACTIVE" || now > rider.expiresAt
    } else {
      false
    }
  }.stateIn(
    scope = viewModelScope,
    started = SharingStarted.WhileSubscribed(5000),
    initialValue = false
  )

  // Interactive Fare Calculator State
  private val _calcDistance = MutableStateFlow("3.0")
  val calcDistance = _calcDistance.asStateFlow()

  private val _calcWaitMinutes = MutableStateFlow("0")
  val calcWaitMinutes = _calcWaitMinutes.asStateFlow()

  // Selected Geo Points in Calculator
  private val _calcPickupLocation = MutableStateFlow<LocationPoint?>(null)
  val calcPickupLocation = _calcPickupLocation.asStateFlow()

  private val _calcDropLocation = MutableStateFlow<LocationPoint?>(null)
  val calcDropLocation = _calcDropLocation.asStateFlow()

  fun setCalcDistance(value: String) {
    _calcDistance.value = value
  }

  fun setCalcWaitMinutes(value: String) {
    _calcWaitMinutes.value = value
  }

  fun selectCalcPickupLocation(point: LocationPoint) {
    _calcPickupLocation.value = point
    recalculateDistanceIfPossible()
  }

  fun selectCalcDropLocation(point: LocationPoint) {
    _calcDropLocation.value = point
    recalculateDistanceIfPossible()
  }

  private fun recalculateDistanceIfPossible() {
    val pickup = _calcPickupLocation.value
    val drop = _calcDropLocation.value
    if (pickup != null && drop != null) {
      val dist = PakistanCitiesData.calculateAccurateDistance(pickup, drop)
      _calcDistance.value = dist.toString()
    } else if (drop != null && drop.approxDistanceKmFromStand > 0) {
      _calcDistance.value = drop.approxDistanceKmFromStand.toString()
    }
  }

  // Pre-filled values for New Ride when transferring from Calculator
  private val _prefillDistance = MutableStateFlow<Double?>(null)
  val prefillDistance = _prefillDistance.asStateFlow()

  private val _prefillWait = MutableStateFlow<Int?>(null)
  val prefillWait = _prefillWait.asStateFlow()

  private val _prefillDestination = MutableStateFlow<String?>(null)
  val prefillDestination = _prefillDestination.asStateFlow()

  fun transferCalcToNewRide(distance: Double, wait: Int, destination: String? = null) {
    _prefillDistance.value = distance
    _prefillWait.value = wait
    _prefillDestination.value = destination
  }

  fun clearPrefill() {
    _prefillDistance.value = null
    _prefillWait.value = null
    _prefillDestination.value = null
  }

  fun calculateFare(distance: Double, waitingMinutes: Int): FareBreakdown {
    val s = settings.value
    return FareCalculator.calculate(
      distanceKm = distance,
      waitingMinutes = waitingMinutes,
      baseFare = s.baseFare,
      baseKm = s.baseKm,
      ratePerKm = s.ratePerAdditionalKm,
      freeWaitMinutes = s.freeWaitingMinutes,
      ratePerWaitMinute = s.waitingRatePerMinute
    )
  }

  fun changeCity(cityId: String) {
    val city = PakistanCitiesData.getCityById(cityId, customCities.value)
    val updated = settings.value.copy(
      selectedCityId = city.id,
      standName = city.defaultStandName,
      standAddress = city.defaultStandAddress
    )
    saveSettings(updated)
    _calcPickupLocation.value = city.locations.firstOrNull { it.category == "Stand" } ?: city.locations.firstOrNull()
    _calcDropLocation.value = null
    _calcDistance.value = "3.0"
  }

  // Custom Pickup State when driver is on another location
  private val _isCustomPickup = MutableStateFlow(false)
  val isCustomPickup = _isCustomPickup.asStateFlow()

  private val _customPickupText = MutableStateFlow("")
  val customPickupText = _customPickupText.asStateFlow()

  private val _customDropText = MutableStateFlow("")
  val customDropText = _customDropText.asStateFlow()

  fun setIsCustomPickup(custom: Boolean) {
    _isCustomPickup.value = custom
    if (!custom) {
      val standLoc = currentCityLocations.value.firstOrNull { it.category == "Stand" }
        ?: currentCityLocations.value.firstOrNull()
      _calcPickupLocation.value = standLoc
      recalculateDistanceIfPossible()
    }
  }

  fun setCustomPickupText(text: String) {
    _customPickupText.value = text
  }

  fun setCustomDropText(text: String) {
    _customDropText.value = text
  }

  fun addCustomDestination(
    cityId: String,
    name: String,
    urduName: String = "",
    category: String = "Popular",
    approxDistanceKm: Double = 0.0,
    lat: Double = 0.0,
    lng: Double = 0.0
  ) {
    viewModelScope.launch {
      repository.insertDestination(
        CityDestination(
          cityId = cityId,
          name = name.trim(),
          urduName = urduName.trim(),
          category = category.trim().ifBlank { "Popular" },
          approxDistanceKm = approxDistanceKm,
          lat = lat,
          lng = lng,
          isCustom = true
        )
      )
    }
  }

  fun deleteCustomDestination(destination: CityDestination) {
    viewModelScope.launch {
      repository.deleteDestination(destination)
    }
  }

  fun addCustomServiceCity(
    name: String,
    urduName: String,
    tagLine: String,
    hallmarkTitle: String,
    hallmarkUrdu: String,
    standName: String,
    standAddress: String
  ) {
    viewModelScope.launch {
      val id = name.trim().lowercase().replace(Regex("[^a-z0-9]"), "_")
      val city = CustomCity(
        id = id,
        name = name.trim(),
        urduName = urduName.trim().ifBlank { name.trim() },
        tagLine = tagLine.trim().ifBlank { "Service City" },
        hallmarkTitle = hallmarkTitle.trim().ifBlank { "City Center" },
        hallmarkUrdu = hallmarkUrdu.trim(),
        hallmarkDescription = "Sasti Sawari bike ride service in ${name.trim()}.",
        defaultStandName = standName.trim().ifBlank { "Main Adda Stand" },
        defaultStandAddress = standAddress.trim().ifBlank { "Main Adda Chowk, ${name.trim()}" },
        centerLat = currentCity.value.centerLat,
        centerLng = currentCity.value.centerLng
      )
      repository.insertCustomCity(city)
      changeCity(city.id)
    }
  }

  fun deleteCustomServiceCity(city: CustomCity) {
    viewModelScope.launch {
      repository.deleteCustomCity(city)
      if (settings.value.selectedCityId == city.id) {
        changeCity("multan")
      }
    }
  }

  fun updateDriverProfile(
    photoUri: String,
    cityId: String,
    name: String,
    phone: String,
    whatsapp: String,
    bikeModel: String,
    plate: String,
    tagline: String,
    standName: String,
    standAddress: String
  ) {
    val city = PakistanCitiesData.getCityById(cityId, customCities.value)
    val updated = settings.value.copy(
      riderPhotoUri = photoUri.trim(),
      selectedCityId = city.id,
      operatorName = name.trim(),
      operatorPhone = phone.trim(),
      operatorWhatsApp = whatsapp.trim(),
      motorcycleModel = bikeModel.trim(),
      motorcyclePlate = plate.trim(),
      riderTagline = tagline.trim(),
      standName = standName.trim().ifBlank { city.defaultStandName },
      standAddress = standAddress.trim().ifBlank { city.defaultStandAddress }
    )
    saveSettings(updated)

    // Also sync to active rider account in database
    currentRider.value?.let { active ->
      val updatedRider = active.copy(
        photoUri = photoUri.trim(),
        name = name.trim(),
        phone = phone.trim(),
        cityId = city.id,
        bikeModel = bikeModel.trim(),
        plate = plate.trim(),
        tagline = tagline.trim(),
        standName = standName.trim().ifBlank { city.defaultStandName },
        standAddress = standAddress.trim().ifBlank { city.defaultStandAddress }
      )
      viewModelScope.launch {
        repository.saveRider(updatedRider)
      }
    }
  }

  fun updateRiderPhotoUri(uriString: String) {
    val updated = settings.value.copy(riderPhotoUri = uriString)
    saveSettings(updated)
    currentRider.value?.let { active ->
      viewModelScope.launch {
        repository.saveRider(active.copy(photoUri = uriString))
      }
    }
  }

  fun updateRiderBranding(
    name: String,
    phone: String,
    whatsapp: String,
    bikeModel: String,
    plate: String,
    tagline: String,
    standName: String,
    standAddress: String
  ) {
    val updated = settings.value.copy(
      operatorName = name.trim(),
      operatorPhone = phone.trim(),
      operatorWhatsApp = whatsapp.trim(),
      motorcycleModel = bikeModel.trim(),
      motorcyclePlate = plate.trim(),
      riderTagline = tagline.trim(),
      standName = standName.trim(),
      standAddress = standAddress.trim()
    )
    saveSettings(updated)
  }

  // Statistics & Financials
  fun computeStatsForRange(startTime: Long, endTime: Long): PeriodStats {
    val rides = allRides.value.filter { it.timestamp in startTime..endTime }
    val expenses = allExpenses.value.filter { it.timestamp in startTime..endTime }
    val s = settings.value

    val count = rides.size
    val revenue = rides.sumOf { it.totalFare }
    val km = rides.sumOf { it.distanceKm }
    val avgFare = if (count > 0) revenue / count else 0.0
    val waiting = rides.sumOf { it.waitingFare }
    val estFuel = if (s.bikeFuelEconomyKmPerLitre > 0) {
      (km / s.bikeFuelEconomyKmPerLitre) * s.fuelPricePerLitre
    } else 0.0
    val recordedExp = expenses.sumOf { it.amount }
    val net = revenue - estFuel - recordedExp

    return PeriodStats(
      rideCount = count,
      totalRevenue = revenue,
      totalKm = km,
      averageFare = avgFare,
      totalWaitingCharges = waiting,
      estimatedFuelCost = estFuel,
      recordedExpenses = recordedExp,
      netIncome = net
    )
  }

  fun getTodayStats(): PeriodStats {
    val cal = Calendar.getInstance()
    cal.set(Calendar.HOUR_OF_DAY, 0)
    cal.set(Calendar.MINUTE, 0)
    cal.set(Calendar.SECOND, 0)
    cal.set(Calendar.MILLISECOND, 0)
    val startOfDay = cal.timeInMillis

    cal.set(Calendar.HOUR_OF_DAY, 23)
    cal.set(Calendar.MINUTE, 59)
    cal.set(Calendar.SECOND, 59)
    val endOfDay = cal.timeInMillis

    return computeStatsForRange(startOfDay, endOfDay)
  }

  fun getWeeklyStats(): PeriodStats {
    val cal = Calendar.getInstance()
    cal.set(Calendar.DAY_OF_WEEK, cal.firstDayOfWeek)
    cal.set(Calendar.HOUR_OF_DAY, 0)
    cal.set(Calendar.MINUTE, 0)
    cal.set(Calendar.SECOND, 0)
    val startOfWeek = cal.timeInMillis
    val now = System.currentTimeMillis()

    return computeStatsForRange(startOfWeek, now)
  }

  fun getMonthlyStats(): PeriodStats {
    val cal = Calendar.getInstance()
    cal.set(Calendar.DAY_OF_MONTH, 1)
    cal.set(Calendar.HOUR_OF_DAY, 0)
    cal.set(Calendar.MINUTE, 0)
    cal.set(Calendar.SECOND, 0)
    val startOfMonth = cal.timeInMillis
    val now = System.currentTimeMillis()

    return computeStatsForRange(startOfMonth, now)
  }

  fun getTopDestinations(limit: Int = 6): List<DestinationCount> {
    return allRides.value
      .groupBy { it.destination.trim() }
      .filter { it.key.isNotBlank() }
      .map { (dest, list) ->
        DestinationCount(
          destination = dest,
          count = list.size,
          totalRevenue = list.sumOf { it.totalFare }
        )
      }
      .sortedByDescending { it.count }
      .take(limit)
  }

  fun generateNextRideCode(): String {
    val cityCode = currentCity.value.name.take(3).uppercase()
    val count = allRides.value.size
    return "$cityCode-${1001 + count}"
  }

  fun addRide(
    destination: String,
    distanceKm: Double,
    waitingMinutes: Int,
    customerName: String,
    customerPhone: String,
    pickupLocation: String,
    paymentMethod: String,
    notes: String,
    fareOverride: Double? = null,
    onSuccess: () -> Unit = {}
  ) {
    viewModelScope.launch {
      val breakdown = calculateFare(distanceKm, waitingMinutes)
      val finalFare = fareOverride ?: breakdown.totalFare
      val ride = Ride(
        rideCode = generateNextRideCode(),
        customerName = customerName.trim(),
        customerPhone = customerPhone.trim(),
        pickupLocation = pickupLocation.ifBlank { settings.value.standName },
        destination = destination.trim(),
        distanceKm = distanceKm,
        waitingMinutes = waitingMinutes,
        baseFare = breakdown.baseFare,
        distanceFare = breakdown.distanceFare,
        waitingFare = breakdown.waitingFare,
        totalFare = finalFare,
        paymentMethod = paymentMethod,
        timestamp = System.currentTimeMillis(),
        notes = notes.trim()
      )
      repository.insertRide(ride)
      onSuccess()
    }
  }

  fun addExpense(
    category: String,
    amount: Double,
    description: String,
    onSuccess: () -> Unit = {}
  ) {
    viewModelScope.launch {
      val expense = Expense(
        category = category,
        amount = amount,
        description = description.trim(),
        timestamp = System.currentTimeMillis()
      )
      repository.insertExpense(expense)
      onSuccess()
    }
  }

  fun deleteExpense(expense: Expense) {
    viewModelScope.launch {
      repository.deleteExpense(expense)
    }
  }

  fun saveSettings(updatedSettings: AppSettings) {
    viewModelScope.launch {
      repository.saveSettings(updatedSettings)
    }
  }

  fun getBusinessReportCsv(): String =
    repository.exportBusinessReportCsv(allRides.value, allExpenses.value, settings.value)

  // Voice Announcement Functions
  fun playRideStartVoice(destination: String = "", stand: String = "") {
    if (adminConfig.value.isVoiceAnnouncementsEnabled) {
      VoiceService.playRideStartVoice(destination, stand)
    }
  }

  fun playRideCompleteVoice(fareAmount: Int) {
    if (adminConfig.value.isVoiceAnnouncementsEnabled) {
      VoiceService.playRideCompleteVoice(fareAmount)
    }
  }

  // Rider Subscription & Payment Management
  fun submitRiderPayment(
    txnId: String,
    paymentMethod: String,
    proofUri: String = "",
    onResult: (Boolean, String) -> Unit
  ) {
    val active = currentRider.value ?: return onResult(false, "No active rider account found")
    viewModelScope.launch {
      val updated = active.copy(
        lastPaymentTxnId = txnId.trim(),
        lastPaymentMethod = paymentMethod,
        lastPaymentProofUri = proofUri.trim(),
        paymentSubmittedAt = System.currentTimeMillis(),
        subscriptionStatus = "PENDING"
      )
      repository.saveRider(updated)

      // Also persist to Cloud Firestore for Admin review
      val firestorePayment = FirestorePayment(
        paymentId = "PAY-${System.currentTimeMillis()}",
        riderId = active.riderId,
        userId = "uid-${active.riderId}",
        riderName = active.name,
        riderEmail = active.email.ifBlank { "${active.name.lowercase().replace(" ", ".")}@sastisawari.pk" },
        riderPhone = active.phone,
        riderCity = active.cityId,
        riderStand = active.standName,
        amount = 100.0,
        transactionId = txnId.trim(),
        paymentMethod = paymentMethod,
        proofUri = proofUri.trim(),
        status = "PENDING",
        submittedAt = System.currentTimeMillis(),
        notes = "Submitted from Rider app for 30-day access"
      )
      firestoreRepository.submitPayment(firestorePayment)

      onResult(true, "Payment submitted! Awaiting Admin verification.")
    }
  }

  fun registerRider(
    name: String,
    phone: String,
    cityId: String,
    password: String,
    bikeModel: String = "Honda CD 70",
    plate: String = "MN-1234",
    standName: String = "",
    standAddress: String = "",
    email: String = "",
    onResult: (Boolean, String, String) -> Unit
  ) {
    viewModelScope.launch {
      val existing = repository.getRiderByPhone(phone.trim())
      if (existing != null) {
        onResult(false, "Phone already registered with Rider ID ${existing.riderId}", "")
        return@launch
      }

      val riderCount = allRiders.value.size
      val newRiderId = "SW-${1001 + riderCount}"
      val city = PakistanCitiesData.getCityById(cityId, customCities.value)
      val userEmail = email.trim().ifBlank { "${name.trim().lowercase().replace(" ", ".")}@sastisawari.pk" }
      val newRider = RiderAccount(
        riderId = newRiderId,
        name = name.trim(),
        email = userEmail,
        phone = phone.trim(),
        cityId = city.id,
        password = password.trim(),
        bikeModel = bikeModel.trim().ifBlank { "Honda CD 70" },
        plate = plate.trim().ifBlank { "PKR-70" },
        standName = standName.trim().ifBlank { city.defaultStandName },
        standAddress = standAddress.trim().ifBlank { city.defaultStandAddress },
        tagline = "Direct Stand Pickup • Sasti Sawari",
        subscriptionStatus = "PENDING", // Must pay Rs. 100 and be approved by admin
        activatedAt = 0L,
        expiresAt = 0L,
        isOnline = true
      )

      repository.saveRider(newRider)
      _activeRiderId.value = newRiderId
      _isRiderLoggedIn.value = true
      _userRole.value = "RIDER"

      // Sync settings
      val updatedSettings = settings.value.copy(
        operatorName = newRider.name,
        operatorPhone = newRider.phone,
        operatorWhatsApp = newRider.phone.replace("-", "").replace(" ", ""),
        selectedCityId = newRider.cityId,
        motorcycleModel = newRider.bikeModel,
        motorcyclePlate = newRider.plate,
        standName = newRider.standName,
        standAddress = newRider.standAddress
      )
      repository.saveSettings(updatedSettings)

      onResult(true, "Registered successfully! Your Rider ID is $newRiderId.", newRiderId)
    }
  }

  fun loginRider(
    phone: String,
    password: String,
    onResult: (Boolean, String) -> Unit
  ) {
    viewModelScope.launch {
      val rider = repository.getRiderByPhone(phone.trim())
      if (rider == null) {
        onResult(false, "No rider registered with phone $phone")
      } else if (rider.password != password.trim()) {
        onResult(false, "Incorrect password. Please try again.")
      } else {
        _activeRiderId.value = rider.riderId
        _isRiderLoggedIn.value = true
        _userRole.value = "RIDER"
        // Update settings
        val updatedSettings = settings.value.copy(
          operatorName = rider.name,
          operatorPhone = rider.phone,
          selectedCityId = rider.cityId,
          motorcycleModel = rider.bikeModel,
          motorcyclePlate = rider.plate,
          standName = rider.standName,
          standAddress = rider.standAddress
        )
        repository.saveSettings(updatedSettings)
        onResult(true, "Welcome back, ${rider.name}!")
      }
    }
  }

  fun switchActiveRider(riderId: String) {
    _activeRiderId.value = riderId
    _isRiderLoggedIn.value = true
    _userRole.value = "RIDER"
    val rider = allRiders.value.firstOrNull { it.riderId == riderId }
    if (rider != null) {
      val updatedSettings = settings.value.copy(
        operatorName = rider.name,
        operatorPhone = rider.phone,
        selectedCityId = rider.cityId,
        motorcycleModel = rider.bikeModel,
        motorcyclePlate = rider.plate,
        standName = rider.standName,
        standAddress = rider.standAddress
      )
      saveSettings(updatedSettings)
    }
  }

  // Admin Actions
  fun approveRiderPayment(riderId: String, durationDays: Int = 30) {
    viewModelScope.launch {
      val rider = allRiders.value.firstOrNull { it.riderId == riderId } ?: return@launch
      val now = System.currentTimeMillis()
      // If currently active and extending, add from current expiry, otherwise from now
      val baseTime = if (rider.subscriptionStatus == "ACTIVE" && rider.expiresAt > now) {
        rider.expiresAt
      } else {
        now
      }
      val newExpiry = baseTime + (durationDays.toLong() * 24 * 60 * 60 * 1000)

      val updated = rider.copy(
        subscriptionStatus = "ACTIVE",
        activatedAt = now,
        expiresAt = newExpiry
      )
      repository.saveRider(updated)
    }
  }

  fun rejectRiderPayment(riderId: String) {
    viewModelScope.launch {
      val rider = allRiders.value.firstOrNull { it.riderId == riderId } ?: return@launch
      val updated = rider.copy(
        subscriptionStatus = "EXPIRED"
      )
      repository.saveRider(updated)
    }
  }

  // Real-time Firestore Admin Actions
  fun approveFirestorePayment(payment: FirestorePayment, durationDays: Int = 30, onComplete: ((Boolean) -> Unit)? = null) {
    viewModelScope.launch {
      val success = firestoreRepository.approvePayment(payment.paymentId, payment.riderId, durationDays)
      // Also update local Room database so UI and offline cache update synchronously
      approveRiderPayment(payment.riderId, durationDays)
      onComplete?.invoke(success)
    }
  }

  fun rejectFirestorePayment(payment: FirestorePayment, reason: String = "", onComplete: ((Boolean) -> Unit)? = null) {
    viewModelScope.launch {
      val success = firestoreRepository.rejectPayment(payment.paymentId, payment.riderId, reason)
      rejectRiderPayment(payment.riderId)
      onComplete?.invoke(success)
    }
  }

  fun renewFirestoreRider(riderId: String, durationDays: Int = 30, onComplete: ((Boolean) -> Unit)? = null) {
    viewModelScope.launch {
      val success = firestoreRepository.renewRiderSubscription(riderId, durationDays)
      approveRiderPayment(riderId, durationDays)
      onComplete?.invoke(success)
    }
  }

  fun lockFirestoreRider(riderId: String, onComplete: ((Boolean) -> Unit)? = null) {
    viewModelScope.launch {
      val success = firestoreRepository.lockRider(riderId)
      toggleRiderAccountStatus(riderId, "EXPIRED")
      onComplete?.invoke(success)
    }
  }

  fun toggleRiderAccountStatus(riderId: String, newStatus: String) {
    viewModelScope.launch {
      val rider = allRiders.value.firstOrNull { it.riderId == riderId } ?: return@launch
      repository.saveRider(rider.copy(subscriptionStatus = newStatus))
    }
  }

  fun toggleSimulateExpired(simulate: Boolean) {
    viewModelScope.launch {
      val current = adminConfig.value
      val updated = current.copy(isTestModeSimulateExpired = simulate)
      repository.saveAdminConfig(updated)
    }
  }

  fun updateAdminConfig(updated: AdminConfig) {
    viewModelScope.launch {
      repository.saveAdminConfig(updated)
    }
  }

  fun runSubscriptionCycleCheck(): Int {
    val now = System.currentTimeMillis()
    var lockedCount = 0
    allRiders.value.forEach { rider ->
      if (rider.subscriptionStatus == "ACTIVE" && rider.expiresAt < now) {
        viewModelScope.launch {
          repository.saveRider(rider.copy(subscriptionStatus = "EXPIRED", isOnline = false))
        }
        lockedCount++
      }
    }
    return lockedCount
  }
}

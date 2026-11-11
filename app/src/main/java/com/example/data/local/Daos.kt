package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.AdminConfig
import com.example.data.model.AppSettings
import com.example.data.model.CityDestination
import com.example.data.model.CustomCity
import com.example.data.model.Expense
import com.example.data.model.Ride
import com.example.data.model.RiderAccount
import kotlinx.coroutines.flow.Flow

@Dao
interface RideDao {
  @Query("SELECT * FROM rides ORDER BY timestamp DESC")
  fun getAllRides(): Flow<List<Ride>>

  @Query("SELECT * FROM rides WHERE timestamp >= :startTime AND timestamp <= :endTime ORDER BY timestamp DESC")
  fun getRidesBetween(startTime: Long, endTime: Long): Flow<List<Ride>>

  @Query("SELECT * FROM rides WHERE id = :id LIMIT 1")
  fun getRideById(id: Long): Flow<Ride?>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertRide(ride: Ride): Long

  @Update
  suspend fun updateRide(ride: Ride)

  @Delete
  suspend fun deleteRide(ride: Ride)

  @Query("DELETE FROM rides WHERE id = :id")
  suspend fun deleteRideById(id: Long)

  @Query("SELECT COUNT(*) FROM rides")
  suspend fun getRidesCount(): Int
}

@Dao
interface ExpenseDao {
  @Query("SELECT * FROM expenses ORDER BY timestamp DESC")
  fun getAllExpenses(): Flow<List<Expense>>

  @Query("SELECT * FROM expenses WHERE timestamp >= :startTime AND timestamp <= :endTime ORDER BY timestamp DESC")
  fun getExpensesBetween(startTime: Long, endTime: Long): Flow<List<Expense>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertExpense(expense: Expense): Long

  @Delete
  suspend fun deleteExpense(expense: Expense)
}

@Dao
interface SettingsDao {
  @Query("SELECT * FROM app_settings WHERE id = 1 LIMIT 1")
  fun getSettings(): Flow<AppSettings?>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun saveSettings(settings: AppSettings)
}

@Dao
interface CityDao {
  @Query("SELECT * FROM city_destinations ORDER BY name ASC")
  fun getAllDestinations(): Flow<List<CityDestination>>

  @Query("SELECT * FROM city_destinations WHERE cityId = :cityId ORDER BY name ASC")
  fun getDestinationsForCity(cityId: String): Flow<List<CityDestination>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertDestination(destination: CityDestination): Long

  @Delete
  suspend fun deleteDestination(destination: CityDestination)

  @Query("DELETE FROM city_destinations WHERE id = :id")
  suspend fun deleteDestinationById(id: Long)

  @Query("SELECT * FROM custom_cities WHERE isEnabled = 1 ORDER BY name ASC")
  fun getAllCustomCities(): Flow<List<CustomCity>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertCustomCity(city: CustomCity)

  @Delete
  suspend fun deleteCustomCity(city: CustomCity)
}

@Dao
interface RiderAccountDao {
  @Query("SELECT * FROM rider_accounts ORDER BY name ASC")
  fun getAllRiders(): Flow<List<RiderAccount>>

  @Query("SELECT * FROM rider_accounts WHERE riderId = :riderId LIMIT 1")
  fun getRiderById(riderId: String): Flow<RiderAccount?>

  @Query("SELECT * FROM rider_accounts WHERE phone = :phone LIMIT 1")
  suspend fun getRiderByPhone(phone: String): RiderAccount?

  @Query("SELECT * FROM rider_accounts WHERE cityId = :cityId AND isOnline = 1 AND subscriptionStatus = 'ACTIVE'")
  fun getActiveRidersInCity(cityId: String): Flow<List<RiderAccount>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertOrUpdateRider(rider: RiderAccount)

  @Delete
  suspend fun deleteRider(rider: RiderAccount)
}

@Dao
interface AdminDao {
  @Query("SELECT * FROM admin_config WHERE id = 1 LIMIT 1")
  fun getAdminConfig(): Flow<AdminConfig?>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun saveAdminConfig(config: AdminConfig)
}



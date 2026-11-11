package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.model.AdminConfig
import com.example.data.model.AppSettings
import com.example.data.model.CityDestination
import com.example.data.model.CustomCity
import com.example.data.model.Expense
import com.example.data.model.Ride
import com.example.data.model.RiderAccount
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Calendar

@Database(
  entities = [Ride::class, Expense::class, AppSettings::class, CityDestination::class, CustomCity::class, RiderAccount::class, AdminConfig::class],
  version = 6,
  exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
  abstract fun rideDao(): RideDao
  abstract fun expenseDao(): ExpenseDao
  abstract fun settingsDao(): SettingsDao
  abstract fun cityDao(): CityDao
  abstract fun riderAccountDao(): RiderAccountDao
  abstract fun adminDao(): AdminDao

  companion object {
    @Volatile
    private var INSTANCE: AppDatabase? = null

    fun getDatabase(context: Context, scope: CoroutineScope): AppDatabase {
      return INSTANCE ?: synchronized(this) {
        val instance = Room.databaseBuilder(
          context.applicationContext,
          AppDatabase::class.java,
          "sasti_sawari_db"
        )
          .fallbackToDestructiveMigration()
          .addCallback(DatabaseCallback(scope))
          .build()
        INSTANCE = instance
        instance
      }
    }

    private class DatabaseCallback(
      private val scope: CoroutineScope
    ) : RoomDatabase.Callback() {
      override fun onCreate(db: SupportSQLiteDatabase) {
        super.onCreate(db)
        INSTANCE?.let { database ->
          scope.launch(Dispatchers.IO) {
            populateInitialData(database)
          }
        }
      }

      suspend fun populateInitialData(database: AppDatabase) {
        val settingsDao = database.settingsDao()
        val adminDao = database.adminDao()

        // Insert Default Settings & Admin Config without dummy data
        settingsDao.saveSettings(AppSettings())
        adminDao.saveAdminConfig(
          AdminConfig(
            adminEmail = "shahid.msc2020@gmail.com",
            adminPassword = "Khan@3636"
          )
        )
      }
    }
  }
}

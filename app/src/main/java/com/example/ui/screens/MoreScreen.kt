package com.example.ui.screens

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DirectionsBike
import androidx.compose.material.icons.filled.LocalGasStation
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Message
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.AppSettings
import com.example.data.model.Expense
import com.example.data.repository.CustomerSummary
import com.example.ui.MainViewModel
import com.example.ui.components.shareCsv
import com.example.ui.theme.CashGreen
import com.example.ui.theme.ExpenseRed
import com.example.ui.theme.ImperialGold
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

enum class MenuSection(val title: String, val subtitle: String, val icon: ImageVector) {
  DRIVER_PROFILE("Driver Profile & Photo", "Customize your photo, bike, stand & contacts", Icons.Default.Person),
  SUBSCRIPTION("Subscription (Rs. 100)", "30-day access, renewal & payment verification", Icons.Default.CreditCard),
  DESTINATIONS("Service Cities & Drop-offs", "Manage common pickup & drop locations", Icons.Default.LocationOn),
  CUSTOMERS("Customer Directory", "Frequent passengers with 1-tap call & WhatsApp", Icons.Default.People),
  EXPENSES("Fuel & Daily Expenses", "Track petrol pump receipts and maintenance", Icons.Default.LocalGasStation),
  FARE_RULES("Fare & Fuel Rules", "Base fare, km rate, waiting rate & fuel mileage", Icons.Default.Tune)
}

@Composable
fun MoreScreen(
  viewModel: MainViewModel,
  snackbarHostState: SnackbarHostState,
  modifier: Modifier = Modifier,
  initialTab: Int = 0
) {
  val context = LocalContext.current
  val settings by viewModel.settings.collectAsStateWithLifecycle()
  val customers by viewModel.customers.collectAsStateWithLifecycle()
  val expenses by viewModel.allExpenses.collectAsStateWithLifecycle()
  val allRides by viewModel.allRides.collectAsStateWithLifecycle()
  val currentRider by viewModel.currentRider.collectAsStateWithLifecycle()
  val currentCity by viewModel.currentCity.collectAsStateWithLifecycle()

  // Track active sub-section. If null, show the clean Menu Hub
  var activeSection by remember {
    mutableStateOf(
      when (initialTab) {
        1 -> MenuSection.SUBSCRIPTION
        2 -> MenuSection.DESTINATIONS
        3 -> MenuSection.CUSTOMERS
        4 -> MenuSection.EXPENSES
        5 -> MenuSection.FARE_RULES
        else -> null
      }
    )
  }

  // Handle hardware / gesture back to return from sub-section to Menu Hub
  BackHandler(enabled = activeSection != null) {
    activeSection = null
  }

  if (activeSection == null) {
    // Clean, Non-Duplicative Menu Hub
    LazyColumn(
      modifier = modifier
        .fillMaxSize()
        .padding(horizontal = 16.dp),
      verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
      item {
        Spacer(modifier = Modifier.height(6.dp))
        // Rider Header Summary
        Card(
          shape = RoundedCornerShape(20.dp),
          colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
          modifier = Modifier.fillMaxWidth()
        ) {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(18.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Column(modifier = Modifier.weight(1f)) {
              Text(
                text = "RIDER BUSINESS HUB",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                letterSpacing = 1.sp
              )
              Text(
                text = settings.operatorName,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.onPrimaryContainer
              )
              Text(
                text = "${currentCity.name} • ${settings.motorcycleModel} (${settings.motorcyclePlate})",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.85f)
              )
            }

            Surface(
              shape = CircleShape,
              color = MaterialTheme.colorScheme.primary,
              modifier = Modifier.size(46.dp)
            ) {
              Box(contentAlignment = Alignment.Center) {
                Icon(
                  imageVector = Icons.Default.DirectionsBike,
                  contentDescription = null,
                  tint = MaterialTheme.colorScheme.onPrimary,
                  modifier = Modifier.size(24.dp)
                )
              }
            }
          }
        }
      }

      // Menu Cards
      item {
        Text(
          text = "Management & Preferences",
          style = MaterialTheme.typography.titleMedium,
          fontWeight = FontWeight.Bold
        )
      }

      items(MenuSection.values()) { section ->
        val badgeText = when (section) {
          MenuSection.SUBSCRIPTION -> {
            val now = System.currentTimeMillis()
            if (currentRider != null && currentRider!!.expiresAt > now) {
              val days = ((currentRider!!.expiresAt - now) / (1000 * 60 * 60 * 24)).toInt()
              "$days days left"
            } else "Status / Renew"
          }
          MenuSection.CUSTOMERS -> "${customers.size} saved"
          MenuSection.EXPENSES -> "Rs. ${expenses.sumOf { it.amount }.toInt()}"
          else -> null
        }

        MenuHubCard(
          title = section.title,
          subtitle = section.subtitle,
          icon = section.icon,
          badge = badgeText,
          onClick = { activeSection = section },
          testTag = "menu_item_${section.name.lowercase()}"
        )
      }

      // Quick Backup Export Button
      item {
        OutlinedButton(
          onClick = {
            shareCsv(
              context = context,
              csvContent = viewModel.getBusinessReportCsv(),
              fileName = "sasti_sawari_report_${settings.operatorName.replace(" ", "_")}.csv"
            )
          },
          modifier = Modifier
            .fillMaxWidth()
            .height(52.dp)
            .testTag("menu_export_backup_button"),
          shape = RoundedCornerShape(14.dp)
        ) {
          Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(18.dp))
          Spacer(modifier = Modifier.width(8.dp))
          Text("Export Business Backup (CSV)", fontWeight = FontWeight.Bold)
        }
      }

      item {
        Spacer(modifier = Modifier.height(80.dp))
      }
    }
  } else {
    // Sub-Screen Container with Top Navigation Bar
    Column(modifier = modifier.fillMaxSize()) {
      Surface(
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 2.dp,
        modifier = Modifier.fillMaxWidth()
      ) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 6.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          IconButton(
            onClick = { activeSection = null },
            modifier = Modifier.testTag("menu_back_button")
          ) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back to Menu")
          }
          Spacer(modifier = Modifier.width(4.dp))
          Text(
            text = activeSection?.title ?: "Menu",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
          )
        }
      }

      // Render the dedicated, non-duplicative sub-screen
      when (activeSection) {
        MenuSection.DRIVER_PROFILE -> {
          DriverProfileScreen(viewModel = viewModel, snackbarHostState = snackbarHostState)
        }
        MenuSection.SUBSCRIPTION -> {
          SubscriptionScreen(viewModel = viewModel, snackbarHostState = snackbarHostState)
        }
        MenuSection.DESTINATIONS -> {
          CityDestinationsScreen(viewModel = viewModel, snackbarHostState = snackbarHostState)
        }
        MenuSection.CUSTOMERS -> {
          CustomersTab(customers = customers, context = context)
        }
        MenuSection.EXPENSES -> {
          ExpensesTab(
            viewModel = viewModel,
            expenses = expenses,
            settings = settings,
            totalKm = allRides.sumOf { it.distanceKm },
            totalRevenue = allRides.sumOf { it.totalFare },
            snackbarHostState = snackbarHostState
          )
        }
        MenuSection.FARE_RULES -> {
          FareRulesTab(
            settings = settings,
            onSave = { viewModel.saveSettings(it) },
            snackbarHostState = snackbarHostState
          )
        }
        null -> {}
      }
    }
  }
}

@Composable
fun MenuHubCard(
  title: String,
  subtitle: String,
  icon: ImageVector,
  badge: String? = null,
  onClick: () -> Unit,
  testTag: String
) {
  Card(
    modifier = Modifier
      .fillMaxWidth()
      .clickable(onClick = onClick)
      .testTag(testTag),
    shape = RoundedCornerShape(16.dp),
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(16.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      Surface(
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.primaryContainer,
        modifier = Modifier.size(44.dp)
      ) {
        Box(contentAlignment = Alignment.Center) {
          Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(22.dp)
          )
        }
      }

      Spacer(modifier = Modifier.width(14.dp))

      Column(modifier = Modifier.weight(1f)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Text(
            text = title,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold
          )
          if (badge != null) {
            Spacer(modifier = Modifier.width(8.dp))
            Surface(
              shape = RoundedCornerShape(8.dp),
              color = CashGreen.copy(alpha = 0.15f)
            ) {
              Text(
                text = badge,
                color = CashGreen,
                fontWeight = FontWeight.Bold,
                fontSize = 11.sp,
                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
              )
            }
          }
        }
        Text(
          text = subtitle,
          style = MaterialTheme.typography.bodySmall,
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )
      }

      Icon(
        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
        contentDescription = null,
        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
        modifier = Modifier.size(20.dp)
      )
    }
  }
}

@Composable
fun CustomersTab(
  customers: List<CustomerSummary>,
  context: Context
) {
  if (customers.isEmpty()) {
    Box(
      modifier = Modifier
        .fillMaxSize()
        .padding(32.dp),
      contentAlignment = Alignment.Center
    ) {
      Text(
        text = "No customer records yet.\nCustomers entered in rides will appear here automatically with 1-tap Call & WhatsApp.",
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        textAlign = androidx.compose.ui.text.style.TextAlign.Center
      )
    }
  } else {
    LazyColumn(
      modifier = Modifier
        .fillMaxSize()
        .padding(16.dp),
      verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
      item {
        Text(
          text = "Frequent Customers (${customers.size})",
          style = MaterialTheme.typography.titleMedium,
          fontWeight = FontWeight.Bold
        )
        Text(
          text = "One-tap direct contact with your repeat passengers",
          style = MaterialTheme.typography.bodySmall,
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(4.dp))
      }

      items(customers, key = { it.phone.ifBlank { it.name } }) { cust ->
        Card(
          shape = RoundedCornerShape(16.dp),
          colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
          border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
          modifier = Modifier.fillMaxWidth()
        ) {
          Column(modifier = Modifier.padding(14.dp)) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Column {
                Text(cust.name, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall)
                Text(cust.phone, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
              }

              Surface(
                shape = RoundedCornerShape(8.dp),
                color = CashGreen.copy(alpha = 0.15f)
              ) {
                Text(
                  text = "${cust.totalRides} Rides • Rs. ${cust.totalSpent.toInt()}",
                  color = CashGreen,
                  fontWeight = FontWeight.Bold,
                  fontSize = 12.sp,
                  modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
              }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
              if (cust.phone.isNotBlank()) {
                Button(
                  onClick = {
                    val phone = cust.phone.replace("-", "").replace(" ", "")
                    context.startActivity(Intent(Intent.ACTION_DIAL, Uri.parse("tel:$phone")))
                  },
                  modifier = Modifier.weight(1f),
                  shape = RoundedCornerShape(10.dp),
                  colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                  Icon(Icons.Default.Call, contentDescription = null, modifier = Modifier.size(16.dp))
                  Spacer(modifier = Modifier.width(4.dp))
                  Text("Call", fontSize = 12.sp)
                }

                Button(
                  onClick = {
                    val phone = cust.phone.replace("-", "").replace(" ", "")
                    val formatted = if (phone.startsWith("0")) "92" + phone.substring(1) else phone
                    val url = "https://wa.me/$formatted?text=${Uri.encode("Assalam-o-Alaikum ${cust.name}! Sasti Sawari bike ride service ready.")}"
                    context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
                  },
                  modifier = Modifier.weight(1f),
                  shape = RoundedCornerShape(10.dp),
                  colors = ButtonDefaults.buttonColors(containerColor = CashGreen)
                ) {
                  Icon(Icons.Default.Message, contentDescription = null, modifier = Modifier.size(16.dp))
                  Spacer(modifier = Modifier.width(4.dp))
                  Text("WhatsApp", fontSize = 12.sp)
                }
              }
            }
          }
        }
      }

      item {
        Spacer(modifier = Modifier.height(80.dp))
      }
    }
  }
}

@Composable
fun ExpensesTab(
  viewModel: MainViewModel,
  expenses: List<Expense>,
  settings: AppSettings,
  totalKm: Double,
  totalRevenue: Double,
  snackbarHostState: SnackbarHostState
) {
  val scope = rememberCoroutineScope()
  var showAddDialog by remember { mutableStateOf(false) }

  val totalRecordedExpenses = expenses.sumOf { it.amount }
  val estimatedFuelCost = if (settings.bikeFuelEconomyKmPerLitre > 0) {
    (totalKm / settings.bikeFuelEconomyKmPerLitre) * settings.fuelPricePerLitre
  } else 0.0

  LazyColumn(
    modifier = Modifier
      .fillMaxSize()
      .padding(16.dp),
    verticalArrangement = Arrangement.spacedBy(14.dp)
  ) {
    item {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column {
          Text(
            text = "Expenses & Fuel Management",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
          )
          Text(
            text = "Track petrol, maintenance, and daily kharcha",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
        }

        Button(
          onClick = { showAddDialog = true },
          shape = RoundedCornerShape(12.dp)
        ) {
          Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
          Spacer(modifier = Modifier.width(4.dp))
          Text("Add", fontSize = 12.sp)
        }
      }
    }

    // Expense Summary Card
    item {
      Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
          Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("Total Recorded Revenue:", style = MaterialTheme.typography.bodyMedium)
            Text("Rs. ${totalRevenue.toInt()}", fontWeight = FontWeight.Bold, color = CashGreen)
          }
          Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("Recorded Expenses:", style = MaterialTheme.typography.bodyMedium)
            Text("Rs. ${totalRecordedExpenses.toInt()}", fontWeight = FontWeight.Bold, color = ExpenseRed)
          }
          Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("Est. Fuel Consumption:", style = MaterialTheme.typography.bodyMedium)
            Text("Rs. ${estimatedFuelCost.toInt()}", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.secondary)
          }
          Spacer(modifier = Modifier.height(2.dp))
          Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("Est. Net Profit:", fontWeight = FontWeight.Bold)
            val net = totalRevenue - totalRecordedExpenses - estimatedFuelCost
            Text("Rs. ${net.toInt()}", fontWeight = FontWeight.ExtraBold, color = if (net >= 0) CashGreen else ExpenseRed)
          }
        }
      }
    }

    // Recorded Expenses List
    items(expenses, key = { it.id }) { exp ->
      Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        modifier = Modifier.fillMaxWidth()
      ) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Column {
            Text(exp.category, fontWeight = FontWeight.Bold)
            if (exp.description.isNotBlank()) {
              Text(exp.description, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
          }

          Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
              text = "Rs. ${exp.amount.toInt()}",
              fontWeight = FontWeight.Bold,
              color = ExpenseRed
            )
            IconButton(onClick = { viewModel.deleteExpense(exp) }) {
              Icon(Icons.Default.Delete, contentDescription = "Delete", tint = ExpenseRed.copy(alpha = 0.7f), modifier = Modifier.size(18.dp))
            }
          }
        }
      }
    }

    item {
      Spacer(modifier = Modifier.height(80.dp))
    }
  }

  if (showAddDialog) {
    var category by remember { mutableStateOf("Fuel") }
    var amountText by remember { mutableStateOf("") }
    var descText by remember { mutableStateOf("") }

    AlertDialog(
      onDismissRequest = { showAddDialog = false },
      title = { Text("Record Expense", fontWeight = FontWeight.Bold) },
      text = {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
          OutlinedTextField(
            value = amountText,
            onValueChange = { amountText = it },
            label = { Text("Amount (PKR) *") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
          )
          OutlinedTextField(
            value = category,
            onValueChange = { category = it },
            label = { Text("Category (Fuel, Maintenance, Tea, Stand)") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
          )
          OutlinedTextField(
            value = descText,
            onValueChange = { descText = it },
            label = { Text("Description (Optional)") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
          )
        }
      },
      confirmButton = {
        Button(
          onClick = {
            val amt = amountText.toDoubleOrNull()
            if (amt != null && amt > 0) {
              viewModel.addExpense(category, amt, descText) {
                scope.launch { snackbarHostState.showSnackbar("Expense recorded: Rs. ${amt.toInt()}") }
              }
              showAddDialog = false
            }
          }
        ) {
          Text("Save Expense")
        }
      },
      dismissButton = {
        TextButton(onClick = { showAddDialog = false }) {
          Text("Cancel")
        }
      }
    )
  }
}

// Clean, Deduplicated Fare & Fuel Rules Screen (No duplicate driver profile fields!)
@Composable
fun FareRulesTab(
  settings: AppSettings,
  onSave: (AppSettings) -> Unit,
  snackbarHostState: SnackbarHostState
) {
  val scope = rememberCoroutineScope()

  var baseFare by remember(settings) { mutableStateOf(settings.baseFare.toInt().toString()) }
  var baseKm by remember(settings) { mutableStateOf(settings.baseKm.toString()) }
  var ratePerKm by remember(settings) { mutableStateOf(settings.ratePerAdditionalKm.toInt().toString()) }
  var freeWaitMins by remember(settings) { mutableStateOf(settings.freeWaitingMinutes.toString()) }
  var waitRate by remember(settings) { mutableStateOf(settings.waitingRatePerMinute.toInt().toString()) }
  var fuelPrice by remember(settings) { mutableStateOf(settings.fuelPricePerLitre.toInt().toString()) }
  var bikeEconomy by remember(settings) { mutableStateOf(settings.bikeFuelEconomyKmPerLitre.toInt().toString()) }

  LazyColumn(
    modifier = Modifier
      .fillMaxSize()
      .padding(16.dp),
    verticalArrangement = Arrangement.spacedBy(14.dp)
  ) {
    item {
      Text(
        text = "Stand Fare Calculation Rules",
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold
      )
      Text(
        text = "Multan Bike standard formula: Rs. 80 (first 2 km) + Rs. 25 per additional km. Edit here if union rates change.",
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant
      )
    }

    item {
      Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
          Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            OutlinedTextField(
              value = baseFare,
              onValueChange = { baseFare = it },
              label = { Text("Base Fare (PKR)") },
              keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
              modifier = Modifier.weight(1f)
            )
            OutlinedTextField(
              value = baseKm,
              onValueChange = { baseKm = it },
              label = { Text("Base Distance (km)") },
              keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
              modifier = Modifier.weight(1f)
            )
          }

          OutlinedTextField(
            value = ratePerKm,
            onValueChange = { ratePerKm = it },
            label = { Text("Rate per Additional Km (PKR)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
          )

          Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            OutlinedTextField(
              value = freeWaitMins,
              onValueChange = { freeWaitMins = it },
              label = { Text("Free Wait (min)") },
              keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
              modifier = Modifier.weight(1f)
            )
            OutlinedTextField(
              value = waitRate,
              onValueChange = { waitRate = it },
              label = { Text("Wait Rate/min (PKR)") },
              keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
              modifier = Modifier.weight(1f)
            )
          }
        }
      }
    }

    item {
      Text(
        text = "Motorcycle & Fuel Parameters",
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold
      )
    }

    item {
      Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
          Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            OutlinedTextField(
              value = bikeEconomy,
              onValueChange = { bikeEconomy = it },
              label = { Text("Mileage (km/Litre)") },
              keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
              modifier = Modifier.weight(1f)
            )
            OutlinedTextField(
              value = fuelPrice,
              onValueChange = { fuelPrice = it },
              label = { Text("Fuel Price (Rs./L)") },
              keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
              modifier = Modifier.weight(1f)
            )
          }
        }
      }
    }

    item {
      Button(
        onClick = {
          val updated = settings.copy(
            baseFare = baseFare.toDoubleOrNull() ?: settings.baseFare,
            baseKm = baseKm.toDoubleOrNull() ?: settings.baseKm,
            ratePerAdditionalKm = ratePerKm.toDoubleOrNull() ?: settings.ratePerAdditionalKm,
            freeWaitingMinutes = freeWaitMins.toIntOrNull() ?: settings.freeWaitingMinutes,
            waitingRatePerMinute = waitRate.toDoubleOrNull() ?: settings.waitingRatePerMinute,
            fuelPricePerLitre = fuelPrice.toDoubleOrNull() ?: settings.fuelPricePerLitre,
            bikeFuelEconomyKmPerLitre = bikeEconomy.toDoubleOrNull() ?: settings.bikeFuelEconomyKmPerLitre
          )
          onSave(updated)
          scope.launch {
            snackbarHostState.showSnackbar("Fare calculation rules updated successfully!")
          }
        },
        modifier = Modifier
          .fillMaxWidth()
          .height(52.dp),
        shape = RoundedCornerShape(14.dp)
      ) {
        Icon(Icons.Default.Save, contentDescription = null, modifier = Modifier.size(18.dp))
        Spacer(modifier = Modifier.width(6.dp))
        Text("Save Fare Rules", fontWeight = FontWeight.Bold)
      }
    }

    item {
      Spacer(modifier = Modifier.height(80.dp))
    }
  }
}

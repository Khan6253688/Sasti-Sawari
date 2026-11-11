package com.example.ui.screens

import android.content.Context
import android.content.Intent
import android.net.Uri
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.DirectionsBike
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.AdminConfig
import com.example.data.model.FirestorePayment
import com.example.data.model.FirestoreRider
import com.example.data.model.RiderAccount
import com.example.ui.MainViewModel
import com.example.ui.theme.CashGreen
import com.example.ui.theme.ExpenseRed
import com.example.ui.theme.ImperialGold
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun AdminDashboardScreen(
  viewModel: MainViewModel,
  snackbarHostState: SnackbarHostState,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  val scope = rememberCoroutineScope()
  val adminConfig by viewModel.adminConfig.collectAsStateWithLifecycle()
  val allRiders by viewModel.allRiders.collectAsStateWithLifecycle()
  val firestoreAllPayments by viewModel.firestoreAllPayments.collectAsStateWithLifecycle()
  val firestorePendingPayments by viewModel.firestorePendingPayments.collectAsStateWithLifecycle()
  val firestoreRiders by viewModel.firestoreRiders.collectAsStateWithLifecycle()

  val isAdminLoggedIn by viewModel.isAdminLoggedIn.collectAsStateWithLifecycle()
  var enteredEmail by remember { mutableStateOf("shahid.msc2020@gmail.com") }
  var enteredPassword by remember { mutableStateOf("") }
  var passwordVisible by remember { mutableStateOf(false) }
  var selectedTab by remember { mutableIntStateOf(0) }
  var searchQuery by remember { mutableStateOf("") }
  var riderStatusFilter by remember { mutableStateOf("ALL") }
  var paymentStatusFilter by remember { mutableStateOf("ALL") }

  // Admin Profile Protocol Authentication Gate
  if (!isAdminLoggedIn) {
    Box(
      modifier = modifier
        .fillMaxSize()
        .padding(20.dp),
      contentAlignment = Alignment.Center
    ) {
      Card(
        modifier = Modifier
          .fillMaxWidth()
          .testTag("admin_login_card"),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        border = BorderStroke(1.dp, ImperialGold.copy(alpha = 0.5f))
      ) {
        Column(
          modifier = Modifier.padding(22.dp),
          horizontalAlignment = Alignment.CenterHorizontally,
          verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
          Surface(
            shape = CircleShape,
            color = ImperialGold.copy(alpha = 0.15f),
            modifier = Modifier.size(68.dp)
          ) {
            Box(contentAlignment = Alignment.Center) {
              Icon(
                imageVector = Icons.Default.AdminPanelSettings,
                contentDescription = null,
                tint = ImperialGold,
                modifier = Modifier.size(38.dp)
              )
            }
          }

          Text(
            text = "Platform Administrator Protocol",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.ExtraBold,
            textAlign = TextAlign.Center
          )

          Surface(
            shape = RoundedCornerShape(10.dp),
            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
            modifier = Modifier.fillMaxWidth()
          ) {
            Column(modifier = Modifier.padding(10.dp)) {
              Text(
                text = "Default Login Credentials:",
                fontWeight = FontWeight.Bold,
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.primary
              )
              Text(
                text = "Email: shahid.msc2020@gmail.com\nPassword: Khan@3636",
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
            }
          }

          OutlinedTextField(
            value = enteredEmail,
            onValueChange = { enteredEmail = it },
            label = { Text("Admin Email") },
            leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
            singleLine = true,
            modifier = Modifier
              .fillMaxWidth()
              .testTag("admin_email_input"),
            shape = RoundedCornerShape(12.dp)
          )

          OutlinedTextField(
            value = enteredPassword,
            onValueChange = { enteredPassword = it },
            label = { Text("Admin Password") },
            placeholder = { Text("Khan@3636") },
            leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
            trailingIcon = {
              IconButton(onClick = { passwordVisible = !passwordVisible }) {
                Icon(
                  imageVector = if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                  contentDescription = null
                )
              }
            },
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            singleLine = true,
            modifier = Modifier
              .fillMaxWidth()
              .testTag("admin_password_input"),
            shape = RoundedCornerShape(12.dp)
          )

          Button(
            onClick = {
              val ok = viewModel.loginAdmin(enteredEmail, enteredPassword)
              if (ok) {
                scope.launch {
                  snackbarHostState.showSnackbar("Welcome Shahid! Administrator access granted.")
                }
              } else {
                scope.launch {
                  snackbarHostState.showSnackbar("Invalid Admin Credentials. (Default: shahid.msc2020@gmail.com / Khan@3636)")
                }
              }
            },
            modifier = Modifier
              .fillMaxWidth()
              .height(52.dp)
              .testTag("admin_login_submit_button"),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
          ) {
            Icon(Icons.Default.Lock, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text("LOGIN TO ADMIN PORTAL", fontWeight = FontWeight.Bold)
          }

          TextButton(onClick = { viewModel.resetRoleToSelector() }) {
            Text("Switch Account Protocol", fontSize = 12.sp)
          }
        }
      }
    }
    return
  }

  val now = System.currentTimeMillis()
  
  // Real-time Payments (from Cloud Firestore, fallback to Room)
  val displayPayments: List<FirestorePayment> = if (firestoreAllPayments.isNotEmpty()) {
    firestoreAllPayments
  } else if (firestorePendingPayments.isNotEmpty()) {
    firestorePendingPayments
  } else {
    allRiders.filter { it.lastPaymentTxnId.isNotBlank() }
      .map { r ->
        FirestorePayment(
          id = r.riderId,
          paymentId = "PAY-${r.riderId}",
          riderId = r.riderId,
          userId = "uid-${r.riderId}",
          riderName = r.name,
          riderEmail = r.email,
          riderPhone = r.phone,
          riderCity = r.cityId,
          riderStand = r.standName,
          amount = adminConfig.monthlySubscriptionFee,
          transactionId = r.lastPaymentTxnId,
          paymentMethod = r.lastPaymentMethod.ifBlank { "JazzCash" },
          proofUri = r.lastPaymentProofUri,
          status = r.subscriptionStatus,
          submittedAt = if (r.paymentSubmittedAt > 0) r.paymentSubmittedAt else System.currentTimeMillis(),
          notes = "Awaiting verification for 30-day access"
        )
      }
  }

  val pendingPaymentsCount = displayPayments.count { it.status.equals("PENDING", ignoreCase = true) }
  val approvedPaymentsCount = displayPayments.count { it.status.equals("APPROVED", ignoreCase = true) }
  val rejectedPaymentsCount = displayPayments.count { it.status.equals("REJECTED", ignoreCase = true) }

  val filteredPayments = displayPayments.filter { payment ->
    val matchesStatus = when (paymentStatusFilter) {
      "PENDING" -> payment.status.equals("PENDING", ignoreCase = true)
      "APPROVED" -> payment.status.equals("APPROVED", ignoreCase = true)
      "REJECTED" -> payment.status.equals("REJECTED", ignoreCase = true)
      else -> true
    }
    val matchesSearch = if (searchQuery.isBlank()) true else {
      payment.riderName.contains(searchQuery, ignoreCase = true) ||
          payment.riderEmail.contains(searchQuery, ignoreCase = true) ||
          payment.riderPhone.contains(searchQuery, ignoreCase = true) ||
          payment.transactionId.contains(searchQuery, ignoreCase = true) ||
          payment.riderId.contains(searchQuery, ignoreCase = true)
    }
    matchesStatus && matchesSearch
  }

  // Real-time Registered Riders (from Cloud Firestore, fallback to Room)
  val displayRiders: List<FirestoreRider> = if (firestoreRiders.isNotEmpty()) {
    firestoreRiders
  } else {
    allRiders.map { r ->
      FirestoreRider(
        id = r.riderId,
        riderId = r.riderId,
        userId = "uid-${r.riderId}",
        name = r.name,
        email = r.email,
        phone = r.phone,
        cityId = r.cityId,
        bikeModel = r.bikeModel,
        plate = r.plate,
        standName = r.standName,
        standAddress = r.standAddress,
        subscriptionStatus = r.subscriptionStatus,
        subscriptionPrice = adminConfig.monthlySubscriptionFee,
        activatedAt = r.activatedAt,
        expiresAt = r.expiresAt,
        lastPaymentTxnId = r.lastPaymentTxnId,
        lastPaymentMethod = r.lastPaymentMethod,
        isOnline = r.isOnline
      )
    }
  }

  val activeSubscriptions = displayRiders.filter { it.subscriptionStatus == "ACTIVE" && it.expiresAt > now }
  val expiredRiders = displayRiders.filter { it.subscriptionStatus == "EXPIRED" || it.expiresAt <= now }
  val totalRevenue = activeSubscriptions.size * adminConfig.monthlySubscriptionFee

  val filteredRiders = displayRiders.filter { rider ->
    val matchesSearch = if (searchQuery.isBlank()) true else {
      rider.name.contains(searchQuery, ignoreCase = true) ||
          rider.email.contains(searchQuery, ignoreCase = true) ||
          rider.phone.contains(searchQuery, ignoreCase = true) ||
          rider.riderId.contains(searchQuery, ignoreCase = true) ||
          rider.cityId.contains(searchQuery, ignoreCase = true)
    }
    val matchesStatus = when (riderStatusFilter) {
      "ACTIVE" -> rider.subscriptionStatus == "ACTIVE" && rider.expiresAt > now
      "PENDING" -> rider.subscriptionStatus == "PENDING"
      "EXPIRED" -> rider.subscriptionStatus == "EXPIRED" || rider.expiresAt <= now
      else -> true
    }
    matchesSearch && matchesStatus
  }

  Column(modifier = modifier.fillMaxSize()) {
    // Admin Profile Banner Header
    Surface(
      color = MaterialTheme.colorScheme.surface,
      tonalElevation = 2.dp,
      modifier = Modifier.fillMaxWidth()
    ) {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 16.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Surface(
            shape = CircleShape,
            color = ImperialGold.copy(alpha = 0.2f),
            modifier = Modifier.size(40.dp)
          ) {
            Box(contentAlignment = Alignment.Center) {
              Icon(
                imageVector = Icons.Default.AdminPanelSettings,
                contentDescription = null,
                tint = ImperialGold,
                modifier = Modifier.size(22.dp)
              )
            }
          }
          Spacer(modifier = Modifier.width(10.dp))
          Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Text(
                text = "Shahid (Administrator)",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.ExtraBold
              )
              Spacer(modifier = Modifier.width(6.dp))
              Surface(
                shape = RoundedCornerShape(4.dp),
                color = ImperialGold.copy(alpha = 0.2f)
              ) {
                Text(
                  text = "SUPER ADMIN",
                  color = ImperialGold,
                  fontSize = 9.sp,
                  fontWeight = FontWeight.Bold,
                  modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                )
              }
            }
            Text(
              text = "shahid.msc2020@gmail.com",
              fontSize = 11.sp,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }
        }

        OutlinedButton(
          onClick = { viewModel.logoutAdmin() },
          shape = RoundedCornerShape(10.dp)
        ) {
          Icon(Icons.Default.Lock, contentDescription = null, modifier = Modifier.size(14.dp))
          Spacer(modifier = Modifier.width(4.dp))
          Text("Sign Out", fontSize = 11.sp, fontWeight = FontWeight.Bold)
        }
      }
    }

    // Top Search Bar to allow searching registered users by name or email
    Surface(
      color = MaterialTheme.colorScheme.surface,
      tonalElevation = 1.dp,
      modifier = Modifier.fillMaxWidth()
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 16.dp, vertical = 6.dp)
      ) {
        OutlinedTextField(
          value = searchQuery,
          onValueChange = { searchQuery = it },
          placeholder = { Text("Search registered users by name or email...", fontSize = 13.sp) },
          leadingIcon = {
            Icon(
              imageVector = Icons.Default.Search,
              contentDescription = "Search registered users",
              tint = MaterialTheme.colorScheme.primary
            )
          },
          trailingIcon = {
            if (searchQuery.isNotEmpty()) {
              IconButton(onClick = { searchQuery = "" }) {
                Icon(
                  imageVector = Icons.Default.Clear,
                  contentDescription = "Clear search",
                  tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
              }
            }
          },
          singleLine = true,
          modifier = Modifier
            .fillMaxWidth()
            .testTag("admin_top_user_search_bar"),
          shape = RoundedCornerShape(12.dp)
        )

        if (searchQuery.isNotBlank()) {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(top = 4.dp, start = 4.dp, end = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text(
              text = "Found ${filteredRiders.size} registered user(s) matching \"$searchQuery\"",
              style = MaterialTheme.typography.bodySmall,
              color = MaterialTheme.colorScheme.primary,
              fontWeight = FontWeight.Bold
            )
            if (selectedTab != 2) {
              TextButton(onClick = { selectedTab = 2 }) {
                Text("View in Users Tab (${filteredRiders.size})", fontSize = 11.sp)
              }
            }
          }
        }
      }
    }

    // 4 Distinct Tabs with real-time badges
    TabRow(selectedTabIndex = selectedTab) {
      Tab(
        selected = selectedTab == 0,
        onClick = { selectedTab = 0 },
        text = { Text("Revenue", fontWeight = FontWeight.Bold, fontSize = 12.sp) }
      )
      Tab(
        selected = selectedTab == 1,
        onClick = { selectedTab = 1 },
        text = {
          Text(
            if (pendingPaymentsCount > 0) "Payments ($pendingPaymentsCount Pending)" else "Payments (${displayPayments.size})",
            fontWeight = FontWeight.Bold,
            fontSize = 11.sp
          )
        }
      )
      Tab(
        selected = selectedTab == 2,
        onClick = { selectedTab = 2 },
        text = {
          Text(
            if (searchQuery.isNotBlank()) "Users (${filteredRiders.size})" else "Riders (${displayRiders.size})",
            fontWeight = FontWeight.Bold,
            fontSize = 12.sp
          )
        }
      )
      Tab(
        selected = selectedTab == 3,
        onClick = { selectedTab = 3 },
        text = { Text("Accounts", fontWeight = FontWeight.Bold, fontSize = 12.sp) }
      )
    }

    LazyColumn(
      modifier = Modifier
        .fillMaxSize()
        .padding(16.dp),
      verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
      // Primary KPI Metrics Header
      item {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          AdminMetricCard(
            title = "ACTIVE",
            value = "${activeSubscriptions.size}",
            subtitle = "Riders",
            color = CashGreen,
            modifier = Modifier.weight(1f)
          )
          AdminMetricCard(
            title = "PENDING",
            value = "$pendingPaymentsCount",
            subtitle = "Payments",
            color = ImperialGold,
            modifier = Modifier.weight(1f)
          )
          AdminMetricCard(
            title = "REVENUE",
            value = "Rs. ${totalRevenue.toInt()}",
            subtitle = "Monthly MRR",
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.weight(1.2f)
          )
        }
      }

      when (selectedTab) {
        0 -> {
          // TAB 0: REVENUE & 30-DAY CYCLE AUTOMATION
          item {
            Card(
              modifier = Modifier.fillMaxWidth(),
              shape = RoundedCornerShape(18.dp),
              colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
              border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
            ) {
              Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.SpaceBetween,
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Text(
                    text = "Subscription Revenue Analytics",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                  )
                  Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = CashGreen.copy(alpha = 0.15f)
                  ) {
                    Text(
                      text = "Rs. ${adminConfig.monthlySubscriptionFee.toInt()}/Rider",
                      color = CashGreen,
                      fontWeight = FontWeight.Bold,
                      fontSize = 11.sp,
                      modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                  }
                }

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                  Text("Total Registered Riders:", style = MaterialTheme.typography.bodyMedium)
                  Text("${allRiders.size}", fontWeight = FontWeight.Bold)
                }

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                  Text("Active Paid Subscriptions (30 Days):", style = MaterialTheme.typography.bodyMedium)
                  Text("${activeSubscriptions.size}", fontWeight = FontWeight.Bold, color = CashGreen)
                }

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                  Text("Expired / Locked Accounts:", style = MaterialTheme.typography.bodyMedium)
                  Text("${expiredRiders.size}", fontWeight = FontWeight.Bold, color = ExpenseRed)
                }

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                  Text("Monthly Recurring Revenue (MRR):", fontWeight = FontWeight.Bold)
                  Text("Rs. ${totalRevenue.toInt()}", fontWeight = FontWeight.ExtraBold, color = CashGreen, fontSize = 16.sp)
                }
              }
            }
          }

          // Cloud Function & 30-Day Cycle Automation Card
          item {
            Card(
              modifier = Modifier.fillMaxWidth(),
              shape = RoundedCornerShape(18.dp),
              colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)),
              border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.3f))
            ) {
              Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                  Icon(
                    imageVector = Icons.Default.CloudDone,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(22.dp)
                  )
                  Spacer(modifier = Modifier.width(8.dp))
                  Text(
                    text = "Cloud Function: Automatic Access Locking",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                  )
                }

                Text(
                  text = "The background cycle monitors 30-day expiry timestamps. When 30 days elapse, rider accounts are automatically locked, blocking calculator & ride entry while preserving all data records.",
                  style = MaterialTheme.typography.bodySmall,
                  color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Button(
                  onClick = {
                    val lockedCount = viewModel.runSubscriptionCycleCheck()
                    scope.launch {
                      snackbarHostState.showSnackbar("Cycle check executed! $lockedCount expired accounts processed.")
                    }
                  },
                  modifier = Modifier.fillMaxWidth(),
                  shape = RoundedCornerShape(12.dp)
                ) {
                  Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                  Spacer(modifier = Modifier.width(6.dp))
                  Text("Run Manual Expiry & Lock Cycle Check", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
              }
            }
          }
        }

        1 -> {
          // TAB 1: PAYMENTS MANAGEMENT (FROM CLOUD FIRESTORE)
          item {
            Surface(
              shape = RoundedCornerShape(12.dp),
              color = CashGreen.copy(alpha = 0.1f),
              border = BorderStroke(1.dp, CashGreen.copy(alpha = 0.3f)),
              modifier = Modifier.fillMaxWidth()
            ) {
              Row(
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
              ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                  Icon(
                    imageVector = Icons.Default.CloudDone,
                    contentDescription = null,
                    tint = CashGreen,
                    modifier = Modifier.size(18.dp)
                  )
                  Spacer(modifier = Modifier.width(6.dp))
                  Text(
                    text = "Cloud Firestore: /payments (Real-Time)",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                  )
                }
                Text(
                  text = "$pendingPaymentsCount Pending • ${displayPayments.size} Total",
                  fontSize = 11.sp,
                  fontWeight = FontWeight.ExtraBold,
                  color = if (pendingPaymentsCount > 0) ImperialGold else CashGreen
                )
              }
            }
          }

          // Payment Status Filter Chips: ALL, PENDING, APPROVED, REJECTED
          item {
            val paymentFilters = listOf(
              "ALL" to "All (${displayPayments.size})",
              "PENDING" to "Pending ($pendingPaymentsCount)",
              "APPROVED" to "Approved ($approvedPaymentsCount)",
              "REJECTED" to "Rejected ($rejectedPaymentsCount)"
            )
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
              items(paymentFilters) { (statusKey, label) ->
                val isSel = paymentStatusFilter == statusKey
                FilterChip(
                  selected = isSel,
                  onClick = { paymentStatusFilter = statusKey },
                  label = { Text(label, fontSize = 11.sp, fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal) },
                  colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = when (statusKey) {
                      "APPROVED" -> CashGreen
                      "PENDING" -> ImperialGold
                      "REJECTED" -> ExpenseRed
                      else -> MaterialTheme.colorScheme.primary
                    },
                    selectedLabelColor = Color.White
                  ),
                  shape = RoundedCornerShape(10.dp)
                )
              }
            }
          }

          if (filteredPayments.isEmpty()) {
            item {
              Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
              ) {
                Column(
                  modifier = Modifier
                    .fillMaxWidth()
                    .padding(32.dp),
                  horizontalAlignment = Alignment.CenterHorizontally
                ) {
                  Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = null,
                    modifier = Modifier.size(44.dp),
                    tint = CashGreen
                  )
                  Spacer(modifier = Modifier.height(10.dp))
                  Text(
                    text = if (paymentStatusFilter == "ALL") "No payments found!" else "No $paymentStatusFilter payments found!",
                    fontWeight = FontWeight.Bold
                  )
                  Text(
                    text = "When riders submit Rs. 100 payments via JazzCash, Easypaisa, or Bank, requests stream live into Firestore and appear here with status badges.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                  )
                }
              }
            }
          } else {
            items(filteredPayments, key = { it.paymentId }) { payment ->
              AdminPendingPaymentCard(
                payment = payment,
                onApprove = {
                  viewModel.approveFirestorePayment(payment, 30) { success ->
                    scope.launch {
                      snackbarHostState.showSnackbar("Approved Rs. 100 payment! 30-day access activated for ${payment.riderName}.")
                    }
                  }
                },
                onReject = {
                  viewModel.rejectFirestorePayment(payment, "Rejected by Admin") { success ->
                    scope.launch {
                      snackbarHostState.showSnackbar("Payment rejected for ${payment.riderName}.")
                    }
                  }
                }
              )
            }
          }
        }

        2 -> {
          // TAB 2: RIDERS & RENEWALS MANAGEMENT (FROM CLOUD FIRESTORE)
          item {
            Surface(
              shape = RoundedCornerShape(12.dp),
              color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f),
              border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.25f)),
              modifier = Modifier.fillMaxWidth()
            ) {
              Row(
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
              ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                  Icon(
                    imageVector = Icons.Default.CloudDone,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(18.dp)
                  )
                  Spacer(modifier = Modifier.width(6.dp))
                  Text(
                    text = "Cloud Firestore: /riders (Real-Time)",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                  )
                }
                Text(
                  text = "${filteredRiders.size} / ${displayRiders.size} Users",
                  fontSize = 11.sp,
                  fontWeight = FontWeight.Bold,
                  color = MaterialTheme.colorScheme.primary
                )
              }
            }
          }

          // Status filter chips
          item {
            val statusFilters = listOf("ALL", "ACTIVE", "PENDING", "EXPIRED")
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
              items(statusFilters) { filter ->
                val isSel = riderStatusFilter == filter
                FilterChip(
                  selected = isSel,
                  onClick = { riderStatusFilter = filter },
                  label = { Text(filter) },
                  colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = MaterialTheme.colorScheme.primary,
                    selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                  ),
                  shape = RoundedCornerShape(10.dp)
                )
              }
            }
          }

          if (filteredRiders.isEmpty()) {
            item {
              Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
              ) {
                Column(
                  modifier = Modifier
                    .fillMaxWidth()
                    .padding(32.dp),
                  horizontalAlignment = Alignment.CenterHorizontally
                ) {
                  Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = null,
                    modifier = Modifier.size(44.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                  )
                  Spacer(modifier = Modifier.height(10.dp))
                  Text(
                    text = if (searchQuery.isNotBlank()) "No users found matching \"$searchQuery\"" else "No users in this category",
                    fontWeight = FontWeight.Bold
                  )
                  if (searchQuery.isNotBlank()) {
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedButton(
                      onClick = { searchQuery = "" },
                      shape = RoundedCornerShape(8.dp)
                    ) {
                      Text("Clear Search Query")
                    }
                  }
                }
              }
            }
          } else {
            items(filteredRiders, key = { it.riderId }) { rider ->
              AdminRiderManageCard(
                rider = rider,
                onExtend = {
                  viewModel.renewFirestoreRider(rider.riderId, 30) { success ->
                    scope.launch {
                      snackbarHostState.showSnackbar("Extended 30 days for ${rider.name} (${rider.riderId}).")
                    }
                  }
                },
                onToggleStatus = { newStatus ->
                  if (newStatus == "EXPIRED") {
                    viewModel.lockFirestoreRider(rider.riderId) {
                      scope.launch {
                        snackbarHostState.showSnackbar("${rider.name} account locked.")
                      }
                    }
                  } else {
                    viewModel.renewFirestoreRider(rider.riderId, 30) {
                      scope.launch {
                        snackbarHostState.showSnackbar("${rider.name} account activated for 30 days.")
                      }
                    }
                  }
                },
                onCall = {
                  val phone = rider.phone.replace("-", "").replace(" ", "")
                  context.startActivity(Intent(Intent.ACTION_DIAL, Uri.parse("tel:$phone")))
                }
              )
            }
          }
        }

        3 -> {
          // TAB 3: ADMIN PAYMENT SETTINGS
          item {
            AdminSettingsSection(
              adminConfig = adminConfig,
              onSave = { updated ->
                viewModel.updateAdminConfig(updated)
                scope.launch {
                  snackbarHostState.showSnackbar("Admin configuration saved successfully.")
                }
              }
            )
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
fun AdminMetricCard(
  title: String,
  value: String,
  subtitle: String,
  color: Color,
  modifier: Modifier = Modifier
) {
  Card(
    modifier = modifier,
    shape = RoundedCornerShape(14.dp),
    colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.12f)),
    border = BorderStroke(1.dp, color.copy(alpha = 0.3f))
  ) {
    Column(
      modifier = Modifier.padding(10.dp),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      Text(text = title, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = color)
      Text(text = value, fontSize = 16.sp, fontWeight = FontWeight.ExtraBold, color = color)
      Text(text = subtitle, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
  }
}

@Composable
fun PaymentStatusBadge(
  status: String,
  modifier: Modifier = Modifier
) {
  val normalized = status.uppercase()
  val (bgColor, borderColor, contentColor, icon, label) = when (normalized) {
    "APPROVED" -> StatusBadgeConfig(
      bg = CashGreen.copy(alpha = 0.15f),
      border = CashGreen.copy(alpha = 0.6f),
      content = CashGreen,
      icon = Icons.Default.CheckCircle,
      label = "Approved"
    )
    "REJECTED" -> StatusBadgeConfig(
      bg = ExpenseRed.copy(alpha = 0.15f),
      border = ExpenseRed.copy(alpha = 0.6f),
      content = ExpenseRed,
      icon = Icons.Default.Cancel,
      label = "Rejected"
    )
    else -> StatusBadgeConfig(
      bg = ImperialGold.copy(alpha = 0.18f),
      border = ImperialGold.copy(alpha = 0.6f),
      content = ImperialGold,
      icon = Icons.Default.Schedule,
      label = "Pending"
    )
  }

  Surface(
    shape = RoundedCornerShape(8.dp),
    color = bgColor,
    border = BorderStroke(1.dp, borderColor),
    modifier = modifier.testTag("payment_status_badge_${label.lowercase()}")
  ) {
    Row(
      modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
      Icon(
        imageVector = icon,
        contentDescription = label,
        tint = contentColor,
        modifier = Modifier.size(13.dp)
      )
      Text(
        text = label,
        fontWeight = FontWeight.ExtraBold,
        color = contentColor,
        fontSize = 11.sp
      )
    }
  }
}

private data class StatusBadgeConfig(
  val bg: Color,
  val border: Color,
  val content: Color,
  val icon: androidx.compose.ui.graphics.vector.ImageVector,
  val label: String
)

@Composable
fun AdminPendingPaymentCard(
  payment: FirestorePayment,
  onApprove: () -> Unit,
  onReject: () -> Unit
) {
  val dateFormat = SimpleDateFormat("dd MMM, hh:mm a", Locale.getDefault())

  val cardBorder = when (payment.status.uppercase()) {
    "APPROVED" -> CashGreen.copy(alpha = 0.4f)
    "REJECTED" -> ExpenseRed.copy(alpha = 0.4f)
    else -> ImperialGold.copy(alpha = 0.5f)
  }

  Card(
    modifier = Modifier
      .fillMaxWidth()
      .testTag("pending_payment_${payment.paymentId}"),
    shape = RoundedCornerShape(16.dp),
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    border = BorderStroke(1.dp, cardBorder)
  ) {
    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column(modifier = Modifier.weight(1f)) {
          Text(
            text = "${payment.riderName} (${payment.riderId})",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
          )
          if (payment.riderEmail.isNotBlank()) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              modifier = Modifier.padding(top = 2.dp)
            ) {
              Icon(
                imageVector = Icons.Default.Email,
                contentDescription = "Email",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(13.dp)
              )
              Spacer(modifier = Modifier.width(4.dp))
              Text(
                text = payment.riderEmail,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.SemiBold
              )
            }
          }
          Text(
            text = "${payment.riderPhone} • ${payment.riderCity.uppercase()} Stand: ${payment.riderStand}",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
        }

        Column(horizontalAlignment = Alignment.End, verticalArrangement = Arrangement.spacedBy(6.dp)) {
          PaymentStatusBadge(status = payment.status)
          Surface(
            shape = RoundedCornerShape(8.dp),
            color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)
          ) {
            Text(
              text = "Rs. ${payment.amount.toInt()}",
              fontWeight = FontWeight.ExtraBold,
              color = MaterialTheme.colorScheme.primary,
              modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
              fontSize = 13.sp
            )
          }
        }
      }

      Surface(
        shape = RoundedCornerShape(10.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text(
              text = "Payment Method: ${payment.paymentMethod}",
              fontWeight = FontWeight.Bold,
              fontSize = 12.sp
            )
            Surface(
              shape = RoundedCornerShape(6.dp),
              color = when (payment.paymentMethod) {
                "JazzCash" -> Color(0xFFD32F2F).copy(alpha = 0.15f)
                "Easypaisa" -> CashGreen.copy(alpha = 0.15f)
                else -> MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
              }
            ) {
              Text(
                text = payment.paymentMethod.uppercase(),
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = when (payment.paymentMethod) {
                  "JazzCash" -> Color(0xFFD32F2F)
                  "Easypaisa" -> CashGreen
                  else -> MaterialTheme.colorScheme.primary
                },
                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
              )
            }
          }

          Text(
            text = "Transaction ID: ${payment.transactionId}",
            fontWeight = FontWeight.ExtraBold,
            fontSize = 13.sp,
            color = MaterialTheme.colorScheme.primary
          )
          if (payment.submittedAt > 0) {
            Text(
              text = "Submitted: ${dateFormat.format(Date(payment.submittedAt))}",
              fontSize = 11.sp,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }
          if (payment.notes.isNotBlank()) {
            Text(
              text = "Note: ${payment.notes}",
              fontSize = 11.sp,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }
          if (payment.proofUri.isNotBlank()) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = null,
                tint = CashGreen,
                modifier = Modifier.size(14.dp)
              )
              Spacer(modifier = Modifier.width(4.dp))
              Text(
                text = "Screenshot Proof Attached ✓",
                fontSize = 11.sp,
                color = CashGreen,
                fontWeight = FontWeight.Bold
              )
            }
          }
        }
      }

      when (payment.status.uppercase()) {
        "PENDING" -> {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
          ) {
            OutlinedButton(
              onClick = onReject,
              modifier = Modifier.weight(1f),
              colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error),
              shape = RoundedCornerShape(10.dp)
            ) {
              Icon(Icons.Default.Close, contentDescription = null, modifier = Modifier.size(16.dp))
              Spacer(modifier = Modifier.width(4.dp))
              Text("Reject", fontSize = 12.sp)
            }

            Button(
              onClick = onApprove,
              modifier = Modifier.weight(1f),
              colors = ButtonDefaults.buttonColors(containerColor = CashGreen),
              shape = RoundedCornerShape(10.dp)
            ) {
              Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp))
              Spacer(modifier = Modifier.width(4.dp))
              Text("Approve +30d", fontWeight = FontWeight.Bold, fontSize = 12.sp)
            }
          }
        }
        "APPROVED" -> {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Surface(
              shape = RoundedCornerShape(8.dp),
              color = CashGreen.copy(alpha = 0.12f),
              border = BorderStroke(1.dp, CashGreen.copy(alpha = 0.3f))
            ) {
              Row(
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
              ) {
                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = CashGreen, modifier = Modifier.size(14.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Approved & 30-Day Access Active", color = CashGreen, fontWeight = FontWeight.Bold, fontSize = 11.sp)
              }
            }

            OutlinedButton(
              onClick = onApprove,
              shape = RoundedCornerShape(8.dp)
            ) {
              Text("Extend +30d", fontSize = 11.sp)
            }
          }
        }
        "REJECTED" -> {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Surface(
              shape = RoundedCornerShape(8.dp),
              color = ExpenseRed.copy(alpha = 0.12f),
              border = BorderStroke(1.dp, ExpenseRed.copy(alpha = 0.3f))
            ) {
              Row(
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
              ) {
                Icon(Icons.Default.Cancel, contentDescription = null, tint = ExpenseRed, modifier = Modifier.size(14.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Payment Rejected", color = ExpenseRed, fontWeight = FontWeight.Bold, fontSize = 11.sp)
              }
            }

            Button(
              onClick = onApprove,
              colors = ButtonDefaults.buttonColors(containerColor = CashGreen),
              shape = RoundedCornerShape(8.dp)
            ) {
              Text("Re-Approve +30d", fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
          }
        }
      }
    }
  }
}

@Composable
fun AdminRiderManageCard(
  rider: FirestoreRider,
  onExtend: () -> Unit,
  onToggleStatus: (String) -> Unit,
  onCall: () -> Unit
) {
  val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
  val now = System.currentTimeMillis()
  val daysLeft = if (rider.expiresAt > now) {
    ((rider.expiresAt - now) / (1000 * 60 * 60 * 24)).toInt()
  } else 0

  Card(
    modifier = Modifier.fillMaxWidth(),
    shape = RoundedCornerShape(14.dp),
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
  ) {
    Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column(modifier = Modifier.weight(1f)) {
          Text(
            text = "${rider.name} • ${rider.riderId}",
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.titleSmall
          )
          if (rider.email.isNotBlank()) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              modifier = Modifier.padding(top = 2.dp)
            ) {
              Icon(
                imageVector = Icons.Default.Email,
                contentDescription = "Email",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(13.dp)
              )
              Spacer(modifier = Modifier.width(4.dp))
              Text(
                text = rider.email,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.SemiBold
              )
            }
          }
          Text(
            text = "${rider.phone} • ${rider.cityId.uppercase()} • ${rider.bikeModel} (${rider.plate})",
            fontSize = 11.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
          Text(
            text = "Stand: ${rider.standName}",
            fontSize = 11.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
        }

        val statusColor = when (rider.subscriptionStatus) {
          "ACTIVE" -> CashGreen
          "PENDING" -> ImperialGold
          else -> MaterialTheme.colorScheme.error
        }
        Surface(
          shape = RoundedCornerShape(6.dp),
          color = statusColor.copy(alpha = 0.15f)
        ) {
          Text(
            text = "${rider.subscriptionStatus} ($daysLeft d)",
            color = statusColor,
            fontWeight = FontWeight.Bold,
            fontSize = 11.sp,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
          )
        }
      }

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = if (rider.expiresAt > 0) "Expires: ${dateFormat.format(Date(rider.expiresAt))}" else "Access Expired / Locked",
          fontSize = 11.sp,
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
          IconButton(onClick = onCall, modifier = Modifier.size(32.dp)) {
            Icon(Icons.Default.Call, contentDescription = "Call Rider", tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(16.dp))
          }

          OutlinedButton(
            onClick = {
              val next = if (rider.subscriptionStatus == "ACTIVE") "EXPIRED" else "ACTIVE"
              onToggleStatus(next)
            },
            shape = RoundedCornerShape(8.dp),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 8.dp, vertical = 4.dp)
          ) {
            Text(if (rider.subscriptionStatus == "ACTIVE") "Lock" else "Unlock", fontSize = 11.sp)
          }

          Button(
            onClick = onExtend,
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 8.dp, vertical = 4.dp)
          ) {
            Text("+30 Days", fontSize = 11.sp, fontWeight = FontWeight.Bold)
          }
        }
      }
    }
  }
}

@Composable
fun AdminSettingsSection(
  adminConfig: AdminConfig,
  onSave: (AdminConfig) -> Unit
) {
  var pin by remember(adminConfig) { mutableStateOf(adminConfig.adminPin) }
  var fee by remember(adminConfig) { mutableStateOf(adminConfig.monthlySubscriptionFee.toInt().toString()) }
  var jazzNumber by remember(adminConfig) { mutableStateOf(adminConfig.jazzCashNumber) }
  var jazzTitle by remember(adminConfig) { mutableStateOf(adminConfig.jazzCashTitle) }
  var epNumber by remember(adminConfig) { mutableStateOf(adminConfig.easypaisaNumber) }
  var epTitle by remember(adminConfig) { mutableStateOf(adminConfig.easypaisaTitle) }
  var bankName by remember(adminConfig) { mutableStateOf(adminConfig.bankName) }
  var bankTitle by remember(adminConfig) { mutableStateOf(adminConfig.bankAccountTitle) }
  var iban by remember(adminConfig) { mutableStateOf(adminConfig.bankIban) }
  var voiceEnabled by remember(adminConfig) { mutableStateOf(adminConfig.isVoiceAnnouncementsEnabled) }
  var simExpired by remember(adminConfig) { mutableStateOf(adminConfig.isTestModeSimulateExpired) }

  Card(
    modifier = Modifier.fillMaxWidth(),
    shape = RoundedCornerShape(16.dp),
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
  ) {
    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
      Text(
        text = "Subscription & Payment Settings",
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold
      )

      OutlinedTextField(
        value = pin,
        onValueChange = { pin = it },
        label = { Text("Admin PIN") },
        singleLine = true,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp)
      )

      OutlinedTextField(
        value = fee,
        onValueChange = { fee = it },
        label = { Text("Monthly Subscription Fee (PKR)") },
        singleLine = true,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp)
      )

      Text("JazzCash Account Details", fontWeight = FontWeight.Bold, fontSize = 13.sp)
      OutlinedTextField(
        value = jazzNumber,
        onValueChange = { jazzNumber = it },
        label = { Text("JazzCash Mobile Number") },
        singleLine = true,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp)
      )
      OutlinedTextField(
        value = jazzTitle,
        onValueChange = { jazzTitle = it },
        label = { Text("JazzCash Account Title") },
        singleLine = true,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp)
      )

      Text("Easypaisa Account Details", fontWeight = FontWeight.Bold, fontSize = 13.sp)
      OutlinedTextField(
        value = epNumber,
        onValueChange = { epNumber = it },
        label = { Text("Easypaisa Mobile Number") },
        singleLine = true,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp)
      )
      OutlinedTextField(
        value = epTitle,
        onValueChange = { epTitle = it },
        label = { Text("Easypaisa Account Title") },
        singleLine = true,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp)
      )

      Text("Bank Account Details", fontWeight = FontWeight.Bold, fontSize = 13.sp)
      OutlinedTextField(
        value = bankName,
        onValueChange = { bankName = it },
        label = { Text("Bank Name") },
        singleLine = true,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp)
      )
      OutlinedTextField(
        value = bankTitle,
        onValueChange = { bankTitle = it },
        label = { Text("Account Title") },
        singleLine = true,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp)
      )
      OutlinedTextField(
        value = iban,
        onValueChange = { iban = it },
        label = { Text("IBAN / Account Number") },
        singleLine = true,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp)
      )

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column(modifier = Modifier.weight(1f)) {
          Text("Urdu Voice Audio Announcements", fontWeight = FontWeight.Bold)
          Text("Play voice on ride start & completion", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Switch(checked = voiceEnabled, onCheckedChange = { voiceEnabled = it })
      }

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column(modifier = Modifier.weight(1f)) {
          Text("Simulate Expired (Test Mode)", fontWeight = FontWeight.Bold)
          Text("Locks all riders to test renewal flow", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Switch(checked = simExpired, onCheckedChange = { simExpired = it })
      }

      Button(
        onClick = {
          val updated = adminConfig.copy(
            adminPin = pin.trim().ifBlank { "1234" },
            monthlySubscriptionFee = fee.toDoubleOrNull() ?: 100.0,
            jazzCashNumber = jazzNumber.trim(),
            jazzCashTitle = jazzTitle.trim(),
            easypaisaNumber = epNumber.trim(),
            easypaisaTitle = epTitle.trim(),
            bankName = bankName.trim(),
            bankAccountTitle = bankTitle.trim(),
            bankIban = iban.trim(),
            isVoiceAnnouncementsEnabled = voiceEnabled,
            isTestModeSimulateExpired = simExpired
          )
          onSave(updated)
        },
        modifier = Modifier
          .fillMaxWidth()
          .height(48.dp),
        shape = RoundedCornerShape(12.dp)
      ) {
        Text("SAVE CONFIGURATION", fontWeight = FontWeight.Bold)
      }
    }
  }
}

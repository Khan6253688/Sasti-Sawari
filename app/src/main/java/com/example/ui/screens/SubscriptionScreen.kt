package com.example.ui.screens

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
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
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.AdminConfig
import com.example.data.model.RiderAccount
import com.example.ui.MainViewModel
import com.example.ui.components.shareCsv
import com.example.ui.theme.CashGreen
import com.example.ui.theme.ImperialGold
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SubscriptionScreen(
  viewModel: MainViewModel,
  snackbarHostState: SnackbarHostState,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  val scope = rememberCoroutineScope()
  val clipboardManager = LocalClipboardManager.current

  val currentRider by viewModel.currentRider.collectAsStateWithLifecycle()
  val adminConfig by viewModel.adminConfig.collectAsStateWithLifecycle()
  val isLocked by viewModel.isSubscriptionLocked.collectAsStateWithLifecycle()
  val allCities by viewModel.serviceCities.collectAsStateWithLifecycle()

  // Payment Submission State
  var paymentTxnId by remember { mutableStateOf("") }
  var paymentMethod by remember { mutableStateOf("JazzCash") }
  var paymentProofUri by remember { mutableStateOf("") }
  var isSubmittingPayment by remember { mutableStateOf(false) }

  // Dialogs
  var showRegisterDialog by remember { mutableStateOf(false) }
  var showLoginDialog by remember { mutableStateOf(false) }

  val photoPickerLauncher = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.PickVisualMedia()
  ) { uri ->
    uri?.let {
      paymentProofUri = it.toString()
      scope.launch {
        snackbarHostState.showSnackbar("Payment screenshot attached successfully.")
      }
    }
  }

  val rider = currentRider
  val now = System.currentTimeMillis()
  val daysRemaining = if (rider != null && rider.expiresAt > now) {
    ((rider.expiresAt - now) / (1000 * 60 * 60 * 24)).toInt()
  } else 0

  val dateFormat = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault())

  LazyColumn(
    modifier = modifier
      .fillMaxSize()
      .padding(horizontal = 16.dp),
    verticalArrangement = Arrangement.spacedBy(16.dp)
  ) {
    item {
      Spacer(modifier = Modifier.height(8.dp))
      // Role & Account Info Header
      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
          containerColor = if (isLocked) MaterialTheme.colorScheme.errorContainer
          else MaterialTheme.colorScheme.primaryContainer
        )
      ) {
        Column(modifier = Modifier.padding(18.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Column(modifier = Modifier.weight(1f)) {
              Text(
                text = "RIDER SUBSCRIPTION",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = if (isLocked) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
              )
              Text(
                text = if (rider != null) "${rider.name} (${rider.riderId})" else "Rider Account",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.ExtraBold,
                color = if (isLocked) MaterialTheme.colorScheme.onErrorContainer else MaterialTheme.colorScheme.onPrimaryContainer
              )
              if (rider != null) {
                Text(
                  text = "${rider.phone} • ${rider.cityId.uppercase()} Stand",
                  style = MaterialTheme.typography.bodyMedium,
                  color = if (isLocked) MaterialTheme.colorScheme.onErrorContainer.copy(alpha = 0.8f)
                  else MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                )
              }
            }

            Surface(
              shape = CircleShape,
              color = if (isLocked) MaterialTheme.colorScheme.error else CashGreen,
              modifier = Modifier.size(46.dp)
            ) {
              Box(contentAlignment = Alignment.Center) {
                Icon(
                  imageVector = if (isLocked) Icons.Default.Lock else Icons.Default.CheckCircle,
                  contentDescription = null,
                  tint = Color.White,
                  modifier = Modifier.size(26.dp)
                )
              }
            }
          }

          Spacer(modifier = Modifier.height(14.dp))

          // Status & Expiry Badges
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            val statusColor = when {
              isLocked -> MaterialTheme.colorScheme.error
              rider?.subscriptionStatus == "PENDING" -> ImperialGold
              else -> CashGreen
            }

            Surface(
              shape = RoundedCornerShape(10.dp),
              color = statusColor.copy(alpha = 0.15f),
              border = BorderStroke(1.dp, statusColor.copy(alpha = 0.4f)),
              modifier = Modifier.weight(1f)
            ) {
              Column(
                modifier = Modifier.padding(10.dp),
                horizontalAlignment = Alignment.CenterHorizontally
              ) {
                Text(text = "STATUS", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = statusColor)
                Text(
                  text = if (isLocked) "EXPIRED / LOCKED" else (rider?.subscriptionStatus ?: "INACTIVE"),
                  fontWeight = FontWeight.ExtraBold,
                  fontSize = 13.sp,
                  color = statusColor
                )
              }
            }

            Surface(
              shape = RoundedCornerShape(10.dp),
              color = MaterialTheme.colorScheme.surface.copy(alpha = 0.6f),
              modifier = Modifier.weight(1f)
            ) {
              Column(
                modifier = Modifier.padding(10.dp),
                horizontalAlignment = Alignment.CenterHorizontally
              ) {
                Text(text = "REMAINING", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text(
                  text = if (isLocked) "0 Days" else "$daysRemaining Days",
                  fontWeight = FontWeight.ExtraBold,
                  fontSize = 13.sp,
                  color = if (daysRemaining <= 3 && !isLocked) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface
                )
              }
            }

            Surface(
              shape = RoundedCornerShape(10.dp),
              color = MaterialTheme.colorScheme.surface.copy(alpha = 0.6f),
              modifier = Modifier.weight(1f)
            ) {
              Column(
                modifier = Modifier.padding(10.dp),
                horizontalAlignment = Alignment.CenterHorizontally
              ) {
                Text(text = "FEE", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text(
                  text = "Rs. ${adminConfig.monthlySubscriptionFee.toInt()}",
                  fontWeight = FontWeight.ExtraBold,
                  fontSize = 13.sp,
                  color = CashGreen
                )
              }
            }
          }
        }
      }
    }

    // Locked Warning Banner
    if (isLocked) {
      item {
        Card(
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(16.dp),
          colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer),
          border = BorderStroke(1.5.dp, MaterialTheme.colorScheme.error)
        ) {
          Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(
                imageVector = Icons.Default.Warning,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.error,
                modifier = Modifier.size(24.dp)
              )
              Spacer(modifier = Modifier.width(8.dp))
              Text(
                text = "Your subscription has expired!",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.onErrorContainer
              )
            }

            Text(
              text = "Renew your subscription for Rs. 100 to continue using the Bike Ride Service application.\n\nYour previous business records are safe. Renew to restore access.",
              style = MaterialTheme.typography.bodyMedium,
              color = MaterialTheme.colorScheme.onErrorContainer
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Download records backup button
            Button(
              onClick = {
                val csv = viewModel.getBusinessReportCsv()
                shareCsv(
                  context = context,
                  csvContent = csv,
                  fileName = "sasti_sawari_backup_${rider?.name?.replace(" ", "_") ?: "rider"}.csv"
                )
              },
              modifier = Modifier
                .fillMaxWidth()
                .testTag("download_backup_button"),
              shape = RoundedCornerShape(12.dp),
              colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
            ) {
              Icon(Icons.Default.Download, contentDescription = null, modifier = Modifier.size(18.dp))
              Spacer(modifier = Modifier.width(8.dp))
              Text("Download Business Records Backup (CSV)", fontWeight = FontWeight.Bold)
            }
          }
        }
      }
    } else if (daysRemaining in 1..7) {
      // Expiry reminder banner
      item {
        Card(
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(14.dp),
          colors = CardDefaults.cardColors(containerColor = ImperialGold.copy(alpha = 0.15f)),
          border = BorderStroke(1.dp, ImperialGold)
        ) {
          Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Icon(
              imageVector = Icons.Default.Timer,
              contentDescription = null,
              tint = ImperialGold,
              modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Column {
              Text(
                text = "Subscription Expiring Soon!",
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
              )
              Text(
                text = "Only $daysRemaining days left. Pay Rs. 100 early to prevent service interruption.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
            }
          }
        }
      }
    }

    // Payment Account Details
    item {
      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
      ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
          Text(
            text = "Official Admin Payment Accounts",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
          )
          Text(
            text = "Transfer Rs. ${adminConfig.monthlySubscriptionFee.toInt()} via JazzCash, Easypaisa, or Bank to activate for 30 days:",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )

          // JazzCash Card
          PaymentAccountRow(
            title = "JazzCash",
            number = adminConfig.jazzCashNumber,
            accountTitle = adminConfig.jazzCashTitle,
            onCopy = {
              clipboardManager.setText(AnnotatedString(adminConfig.jazzCashNumber))
              scope.launch { snackbarHostState.showSnackbar("JazzCash number copied to clipboard!") }
            }
          )

          // Easypaisa Card
          PaymentAccountRow(
            title = "Easypaisa",
            number = adminConfig.easypaisaNumber,
            accountTitle = adminConfig.easypaisaTitle,
            onCopy = {
              clipboardManager.setText(AnnotatedString(adminConfig.easypaisaNumber))
              scope.launch { snackbarHostState.showSnackbar("Easypaisa number copied to clipboard!") }
            }
          )

          // Bank Card
          PaymentAccountRow(
            title = adminConfig.bankName,
            number = adminConfig.bankIban,
            accountTitle = adminConfig.bankAccountTitle,
            onCopy = {
              clipboardManager.setText(AnnotatedString(adminConfig.bankIban))
              scope.launch { snackbarHostState.showSnackbar("Bank IBAN copied to clipboard!") }
            }
          )
        }
      }
    }

    // Payment Submission Form
    item {
      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
      ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
          Text(
            text = "Submit Payment Verification",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
          )

          // Payment Method Selector
          Text(
            text = "Payment Method Used",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            listOf("JazzCash", "Easypaisa", "Bank").forEach { method ->
              val isSelected = paymentMethod == method
              FilterChip(
                selected = isSelected,
                onClick = { paymentMethod = method },
                label = { Text(method) },
                leadingIcon = if (isSelected) {
                  { Icon(Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(16.dp)) }
                } else null,
                colors = FilterChipDefaults.filterChipColors(
                  selectedContainerColor = MaterialTheme.colorScheme.primary,
                  selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                ),
                shape = RoundedCornerShape(10.dp)
              )
            }
          }

          // Transaction ID Input
          OutlinedTextField(
            value = paymentTxnId,
            onValueChange = { paymentTxnId = it },
            label = { Text("Transaction ID / TID *") },
            placeholder = { Text("e.g. TXN-8941203") },
            modifier = Modifier
              .fillMaxWidth()
              .testTag("payment_txnid_input"),
            shape = RoundedCornerShape(12.dp),
            singleLine = true
          )

          // Screenshot Attachment Button
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            OutlinedButton(
              onClick = {
                photoPickerLauncher.launch(
                  PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                )
              },
              shape = RoundedCornerShape(12.dp)
            ) {
              Icon(Icons.Default.Payment, contentDescription = null, modifier = Modifier.size(18.dp))
              Spacer(modifier = Modifier.width(6.dp))
              Text(if (paymentProofUri.isBlank()) "Attach Screenshot" else "Screenshot Attached ✓")
            }

            if (paymentProofUri.isNotBlank()) {
              Text(
                text = "Proof Attached",
                color = CashGreen,
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp
              )
            }
          }

          // Submit Button
          Button(
            onClick = {
              if (paymentTxnId.isBlank()) {
                scope.launch {
                  snackbarHostState.showSnackbar("Please enter your payment Transaction ID.")
                }
                return@Button
              }
              isSubmittingPayment = true
              viewModel.submitRiderPayment(
                txnId = paymentTxnId,
                paymentMethod = paymentMethod,
                proofUri = paymentProofUri
              ) { success, msg ->
                isSubmittingPayment = false
                scope.launch {
                  snackbarHostState.showSnackbar(msg)
                }
                if (success) {
                  paymentTxnId = ""
                  paymentProofUri = ""
                }
              }
            },
            modifier = Modifier
              .fillMaxWidth()
              .height(52.dp)
              .testTag("submit_payment_button"),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
          ) {
            Icon(Icons.Default.Send, contentDescription = null, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              text = "SUBMIT PAYMENT FOR APPROVAL",
              fontWeight = FontWeight.Bold,
              fontSize = 14.sp
            )
          }

          Text(
            text = "Note: Submitting a transaction ID submits a request to the Admin. The subscription activates for 30 days immediately upon Admin approval.",
            fontSize = 11.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
          )
        }
      }
    }

    // Switch or Register Rider Account
    item {
      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
      ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
          Text(
            text = "Rider Account Management",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
          )

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
          ) {
            OutlinedButton(
              onClick = { showRegisterDialog = true },
              modifier = Modifier.weight(1f),
              shape = RoundedCornerShape(12.dp)
            ) {
              Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
              Spacer(modifier = Modifier.width(4.dp))
              Text("Register New", fontSize = 12.sp)
            }

            OutlinedButton(
              onClick = { showLoginDialog = true },
              modifier = Modifier.weight(1f),
              shape = RoundedCornerShape(12.dp)
            ) {
              Icon(Icons.Default.Person, contentDescription = null, modifier = Modifier.size(16.dp))
              Spacer(modifier = Modifier.width(4.dp))
              Text("Login / Switch", fontSize = 12.sp)
            }
          }
        }
      }
    }

    // Test Mode Simulation Toggle
    item {
      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
      ) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Column(modifier = Modifier.weight(1f)) {
            Text(
              text = "Simulate Expired Subscription",
              fontWeight = FontWeight.Bold,
              style = MaterialTheme.typography.titleSmall
            )
            Text(
              text = "Test mode to verify 30-day auto-lock and renewal behavior immediately.",
              fontSize = 11.sp,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }

          Switch(
            checked = adminConfig.isTestModeSimulateExpired,
            onCheckedChange = { viewModel.toggleSimulateExpired(it) },
            modifier = Modifier.testTag("test_mode_toggle")
          )
        }
      }
    }

    item {
      Spacer(modifier = Modifier.height(80.dp))
    }
  }

  // Register Dialog
  if (showRegisterDialog) {
    var regName by remember { mutableStateOf("") }
    var regPhone by remember { mutableStateOf("") }
    var regCityId by remember { mutableStateOf("multan") }
    var regPassword by remember { mutableStateOf("") }
    var regBikeModel by remember { mutableStateOf("Honda CD 70") }
    var regPlate by remember { mutableStateOf("MNK-4821") }

    AlertDialog(
      onDismissRequest = { showRegisterDialog = false },
      title = { Text("Register Rider Account", fontWeight = FontWeight.Bold) },
      text = {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
          OutlinedTextField(
            value = regName,
            onValueChange = { regName = it },
            label = { Text("Full Name *") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
          )
          OutlinedTextField(
            value = regPhone,
            onValueChange = { regPhone = it },
            label = { Text("Mobile Number (0300...) *") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
          )
          OutlinedTextField(
            value = regPassword,
            onValueChange = { regPassword = it },
            label = { Text("Password *") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
          )
          OutlinedTextField(
            value = regBikeModel,
            onValueChange = { regBikeModel = it },
            label = { Text("Bike Model") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
          )
          OutlinedTextField(
            value = regPlate,
            onValueChange = { regPlate = it },
            label = { Text("Number Plate") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
          )
        }
      },
      confirmButton = {
        Button(
          onClick = {
            if (regName.isBlank() || regPhone.isBlank() || regPassword.isBlank()) {
              scope.launch { snackbarHostState.showSnackbar("Please fill all required fields.") }
              return@Button
            }
            viewModel.registerRider(
              name = regName,
              phone = regPhone,
              cityId = regCityId,
              password = regPassword,
              bikeModel = regBikeModel,
              plate = regPlate
            ) { success, msg, id ->
              scope.launch { snackbarHostState.showSnackbar(msg) }
              if (success) {
                showRegisterDialog = false
              }
            }
          }
        ) {
          Text("Register & Get ID")
        }
      },
      dismissButton = {
        TextButton(onClick = { showRegisterDialog = false }) {
          Text("Cancel")
        }
      }
    )
  }

  // Login Dialog
  if (showLoginDialog) {
    var logPhone by remember { mutableStateOf("") }
    var logPassword by remember { mutableStateOf("") }

    AlertDialog(
      onDismissRequest = { showLoginDialog = false },
      title = { Text("Rider Login", fontWeight = FontWeight.Bold) },
      text = {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
          OutlinedTextField(
            value = logPhone,
            onValueChange = { logPhone = it },
            label = { Text("Registered Mobile Number") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
          )
          OutlinedTextField(
            value = logPassword,
            onValueChange = { logPassword = it },
            label = { Text("Password") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
          )
        }
      },
      confirmButton = {
        Button(
          onClick = {
            if (logPhone.isBlank() || logPassword.isBlank()) {
              scope.launch { snackbarHostState.showSnackbar("Please enter phone and password.") }
              return@Button
            }
            viewModel.loginRider(logPhone, logPassword) { success, msg ->
              scope.launch { snackbarHostState.showSnackbar(msg) }
              if (success) {
                showLoginDialog = false
              }
            }
          }
        ) {
          Text("Login")
        }
      },
      dismissButton = {
        TextButton(onClick = { showLoginDialog = false }) {
          Text("Cancel")
        }
      }
    )
  }
}

@Composable
fun PaymentAccountRow(
  title: String,
  number: String,
  accountTitle: String,
  onCopy: () -> Unit
) {
  Surface(
    shape = RoundedCornerShape(12.dp),
    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
    modifier = Modifier.fillMaxWidth()
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(12.dp),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Column(modifier = Modifier.weight(1f)) {
        Text(
          text = title,
          fontWeight = FontWeight.Bold,
          style = MaterialTheme.typography.labelLarge,
          color = MaterialTheme.colorScheme.primary
        )
        Text(
          text = number,
          style = MaterialTheme.typography.titleMedium,
          fontWeight = FontWeight.ExtraBold
        )
        Text(
          text = "Title: $accountTitle",
          fontSize = 11.sp,
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )
      }

      IconButton(onClick = onCopy) {
        Icon(
          imageVector = Icons.Default.ContentCopy,
          contentDescription = "Copy number",
          tint = MaterialTheme.colorScheme.primary
        )
      }
    }
  }
}

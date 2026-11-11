package com.example.ui.screens

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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.DirectionsBike
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.TwoWheeler
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.MainViewModel
import com.example.ui.theme.CashGreen
import com.example.ui.theme.ImperialGold
import kotlinx.coroutines.launch

@Composable
fun AccountProtocolScreen(
  viewModel: MainViewModel,
  snackbarHostState: SnackbarHostState,
  modifier: Modifier = Modifier
) {
  val userRole by viewModel.userRole.collectAsStateWithLifecycle()
  var targetProtocol by remember { mutableStateOf(userRole.ifBlank { "NONE" }) }

  // Sync if external role changes
  if (userRole != "NONE" && targetProtocol == "NONE") {
    targetProtocol = userRole
  }

  when (targetProtocol) {
    "RIDER" -> {
      RiderProtocolView(
        viewModel = viewModel,
        snackbarHostState = snackbarHostState,
        onBackToSelector = {
          targetProtocol = "NONE"
          viewModel.resetRoleToSelector()
        },
        modifier = modifier
      )
    }

    "PASSENGER" -> {
      PassengerProtocolView(
        viewModel = viewModel,
        snackbarHostState = snackbarHostState,
        onBackToSelector = {
          targetProtocol = "NONE"
          viewModel.resetRoleToSelector()
        },
        modifier = modifier
      )
    }

    "ADMIN" -> {
      AdminProtocolView(
        viewModel = viewModel,
        snackbarHostState = snackbarHostState,
        onBackToSelector = {
          targetProtocol = "NONE"
          viewModel.resetRoleToSelector()
        },
        modifier = modifier
      )
    }

    else -> {
      AccountProtocolSelector(
        onSelectRole = { role ->
          targetProtocol = role
        },
        modifier = modifier
      )
    }
  }
}

@Composable
fun AccountProtocolSelector(
  onSelectRole: (String) -> Unit,
  modifier: Modifier = Modifier
) {
  LazyColumn(
    modifier = modifier
      .fillMaxSize()
      .padding(20.dp),
    verticalArrangement = Arrangement.spacedBy(16.dp),
    horizontalAlignment = Alignment.CenterHorizontally
  ) {
    item {
      Spacer(modifier = Modifier.height(16.dp))
      Surface(
        shape = CircleShape,
        color = MaterialTheme.colorScheme.primaryContainer,
        modifier = Modifier.size(76.dp)
      ) {
        Box(contentAlignment = Alignment.Center) {
          Icon(
            imageVector = Icons.Default.TwoWheeler,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(42.dp)
          )
        }
      }
      Spacer(modifier = Modifier.height(12.dp))
      Text(
        text = "Sasti Sawari Pakistan",
        style = MaterialTheme.typography.headlineSmall,
        fontWeight = FontWeight.ExtraBold,
        textAlign = TextAlign.Center
      )
      Text(
        text = "Account Profile Protocols",
        style = MaterialTheme.typography.titleMedium,
        color = MaterialTheme.colorScheme.primary,
        fontWeight = FontWeight.Bold,
        textAlign = TextAlign.Center
      )
      Text(
        text = "Please complete the profile protocol for your account type to proceed with verified services.",
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        textAlign = TextAlign.Center,
        modifier = Modifier.padding(top = 4.dp, bottom = 8.dp)
      )
    }

    // Role Option 1: Rider
    item {
      ProtocolOptionCard(
        title = "Rider Account Protocol",
        subtitle = "Motorcycle Captain & Stand Operator",
        description = "Fare calculator, ride booking, daily financial records, customer log, and 30-day subscription (Rs. 100/mo).",
        icon = Icons.Default.TwoWheeler,
        badge = "Captains Only",
        accentColor = MaterialTheme.colorScheme.primary,
        testTag = "role_select_rider_card",
        onClick = { onSelectRole("RIDER") }
      )
    }

    // Role Option 2: Passenger
    item {
      ProtocolOptionCard(
        title = "Passenger Account Protocol",
        subtitle = "Commuter & Daily Traveller",
        description = "Search verified stand riders across Multan, Lahore, Islamabad & Karachi. Check transparent fares and call riders.",
        icon = Icons.Default.Person,
        badge = "Passengers",
        accentColor = CashGreen,
        testTag = "role_select_passenger_card",
        onClick = { onSelectRole("PASSENGER") }
      )
    }

    // Role Option 3: Admin
    item {
      ProtocolOptionCard(
        title = "Admin Portal Protocol",
        subtitle = "Platform Administrator (Shahid)",
        description = "Verify Rs. 100 subscription payments, manage registered riders, cycle locking, and configure payment accounts.",
        icon = Icons.Default.AdminPanelSettings,
        badge = "Restricted",
        accentColor = ImperialGold,
        testTag = "role_select_admin_card",
        onClick = { onSelectRole("ADMIN") }
      )
    }

    item {
      Spacer(modifier = Modifier.height(16.dp))
      Text(
        text = "Sasti Sawari • Direct Motorcycle Stand Network of Pakistan",
        style = MaterialTheme.typography.labelSmall,
        color = MaterialTheme.colorScheme.outline
      )
    }
  }
}

@Composable
fun ProtocolOptionCard(
  title: String,
  subtitle: String,
  description: String,
  icon: ImageVector,
  badge: String,
  accentColor: Color,
  testTag: String,
  onClick: () -> Unit
) {
  Card(
    modifier = Modifier
      .fillMaxWidth()
      .clickable { onClick() }
      .testTag(testTag),
    shape = RoundedCornerShape(20.dp),
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    border = BorderStroke(1.dp, accentColor.copy(alpha = 0.4f))
  ) {
    Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Surface(
            shape = RoundedCornerShape(12.dp),
            color = accentColor.copy(alpha = 0.15f),
            modifier = Modifier.size(44.dp)
          ) {
            Box(contentAlignment = Alignment.Center) {
              Icon(imageVector = icon, contentDescription = null, tint = accentColor, modifier = Modifier.size(24.dp))
            }
          }
          Spacer(modifier = Modifier.width(12.dp))
          Column {
            Text(text = title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Text(text = subtitle, style = MaterialTheme.typography.labelMedium, color = accentColor, fontWeight = FontWeight.SemiBold)
          }
        }

        Surface(
          shape = RoundedCornerShape(8.dp),
          color = accentColor.copy(alpha = 0.15f)
        ) {
          Text(
            text = badge,
            color = accentColor,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
          )
        }
      }

      Text(
        text = description,
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant
      )

      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(top = 4.dp),
        horizontalArrangement = Arrangement.End,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = "Complete Protocol",
          style = MaterialTheme.typography.labelMedium,
          color = accentColor,
          fontWeight = FontWeight.Bold
        )
        Icon(
          imageVector = Icons.Default.ChevronRight,
          contentDescription = null,
          tint = accentColor,
          modifier = Modifier.size(18.dp)
        )
      }
    }
  }
}

// -------------------------------------------------------------
// RIDER PROTOCOL VIEW (LOGIN & REGISTRATION)
// -------------------------------------------------------------
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RiderProtocolView(
  viewModel: MainViewModel,
  snackbarHostState: SnackbarHostState,
  onBackToSelector: () -> Unit,
  modifier: Modifier = Modifier
) {
  val scope = rememberCoroutineScope()
  val allCities by viewModel.serviceCities.collectAsStateWithLifecycle()
  var tabIndex by remember { mutableIntStateOf(0) }

  // Login Form
  var loginPhone by remember { mutableStateOf("") }
  var loginPassword by remember { mutableStateOf("") }

  // Registration Form
  var regName by remember { mutableStateOf("") }
  var regPhone by remember { mutableStateOf("") }
  var regEmail by remember { mutableStateOf("") }
  var regCityId by remember { mutableStateOf(allCities.firstOrNull()?.id ?: "multan") }
  var regBikeModel by remember { mutableStateOf("Honda CD 70") }
  var regPlate by remember { mutableStateOf("") }
  var regStandName by remember { mutableStateOf("") }
  var regPassword by remember { mutableStateOf("") }
  var cityMenuExpanded by remember { mutableStateOf(false) }

  LazyColumn(
    modifier = modifier
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
        OutlinedButton(
          onClick = onBackToSelector,
          shape = RoundedCornerShape(10.dp)
        ) {
          Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null, modifier = Modifier.size(16.dp))
          Spacer(modifier = Modifier.width(4.dp))
          Text("Account Types", fontSize = 12.sp)
        }

        Surface(
          shape = RoundedCornerShape(8.dp),
          color = MaterialTheme.colorScheme.primaryContainer
        ) {
          Text(
            text = "Rider Protocol",
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Bold,
            fontSize = 11.sp,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
          )
        }
      }
    }

    item {
      Text(
        text = "Rider Account Protocol",
        style = MaterialTheme.typography.titleLarge,
        fontWeight = FontWeight.ExtraBold
      )
      Text(
        text = "Authenticate or complete your Captain Profile protocol to access fare calculator and subscription tools.",
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant
      )
    }

    item {
      TabRow(selectedTabIndex = tabIndex) {
        Tab(
          selected = tabIndex == 0,
          onClick = { tabIndex = 0 },
          text = { Text("Captain Login", fontWeight = FontWeight.Bold) }
        )
        Tab(
          selected = tabIndex == 1,
          onClick = { tabIndex = 1 },
          text = { Text("Register Profile", fontWeight = FontWeight.Bold) }
        )
      }
    }

    if (tabIndex == 0) {
      // LOGIN TAB
      item {
        Card(
          shape = RoundedCornerShape(16.dp),
          colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
          border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
        ) {
          Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text(text = "Rider Account Login", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)

            OutlinedTextField(
              value = loginPhone,
              onValueChange = { loginPhone = it },
              label = { Text("Registered Mobile Number") },
              placeholder = { Text("0300-1234567") },
              leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null) },
              modifier = Modifier
                .fillMaxWidth()
                .testTag("rider_login_phone_input"),
              shape = RoundedCornerShape(12.dp),
              keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
              singleLine = true
            )

            OutlinedTextField(
              value = loginPassword,
              onValueChange = { loginPassword = it },
              label = { Text("Password") },
              leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
              visualTransformation = PasswordVisualTransformation(),
              modifier = Modifier
                .fillMaxWidth()
                .testTag("rider_login_password_input"),
              shape = RoundedCornerShape(12.dp),
              singleLine = true
            )

            Button(
              onClick = {
                if (loginPhone.isBlank() || loginPassword.isBlank()) {
                  scope.launch { snackbarHostState.showSnackbar("Please enter both phone and password") }
                  return@Button
                }
                viewModel.loginRider(loginPhone, loginPassword) { success, msg ->
                  scope.launch { snackbarHostState.showSnackbar(msg) }
                }
              },
              modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .testTag("rider_login_submit_button"),
              shape = RoundedCornerShape(12.dp)
            ) {
              Text("LOGIN TO CAPTAIN DASHBOARD", fontWeight = FontWeight.Bold)
            }
          }
        }
      }
    } else {
      // REGISTRATION TAB
      item {
        Card(
          shape = RoundedCornerShape(16.dp),
          colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
          border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
        ) {
          Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text(text = "Captain Profile Registration", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)

            OutlinedTextField(
              value = regName,
              onValueChange = { regName = it },
              label = { Text("Full Name *") },
              placeholder = { Text("e.g. Muhammad Rashid") },
              leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
              modifier = Modifier
                .fillMaxWidth()
                .testTag("rider_reg_name_input"),
              shape = RoundedCornerShape(12.dp),
              singleLine = true
            )

            OutlinedTextField(
              value = regPhone,
              onValueChange = { regPhone = it },
              label = { Text("Mobile Number (WhatsApp) *") },
              placeholder = { Text("0308-5142950") },
              leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null) },
              modifier = Modifier
                .fillMaxWidth()
                .testTag("rider_reg_phone_input"),
              shape = RoundedCornerShape(12.dp),
              keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
              singleLine = true
            )

            OutlinedTextField(
              value = regEmail,
              onValueChange = { regEmail = it },
              label = { Text("Email Address (Optional)") },
              placeholder = { Text("e.g. rider@gmail.com") },
              leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
              modifier = Modifier
                .fillMaxWidth()
                .testTag("rider_reg_email_input"),
              shape = RoundedCornerShape(12.dp),
              singleLine = true
            )

            // City picker
            val selectedCity = allCities.firstOrNull { it.id == regCityId } ?: allCities.first()
            ExposedDropdownMenuBox(
              expanded = cityMenuExpanded,
              onExpandedChange = { cityMenuExpanded = it }
            ) {
              OutlinedTextField(
                value = "${selectedCity.name} (${selectedCity.urduName})",
                onValueChange = {},
                readOnly = true,
                label = { Text("Service City *") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = cityMenuExpanded) },
                modifier = Modifier
                  .fillMaxWidth()
                  .menuAnchor(),
                shape = RoundedCornerShape(12.dp)
              )
              ExposedDropdownMenu(
                expanded = cityMenuExpanded,
                onDismissRequest = { cityMenuExpanded = false }
              ) {
                allCities.forEach { city ->
                  DropdownMenuItem(
                    text = { Text("${city.name} (${city.urduName})") },
                    onClick = {
                      cityMenuExpanded = false
                      regCityId = city.id
                    }
                  )
                }
              }
            }

            OutlinedTextField(
              value = regBikeModel,
              onValueChange = { regBikeModel = it },
              label = { Text("Motorcycle Model") },
              placeholder = { Text("Honda CD 70 / CG 125") },
              leadingIcon = { Icon(Icons.Default.DirectionsBike, contentDescription = null) },
              modifier = Modifier.fillMaxWidth(),
              shape = RoundedCornerShape(12.dp),
              singleLine = true
            )

            OutlinedTextField(
              value = regPlate,
              onValueChange = { regPlate = it },
              label = { Text("Number Plate") },
              placeholder = { Text("MNK-4821") },
              modifier = Modifier.fillMaxWidth(),
              shape = RoundedCornerShape(12.dp),
              singleLine = true
            )

            OutlinedTextField(
              value = regStandName,
              onValueChange = { regStandName = it },
              label = { Text("Stand / Operating Area") },
              placeholder = { Text("Ghanta Ghar / Cantt Stand") },
              modifier = Modifier.fillMaxWidth(),
              shape = RoundedCornerShape(12.dp),
              singleLine = true
            )

            OutlinedTextField(
              value = regPassword,
              onValueChange = { regPassword = it },
              label = { Text("Account Password *") },
              leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
              visualTransformation = PasswordVisualTransformation(),
              modifier = Modifier
                .fillMaxWidth()
                .testTag("rider_reg_password_input"),
              shape = RoundedCornerShape(12.dp),
              singleLine = true
            )

            Surface(
              shape = RoundedCornerShape(10.dp),
              color = ImperialGold.copy(alpha = 0.15f),
              border = BorderStroke(1.dp, ImperialGold.copy(alpha = 0.35f))
            ) {
              Text(
                text = "ℹ A unique Rider ID (e.g. SW-1001) is automatically generated. Access requires Rs. 100/30-day subscription approval.",
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(10.dp)
              )
            }

            Button(
              onClick = {
                if (regName.isBlank() || regPhone.isBlank() || regPassword.isBlank()) {
                  scope.launch { snackbarHostState.showSnackbar("Please fill in Name, Phone, and Password.") }
                  return@Button
                }
                viewModel.registerRider(
                  name = regName,
                  phone = regPhone,
                  cityId = regCityId,
                  password = regPassword,
                  bikeModel = regBikeModel,
                  plate = regPlate,
                  standName = regStandName,
                  email = regEmail
                ) { success, msg, newId ->
                  scope.launch { snackbarHostState.showSnackbar(msg) }
                }
              },
              modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .testTag("rider_register_submit_button"),
              shape = RoundedCornerShape(12.dp)
            ) {
              Text("COMPLETE RIDER PROTOCOL & REGISTER", fontWeight = FontWeight.Bold)
            }
          }
        }
      }
    }
  }
}

// -------------------------------------------------------------
// PASSENGER PROTOCOL VIEW
// -------------------------------------------------------------
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PassengerProtocolView(
  viewModel: MainViewModel,
  snackbarHostState: SnackbarHostState,
  onBackToSelector: () -> Unit,
  modifier: Modifier = Modifier
) {
  val scope = rememberCoroutineScope()
  val allCities by viewModel.serviceCities.collectAsStateWithLifecycle()
  val currentPassenger by viewModel.currentPassenger.collectAsStateWithLifecycle()

  var name by remember { mutableStateOf(currentPassenger.name) }
  var phone by remember { mutableStateOf(currentPassenger.phone) }
  var selectedCityId by remember { mutableStateOf(currentPassenger.cityId.ifBlank { "multan" }) }
  var cityDropdownExpanded by remember { mutableStateOf(false) }

  LazyColumn(
    modifier = modifier
      .fillMaxSize()
      .padding(16.dp),
    verticalArrangement = Arrangement.spacedBy(16.dp)
  ) {
    item {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        OutlinedButton(
          onClick = onBackToSelector,
          shape = RoundedCornerShape(10.dp)
        ) {
          Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null, modifier = Modifier.size(16.dp))
          Spacer(modifier = Modifier.width(4.dp))
          Text("Account Types", fontSize = 12.sp)
        }

        Surface(
          shape = RoundedCornerShape(8.dp),
          color = CashGreen.copy(alpha = 0.15f)
        ) {
          Text(
            text = "Passenger Protocol",
            color = CashGreen,
            fontWeight = FontWeight.Bold,
            fontSize = 11.sp,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
          )
        }
      }
    }

    item {
      Text(
        text = "Passenger Account Protocol",
        style = MaterialTheme.typography.titleLarge,
        fontWeight = FontWeight.ExtraBold
      )
      Text(
        text = "Enter your commuter profile details to search motorcycle captains and verified stand rides in your area.",
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant
      )
    }

    item {
      Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, CashGreen.copy(alpha = 0.4f))
      ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
          OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Passenger Full Name *") },
            placeholder = { Text("e.g. Ahmad Ali") },
            leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = CashGreen) },
            modifier = Modifier
              .fillMaxWidth()
              .testTag("passenger_name_input"),
            shape = RoundedCornerShape(12.dp),
            singleLine = true
          )

          OutlinedTextField(
            value = phone,
            onValueChange = { phone = it },
            label = { Text("Mobile Contact Number *") },
            placeholder = { Text("0301-2345678") },
            leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null, tint = CashGreen) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
            modifier = Modifier
              .fillMaxWidth()
              .testTag("passenger_phone_input"),
            shape = RoundedCornerShape(12.dp),
            singleLine = true
          )

          val selectedCity = allCities.firstOrNull { it.id == selectedCityId } ?: allCities.first()
          ExposedDropdownMenuBox(
            expanded = cityDropdownExpanded,
            onExpandedChange = { cityDropdownExpanded = it }
          ) {
            OutlinedTextField(
              value = "${selectedCity.name} (${selectedCity.urduName})",
              onValueChange = {},
              readOnly = true,
              label = { Text("Current City *") },
              trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = cityDropdownExpanded) },
              modifier = Modifier
                .fillMaxWidth()
                .menuAnchor(),
              shape = RoundedCornerShape(12.dp)
            )
            ExposedDropdownMenu(
              expanded = cityDropdownExpanded,
              onDismissRequest = { cityDropdownExpanded = false }
            ) {
              allCities.forEach { city ->
                DropdownMenuItem(
                  text = { Text("${city.name} (${city.urduName})") },
                  onClick = {
                    cityDropdownExpanded = false
                    selectedCityId = city.id
                  }
                )
              }
            }
          }

          Button(
            onClick = {
              if (name.isBlank() || phone.isBlank()) {
                scope.launch { snackbarHostState.showSnackbar("Please enter your name and phone number.") }
                return@Button
              }
              viewModel.savePassengerProfile(name, phone, selectedCityId)
              scope.launch { snackbarHostState.showSnackbar("Welcome $name! Passenger profile established.") }
            },
            colors = ButtonDefaults.buttonColors(containerColor = CashGreen),
            modifier = Modifier
              .fillMaxWidth()
              .height(50.dp)
              .testTag("passenger_continue_button"),
            shape = RoundedCornerShape(12.dp)
          ) {
            Icon(Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text("PROCEED AS PASSENGER", fontWeight = FontWeight.Bold)
          }
        }
      }
    }
  }
}

// -------------------------------------------------------------
// ADMIN PROTOCOL VIEW (EMAIL & PASSWORD)
// -------------------------------------------------------------
@Composable
fun AdminProtocolView(
  viewModel: MainViewModel,
  snackbarHostState: SnackbarHostState,
  onBackToSelector: () -> Unit,
  modifier: Modifier = Modifier
) {
  val scope = rememberCoroutineScope()
  var email by remember { mutableStateOf("shahid.msc2020@gmail.com") }
  var password by remember { mutableStateOf("") }
  var passwordVisible by remember { mutableStateOf(false) }

  LazyColumn(
    modifier = modifier
      .fillMaxSize()
      .padding(16.dp),
    verticalArrangement = Arrangement.spacedBy(16.dp)
  ) {
    item {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        OutlinedButton(
          onClick = onBackToSelector,
          shape = RoundedCornerShape(10.dp)
        ) {
          Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null, modifier = Modifier.size(16.dp))
          Spacer(modifier = Modifier.width(4.dp))
          Text("Account Types", fontSize = 12.sp)
        }

        Surface(
          shape = RoundedCornerShape(8.dp),
          color = ImperialGold.copy(alpha = 0.2f)
        ) {
          Text(
            text = "Admin Protocol",
            color = ImperialGold,
            fontWeight = FontWeight.Bold,
            fontSize = 11.sp,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
          )
        }
      }
    }

    item {
      Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, ImperialGold.copy(alpha = 0.5f))
      ) {
        Column(
          modifier = Modifier.padding(20.dp),
          horizontalAlignment = Alignment.CenterHorizontally,
          verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
          Surface(
            shape = CircleShape,
            color = ImperialGold.copy(alpha = 0.15f),
            modifier = Modifier.size(64.dp)
          ) {
            Box(contentAlignment = Alignment.Center) {
              Icon(
                imageVector = Icons.Default.AdminPanelSettings,
                contentDescription = null,
                tint = ImperialGold,
                modifier = Modifier.size(36.dp)
              )
            }
          }

          Text(
            text = "Platform Administrator Login",
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
                text = "Default Admin Credentials:",
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
            value = email,
            onValueChange = { email = it },
            label = { Text("Admin Email") },
            leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
            modifier = Modifier
              .fillMaxWidth()
              .testTag("admin_email_input"),
            shape = RoundedCornerShape(12.dp),
            singleLine = true
          )

          OutlinedTextField(
            value = password,
            onValueChange = { password = it },
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
            modifier = Modifier
              .fillMaxWidth()
              .testTag("admin_password_input"),
            shape = RoundedCornerShape(12.dp),
            singleLine = true
          )

          Button(
            onClick = {
              val success = viewModel.loginAdmin(email, password)
              if (success) {
                scope.launch { snackbarHostState.showSnackbar("Welcome Shahid! Administrator access granted.") }
              } else {
                scope.launch {
                  snackbarHostState.showSnackbar("Invalid Admin Credentials. (Default: shahid.msc2020@gmail.com / Khan@3636)")
                }
              }
            },
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
            modifier = Modifier
              .fillMaxWidth()
              .height(50.dp)
              .testTag("admin_login_submit_button"),
            shape = RoundedCornerShape(12.dp)
          ) {
            Icon(Icons.Default.Lock, contentDescription = null, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text("AUTHENTICATE AS ADMIN", fontWeight = FontWeight.Bold)
          }
        }
      }
    }
  }
}

package com.example.ui.screens

import android.content.Context
import android.content.Intent
import android.net.Uri
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.BorderStroke
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DirectionsBike
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.PinDrop
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Timer
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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.MainViewModel
import com.example.ui.theme.CashGreen
import com.example.ui.theme.EasypaisaTeal
import com.example.ui.theme.ImperialGold
import com.example.ui.theme.JazzCashOrange
import kotlinx.coroutines.launch

@Composable
fun NewRideScreen(
  viewModel: MainViewModel,
  snackbarHostState: SnackbarHostState,
  onRideSaved: () -> Unit,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  val settings by viewModel.settings.collectAsStateWithLifecycle()
  val city by viewModel.currentCity.collectAsStateWithLifecycle()
  val appTitle by viewModel.appTitleWithCity.collectAsStateWithLifecycle()
  val cityLocations by viewModel.currentCityLocations.collectAsStateWithLifecycle()
  val customers by viewModel.customers.collectAsStateWithLifecycle()
  val prefillDist by viewModel.prefillDistance.collectAsStateWithLifecycle()
  val prefillWait by viewModel.prefillWait.collectAsStateWithLifecycle()
  val prefillDest by viewModel.prefillDestination.collectAsStateWithLifecycle()

  val scope = rememberCoroutineScope()

  var destination by remember { mutableStateOf("") }
  var distanceText by remember { mutableStateOf("3.0") }
  var waitingText by remember { mutableStateOf("0") }
  var customerName by remember { mutableStateOf("") }
  var customerPhone by remember { mutableStateOf("") }
  var pickupLocation by remember { mutableStateOf(settings.standName) }
  var isAnotherPickupLocation by remember { mutableStateOf(false) }
  var saveDestinationToCity by remember { mutableStateOf(false) }
  var paymentMethod by remember { mutableStateOf("Cash") }
  var notes by remember { mutableStateOf("") }
  var fareOverrideText by remember { mutableStateOf("") }

  // Consume prefilled values if navigated from Fare Calculator
  LaunchedEffect(prefillDist, prefillWait, prefillDest) {
    if (prefillDist != null) {
      distanceText = prefillDist.toString()
    }
    if (prefillWait != null) {
      waitingText = prefillWait.toString()
    }
    if (prefillDest != null) {
      destination = prefillDest ?: ""
    }
    viewModel.clearPrefill()
  }

  // Update pickup if settings or city change
  LaunchedEffect(settings.standName) {
    if (pickupLocation.isBlank() || pickupLocation.contains("Stand")) {
      pickupLocation = settings.standName
    }
  }

  val distance = distanceText.toDoubleOrNull() ?: 0.0
  val waiting = waitingText.toIntOrNull() ?: 0
  val breakdown = viewModel.calculateFare(distance, waiting)
  val finalCalculatedFare = fareOverrideText.toDoubleOrNull() ?: breakdown.totalFare

  val quickDistances = listOf(1.5, 2.0, 3.0, 4.0, 5.0, 6.0, 8.0, 10.0, 12.0, 15.0)
  val quickWaiting = listOf(0, 5, 10, 15, 20)

  LazyColumn(
    modifier = modifier
      .fillMaxSize()
      .padding(horizontal = 16.dp),
    verticalArrangement = Arrangement.spacedBy(14.dp)
  ) {
    item {
      Spacer(modifier = Modifier.height(4.dp))
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column {
          Text(
            text = "Record New Ride",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
          )
          Text(
            text = "$appTitle • Under 30 seconds entry",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
        }

        Surface(
          shape = RoundedCornerShape(10.dp),
          color = MaterialTheme.colorScheme.primaryContainer
        ) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
          ) {
            Icon(
              imageVector = Icons.Default.DirectionsBike,
              contentDescription = null,
              tint = MaterialTheme.colorScheme.primary,
              modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
              text = viewModel.generateNextRideCode(),
              fontWeight = FontWeight.Bold,
              color = MaterialTheme.colorScheme.primary,
              fontSize = 13.sp
            )
          }
        }
      }
    }

    // 1. Pickup Location (Stand or Custom Another Location)
    item {
      Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(modifier = Modifier.padding(14.dp)) {
          Text(
            text = "Pickup Location (${city.name}) *",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold
          )
          Spacer(modifier = Modifier.height(8.dp))

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            Surface(
              shape = RoundedCornerShape(10.dp),
              color = if (!isAnotherPickupLocation) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant,
              border = if (!isAnotherPickupLocation) BorderStroke(1.5.dp, MaterialTheme.colorScheme.primary) else null,
              modifier = Modifier
                .weight(1f)
                .clickable {
                  isAnotherPickupLocation = false
                  pickupLocation = settings.standName
                }
            ) {
              Row(
                modifier = Modifier.padding(vertical = 10.dp, horizontal = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
              ) {
                Icon(
                  imageVector = Icons.Default.PinDrop,
                  contentDescription = null,
                  modifier = Modifier.size(16.dp),
                  tint = if (!isAnotherPickupLocation) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                  text = "At Fixed Stand",
                  fontWeight = if (!isAnotherPickupLocation) FontWeight.Bold else FontWeight.Medium,
                  fontSize = 12.sp,
                  color = if (!isAnotherPickupLocation) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                )
              }
            }

            Surface(
              shape = RoundedCornerShape(10.dp),
              color = if (isAnotherPickupLocation) ImperialGold.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surfaceVariant,
              border = if (isAnotherPickupLocation) BorderStroke(1.5.dp, ImperialGold) else null,
              modifier = Modifier
                .weight(1f)
                .clickable { isAnotherPickupLocation = true }
            ) {
              Row(
                modifier = Modifier.padding(vertical = 10.dp, horizontal = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
              ) {
                Icon(
                  imageVector = Icons.Default.DirectionsBike,
                  contentDescription = null,
                  modifier = Modifier.size(16.dp),
                  tint = if (isAnotherPickupLocation) ImperialGold else MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                  text = "Another Spot",
                  fontWeight = if (isAnotherPickupLocation) FontWeight.Bold else FontWeight.Medium,
                  fontSize = 12.sp,
                  color = if (isAnotherPickupLocation) ImperialGold else MaterialTheme.colorScheme.onSurfaceVariant
                )
              }
            }
          }

          Spacer(modifier = Modifier.height(10.dp))

          OutlinedTextField(
            value = pickupLocation,
            onValueChange = { pickupLocation = it },
            placeholder = { Text(if (isAnotherPickupLocation) "e.g. Chowk Kumharanwala, Cantt, Gate 2" else settings.standName) },
            leadingIcon = {
              Icon(Icons.Default.LocationOn, contentDescription = null, tint = if (isAnotherPickupLocation) ImperialGold else MaterialTheme.colorScheme.primary)
            },
            trailingIcon = {
              if (pickupLocation.isNotBlank()) {
                IconButton(onClick = {
                  val mapUri = Uri.parse("geo:0,0?q=${Uri.encode("$pickupLocation, ${city.name}, Pakistan")}")
                  context.startActivity(Intent(Intent.ACTION_VIEW, mapUri))
                }) {
                  Icon(Icons.Default.Map, contentDescription = "View on Maps", tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(18.dp))
                }
              }
            },
            singleLine = true,
            modifier = Modifier.fillMaxWidth().testTag("new_ride_pickup_input")
          )

          if (isAnotherPickupLocation) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
              text = "Or tap landmark to set pickup location:",
              style = MaterialTheme.typography.labelSmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(6.dp))
            LazyRow(
              horizontalArrangement = Arrangement.spacedBy(8.dp),
              modifier = Modifier.fillMaxWidth()
            ) {
              items(cityLocations) { place ->
                FilterChip(
                  selected = (pickupLocation == place.name),
                  onClick = { pickupLocation = place.name },
                  label = { Text(place.name, fontSize = 12.sp) }
                )
              }
            }
          }
        }
      }
    }

    // 2. Destination Field + Quick City Location Chips
    item {
      Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(modifier = Modifier.padding(14.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text(
              text = "Dropped Destination in ${city.name} *",
              style = MaterialTheme.typography.titleSmall,
              fontWeight = FontWeight.Bold
            )

            if (destination.isNotBlank()) {
              IconButton(
                onClick = {
                  val mapUri = Uri.parse("geo:0,0?q=${Uri.encode("$destination, ${city.name}, Pakistan")}")
                  context.startActivity(Intent(Intent.ACTION_VIEW, mapUri))
                },
                modifier = Modifier.size(28.dp)
              ) {
                Icon(
                  imageVector = Icons.Default.Map,
                  contentDescription = "Open in Maps",
                  tint = MaterialTheme.colorScheme.primary,
                  modifier = Modifier.size(18.dp)
                )
              }
            }
          }

          Spacer(modifier = Modifier.height(8.dp))

          OutlinedTextField(
            value = destination,
            onValueChange = { destination = it },
            placeholder = { Text("e.g. Hospital, University, Station, Market") },
            leadingIcon = {
              Icon(Icons.Default.PinDrop, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
            },
            singleLine = true,
            modifier = Modifier
              .fillMaxWidth()
              .testTag("new_ride_destination_input")
          )

          if (destination.isNotBlank()) {
            Spacer(modifier = Modifier.height(6.dp))
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.End
            ) {
              OutlinedButton(
                onClick = {
                  viewModel.addCustomDestination(
                    cityId = city.id,
                    name = destination,
                    category = "Popular",
                    approxDistanceKm = distance,
                    lat = city.centerLat,
                    lng = city.centerLng
                  )
                  saveDestinationToCity = true
                },
                shape = RoundedCornerShape(8.dp),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 8.dp, vertical = 4.dp)
              ) {
                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(14.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                  text = if (saveDestinationToCity) "Saved to Popular" else "Save to Popular Destinations",
                  fontSize = 11.sp,
                  fontWeight = FontWeight.Bold
                )
              }
            }
          }

          Spacer(modifier = Modifier.height(8.dp))
          Text(
            text = "Tap Common ${city.name} Drop-off Points:",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
          Spacer(modifier = Modifier.height(6.dp))

          LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
          ) {
            items(cityLocations.filter { it.name != pickupLocation }) { place ->
              FilterChip(
                selected = (destination == place.name),
                onClick = {
                  destination = place.name
                  if (place.approxDistanceKmFromStand > 0) {
                    distanceText = place.approxDistanceKmFromStand.toString()
                  }
                },
                label = { Text("${place.name} (${if (place.approxDistanceKmFromStand > 0) "${place.approxDistanceKmFromStand}km" else "Stand"})", fontSize = 12.sp) }
              )
            }
          }
        }
      }
    }


    // 2. Distance & Waiting Quick Inputs
    item {
      Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(modifier = Modifier.padding(14.dp)) {
          // Distance
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text(
              text = "Distance (km) *",
              style = MaterialTheme.typography.titleSmall,
              fontWeight = FontWeight.Bold
            )
            Text(
              text = "$distance km",
              style = MaterialTheme.typography.bodyMedium,
              fontWeight = FontWeight.Bold,
              color = MaterialTheme.colorScheme.primary
            )
          }

          Spacer(modifier = Modifier.height(6.dp))

          OutlinedTextField(
            value = distanceText,
            onValueChange = { distanceText = it },
            placeholder = { Text("3.0") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            leadingIcon = {
              Icon(Icons.Default.Speed, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
            },
            singleLine = true,
            modifier = Modifier
              .fillMaxWidth()
              .testTag("new_ride_distance_input")
          )

          Spacer(modifier = Modifier.height(6.dp))

          LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
          ) {
            items(quickDistances) { dist ->
              FilterChip(
                selected = (distance == dist),
                onClick = { distanceText = dist.toString() },
                label = { Text("${dist}km", fontSize = 12.sp) }
              )
            }
          }

          Spacer(modifier = Modifier.height(14.dp))

          // Waiting
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text(
              text = "Waiting Time (mins)",
              style = MaterialTheme.typography.titleSmall,
              fontWeight = FontWeight.Bold
            )
            Text(
              text = "$waiting mins",
              style = MaterialTheme.typography.bodyMedium,
              fontWeight = FontWeight.Bold,
              color = MaterialTheme.colorScheme.primary
            )
          }

          Spacer(modifier = Modifier.height(6.dp))

          OutlinedTextField(
            value = waitingText,
            onValueChange = { waitingText = it },
            placeholder = { Text("0") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            leadingIcon = {
              Icon(Icons.Default.Timer, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
            },
            singleLine = true,
            modifier = Modifier
              .fillMaxWidth()
              .testTag("new_ride_waiting_input")
          )

          Spacer(modifier = Modifier.height(6.dp))

          LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
          ) {
            items(quickWaiting) { waitVal ->
              FilterChip(
                selected = (waiting == waitVal),
                onClick = { waitingText = waitVal.toString() },
                label = { Text(if (waitVal == 0) "0m" else "${waitVal}m", fontSize = 12.sp) }
              )
            }
          }
        }
      }
    }

    // 3. Live Auto-Calculated Fare Display & Override
    item {
      Surface(
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.primaryContainer,
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(modifier = Modifier.padding(16.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Column {
              Text(
                text = "Auto-Calculated Fare",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onPrimaryContainer
              )
              Text(
                text = "Rs. ${breakdown.totalFare.toInt()}",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
              )
            }

            Column(horizontalAlignment = Alignment.End) {
              Text(
                text = "Base (2km): Rs. ${breakdown.baseFare.toInt()}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onPrimaryContainer
              )
              if (breakdown.distanceFare > 0) {
                Text(
                  text = "Extra Dist: Rs. ${breakdown.distanceFare.toInt()}",
                  style = MaterialTheme.typography.bodySmall,
                  color = MaterialTheme.colorScheme.onPrimaryContainer
                )
              }
              if (breakdown.waitingFare > 0) {
                Text(
                  text = "Waiting: Rs. ${breakdown.waitingFare.toInt()}",
                  style = MaterialTheme.typography.bodySmall,
                  color = MaterialTheme.colorScheme.onPrimaryContainer
                )
              }
            }
          }

          Spacer(modifier = Modifier.height(10.dp))

          OutlinedTextField(
            value = fareOverrideText,
            onValueChange = { fareOverrideText = it },
            label = { Text("Fare Override / Bargained (Optional)") },
            placeholder = { Text("Default: Rs. ${breakdown.totalFare.toInt()}") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine = true,
            modifier = Modifier
              .fillMaxWidth()
              .testTag("new_ride_fare_override_input")
          )
        }
      }
    }

    // 4. Payment Method Selection (Cash / JazzCash / Easypaisa)
    item {
      Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(modifier = Modifier.padding(14.dp)) {
          Text(
            text = "Payment Method",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold
          )

          Spacer(modifier = Modifier.height(8.dp))

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            listOf("Cash", "JazzCash", "Easypaisa").forEach { method ->
              val isSelected = paymentMethod == method
              val color = when (method) {
                "JazzCash" -> JazzCashOrange
                "Easypaisa" -> EasypaisaTeal
                else -> CashGreen
              }

              Surface(
                shape = RoundedCornerShape(12.dp),
                color = if (isSelected) color.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surfaceVariant,
                border = if (isSelected) androidx.compose.foundation.BorderStroke(2.dp, color) else null,
                modifier = Modifier
                  .weight(1f)
                  .clickable { paymentMethod = method }
                  .testTag("payment_chip_$method")
              ) {
                Column(
                  modifier = Modifier.padding(vertical = 12.dp),
                  horizontalAlignment = Alignment.CenterHorizontally
                ) {
                  Text(
                    text = method,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                    color = if (isSelected) color else MaterialTheme.colorScheme.onSurface,
                    fontSize = 13.sp
                  )
                }
              }
            }
          }
        }
      }
    }

    // 5. Customer Details (Optional) & Repeat Customer Chips
    item {
      Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(modifier = Modifier.padding(14.dp)) {
          Text(
            text = "Customer Details (Optional)",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold
          )

          Spacer(modifier = Modifier.height(8.dp))

          if (customers.isNotEmpty()) {
            Text(
              text = "Recent Passengers:",
              style = MaterialTheme.typography.labelSmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(6.dp))
            LazyRow(
              horizontalArrangement = Arrangement.spacedBy(8.dp),
              modifier = Modifier.fillMaxWidth()
            ) {
              items(customers.take(4)) { cust ->
                FilterChip(
                  selected = (customerPhone == cust.phone),
                  onClick = {
                    customerName = cust.name
                    customerPhone = cust.phone
                  },
                  label = { Text("${cust.name} (${cust.totalRides})", fontSize = 12.sp) }
                )
              }
            }
            Spacer(modifier = Modifier.height(8.dp))
          }

          OutlinedTextField(
            value = customerName,
            onValueChange = { customerName = it },
            label = { Text("Customer Name (Optional)") },
            placeholder = { Text("e.g. Ali Raza") },
            leadingIcon = {
              Icon(Icons.Default.Person, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
            },
            singleLine = true,
            modifier = Modifier
              .fillMaxWidth()
              .testTag("new_ride_customer_name_input")
          )

          Spacer(modifier = Modifier.height(8.dp))

          OutlinedTextField(
            value = customerPhone,
            onValueChange = { customerPhone = it },
            label = { Text("Customer Phone (Optional)") },
            placeholder = { Text("0308-5142950") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
            leadingIcon = {
              Icon(Icons.Default.Phone, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
            },
            singleLine = true,
            modifier = Modifier
              .fillMaxWidth()
              .testTag("new_ride_customer_phone_input")
          )

          Spacer(modifier = Modifier.height(8.dp))

          OutlinedTextField(
            value = notes,
            onValueChange = { notes = it },
            label = { Text("Notes (Optional)") },
            placeholder = { Text("e.g. luggage, waiting at pharmacy, drop off at gate") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
          )
        }
      }
    }

    // 6. Voice Announcements & Actions
    item {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        OutlinedButton(
          onClick = {
            if (destination.isBlank()) {
              scope.launch { snackbarHostState.showSnackbar("Please enter a destination first.") }
            } else {
              viewModel.playRideStartVoice(destination = destination, stand = pickupLocation)
              scope.launch { snackbarHostState.showSnackbar("Playing Ride Start Voice in Urdu...") }
            }
          },
          modifier = Modifier
            .weight(1f)
            .height(52.dp)
            .testTag("play_start_voice_button"),
          shape = RoundedCornerShape(14.dp)
        ) {
          Icon(Icons.Default.Speed, contentDescription = null, modifier = Modifier.size(20.dp))
          Spacer(modifier = Modifier.width(6.dp))
          Text("START RIDE VOICE", fontWeight = FontWeight.Bold, fontSize = 12.sp)
        }

        OutlinedButton(
          onClick = {
            viewModel.playRideCompleteVoice(finalCalculatedFare.toInt())
            scope.launch { snackbarHostState.showSnackbar("Playing Ride Complete Voice in Urdu...") }
          },
          modifier = Modifier
            .weight(1f)
            .height(52.dp)
            .testTag("play_complete_voice_button"),
          shape = RoundedCornerShape(14.dp)
        ) {
          Icon(Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(20.dp), tint = CashGreen)
          Spacer(modifier = Modifier.width(6.dp))
          Text("FARE AUDIO", fontWeight = FontWeight.Bold, fontSize = 12.sp)
        }
      }
    }

    // 7. Big Action Button: SAVE & COMPLETE RIDE
    item {
      Button(
        onClick = {
          if (destination.isBlank()) {
            scope.launch {
              snackbarHostState.showSnackbar("Please enter or select a destination in ${city.name}.")
            }
            return@Button
          }

          if (saveDestinationToCity) {
            viewModel.addCustomDestination(
              cityId = city.id,
              name = destination,
              category = "Popular",
              approxDistanceKm = distance,
              lat = city.centerLat,
              lng = city.centerLng
            )
          }

          viewModel.addRide(
            destination = destination,
            distanceKm = distance,
            waitingMinutes = waiting,
            customerName = customerName,
            customerPhone = customerPhone,
            pickupLocation = pickupLocation,
            paymentMethod = paymentMethod,
            notes = notes,
            fareOverride = fareOverrideText.toDoubleOrNull(),
            onSuccess = {
              // Play ride completion voice announcement
              viewModel.playRideCompleteVoice(finalCalculatedFare.toInt())

              scope.launch {
                snackbarHostState.showSnackbar("Ride saved successfully! Fare: Rs. ${finalCalculatedFare.toInt()}")
              }
              // Reset form fields
              destination = ""
              distanceText = "3.0"
              waitingText = "0"
              customerName = ""
              customerPhone = ""
              notes = ""
              fareOverrideText = ""
              saveDestinationToCity = false
              onRideSaved()
            }
          )
        },
        modifier = Modifier
          .fillMaxWidth()
          .height(56.dp)
          .testTag("save_ride_button"),
        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
        shape = RoundedCornerShape(16.dp)
      ) {
        Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(22.dp))
        Spacer(modifier = Modifier.width(8.dp))
        Text(
          text = "COMPLETE RIDE (Rs. ${finalCalculatedFare.toInt()})",
          fontSize = 16.sp,
          fontWeight = FontWeight.Bold
        )
      }
    }

    item {
      Spacer(modifier = Modifier.height(80.dp))
    }
  }
}

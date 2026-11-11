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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DirectionsBike
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Message
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material.icons.filled.PinDrop
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Smartphone
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Timer
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
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.AppSettings
import com.example.data.model.CityInfo
import com.example.data.model.FareBreakdown
import com.example.data.model.LocationPoint
import com.example.data.model.PakistanCitiesData
import com.example.ui.MainViewModel
import com.example.ui.components.shareText
import com.example.ui.theme.CashGreen
import com.example.ui.theme.ImperialGold
import com.example.ui.theme.ImperialGoldBright
import kotlin.math.max
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FareCalculatorScreen(
  viewModel: MainViewModel,
  onNavigateToNewRideWithValues: (distance: Double, waiting: Int, destination: String?) -> Unit,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  val settings by viewModel.settings.collectAsStateWithLifecycle()
  val city by viewModel.currentCity.collectAsStateWithLifecycle()
  val appTitle by viewModel.appTitleWithCity.collectAsStateWithLifecycle()
  val distText by viewModel.calcDistance.collectAsStateWithLifecycle()
  val waitText by viewModel.calcWaitMinutes.collectAsStateWithLifecycle()
  val selectedPickup by viewModel.calcPickupLocation.collectAsStateWithLifecycle()
  val selectedDrop by viewModel.calcDropLocation.collectAsStateWithLifecycle()
  val cityLocations by viewModel.currentCityLocations.collectAsStateWithLifecycle()

  var showCustomerModal by remember { mutableStateOf(false) }
  var cityDropdownExpanded by remember { mutableStateOf(false) }
  var isAnotherPickupLocation by remember { mutableStateOf(false) }
  var customPickupText by remember { mutableStateOf("") }
  var customDropText by remember { mutableStateOf("") }
  var destinationSavedMessage by remember { mutableStateOf<String?>(null) }

  val distance = distText.toDoubleOrNull() ?: 0.0
  val waiting = waitText.toIntOrNull() ?: 0

  val breakdown = viewModel.calculateFare(distance, waiting)

  val quickDistances = listOf(1.5, 2.0, 3.0, 4.0, 5.0, 6.0, 8.0, 10.0, 12.0, 15.0, 20.0)
  val quickWaiting = listOf(0, 5, 10, 15, 20, 30, 45, 60)

  LazyColumn(
    modifier = modifier
      .fillMaxSize()
      .padding(horizontal = 16.dp),
    verticalArrangement = Arrangement.spacedBy(14.dp)
  ) {
    // 1. DYNAMIC CITY APP TITLE & CITY SELECTOR
    item {
      Spacer(modifier = Modifier.height(4.dp))
      Card(
        shape = RoundedCornerShape(18.dp),
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
            Column {
              Text(
                text = appTitle,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.primary
              )
              Text(
                text = "${city.urduName} • ${city.tagLine}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
            }

            // City Picker Menu Button
            ExposedDropdownMenuBox(
              expanded = cityDropdownExpanded,
              onExpandedChange = { cityDropdownExpanded = it }
            ) {
              Surface(
                shape = RoundedCornerShape(10.dp),
                color = MaterialTheme.colorScheme.primaryContainer,
                modifier = Modifier
                  .menuAnchor()
                  .clickable { cityDropdownExpanded = true }
              ) {
                Row(
                  verticalAlignment = Alignment.CenterVertically,
                  modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                  Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(16.dp)
                  )
                  Spacer(modifier = Modifier.width(4.dp))
                  Text(
                    text = city.name,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    fontSize = 13.sp
                  )
                }
              }

              ExposedDropdownMenu(
                expanded = cityDropdownExpanded,
                onDismissRequest = { cityDropdownExpanded = false }
              ) {
                PakistanCitiesData.cities.forEach { c ->
                  DropdownMenuItem(
                    text = {
                      Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                      ) {
                        Text(c.name, fontWeight = if (c.id == city.id) FontWeight.Bold else FontWeight.Normal)
                        Text(c.urduName, color = MaterialTheme.colorScheme.primary)
                      }
                    },
                    onClick = {
                      cityDropdownExpanded = false
                      viewModel.changeCity(c.id)
                    }
                  )
                }
              }
            }
          }

          Spacer(modifier = Modifier.height(10.dp))

          // City Hallmark badge preview
          Surface(
            shape = RoundedCornerShape(10.dp),
            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
            modifier = Modifier.fillMaxWidth()
          ) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              modifier = Modifier.padding(8.dp)
            ) {
              Icon(
                imageVector = Icons.Default.DirectionsBike,
                contentDescription = null,
                tint = ImperialGold,
                modifier = Modifier.size(16.dp)
              )
              Spacer(modifier = Modifier.width(8.dp))
              Text(
                text = "City Hallmark: ${city.hallmarkTitle} (${city.hallmarkUrdu})",
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
              )
            }
          }
        }
      }
    }

    // 2. HERO FARE CARD (LIVE TRANSPARENT FARE)
    item {
      Surface(
        shape = RoundedCornerShape(22.dp),
        color = Color(0xFF062016),
        border = BorderStroke(1.5.dp, Brush.linearGradient(listOf(ImperialGoldBright, Color(0xFF78350F)))),
        shadowElevation = 6.dp,
        modifier = Modifier.fillMaxWidth()
      ) {
        Box(
          modifier = Modifier
            .background(
              Brush.verticalGradient(
                colors = listOf(
                  Color(0xFF044E3B),
                  Color(0xFF032B21),
                  Color(0xFF0B1411)
                )
              )
            )
            .padding(18.dp)
        ) {
          Column {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                  modifier = Modifier
                    .size(34.dp)
                    .background(Color.White.copy(alpha = 0.15f), CircleShape),
                  contentAlignment = Alignment.Center
                ) {
                  Icon(
                    imageVector = Icons.Default.Calculate,
                    contentDescription = null,
                    tint = Color(0xFFFCD34D),
                    modifier = Modifier.size(18.dp)
                  )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                  Text(
                    text = "TOTAL CUSTOMER FARE",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.White.copy(alpha = 0.85f),
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.8.sp
                  )
                  Text(
                    text = "${distance} km travel • ${waiting} mins wait",
                    fontSize = 11.sp,
                    color = Color.White.copy(alpha = 0.7f)
                  )
                }
              }

              Surface(
                shape = RoundedCornerShape(8.dp),
                color = ImperialGold
              ) {
                Text(
                  text = "${city.name} Rate",
                  fontSize = 11.sp,
                  fontWeight = FontWeight.Bold,
                  color = Color.White,
                  modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
              }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Massive Fare Text
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.Bottom
            ) {
              Column {
                Text(
                  text = "Rs. ${breakdown.totalFare.toInt()}",
                  style = MaterialTheme.typography.displaySmall,
                  fontWeight = FontWeight.ExtraBold,
                  color = Color(0xFFFCD34D)
                )
                Text(
                  text = "Sasti Sawari • No hidden surcharges",
                  fontSize = 11.sp,
                  color = Color.White.copy(alpha = 0.8f)
                )
              }

              Button(
                onClick = { showCustomerModal = true },
                colors = ButtonDefaults.buttonColors(
                  containerColor = Color.White.copy(alpha = 0.2f),
                  contentColor = Color.White
                ),
                shape = RoundedCornerShape(10.dp)
              ) {
                Icon(Icons.Default.Smartphone, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Show", fontSize = 12.sp, fontWeight = FontWeight.Bold)
              }
            }

            Spacer(modifier = Modifier.height(14.dp))
            Surface(
              modifier = Modifier
                .fillMaxWidth()
                .height(1.dp),
              color = Color.White.copy(alpha = 0.2f)
            ) {}
            Spacer(modifier = Modifier.height(10.dp))

            // Itemized Calculation
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
              ) {
                Text(
                  text = "• Base Fare (first ${settings.baseKm.toInt()} km)",
                  style = MaterialTheme.typography.bodySmall,
                  color = Color.White.copy(alpha = 0.9f)
                )
                Text(
                  text = "Rs. ${breakdown.baseFare.toInt()}",
                  style = MaterialTheme.typography.bodySmall,
                  fontWeight = FontWeight.SemiBold,
                  color = Color.White
                )
              }

              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
              ) {
                Text(
                  text = "• Additional (${"%.1f".format(breakdown.additionalKm)} km @ Rs. ${settings.ratePerAdditionalKm.toInt()}/km)",
                  style = MaterialTheme.typography.bodySmall,
                  color = Color.White.copy(alpha = 0.9f)
                )
                Text(
                  text = "Rs. ${breakdown.distanceFare.toInt()}",
                  style = MaterialTheme.typography.bodySmall,
                  fontWeight = FontWeight.SemiBold,
                  color = Color.White
                )
              }

              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
              ) {
                Text(
                  text = "• Waiting (${breakdown.chargeableWaitMinutes}m chargeable @ Rs. ${settings.waitingRatePerMinute.toInt()}/m)",
                  style = MaterialTheme.typography.bodySmall,
                  color = Color.White.copy(alpha = 0.9f)
                )
                Text(
                  text = "Rs. ${breakdown.waitingFare.toInt()}",
                  style = MaterialTheme.typography.bodySmall,
                  fontWeight = FontWeight.SemiBold,
                  color = Color.White
                )
              }
            }
          }
        }
      }
    }

    // 3. OFFLINE-ACCURATE LOCATION SELECTOR & GOOGLE MAPS LINKING (STAND OR CUSTOM PICKUP)
    item {
      Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(modifier = Modifier.padding(16.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(
                imageVector = Icons.Default.LocationOn,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(20.dp)
              )
              Spacer(modifier = Modifier.width(6.dp))
              Text(
                text = "Route in ${city.name}",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
              )
            }

            Surface(
              shape = RoundedCornerShape(8.dp),
              color = CashGreen.copy(alpha = 0.15f)
            ) {
              Text(
                text = "100% Offline GPS",
                fontSize = 11.sp,
                color = CashGreen,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
              )
            }
          }

          Text(
            text = "Calculate fare from fixed stand or any custom pickup location where the rider is currently waiting",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )

          Spacer(modifier = Modifier.height(12.dp))

          // 3A. PICKUP MODE TOGGLE: Fixed Stand vs Another Location
          Text(
            text = "Pickup Location:",
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold
          )
          Spacer(modifier = Modifier.height(6.dp))

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
                  val standLoc = cityLocations.firstOrNull { it.category == "Stand" } ?: cityLocations.firstOrNull()
                  if (standLoc != null) viewModel.selectCalcPickupLocation(standLoc)
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

          if (isAnotherPickupLocation) {
            Spacer(modifier = Modifier.height(10.dp))
            OutlinedTextField(
              value = customPickupText,
              onValueChange = { customPickupText = it },
              label = { Text("Enter Current Pickup Location / Area") },
              placeholder = { Text("e.g. Chowk Kumharanwala, Cantt, Gate 2") },
              leadingIcon = { Icon(Icons.Default.LocationOn, contentDescription = null, tint = ImperialGold) },
              trailingIcon = {
                if (customPickupText.isNotBlank()) {
                  IconButton(onClick = {
                    val uri = Uri.parse("geo:0,0?q=${Uri.encode("$customPickupText, ${city.name}, Pakistan")}")
                    context.startActivity(Intent(Intent.ACTION_VIEW, uri))
                  }) {
                    Icon(Icons.Default.Map, contentDescription = "View on Maps", tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(18.dp))
                  }
                }
              },
              singleLine = true,
              modifier = Modifier.fillMaxWidth().testTag("custom_pickup_input")
            )

            Spacer(modifier = Modifier.height(8.dp))
            Text(
              text = "Or tap a common city location for pickup:",
              style = MaterialTheme.typography.labelSmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(6.dp))
            LazyRow(
              horizontalArrangement = Arrangement.spacedBy(8.dp),
              modifier = Modifier.fillMaxWidth()
            ) {
              items(cityLocations) { loc ->
                val isSelected = selectedPickup?.name == loc.name && customPickupText.isBlank()
                FilterChip(
                  selected = isSelected,
                  onClick = {
                    customPickupText = ""
                    viewModel.selectCalcPickupLocation(loc)
                  },
                  label = { Text(loc.name, fontSize = 12.sp) }
                )
              }
            }
          } else {
            Spacer(modifier = Modifier.height(6.dp))
            Surface(
              shape = RoundedCornerShape(8.dp),
              color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
              modifier = Modifier.fillMaxWidth()
            ) {
              Row(
                modifier = Modifier.padding(10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
              ) {
                Column {
                  Text(
                    text = "Stand: ${settings.standName}",
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp
                  )
                  Text(
                    text = settings.standAddress,
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                  )
                }

                IconButton(
                  onClick = {
                    val p = selectedPickup ?: cityLocations.firstOrNull() ?: city.locations.first()
                    val uri = Uri.parse("geo:${p.lat},${p.lng}?q=${p.lat},${p.lng}(${Uri.encode(settings.standName)})")
                    context.startActivity(Intent(Intent.ACTION_VIEW, uri))
                  },
                  modifier = Modifier.size(32.dp)
                ) {
                  Icon(Icons.Default.Map, contentDescription = "View Stand", tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(18.dp))
                }
              }
            }
          }

          Spacer(modifier = Modifier.height(14.dp))

          // 3B. DESTINATION DROPOFF CHIPS & CUSTOM DROP-OFF
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text(
              text = "Dropped Destination in ${city.name}:",
              style = MaterialTheme.typography.labelMedium,
              fontWeight = FontWeight.Bold
            )

            if (selectedDrop != null || customDropText.isNotBlank()) {
              val dropName = if (customDropText.isNotBlank()) customDropText else (selectedDrop?.name ?: "")
              IconButton(
                onClick = {
                  val d = selectedDrop
                  val uri = if (d != null && d.lat != 0.0 && customDropText.isBlank()) {
                    Uri.parse("geo:${d.lat},${d.lng}?q=${d.lat},${d.lng}(${Uri.encode(dropName)})")
                  } else {
                    Uri.parse("geo:0,0?q=${Uri.encode("$dropName, ${city.name}, Pakistan")}")
                  }
                  context.startActivity(Intent(Intent.ACTION_VIEW, uri))
                },
                modifier = Modifier.size(28.dp)
              ) {
                Icon(Icons.Default.Map, contentDescription = "View Drop", tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(18.dp))
              }
            }
          }

          Spacer(modifier = Modifier.height(6.dp))

          OutlinedTextField(
            value = customDropText,
            onValueChange = { customDropText = it },
            placeholder = { Text("Type custom destination or select chip below") },
            leadingIcon = { Icon(Icons.Default.PinDrop, contentDescription = null, tint = MaterialTheme.colorScheme.primary) },
            singleLine = true,
            modifier = Modifier.fillMaxWidth().testTag("custom_drop_input")
          )

          // Button to save custom dropped destination to popular destinations
          if (customDropText.isNotBlank()) {
            Spacer(modifier = Modifier.height(6.dp))
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.End,
              verticalAlignment = Alignment.CenterVertically
            ) {
              OutlinedButton(
                onClick = {
                  viewModel.addCustomDestination(
                    cityId = city.id,
                    name = customDropText,
                    category = "Popular",
                    approxDistanceKm = distance,
                    lat = city.centerLat,
                    lng = city.centerLng
                  )
                  destinationSavedMessage = "Saved '$customDropText' to ${city.name} destinations!"
                },
                shape = RoundedCornerShape(8.dp),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 8.dp, vertical = 4.dp)
              ) {
                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(14.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Save to ${city.name} Destinations", fontSize = 11.sp, fontWeight = FontWeight.Bold)
              }
            }
          }

          if (destinationSavedMessage != null) {
            Text(
              text = destinationSavedMessage ?: "",
              fontSize = 11.sp,
              color = CashGreen,
              fontWeight = FontWeight.SemiBold,
              modifier = Modifier.padding(vertical = 4.dp)
            )
          }

          Spacer(modifier = Modifier.height(6.dp))

          LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
          ) {
            items(cityLocations.filter { it.name != selectedPickup?.name }) { loc ->
              val isSelected = selectedDrop?.name == loc.name && customDropText.isBlank()
              FilterChip(
                selected = isSelected,
                onClick = {
                  customDropText = ""
                  viewModel.selectCalcDropLocation(loc)
                },
                label = { Text("${loc.name} (${loc.approxDistanceKmFromStand}km)", fontSize = 12.sp) },
                leadingIcon = {
                  if (isSelected) {
                    Icon(Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(14.dp))
                  }
                }
              )
            }
          }

          Spacer(modifier = Modifier.height(14.dp))

          // 3C. GOOGLE MAPS NAVIGATION ROUTE BUTTON
          Button(
            onClick = {
              val p = selectedPickup ?: cityLocations.firstOrNull() ?: city.locations.first()
              val d = selectedDrop ?: cityLocations.lastOrNull() ?: city.locations.last()

              val pName = if (isAnotherPickupLocation && customPickupText.isNotBlank()) customPickupText else p.name
              val dName = if (customDropText.isNotBlank()) customDropText else d.name

              val pLat = if (!isAnotherPickupLocation || customPickupText.isBlank()) p.lat else 0.0
              val pLng = if (!isAnotherPickupLocation || customPickupText.isBlank()) p.lng else 0.0
              val dLat = if (customDropText.isBlank()) d.lat else 0.0
              val dLng = if (customDropText.isBlank()) d.lng else 0.0

              openGoogleMapsRoute(context, pLat, pLng, dLat, dLng, pName, dName, city.name)
            },
            modifier = Modifier
              .fillMaxWidth()
              .height(48.dp)
              .testTag("open_google_maps_route_button"),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary),
            shape = RoundedCornerShape(12.dp)
          ) {
            Icon(Icons.Default.Navigation, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              text = "Open Route in Google Maps (Bike Navigation)",
              fontSize = 13.sp,
              fontWeight = FontWeight.Bold
            )
          }
        }
      }
    }


    // 4. DISTANCE ADJUSTMENT STEPPERS & SLIDER
    item {
      Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(modifier = Modifier.padding(16.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(Icons.Default.Speed, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
              Spacer(modifier = Modifier.width(8.dp))
              Text(
                text = "Travel Distance",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
              )
            }
            Text(
              text = "$distance km",
              style = MaterialTheme.typography.titleMedium,
              fontWeight = FontWeight.Bold,
              color = MaterialTheme.colorScheme.primary
            )
          }

          Spacer(modifier = Modifier.height(10.dp))

          // Stepper + Text Field
          Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            OutlinedButton(
              onClick = {
                val newDist = max(0.0, ((distance - 0.5) * 2).roundToInt() / 2.0)
                viewModel.setCalcDistance(newDist.toString())
              },
              shape = RoundedCornerShape(12.dp),
              modifier = Modifier
                .size(48.dp)
                .testTag("calc_dist_minus")
            ) {
              Icon(Icons.Default.Remove, contentDescription = "Minus distance")
            }

            OutlinedTextField(
              value = distText,
              onValueChange = { viewModel.setCalcDistance(it) },
              label = { Text("Kilometres (km)") },
              placeholder = { Text("3.0") },
              keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
              singleLine = true,
              modifier = Modifier
                .weight(1f)
                .testTag("calc_distance_input")
            )

            Button(
              onClick = {
                val newDist = ((distance + 0.5) * 2).roundToInt() / 2.0
                viewModel.setCalcDistance(newDist.toString())
              },
              shape = RoundedCornerShape(12.dp),
              modifier = Modifier
                .size(48.dp)
                .testTag("calc_dist_plus")
            ) {
              Icon(Icons.Default.Add, contentDescription = "Plus distance")
            }
          }

          Spacer(modifier = Modifier.height(10.dp))

          Slider(
            value = distance.toFloat().coerceIn(0f, 30f),
            onValueChange = {
              val formatted = (it * 2).roundToInt() / 2.0
              viewModel.setCalcDistance(formatted.toString())
            },
            valueRange = 0f..30f,
            modifier = Modifier.fillMaxWidth()
          )

          LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
          ) {
            items(quickDistances) { d ->
              FilterChip(
                selected = distance == d,
                onClick = { viewModel.setCalcDistance(d.toString()) },
                label = { Text("${d}km", fontSize = 12.sp) }
              )
            }
          }
        }
      }
    }

    // 5. WAITING TIME STEPPERS
    item {
      Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(modifier = Modifier.padding(16.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(Icons.Default.Timer, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
              Spacer(modifier = Modifier.width(8.dp))
              Text(
                text = "Waiting Time",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
              )
            }
            Text(
              text = "$waiting mins",
              style = MaterialTheme.typography.titleMedium,
              fontWeight = FontWeight.Bold,
              color = MaterialTheme.colorScheme.primary
            )
          }

          Text(
            text = "First ${settings.freeWaitingMinutes} mins free • Rs. ${settings.waitingRatePerMinute.toInt()}/min after",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )

          Spacer(modifier = Modifier.height(10.dp))

          Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            OutlinedButton(
              onClick = {
                val newWait = max(0, waiting - 5)
                viewModel.setCalcWaitMinutes(newWait.toString())
              },
              shape = RoundedCornerShape(12.dp),
              modifier = Modifier
                .size(48.dp)
                .testTag("calc_wait_minus")
            ) {
              Icon(Icons.Default.Remove, contentDescription = "Minus wait")
            }

            OutlinedTextField(
              value = waitText,
              onValueChange = { viewModel.setCalcWaitMinutes(it) },
              label = { Text("Minutes") },
              placeholder = { Text("0") },
              keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
              singleLine = true,
              modifier = Modifier
                .weight(1f)
                .testTag("calc_wait_input")
            )

            Button(
              onClick = {
                val newWait = waiting + 5
                viewModel.setCalcWaitMinutes(newWait.toString())
              },
              shape = RoundedCornerShape(12.dp),
              modifier = Modifier
                .size(48.dp)
                .testTag("calc_wait_plus")
            ) {
              Icon(Icons.Default.Add, contentDescription = "Plus wait")
            }
          }

          Spacer(modifier = Modifier.height(8.dp))

          LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
          ) {
            items(quickWaiting) { w ->
              FilterChip(
                selected = waiting == w,
                onClick = { viewModel.setCalcWaitMinutes(w.toString()) },
                label = { Text(if (w == 0) "0m" else "${w}m", fontSize = 12.sp) }
              )
            }
          }
        }
      }
    }

    // 6. ACTION BUTTONS: BOOK RIDE, SHARE QUOTE, CALL & WHATSAPP 03085142950
    item {
      Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Button(
          onClick = {
            val dest = selectedDrop?.name ?: "Customer Ride (${city.name})"
            onNavigateToNewRideWithValues(distance, waiting, dest)
          },
          modifier = Modifier
            .fillMaxWidth()
            .height(54.dp)
            .testTag("calc_book_ride_button"),
          colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
          shape = RoundedCornerShape(14.dp)
        ) {
          Icon(Icons.Default.DirectionsBike, contentDescription = null, modifier = Modifier.size(20.dp))
          Spacer(modifier = Modifier.width(8.dp))
          Text(
            text = "Book Ride with this Fare (Rs. ${breakdown.totalFare.toInt()})",
            fontWeight = FontWeight.Bold,
            fontSize = 15.sp
          )
          Spacer(modifier = Modifier.width(6.dp))
          Icon(Icons.Default.ArrowForward, contentDescription = null, modifier = Modifier.size(18.dp))
        }

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          OutlinedButton(
            onClick = { showCustomerModal = true },
            modifier = Modifier
              .weight(1f)
              .height(50.dp)
              .testTag("calc_show_customer_modal_button"),
            shape = RoundedCornerShape(12.dp)
          ) {
            Icon(Icons.Default.Smartphone, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text("Show Customer", fontSize = 12.sp, fontWeight = FontWeight.Bold)
          }

          Button(
            onClick = {
              val quoteText = """
                🏍️ *${appTitle.uppercase()} - FARE ESTIMATE* 🏍️
                City: ${city.name} (${city.urduName})
                Rider: ${settings.operatorName} (${settings.operatorPhone})
                -------------------------------------
                📍 Pickup: ${selectedPickup?.name ?: settings.standName}
                🏁 Destination: ${selectedDrop?.name ?: "$distance km trip"}
                📏 Distance: ${distance} km
                ⏳ Waiting: ${waiting} mins
                -------------------------------------
                • Base Fare: Rs. ${breakdown.baseFare.toInt()} (First 2 km)
                • Additional Km: Rs. ${breakdown.distanceFare.toInt()}
                • Waiting Charges: Rs. ${breakdown.waitingFare.toInt()}
                -------------------------------------
                💰 *TOTAL FARE: Rs. ${breakdown.totalFare.toInt()}*
                Call/WhatsApp: 0308-5142950
                _Safe • Fast • Affordable Sasti Sawari_
              """.trimIndent()
              shareText(context, quoteText, "Sasti Sawari Fare Estimate")
            },
            modifier = Modifier
              .weight(1f)
              .height(50.dp)
              .testTag("calc_share_quote_button"),
            colors = ButtonDefaults.buttonColors(containerColor = CashGreen),
            shape = RoundedCornerShape(12.dp)
          ) {
            Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text("WhatsApp Quote", fontSize = 12.sp, fontWeight = FontWeight.Bold)
          }
        }

        // Direct Call & WhatsApp 03085142950
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          OutlinedButton(
            onClick = { dialPhone(context, "03085142950") },
            modifier = Modifier
              .weight(1f)
              .height(46.dp),
            shape = RoundedCornerShape(12.dp)
          ) {
            Icon(Icons.Default.Call, contentDescription = null, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text("Call: 03085142950", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
          }

          OutlinedButton(
            onClick = { openWhatsApp(context, "923085142950", "Assalam-o-Alaikum! I want to request a bike ride in ${city.name}.") },
            modifier = Modifier
              .weight(1f)
              .height(46.dp),
            shape = RoundedCornerShape(12.dp)
          ) {
            Icon(Icons.Default.Message, contentDescription = null, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text("WhatsApp Us", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
          }
        }
      }
    }

    item {
      Spacer(modifier = Modifier.height(80.dp))
    }
  }

  // Customer Full-Screen Fare Modal
  if (showCustomerModal) {
    CustomerFareExplanationDialog(
      breakdown = breakdown,
      settings = settings,
      onDismiss = { showCustomerModal = false }
    )
  }
}

fun openGoogleMapsRoute(
  context: Context,
  originLat: Double,
  originLng: Double,
  destLat: Double,
  destLng: Double,
  originName: String,
  destName: String,
  cityName: String = "Pakistan"
) {
  try {
    val originQuery = if (originLat != 0.0 && originLng != 0.0) "$originLat,$originLng" else Uri.encode("$originName, $cityName, Pakistan")
    val destQuery = if (destLat != 0.0 && destLng != 0.0) "$destLat,$destLng" else Uri.encode("$destName, $cityName, Pakistan")
    val uri = Uri.parse("https://www.google.com/maps/dir/?api=1&origin=$originQuery&destination=$destQuery&travelmode=two-wheeler")
    val intent = Intent(Intent.ACTION_VIEW, uri)
    intent.setPackage("com.google.android.apps.maps")
    if (intent.resolveActivity(context.packageManager) != null) {
      context.startActivity(intent)
    } else {
      context.startActivity(Intent(Intent.ACTION_VIEW, uri))
    }
  } catch (e: Exception) {
    val fallbackUri = Uri.parse("https://www.google.com/maps/search/?api=1&query=${Uri.encode("$destName, $cityName, Pakistan")}")
    context.startActivity(Intent(Intent.ACTION_VIEW, fallbackUri))
  }
}

@Composable
fun CustomerFareExplanationDialog(
  breakdown: FareBreakdown,
  settings: AppSettings,
  onDismiss: () -> Unit
) {
  val city = PakistanCitiesData.getCityById(settings.selectedCityId)
  Dialog(onDismissRequest = onDismiss) {
    Surface(
      shape = RoundedCornerShape(24.dp),
      color = Color(0xFF062016),
      border = BorderStroke(2.dp, Brush.linearGradient(listOf(ImperialGoldBright, Color(0xFF78350F)))),
      modifier = Modifier
        .fillMaxWidth()
        .padding(8.dp)
        .testTag("customer_fare_dialog")
    ) {
      Column(
        modifier = Modifier.padding(22.dp),
        horizontalAlignment = Alignment.CenterHorizontally
      ) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
              modifier = Modifier
                .size(36.dp)
                .background(ImperialGold, CircleShape),
              contentAlignment = Alignment.Center
            ) {
              Text(
                text = "PKR",
                color = Color.White,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
              )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Column {
              Text(
                text = "SASTI SAWARI ${city.name.uppercase()}",
                color = Color.White,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 15.sp
              )
              Text(
                text = "${settings.standName} • ${city.tagLine}",
                color = Color.White.copy(alpha = 0.8f),
                fontSize = 11.sp
              )
            }
          }

          IconButton(onClick = onDismiss) {
            Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White)
          }
        }

        Spacer(modifier = Modifier.height(18.dp))

        Surface(
          shape = RoundedCornerShape(16.dp),
          color = Color(0xFF132A22),
          modifier = Modifier.fillMaxWidth()
        ) {
          Column(
            modifier = Modifier.padding(18.dp),
            horizontalAlignment = Alignment.CenterHorizontally
          ) {
            Text(
              text = "TOTAL RIDE FARE",
              color = Color.White.copy(alpha = 0.85f),
              fontSize = 12.sp,
              fontWeight = FontWeight.Bold,
              letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
              text = "Rs. ${breakdown.totalFare.toInt()}",
              color = Color(0xFFFCD34D),
              fontSize = 44.sp,
              fontWeight = FontWeight.ExtraBold
            )
            Text(
              text = "Direct Pickup • Verified Rider • Transparent Rate",
              color = Color.White.copy(alpha = 0.75f),
              fontSize = 11.sp
            )
          }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Transparent Calculation
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF132A22).copy(alpha = 0.6f), RoundedCornerShape(12.dp))
            .padding(14.dp)
        ) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Text("Trip Distance", color = Color.White.copy(alpha = 0.85f), fontSize = 14.sp)
            Text("${breakdown.distanceKm} km", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
          }
          Spacer(modifier = Modifier.height(6.dp))
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Text("First 2 km minimum fare", color = Color.White.copy(alpha = 0.75f), fontSize = 13.sp)
            Text("Rs. ${breakdown.baseFare.toInt()}", color = Color.White, fontSize = 13.sp)
          }
          if (breakdown.additionalKm > 0) {
            Spacer(modifier = Modifier.height(4.dp))
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween
            ) {
              Text("Extra ${"%.1f".format(breakdown.additionalKm)} km @ Rs. 25/km", color = Color.White.copy(alpha = 0.75f), fontSize = 13.sp)
              Text("Rs. ${breakdown.distanceFare.toInt()}", color = Color.White, fontSize = 13.sp)
            }
          }
          if (breakdown.waitingMinutes > 0) {
            Spacer(modifier = Modifier.height(4.dp))
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween
            ) {
              Text("Waiting: ${breakdown.waitingMinutes}m (5m free)", color = Color.White.copy(alpha = 0.75f), fontSize = 13.sp)
              Text("Rs. ${breakdown.waitingFare.toInt()}", color = Color.White, fontSize = 13.sp)
            }
          }
        }

        Spacer(modifier = Modifier.height(18.dp))

        Button(
          onClick = onDismiss,
          modifier = Modifier
            .fillMaxWidth()
            .height(48.dp),
          colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
          shape = RoundedCornerShape(12.dp)
        ) {
          Icon(Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(18.dp))
          Spacer(modifier = Modifier.width(6.dp))
          Text("Understood / Close", fontWeight = FontWeight.Bold)
        }
      }
    }
  }
}

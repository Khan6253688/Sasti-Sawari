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
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DirectionsBike
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Message
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.TwoWheeler
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
import androidx.compose.material3.Text
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.data.model.CityInfo
import com.example.data.model.LocationPoint
import com.example.data.model.RiderAccount
import com.example.ui.MainViewModel
import com.example.ui.theme.CashGreen
import com.example.ui.theme.ImperialGold
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PassengerScreen(
  viewModel: MainViewModel,
  snackbarHostState: SnackbarHostState,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  val scope = rememberCoroutineScope()
  val allCities by viewModel.serviceCities.collectAsStateWithLifecycle()
  val allRiders by viewModel.allRiders.collectAsStateWithLifecycle()
  val currentCity by viewModel.currentCity.collectAsStateWithLifecycle()

  val currentPassenger by viewModel.currentPassenger.collectAsStateWithLifecycle()
  var selectedCityId by remember { mutableStateOf(currentPassenger.cityId.ifBlank { currentCity.id }) }
  var selectedAreaFilter by remember { mutableStateOf("All Stands") }
  var searchQuery by remember { mutableStateOf("") }
  var cityDropdownExpanded by remember { mutableStateOf(false) }

  // Quick Fare Estimator State
  var estDistanceKm by remember { mutableStateOf("3.0") }
  val calculatedFare = viewModel.calculateFare(estDistanceKm.toDoubleOrNull() ?: 3.0, 0)

  val activeCity = allCities.firstOrNull { it.id == selectedCityId } ?: currentCity

  // Filter riders for selected city & active status
  val filteredRiders = allRiders.filter { rider ->
    val matchesCity = rider.cityId.equals(selectedCityId, ignoreCase = true)
    val matchesActive = rider.subscriptionStatus == "ACTIVE"
    val matchesArea = if (selectedAreaFilter == "All Stands") true else {
      rider.standName.contains(selectedAreaFilter, ignoreCase = true) ||
          rider.standAddress.contains(selectedAreaFilter, ignoreCase = true)
    }
    val matchesSearch = if (searchQuery.isBlank()) true else {
      rider.name.contains(searchQuery, ignoreCase = true) ||
          rider.standName.contains(searchQuery, ignoreCase = true) ||
          rider.phone.contains(searchQuery, ignoreCase = true)
    }
    matchesCity && matchesActive && matchesArea && matchesSearch
  }

  LazyColumn(
    modifier = modifier
      .fillMaxSize()
      .padding(horizontal = 16.dp),
    verticalArrangement = Arrangement.spacedBy(16.dp)
  ) {
    // Passenger Profile Header Card
    item {
      Spacer(modifier = Modifier.height(4.dp))
      Surface(
        color = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, CashGreen.copy(alpha = 0.4f)),
        modifier = Modifier
          .fillMaxWidth()
          .testTag("passenger_profile_header_card")
      ) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Surface(
              shape = CircleShape,
              color = CashGreen.copy(alpha = 0.15f),
              modifier = Modifier.size(42.dp)
            ) {
              Box(contentAlignment = Alignment.Center) {
                Icon(
                  imageVector = Icons.Default.Person,
                  contentDescription = null,
                  tint = CashGreen,
                  modifier = Modifier.size(24.dp)
                )
              }
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                  text = currentPassenger.name.ifBlank { "Passenger Profile" },
                  style = MaterialTheme.typography.titleMedium,
                  fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.width(6.dp))
                Surface(
                  shape = RoundedCornerShape(4.dp),
                  color = CashGreen.copy(alpha = 0.15f)
                ) {
                  Text(
                    text = "PASSENGER",
                    color = CashGreen,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                  )
                }
              }
              Text(
                text = "${if (currentPassenger.phone.isNotBlank()) "${currentPassenger.phone} • " else ""}${activeCity.name} Commuter",
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
            }
          }

          OutlinedButton(
            onClick = { viewModel.logoutPassenger() },
            shape = RoundedCornerShape(8.dp)
          ) {
            Text("Switch", fontSize = 11.sp, fontWeight = FontWeight.Bold)
          }
        }
      }
    }
    // Header Banner
    item {
      Spacer(modifier = Modifier.height(8.dp))
      Card(
        modifier = Modifier
          .fillMaxWidth()
          .testTag("passenger_hero_card"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
      ) {
        Column(modifier = Modifier.padding(18.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Column(modifier = Modifier.weight(1f)) {
              Text(
                text = "PASSENGER DIRECT FINDER",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
              )
              Text(
                text = "Find Verified Bike Riders in ${activeCity.name}",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.onPrimaryContainer
              )
            }
            Surface(
              shape = CircleShape,
              color = MaterialTheme.colorScheme.primary,
              modifier = Modifier.size(44.dp)
            ) {
              Box(contentAlignment = Alignment.Center) {
                Icon(
                  imageVector = Icons.Default.TwoWheeler,
                  contentDescription = null,
                  tint = MaterialTheme.colorScheme.onPrimary,
                  modifier = Modifier.size(26.dp)
                )
              }
            }
          }

          Spacer(modifier = Modifier.height(10.dp))
          Text(
            text = "Direct Stand-and-Pickup service. No commission cuts, lowest standard rates, direct WhatsApp and Phone call to verified motorcycle riders at their stands.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.85f)
          )
        }
      }
    }

    // City Selector & Search
    item {
      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
      ) {
        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
          Text(
            text = "Select Service City",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold
          )

          ExposedDropdownMenuBox(
            expanded = cityDropdownExpanded,
            onExpandedChange = { cityDropdownExpanded = !cityDropdownExpanded }
          ) {
            OutlinedTextField(
              value = "${activeCity.name} (${activeCity.urduName})",
              onValueChange = {},
              readOnly = true,
              label = { Text("City") },
              trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = cityDropdownExpanded) },
              modifier = Modifier
                .menuAnchor()
                .fillMaxWidth()
                .testTag("passenger_city_selector"),
              shape = RoundedCornerShape(12.dp)
            )

            ExposedDropdownMenu(
              expanded = cityDropdownExpanded,
              onDismissRequest = { cityDropdownExpanded = false }
            ) {
              allCities.forEach { city ->
                DropdownMenuItem(
                  text = {
                    Column {
                      Text(city.name, fontWeight = FontWeight.Bold)
                      Text(city.urduName, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                  },
                  onClick = {
                    cityDropdownExpanded = false
                    selectedCityId = city.id
                    selectedAreaFilter = "All Stands"
                  }
                )
              }
            }
          }

          // Area / Stand Filter Chips
          Text(
            text = "Filter by Stand / Area",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )

          val areaOptions = listOf("All Stands") + activeCity.locations
            .filter { it.category == "Stand" || it.approxDistanceKmFromStand == 0.0 }
            .map { it.name }
            .distinct()

          LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(areaOptions) { area ->
              val isSelected = selectedAreaFilter == area
              FilterChip(
                selected = isSelected,
                onClick = { selectedAreaFilter = area },
                label = { Text(area) },
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

          // Search input
          OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text("Search rider name, phone, or stand...") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            singleLine = true
          )
        }
      }
    }

    // Transparent Fare Estimator for Passengers
    item {
      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
      ) {
        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(
                imageVector = Icons.Default.Shield,
                contentDescription = null,
                tint = CashGreen,
                modifier = Modifier.size(20.dp)
              )
              Spacer(modifier = Modifier.width(6.dp))
              Text(
                text = "Transparent Fare Estimator",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold
              )
            }
            Surface(
              shape = RoundedCornerShape(8.dp),
              color = CashGreen.copy(alpha = 0.15f)
            ) {
              Text(
                text = "Rs. ${calculatedFare.totalFare.toInt()} Est.",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.ExtraBold,
                color = CashGreen,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
              )
            }
          }

          Text(
            text = "Official Stand Pricing: Rs. 80 (first 2 km) + Rs. 25 per additional km. No hidden surge pricing.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            listOf("2.0", "3.0", "5.0", "8.0", "10.0").forEach { km ->
              val isSel = estDistanceKm == km
              Surface(
                shape = RoundedCornerShape(8.dp),
                color = if (isSel) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface,
                border = BorderStroke(1.dp, if (isSel) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline),
                modifier = Modifier
                  .weight(1f)
                  .clickable { estDistanceKm = km }
              ) {
                Text(
                  text = "${km}km",
                  textAlign = TextAlign.Center,
                  fontSize = 12.sp,
                  fontWeight = FontWeight.Bold,
                  color = if (isSel) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface,
                  modifier = Modifier.padding(vertical = 6.dp)
                )
              }
            }
          }
        }
      }
    }

    // Active Riders Section Header
    item {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = "Available Riders (${filteredRiders.size})",
          style = MaterialTheme.typography.titleMedium,
          fontWeight = FontWeight.Bold
        )

        Surface(
          shape = RoundedCornerShape(12.dp),
          color = CashGreen.copy(alpha = 0.15f)
        ) {
          Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Box(
              modifier = Modifier
                .size(8.dp)
                .clip(CircleShape)
                .background(CashGreen)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
              text = "Online at Stands",
              fontSize = 11.sp,
              fontWeight = FontWeight.Bold,
              color = CashGreen
            )
          }
        }
      }
    }

    // Riders List
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
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
          ) {
            Icon(
              imageVector = Icons.Default.DirectionsBike,
              contentDescription = null,
              modifier = Modifier.size(48.dp),
              tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
              text = "No active riders found in this area",
              fontWeight = FontWeight.Bold,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
              text = "Try selecting 'All Stands' or choosing another service city.",
              style = MaterialTheme.typography.bodySmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
              textAlign = TextAlign.Center
            )
          }
        }
      }
    } else {
      items(filteredRiders, key = { it.riderId }) { rider ->
        PassengerRiderCard(
          rider = rider,
          cityName = activeCity.name,
          onCall = {
            val phone = rider.phone.replace("-", "").replace(" ", "")
            val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$phone"))
            context.startActivity(intent)
          },
          onWhatsApp = {
            val phone = rider.phone.replace("-", "").replace(" ", "")
            val formatted = if (phone.startsWith("0")) "92" + phone.substring(1) else phone
            val message = Uri.encode("Assalam-o-Alaikum ${rider.name}! Mujhe Sasti Sawari bike ride chahiye aap ke stand (${rider.standName}) se.")
            val url = "https://wa.me/$formatted?text=$message"
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            context.startActivity(intent)
          }
        )
      }
    }

    item {
      Spacer(modifier = Modifier.height(80.dp))
    }
  }
}

@Composable
fun PassengerRiderCard(
  rider: RiderAccount,
  cityName: String,
  onCall: () -> Unit,
  onWhatsApp: () -> Unit
) {
  Card(
    modifier = Modifier
      .fillMaxWidth()
      .testTag("rider_card_${rider.riderId}"),
    shape = RoundedCornerShape(18.dp),
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
  ) {
    Column(modifier = Modifier.padding(16.dp)) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
      ) {
        // Rider Photo or Initials
        if (rider.photoUri.isNotBlank()) {
          AsyncImage(
            model = rider.photoUri,
            contentDescription = rider.name,
            modifier = Modifier
              .size(60.dp)
              .clip(CircleShape),
            contentScale = ContentScale.Crop
          )
        } else {
          Surface(
            shape = CircleShape,
            color = MaterialTheme.colorScheme.primaryContainer,
            modifier = Modifier.size(60.dp)
          ) {
            Box(contentAlignment = Alignment.Center) {
              Text(
                text = rider.name.split(" ").mapNotNull { it.firstOrNull()?.toString() }.take(2).joinToString(""),
                fontSize = 20.sp,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.onPrimaryContainer
              )
            }
          }
        }

        Spacer(modifier = Modifier.width(14.dp))

        Column(modifier = Modifier.weight(1f)) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
              text = rider.name,
              style = MaterialTheme.typography.titleMedium,
              fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.width(6.dp))
            Icon(
              imageVector = Icons.Default.CheckCircle,
              contentDescription = "Verified Rider",
              tint = CashGreen,
              modifier = Modifier.size(16.dp)
            )
          }

          Text(
            text = "${rider.bikeModel} • ${rider.plate}",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontWeight = FontWeight.SemiBold
          )

          Text(
            text = "ID: ${rider.riderId} • Verified Sasti Sawari Rider",
            fontSize = 11.sp,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Bold
          )
        }
      }

      Spacer(modifier = Modifier.height(12.dp))

      // Stand Location
      Surface(
        shape = RoundedCornerShape(10.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
        modifier = Modifier.fillMaxWidth()
      ) {
        Row(
          modifier = Modifier.padding(10.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Icon(
            imageVector = Icons.Default.LocationOn,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(18.dp)
          )
          Spacer(modifier = Modifier.width(8.dp))
          Column {
            Text(
              text = rider.standName,
              style = MaterialTheme.typography.labelMedium,
              fontWeight = FontWeight.Bold
            )
            Text(
              text = rider.standAddress,
              style = MaterialTheme.typography.bodySmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }
        }
      }

      if (rider.tagline.isNotBlank()) {
        Spacer(modifier = Modifier.height(8.dp))
        Text(
          text = "\"${rider.tagline}\"",
          style = MaterialTheme.typography.bodySmall,
          color = MaterialTheme.colorScheme.onSurfaceVariant,
          fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
        )
      }

      Spacer(modifier = Modifier.height(14.dp))

      // Action Buttons: Call & WhatsApp
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        Button(
          onClick = onCall,
          modifier = Modifier
            .weight(1f)
            .height(46.dp)
            .testTag("call_rider_${rider.riderId}"),
          shape = RoundedCornerShape(12.dp),
          colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
        ) {
          Icon(Icons.Default.Call, contentDescription = null, modifier = Modifier.size(18.dp))
          Spacer(modifier = Modifier.width(6.dp))
          Text("Call Rider", fontWeight = FontWeight.Bold, fontSize = 13.sp)
        }

        Button(
          onClick = onWhatsApp,
          modifier = Modifier
            .weight(1f)
            .height(46.dp)
            .testTag("whatsapp_rider_${rider.riderId}"),
          shape = RoundedCornerShape(12.dp),
          colors = ButtonDefaults.buttonColors(containerColor = CashGreen)
        ) {
          Icon(Icons.Default.Message, contentDescription = null, modifier = Modifier.size(18.dp))
          Spacer(modifier = Modifier.width(6.dp))
          Text("WhatsApp", fontWeight = FontWeight.Bold, fontSize = 13.sp)
        }
      }
    }
  }
}

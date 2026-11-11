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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddLocationAlt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DirectionsBike
import androidx.compose.material.icons.filled.LocationCity
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.PinDrop
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Speed
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
import com.example.data.model.CityDestination
import com.example.data.model.CityInfo
import com.example.data.model.CustomCity
import com.example.data.model.LocationPoint
import com.example.ui.MainViewModel
import com.example.ui.theme.CashGreen
import com.example.ui.theme.ImperialGold
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CityDestinationsScreen(
  viewModel: MainViewModel,
  snackbarHostState: SnackbarHostState,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  val scope = rememberCoroutineScope()
  val settings by viewModel.settings.collectAsStateWithLifecycle()
  val currentCity by viewModel.currentCity.collectAsStateWithLifecycle()
  val serviceCities by viewModel.serviceCities.collectAsStateWithLifecycle()
  val customCities by viewModel.customCities.collectAsStateWithLifecycle()
  val allCustomDestinations by viewModel.allCustomDestinations.collectAsStateWithLifecycle()

  var viewingCityId by remember(currentCity.id) { mutableStateOf(currentCity.id) }
  var cityDropdownExpanded by remember { mutableStateOf(false) }

  var selectedCategoryFilter by remember { mutableStateOf("All") }
  var searchQuery by remember { mutableStateOf("") }

  var showAddDestinationDialog by remember { mutableStateOf(false) }
  var showAddCityDialog by remember { mutableStateOf(false) }

  val targetCity = serviceCities.find { it.id == viewingCityId } ?: currentCity
  val combinedLocations = remember(targetCity, allCustomDestinations) {
    com.example.data.model.PakistanCitiesData.getCombinedCityLocations(targetCity, allCustomDestinations)
  }

  val categories = listOf("All", "Transport", "Hospital", "University", "Market", "Landmark", "Residential", "Custom")

  val filteredLocations = combinedLocations.filter { loc ->
    val matchesCategory = if (selectedCategoryFilter == "All") true
    else if (selectedCategoryFilter == "Custom") {
      allCustomDestinations.any { it.name.equals(loc.name, ignoreCase = true) && it.cityId.equals(targetCity.id, ignoreCase = true) }
    } else {
      loc.category.equals(selectedCategoryFilter, ignoreCase = true)
    }

    val matchesSearch = if (searchQuery.isBlank()) true
    else loc.name.contains(searchQuery, ignoreCase = true) || loc.urduName.contains(searchQuery)

    matchesCategory && matchesSearch
  }

  LazyColumn(
    modifier = modifier
      .fillMaxSize()
      .padding(horizontal = 16.dp),
    verticalArrangement = Arrangement.spacedBy(14.dp)
  ) {
    item {
      Spacer(modifier = Modifier.height(6.dp))
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column {
          Text(
            text = "Service Cities & Destinations",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.ExtraBold,
            color = MaterialTheme.colorScheme.primary
          )
          Text(
            text = "Define operating cities & configure common drop-off points",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
        }

        Button(
          onClick = { showAddCityDialog = true },
          colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
          shape = RoundedCornerShape(10.dp),
          modifier = Modifier.testTag("add_service_city_button")
        ) {
          Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
          Spacer(modifier = Modifier.width(4.dp))
          Text("Add City", fontSize = 12.sp, fontWeight = FontWeight.Bold)
        }
      }
    }

    // 1. SERVICE CITIES CONFIGURATION CARD
    item {
      Card(
        shape = RoundedCornerShape(20.dp),
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
              Icon(Icons.Default.LocationCity, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
              Spacer(modifier = Modifier.width(8.dp))
              Text(
                text = "Service Cities (${serviceCities.size} Available)",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
              )
            }

            Surface(shape = RoundedCornerShape(8.dp), color = CashGreen.copy(alpha = 0.15f)) {
              Text(
                text = "Active: ${currentCity.name}",
                fontSize = 11.sp,
                color = CashGreen,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
              )
            }
          }

          Spacer(modifier = Modifier.height(10.dp))
          Text(
            text = "Tap any city to view & configure its destinations. Tap 'Set Active' to switch your operating city.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )

          Spacer(modifier = Modifier.height(12.dp))

          // Horizontal scroll of Service Cities
          LazyRow(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.fillMaxWidth()
          ) {
            items(serviceCities) { c ->
              val isViewing = viewingCityId == c.id
              val isActive = currentCity.id == c.id
              val isCustom = customCities.any { it.id == c.id }

              Surface(
                shape = RoundedCornerShape(14.dp),
                color = if (isViewing) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant,
                border = if (isActive) androidx.compose.foundation.BorderStroke(2.dp, ImperialGold) else null,
                modifier = Modifier
                  .clickable { viewingCityId = c.id }
                  .testTag("city_chip_${c.id}")
              ) {
                Column(modifier = Modifier.padding(12.dp)) {
                  Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                      text = c.name,
                      fontWeight = FontWeight.Bold,
                      fontSize = 13.sp,
                      color = if (isViewing) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface
                    )
                    if (isActive) {
                      Spacer(modifier = Modifier.width(6.dp))
                      Icon(Icons.Default.CheckCircle, contentDescription = null, tint = ImperialGold, modifier = Modifier.size(14.dp))
                    }
                  }

                  Text(
                    text = c.urduName,
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.primary
                  )

                  Spacer(modifier = Modifier.height(6.dp))

                  Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    if (!isActive) {
                      TextButton(
                        onClick = {
                          viewModel.changeCity(c.id)
                          viewingCityId = c.id
                          scope.launch { snackbarHostState.showSnackbar("Switched operating city to ${c.name}") }
                        },
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 6.dp, vertical = 2.dp)
                      ) {
                        Text("Set Active", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                      }
                    }

                    if (isCustom) {
                      IconButton(
                        onClick = {
                          customCities.find { it.id == c.id }?.let { viewModel.deleteCustomServiceCity(it) }
                          viewingCityId = "multan"
                        },
                        modifier = Modifier.size(24.dp)
                      ) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete City", tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(14.dp))
                      }
                    }
                  }
                }
              }
            }
          }
        }
      }
    }

    // 2. POPULAR DESTINATIONS MANAGEMENT FOR SELECTED CITY
    item {
      Card(
        shape = RoundedCornerShape(20.dp),
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
            Column {
              Text(
                text = "Destinations in ${targetCity.name}",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
              )
              Text(
                text = "${filteredLocations.size} drop-off points available for fast rider selection",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
            }

            Button(
              onClick = { showAddDestinationDialog = true },
              shape = RoundedCornerShape(10.dp),
              colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary),
              modifier = Modifier.testTag("add_destination_button")
            ) {
              Icon(Icons.Default.AddLocationAlt, contentDescription = null, modifier = Modifier.size(16.dp))
              Spacer(modifier = Modifier.width(4.dp))
              Text("Add Destination", fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
          }

          Spacer(modifier = Modifier.height(12.dp))

          // Search Box
          OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text("Search ${targetCity.name} destinations...") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
            trailingIcon = {
              if (searchQuery.isNotBlank()) {
                IconButton(onClick = { searchQuery = "" }) {
                  Icon(Icons.Default.Close, contentDescription = "Clear")
                }
              }
            },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
          )

          Spacer(modifier = Modifier.height(10.dp))

          // Category Chips Filter
          LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
          ) {
            items(categories) { cat ->
              FilterChip(
                selected = selectedCategoryFilter == cat,
                onClick = { selectedCategoryFilter = cat },
                label = { Text(cat, fontSize = 11.sp) }
              )
            }
          }
        }
      }
    }

    // 3. DESTINATIONS LIST
    items(filteredLocations) { loc ->
      val customDest = allCustomDestinations.find {
        it.cityId.equals(targetCity.id, ignoreCase = true) && it.name.equals(loc.name, ignoreCase = true)
      }
      val isCustom = customDest != null

      Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = Modifier.fillMaxWidth()
      ) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(14.dp),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
          ) {
            Box(
              modifier = Modifier
                .size(36.dp)
                .background(
                  if (isCustom) ImperialGold.copy(alpha = 0.15f) else MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),
                  CircleShape
                ),
              contentAlignment = Alignment.Center
            ) {
              Icon(
                imageVector = when (loc.category) {
                  "Hospital" -> Icons.Default.LocationOn
                  "University" -> Icons.Default.LocationOn
                  "Transport" -> Icons.Default.DirectionsBike
                  "Stand" -> Icons.Default.PinDrop
                  else -> Icons.Default.PinDrop
                },
                contentDescription = null,
                tint = if (isCustom) ImperialGold else MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(18.dp)
              )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                  text = loc.name,
                  fontWeight = FontWeight.Bold,
                  fontSize = 13.sp
                )
                if (isCustom) {
                  Spacer(modifier = Modifier.width(6.dp))
                  Surface(shape = RoundedCornerShape(4.dp), color = ImperialGold.copy(alpha = 0.2f)) {
                    Text("Driver Added", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = ImperialGold, modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp))
                  }
                }
              }

              if (loc.urduName.isNotBlank()) {
                Text(
                  text = loc.urduName,
                  fontSize = 11.sp,
                  color = MaterialTheme.colorScheme.primary
                )
              }

              Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                  text = "${loc.category} • ${if (loc.approxDistanceKmFromStand > 0) "${loc.approxDistanceKmFromStand} km from Stand" else "Stand Location"}",
                  fontSize = 11.sp,
                  color = MaterialTheme.colorScheme.onSurfaceVariant
                )
              }
            }
          }

          Row(verticalAlignment = Alignment.CenterVertically) {
            // Google Maps button
            IconButton(
              onClick = {
                val uri = if (loc.lat != 0.0 && loc.lng != 0.0) {
                  Uri.parse("geo:${loc.lat},${loc.lng}?q=${loc.lat},${loc.lng}(${Uri.encode(loc.name)})")
                } else {
                  Uri.parse("geo:0,0?q=${Uri.encode("${loc.name}, ${targetCity.name}, Pakistan")}")
                }
                context.startActivity(Intent(Intent.ACTION_VIEW, uri))
              },
              modifier = Modifier.size(32.dp)
            ) {
              Icon(Icons.Default.Map, contentDescription = "View on Maps", tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(18.dp))
            }

            if (isCustom && customDest != null) {
              IconButton(
                onClick = {
                  viewModel.deleteCustomDestination(customDest)
                  scope.launch { snackbarHostState.showSnackbar("Removed '${loc.name}' from destinations") }
                },
                modifier = Modifier.size(32.dp)
              ) {
                Icon(Icons.Default.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(18.dp))
              }
            }
          }
        }
      }
    }

    item {
      Spacer(modifier = Modifier.height(24.dp))
    }
  }

  // DIALOG: ADD POPULAR DESTINATION
  if (showAddDestinationDialog) {
    var destName by remember { mutableStateOf("") }
    var destUrdu by remember { mutableStateOf("") }
    var destCategory by remember { mutableStateOf("Popular") }
    var destDistKm by remember { mutableStateOf("3.0") }
    var categoryDropdownExpanded by remember { mutableStateOf(false) }

    val categoryOptions = listOf("Popular", "Transport", "Hospital", "University", "Market", "Landmark", "Residential", "Commercial", "Other")

    AlertDialog(
      onDismissRequest = { showAddDestinationDialog = false },
      title = { Text("Add Destination in ${targetCity.name}", fontWeight = FontWeight.Bold) },
      text = {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
          Text(
            text = "This destination will appear as a quick one-tap chip in the Fare Calculator and New Ride entry.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )

          OutlinedTextField(
            value = destName,
            onValueChange = { destName = it },
            label = { Text("Destination / Drop-off Name *") },
            placeholder = { Text("e.g. Metro Bus Chungi 9") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth().testTag("add_dest_name_input")
          )

          OutlinedTextField(
            value = destUrdu,
            onValueChange = { destUrdu = it },
            label = { Text("Urdu Name (Optional)") },
            placeholder = { Text("چنگی نمبر 9") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
          )

          ExposedDropdownMenuBox(
            expanded = categoryDropdownExpanded,
            onExpandedChange = { categoryDropdownExpanded = it }
          ) {
            OutlinedTextField(
              value = destCategory,
              onValueChange = {},
              readOnly = true,
              label = { Text("Category") },
              trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = categoryDropdownExpanded) },
              modifier = Modifier.fillMaxWidth().menuAnchor()
            )
            ExposedDropdownMenu(
              expanded = categoryDropdownExpanded,
              onDismissRequest = { categoryDropdownExpanded = false }
            ) {
              categoryOptions.forEach { opt ->
                DropdownMenuItem(
                  text = { Text(opt) },
                  onClick = {
                    categoryDropdownExpanded = false
                    destCategory = opt
                  }
                )
              }
            }
          }

          OutlinedTextField(
            value = destDistKm,
            onValueChange = { destDistKm = it },
            label = { Text("Approx Distance from Stand (km)") },
            placeholder = { Text("3.0") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            leadingIcon = { Icon(Icons.Default.Speed, contentDescription = null) },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
          )
        }
      },
      confirmButton = {
        Button(
          onClick = {
            if (destName.isNotBlank()) {
              val km = destDistKm.toDoubleOrNull() ?: 3.0
              viewModel.addCustomDestination(
                cityId = targetCity.id,
                name = destName,
                urduName = destUrdu,
                category = destCategory,
                approxDistanceKm = km,
                lat = targetCity.centerLat,
                lng = targetCity.centerLng
              )
              showAddDestinationDialog = false
              scope.launch {
                snackbarHostState.showSnackbar("Added '$destName' to ${targetCity.name} popular destinations!")
              }
            }
          },
          shape = RoundedCornerShape(10.dp)
        ) {
          Text("Add to Destinations")
        }
      },
      dismissButton = {
        TextButton(onClick = { showAddDestinationDialog = false }) {
          Text("Cancel")
        }
      }
    )
  }

  // DIALOG: ADD NEW SERVICE CITY
  if (showAddCityDialog) {
    var cityName by remember { mutableStateOf("") }
    var cityUrdu by remember { mutableStateOf("") }
    var cityTagline by remember { mutableStateOf("Service City") }
    var cityHallmark by remember { mutableStateOf("City Center Landmark") }
    var cityStand by remember { mutableStateOf("Main Adda Stand") }
    var cityStandAddress by remember { mutableStateOf("Main Adda Chowk") }

    AlertDialog(
      onDismissRequest = { showAddCityDialog = false },
      title = { Text("Add New Service City", fontWeight = FontWeight.Bold) },
      text = {
        LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
          item {
            Text(
              text = "Define a new operating city for Sasti Sawari in Pakistan.",
              style = MaterialTheme.typography.bodySmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }

          item {
            OutlinedTextField(
              value = cityName,
              onValueChange = { cityName = it },
              label = { Text("City Name *") },
              placeholder = { Text("e.g. Sahiwal, Okara, Sargodha") },
              singleLine = true,
              modifier = Modifier.fillMaxWidth().testTag("add_city_name_input")
            )
          }

          item {
            OutlinedTextField(
              value = cityUrdu,
              onValueChange = { cityUrdu = it },
              label = { Text("City Name in Urdu") },
              placeholder = { Text("مثلاً ساہیوال") },
              singleLine = true,
              modifier = Modifier.fillMaxWidth()
            )
          }

          item {
            OutlinedTextField(
              value = cityStand,
              onValueChange = { cityStand = it },
              label = { Text("Default Stand Location") },
              placeholder = { Text("General Bus Stand / Adda") },
              singleLine = true,
              modifier = Modifier.fillMaxWidth()
            )
          }

          item {
            OutlinedTextField(
              value = cityStandAddress,
              onValueChange = { cityStandAddress = it },
              label = { Text("Stand Address / Chowk") },
              placeholder = { Text("Main GT Road Chowk") },
              singleLine = true,
              modifier = Modifier.fillMaxWidth()
            )
          }

          item {
            OutlinedTextField(
              value = cityHallmark,
              onValueChange = { cityHallmark = it },
              label = { Text("City Hallmark / Famous Monument") },
              placeholder = { Text("Clock Tower / Yaadgar") },
              singleLine = true,
              modifier = Modifier.fillMaxWidth()
            )
          }

          item {
            OutlinedTextField(
              value = cityTagline,
              onValueChange = { cityTagline = it },
              label = { Text("City Tagline") },
              placeholder = { Text("City of Culture") },
              singleLine = true,
              modifier = Modifier.fillMaxWidth()
            )
          }
        }
      },
      confirmButton = {
        Button(
          onClick = {
            if (cityName.isNotBlank()) {
              viewModel.addCustomServiceCity(
                name = cityName,
                urduName = cityUrdu,
                tagLine = cityTagline,
                hallmarkTitle = cityHallmark,
                hallmarkUrdu = cityUrdu,
                standName = cityStand,
                standAddress = cityStandAddress
              )
              showAddCityDialog = false
              scope.launch {
                snackbarHostState.showSnackbar("Service City '$cityName' added and activated!")
              }
            }
          },
          shape = RoundedCornerShape(10.dp)
        ) {
          Text("Add Service City")
        }
      },
      dismissButton = {
        TextButton(onClick = { showAddCityDialog = false }) {
          Text("Cancel")
        }
      }
    )
  }
}

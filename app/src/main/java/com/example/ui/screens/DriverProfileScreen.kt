package com.example.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DirectionsBike
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Message
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.data.model.AppSettings
import com.example.ui.MainViewModel
import com.example.ui.theme.ImperialGold
import com.example.ui.theme.ImperialGoldBright
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DriverProfileScreen(
  viewModel: MainViewModel,
  snackbarHostState: SnackbarHostState,
  modifier: Modifier = Modifier
) {
  val scope = rememberCoroutineScope()
  val settings by viewModel.settings.collectAsStateWithLifecycle()
  val currentCity by viewModel.currentCity.collectAsStateWithLifecycle()
  val serviceCities by viewModel.serviceCities.collectAsStateWithLifecycle()

  // Form states initialized from settings
  var photoUri by remember(settings.riderPhotoUri) { mutableStateOf(settings.riderPhotoUri) }
  var selectedCityId by remember(settings.selectedCityId) { mutableStateOf(settings.selectedCityId) }
  var driverName by remember(settings.operatorName) { mutableStateOf(settings.operatorName) }
  var driverPhone by remember(settings.operatorPhone) { mutableStateOf(settings.operatorPhone) }
  var driverWhatsApp by remember(settings.operatorWhatsApp) { mutableStateOf(settings.operatorWhatsApp) }
  var bikeModel by remember(settings.motorcycleModel) { mutableStateOf(settings.motorcycleModel) }
  var bikePlate by remember(settings.motorcyclePlate) { mutableStateOf(settings.motorcyclePlate) }
  var standName by remember(settings.standName) { mutableStateOf(settings.standName) }
  var standAddress by remember(settings.standAddress) { mutableStateOf(settings.standAddress) }
  var tagline by remember(settings.riderTagline) { mutableStateOf(settings.riderTagline) }

  var cityDropdownExpanded by remember { mutableStateOf(false) }

  // Zero-permission Android Photo Picker for Driver Photo
  val photoPickerLauncher = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.PickVisualMedia()
  ) { uri: Uri? ->
    if (uri != null) {
      photoUri = uri.toString()
      viewModel.updateRiderPhotoUri(uri.toString())
      scope.launch {
        snackbarHostState.showSnackbar("Profile photo updated successfully!")
      }
    }
  }

  val activeCity = serviceCities.find { it.id == selectedCityId } ?: currentCity

  LazyColumn(
    modifier = modifier
      .fillMaxSize()
      .padding(horizontal = 16.dp),
    verticalArrangement = Arrangement.spacedBy(16.dp)
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
            text = "Driver Profile",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.ExtraBold,
            color = MaterialTheme.colorScheme.primary
          )
          Text(
            text = "Personalize your photo, city, motorcycle, and luxury business card",
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
            Icon(Icons.Default.Verified, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text("Verified Rider", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
          }
        }
      }
    }

    // 1. DRIVER PHOTO UPLOAD CARD
    item {
      Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(
          modifier = Modifier.padding(18.dp),
          horizontalAlignment = Alignment.CenterHorizontally
        ) {
          Text(
            text = "Rider Photo & Identity",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.fillMaxWidth()
          )
          Text(
            text = "Upload a clear photo for your digital business card shown to passengers",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.fillMaxWidth()
          )

          Spacer(modifier = Modifier.height(16.dp))

          // Photo Avatar with Gold Border
          Box(
            modifier = Modifier
              .size(108.dp)
              .clip(CircleShape)
              .background(MaterialTheme.colorScheme.surfaceVariant)
              .border(3.dp, Brush.linearGradient(listOf(ImperialGoldBright, ImperialGold)), CircleShape),
            contentAlignment = Alignment.Center
          ) {
            if (photoUri.isNotBlank()) {
              AsyncImage(
                model = photoUri,
                contentDescription = "Driver Photo",
                modifier = Modifier
                  .size(108.dp)
                  .clip(CircleShape),
                contentScale = ContentScale.Crop
              )
            } else {
              Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                  imageVector = Icons.Default.Person,
                  contentDescription = null,
                  modifier = Modifier.size(54.dp),
                  tint = MaterialTheme.colorScheme.primary
                )
              }
            }
          }

          Spacer(modifier = Modifier.height(14.dp))

          Row(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Button(
              onClick = {
                photoPickerLauncher.launch(
                  PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                )
              },
              shape = RoundedCornerShape(12.dp),
              colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
              modifier = Modifier.testTag("driver_upload_photo_button")
            ) {
              Icon(Icons.Default.CameraAlt, contentDescription = null, modifier = Modifier.size(16.dp))
              Spacer(modifier = Modifier.width(6.dp))
              Text(if (photoUri.isNotBlank()) "Change Photo" else "Upload Photo", fontWeight = FontWeight.Bold)
            }

            if (photoUri.isNotBlank()) {
              OutlinedButton(
                onClick = {
                  photoUri = ""
                  viewModel.updateRiderPhotoUri("")
                },
                shape = RoundedCornerShape(12.dp)
              ) {
                Icon(Icons.Default.Delete, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Remove")
              }
            }
          }
        }
      }
    }

    // 2. OPERATING SERVICE CITY SELECTOR
    item {
      Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(modifier = Modifier.padding(18.dp)) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.LocationOn, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              text = "Operating City",
              style = MaterialTheme.typography.titleMedium,
              fontWeight = FontWeight.Bold
            )
          }
          Spacer(modifier = Modifier.height(4.dp))
          Text(
            text = "Selecting your city sets app title to 'Sasti Sawari <City>', activates local hallmarks, and updates default stand.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )

          Spacer(modifier = Modifier.height(12.dp))

          ExposedDropdownMenuBox(
            expanded = cityDropdownExpanded,
            onExpandedChange = { cityDropdownExpanded = it }
          ) {
            OutlinedTextField(
              value = "${activeCity.name} (${activeCity.urduName}) - ${activeCity.tagLine}",
              onValueChange = {},
              readOnly = true,
              label = { Text("Select Your Operating City") },
              trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = cityDropdownExpanded) },
              modifier = Modifier
                .fillMaxWidth()
                .menuAnchor()
            )

            ExposedDropdownMenu(
              expanded = cityDropdownExpanded,
              onDismissRequest = { cityDropdownExpanded = false }
            ) {
              serviceCities.forEach { c ->
                DropdownMenuItem(
                  text = {
                    Row(
                      modifier = Modifier.fillMaxWidth(),
                      horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                      Text(c.name, fontWeight = if (c.id == selectedCityId) FontWeight.Bold else FontWeight.Normal)
                      Text(c.urduName, color = MaterialTheme.colorScheme.primary)
                    }
                  },
                  onClick = {
                    cityDropdownExpanded = false
                    selectedCityId = c.id
                    standName = c.defaultStandName
                    standAddress = c.defaultStandAddress
                    viewModel.changeCity(c.id)
                  }
                )
              }
            }
          }

          Spacer(modifier = Modifier.height(10.dp))

          Surface(
            shape = RoundedCornerShape(10.dp),
            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
            modifier = Modifier.fillMaxWidth()
          ) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              modifier = Modifier.padding(10.dp)
            ) {
              Icon(Icons.Default.Badge, contentDescription = null, tint = ImperialGold, modifier = Modifier.size(18.dp))
              Spacer(modifier = Modifier.width(8.dp))
              Column {
                Text(
                  text = "City Hallmark: ${activeCity.hallmarkTitle}",
                  fontWeight = FontWeight.Bold,
                  fontSize = 12.sp
                )
                Text(
                  text = activeCity.hallmarkDescription,
                  fontSize = 11.sp,
                  color = MaterialTheme.colorScheme.onSurfaceVariant
                )
              }
            }
          }
        }
      }
    }

    // 3. RIDER & MOTORCYCLE PERSONALIZATION
    item {
      Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Person, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              text = "Personal & Motorcycle Details",
              style = MaterialTheme.typography.titleMedium,
              fontWeight = FontWeight.Bold
            )
          }

          OutlinedTextField(
            value = driverName,
            onValueChange = { driverName = it },
            label = { Text("Driver / Operator Name") },
            placeholder = { Text("Muhammad Rashid") },
            leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
            singleLine = true,
            modifier = Modifier.fillMaxWidth().testTag("profile_name_input")
          )

          Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            OutlinedTextField(
              value = bikeModel,
              onValueChange = { bikeModel = it },
              label = { Text("Bike Model") },
              placeholder = { Text("Honda CD 70") },
              leadingIcon = { Icon(Icons.Default.DirectionsBike, contentDescription = null) },
              singleLine = true,
              modifier = Modifier.weight(1f).testTag("profile_bike_model_input")
            )

            OutlinedTextField(
              value = bikePlate,
              onValueChange = { bikePlate = it },
              label = { Text("Number Plate") },
              placeholder = { Text("MNK-4821") },
              singleLine = true,
              modifier = Modifier.weight(1f).testTag("profile_plate_input")
            )
          }

          Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            OutlinedTextField(
              value = driverPhone,
              onValueChange = { driverPhone = it },
              label = { Text("Call Number") },
              placeholder = { Text("0308-5142950") },
              keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
              leadingIcon = { Icon(Icons.Default.Call, contentDescription = null) },
              singleLine = true,
              modifier = Modifier.weight(1f).testTag("profile_phone_input")
            )

            OutlinedTextField(
              value = driverWhatsApp,
              onValueChange = { driverWhatsApp = it },
              label = { Text("WhatsApp (no +)") },
              placeholder = { Text("923085142950") },
              keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
              leadingIcon = { Icon(Icons.Default.Message, contentDescription = null) },
              singleLine = true,
              modifier = Modifier.weight(1f).testTag("profile_whatsapp_input")
            )
          }

          OutlinedTextField(
            value = standName,
            onValueChange = { standName = it },
            label = { Text("Fixed Stand / Base Location") },
            placeholder = { Text("Ghanta Ghar Stand") },
            leadingIcon = { Icon(Icons.Default.Storefront, contentDescription = null) },
            singleLine = true,
            modifier = Modifier.fillMaxWidth().testTag("profile_stand_name_input")
          )

          OutlinedTextField(
            value = standAddress,
            onValueChange = { standAddress = it },
            label = { Text("Stand Address / Landmark") },
            placeholder = { Text("Ghanta Ghar / Clock Tower, Multan") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth().testTag("profile_stand_address_input")
          )

          OutlinedTextField(
            value = tagline,
            onValueChange = { tagline = it },
            label = { Text("Personal Tagline / Business Slogan") },
            placeholder = { Text("Direct Pickup • Safe Ride • Most Affordable Fares") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth().testTag("profile_tagline_input")
          )
        }
      }
    }

    // 4. LIVE BUSINESS CARD PREVIEW
    item {
      Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(modifier = Modifier.padding(18.dp)) {
          Text(
            text = "Live Business Card Preview",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
          )
          Spacer(modifier = Modifier.height(10.dp))

          // Miniature Card Preview
          Surface(
            shape = RoundedCornerShape(16.dp),
            color = Color(0xFF032B21),
            border = BorderStroke(1.5.dp, Brush.linearGradient(listOf(ImperialGoldBright, Color(0xFF78350F)))),
            modifier = Modifier.fillMaxWidth()
          ) {
            Column(modifier = Modifier.padding(16.dp)) {
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                  Box(
                    modifier = Modifier
                      .size(46.dp)
                      .clip(CircleShape)
                      .background(Color.White.copy(alpha = 0.1f))
                      .border(1.5.dp, ImperialGold, CircleShape),
                    contentAlignment = Alignment.Center
                  ) {
                    if (photoUri.isNotBlank()) {
                      AsyncImage(
                        model = photoUri,
                        contentDescription = null,
                        modifier = Modifier.size(46.dp).clip(CircleShape),
                        contentScale = ContentScale.Crop
                      )
                    } else {
                      Icon(Icons.Default.Person, contentDescription = null, tint = ImperialGold, modifier = Modifier.size(24.dp))
                    }
                  }
                  Spacer(modifier = Modifier.width(10.dp))
                  Column {
                    Text("SASTI SAWARI ${activeCity.name.uppercase()}", fontWeight = FontWeight.ExtraBold, color = ImperialGold, fontSize = 14.sp)
                    Text(driverName.ifBlank { "Muhammad Rashid" }, fontWeight = FontWeight.Bold, color = Color.White, fontSize = 13.sp)
                  }
                }

                Surface(shape = RoundedCornerShape(6.dp), color = ImperialGold) {
                  Text(bikePlate.ifBlank { "MNK-4821" }, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.Black, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                }
              }

              Spacer(modifier = Modifier.height(10.dp))
              Text("📍 ${standName.ifBlank { activeCity.defaultStandName }}", color = Color.White.copy(alpha = 0.9f), fontSize = 11.sp)
              Text("📞 Call: ${driverPhone.ifBlank { "0308-5142950" }} • 💬 WhatsApp: ${driverWhatsApp.ifBlank { "923085142950" }}", color = Color.White.copy(alpha = 0.8f), fontSize = 10.sp)
              Text("🏛️ Hallmark: ${activeCity.hallmarkTitle}", color = Color(0xFFFCD34D), fontSize = 10.sp)
            }
          }
        }
      }
    }

    // 5. SAVE PROFILE BUTTON
    item {
      Button(
        onClick = {
          viewModel.updateDriverProfile(
            photoUri = photoUri,
            cityId = selectedCityId,
            name = driverName,
            phone = driverPhone,
            whatsapp = driverWhatsApp,
            bikeModel = bikeModel,
            plate = bikePlate,
            tagline = tagline,
            standName = standName,
            standAddress = standAddress
          )
          scope.launch {
            snackbarHostState.showSnackbar("Driver Profile & Business Card updated successfully!")
          }
        },
        modifier = Modifier
          .fillMaxWidth()
          .height(52.dp)
          .testTag("save_driver_profile_button"),
        shape = RoundedCornerShape(14.dp),
        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
      ) {
        Icon(Icons.Default.Save, contentDescription = null)
        Spacer(modifier = Modifier.width(8.dp))
        Text("Save Driver Profile", fontWeight = FontWeight.Bold, fontSize = 15.sp)
      }
      Spacer(modifier = Modifier.height(20.dp))
    }
  }
}

package com.example.ui.screens

import android.content.Context
import android.content.Intent
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DirectionsBike
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Message
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
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
import com.example.data.model.AppSettings
import com.example.data.model.CityInfo
import com.example.ui.MainViewModel
import com.example.ui.components.shareText
import com.example.ui.theme.CashGreen
import com.example.ui.theme.ImperialGold
import com.example.ui.theme.ImperialGoldBright
import kotlinx.coroutines.launch

@Composable
fun RiderBusinessCardScreen(
  viewModel: MainViewModel,
  snackbarHostState: SnackbarHostState,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  val scope = rememberCoroutineScope()
  val settings by viewModel.settings.collectAsStateWithLifecycle()
  val city by viewModel.currentCity.collectAsStateWithLifecycle()
  val appTitle by viewModel.appTitleWithCity.collectAsStateWithLifecycle()

  var showEditProfileDialog by remember { mutableStateOf(false) }

  // Photo Picker launcher compliant with Google Play policy
  val photoPickerLauncher = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.PickVisualMedia()
  ) { uri: Uri? ->
    if (uri != null) {
      viewModel.updateRiderPhotoUri(uri.toString())
      scope.launch {
        snackbarHostState.showSnackbar("Rider photo updated successfully!")
      }
    }
  }

  Column(
    modifier = modifier
      .fillMaxSize()
      .verticalScroll(rememberScrollState())
      .padding(horizontal = 16.dp),
    verticalArrangement = Arrangement.spacedBy(16.dp)
  ) {
    Spacer(modifier = Modifier.height(4.dp))

    // Header Title
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Column {
        Text(
          text = "Digital Business Card",
          style = MaterialTheme.typography.headlineSmall,
          fontWeight = FontWeight.Bold
        )
        Text(
          text = "Share with customers via WhatsApp or show on stand",
          style = MaterialTheme.typography.bodySmall,
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )
      }

      IconButton(
        onClick = { showEditProfileDialog = true },
        modifier = Modifier.testTag("edit_branding_icon_button")
      ) {
        Icon(
          imageVector = Icons.Default.Edit,
          contentDescription = "Edit Profile & Branding",
          tint = MaterialTheme.colorScheme.primary
        )
      }
    }

    // 1. THE LUXURY BUSINESS CARD
    Surface(
      shape = RoundedCornerShape(26.dp),
      color = Color(0xFF062016),
      border = BorderStroke(2.dp, Brush.linearGradient(listOf(ImperialGoldBright, Color(0xFF78350F)))),
      shadowElevation = 8.dp,
      modifier = Modifier
        .fillMaxWidth()
        .testTag("premium_business_card")
    ) {
      Box(
        modifier = Modifier
          .background(
            Brush.verticalGradient(
              colors = listOf(
                Color(0xFF064E3B),
                Color(0xFF042F24),
                Color(0xFF0B1713)
              )
            )
          )
          .padding(20.dp)
      ) {
        Column {
          // Top Row: App Name + City Badge + Official Hallmark Seal
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Column {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                  text = appTitle,
                  style = MaterialTheme.typography.titleLarge,
                  fontWeight = FontWeight.ExtraBold,
                  color = Color.White,
                  letterSpacing = 0.5.sp
                )
                Spacer(modifier = Modifier.width(6.dp))
                Icon(
                  imageVector = Icons.Default.Verified,
                  contentDescription = "Verified Rider",
                  tint = ImperialGoldBright,
                  modifier = Modifier.size(18.dp)
                )
              }
              Text(
                text = "${city.name} Official Stand Service • ${city.tagLine}",
                style = MaterialTheme.typography.bodySmall,
                color = Color.White.copy(alpha = 0.8f)
              )
            }

            // City Hallmark Emblem Pill
            Surface(
              shape = RoundedCornerShape(12.dp),
              color = Color.White.copy(alpha = 0.12f),
              border = BorderStroke(1.dp, ImperialGoldBright.copy(alpha = 0.7f))
            ) {
              Column(
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                horizontalAlignment = Alignment.CenterHorizontally
              ) {
                Text(
                  text = city.urduName,
                  color = Color(0xFFFCD34D),
                  fontSize = 12.sp,
                  fontWeight = FontWeight.Bold
                )
                Text(
                  text = city.name.uppercase(),
                  color = Color.White.copy(alpha = 0.85f),
                  fontSize = 9.sp,
                  fontWeight = FontWeight.SemiBold
                )
              }
            }
          }

          Spacer(modifier = Modifier.height(18.dp))

          // Middle Row: Rider Photo + Name + Bike Info
          Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
          ) {
            // Driver Profile Photo with Gold Ring & Camera Icon for Upload
            Box(
              modifier = Modifier
                .size(76.dp)
                .border(2.5.dp, ImperialGoldBright, CircleShape)
                .clip(CircleShape)
                .background(Color(0xFF1E293B))
                .clickable {
                  photoPickerLauncher.launch(
                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                  )
                }
                .testTag("driver_photo_avatar"),
              contentAlignment = Alignment.Center
            ) {
              if (settings.riderPhotoUri.isNotBlank()) {
                AsyncImage(
                  model = settings.riderPhotoUri,
                  contentDescription = "Driver Photo",
                  modifier = Modifier.fillMaxSize(),
                  contentScale = ContentScale.Crop
                )
              } else {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                  Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = null,
                    tint = Color.White.copy(alpha = 0.6f),
                    modifier = Modifier.size(36.dp)
                  )
                  Text(
                    text = "Upload",
                    fontSize = 9.sp,
                    color = Color(0xFFFCD34D),
                    fontWeight = FontWeight.Bold
                  )
                }
              }
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
              Text(
                text = settings.operatorName,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Color.White
              )
              Text(
                text = "Registered Bike Operator",
                style = MaterialTheme.typography.bodySmall,
                color = Color(0xFFFCD34D),
                fontWeight = FontWeight.SemiBold
              )
              Spacer(modifier = Modifier.height(4.dp))
              Text(
                text = "${settings.motorcycleModel} • ${settings.motorcyclePlate}",
                style = MaterialTheme.typography.bodySmall,
                color = Color.White.copy(alpha = 0.85f)
              )
            }
          }

          Spacer(modifier = Modifier.height(16.dp))

          // City Hallmark Feature Box
          Surface(
            shape = RoundedCornerShape(14.dp),
            color = Color.White.copy(alpha = 0.08f),
            border = BorderStroke(1.dp, Color.White.copy(alpha = 0.15f)),
            modifier = Modifier.fillMaxWidth()
          ) {
            Row(
              modifier = Modifier.padding(12.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Box(
                modifier = Modifier
                  .size(36.dp)
                  .background(ImperialGold.copy(alpha = 0.3f), CircleShape),
                contentAlignment = Alignment.Center
              ) {
                Icon(
                  imageVector = Icons.Default.Star,
                  contentDescription = null,
                  tint = ImperialGoldBright,
                  modifier = Modifier.size(20.dp)
                )
              }
              Spacer(modifier = Modifier.width(10.dp))
              Column {
                Text(
                  text = "City Hallmark: ${city.hallmarkTitle}",
                  style = MaterialTheme.typography.labelSmall,
                  fontWeight = FontWeight.Bold,
                  color = Color(0xFFFCD34D)
                )
                Text(
                  text = "${city.hallmarkUrdu} — ${city.hallmarkDescription}",
                  style = MaterialTheme.typography.bodySmall,
                  color = Color.White.copy(alpha = 0.8f),
                  fontSize = 11.sp
                )
              }
            }
          }

          Spacer(modifier = Modifier.height(14.dp))

          // Stand Location & Tagline
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
              imageVector = Icons.Default.LocationOn,
              contentDescription = null,
              tint = ImperialGoldBright,
              modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
              text = "${settings.standName} • ${settings.standAddress}",
              style = MaterialTheme.typography.bodySmall,
              color = Color.White,
              fontWeight = FontWeight.Medium
            )
          }

          Spacer(modifier = Modifier.height(6.dp))
          Text(
            text = "Starting Rs. ${settings.baseFare.toInt()} (First 2 km) • Rs. ${settings.ratePerAdditionalKm.toInt()}/km • 5 min free wait",
            style = MaterialTheme.typography.labelSmall,
            color = Color(0xFF6EE7B7),
            fontWeight = FontWeight.Bold
          )

          Spacer(modifier = Modifier.height(16.dp))

          // Card Direct CTA Buttons (Call 0308-5142950 & WhatsApp 03085142950)
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
          ) {
            Button(
              onClick = { dialPhone(context, settings.operatorPhone) },
              modifier = Modifier
                .weight(1f)
                .height(44.dp)
                .testTag("card_call_button"),
              colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF1E3A8A),
                contentColor = Color.White
              ),
              shape = RoundedCornerShape(12.dp)
            ) {
              Icon(Icons.Default.Call, contentDescription = null, modifier = Modifier.size(16.dp))
              Spacer(modifier = Modifier.width(6.dp))
              Text("0308-5142950", fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }

            Button(
              onClick = {
                openWhatsApp(
                  context,
                  settings.operatorWhatsApp,
                  "Assalam-o-Alaikum! I want to book a ride with Sasti Sawari ${city.name}."
                )
              },
              modifier = Modifier
                .weight(1f)
                .height(44.dp)
                .testTag("card_whatsapp_button"),
              colors = ButtonDefaults.buttonColors(
                containerColor = CashGreen,
                contentColor = Color.White
              ),
              shape = RoundedCornerShape(12.dp)
            ) {
              Icon(Icons.Default.Message, contentDescription = null, modifier = Modifier.size(16.dp))
              Spacer(modifier = Modifier.width(6.dp))
              Text("WhatsApp", fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
          }
        }
      }
    }

    // 2. Action Buttons: Share via WhatsApp & Upload Photo
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
      Button(
        onClick = {
          val cardText = """
            🌟 *${appTitle.uppercase()}* 🌟
            *${city.tagLine}*
            ------------------------------------------
            🏍️ *Rider:* ${settings.operatorName}
            🛵 *Bike:* ${settings.motorcycleModel} (${settings.motorcyclePlate})
            📍 *City Hallmark:* ${city.hallmarkTitle}
            🏛️ *Stand Location:* ${settings.standName}, ${city.name}
            ------------------------------------------
            💰 *FARES:*
            • Minimum Fare: Rs. ${settings.baseFare.toInt()} (First 2 km)
            • Extra Distance: Rs. ${settings.ratePerAdditionalKm.toInt()} per km
            • Free Waiting: First 5 minutes free
            • Waiting Rate: Rs. ${settings.waitingRatePerMinute.toInt()} per minute
            ------------------------------------------
            📞 *Direct Call:* ${settings.operatorPhone}
            💬 *WhatsApp:* https://wa.me/${settings.operatorWhatsApp}
            ------------------------------------------
            _Safe • Reliable • Affordable Sasti Sawari_
          """.trimIndent()
          shareText(context, cardText, "Sasti Sawari Business Card")
        },
        modifier = Modifier
          .weight(1.2f)
          .height(52.dp)
          .testTag("share_business_card_button"),
        colors = ButtonDefaults.buttonColors(containerColor = CashGreen),
        shape = RoundedCornerShape(14.dp)
      ) {
        Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(18.dp))
        Spacer(modifier = Modifier.width(6.dp))
        Text("Share Card on WhatsApp", fontWeight = FontWeight.Bold, fontSize = 13.sp)
      }

      OutlinedButton(
        onClick = {
          photoPickerLauncher.launch(
            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
          )
        },
        modifier = Modifier
          .weight(0.9f)
          .height(52.dp)
          .testTag("upload_driver_photo_button"),
        shape = RoundedCornerShape(14.dp)
      ) {
        Icon(Icons.Default.CameraAlt, contentDescription = null, modifier = Modifier.size(18.dp))
        Spacer(modifier = Modifier.width(4.dp))
        Text("Upload Photo", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
      }
    }

    // 3. City Hallmark Details Card
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
          Column {
            Text(
              text = "Hallmark of ${city.name}",
              style = MaterialTheme.typography.titleMedium,
              fontWeight = FontWeight.Bold
            )
            Text(
              text = city.hallmarkUrdu,
              style = MaterialTheme.typography.bodySmall,
              color = MaterialTheme.colorScheme.primary,
              fontWeight = FontWeight.SemiBold
            )
          }

          Surface(
            shape = RoundedCornerShape(8.dp),
            color = MaterialTheme.colorScheme.primaryContainer
          ) {
            Text(
              text = city.name,
              style = MaterialTheme.typography.labelSmall,
              fontWeight = FontWeight.Bold,
              color = MaterialTheme.colorScheme.onPrimaryContainer,
              modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
            )
          }
        }

        Spacer(modifier = Modifier.height(10.dp))

        Text(
          text = city.hallmarkDescription,
          style = MaterialTheme.typography.bodyMedium,
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(10.dp))

        Text(
          text = "Every rider can customize their name, bike number plate, phone, and photo. The city hallmark is customized automatically when switching cities.",
          style = MaterialTheme.typography.bodySmall,
          color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
        )
      }
    }

    Spacer(modifier = Modifier.height(80.dp))
  }

  // Edit Rider Profile & Custom Branding Dialog
  if (showEditProfileDialog) {
    EditRiderProfileDialog(
      settings = settings,
      onDismiss = { showEditProfileDialog = false },
      onSave = { name, phone, whatsapp, bike, plate, tagline, stand, address ->
        viewModel.updateRiderBranding(name, phone, whatsapp, bike, plate, tagline, stand, address)
        showEditProfileDialog = false
        scope.launch {
          snackbarHostState.showSnackbar("Branding updated successfully!")
        }
      }
    )
  }
}

@Composable
fun EditRiderProfileDialog(
  settings: AppSettings,
  onDismiss: () -> Unit,
  onSave: (
    name: String,
    phone: String,
    whatsapp: String,
    bike: String,
    plate: String,
    tagline: String,
    stand: String,
    address: String
  ) -> Unit
) {
  var name by remember { mutableStateOf(settings.operatorName) }
  var phone by remember { mutableStateOf(settings.operatorPhone) }
  var whatsapp by remember { mutableStateOf(settings.operatorWhatsApp) }
  var bike by remember { mutableStateOf(settings.motorcycleModel) }
  var plate by remember { mutableStateOf(settings.motorcyclePlate) }
  var tagline by remember { mutableStateOf(settings.riderTagline) }
  var stand by remember { mutableStateOf(settings.standName) }
  var address by remember { mutableStateOf(settings.standAddress) }

  AlertDialog(
    onDismissRequest = onDismiss,
    title = { Text("Customize Rider Branding") },
    text = {
      Column(
        modifier = Modifier.verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        Text(
          text = "You can customize all personal and motorcycle branding. (App name 'Sasti Sawari' remains permanent)",
          style = MaterialTheme.typography.bodySmall,
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        OutlinedTextField(
          value = name,
          onValueChange = { name = it },
          label = { Text("Rider / Operator Name") },
          singleLine = true,
          modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
          value = phone,
          onValueChange = { phone = it },
          label = { Text("Call Phone (0308-5142950)") },
          singleLine = true,
          modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
          value = whatsapp,
          onValueChange = { whatsapp = it },
          label = { Text("WhatsApp (923085142950)") },
          singleLine = true,
          modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
          value = bike,
          onValueChange = { bike = it },
          label = { Text("Motorcycle Model") },
          singleLine = true,
          modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
          value = plate,
          onValueChange = { plate = it },
          label = { Text("Number Plate") },
          singleLine = true,
          modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
          value = stand,
          onValueChange = { stand = it },
          label = { Text("Pickup Stand Name") },
          singleLine = true,
          modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
          value = address,
          onValueChange = { address = it },
          label = { Text("Stand Address") },
          singleLine = true,
          modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
          value = tagline,
          onValueChange = { tagline = it },
          label = { Text("Custom Tagline") },
          singleLine = true,
          modifier = Modifier.fillMaxWidth()
        )
      }
    },
    confirmButton = {
      Button(
        onClick = {
          onSave(name, phone, whatsapp, bike, plate, tagline, stand, address)
        }
      ) {
        Text("Save Branding")
      }
    },
    dismissButton = {
      TextButton(onClick = onDismiss) {
        Text("Cancel")
      }
    }
  )
}

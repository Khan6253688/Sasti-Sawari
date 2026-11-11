package com.example.ui.screens

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DirectionsBike
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Message
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AppSettings
import com.example.data.model.CityInfo
import com.example.data.model.PakistanCitiesData
import com.example.ui.theme.CashGreen
import com.example.ui.theme.ImperialGold
import com.example.ui.theme.ImperialGoldBright

@Composable
fun PublicStandCard(
  settings: AppSettings,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  val city = PakistanCitiesData.getCityById(settings.selectedCityId)

  Column(
    modifier = modifier
      .fillMaxSize()
      .verticalScroll(rememberScrollState())
      .padding(horizontal = 16.dp),
    verticalArrangement = Arrangement.spacedBy(16.dp)
  ) {
    Spacer(modifier = Modifier.height(4.dp))

    // Poster Header Board
    Surface(
      shape = RoundedCornerShape(24.dp),
      color = Color(0xFF062016),
      border = BorderStroke(2.dp, Brush.linearGradient(listOf(ImperialGoldBright, Color(0xFF78350F)))),
      shadowElevation = 6.dp,
      modifier = Modifier
        .fillMaxWidth()
        .testTag("public_stand_poster")
    ) {
      Box(
        modifier = Modifier
          .background(
            Brush.verticalGradient(
              colors = listOf(
                Color(0xFF044E3B),
                Color(0xFF03281E),
                Color(0xFF0A1410)
              )
            )
          )
          .padding(24.dp)
      ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
          Box(
            modifier = Modifier
              .size(68.dp)
              .background(Color.White.copy(alpha = 0.15f), CircleShape),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              imageVector = Icons.Default.DirectionsBike,
              contentDescription = null,
              tint = Color(0xFFFCD34D),
              modifier = Modifier.size(38.dp)
            )
          }

          Spacer(modifier = Modifier.height(14.dp))

          Text(
            text = "SASTI SAWARI ${city.name.uppercase()}",
            color = Color.White,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.ExtraBold,
            textAlign = TextAlign.Center,
            letterSpacing = 0.8.sp
          )

          Text(
            text = "${city.urduName} • ${city.tagLine}",
            color = Color(0xFFFCD34D),
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
          )

          Spacer(modifier = Modifier.height(6.dp))

          Text(
            text = settings.riderTagline,
            color = Color.White.copy(alpha = 0.85f),
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.Center
          )

          Spacer(modifier = Modifier.height(18.dp))

          // Hallmark Feature Card
          Surface(
            shape = RoundedCornerShape(12.dp),
            color = Color.White.copy(alpha = 0.1f),
            modifier = Modifier.fillMaxWidth()
          ) {
            Row(
              modifier = Modifier.padding(10.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Icon(
                imageVector = Icons.Default.Star,
                contentDescription = null,
                tint = ImperialGoldBright,
                modifier = Modifier.size(18.dp)
              )
              Spacer(modifier = Modifier.width(8.dp))
              Text(
                text = "${city.name} Hallmark: ${city.hallmarkTitle}",
                color = Color.White,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold
              )
            }
          }

          Spacer(modifier = Modifier.height(16.dp))

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
                text = "Starting Rs. ${settings.baseFare.toInt()}",
                color = Color(0xFFFCD34D),
                fontSize = 34.sp,
                fontWeight = FontWeight.ExtraBold
              )
              Text(
                text = "Includes first ${settings.baseKm.toInt()} km",
                color = Color.White.copy(alpha = 0.75f),
                fontSize = 13.sp
              )

              Spacer(modifier = Modifier.height(12.dp))
              Surface(
                modifier = Modifier
                  .fillMaxWidth()
                  .height(1.dp),
                color = Color.White.copy(alpha = 0.15f)
              ) {}
              Spacer(modifier = Modifier.height(12.dp))

              Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                StandBenefitRow("Rs. ${settings.ratePerAdditionalKm.toInt()} per additional kilometre")
                StandBenefitRow("First ${settings.freeWaitingMinutes} minutes waiting free")
                StandBenefitRow("Rs. ${settings.waitingRatePerMinute.toInt()} per additional waiting minute")
                StandBenefitRow("Reliable ${settings.motorcycleModel} • ${settings.motorcyclePlate}")
              }
            }
          }

          Spacer(modifier = Modifier.height(18.dp))

          // Stand Location Card inside Poster
          Surface(
            shape = RoundedCornerShape(14.dp),
            color = Color.White.copy(alpha = 0.1f),
            modifier = Modifier.fillMaxWidth()
          ) {
            Row(
              modifier = Modifier.padding(14.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Icon(
                imageVector = Icons.Default.LocationOn,
                contentDescription = null,
                tint = Color(0xFFFCD34D),
                modifier = Modifier.size(24.dp)
              )
              Spacer(modifier = Modifier.width(10.dp))
              Column {
                Text(
                  text = "Fixed Stand Location (${city.name}):",
                  color = Color.White.copy(alpha = 0.7f),
                  fontSize = 11.sp
                )
                Text(
                  text = settings.standName,
                  color = Color.White,
                  fontWeight = FontWeight.Bold,
                  fontSize = 14.sp
                )
                Text(
                  text = settings.standAddress,
                  color = Color.White.copy(alpha = 0.8f),
                  fontSize = 12.sp
                )
              }
            }
          }
        }
      }
    }

    // Contact & Booking Action Buttons (0308-5142950 & WhatsApp 03085142950)
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
      Button(
        onClick = { dialPhone(context, "03085142950") },
        modifier = Modifier
          .weight(1f)
          .height(52.dp)
          .testTag("stand_call_operator_button"),
        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
        shape = RoundedCornerShape(14.dp)
      ) {
        Icon(Icons.Default.Call, contentDescription = null, modifier = Modifier.size(18.dp))
        Spacer(modifier = Modifier.width(6.dp))
        Text("0308-5142950", fontWeight = FontWeight.Bold, fontSize = 13.sp)
      }

      Button(
        onClick = {
          openWhatsApp(
            context,
            "923085142950",
            "Assalam-o-Alaikum! I want to request a Sasti Sawari bike ride from ${settings.standName}, ${city.name}."
          )
        },
        modifier = Modifier
          .weight(1f)
          .height(52.dp)
          .testTag("stand_whatsapp_button"),
        colors = ButtonDefaults.buttonColors(containerColor = CashGreen),
        shape = RoundedCornerShape(14.dp)
      ) {
        Icon(Icons.Default.Message, contentDescription = null, modifier = Modifier.size(18.dp))
        Spacer(modifier = Modifier.width(6.dp))
        Text("WhatsApp", fontWeight = FontWeight.Bold)
      }
    }

    // Transparent Reference Chart
    Card(
      shape = RoundedCornerShape(18.dp),
      colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
      elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
      modifier = Modifier.fillMaxWidth()
    ) {
      Column(modifier = Modifier.padding(16.dp)) {
        Text(
          text = "Official Sasti Sawari ${city.name} Fare Chart",
          style = MaterialTheme.typography.titleMedium,
          fontWeight = FontWeight.Bold
        )
        Text(
          text = "Direct pickup • Verified rider • Fixed transparent pricing",
          style = MaterialTheme.typography.bodySmall,
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(12.dp))

        val distances = listOf(2.0, 3.0, 4.0, 5.0, 6.0, 8.0, 10.0, 15.0)
        distances.forEach { km ->
          val fare = if (km <= settings.baseKm) settings.baseFare else settings.baseFare + ((km - settings.baseKm) * settings.ratePerAdditionalKm)
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(vertical = 6.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text(
              text = "${km.toInt()} Kilometres trip",
              style = MaterialTheme.typography.bodyMedium
            )
            Text(
              text = "Rs. ${fare.toInt()}",
              style = MaterialTheme.typography.titleSmall,
              fontWeight = FontWeight.Bold,
              color = MaterialTheme.colorScheme.primary
            )
          }
        }
      }
    }

    Spacer(modifier = Modifier.height(80.dp))
  }
}

@Composable
fun StandBenefitRow(text: String) {
  Row(
    verticalAlignment = Alignment.CenterVertically,
    modifier = Modifier.fillMaxWidth()
  ) {
    Icon(
      imageVector = Icons.Default.Check,
      contentDescription = null,
      tint = Color(0xFF34D399),
      modifier = Modifier.size(16.dp)
    )
    Spacer(modifier = Modifier.width(8.dp))
    Text(
      text = text,
      color = Color.White.copy(alpha = 0.9f),
      fontSize = 13.sp
    )
  }
}

fun dialPhone(context: Context, phoneNumber: String) {
  val cleanNumber = phoneNumber.replace("[^0-9+]".toRegex(), "")
  val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$cleanNumber"))
  context.startActivity(intent)
}

fun openWhatsApp(context: Context, phone: String, message: String) {
  try {
    val cleanPhone = phone.replace("[^0-9]".toRegex(), "")
    val url = "https://api.whatsapp.com/send?phone=$cleanPhone&text=${Uri.encode(message)}"
    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
    context.startActivity(intent)
  } catch (e: Exception) {
    dialPhone(context, phone)
  }
}

package com.example.ui.screens

import android.content.Context
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.DirectionsBike
import androidx.compose.material.icons.filled.LocalGasStation
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Message
import androidx.compose.material.icons.filled.Money
import androidx.compose.material.icons.filled.PinDrop
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.TrendingUp
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
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.Ride
import com.example.ui.MainViewModel
import com.example.ui.components.PaymentBadge
import com.example.ui.components.StatCard
import com.example.ui.theme.CashGreen
import com.example.ui.theme.ImperialGold
import com.example.ui.theme.ImperialGoldBright
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun DashboardScreen(
  viewModel: MainViewModel,
  onNavigateToNewRide: () -> Unit,
  onNavigateToCalculator: () -> Unit,
  onNavigateToCard: () -> Unit,
  onNavigateToStandBoard: () -> Unit,
  onNavigateToRideDetail: (Ride) -> Unit,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  val settings by viewModel.settings.collectAsStateWithLifecycle()
  val city by viewModel.currentCity.collectAsStateWithLifecycle()
  val appTitle by viewModel.appTitleWithCity.collectAsStateWithLifecycle()
  val allRides by viewModel.allRides.collectAsStateWithLifecycle()
  val allExpenses by viewModel.allExpenses.collectAsStateWithLifecycle()

  val todayStats = viewModel.getTodayStats()
  val weeklyStats = viewModel.getWeeklyStats()
  val monthlyStats = viewModel.getMonthlyStats()
  val topDestinations = viewModel.getTopDestinations(5)
  val recentRides = allRides.take(5)

  LazyColumn(
    modifier = modifier
      .fillMaxSize()
      .padding(horizontal = 16.dp),
    verticalArrangement = Arrangement.spacedBy(16.dp)
  ) {
    // 1. Hero Stand & Operator Header Card (Sasti Sawari <City>)
    item {
      Spacer(modifier = Modifier.height(4.dp))
      Surface(
        shape = RoundedCornerShape(22.dp),
        color = Color(0xFF062016),
        shadowElevation = 4.dp,
        modifier = Modifier.fillMaxWidth()
      ) {
        Box(
          modifier = Modifier
            .background(
              Brush.verticalGradient(
                colors = listOf(
                  Color(0xFF044E3B),
                  Color(0xFF063A2E),
                  Color(0xFF0B1713)
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
                    .size(42.dp)
                    .background(Color.White.copy(alpha = 0.15f), CircleShape),
                  contentAlignment = Alignment.Center
                ) {
                  Icon(
                    imageVector = Icons.Default.DirectionsBike,
                    contentDescription = null,
                    tint = Color(0xFFFCD34D),
                    modifier = Modifier.size(24.dp)
                  )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                  Text(
                    text = appTitle,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White
                  )
                  Text(
                    text = "${settings.motorcycleModel} • ${settings.motorcyclePlate}",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White.copy(alpha = 0.85f)
                  )
                }
              }

              Surface(
                shape = RoundedCornerShape(8.dp),
                color = ImperialGold
              ) {
                Text(
                  text = city.urduName,
                  fontSize = 12.sp,
                  fontWeight = FontWeight.Bold,
                  color = Color.White,
                  modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
              }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(
                imageVector = Icons.Default.LocationOn,
                contentDescription = null,
                tint = Color(0xFFFCD34D),
                modifier = Modifier.size(16.dp)
              )
              Spacer(modifier = Modifier.width(6.dp))
              Text(
                text = "${settings.standName} • ${settings.standAddress}",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                color = Color.White
              )
            }

            Spacer(modifier = Modifier.height(4.dp))
            Text(
              text = "Operator: ${settings.operatorName} • Hallmark: ${city.hallmarkTitle}",
              style = MaterialTheme.typography.bodySmall,
              color = Color.White.copy(alpha = 0.8f)
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Quick Call & WhatsApp 03085142950 bar
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
              Button(
                onClick = { dialPhone(context, "03085142950") },
                modifier = Modifier
                  .weight(1f)
                  .height(38.dp),
                colors = ButtonDefaults.buttonColors(
                  containerColor = Color.White.copy(alpha = 0.2f),
                  contentColor = Color.White
                ),
                shape = RoundedCornerShape(10.dp)
              ) {
                Icon(Icons.Default.Call, contentDescription = null, modifier = Modifier.size(14.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("0308-5142950", fontSize = 11.sp, fontWeight = FontWeight.Bold)
              }

              Button(
                onClick = {
                  openWhatsApp(
                    context,
                    "923085142950",
                    "Assalam-o-Alaikum! I want to request a Sasti Sawari ride in ${city.name}."
                  )
                },
                modifier = Modifier
                  .weight(1f)
                  .height(38.dp),
                colors = ButtonDefaults.buttonColors(
                  containerColor = CashGreen,
                  contentColor = Color.White
                ),
                shape = RoundedCornerShape(10.dp)
              ) {
                Icon(Icons.Default.Message, contentDescription = null, modifier = Modifier.size(14.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("WhatsApp", fontSize = 11.sp, fontWeight = FontWeight.Bold)
              }
            }
          }
        }
      }
    }

    // 1b. Subscription Status Banner (30-Day Rs. 100 Access)
    item {
      val currentAccount by viewModel.currentRider.collectAsStateWithLifecycle()
      val rider = currentAccount
      val now = System.currentTimeMillis()
      val daysLeft = if (rider != null && rider.expiresAt > now) {
        ((rider.expiresAt - now) / (1000 * 60 * 60 * 24)).toInt()
      } else 0

      Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
        modifier = Modifier.fillMaxWidth()
      ) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp, vertical = 10.dp),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
              modifier = Modifier
                .size(8.dp)
                .background(CashGreen, CircleShape)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              text = "30-Day Plan: Active ($daysLeft days remaining)",
              fontWeight = FontWeight.Bold,
              fontSize = 12.sp,
              color = MaterialTheme.colorScheme.onSurface
            )
          }

          Text(
            text = "Rs. 100/mo",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
          )
        }
      }
    }

    // 2. Quick Action Buttons
    item {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        Button(
          onClick = onNavigateToCalculator,
          modifier = Modifier
            .weight(1.1f)
            .height(52.dp)
            .testTag("dashboard_calculator_button"),
          colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
          shape = RoundedCornerShape(14.dp)
        ) {
          Icon(Icons.Default.Calculate, contentDescription = null, modifier = Modifier.size(18.dp))
          Spacer(modifier = Modifier.width(4.dp))
          Text(text = "Calculator", fontSize = 13.sp, fontWeight = FontWeight.Bold)
        }

        Button(
          onClick = onNavigateToNewRide,
          modifier = Modifier
            .weight(1.1f)
            .height(52.dp)
            .testTag("dashboard_new_ride_button"),
          colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary),
          shape = RoundedCornerShape(14.dp)
        ) {
          Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
          Spacer(modifier = Modifier.width(4.dp))
          Text(text = "New Ride", fontSize = 13.sp, fontWeight = FontWeight.Bold)
        }

        OutlinedButton(
          onClick = onNavigateToCard,
          modifier = Modifier
            .weight(0.9f)
            .height(52.dp)
            .testTag("dashboard_card_button"),
          shape = RoundedCornerShape(14.dp)
        ) {
          Icon(Icons.Default.Badge, contentDescription = null, modifier = Modifier.size(16.dp))
          Spacer(modifier = Modifier.width(4.dp))
          Text(text = "Card", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
        }
      }
    }

    // 3. Today's Performance Header
    item {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = "Today's Business Summary",
          style = MaterialTheme.typography.titleMedium,
          fontWeight = FontWeight.Bold
        )
        Text(
          text = SimpleDateFormat("EEEE, d MMM", Locale.getDefault()).format(Date()),
          style = MaterialTheme.typography.bodySmall,
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )
      }
    }

    // 4. Primary KPI Highlights (Total Revenue & Net Income)
    item {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
      ) {
        StatCard(
          title = "Today's Revenue",
          value = "Rs. ${todayStats.totalRevenue.toInt()}",
          subtitle = "${todayStats.rideCount} rides completed",
          containerColor = MaterialTheme.colorScheme.primaryContainer,
          contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
          icon = {
            Icon(
              imageVector = Icons.Default.Money,
              contentDescription = null,
              tint = MaterialTheme.colorScheme.primary
            )
          },
          modifier = Modifier.weight(1f)
        )

        StatCard(
          title = "Est. Net Income",
          value = "Rs. ${todayStats.netIncome.toInt()}",
          subtitle = "After fuel & expenses",
          containerColor = CashGreen.copy(alpha = 0.12f),
          contentColor = MaterialTheme.colorScheme.onSurface,
          icon = {
            Icon(
              imageVector = Icons.Default.TrendingUp,
              contentDescription = null,
              tint = CashGreen
            )
          },
          modifier = Modifier.weight(1f)
        )
      }
    }

    // 5. Secondary KPI Grid (Avg Fare, Distance, Waiting, Fuel)
    item {
      Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          StatCard(
            title = "Average Fare",
            value = "Rs. ${todayStats.averageFare.toInt()}",
            subtitle = "per trip today",
            modifier = Modifier.weight(1f)
          )

          StatCard(
            title = "Total Distance",
            value = "${"%.1f".format(todayStats.totalKm)} km",
            subtitle = "travelled today",
            modifier = Modifier.weight(1f)
          )
        }

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          StatCard(
            title = "Waiting Charges",
            value = "Rs. ${todayStats.totalWaitingCharges.toInt()}",
            subtitle = "collected at Rs. 5/min",
            icon = {
              Icon(
                imageVector = Icons.Default.Timer,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
              )
            },
            modifier = Modifier.weight(1f)
          )

          StatCard(
            title = "Est. Fuel Expense",
            value = "Rs. ${todayStats.estimatedFuelCost.toInt()}",
            subtitle = "@ 40 km/L economy",
            icon = {
              Icon(
                imageVector = Icons.Default.LocalGasStation,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
              )
            },
            modifier = Modifier.weight(1f)
          )
        }
      }
    }

    // 6. Periodic Performance
    item {
      Text(
        text = "Periodic Performance",
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold
      )
    }

    item {
      Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
          containerColor = MaterialTheme.colorScheme.surface
        ),
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
                text = "This Week's Performance",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold
              )
              Text(
                text = "${weeklyStats.rideCount} rides • ${"%.1f".format(weeklyStats.totalKm)} km",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
            }
            Column(horizontalAlignment = Alignment.End) {
              Text(
                text = "Rs. ${weeklyStats.totalRevenue.toInt()}",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
              )
              Text(
                text = "Net: Rs. ${weeklyStats.netIncome.toInt()}",
                style = MaterialTheme.typography.bodySmall,
                color = CashGreen,
                fontWeight = FontWeight.SemiBold
              )
            }
          }

          Spacer(modifier = Modifier.height(14.dp))
          Surface(
            modifier = Modifier
              .fillMaxWidth()
              .height(1.dp),
            color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
          ) {}
          Spacer(modifier = Modifier.height(14.dp))

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Column {
              Text(
                text = "This Month's Performance",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold
              )
              Text(
                text = "${monthlyStats.rideCount} rides • ${"%.1f".format(monthlyStats.totalKm)} km",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
            }
            Column(horizontalAlignment = Alignment.End) {
              Text(
                text = "Rs. ${monthlyStats.totalRevenue.toInt()}",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
              )
              Text(
                text = "Net: Rs. ${monthlyStats.netIncome.toInt()}",
                style = MaterialTheme.typography.bodySmall,
                color = CashGreen,
                fontWeight = FontWeight.SemiBold
              )
            }
          }
        }
      }
    }

    // 7. Frequent Routes
    if (topDestinations.isNotEmpty()) {
      item {
        Text(
          text = "Frequent Routes in ${city.name}",
          style = MaterialTheme.typography.titleMedium,
          fontWeight = FontWeight.Bold
        )
      }

      item {
        Card(
          shape = RoundedCornerShape(16.dp),
          colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
          elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
          modifier = Modifier.fillMaxWidth()
        ) {
          Column(modifier = Modifier.padding(14.dp)) {
            topDestinations.forEachIndexed { index, item ->
              Row(
                modifier = Modifier
                  .fillMaxWidth()
                  .padding(vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Row(
                  verticalAlignment = Alignment.CenterVertically,
                  modifier = Modifier.weight(1f)
                ) {
                  Box(
                    modifier = Modifier
                      .size(24.dp)
                      .background(
                        MaterialTheme.colorScheme.primaryContainer,
                        CircleShape
                      ),
                    contentAlignment = Alignment.Center
                  ) {
                    Text(
                      text = "${index + 1}",
                      fontSize = 12.sp,
                      fontWeight = FontWeight.Bold,
                      color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                  }
                  Spacer(modifier = Modifier.width(10.dp))
                  Text(
                    text = item.destination,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium
                  )
                }
                Text(
                  text = "${item.count} rides (Rs. ${item.totalRevenue.toInt()})",
                  style = MaterialTheme.typography.bodySmall,
                  color = MaterialTheme.colorScheme.onSurfaceVariant
                )
              }
            }
          }
        }
      }
    }

    item {
      Spacer(modifier = Modifier.height(80.dp))
    }
  }
}

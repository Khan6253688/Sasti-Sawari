package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.TwoWheeler
import androidx.compose.material.icons.outlined.AddCircleOutline
import androidx.compose.material.icons.outlined.Badge
import androidx.compose.material.icons.outlined.Calculate
import androidx.compose.material.icons.outlined.Dashboard
import androidx.compose.material.icons.outlined.Menu
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.MainViewModel
import com.example.ui.screens.AccountProtocolScreen
import com.example.ui.screens.RiderProtocolView
import com.example.ui.screens.PassengerProtocolView
import com.example.ui.screens.AdminProtocolView
import com.example.ui.screens.AdminDashboardScreen
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.FareCalculatorScreen
import com.example.ui.screens.MoreScreen
import com.example.ui.screens.NewRideScreen
import com.example.ui.screens.PassengerScreen
import com.example.ui.screens.RiderBusinessCardScreen
import com.example.ui.screens.SubscriptionScreen
import com.example.ui.theme.MyApplicationTheme

enum class AppScreen(
  val title: String,
  val selectedIcon: ImageVector,
  val unselectedIcon: ImageVector,
  val testTag: String
) {
  DASHBOARD("Home", Icons.Filled.Dashboard, Icons.Outlined.Dashboard, "nav_dashboard"),
  CALCULATOR("Fare Calc", Icons.Filled.Calculate, Icons.Outlined.Calculate, "nav_calculator"),
  NEW_RIDE("New Ride", Icons.Filled.AddCircle, Icons.Outlined.AddCircleOutline, "nav_new_ride"),
  BUSINESS_CARD("Card", Icons.Filled.Badge, Icons.Outlined.Badge, "nav_card"),
  MORE("Menu", Icons.Filled.Menu, Icons.Outlined.Menu, "nav_more")
}

class MainActivity : ComponentActivity() {
  private val viewModel: MainViewModel by viewModels()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      MyApplicationTheme {
        SastiSawariApp(viewModel = viewModel)
      }
    }
  }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SastiSawariApp(viewModel: MainViewModel) {
  val appTitle by viewModel.appTitleWithCity.collectAsStateWithLifecycle()
  val userRole by viewModel.userRole.collectAsStateWithLifecycle()
  val isLocked by viewModel.isSubscriptionLocked.collectAsStateWithLifecycle()
  val currentRider by viewModel.currentRider.collectAsStateWithLifecycle()
  val isRiderLoggedIn by viewModel.isRiderLoggedIn.collectAsStateWithLifecycle()
  val currentPassenger by viewModel.currentPassenger.collectAsStateWithLifecycle()
  val isPassengerLoggedIn by viewModel.isPassengerLoggedIn.collectAsStateWithLifecycle()
  val isAdminLoggedIn by viewModel.isAdminLoggedIn.collectAsStateWithLifecycle()

  var currentScreen by remember { mutableStateOf(AppScreen.DASHBOARD) }
  var moreScreenInitialTab by remember { mutableIntStateOf(0) }
  val snackbarHostState = remember { SnackbarHostState() }

  // Handle hardware / gesture back navigation cleanly
  BackHandler(enabled = userRole != "NONE" || currentScreen != AppScreen.DASHBOARD) {
    if (userRole == "RIDER" && currentScreen != AppScreen.DASHBOARD) {
      currentScreen = AppScreen.DASHBOARD
    } else {
      viewModel.resetRoleToSelector()
    }
  }

  Scaffold(
    modifier = Modifier.fillMaxSize(),
    topBar = {
      TopAppBar(
        title = {
          Column {
            Text(
              text = when (userRole) {
                "PASSENGER" -> "Passenger Portal"
                "ADMIN" -> "Admin Portal"
                "RIDER" -> appTitle
                else -> "Sasti Sawari Pakistan"
              },
              style = MaterialTheme.typography.titleMedium,
              fontWeight = FontWeight.ExtraBold,
              maxLines = 1
            )
            when (userRole) {
              "ADMIN" -> {
                Text(
                  text = "Shahid (shahid.msc2020@gmail.com) • Super Admin",
                  fontSize = 11.sp,
                  color = MaterialTheme.colorScheme.primary,
                  fontWeight = FontWeight.Bold,
                  maxLines = 1
                )
              }
              "PASSENGER" -> {
                Text(
                  text = if (isPassengerLoggedIn && currentPassenger.name.isNotBlank()) {
                    "${currentPassenger.name} • ${currentPassenger.phone}"
                  } else {
                    "Passenger Profile Protocol"
                  },
                  fontSize = 11.sp,
                  color = MaterialTheme.colorScheme.onSurfaceVariant,
                  maxLines = 1
                )
              }
              "RIDER" -> {
                Text(
                  text = if (isRiderLoggedIn && currentRider != null) {
                    "${currentRider?.name} (${currentRider?.riderId}) • ${currentRider?.standName}"
                  } else {
                    "Captain Profile Protocol"
                  },
                  fontSize = 11.sp,
                  color = MaterialTheme.colorScheme.onSurfaceVariant,
                  maxLines = 1
                )
              }
              else -> {
                Text(
                  text = "Select Account Profile Protocol",
                  fontSize = 11.sp,
                  color = MaterialTheme.colorScheme.onSurfaceVariant
                )
              }
            }
          }
        },
        actions = {
          if (userRole != "NONE") {
            OutlinedButton(
              onClick = { viewModel.resetRoleToSelector() },
              shape = RoundedCornerShape(10.dp),
              modifier = Modifier.padding(end = 8.dp)
            ) {
              Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null, modifier = Modifier.size(14.dp))
              Spacer(modifier = Modifier.width(4.dp))
              Text("Switch Role", fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
          }
        },
        colors = TopAppBarDefaults.topAppBarColors(
          containerColor = MaterialTheme.colorScheme.surface,
          titleContentColor = MaterialTheme.colorScheme.onSurface
        )
      )
    },
    snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
    bottomBar = {
      // Show bottom navigation in Rider Mode when active, logged in and not locked
      if (userRole == "RIDER" && isRiderLoggedIn && currentRider != null && !isLocked) {
        NavigationBar(modifier = Modifier.testTag("bottom_nav_bar")) {
          AppScreen.values().forEach { screen ->
            val isSelected = currentScreen == screen
            NavigationBarItem(
              selected = isSelected,
              onClick = {
                currentScreen = screen
                if (screen == AppScreen.MORE) {
                  moreScreenInitialTab = 0
                }
              },
              icon = {
                Icon(
                  imageVector = if (isSelected) screen.selectedIcon else screen.unselectedIcon,
                  contentDescription = screen.title
                )
              },
              label = { Text(screen.title) },
              modifier = Modifier.testTag(screen.testTag)
            )
          }
        }
      }
    }
  ) { innerPadding ->
    val contentModifier = Modifier.padding(innerPadding)

    when (userRole) {
      "PASSENGER" -> {
        if (!isPassengerLoggedIn) {
          PassengerProtocolView(
            viewModel = viewModel,
            snackbarHostState = snackbarHostState,
            onBackToSelector = { viewModel.resetRoleToSelector() },
            modifier = contentModifier
          )
        } else {
          PassengerScreen(
            viewModel = viewModel,
            snackbarHostState = snackbarHostState,
            modifier = contentModifier
          )
        }
      }

      "ADMIN" -> {
        if (!isAdminLoggedIn) {
          AdminProtocolView(
            viewModel = viewModel,
            snackbarHostState = snackbarHostState,
            onBackToSelector = { viewModel.resetRoleToSelector() },
            modifier = contentModifier
          )
        } else {
          AdminDashboardScreen(
            viewModel = viewModel,
            snackbarHostState = snackbarHostState,
            modifier = contentModifier
          )
        }
      }

      "RIDER" -> {
        if (!isRiderLoggedIn || currentRider == null) {
          RiderProtocolView(
            viewModel = viewModel,
            snackbarHostState = snackbarHostState,
            onBackToSelector = { viewModel.resetRoleToSelector() },
            modifier = contentModifier
          )
        } else if (isLocked) {
          // Automatic Subscription Lock Gate (Rs. 100 / 30-Day Expiry)
          SubscriptionScreen(
            viewModel = viewModel,
            snackbarHostState = snackbarHostState,
            modifier = contentModifier
          )
        } else {
          when (currentScreen) {
            AppScreen.DASHBOARD -> {
              DashboardScreen(
                viewModel = viewModel,
                onNavigateToNewRide = { currentScreen = AppScreen.NEW_RIDE },
                onNavigateToCalculator = { currentScreen = AppScreen.CALCULATOR },
                onNavigateToCard = { currentScreen = AppScreen.BUSINESS_CARD },
                onNavigateToStandBoard = { currentScreen = AppScreen.BUSINESS_CARD },
                onNavigateToRideDetail = { /* Detail preview */ },
                modifier = contentModifier
              )
            }

            AppScreen.CALCULATOR -> {
              FareCalculatorScreen(
                viewModel = viewModel,
                onNavigateToNewRideWithValues = { dist, wait, dest ->
                  viewModel.transferCalcToNewRide(dist, wait, dest)
                  currentScreen = AppScreen.NEW_RIDE
                },
                modifier = contentModifier
              )
            }

            AppScreen.NEW_RIDE -> {
              NewRideScreen(
                viewModel = viewModel,
                snackbarHostState = snackbarHostState,
                onRideSaved = {
                  currentScreen = AppScreen.DASHBOARD
                },
                modifier = contentModifier
              )
            }

            AppScreen.BUSINESS_CARD -> {
              RiderBusinessCardScreen(
                viewModel = viewModel,
                snackbarHostState = snackbarHostState,
                modifier = contentModifier
              )
            }

            AppScreen.MORE -> {
              MoreScreen(
                viewModel = viewModel,
                snackbarHostState = snackbarHostState,
                initialTab = moreScreenInitialTab,
                modifier = contentModifier
              )
            }
          }
        }
      }

      else -> {
        // "NONE" -> Account Protocol Selector
        AccountProtocolScreen(
          viewModel = viewModel,
          snackbarHostState = snackbarHostState,
          modifier = contentModifier
        )
      }
    }
  }
}

package fi.metropolia.homeweather.ui.views.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import fi.metropolia.homeweather.R
import fi.metropolia.homeweather.ui.theme.HomeWeatherTheme
import fi.metropolia.homeweather.ui.views.AlertScreen
import fi.metropolia.homeweather.ui.views.BluetoothScreen
import fi.metropolia.homeweather.ui.views.HomeScreen
import fi.metropolia.homeweather.ui.views.QRScreen
import fi.metropolia.homeweather.ui.views.StatisticScreen
import fi.metropolia.homeweather.viewmodels.AppViewModel
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainApp(appViewModel: AppViewModel) {
    val navigationState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    var selectedItemIndex by rememberSaveable {
        mutableIntStateOf(0)
    }
    val navController = rememberNavController()
    val bluetoothLEService = appViewModel.bluetoothLEServiceLiveData.observeAsState()
    val temperatureValue = bluetoothLEService.value?.temperature?.observeAsState()
    val humidityValue = bluetoothLEService.value?.humidity?.observeAsState()

    val items = listOf(
        DrawerItem(
            title = stringResource(id = R.string.home_label),
            selectedIcon = Icons.Filled.Home,
            unselectedIcon = Icons.Outlined.Home,
            navigationDestination = "home"
        ),
        DrawerItem(
            title = stringResource(id = R.string.bluetooth_label),
            selectedIcon = ImageVector.vectorResource(id = R.drawable.bluetooth) ,
            unselectedIcon = ImageVector.vectorResource(id = R.drawable.bluetooth),
            navigationDestination = "bluetooth"
        ),
        DrawerItem(
            title = "Statistic",
            selectedIcon = ImageVector.vectorResource(id = R.drawable.thermostat),
            unselectedIcon = ImageVector.vectorResource(id = R.drawable.thermostat),
            navigationDestination = "statistic"
        ),
        DrawerItem(title = "Alert",
            selectedIcon = ImageVector.vectorResource(id = R.drawable.notification_filled),
            unselectedIcon = ImageVector.vectorResource(id = R.drawable.notification_outline),
            navigationDestination = "alert"
        ),
        DrawerItem(
            title = stringResource(id = R.string.qr_label),
            selectedIcon = ImageVector.vectorResource(id = R.drawable.qr),
            unselectedIcon = ImageVector.vectorResource(id = R.drawable.qr),
            navigationDestination = "qr"
        ),

        )
    Surface {
        ModalNavigationDrawer(
            drawerContent = {
                ModalDrawerSheet {
                    Spacer(modifier = Modifier.height(26.dp))
                    Image(
                        painter = painterResource(R.drawable.app_logo),
                        contentDescription = "",
                        modifier = Modifier
                            .size(150.dp)
                            .fillMaxWidth()
                            .align(Alignment.CenterHorizontally)
                    )
                    Spacer(modifier = Modifier.height(26.dp))
                    items.forEachIndexed { index, drawerItem ->
                        NavigationDrawerItem(
                            label = { Text(text = drawerItem.title) },
                            selected = index == selectedItemIndex,
                            onClick = {
                                selectedItemIndex = index
                                scope.launch {
                                    navController.navigate(drawerItem.navigationDestination)
                                    navigationState.close()
                                }
                            },
                            icon = {
                                Icon(
                                    imageVector = if (index == selectedItemIndex) {
                                        drawerItem.selectedIcon
                                    } else drawerItem.unselectedIcon,
                                    contentDescription = drawerItem.title
                                )
                            },
                            modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                        )
                    }
                    Spacer(modifier = Modifier.height(200.dp))
                    Column(
                        verticalArrangement = Arrangement.Bottom,
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(text = "Version 0.0.1",
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text(text ="© 2023 HomeWeather Team",
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }

                }
            },
            drawerState = navigationState,
        ) {
            Scaffold(topBar = {
                TopAppBar(title = {
                    Text(text = items[selectedItemIndex].title)
                }, navigationIcon = {
                    IconButton(onClick = {
                        scope.launch {
                            navigationState.open()
                        }
                    }) {
                        Icon(imageVector = Icons.Default.Menu, contentDescription = "Menu")
                    }
                })
            }) {
                NavHost(navController = navController, startDestination = "home", modifier = Modifier.padding(it)) {
                    composable("home") {
                        HomeScreen(temperature = temperatureValue?.value, humidity = humidityValue?.value)
                    }
                    composable("bluetooth") { bluetoothLEService.value?.let { service ->
                        BluetoothScreen(service)
                    }}
                    composable("qr") { QRScreen() }
                    composable("statistic") { StatisticScreen() }
                    composable("alert") { AlertScreen() }
                }
            }

        }
    }
}


@Preview(showBackground = true, widthDp = 400, heightDp = 500)
@Composable
fun MainAppPreview() {
    val appViewModel = AppViewModel()
    HomeWeatherTheme {
        MainApp(appViewModel = appViewModel)
    }
}

data class DrawerItem(
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val navigationDestination:String
)
package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.FadixBottomNavigation
import com.example.ui.components.FadixDrawer
import com.example.ui.components.FadixTopAppBar
import com.example.ui.screens.AccountScreen
import com.example.ui.screens.AdminScreen
import com.example.ui.screens.CartScreen
import com.example.ui.screens.CategoriesScreen
import com.example.ui.screens.CategoryProductsScreen
import com.example.ui.screens.CheckoutScreen
import com.example.ui.screens.FavoritesScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.LoginScreen
import com.example.ui.screens.OrdersScreen
import com.example.ui.screens.ProductDetailScreen
import com.example.ui.screens.SplashScreen
import com.example.ui.theme.FadixNavyDark
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.FadixViewModel
import com.example.ui.viewmodel.Screen
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private val viewModel: FadixViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            MyApplicationTheme {
                // Ensure full Arabic & Right-to-Left (RTL) support
                CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                    FadixMainApp(viewModel = viewModel)
                }
            }
        }
    }
}

@Composable
fun FadixMainApp(viewModel: FadixViewModel) {
    val currentScreen by viewModel.currentScreen.collectAsState()
    val cartTotalCount by viewModel.totalCartCount.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val notification by viewModel.notification.collectAsState()

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    BackHandler(enabled = drawerState.isOpen || currentScreen != Screen.Home) {
        if (drawerState.isOpen) {
            scope.launch { drawerState.close() }
        } else {
            val handled = viewModel.handleBackPress()
            if (!handled) {
                viewModel.navigateTo(Screen.Home)
            }
        }
    }

    LaunchedEffect(notification) {
        notification?.let {
            snackbarHostState.showSnackbar(message = it.message)
            viewModel.clearNotification()
        }
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            FadixDrawer(
                currentScreen = currentScreen,
                cartCount = cartTotalCount,
                onNavigate = { screen ->
                    viewModel.navigateTo(screen)
                },
                onClose = {
                    scope.launch { drawerState.close() }
                }
            )
        }
    ) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            topBar = {
                // Display custom FADIX Header on Home Screen
                if (currentScreen == Screen.Home) {
                    FadixTopAppBar(
                        searchQuery = searchQuery,
                        onSearchQueryChanged = { viewModel.onSearchQueryChanged(it) },
                        cartCount = cartTotalCount,
                        onCartClick = { viewModel.navigateTo(Screen.Cart) },
                        onMenuClick = {
                            scope.launch { drawerState.open() }
                        }
                    )
                }
            },
            bottomBar = {
                // Bottom navigation bar (5 tabs: الرئيسية, الأقسام, سلة التسوق, المفضلة, حسابي)
                if (currentScreen is Screen.Home ||
                    currentScreen is Screen.CategoriesList ||
                    currentScreen is Screen.CategoryProducts ||
                    currentScreen is Screen.Favorites ||
                    currentScreen is Screen.Offers ||
                    currentScreen is Screen.Cart ||
                    currentScreen is Screen.Account ||
                    currentScreen is Screen.Orders
                ) {
                    FadixBottomNavigation(
                        currentScreen = currentScreen,
                        cartCount = cartTotalCount,
                        onNavigate = { viewModel.navigateTo(it) }
                    )
                }
            },
            snackbarHost = {
                SnackbarHost(
                    hostState = snackbarHostState,
                    modifier = Modifier.padding(16.dp)
                ) { data ->
                    Snackbar(
                        containerColor = FadixNavyDark,
                        contentColor = Color.White,
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text(
                            text = data.visuals.message,
                            fontSize = 13.sp,
                            color = Color.White
                        )
                    }
                }
            },
            contentWindowInsets = WindowInsets(0, 0, 0, 0)
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                Crossfade(
                    targetState = currentScreen,
                    label = "screen_crossfade"
                ) { screen ->
                    when (screen) {
                        is Screen.Splash -> SplashScreen(onStartShopping = { viewModel.navigateTo(Screen.Home) })
                        is Screen.Home -> HomeScreen(viewModel = viewModel)
                        is Screen.CategoriesList -> CategoriesScreen(viewModel = viewModel)
                        is Screen.CategoryProducts -> CategoryProductsScreen(
                            categoryName = screen.categoryName,
                            viewModel = viewModel
                        )
                        is Screen.Favorites -> FavoritesScreen(viewModel = viewModel)
                        is Screen.Offers -> com.example.ui.screens.OffersScreen(viewModel = viewModel)
                        is Screen.ProductDetail -> ProductDetailScreen(
                            productId = screen.productId,
                            viewModel = viewModel
                        )
                        is Screen.Cart -> CartScreen(viewModel = viewModel)
                        is Screen.Checkout -> CheckoutScreen(viewModel = viewModel)
                        is Screen.Orders -> OrdersScreen(viewModel = viewModel)
                        is Screen.Account -> AccountScreen(viewModel = viewModel)
                        is Screen.Login -> LoginScreen(viewModel = viewModel)
                        is Screen.Admin -> AdminScreen(viewModel = viewModel)
                    }
                }
            }
        }
    }
}

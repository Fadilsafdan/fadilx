package com.example.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.theme.FadixNavy
import com.example.ui.theme.FadixNavyDark
import com.example.ui.theme.FadixYellow
import com.example.ui.viewmodel.Screen

@Composable
fun FadixDrawer(
    currentScreen: Screen,
    cartCount: Int,
    onNavigate: (Screen) -> Unit,
    onClose: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        color = FadixNavy,
        modifier = modifier
            .fillMaxHeight()
            .width(280.dp)
            .statusBarsPadding()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(vertical = 16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                // Top close button
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.End
                ) {
                    IconButton(onClick = onClose) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "إغلاق",
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // FADIX Logo centered
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    FadixLogo(
                        isDarkBackground = true,
                        showSubtitle = true,
                        horizontal = false,
                        scale = 1.0f
                    )
                }

                Spacer(modifier = Modifier.height(30.dp))

                // Navigation Items List (matching Screen 10 in reference)
                DrawerNavItem(
                    title = "الرئيسية",
                    icon = Icons.Default.Home,
                    isSelected = currentScreen == Screen.Home,
                    onClick = {
                        onNavigate(Screen.Home)
                        onClose()
                    }
                )

                DrawerNavItem(
                    title = "التصنيفات",
                    icon = Icons.Default.GridView,
                    isSelected = currentScreen == Screen.CategoriesList,
                    onClick = {
                        onNavigate(Screen.CategoriesList)
                        onClose()
                    }
                )

                DrawerNavItem(
                    title = "السلة",
                    icon = Icons.Default.ShoppingCart,
                    badgeCount = cartCount,
                    isSelected = currentScreen == Screen.Cart,
                    onClick = {
                        onNavigate(Screen.Cart)
                        onClose()
                    }
                )

                DrawerNavItem(
                    title = "المفضلة",
                    icon = Icons.Default.Favorite,
                    isSelected = currentScreen == Screen.Favorites,
                    onClick = {
                        onNavigate(Screen.Favorites)
                        onClose()
                    }
                )

                DrawerNavItem(
                    title = "طلباتي",
                    icon = Icons.Default.ReceiptLong,
                    isSelected = currentScreen == Screen.Orders,
                    onClick = {
                        onNavigate(Screen.Orders)
                        onClose()
                    }
                )

                DrawerNavItem(
                    title = "حسابي",
                    icon = Icons.Default.Person,
                    isSelected = currentScreen == Screen.Account,
                    onClick = {
                        onNavigate(Screen.Account)
                        onClose()
                    }
                )

                DrawerNavItem(
                    title = "لوحة الإدارة (المالك)",
                    icon = Icons.Default.AdminPanelSettings,
                    isSelected = currentScreen == Screen.Admin,
                    onClick = {
                        onNavigate(Screen.Admin)
                        onClose()
                    }
                )

                DrawerNavItem(
                    title = "شاشة البداية (Splash)",
                    icon = Icons.Default.Home,
                    isSelected = currentScreen == Screen.Splash,
                    onClick = {
                        onNavigate(Screen.Splash)
                        onClose()
                    }
                )
            }

            // Bottom Section: Location Info and Casablanca Mosque Skyline Graphic
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 16.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = null,
                        tint = FadixYellow,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "التوصيل فقط داخل\nالدار البيضاء والنواحي",
                        color = Color(0xFFE2E8F0),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        lineHeight = 16.sp
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Casablanca Hassan II Mosque Skyline in Yellow outline
                Image(
                    painter = painterResource(id = R.drawable.casablanca_skyline),
                    contentDescription = "Casablanca Skyline",
                    contentScale = ContentScale.FillWidth,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(80.dp)
                )
            }
        }
    }
}

@Composable
fun DrawerNavItem(
    title: String,
    icon: ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit,
    badgeCount: Int = 0,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp, vertical = 4.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(if (isSelected) FadixYellow else Color.Transparent)
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 10.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = if (isSelected) FadixNavyDark else Color.White,
                    modifier = Modifier.size(22.dp)
                )
                Spacer(modifier = Modifier.width(14.dp))
                Text(
                    text = title,
                    color = if (isSelected) FadixNavyDark else Color.White,
                    fontSize = 14.sp,
                    fontWeight = if (isSelected) FontWeight.Black else FontWeight.Bold
                )
            }

            if (badgeCount > 0) {
                Box(
                    modifier = Modifier
                        .size(20.dp)
                        .clip(CircleShape)
                        .background(if (isSelected) FadixNavyDark else FadixYellow),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (badgeCount > 99) "99+" else "$badgeCount",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isSelected) Color.White else FadixNavyDark
                    )
                }
            }
        }
    }
}

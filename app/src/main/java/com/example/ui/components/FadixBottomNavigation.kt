package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.GridView
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.FadixNavy
import com.example.ui.theme.FadixYellow
import com.example.ui.viewmodel.Screen

data class NavTabItem(
    val screen: Screen,
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val tag: String,
    val hasBadge: Boolean = false
)

@Composable
fun FadixBottomNavigation(
    currentScreen: Screen,
    cartCount: Int,
    onNavigate: (Screen) -> Unit,
    modifier: Modifier = Modifier
) {
    // 5 tabs matching reference image exactly:
    // الرئيسية, الأقسام, سلة التسوق, المفضلة, حسابي
    val tabs = listOf(
        NavTabItem(
            screen = Screen.Home,
            title = "الرئيسية",
            selectedIcon = Icons.Filled.Home,
            unselectedIcon = Icons.Outlined.Home,
            tag = "nav_home"
        ),
        NavTabItem(
            screen = Screen.CategoriesList,
            title = "الأقسام",
            selectedIcon = Icons.Filled.GridView,
            unselectedIcon = Icons.Outlined.GridView,
            tag = "nav_categories"
        ),
        NavTabItem(
            screen = Screen.Cart,
            title = "سلة التسوق",
            selectedIcon = Icons.Filled.ShoppingCart,
            unselectedIcon = Icons.Outlined.ShoppingCart,
            tag = "nav_cart",
            hasBadge = true
        ),
        NavTabItem(
            screen = Screen.Favorites,
            title = "المفضلة",
            selectedIcon = Icons.Outlined.FavoriteBorder,
            unselectedIcon = Icons.Outlined.FavoriteBorder,
            tag = "nav_favorites"
        ),
        NavTabItem(
            screen = Screen.Account,
            title = "حسابي",
            selectedIcon = Icons.Filled.Person,
            unselectedIcon = Icons.Outlined.Person,
            tag = "nav_account"
        )
    )

    Surface(
        color = FadixNavy, // #0B1D3A Premium Dark Navy
        shadowElevation = 12.dp,
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .windowInsetsPadding(WindowInsets.navigationBars)
                .height(68.dp)
                .padding(horizontal = 4.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            tabs.forEach { tab ->
                val isSelected = currentScreen == tab.screen ||
                        (tab.screen == Screen.CategoriesList && currentScreen is Screen.CategoryProducts)

                val interactionSource = remember { MutableInteractionSource() }

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier
                        .weight(1f)
                        .height(68.dp)
                        .clickable(
                            interactionSource = interactionSource,
                            indication = null
                        ) {
                            onNavigate(tab.screen)
                        }
                        .testTag(tab.tag)
                ) {
                    // Icon (with optional red badge for cart)
                    if (tab.hasBadge && cartCount > 0) {
                        BadgedBox(
                            badge = {
                                Box(
                                    modifier = Modifier
                                        .size(16.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFFE53935)), // Bright Red badge
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = if (cartCount > 99) "99+" else "$cartCount",
                                        color = Color.White,
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        ) {
                            Icon(
                                imageVector = if (isSelected) tab.selectedIcon else tab.unselectedIcon,
                                contentDescription = tab.title,
                                tint = if (isSelected) FadixYellow else Color.White,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    } else {
                        Icon(
                            imageVector = if (isSelected) tab.selectedIcon else tab.unselectedIcon,
                            contentDescription = tab.title,
                            tint = if (isSelected) FadixYellow else Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(3.dp))

                    // Title
                    Text(
                        text = tab.title,
                        color = if (isSelected) FadixYellow else Color.White,
                        fontSize = 11.sp,
                        fontWeight = if (isSelected) FontWeight.Black else FontWeight.Medium
                    )

                    Spacer(modifier = Modifier.height(3.dp))

                    // Active indicator bar under selected tab
                    if (isSelected) {
                        Box(
                            modifier = Modifier
                                .size(width = 32.dp, height = 3.dp)
                                .clip(RoundedCornerShape(1.5.dp))
                                .background(FadixYellow)
                        )
                    } else {
                        Spacer(modifier = Modifier.height(3.dp))
                    }
                }
            }
        }
    }
}

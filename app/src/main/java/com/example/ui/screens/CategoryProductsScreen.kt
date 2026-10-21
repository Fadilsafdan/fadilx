package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.ProductCard
import com.example.ui.theme.FadixBorder
import com.example.ui.theme.FadixNavy
import com.example.ui.theme.FadixNavyDark
import com.example.ui.theme.FadixYellow
import com.example.ui.viewmodel.FadixViewModel
import com.example.ui.viewmodel.Screen

@Composable
fun CategoryProductsScreen(
    categoryName: String,
    viewModel: FadixViewModel,
    modifier: Modifier = Modifier
) {
    BackHandler {
        viewModel.navigateTo(Screen.CategoriesList)
    }

    val allProducts by viewModel.products.collectAsState()
    val cartQuantities by viewModel.cartQuantities.collectAsState()

    // Sub-category filters matching Screen 5 in reference
    val subFilters = listOf("الكل", "معلبات", "أرز وسميد", "زيوت")
    var selectedFilter by remember { mutableStateOf("الكل") }

    val filteredProducts = remember(allProducts, categoryName, selectedFilter) {
        val base = allProducts.filter { it.category == categoryName || categoryName == "جميع المنتجات" }
        if (selectedFilter == "الكل") {
            base
        } else {
            base.filter { product ->
                when (selectedFilter) {
                    "معلبات" -> product.name.contains("معلب") || product.name.contains("طماطم") || product.name.contains("صلصة")
                    "أرز وسميد" -> product.name.contains("أرز") || product.name.contains("سميد") || product.name.contains("دقيق")
                    "زيوت" -> product.name.contains("زيت")
                    else -> true
                }
            }
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF4F5F7))
    ) {
        // Top Header matching Screen 5 in reference design
        Surface(
            color = FadixNavy,
            shadowElevation = 2.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = 14.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { viewModel.navigateTo(Screen.CategoriesList) }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "التصنيفات",
                        tint = Color.White
                    )
                }

                Text(
                    text = categoryName,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )

                IconButton(onClick = { viewModel.navigateTo(Screen.Home) }) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "بحث",
                        tint = Color.White
                    )
                }
            }
        }

        // Sub-filter Pills Row (Screen 5 in reference: الكل, معلبات, أرز وسميد, زيوت)
        Surface(
            color = Color.White,
            modifier = Modifier.fillMaxWidth()
        ) {
            LazyRow(
                contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(subFilters) { filter ->
                    val isSelected = filter == selectedFilter
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isSelected) FadixYellow else Color.White)
                            .border(
                                border = if (isSelected) BorderStroke(0.dp, Color.Transparent) else BorderStroke(1.dp, FadixBorder),
                                shape = RoundedCornerShape(8.dp)
                            )
                            .clickable { selectedFilter = filter }
                            .padding(horizontal = 14.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = filter,
                            fontSize = 12.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = if (isSelected) FadixNavyDark else Color(0xFF4B5563)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        val favoriteIds by viewModel.favoriteProductIds.collectAsState()

        // 2-Column Wholesale Product Grid
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier
                .fillMaxSize()
                .testTag("category_products_grid")
        ) {
            items(filteredProducts, key = { it.id }) { product ->
                val qty = cartQuantities[product.id] ?: 0
                ProductCard(
                    product = product,
                    cartQuantity = qty,
                    isFavorite = favoriteIds.contains(product.id),
                    onToggleFavorite = { viewModel.toggleFavorite(product.id) },
                    onAddToCart = { selectedPackages ->
                        viewModel.setItemQuantity(product.id, selectedPackages)
                    },
                    onRemoveOneFromCart = { viewModel.addToCart(product.id, -1) },
                    onClick = { viewModel.navigateTo(Screen.ProductDetail(product.id)) }
                )
            }
        }
    }
}

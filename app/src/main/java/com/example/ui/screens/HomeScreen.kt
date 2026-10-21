package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.SearchOff
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest
import com.example.data.model.Categories
import com.example.ui.components.ProductCard
import com.example.ui.theme.FadixBorder
import com.example.ui.theme.FadixNavy
import com.example.ui.theme.FadixNavyDark
import com.example.ui.theme.FadixYellow
import com.example.ui.viewmodel.FadixViewModel
import com.example.ui.viewmodel.Screen

data class CategoryCardItem(
    val title: String,
    val categoryKey: String,
    val photoUrl: String
)

@Composable
fun HomeScreen(
    viewModel: FadixViewModel,
    modifier: Modifier = Modifier
) {
    val products by viewModel.products.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val selectedCategory by viewModel.selectedCategory.collectAsState()
    val cartQuantities by viewModel.cartQuantities.collectAsState()
    val cartTotalCount by viewModel.totalCartCount.collectAsState()
    val cartSubtotal by viewModel.cartSubtotal.collectAsState()
    val favoriteIds by viewModel.favoriteProductIds.collectAsState()

    // 4 major categories matching reference image exactly with rich wholesale photos:
    // "مواد غذائية", "مشروبات", "حلويات", "بسكويت وشوكولا"
    val categoriesList = listOf(
        CategoryCardItem(
            title = "مواد غذائية",
            categoryKey = Categories.FOOD,
            // Rice, cooking oil, lentils, grains
            photoUrl = "https://images.unsplash.com/photo-1542838132-92c53300491e?w=400&auto=format&fit=crop&q=80"
        ),
        CategoryCardItem(
            title = "مشروبات",
            categoryKey = Categories.DRINKS,
            // Colorful soda bottles, beverages
            photoUrl = "https://images.unsplash.com/photo-1527661591475-527312dd65f5?w=400&auto=format&fit=crop&q=80"
        ),
        CategoryCardItem(
            title = "حلويات",
            categoryKey = Categories.SWEETS,
            // Gummy candies, sweets assortment
            photoUrl = "https://images.unsplash.com/photo-1582058091505-f87a2e55a40f?w=400&auto=format&fit=crop&q=80"
        ),
        CategoryCardItem(
            title = "بسكويت\nوشوكولا",
            categoryKey = Categories.SWEETS_BISCUITS,
            // Chocolate cookies, biscuits
            photoUrl = "https://images.unsplash.com/photo-1558961363-fa8fdf82db35?w=400&auto=format&fit=crop&q=80"
        )
    )

    Box(modifier = modifier.fillMaxSize()) {
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            contentPadding = PaddingValues(
                start = 12.dp,
                end = 12.dp,
                top = 10.dp,
                bottom = if (cartTotalCount > 0) 84.dp else 24.dp
            ),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF4F6F9))
                .testTag("home_products_grid")
        ) {
            // 1. Hero Promotional Banner: "منتجات بجودة عالية بأسعار الجملة المناسبة"
            item(span = { GridItemSpan(maxLineSpan) }) {
                HeroPromotionalBanner(
                    onShopNowClick = {
                        viewModel.selectCategory(Categories.ALL)
                        viewModel.clearSearch()
                    }
                )
            }

            // 2. Horizontal Categories Row with "كل الأقسام" + large image cards
            item(span = { GridItemSpan(maxLineSpan) }) {
                HorizontalCategoriesSection(
                    selectedCategory = selectedCategory,
                    categoriesList = categoriesList,
                    onSelectCategory = { catKey ->
                        if (selectedCategory == catKey) {
                            viewModel.selectCategory(Categories.ALL)
                        } else {
                            viewModel.selectCategory(catKey)
                        }
                    },
                    onAllCategoriesClick = {
                        viewModel.navigateTo(Screen.CategoriesList)
                    }
                )
            }

            // 3. Section Heading: "الأكثر مبيعاً" with Flame Icon 🔥 and Golden Underline, plus "المزيد ←"
            item(span = { GridItemSpan(maxLineSpan) }) {
                ProductSectionHeading(
                    title = if (searchQuery.isBlank()) {
                        if (selectedCategory == Categories.ALL) "الأكثر مبيعاً" else selectedCategory
                    } else {
                        "نتائج البحث: $searchQuery"
                    },
                    productCount = products.size,
                    showFilterReset = selectedCategory != Categories.ALL,
                    onResetFilter = { viewModel.selectCategory(Categories.ALL) },
                    onSeeMoreClick = { viewModel.navigateTo(Screen.CategoriesList) }
                )
            }

            // 4. Two-Column Wholesale Product Grid
            if (products.isEmpty()) {
                item(span = { GridItemSpan(maxLineSpan) }) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 40.dp, horizontal = 20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.SearchOff,
                            contentDescription = null,
                            tint = Color(0xFF9CA3AF),
                            modifier = Modifier.size(54.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "لم يتم العثور على أي منتج",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = FadixNavy
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "جرب البحث بكلمات أخرى أو تصفح كافة المنتجات",
                            fontSize = 13.sp,
                            color = Color(0xFF6B7280)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = {
                                viewModel.clearSearch()
                                viewModel.selectCategory(Categories.ALL)
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = FadixNavy),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text("عرض كافة المنتجات", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            } else {
                items(products, key = { it.id }) { product ->
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

        // Floating Quick Cart Bar (Bottom shopping cart summary bar)
        AnimatedVisibility(
            visible = cartTotalCount > 0,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(14.dp)
        ) {
            ElevatedCard(
                onClick = { viewModel.navigateTo(Screen.Cart) },
                colors = CardDefaults.elevatedCardColors(containerColor = FadixNavy),
                elevation = CardDefaults.elevatedCardElevation(defaultElevation = 8.dp),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("floating_cart_bar")
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(FadixYellow),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.ShoppingCart,
                                contentDescription = null,
                                tint = FadixNavyDark,
                                modifier = Modifier.size(19.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "$cartTotalCount كراتين بالسلة",
                                color = Color.White,
                                fontSize = 13.5.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "المجموع: ${String.format("%.2f", cartSubtotal)} د.م",
                                color = FadixYellow,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "إتمام الطلب",
                            color = Color.White,
                            fontSize = 13.5.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "←",
                            color = FadixYellow,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Black
                        )
                    }
                }
            }
        }
    }
}

/**
 * Hero Promotional Banner exactly matching reference design:
 * Supermarket aisle imagery, wholesale shopping cart, text "منتجات بجودة عالية بأسعار الجملة المناسبة" and "تسوق الآن"
 */
@Composable
fun HeroPromotionalBanner(
    onShopNowClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF0B1D3A)), // Deep Dark Navy #0B1D3A
        border = BorderStroke(1.5.dp, FadixYellow),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
        modifier = modifier
            .fillMaxWidth()
            .height(150.dp)
            .testTag("home_hero_banner")
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            // Background Composite: Supermarket aisle on right/left + Cart illustration
            Row(modifier = Modifier.fillMaxSize()) {
                // Right photo side (Supermarket aisle)
                Box(
                    modifier = Modifier
                        .weight(1.1f)
                        .fillMaxHeight()
                ) {
                    SubcomposeAsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data("https://images.unsplash.com/photo-1578916171728-46686eac8d58?w=600&auto=format&fit=crop&q=80")
                            .crossfade(true)
                            .build(),
                        contentDescription = "سوبرماركت الجملة",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )

                    // Gradient overlay to blend seamlessly into center dark navy
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.horizontalGradient(
                                    colors = listOf(
                                        Color(0xFF0B1D3A).copy(alpha = 0.85f),
                                        Color.Transparent
                                    )
                                )
                            )
                    )
                }

                // Left photo side (Shopping Cart / Delivery boxes)
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                ) {
                    SubcomposeAsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data("https://images.unsplash.com/photo-1586528116311-ad8dd3c8310d?w=600&auto=format&fit=crop&q=80")
                            .crossfade(true)
                            .build(),
                        contentDescription = "عربة تسوق وكراتين",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )

                    // Gradient overlay
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.horizontalGradient(
                                    colors = listOf(
                                        Color.Transparent,
                                        Color(0xFF0B1D3A).copy(alpha = 0.85f)
                                    )
                                )
                            )
                    )
                }
            }

            // Dark Navy Center Content Overlay
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFF0B1D3A).copy(alpha = 0.78f))
                    .padding(horizontal = 14.dp, vertical = 10.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    // Line 1: "منتجات بجودة عالية" in white
                    Text(
                        text = "منتجات بجودة عالية",
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 0.3.sp
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    // Line 2: "بأسعار الجملة المناسبة" in Yellow Pill
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(FadixYellow)
                            .padding(horizontal = 12.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = "بأسعار الجملة المناسبة",
                            color = Color(0xFF0B1D3A),
                            fontSize = 13.5.sp,
                            fontWeight = FontWeight.Black
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Line 3: Yellow CTA Button "تسوق الآن ›"
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .border(BorderStroke(1.dp, FadixYellow), RoundedCornerShape(20.dp))
                            .background(Color(0xFF071326))
                            .clickable(onClick = onShopNowClick)
                            .padding(horizontal = 14.dp, vertical = 4.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = "تسوق الآن",
                                color = FadixYellow,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Box(
                                modifier = Modifier
                                    .size(16.dp)
                                    .clip(CircleShape)
                                    .background(FadixYellow),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "›",
                                    color = Color(0xFF0B1D3A),
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Black
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * Horizontal Categories Row matching reference design:
 * "كل الأقسام" (Dark Navy card + golden 4 squares icon) + large image category cards
 */
@Composable
fun HorizontalCategoriesSection(
    selectedCategory: String,
    categoriesList: List<CategoryCardItem>,
    onSelectCategory: (String) -> Unit,
    onAllCategoriesClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    LazyRow(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 1. "كل الأقسام" Card (Dark Navy container, 4 golden square grid icon, white text)
        item {
            val isAllSelected = selectedCategory == Categories.ALL
            Card(
                modifier = Modifier
                    .width(82.dp)
                    .height(98.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .clickable { onAllCategoriesClick() }
                    .testTag("cat_button_all"),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(
                    width = if (isAllSelected) 2.dp else 1.dp,
                    color = if (isAllSelected) FadixYellow else Color(0xFF1E2D48)
                ),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF0B1D3A)),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    // Golden 4-square grid icon
                    Icon(
                        imageVector = Icons.Default.GridView,
                        contentDescription = "كل الأقسام",
                        tint = FadixYellow,
                        modifier = Modifier.size(34.dp)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "كل الأقسام",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }

        // 2. Specific Category Cards: "مواد غذائية", "مشروبات", "حلويات", "بسكويت وشوكولا"
        items(categoriesList) { cat ->
            val isSelected = selectedCategory == cat.categoryKey
            Card(
                modifier = Modifier
                    .width(82.dp)
                    .height(98.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .clickable { onSelectCategory(cat.categoryKey) }
                    .testTag("cat_button_${cat.categoryKey}"),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(
                    width = if (isSelected) 2.dp else 1.dp,
                    color = if (isSelected) FadixYellow else Color(0xFFE5E7EB)
                ),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = if (isSelected) 3.dp else 1.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Top: Category Photo
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(58.dp)
                            .background(Color(0xFFF1F5F9))
                    ) {
                        SubcomposeAsyncImage(
                            model = ImageRequest.Builder(LocalContext.current)
                                .data(cat.photoUrl)
                                .crossfade(true)
                                .build(),
                            contentDescription = cat.title,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                        if (isSelected) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(Color(0xFF0B1D3A).copy(alpha = 0.25f))
                            )
                        }
                    }

                    // Bottom: Category Title in clean Arabic
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                            .background(if (isSelected) Color(0xFF0B1D3A) else Color.White)
                            .padding(horizontal = 2.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = cat.title,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isSelected) FadixYellow else Color(0xFF1E293B),
                            textAlign = TextAlign.Center,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                            lineHeight = 12.sp
                        )
                    }
                }
            }
        }
    }
}

/**
 * Product Section Heading with Flame Icon 🔥, Golden Underline, and "المزيد ←" Button
 */
@Composable
fun ProductSectionHeading(
    title: String,
    productCount: Int,
    showFilterReset: Boolean,
    onResetFilter: () -> Unit,
    onSeeMoreClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 4.dp, bottom = 2.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Right side in RTL (Heading + Flame Icon + Golden Underline)
        Column {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Black,
                    color = Color(0xFF0B1D3A)
                )

                // Flame icon 🔥
                Text(
                    text = "🔥",
                    fontSize = 15.sp
                )

                if (showFilterReset) {
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "(إلغاء ✕)",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFEF4444),
                        modifier = Modifier.clickable { onResetFilter() }
                    )
                }
            }

            Spacer(modifier = Modifier.height(2.dp))

            // Golden underline accent bar matching reference
            Box(
                modifier = Modifier
                    .width(42.dp)
                    .height(3.dp)
                    .clip(RoundedCornerShape(1.5.dp))
                    .background(FadixYellow)
            )
        }

        // Left side in RTL ("المزيد ←" Button)
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(16.dp))
                .background(Color(0xFFE8EEF5))
                .clickable { onSeeMoreClick() }
                .padding(horizontal = 12.dp, vertical = 5.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "المزيد",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF0B1D3A)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "←",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Black,
                    color = Color(0xFF0B1D3A)
                )
            }
        }
    }
}

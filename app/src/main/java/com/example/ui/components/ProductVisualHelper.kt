package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Fastfood
import androidx.compose.material.icons.filled.LocalDrink
import androidx.compose.material.icons.filled.Cookie
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest
import com.example.data.model.Categories
import com.example.data.model.ProductEntity
import com.example.ui.theme.FadixNavy
import com.example.ui.theme.FadixYellow

/**
 * High-resolution direct photo URLs for Moroccan B2B wholesale groceries.
 * Uses real photographic assets for cooking oil, green tea, tomato paste, rice, sugar, biscuits, etc.
 */
fun getProductImageUrl(productName: String, category: String = ""): String {
    return when {
        // Cooking Oil (زيت المائدة)
        productName.contains("زيت") ->
            "https://images.unsplash.com/photo-1474979266404-7eaacbcd87c5?w=600&auto=format&fit=crop&q=80"

        // Moroccan Green Tea (شاي أخضر)
        productName.contains("شاي") ->
            "https://images.unsplash.com/photo-1576092768241-dec231879fc3?w=600&auto=format&fit=crop&q=80"

        // Mayonnaise / Star Mayo (مايونيز سطار)
        productName.contains("مايونيز") || productName.contains("سطار") ->
            "https://images.unsplash.com/photo-1528751090409-57754f4942d9?w=600&auto=format&fit=crop&q=80"

        // Tomato paste / Aicha cans (طماطم معلبة)
        productName.contains("طماطم") || productName.contains("عائشة") ->
            "https://images.unsplash.com/photo-1592924357228-91a4daadcfea?w=600&auto=format&fit=crop&q=80"

        // Basmati Rice (أرز بسمتي)
        productName.contains("أرز") ->
            "https://images.unsplash.com/photo-1586201375761-83865001e31c?w=600&auto=format&fit=crop&q=80"

        // Semolina / Couscous / Flour (سميد / كسكس / دقيق ميمونة)
        productName.contains("سميد") || productName.contains("كسكس") || productName.contains("دقيق") ->
            "https://images.unsplash.com/photo-1509440159596-0249088772ff?w=600&auto=format&fit=crop&q=80"

        // Lentils (عدس)
        productName.contains("عدس") ->
            "https://images.unsplash.com/photo-1515543237350-b3eea1ec8082?w=600&auto=format&fit=crop&q=80"

        // Sugar (سكر كلاصي / سنيدة)
        productName.contains("سكر") ->
            "https://images.unsplash.com/photo-1622484212850-eb596d769edc?w=600&auto=format&fit=crop&q=80"

        // Energy drinks - Sting Berry / Red Bull (ستينغ / ريد بول)
        productName.contains("ستينغ") && (productName.contains("بوري") || productName.contains("بلاست")) ->
            "https://images.unsplash.com/photo-1622543925917-763c34d1a86e?w=600&auto=format&fit=crop&q=80"
        productName.contains("ستينغ") || productName.contains("ريد بول") ->
            "https://images.unsplash.com/photo-1551024709-8f23befc6f87?w=600&auto=format&fit=crop&q=80"

        // Sodas / Water (كوكاكولا / سيدي علي / بومس)
        productName.contains("كوكاكولا") || productName.contains("بومس") ->
            "https://images.unsplash.com/photo-1622483767028-3f66f32aef97?w=600&auto=format&fit=crop&q=80"
        productName.contains("سيدي علي") || productName.contains("مياه") ->
            "https://images.unsplash.com/photo-1548839140-29a749e1bc4e?w=600&auto=format&fit=crop&q=80"
        productName.contains("عصير") || productName.contains("حليب") ->
            "https://images.unsplash.com/photo-1600271886742-f049cd451bba?w=600&auto=format&fit=crop&q=80"

        // Biscuits & Cookies (بسكويت بيمو / طوكيو / صابلي)
        productName.contains("بسكويت") || productName.contains("صابلي") || productName.contains("كوكيز") || productName.contains("كوفريت") ->
            "https://images.unsplash.com/photo-1558961363-fa8fdf82db35?w=600&auto=format&fit=crop&q=80"

        // Chocolate / Merendina / Nutella (شوكولاتة / ميريندينا / نوتيلا)
        productName.contains("شوكولاتة") || productName.contains("ميريندينا") || productName.contains("نوتيلا") || productName.contains("بوينو") ->
            "https://images.unsplash.com/photo-1549007994-cb92caebd54b?w=600&auto=format&fit=crop&q=80"

        // Sweets & Candies (سكاكر / مصاصات / علكة / حلوى)
        productName.contains("سكاكر") || productName.contains("مصاص") || productName.contains("علكة") || productName.contains("حلوى") ->
            "https://images.unsplash.com/photo-1582058091505-f87a2e55a40f?w=600&auto=format&fit=crop&q=80"

        // Chips & Snacks (بطاطس ليز / برينجلز / فشار)
        productName.contains("ليز") || productName.contains("برينجلز") || productName.contains("فشار") || productName.contains("مقرمشات") ->
            "https://images.unsplash.com/photo-1566478989037-eec170784d0b?w=600&auto=format&fit=crop&q=80"

        // Cheese (جبن كيري)
        productName.contains("جبن") || productName.contains("كيري") ->
            "https://images.unsplash.com/photo-1486297678162-eb2a19b0a32d?w=600&auto=format&fit=crop&q=80"

        // Pasta (مكرونة)
        productName.contains("مكرونة") ->
            "https://images.unsplash.com/photo-1551462147-ff29053bfc14?w=600&auto=format&fit=crop&q=80"

        // Fallback by category
        category == Categories.FOOD ->
            "https://images.unsplash.com/photo-1542838132-92c53300491e?w=600&auto=format&fit=crop&q=80"
        category == Categories.DRINKS ->
            "https://images.unsplash.com/photo-1527661591475-527312dd65f5?w=600&auto=format&fit=crop&q=80"
        category == Categories.SWEETS ->
            "https://images.unsplash.com/photo-1582058091505-f87a2e55a40f?w=600&auto=format&fit=crop&q=80"
        category == Categories.BISCUITS || category == Categories.SWEETS_BISCUITS ->
            "https://images.unsplash.com/photo-1558961363-fa8fdf82db35?w=600&auto=format&fit=crop&q=80"
        else ->
            "https://images.unsplash.com/photo-1578916171728-46686eac8d58?w=600&auto=format&fit=crop&q=80"
    }
}

@Composable
fun ProductVisual(
    product: ProductEntity,
    modifier: Modifier = Modifier,
    size: Dp = 90.dp
) {
    val photoUrl = if (product.imageUrl.isNotBlank()) product.imageUrl else getProductImageUrl(product.name, product.category)
    val context = LocalContext.current

    Box(
        modifier = modifier
            .size(size)
            .clip(RoundedCornerShape(8.dp))
            .background(Color(0xFFF8FAFC)),
        contentAlignment = Alignment.Center
    ) {
        SubcomposeAsyncImage(
            model = ImageRequest.Builder(context)
                .data(photoUrl)
                .crossfade(true)
                .build(),
            contentDescription = product.name,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize(),
            loading = {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = FadixYellow,
                        strokeWidth = 2.dp
                    )
                }
            },
            error = {
                // High-contrast clean placeholder if network is unavailable
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color(0xFFF1F5F9)),
                    contentAlignment = Alignment.Center
                ) {
                    val fallbackIcon = when (product.category) {
                        Categories.DRINKS -> Icons.Default.LocalDrink
                        Categories.BISCUITS, Categories.SWEETS_BISCUITS -> Icons.Default.Cookie
                        Categories.SWEETS -> Icons.Default.ShoppingBag
                        else -> Icons.Default.Fastfood
                    }
                    Icon(
                        imageVector = fallbackIcon,
                        contentDescription = product.name,
                        tint = FadixNavy,
                        modifier = Modifier.size(size * 0.45f)
                    )
                }
            }
        )
    }
}

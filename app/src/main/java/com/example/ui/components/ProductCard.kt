package com.example.ui.components

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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ProductEntity
import com.example.ui.theme.FadixBorder
import com.example.ui.theme.FadixNavy
import com.example.ui.theme.FadixNavyDark
import com.example.ui.theme.FadixYellow
import java.util.Locale

@Composable
fun ProductCard(
    product: ProductEntity,
    cartQuantity: Int,
    onAddToCart: (quantity: Int) -> Unit,
    onRemoveOneFromCart: () -> Unit = {},
    onClick: () -> Unit = {},
    isFavorite: Boolean = false,
    onToggleFavorite: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var localFavorite by remember(isFavorite) { mutableStateOf(isFavorite) }
    var selectedPackages by remember(cartQuantity) {
        mutableIntStateOf(if (cartQuantity > 0) cartQuantity else 1)
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .testTag("product_card_${product.id}"),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Color(0xFFE5E7EB)),
        shape = RoundedCornerShape(14.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            // Top Section: Product Image with Favorite Heart and "كرتونة كاملة" Golden Badge
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(138.dp)
                    .background(Color(0xFFF8FAFC))
                    .clickable(onClick = onClick),
                contentAlignment = Alignment.Center
            ) {
                val photoUrl = if (product.imageUrl.isNotBlank()) product.imageUrl else getProductImageUrl(product.name, product.category)

                coil.compose.SubcomposeAsyncImage(
                    model = coil.request.ImageRequest.Builder(androidx.compose.ui.platform.LocalContext.current)
                        .data(photoUrl)
                        .crossfade(true)
                        .build(),
                    contentDescription = product.name,
                    contentScale = androidx.compose.ui.layout.ContentScale.Crop,
                    modifier = Modifier.fillMaxSize(),
                    loading = {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            androidx.compose.material3.CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                color = FadixYellow,
                                strokeWidth = 2.dp
                            )
                        }
                    },
                    error = {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Color(0xFFF1F5F9)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "صورة المنتج",
                                color = Color(0xFF94A3B8),
                                fontSize = 11.sp
                            )
                        }
                    }
                )

                // Top-Right Corner (in RTL, Start is Right): Golden "كرتونة كاملة" wholesale badge
                Box(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(top = 8.dp, start = 8.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(FadixYellow)
                        .padding(horizontal = 10.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = "كرتونة كاملة",
                        color = FadixNavyDark,
                        fontSize = 10.5.sp,
                        fontWeight = FontWeight.Black
                    )
                }

                // Top-Left Corner (in RTL, End is Left): White Wishlist / Heart Icon
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(top = 8.dp, end = 8.dp)
                        .size(30.dp)
                        .clip(CircleShape)
                        .background(Color.Black.copy(alpha = 0.25f))
                        .clickable {
                            localFavorite = !localFavorite
                            onToggleFavorite()
                        }
                        .testTag("favorite_btn_${product.id}"),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (localFavorite) Icons.Default.Favorite else Icons.Outlined.FavoriteBorder,
                        contentDescription = "المفضلة",
                        tint = if (localFavorite) Color(0xFFEF4444) else Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            // Content Section: Strict hierarchy matching reference design
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp, vertical = 8.dp)
            ) {
                // Line 1: Bold Arabic product name and carton specification
                Text(
                    text = "${product.name} - ${product.displaySellingUnit}",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF111827),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.clickable(onClick = onClick)
                )

                Spacer(modifier = Modifier.height(3.dp))

                // Line 2: Package packing info in clean grey
                Text(
                    text = "الكمية: ${product.unitsPerPackage} ${product.unitTypeLabel} بال${product.displaySellingUnit}",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Normal,
                    color = Color(0xFF6B7280),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(4.dp))

                // Line 3: Prominent bold total price in MAD (e.g. 32.00 د.م) with optional old price
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = "${String.format(Locale.US, "%.2f", product.wholesalePrice)} د.م",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Black,
                        color = Color(0xFF0B1D3A)
                    )

                    if (product.originalPrice != null) {
                        Text(
                            text = "${String.format(Locale.US, "%.2f", product.originalPrice)} د.م",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Normal,
                            color = Color(0xFF9CA3AF),
                            textDecoration = TextDecoration.LineThrough
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Bottom Action Row: Golden Add to Cart Button + (+ 1 -) Counter Stepper
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Golden Yellow Add to Cart button with Cart Icon (on right in RTL)
                    Button(
                        onClick = {
                            onAddToCart(selectedPackages)
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = FadixYellow,
                            contentColor = FadixNavyDark
                        ),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 0.dp),
                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp),
                        modifier = Modifier
                            .weight(1.3f)
                            .height(34.dp)
                            .testTag("add_to_cart_${product.id}")
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.ShoppingCart,
                                contentDescription = null,
                                tint = FadixNavyDark,
                                modifier = Modifier.size(15.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (cartQuantity > 0 && selectedPackages == cartQuantity) {
                                    "في السلة"
                                } else {
                                    "أضف للسلة"
                                },
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Black,
                                maxLines = 1
                            )
                        }
                    }

                    // Stepper Box (- 1 +) on left in RTL
                    Row(
                        modifier = Modifier
                            .weight(0.9f)
                            .height(34.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .border(BorderStroke(1.dp, Color(0xFFD1D5DB)), RoundedCornerShape(8.dp))
                            .background(Color(0xFFF8FAFC)),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        // Plus (+)
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(34.dp)
                                .clickable {
                                    selectedPackages++
                                    if (cartQuantity > 0) {
                                        onAddToCart(selectedPackages)
                                    }
                                }
                                .testTag("product_qty_inc_${product.id}"),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "+",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF1E293B)
                            )
                        }

                        // Quantity
                        Text(
                            text = "$selectedPackages",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Black,
                            color = Color(0xFF0B1D3A),
                            modifier = Modifier.padding(horizontal = 2.dp)
                        )

                        // Minus (-)
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(34.dp)
                                .clickable {
                                    if (selectedPackages > 1) {
                                        selectedPackages--
                                        if (cartQuantity > 0) {
                                            onAddToCart(selectedPackages)
                                        }
                                    }
                                }
                                .testTag("product_qty_dec_${product.id}"),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "-",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF1E293B)
                            )
                        }
                    }
                }
            }
        }
    }
}

package com.example.ui.screens

import androidx.activity.compose.BackHandler
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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.RemoveShoppingCart
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CartItemWithProduct
import com.example.ui.components.ProductVisual
import com.example.ui.theme.FadixBorder
import com.example.ui.theme.FadixNavy
import com.example.ui.theme.FadixNavyDark
import com.example.ui.theme.FadixYellow
import com.example.ui.viewmodel.FadixViewModel
import com.example.ui.viewmodel.Screen

@Composable
fun CartScreen(
    viewModel: FadixViewModel,
    modifier: Modifier = Modifier
) {
    BackHandler {
        viewModel.navigateTo(Screen.Home)
    }

    val cartItems by viewModel.cartItems.collectAsState()
    val subtotal by viewModel.cartSubtotal.collectAsState()
    val selectedZone by viewModel.selectedDeliveryZone.collectAsState()
    val deliveryFee by viewModel.deliveryFee.collectAsState()
    val totalAmount by viewModel.orderTotalAmount.collectAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF4F5F7))
    ) {
        // Top Header matching Screen 7 in reference design
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
                IconButton(onClick = { viewModel.navigateTo(Screen.Home) }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "الرئيسية",
                        tint = Color.White
                    )
                }

                Text(
                    text = "سلة المشتريات",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )

                if (cartItems.isNotEmpty()) {
                    IconButton(onClick = { viewModel.clearCart() }) {
                        Icon(
                            imageVector = Icons.Default.DeleteOutline,
                            contentDescription = "إفراغ السلة",
                            tint = Color.White
                        )
                    }
                } else {
                    Spacer(modifier = Modifier.size(48.dp))
                }
            }
        }

        if (cartItems.isEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(Color.White),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.RemoveShoppingCart,
                        contentDescription = null,
                        tint = Color(0xFF9CA3AF),
                        modifier = Modifier.size(40.dp)
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "سلة المشتريات فارغة",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = FadixNavy
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "تصفح كتالوج السلع والمنتجات بالجملة وأضف بضائع إلى محلك التجاري.",
                    fontSize = 13.sp,
                    color = Color(0xFF6B7280),
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(20.dp))
                Button(
                    onClick = { viewModel.navigateTo(Screen.Home) },
                    colors = ButtonDefaults.buttonColors(containerColor = FadixYellow, contentColor = FadixNavyDark),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("ابدأ التسوق بالجملة", fontWeight = FontWeight.Bold)
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 14.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Cart Items matching Screen 7
                items(cartItems, key = { it.product.id }) { item ->
                    ReferenceCartItemCard(
                        item = item,
                        onIncrease = { viewModel.addToCart(item.product.id, 1) },
                        onDecrease = { viewModel.addToCart(item.product.id, -1) },
                        onRemove = { viewModel.removeFromCart(item.product.id) }
                    )
                }

                // Delivery Zone selection
                item {
                    Spacer(modifier = Modifier.height(6.dp))
                    DeliveryZoneCard(
                        selectedZone = selectedZone,
                        onSelectZone = { viewModel.setDeliveryZone(it) }
                    )
                }

                // Order summary breakdown matching Screen 7 exactly
                item {
                    Spacer(modifier = Modifier.height(6.dp))
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        border = BorderStroke(1.dp, FadixBorder),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(text = "إجمالي المنتجات", fontSize = 13.sp, color = Color(0xFF4B5563))
                                Text(
                                    text = "${String.format("%.2f", subtotal)} د.م",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = FadixNavyDark
                                )
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column {
                                    Text(text = "رسوم التوصيل", fontSize = 13.sp, color = Color(0xFF4B5563))
                                    Text(
                                        text = if (selectedZone.contains("خارج") || selectedZone.contains("ضواحي"))
                                            "(خارج الدار البيضاء والنواحي)"
                                        else "(داخل الدار البيضاء والنواحي)",
                                        fontSize = 11.sp,
                                        color = Color(0xFF9CA3AF)
                                    )
                                }
                                Text(
                                    text = if (deliveryFee == 0.0) "0.00 د.م" else "${String.format("%.2f", deliveryFee)} د.م",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (deliveryFee == 0.0) Color(0xFF10B981) else FadixNavyDark
                                )
                            }

                            Spacer(modifier = Modifier.height(10.dp))
                            Divider(color = FadixBorder)
                            Spacer(modifier = Modifier.height(10.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "المجموع الكلي",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Black,
                                    color = FadixNavyDark
                                )
                                Text(
                                    text = "${String.format("%.2f", totalAmount)} د.م",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Black,
                                    color = FadixNavy
                                )
                            }
                        }
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(10.dp))
                }
            }

            // Bottom Full-width Yellow Checkout Button (Screen 7 in reference)
            Surface(
                color = Color.White,
                shadowElevation = 8.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Button(
                        onClick = { viewModel.navigateTo(Screen.Checkout) },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = FadixYellow,
                            contentColor = FadixNavyDark
                        ),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("cart_proceed_button")
                    ) {
                        Text(
                            text = "إتمام الطلب",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ReferenceCartItemCard(
    item: CartItemWithProduct,
    onIncrease: () -> Unit,
    onDecrease: () -> Unit,
    onRemove: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, FadixBorder),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Product image
            ProductVisual(
                product = item.product,
                size = 54.dp
            )

            Spacer(modifier = Modifier.width(12.dp))

            // Details
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.product.name,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = FadixNavy,
                    maxLines = 2
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "${String.format("%.2f", item.product.wholesalePrice)} د.م / ${item.product.displaySellingUnit}",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Black,
                    color = FadixNavyDark
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "${String.format("%.2f", item.product.pricePerUnit)} د.م للحبة (${item.product.unitsPerPackage} حبة)",
                    fontSize = 11.sp,
                    color = Color(0xFF64748B)
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "المجموع: ${String.format("%.2f", item.subtotal)} د.م",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF15803D)
                )
            }

            // Delete trash icon and Stepper
            Column(horizontalAlignment = Alignment.End) {
                IconButton(
                    onClick = onRemove,
                    modifier = Modifier.size(26.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.DeleteOutline,
                        contentDescription = "حذف",
                        tint = Color(0xFF9CA3AF),
                        modifier = Modifier.size(18.dp)
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                // Quantity selector: [- qty باكية +]
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(Color(0xFFF3F4F6))
                        .padding(horizontal = 4.dp, vertical = 2.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = onDecrease,
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Remove,
                            contentDescription = "إنقاص",
                            tint = FadixNavyDark,
                            modifier = Modifier.size(14.dp)
                        )
                    }

                    Text(
                        text = "${item.quantity} ${item.product.displaySellingUnit}",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = FadixNavyDark,
                        modifier = Modifier.padding(horizontal = 4.dp)
                    )

                    IconButton(
                        onClick = onIncrease,
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "زيادة",
                            tint = FadixNavyDark,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun DeliveryZoneCard(
    selectedZone: String,
    onSelectZone: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, FadixBorder),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(
                text = "منطقة التوصيل (الدار البيضاء والنواحي)",
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                color = FadixNavyDark
            )

            Spacer(modifier = Modifier.height(8.dp))

            val isInside = selectedZone.contains("داخل")
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(if (isInside) Color(0xFFEFF6FF) else Color.White)
                    .clickable { onSelectZone("داخل الدار البيضاء") }
                    .padding(10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "داخل الدار البيضاء (مجاني فوق 500 د.م)",
                    fontSize = 12.sp,
                    fontWeight = if (isInside) FontWeight.Bold else FontWeight.Medium,
                    color = FadixNavyDark
                )
                Text(
                    text = if (isInside) "✓ محدد" else "اختيار",
                    fontSize = 11.sp,
                    color = if (isInside) Color(0xFF2563EB) else Color(0xFF9CA3AF),
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            val isOutside = !isInside
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(if (isOutside) Color(0xFFFFFBEB) else Color.White)
                    .clickable { onSelectZone("ضواحي الدار البيضاء والمناطق الخارجية") }
                    .padding(10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "ضواحي الدار البيضاء والنواحي (40.00 د.م)",
                    fontSize = 12.sp,
                    fontWeight = if (isOutside) FontWeight.Bold else FontWeight.Medium,
                    color = FadixNavyDark
                )
                Text(
                    text = if (isOutside) "✓ محدد" else "اختيار",
                    fontSize = 11.sp,
                    color = if (isOutside) Color(0xFFB45309) else Color(0xFF9CA3AF),
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}


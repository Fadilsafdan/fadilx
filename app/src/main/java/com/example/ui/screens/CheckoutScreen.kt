package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.FadixBorder
import com.example.ui.theme.FadixNavy
import com.example.ui.theme.FadixNavyDark
import com.example.ui.theme.FadixSuccess
import com.example.ui.theme.FadixYellow
import com.example.ui.viewmodel.FadixViewModel
import com.example.ui.viewmodel.Screen

@Composable
fun CheckoutScreen(
    viewModel: FadixViewModel,
    modifier: Modifier = Modifier
) {
    BackHandler {
        viewModel.handleBackPress()
    }

    val customerProfile by viewModel.customerProfile.collectAsState()
    val subtotal by viewModel.cartSubtotal.collectAsState()
    val deliveryZone by viewModel.selectedDeliveryZone.collectAsState()
    val deliveryFee by viewModel.deliveryFee.collectAsState()
    val totalAmount by viewModel.orderTotalAmount.collectAsState()
    val isPlacingOrder by viewModel.isPlacingOrder.collectAsState()
    val lastPlacedOrder by viewModel.lastPlacedOrder.collectAsState()

    var grocerName by remember { mutableStateOf("") }
    var shopName by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var city by remember { mutableStateOf("الدار البيضاء") }

    LaunchedEffect(customerProfile) {
        customerProfile?.let {
            if (grocerName.isBlank()) grocerName = it.grocerName
            if (shopName.isBlank()) shopName = it.shopName
            if (phone.isBlank()) phone = it.phone
            if (address.isBlank()) address = it.deliveryAddress
        }
    }

    // Success dialog
    lastPlacedOrder?.let { order ->
        AlertDialog(
            onDismissRequest = { viewModel.dismissOrderDialog() },
            containerColor = Color.White,
            shape = RoundedCornerShape(16.dp),
            icon = {
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFDCFCE7)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = FadixSuccess,
                        modifier = Modifier.size(32.dp)
                    )
                }
            },
            title = {
                Text(
                    text = "تم تأكيد طلبك بنجاح!",
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp,
                    color = FadixNavy,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "رقم الطلبية: ${order.orderNumber}",
                        fontWeight = FontWeight.Black,
                        fontSize = 16.sp,
                        color = FadixNavy
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "المجموع: ${String.format("%.2f", order.totalAmount)} د.م",
                        fontWeight = FontWeight.Bold,
                        color = FadixNavyDark
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "طريقة الدفع: نقداً عند الاستلام",
                        fontSize = 12.sp,
                        color = FadixSuccess,
                        fontWeight = FontWeight.Bold
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = { viewModel.dismissOrderDialog() },
                    colors = ButtonDefaults.buttonColors(containerColor = FadixNavy),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("متابعة الطلبات", fontWeight = FontWeight.Bold)
                }
            }
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF4F5F7))
    ) {
        // Top Header matching Screen 8 in reference design
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
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { viewModel.handleBackPress() }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "رجوع",
                        tint = Color.White
                    )
                }
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "إتمام الطلب",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Delivery Info Card matching Screen 8
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.dp, FadixBorder),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.LocalShipping,
                                contentDescription = null,
                                tint = FadixNavy,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "معلومات التوصيل",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = FadixNavy
                            )
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        // Subtitle note
                        Text(
                            text = "• التوصيل متاح فقط داخل الدار البيضاء والنواحي",
                            fontSize = 12.sp,
                            color = Color(0xFFB45309),
                            fontWeight = FontWeight.Medium
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        // City field
                        Text(text = "المدينة", fontSize = 12.sp, color = Color(0xFF4B5563))
                        Spacer(modifier = Modifier.height(4.dp))
                        OutlinedTextField(
                            value = city,
                            onValueChange = { city = it },
                            singleLine = true,
                            shape = RoundedCornerShape(8.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = FadixNavy,
                                unfocusedBorderColor = FadixBorder
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // Address field
                        Text(text = "العنوان", fontSize = 12.sp, color = Color(0xFF4B5563))
                        Spacer(modifier = Modifier.height(4.dp))
                        OutlinedTextField(
                            value = address,
                            onValueChange = { address = it },
                            placeholder = { Text("أدخل عنوانك بالتفصيل", color = Color(0xFF9CA3AF), fontSize = 13.sp) },
                            singleLine = false,
                            maxLines = 2,
                            shape = RoundedCornerShape(8.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = FadixNavy,
                                unfocusedBorderColor = FadixBorder
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // Phone Number
                        Text(text = "رقم الهاتف", fontSize = 12.sp, color = Color(0xFF4B5563))
                        Spacer(modifier = Modifier.height(4.dp))
                        OutlinedTextField(
                            value = phone,
                            onValueChange = { phone = it },
                            placeholder = { Text("06XXXXXXXX", color = Color(0xFF9CA3AF), fontSize = 13.sp) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                            singleLine = true,
                            shape = RoundedCornerShape(8.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = FadixNavy,
                                unfocusedBorderColor = FadixBorder
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // Shop Name
                        Text(text = "اسم المحل / البقالة", fontSize = 12.sp, color = Color(0xFF4B5563))
                        Spacer(modifier = Modifier.height(4.dp))
                        OutlinedTextField(
                            value = shopName,
                            onValueChange = { shopName = it },
                            placeholder = { Text("مثال: بقالة الخير", color = Color(0xFF9CA3AF), fontSize = 13.sp) },
                            singleLine = true,
                            shape = RoundedCornerShape(8.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = FadixNavy,
                                unfocusedBorderColor = FadixBorder
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }

            // Big Yellow CTA button matching Screen 8: "متابعة الدفع"
            item {
                Button(
                    onClick = {
                        viewModel.placeOrder(
                            grocerName = grocerName.ifBlank { "التاجر" },
                            shopName = shopName.ifBlank { "بقالة التاجر" },
                            phone = phone,
                            address = address,
                            zone = deliveryZone,
                            notes = ""
                        )
                    },
                    enabled = !isPlacingOrder,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = FadixYellow,
                        contentColor = FadixNavyDark
                    ),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("checkout_submit_button")
                ) {
                    if (isPlacingOrder) {
                        CircularProgressIndicator(
                            color = FadixNavyDark,
                            modifier = Modifier.size(20.dp),
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text(
                            text = "متابعة الدفع",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // Order Summary section matching Screen 8
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.dp, FadixBorder),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "ملخص الطلب",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = FadixNavy
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = "إجمالي المنتجات", fontSize = 13.sp, color = Color(0xFF4B5563))
                            Text(
                                text = "${String.format("%.2f", subtotal)} د.م",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = FadixNavyDark
                            )
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = "رسوم التوصيل", fontSize = 13.sp, color = Color(0xFF4B5563))
                            Text(
                                text = if (deliveryFee == 0.0) "0.00 د.م" else "${String.format("%.2f", deliveryFee)} د.م",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (deliveryFee == 0.0) FadixSuccess else FadixNavyDark
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        Divider(color = FadixBorder)
                        Spacer(modifier = Modifier.height(8.dp))

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
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Black,
                                color = FadixNavy
                            )
                        }
                    }
                }
            }
        }
    }
}

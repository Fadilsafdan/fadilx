package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.ShoppingBag
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.OrderWithItems
import com.example.ui.theme.FadixBorder
import com.example.ui.theme.FadixNavyDark
import com.example.ui.theme.FadixNavyPrimary
import com.example.ui.theme.FadixSurfaceVariant
import com.example.ui.theme.FadixYellowAccent
import com.example.ui.viewmodel.FadixViewModel
import com.example.ui.viewmodel.Screen
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun OrdersScreen(
    viewModel: FadixViewModel,
    modifier: Modifier = Modifier
) {
    BackHandler {
        viewModel.navigateTo(Screen.Home)
    }

    val customerOrders by viewModel.customerOrders.collectAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF8FAFC))
    ) {
        // App bar
        Surface(
            color = Color.White,
            shadowElevation = 2.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { viewModel.navigateTo(Screen.Home) }) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "الرئيسية",
                        tint = FadixNavyPrimary
                    )
                }
                Spacer(modifier = Modifier.width(6.dp))
                Column {
                    Text(
                        text = "سجل طلباتي بالجملة",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = FadixNavyDark
                    )
                    Text(
                        text = "متابعة حالة وتفاصيل الشحنات والمشتريات السابقة",
                        fontSize = 11.sp,
                        color = Color(0xFF64748B)
                    )
                }
            }
        }

        if (customerOrders.isEmpty()) {
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
                        .background(FadixSurfaceVariant),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.ReceiptLong,
                        contentDescription = null,
                        tint = Color(0xFF94A3B8),
                        modifier = Modifier.size(42.dp)
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "لا توجد طلبيات سابقة بعد",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = FadixNavyDark
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "قم بإنشاء طلبيتك الأولى من السلع والمنتجات الغذائية لتصلك مباشرة إلى محلك التجاري.",
                    fontSize = 13.sp,
                    color = Color(0xFF64748B),
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(20.dp))
                Button(
                    onClick = { viewModel.navigateTo(Screen.Home) },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = FadixNavyPrimary,
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(Icons.Default.ShoppingBag, contentDescription = null, tint = FadixYellowAccent)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("ابدأ التسوق بالجملة")
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(customerOrders, key = { it.order.id }) { orderWithItems ->
                    OrderHistoryCard(orderWithItems = orderWithItems)
                }
            }
        }
    }
}

@Composable
fun OrderHistoryCard(
    orderWithItems: OrderWithItems,
    modifier: Modifier = Modifier
) {
    val order = orderWithItems.order
    val items = orderWithItems.items
    var expanded by remember { mutableStateOf(false) }

    val formattedDate = remember(order.createdAt) {
        val sdf = SimpleDateFormat("dd MMMM yyyy - HH:mm", Locale("ar"))
        sdf.format(Date(order.createdAt))
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("order_card_${order.id}"),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, FadixBorder),
        shape = RoundedCornerShape(14.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Header: Order Number + Status Chip
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(FadixNavyPrimary.copy(alpha = 0.1f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.ReceiptLong,
                            contentDescription = null,
                            tint = FadixNavyPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = order.orderNumber,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Black,
                            color = FadixNavyDark
                        )
                        Text(
                            text = formattedDate,
                            fontSize = 11.sp,
                            color = Color(0xFF64748B)
                        )
                    }
                }

                OrderStatusBadge(status = order.status)
            }

            Spacer(modifier = Modifier.height(10.dp))
            Divider(color = FadixBorder.copy(alpha = 0.6f))
            Spacer(modifier = Modifier.height(10.dp))

            // Details summary
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(text = "المتجر:", fontSize = 11.sp, color = Color(0xFF64748B))
                    Text(
                        text = order.shopName.ifBlank { "بقالة التاجر" },
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = FadixNavyDark
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(text = "المبلغ الإجمالي:", fontSize = 11.sp, color = Color(0xFF64748B))
                    Text(
                        text = "${String.format("%.2f", order.totalAmount)} د.م",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Black,
                        color = FadixNavyPrimary
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Delivery address
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = null,
                    tint = Color(0xFF94A3B8),
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "${order.deliveryAddress} • ${order.deliveryZone}",
                    fontSize = 11.sp,
                    color = Color(0xFF475569),
                    maxLines = 1
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Expandable Items Section Toggle
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(FadixSurfaceVariant)
                    .clickable { expanded = !expanded }
                    .padding(horizontal = 10.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "عرض أصناف الطلبية (${items.size} أصناف)",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = FadixNavyPrimary
                )
                Icon(
                    imageVector = if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = null,
                    tint = FadixNavyPrimary
                )
            }

            AnimatedVisibility(visible = expanded) {
                Column(modifier = Modifier.padding(top = 10.dp)) {
                    items.forEach { item ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = item.productName,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = FadixNavyDark
                                )
                                Text(
                                    text = "${item.packageUnit} • ${item.quantity}× بسعر ${String.format("%.2f", item.unitPrice)} د.م",
                                    fontSize = 11.sp,
                                    color = Color(0xFF64748B)
                                )
                            }
                            Text(
                                text = "${String.format("%.2f", item.subtotal)} د.م",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = FadixNavyDark
                            )
                        }
                        Divider(color = Color(0xFFF1F5F9))
                    }

                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "رسوم التوصيل:", fontSize = 12.sp, color = Color(0xFF64748B))
                        Text(
                            text = if (order.deliveryFee == 0.0) "مجاني" else "${order.deliveryFee} د.م",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF15803D)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun OrderStatusBadge(status: String) {
    val (bgColor, textColor) = when (status) {
        "جديد" -> Pair(Color(0xFFFEF3C7), Color(0xFFB45309))
        "قيد التجهيز" -> Pair(Color(0xFFE0F2FE), Color(0xFF0369A1))
        "في الطريق للتوصيل" -> Pair(Color(0xFFF3E8FF), Color(0xFF7E22CE))
        "تم التسليم" -> Pair(Color(0xFFDCFCE7), Color(0xFF15803D))
        "ملغي" -> Pair(Color(0xFFFEE2E2), Color(0xFFB91C1C))
        else -> Pair(Color(0xFFE2E8F0), Color(0xFF475569))
    }

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(bgColor)
            .padding(horizontal = 10.dp, vertical = 4.dp)
    ) {
        Text(
            text = status,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = textColor
        )
    }
}

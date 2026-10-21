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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.PendingActions
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Categories
import com.example.data.model.OrderWithItems
import com.example.data.model.ProductEntity
import com.example.ui.theme.FadixBorder
import com.example.ui.theme.FadixError
import com.example.ui.theme.FadixNavyDark
import com.example.ui.theme.FadixNavyPrimary
import com.example.ui.theme.FadixSuccess
import com.example.ui.theme.FadixSurfaceVariant
import com.example.ui.theme.FadixYellowAccent
import com.example.ui.viewmodel.FadixViewModel
import com.example.ui.viewmodel.Screen
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminScreen(
    viewModel: FadixViewModel,
    modifier: Modifier = Modifier
) {
    BackHandler {
        viewModel.navigateTo(Screen.Home)
    }

    val isAdminUnlocked by viewModel.isAdminUnlocked.collectAsState()
    val allOrders by viewModel.allOrdersWithItems.collectAsState()
    val allProducts by viewModel.products.collectAsState()

    var pinInput by remember { mutableStateOf("") }
    var selectedTab by remember { mutableIntStateOf(0) }

    // Dialogs state
    var showProductDialog by remember { mutableStateOf(false) }
    var editingProduct by remember { mutableStateOf<ProductEntity?>(null) }
    var productToDelete by remember { mutableStateOf<ProductEntity?>(null) }

    // 1. PIN Security Gate if locked
    if (!isAdminUnlocked) {
        Column(
            modifier = modifier
                .fillMaxSize()
                .background(Color(0xFFF8FAFC))
        ) {
            Surface(
                color = FadixNavyPrimary,
                shadowElevation = 4.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = { viewModel.navigateTo(Screen.Home) }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "رجوع",
                            tint = Color.White
                        )
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "بوابة إدارة منصة FADIX",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(FadixYellowAccent),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = "قفل الإدارة",
                        tint = FadixNavyDark,
                        modifier = Modifier.size(40.dp)
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))
                Text(
                    text = "منطقة خاصة بمالك منصة FADIX",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = FadixNavyDark
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "أدخل رمز المرور السري (PIN) للدخول إلى لوحة التحكم بالمنتجات، الأسعار، الطلبات ورسوم التوصيل.",
                    fontSize = 13.sp,
                    color = Color(0xFF64748B),
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(8.dp))
                Surface(
                    color = Color(0xFFFEF3C7),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = "الرمز الافتراضي للمالك: 1234",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFB45309),
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                OutlinedTextField(
                    value = pinInput,
                    onValueChange = { if (it.length <= 6) pinInput = it },
                    placeholder = { Text("رمز المرور PIN (1234)", textAlign = TextAlign.Center) },
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .width(220.dp)
                        .testTag("admin_pin_input")
                )

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = { viewModel.unlockAdmin(pinInput) },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = FadixNavyPrimary,
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .width(220.dp)
                        .height(48.dp)
                        .testTag("admin_login_button")
                ) {
                    Icon(Icons.Default.LockOpen, contentDescription = null, tint = FadixYellowAccent)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("دخول للوحة الإدارة", fontWeight = FontWeight.Bold)
                }
            }
        }
        return
    }

    // Product Add/Edit Dialog
    if (showProductDialog) {
        ProductEditDialog(
            initialProduct = editingProduct,
            onDismiss = {
                showProductDialog = false
                editingProduct = null
            },
            onSave = { name, category, price, packageUnit, unitsPerPackage, stock, description ->
                if (editingProduct != null) {
                    viewModel.updateProduct(
                        editingProduct!!.copy(
                            name = name,
                            category = category,
                            wholesalePrice = price,
                            packageUnit = packageUnit,
                            unitsPerPackage = unitsPerPackage,
                            stock = stock,
                            description = description
                        )
                    )
                } else {
                    viewModel.addProduct(name, category, price, packageUnit, unitsPerPackage, stock, description)
                }
                showProductDialog = false
                editingProduct = null
            }
        )
    }

    // Product Delete Confirmation Dialog
    productToDelete?.let { prod ->
        AlertDialog(
            onDismissRequest = { productToDelete = null },
            containerColor = Color.White,
            shape = RoundedCornerShape(16.dp),
            title = { Text("حذف المنتج من الكتالوج", fontWeight = FontWeight.Bold, color = FadixError) },
            text = {
                Text(
                    text = "هل أنت متأكد من حذف المنتج: \"${prod.name}\"؟ لن يظهر مجدداً في كتالوج الجملة للمحلات.",
                    fontSize = 13.sp,
                    color = Color(0xFF334155)
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deleteProduct(prod)
                        productToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = FadixError)
                ) {
                    Text("تأكيد الحذف", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { productToDelete = null }) {
                    Text("إلغاء", color = Color(0xFF64748B))
                }
            }
        )
    }

    // 2. Unlocked Dashboard UI
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF8FAFC))
    ) {
        // App bar
        Surface(
            color = FadixNavyPrimary,
            shadowElevation = 4.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = { viewModel.navigateTo(Screen.Home) }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "الرئيسية",
                            tint = Color.White
                        )
                    }
                    Spacer(modifier = Modifier.width(4.dp))
                    Column {
                        Text(
                            text = "لوحة إدارة FADIX للبيع بالجملة",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = "التحكم بالطلبات، البضائع والأسعار للمالك",
                            fontSize = 11.sp,
                            color = Color(0xFFCBD5E1)
                        )
                    }
                }

                IconButton(onClick = { viewModel.lockAdmin() }) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = "قفل اللوحة",
                        tint = FadixYellowAccent
                    )
                }
            }
        }

        // Quick KPI Stats Bar
        val totalRevenue = allOrders.sumOf { it.order.totalAmount }
        val pendingOrdersCount = allOrders.count { it.order.status != "تم التسليم" && it.order.status != "ملغي" }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            KpiStatCard(
                title = "إجمالي المبيعات",
                value = "${String.format("%.0f", totalRevenue)} د.م",
                icon = Icons.Default.AttachMoney,
                iconTint = Color(0xFF16A34A),
                modifier = Modifier.weight(1f)
            )
            KpiStatCard(
                title = "إجمالي الطلبات",
                value = "${allOrders.size}",
                icon = Icons.Default.ReceiptLong,
                iconTint = FadixNavyPrimary,
                modifier = Modifier.weight(1f)
            )
            KpiStatCard(
                title = "طلبات جارية",
                value = "$pendingOrdersCount",
                icon = Icons.Default.PendingActions,
                iconTint = Color(0xFFB45309),
                modifier = Modifier.weight(1f)
            )
        }

        // Tabs
        val tabs = listOf("إدارة الطلبيات (${allOrders.size})", "المنتجات والمخزون", "الشحن والتغطية")
        ScrollableTabRow(
            selectedTabIndex = selectedTab,
            containerColor = Color.White,
            contentColor = FadixNavyPrimary,
            edgePadding = 16.dp
        ) {
            tabs.forEachIndexed { index, tabTitle ->
                Tab(
                    selected = selectedTab == index,
                    onClick = { selectedTab = index },
                    text = {
                        Text(
                            text = tabTitle,
                            fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Medium,
                            fontSize = 13.sp
                        )
                    }
                )
            }
        }

        when (selectedTab) {
            // Tab 0: Orders Management
            0 -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    if (allOrders.isEmpty()) {
                        item {
                            Text(
                                text = "لا توجد طلبيات مسجلة بعد.",
                                color = Color(0xFF64748B),
                                textAlign = TextAlign.Center,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(32.dp)
                            )
                        }
                    } else {
                        items(allOrders, key = { it.order.id }) { orderWithItems ->
                            AdminOrderCard(
                                orderWithItems = orderWithItems,
                                onUpdateStatus = { newStatus ->
                                    viewModel.updateOrderStatus(orderWithItems.order.id, newStatus)
                                }
                            )
                        }
                    }
                }
            }

            // Tab 1: Products & Inventory Management
            1 -> {
                Column(modifier = Modifier.fillMaxSize()) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "كتالوج البضائع بالجملة (${allProducts.size} منتج)",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = FadixNavyDark
                        )
                        ElevatedButton(
                            onClick = {
                                editingProduct = null
                                showProductDialog = true
                            },
                            colors = ButtonDefaults.elevatedButtonColors(
                                containerColor = FadixYellowAccent,
                                contentColor = FadixNavyDark
                            ),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.testTag("admin_add_product_button")
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("إضافة منتج جديد", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 14.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(allProducts, key = { it.id }) { product ->
                            AdminProductRow(
                                product = product,
                                onEdit = {
                                    editingProduct = product
                                    showProductDialog = true
                                },
                                onDelete = {
                                    productToDelete = product
                                }
                            )
                        }
                    }
                }
            }

            // Tab 2: Delivery & Service Coverage
            2 -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            border = BorderStroke(1.dp, FadixBorder),
                            shape = RoundedCornerShape(14.dp)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.LocalShipping,
                                        contentDescription = null,
                                        tint = FadixNavyPrimary
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "إعدادات مناطق وتكاليف التوصيل المعتمدة",
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = FadixNavyDark
                                    )
                                }
                                Spacer(modifier = Modifier.height(14.dp))

                                // Zone 1
                                Surface(
                                    color = FadixSurfaceVariant,
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column(modifier = Modifier.padding(12.dp)) {
                                        Text(
                                            text = "1. داخل الدار البيضاء (المناطق الأساسية)",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 13.sp,
                                            color = FadixNavyDark
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = "• توصيل مجاني لطلبيات الجملة البالغة 500.00 د.م فما فوق.",
                                            fontSize = 12.sp,
                                            color = Color(0xFF15803D)
                                        )
                                        Text(
                                            text = "• رسم 20.00 د.م للطلبيات الأقل من 500 د.م.",
                                            fontSize = 12.sp,
                                            color = Color(0xFF475569)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(10.dp))

                                // Zone 2 (40 MAD rule strictly preserved)
                                Surface(
                                    color = Color(0xFFFEF3C7),
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column(modifier = Modifier.padding(12.dp)) {
                                        Text(
                                            text = "2. ضواحي الدار البيضاء والمناطق الخارجية",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 13.sp,
                                            color = Color(0xFF78350F)
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = "• رسم توصيل ثابت: 40.00 درهم مغربي (MAD).",
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFFB45309)
                                        )
                                        Text(
                                            text = "• يشمل: المحمدية، دار بوعزة، بوسكورة، النواصر، تيط مليل والمناطق المجاورة.",
                                            fontSize = 11.sp,
                                            color = Color(0xFF78350F)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun KpiStatCard(
    title: String,
    value: String,
    icon: ImageVector,
    iconTint: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, FadixBorder),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconTint,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = value,
                fontSize = 15.sp,
                fontWeight = FontWeight.Black,
                color = FadixNavyDark
            )
            Text(
                text = title,
                fontSize = 10.sp,
                color = Color(0xFF64748B)
            )
        }
    }
}

@Composable
fun AdminOrderCard(
    orderWithItems: OrderWithItems,
    onUpdateStatus: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val order = orderWithItems.order
    val items = orderWithItems.items

    val formattedDate = remember(order.createdAt) {
        val sdf = SimpleDateFormat("dd/MM/yyyy - HH:mm", Locale("ar"))
        sdf.format(Date(order.createdAt))
    }

    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, FadixBorder),
        shape = RoundedCornerShape(14.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = order.orderNumber,
                        fontWeight = FontWeight.Black,
                        fontSize = 15.sp,
                        color = FadixNavyPrimary
                    )
                    Text(
                        text = formattedDate,
                        fontSize = 11.sp,
                        color = Color(0xFF64748B)
                    )
                }
                OrderStatusBadge(status = order.status)
            }

            Spacer(modifier = Modifier.height(8.dp))
            Divider(color = FadixBorder)
            Spacer(modifier = Modifier.height(8.dp))

            // Grocer Details
            Text(
                text = "المتجر: ${order.shopName} (${order.grocerName})",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = FadixNavyDark
            )
            Text(
                text = "الهاتف: ${order.customerPhone}",
                fontSize = 12.sp,
                color = FadixNavyPrimary,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = "العنوان: ${order.deliveryAddress} • ${order.deliveryZone}",
                fontSize = 11.sp,
                color = Color(0xFF475569)
            )
            if (order.notes.isNotBlank()) {
                Text(
                    text = "ملاحظات: ${order.notes}",
                    fontSize = 11.sp,
                    color = Color(0xFFB45309),
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Items count and total
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "${items.size} أصناف بالطلبية",
                    fontSize = 12.sp,
                    color = Color(0xFF64748B)
                )
                Text(
                    text = "الإجمالي: ${String.format("%.2f", order.totalAmount)} د.م",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Black,
                    color = FadixNavyDark
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Fast Status Updater Buttons
            Text(text = "تحديث حالة الطلب:", fontSize = 11.sp, color = Color(0xFF64748B))
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                val statuses = listOf("جديد", "قيد التجهيز", "في الطريق للتوصيل", "تم التسليم")
                statuses.forEach { st ->
                    val isCurrent = order.status == st
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = if (isCurrent) FadixNavyPrimary else FadixSurfaceVariant,
                        border = BorderStroke(1.dp, if (isCurrent) FadixNavyPrimary else FadixBorder),
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(6.dp))
                            .buttonClick { onUpdateStatus(st) }
                            .padding(vertical = 6.dp)
                    ) {
                        Text(
                            text = st,
                            fontSize = 10.sp,
                            fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Medium,
                            color = if (isCurrent) Color.White else FadixNavyDark,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}

// Helper extension for clean surface clicks
fun Modifier.buttonClick(onClick: () -> Unit): Modifier = this.clickable(onClick = onClick)

@Composable
fun AdminProductRow(
    product: ProductEntity,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, FadixBorder),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = product.name,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = FadixNavyDark,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(2.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = product.category,
                        fontSize = 11.sp,
                        color = FadixNavyPrimary,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(text = " • ", fontSize = 11.sp, color = Color(0xFFCBD5E1))
                    Text(
                        text = "${product.packageUnit} (${product.unitsPerPackage} حبة • ${String.format("%.2f", product.pricePerUnit)} د.م/حبة)",
                        fontSize = 11.sp,
                        color = Color(0xFF64748B)
                    )
                }
                Spacer(modifier = Modifier.height(2.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "${String.format("%.2f", product.wholesalePrice)} د.م",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Black,
                        color = FadixNavyDark
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "المخزون: ${product.stock}",
                        fontSize = 11.sp,
                        color = if (product.stock > 10) Color(0xFF15803D) else FadixError
                    )
                }
            }

            Row {
                IconButton(onClick = onEdit) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "تعديل",
                        tint = FadixNavyPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                }
                IconButton(onClick = onDelete) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "حذف",
                        tint = FadixError,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductEditDialog(
    initialProduct: ProductEntity?,
    onDismiss: () -> Unit,
    onSave: (name: String, category: String, price: Double, packageUnit: String, unitsPerPackage: Int, stock: Int, description: String) -> Unit
) {
    var name by remember { mutableStateOf(initialProduct?.name ?: "") }
    var category by remember { mutableStateOf(initialProduct?.category ?: Categories.FOOD) }
    var priceText by remember { mutableStateOf(initialProduct?.wholesalePrice?.toString() ?: "") }
    var packageUnit by remember { mutableStateOf(initialProduct?.packageUnit ?: "") }
    var unitsPerPackageText by remember { mutableStateOf(initialProduct?.unitsPerPackage?.toString() ?: "12") }
    var stockText by remember { mutableStateOf(initialProduct?.stock?.toString() ?: "50") }
    var description by remember { mutableStateOf(initialProduct?.description ?: "") }

    var categoryExpanded by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Color.White,
        shape = RoundedCornerShape(16.dp),
        title = {
            Text(
                text = if (initialProduct == null) "إضافة منتج جملة جديد" else "تعديل بيانات المنتج",
                fontWeight = FontWeight.Bold,
                fontSize = 17.sp,
                color = FadixNavyDark
            )
        },
        text = {
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                item {
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("اسم المنتج التجاري *") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                // Category dropdown
                item {
                    ExposedDropdownMenuBox(
                        expanded = categoryExpanded,
                        onExpandedChange = { categoryExpanded = !categoryExpanded }
                    ) {
                        OutlinedTextField(
                            value = category,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("قسم المنتج *") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = categoryExpanded) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor()
                        )
                        ExposedDropdownMenu(
                            expanded = categoryExpanded,
                            onDismissRequest = { categoryExpanded = false }
                        ) {
                            Categories.list.filter { it != Categories.ALL }.forEach { cat ->
                                DropdownMenuItem(
                                    text = { Text(cat) },
                                    onClick = {
                                        category = cat
                                        categoryExpanded = false
                                    }
                                )
                            }
                        }
                    }
                }

                item {
                    OutlinedTextField(
                        value = priceText,
                        onValueChange = { priceText = it },
                        label = { Text("سعر الباكية/الكرتون بالجملة (MAD) *") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                item {
                    OutlinedTextField(
                        value = packageUnit,
                        onValueChange = { packageUnit = it },
                        label = { Text("نوع العبوة * (مثال: باكية, كرتون 20 علبة)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                item {
                    OutlinedTextField(
                        value = unitsPerPackageText,
                        onValueChange = { unitsPerPackageText = it },
                        label = { Text("عدد الحبات في العبوة/الباكية * (مثال: 18)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                item {
                    OutlinedTextField(
                        value = stockText,
                        onValueChange = { stockText = it },
                        label = { Text("الكمية المتوفرة في المخزن") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                item {
                    OutlinedTextField(
                        value = description,
                        onValueChange = { description = it },
                        label = { Text("وصف المنتج وملاحظات الجودة") },
                        maxLines = 2,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val price = priceText.toDoubleOrNull() ?: 0.0
                    val unitsPerPackage = unitsPerPackageText.toIntOrNull() ?: 12
                    val stock = stockText.toIntOrNull() ?: 0
                    if (name.isNotBlank() && price > 0) {
                        onSave(name, category, price, packageUnit, unitsPerPackage, stock, description)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = FadixNavyPrimary)
            ) {
                Text("حفظ التغييرات", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("إلغاء", color = Color(0xFF64748B))
            }
        }
    )
}

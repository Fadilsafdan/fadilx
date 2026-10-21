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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PersonOutline
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.FadixBorder
import com.example.ui.theme.FadixError
import com.example.ui.theme.FadixNavy
import com.example.ui.theme.FadixNavyDark
import com.example.ui.theme.FadixYellow
import com.example.ui.viewmodel.FadixViewModel
import com.example.ui.viewmodel.Screen

@Composable
fun AccountScreen(
    viewModel: FadixViewModel,
    modifier: Modifier = Modifier
) {
    BackHandler {
        viewModel.navigateTo(Screen.Home)
    }

    val customerProfile by viewModel.customerProfile.collectAsState()

    var showEditProfileDialog by remember { mutableStateOf(false) }
    var showAddressDialog by remember { mutableStateOf(false) }
    var showHelpDialog by remember { mutableStateOf(false) }

    // Dialog for "معلوماتي"
    if (showEditProfileDialog) {
        var nameInput by remember { mutableStateOf(customerProfile?.grocerName ?: "") }
        var shopInput by remember { mutableStateOf(customerProfile?.shopName ?: "") }
        var phoneInput by remember { mutableStateOf(customerProfile?.phone ?: "") }

        AlertDialog(
            onDismissRequest = { showEditProfileDialog = false },
            containerColor = Color.White,
            shape = RoundedCornerShape(14.dp),
            title = { Text("معلوماتي الشخصية والمحل", fontWeight = FontWeight.Bold, color = FadixNavy) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = nameInput,
                        onValueChange = { nameInput = it },
                        label = { Text("اسم التاجر") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = shopInput,
                        onValueChange = { shopInput = it },
                        label = { Text("اسم البقالة / المتجر") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = phoneInput,
                        onValueChange = { phoneInput = it },
                        label = { Text("رقم الهاتف") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.authenticatePhone(
                            phone = phoneInput,
                            grocerName = nameInput,
                            shopName = shopInput,
                            address = customerProfile?.deliveryAddress ?: "الدار البيضاء",
                            zone = customerProfile?.deliveryZone ?: "داخل الدار البيضاء"
                        )
                        showEditProfileDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = FadixNavy)
                ) {
                    Text("حفظ التغييرات")
                }
            },
            dismissButton = {
                TextButton(onClick = { showEditProfileDialog = false }) {
                    Text("إلغاء", color = Color(0xFF6B7280))
                }
            }
        )
    }

    // Dialog for "عناويني"
    if (showAddressDialog) {
        var addressInput by remember { mutableStateOf(customerProfile?.deliveryAddress ?: "") }
        AlertDialog(
            onDismissRequest = { showAddressDialog = false },
            containerColor = Color.White,
            shape = RoundedCornerShape(14.dp),
            title = { Text("عنوان التوصيل بالدار البيضاء", fontWeight = FontWeight.Bold, color = FadixNavy) },
            text = {
                Column {
                    OutlinedTextField(
                        value = addressInput,
                        onValueChange = { addressInput = it },
                        label = { Text("عنوان المحل بالتفصيل") },
                        maxLines = 3,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        customerProfile?.let {
                            viewModel.authenticatePhone(
                                phone = it.phone,
                                grocerName = it.grocerName,
                                shopName = it.shopName,
                                address = addressInput,
                                zone = it.deliveryZone
                            )
                        }
                        showAddressDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = FadixNavy)
                ) {
                    Text("تحديث العنوان")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddressDialog = false }) {
                    Text("إلغاء", color = Color(0xFF6B7280))
                }
            }
        )
    }

    // Dialog for "المساعدة"
    if (showHelpDialog) {
        AlertDialog(
            onDismissRequest = { showHelpDialog = false },
            containerColor = Color.White,
            shape = RoundedCornerShape(14.dp),
            title = { Text("المساعدة وخدمة الزبائن", fontWeight = FontWeight.Bold, color = FadixNavy) },
            text = {
                Column {
                    Text(
                        text = "فريق الدعم والتوزيع لمنصة FADIX جاهز للإجابة عن استفساراتكم وتأكيد شحنات الجملة اليومية بالدار البيضاء.",
                        fontSize = 13.sp,
                        color = Color(0xFF4B5563),
                        lineHeight = 18.sp
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "الهاتف المباشر: 0522-XXXXXX\nالبريد: support@fadix.ma\nساعات العمل: من 8:00 صباحاً إلى 8:00 مساءً",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = FadixNavyDark
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = { showHelpDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = FadixNavy)
                ) {
                    Text("حسناً")
                }
            }
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF4F5F7))
    ) {
        // Top Header matching Screen 9 in reference design
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
                IconButton(onClick = { viewModel.navigateTo(Screen.Home) }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "الرئيسية",
                        tint = Color.White
                    )
                }
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "حسابي",
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
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Profile Big Navy Avatar Circle matching Screen 9
            item {
                Spacer(modifier = Modifier.height(10.dp))
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(FadixNavy),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "الصورة الشخصية",
                        tint = Color.White,
                        modifier = Modifier.size(46.dp)
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Name: "مرحباً تاجر"
                Text(
                    text = "مرحباً",
                    fontSize = 15.sp,
                    color = Color(0xFF6B7280)
                )
                Text(
                    text = customerProfile?.grocerName?.ifBlank { "تاجر" } ?: "تاجر",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = FadixNavyDark
                )
                Text(
                    text = customerProfile?.shopName?.ifBlank { "بقالة التاجر الشريك" } ?: "بقالة التاجر الشريك",
                    fontSize = 12.sp,
                    color = Color(0xFF9CA3AF)
                )

                Spacer(modifier = Modifier.height(18.dp))
            }

            // Menu Options matching Screen 9 in reference design
            item {
                AccountMenuCard(
                    title = "معلوماتي",
                    icon = Icons.Default.PersonOutline,
                    onClick = { showEditProfileDialog = true }
                )
            }

            item {
                AccountMenuCard(
                    title = "طلباتي",
                    icon = Icons.Default.ReceiptLong,
                    onClick = { viewModel.navigateTo(Screen.Orders) }
                )
            }

            item {
                AccountMenuCard(
                    title = "عناويني",
                    icon = Icons.Default.LocationOn,
                    onClick = { showAddressDialog = true }
                )
            }

            item {
                AccountMenuCard(
                    title = "المساعدة",
                    icon = Icons.Default.HelpOutline,
                    onClick = { showHelpDialog = true }
                )
            }

            item {
                AccountMenuCard(
                    title = "لوحة الإدارة (المالك)",
                    icon = Icons.Default.AdminPanelSettings,
                    onClick = { viewModel.navigateTo(Screen.Admin) }
                )
            }

            item {
                AccountMenuCard(
                    title = "تسجيل الخروج",
                    icon = Icons.Default.Logout,
                    iconTint = FadixError,
                    textColor = FadixError,
                    onClick = { viewModel.logout() }
                )
            }
        }
    }
}

@Composable
fun AccountMenuCard(
    title: String,
    icon: ImageVector,
    onClick: () -> Unit,
    iconTint: Color = FadixNavy,
    textColor: Color = FadixNavyDark,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, FadixBorder),
        shape = RoundedCornerShape(10.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.5.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = iconTint,
                    modifier = Modifier.size(22.dp)
                )
                Spacer(modifier = Modifier.width(14.dp))
                Text(
                    text = title,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = textColor
                )
            }

            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                contentDescription = null,
                tint = Color(0xFF9CA3AF),
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

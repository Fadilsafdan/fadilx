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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.FadixLogo
import com.example.ui.theme.FadixBorder
import com.example.ui.theme.FadixNavy
import com.example.ui.theme.FadixNavyDark
import com.example.ui.theme.FadixYellow
import com.example.ui.viewmodel.FadixViewModel
import com.example.ui.viewmodel.Screen

@Composable
fun LoginScreen(
    viewModel: FadixViewModel,
    modifier: Modifier = Modifier
) {
    BackHandler {
        viewModel.navigateTo(Screen.Home)
    }

    var phoneInput by remember { mutableStateOf("") }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White)
            .statusBarsPadding()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Top Back Button
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Start
        ) {
            IconButton(onClick = { viewModel.navigateTo(Screen.Home) }) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "رجوع",
                    tint = FadixNavy
                )
            }
        }

        // Center Content matching Screen 2 in reference design
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            // Big FADIX logo on light background
            FadixLogo(
                isDarkBackground = false,
                showSubtitle = true,
                horizontal = false,
                scale = 1.15f
            )

            Spacer(modifier = Modifier.height(36.dp))

            Text(
                text = "مرحباً بك",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = FadixNavyDark
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "سجل دخولك برقم الهاتف",
                fontSize = 13.sp,
                color = Color(0xFF6B7280)
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Phone number field
            OutlinedTextField(
                value = phoneInput,
                onValueChange = { phoneInput = it },
                placeholder = {
                    Text(
                        text = "رقم الهاتف",
                        color = Color(0xFF9CA3AF),
                        fontSize = 14.sp
                    )
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Phone,
                        contentDescription = null,
                        tint = FadixNavy,
                        modifier = Modifier.size(20.dp)
                    )
                },
                singleLine = true,
                shape = RoundedCornerShape(10.dp),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = FadixNavy,
                    unfocusedBorderColor = FadixBorder
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("login_phone_input")
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Yellow Sign In Button: "دخول"
            Button(
                onClick = {
                    val phone = phoneInput.ifBlank { "0661234567" }
                    viewModel.authenticatePhone(
                        phone = phone,
                        grocerName = "رشيد المنصوري",
                        shopName = "بقالة الخير والبركة",
                        address = "شارع الحسن الثاني، درب غلف",
                        zone = "داخل الدار البيضاء"
                    )
                    viewModel.navigateTo(Screen.Home)
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = FadixYellow,
                    contentColor = FadixNavyDark
                ),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("login_submit_button")
            ) {
                Text(
                    text = "دخول",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "ليس لديك حساب؟",
                fontSize = 12.sp,
                color = Color(0xFF6B7280)
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Outlined Create Account Button: "إنشاء حساب جديد"
            OutlinedButton(
                onClick = {
                    val phone = phoneInput.ifBlank { "0661234567" }
                    viewModel.authenticatePhone(
                        phone = phone,
                        grocerName = "تاجر جديد",
                        shopName = "بقالة التاجر",
                        address = "الدار البيضاء",
                        zone = "داخل الدار البيضاء"
                    )
                    viewModel.navigateTo(Screen.Account)
                },
                shape = RoundedCornerShape(10.dp),
                border = BorderStroke(1.dp, FadixNavy),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
            ) {
                Text(
                    text = "إنشاء حساب جديد",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = FadixNavy
                )
            }
        }

        // Footer: FADIX © 2026
        Text(
            text = "FADIX © 2026",
            fontSize = 11.sp,
            color = Color(0xFF9CA3AF),
            textAlign = TextAlign.Center
        )
    }
}

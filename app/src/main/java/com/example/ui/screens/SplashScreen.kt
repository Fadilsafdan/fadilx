package com.example.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.components.FadixLogo
import com.example.ui.theme.FadixNavyDark
import com.example.ui.theme.FadixYellow
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    onStartShopping: () -> Unit,
    modifier: Modifier = Modifier,
    autoNavigateDurationMs: Long = 2800L
) {
    LaunchedEffect(Unit) {
        if (autoNavigateDurationMs > 0) {
            delay(autoNavigateDurationMs)
            onStartShopping()
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF0B1D3A)) // Deep Navy Blue (#0B1D3A)
            .testTag("splash_screen")
    ) {
        // Diagonal golden geometric accent lines in background
        Canvas(modifier = Modifier.fillMaxSize()) {
            val goldColor = Color(0xFFF5B800).copy(alpha = 0.22f)
            val strokeWidth = 1.5.dp.toPx()

            // Diagonal decorative lines
            drawLine(
                color = goldColor,
                start = Offset(-50f, size.height * 0.15f),
                end = Offset(size.width * 0.85f, -50f),
                strokeWidth = strokeWidth
            )
            drawLine(
                color = goldColor,
                start = Offset(-20f, size.height * 0.35f),
                end = Offset(size.width + 40f, size.height * 0.05f),
                strokeWidth = strokeWidth
            )
            drawLine(
                color = goldColor,
                start = Offset(0f, size.height * 0.70f),
                end = Offset(size.width + 60f, size.height * 0.45f),
                strokeWidth = strokeWidth
            )
            drawLine(
                color = goldColor.copy(alpha = 0.15f),
                start = Offset(size.width * 0.3f, size.height + 50f),
                end = Offset(size.width + 50f, size.height * 0.65f),
                strokeWidth = strokeWidth
            )
        }

        // Center Content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Spacer(modifier = Modifier.height(60.dp))

            // Centered Logo & Tagline
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // Centered "FADIX للتجارة بالجملة" logo
                FadixLogo(
                    isDarkBackground = true,
                    showSubtitle = true,
                    horizontal = false,
                    scale = 1.35f
                )

                Spacer(modifier = Modifier.height(28.dp))

                // Tagline matching reference: "أكثر من مجرد منتجات .. شريكك في النجاح"
                Text(
                    text = "أكثر من مجرد منتجات .. شريكك في النجاح",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFFE2E8F0),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(32.dp))

                // Action button to start shopping immediately
                Button(
                    onClick = onStartShopping,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = FadixYellow,
                        contentColor = FadixNavyDark
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth(0.75f)
                        .height(46.dp)
                        .testTag("splash_start_button")
                ) {
                    Text(
                        text = "ابدأ التسوق",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Bottom graphic: Golden line-art illustration of the Casablanca city skyline
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painter = painterResource(id = R.drawable.casablanca_skyline),
                    contentDescription = "Casablanca City Skyline",
                    contentScale = ContentScale.FillWidth,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(95.dp)
                )
            }
        }
    }
}

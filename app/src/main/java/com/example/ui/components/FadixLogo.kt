package com.example.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.theme.FadixNavy
import com.example.ui.theme.FadixYellow

@Composable
fun FadixLogo(
    modifier: Modifier = Modifier,
    isDarkBackground: Boolean = true,
    showSubtitle: Boolean = true,
    horizontal: Boolean = true,
    scale: Float = 1.0f
) {
    val primaryTextColor = if (isDarkBackground) Color.White else FadixNavy
    val subtitleTextColor = if (isDarkBackground) Color(0xFFE2E8F0) else Color(0xFF475569)

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Main Brand Row: Cart Emblem + Brand Name
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            // Cart Emblem
            Image(
                painter = painterResource(id = R.drawable.ic_fadilex_cart_emblem),
                contentDescription = "FADILEX Logo",
                modifier = Modifier
                    .size((34 * scale).dp)
                    .padding(end = (4 * scale).dp)
            )

            // Brand Typography: FADI in white, LEX in golden yellow
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "FADI",
                    color = primaryTextColor,
                    fontSize = (22 * scale).sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = (0.5 * scale).sp
                )
                Text(
                    text = "LEX",
                    color = FadixYellow,
                    fontSize = (22 * scale).sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = (0.5 * scale).sp
                )
            }
        }

        if (showSubtitle) {
            Spacer(modifier = Modifier.height((2 * scale).dp))
            // Subtitle: "للتجارة بالجملة"
            Text(
                text = "للتجارة بالجملة",
                color = subtitleTextColor,
                fontSize = (10.5 * scale).sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height((2 * scale).dp))

            // Golden accent line with dots matching reference: — • • —
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy((3 * scale).dp)
            ) {
                Box(
                    modifier = Modifier
                        .width((28 * scale).dp)
                        .height((2 * scale).dp)
                        .clip(RoundedCornerShape(1.dp))
                        .background(FadixYellow)
                )
                Box(
                    modifier = Modifier
                        .size((3.5 * scale).dp)
                        .clip(CircleShape)
                        .background(FadixYellow)
                )
                Box(
                    modifier = Modifier
                        .size((3.5 * scale).dp)
                        .clip(CircleShape)
                        .background(FadixYellow)
                )
                Box(
                    modifier = Modifier
                        .width((28 * scale).dp)
                        .height((2 * scale).dp)
                        .clip(RoundedCornerShape(1.dp))
                        .background(FadixYellow)
                )
            }
        }
    }
}

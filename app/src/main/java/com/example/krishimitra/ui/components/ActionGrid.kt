package com.example.krishimitra.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.krishimitra.R
import com.example.krishimitra.ui.theme.*

data class ActionItem(
    val title: String,
    val subtitle: String,
    val icon: ImageVector? = null,
    val iconColor: Color = Color.Unspecified,
    val imageRes: Int? = null,
    val bgColor: Color,
    val actionType: String
)

@Composable
fun ActionGrid(
    onActionClick: (String) -> Unit = {}
) {
    val items = listOf(
        ActionItem(
            title = "Buy Plants",
            subtitle = "from nearby\nnurseries",
            imageRes = R.drawable.seedling_tomato,
            bgColor = ActionBgGreen,
            actionType = "BUY_PLANTS"
        ),
        ActionItem(
            title = "Pre-Order",
            subtitle = "for next\nseason",
            imageRes = R.drawable.nursery_seedlings,
            bgColor = ActionBgAmber,
            actionType = "PRE_ORDER"
        ),
        ActionItem(
            title = "Crop\nRecommendation",
            subtitle = "AI suitability",
            icon = Icons.Default.Psychology,
            iconColor = Color(0xFF7C3AED),
            bgColor = ActionBgPurple,
            actionType = "CROP_RECOMMENDATION"
        ),
        ActionItem(
            title = "Weather\nUpdates",
            subtitle = "Forecast & rain",
            icon = Icons.Default.WbSunny,
            iconColor = SkyBlue,
            bgColor = ActionBgBlue,
            actionType = "WEATHER"
        ),
        ActionItem(
            title = "Disease\nDetection",
            subtitle = "Scan & detect",
            icon = Icons.Default.Shield,
            iconColor = AlertRed,
            bgColor = ActionBgPink,
            actionType = "DISEASE_DETECTION"
        ),
        ActionItem(
            title = "Farmer\nCommunity",
            subtitle = "Ask & share",
            icon = Icons.Default.Groups,
            iconColor = Color(0xFF0D9488),
            bgColor = ActionBgCyan,
            actionType = "COMMUNITY"
        )
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp, vertical = 6.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            items.take(3).forEach { item ->
                ActionTile(item = item, onClick = { onActionClick(item.actionType) }, modifier = Modifier.weight(1f))
            }
        }
        Spacer(modifier = Modifier.height(10.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            items.drop(3).forEach { item ->
                ActionTile(item = item, onClick = { onActionClick(item.actionType) }, modifier = Modifier.weight(1f))
            }
        }
    }
}

@Composable
private fun ActionTile(
    item: ActionItem,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .padding(horizontal = 4.dp)
            .clickable(onClick = onClick)
    ) {
        Box(
            modifier = Modifier
                .size(56.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(item.bgColor),
            contentAlignment = Alignment.Center
        ) {
            if (item.imageRes != null) {
                Image(
                    painter = painterResource(id = item.imageRes),
                    contentDescription = item.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            } else if (item.icon != null) {
                Icon(
                    imageVector = item.icon,
                    contentDescription = item.title,
                    tint = item.iconColor,
                    modifier = Modifier.size(28.dp)
                )
            }
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = item.title,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = TextDark,
            textAlign = TextAlign.Center,
            lineHeight = 14.sp
        )
        Text(
            text = item.subtitle,
            fontSize = 9.sp,
            fontWeight = FontWeight.Normal,
            color = TextMedium,
            textAlign = TextAlign.Center,
            lineHeight = 11.sp
        )
    }
}

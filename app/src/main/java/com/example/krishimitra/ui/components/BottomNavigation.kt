package com.example.krishimitra.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CropFree
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.krishimitra.ui.theme.*

enum class NavigationTab {
    HOME,
    MARKETPLACE,
    SCAN_PLANT,
    COMMUNITY,
    PROFILE
}

@Composable
fun KrishiBottomBar(
    currentTab: NavigationTab,
    onTabSelected: (NavigationTab) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding(),
        contentAlignment = Alignment.BottomCenter
    ) {
        // Base bar
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp)
                .shadow(elevation = 12.dp),
            color = Color.White
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 12.dp),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Tab 1: Home
                NavTabItem(
                    label = "Home",
                    icon = Icons.Default.Home,
                    isSelected = currentTab == NavigationTab.HOME,
                    onClick = { onTabSelected(NavigationTab.HOME) }
                )

                // Tab 2: Marketplace
                NavTabItem(
                    label = "Marketplace",
                    icon = Icons.Default.Storefront,
                    isSelected = currentTab == NavigationTab.MARKETPLACE,
                    onClick = { onTabSelected(NavigationTab.MARKETPLACE) }
                )

                // Center placeholder spacing for elevated FAB
                Spacer(modifier = Modifier.width(56.dp))

                // Tab 4: Community
                NavTabItem(
                    label = "Community",
                    icon = Icons.Default.Spa,
                    isSelected = currentTab == NavigationTab.COMMUNITY,
                    onClick = { onTabSelected(NavigationTab.COMMUNITY) }
                )

                // Tab 5: Profile
                NavTabItem(
                    label = "Profile",
                    icon = Icons.Default.Person,
                    isSelected = currentTab == NavigationTab.PROFILE,
                    onClick = { onTabSelected(NavigationTab.PROFILE) }
                )
            }
        }

        // Center Floating Scan Plant Button
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .offset(y = (-14).dp)
                .clickable { onTabSelected(NavigationTab.SCAN_PLANT) }
        ) {
            Box(
                modifier = Modifier
                    .size(54.dp)
                    .clip(CircleShape)
                    .background(
                        brush = Brush.linearGradient(
                            colors = listOf(PrimaryGreen, PrimaryGreenDark)
                        )
                    )
                    .shadow(elevation = 8.dp, shape = CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Scan Plant",
                    tint = Color.White,
                    modifier = Modifier.size(30.dp)
                )
            }
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "Scan Plant",
                fontSize = 11.sp,
                fontWeight = if (currentTab == NavigationTab.SCAN_PLANT) FontWeight.Bold else FontWeight.Medium,
                color = if (currentTab == NavigationTab.SCAN_PLANT) PrimaryGreen else TextMedium
            )
        }
    }
}

@Composable
private fun NavTabItem(
    label: String,
    icon: ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .clickable(onClick = onClick)
            .padding(horizontal = 6.dp, vertical = 4.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = if (isSelected) PrimaryGreen else TextLight,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = label,
            fontSize = 10.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            color = if (isSelected) PrimaryGreen else TextMedium
        )
    }
}

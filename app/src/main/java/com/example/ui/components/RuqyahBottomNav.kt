package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoStories
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.AppTab
import com.example.ui.theme.Emerald700
import com.example.ui.theme.Slate500

@Composable
fun RuqyahBottomNav(
    activeTab: AppTab,
    onTabSelected: (AppTab) -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shadowElevation = 8.dp,
        color = Color.White
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 6.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            NavItem(
                tab = AppTab.HOME,
                icon = Icons.Default.Home,
                isActive = activeTab == AppTab.HOME,
                onClick = { onTabSelected(AppTab.HOME) }
            )
            NavItem(
                tab = AppTab.AZKAR,
                icon = Icons.Default.MenuBook,
                isActive = activeTab == AppTab.AZKAR,
                onClick = { onTabSelected(AppTab.AZKAR) }
            )
            NavItem(
                tab = AppTab.QURAN,
                icon = Icons.Default.AutoStories,
                isActive = activeTab == AppTab.QURAN,
                onClick = { onTabSelected(AppTab.QURAN) }
            )
            NavItem(
                tab = AppTab.NAMES,
                icon = Icons.Default.Star,
                isActive = activeTab == AppTab.NAMES,
                onClick = { onTabSelected(AppTab.NAMES) }
            )
            NavItem(
                tab = AppTab.HISTORY,
                icon = Icons.Default.History,
                isActive = activeTab == AppTab.HISTORY,
                onClick = { onTabSelected(AppTab.HISTORY) }
            )
        }
    }
}

@Composable
private fun NavItem(
    tab: AppTab,
    icon: ImageVector,
    isActive: Boolean,
    onClick: () -> Unit
) {
    val color = if (isActive) Emerald700 else Slate500

    IconButton(
        onClick = onClick,
        modifier = Modifier.padding(2.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = tab.title,
                tint = color,
                modifier = Modifier.size(22.dp)
            )
            Text(
                text = tab.title,
                color = color,
                fontSize = 10.sp,
                fontWeight = if (isActive) FontWeight.Bold else FontWeight.Normal
            )
        }
    }
}

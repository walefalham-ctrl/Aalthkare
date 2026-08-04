package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AzkarData
import com.example.model.ZikrCategory
import com.example.model.ZikrItem
import com.example.ui.RuqyahUiState
import com.example.ui.theme.Amber100
import com.example.ui.theme.Amber500
import com.example.ui.theme.Amber700
import com.example.ui.theme.Emerald100
import com.example.ui.theme.Emerald50
import com.example.ui.theme.Emerald600
import com.example.ui.theme.Emerald700
import com.example.ui.theme.Emerald800
import com.example.ui.theme.Slate200
import com.example.ui.theme.Slate500
import com.example.ui.theme.Slate800

@Composable
fun AzkarDeckScreen(
    uiState: RuqyahUiState,
    onSelectCategory: (ZikrCategory) -> Unit,
    onNextZikr: () -> Unit = {},
    onPrevZikr: () -> Unit = {},
    onTapCounter: () -> Unit = {},
    onResetCounter: () -> Unit = {},
    onTapZikrItem: (ZikrItem) -> Unit = {},
    onResetZikrItem: (ZikrItem) -> Unit = {}
) {
    val scrollState = rememberScrollState()

    // State for expanded categories in the Expandable List
    var expandedCategories by remember {
        mutableStateOf(setOf<ZikrCategory>())
    }

    var selectedFilterTab by remember {
        mutableStateOf<ZikrCategory?>(null) // null = show all sections
    }

    val displayedCategories = if (selectedFilterTab != null) {
        listOf(selectedFilterTab!!)
    } else {
        ZikrCategory.entries
    }

    // Total Azkar Progress summary across all sections
    val totalAzkarCount = AzkarData.allAzkar.size
    val totalCompletedAzkar = AzkarData.allAzkar.count { zikr ->
        val cnt = uiState.zikrProgressMap[zikr.id] ?: 0
        cnt >= zikr.targetCount
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Overall Summary Banner
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            border = BorderStroke(1.dp, Emerald100)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "🌿 قائمة الأذكار القابلة للطي",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "عرض ذكر واحد لكل قسم لتجنب الازدحام مع إمكانية التوسع",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(Emerald100)
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = "$totalCompletedAzkar / $totalAzkarCount مكتمل ⚡",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Emerald800
                    )
                }
            }
        }

        // Category Filter Tabs
        ScrollableTabRow(
            selectedTabIndex = if (selectedFilterTab == null) 0 else ZikrCategory.entries.indexOf(selectedFilterTab) + 1,
            edgePadding = 0.dp,
            containerColor = Color.Transparent,
            divider = {},
            indicator = {}
        ) {
            // "الكل" Tab
            val isAllSelected = selectedFilterTab == null
            Box(
                modifier = Modifier
                    .padding(end = 8.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(if (isAllSelected) Emerald700 else Color.White)
                    .border(
                        1.dp,
                        if (isAllSelected) Color.Transparent else Slate200,
                        RoundedCornerShape(12.dp)
                    )
                    .clickable { selectedFilterTab = null }
                    .padding(horizontal = 14.dp, vertical = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "جميع الأقسام ✨",
                    color = if (isAllSelected) Color.White else Slate800,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            ZikrCategory.entries.forEach { cat ->
                val isSelected = selectedFilterTab == cat
                val bgColor = if (isSelected) {
                    if (cat == ZikrCategory.TAJ) Amber500 else Emerald700
                } else Color.White

                Box(
                    modifier = Modifier
                        .padding(end = 8.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(bgColor)
                        .border(
                            1.dp,
                            if (isSelected) Color.Transparent else Slate200,
                            RoundedCornerShape(12.dp)
                        )
                        .clickable { selectedFilterTab = cat }
                        .padding(horizontal = 14.dp, vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = cat.label,
                        color = if (isSelected) Color.White else Slate800,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        // Expandable List Sections per Category
        displayedCategories.forEach { category ->
            val categoryItems = AzkarData.allAzkar.filter { it.category == category }
            val completedInCat = categoryItems.count { zikr ->
                val cnt = uiState.zikrProgressMap[zikr.id] ?: 0
                cnt >= zikr.targetCount
            }

            val isExpanded = expandedCategories.contains(category)

            ExpandableCategorySection(
                category = category,
                categoryItems = categoryItems,
                completedCount = completedInCat,
                isExpanded = isExpanded,
                zikrProgressMap = uiState.zikrProgressMap,
                onToggleExpand = {
                    expandedCategories = if (isExpanded) {
                        expandedCategories - category
                    } else {
                        expandedCategories + category
                    }
                },
                onTapZikr = { item ->
                    onTapZikrItem(item)
                    onTapCounter()
                },
                onResetZikr = { item ->
                    onResetZikrItem(item)
                }
            )
        }
    }
}

@Composable
fun ExpandableCategorySection(
    category: ZikrCategory,
    categoryItems: List<ZikrItem>,
    completedCount: Int,
    isExpanded: Boolean,
    zikrProgressMap: Map<String, Int>,
    onToggleExpand: () -> Unit,
    onTapZikr: (ZikrItem) -> Unit,
    onResetZikr: (ZikrItem) -> Unit
) {
    val totalCount = categoryItems.size
    val isAllCatDone = totalCount > 0 && completedCount >= totalCount

    val firstItem = categoryItems.firstOrNull()
    val remainingItems = if (categoryItems.size > 1) categoryItems.drop(1) else emptyList()

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
        border = BorderStroke(1.dp, if (isAllCatDone) Emerald600 else Slate200)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Section Header Banner
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onToggleExpand() },
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (category == ZikrCategory.TAJ) Color(0xFFFEF3C7) else Emerald100),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.WbSunny,
                            contentDescription = null,
                            tint = if (category == ZikrCategory.TAJ) Amber700 else Emerald700,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Column {
                        Text(
                            text = category.label,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = if (isAllCatDone) "مكتمل بالكامل 🎉 ($completedCount/$totalCount)" else "$completedCount من $totalCount أذكار أنجزت",
                            fontSize = 11.sp,
                            color = if (isAllCatDone) Emerald700 else Slate500
                        )
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (isAllCatDone) Emerald100 else Slate200)
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "$completedCount/$totalCount",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isAllCatDone) Emerald800 else Slate800
                        )
                    }

                    Spacer(modifier = Modifier.width(4.dp))

                    IconButton(onClick = onToggleExpand) {
                        Icon(
                            imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                            contentDescription = if (isExpanded) "طي القسم" else "توسيع القسم",
                            tint = Emerald700
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // 1st Zikr Card (Always visible as 1 zikr preview per section)
            if (firstItem != null) {
                val currentDone = zikrProgressMap[firstItem.id] ?: 0
                SingleZikrCardItem(
                    item = firstItem,
                    doneCount = currentDone,
                    onTap = { onTapZikr(firstItem) },
                    onReset = { onResetZikr(firstItem) }
                )
            }

            // Expand / Collapse Button for remaining items in this section
            if (remainingItems.isNotEmpty()) {
                Spacer(modifier = Modifier.height(10.dp))

                OutlinedButton(
                    onClick = onToggleExpand,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(42.dp)
                        .testTag("expand_category_${category.id}"),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, Emerald700),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Emerald700)
                ) {
                    Icon(
                        imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (isExpanded) {
                            "طي أذكار ${category.label} 🔼"
                        } else {
                            "عرض باقي أذكار ${category.label} (+${remainingItems.size} أذكار) 🔽"
                        },
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                // Expanded Section Content with remaining Azkar
                AnimatedVisibility(
                    visible = isExpanded,
                    enter = expandVertically() + fadeIn(),
                    exit = shrinkVertically() + fadeOut()
                ) {
                    Column(
                        modifier = Modifier.padding(top = 12.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        remainingItems.forEach { item ->
                            val currentDone = zikrProgressMap[item.id] ?: 0
                            SingleZikrCardItem(
                                item = item,
                                doneCount = currentDone,
                                onTap = { onTapZikr(item) },
                                onReset = { onResetZikr(item) }
                            )
                        }

                        // Collapse Button at bottom of expanded section
                        TextButton(
                            onClick = onToggleExpand,
                            modifier = Modifier.align(Alignment.CenterHorizontally)
                        ) {
                            Text(
                                text = "طي هذه القائمة 🔼",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Slate500
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SingleZikrCardItem(
    item: ZikrItem,
    doneCount: Int,
    onTap: () -> Unit,
    onReset: () -> Unit
) {
    val isCompleted = doneCount >= item.targetCount

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isCompleted) Emerald50.copy(alpha = 0.5f) else Color(0xFFF8FAFC)
        ),
        border = BorderStroke(1.dp, if (isCompleted) Emerald600 else Slate200)
    ) {
        Column(
            modifier = Modifier.padding(14.dp)
        ) {
            // Header Row: Title & Target Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Book,
                        contentDescription = null,
                        tint = Emerald700,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = item.title,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (isCompleted) Emerald100 else Amber100)
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = "التكرار: ${item.targetCount} مرة",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isCompleted) Emerald800 else Amber700
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Zikr Text
            Text(
                text = item.text,
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
                fontFamily = FontFamily.Serif,
                color = MaterialTheme.colorScheme.onSurface,
                lineHeight = 24.sp,
                textAlign = TextAlign.Justify,
                modifier = Modifier.padding(horizontal = 2.dp)
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Virtue Box
            if (item.virtue.isNotEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFFFFFBEB))
                        .border(1.dp, Color(0xFFFDE68A), RoundedCornerShape(12.dp))
                        .padding(10.dp)
                ) {
                    Row(verticalAlignment = Alignment.Top) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = null,
                            tint = Amber500,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = item.virtue,
                            fontSize = 11.sp,
                            color = Color(0xFF78350F),
                            lineHeight = 16.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))
            }

            // Interactive Counter Button
            val buttonGradient = if (isCompleted) {
                Brush.horizontalGradient(listOf(Emerald600, Emerald700))
            } else {
                Brush.horizontalGradient(listOf(Emerald700, Emerald800))
            }

            Button(
                onClick = onTap,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("zikr_item_btn_${item.id}"),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                contentPadding = androidx.compose.foundation.layout.PaddingValues()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(buttonGradient)
                        .padding(horizontal = 12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = if (isCompleted) Icons.Default.CheckCircle else Icons.Default.Fingerprint,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (isCompleted) {
                                "مكتمل بنجاح ✅ ($doneCount / ${item.targetCount})"
                            } else {
                                "اضغط للذكر ($doneCount / ${item.targetCount})"
                            },
                            color = Color.White,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            if (doneCount > 0) {
                TextButton(
                    onClick = onReset,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                ) {
                    Text(
                        text = "إعادة الضبط",
                        fontSize = 10.sp,
                        color = Slate500
                    )
                }
            }
        }
    }
}

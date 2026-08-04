package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
fun FamilyDuaaCard(
    isCompleted: Boolean,
    onToggleCompleted: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val backgroundColor by animateColorAsState(
        targetValue = if (isCompleted) Emerald50.copy(alpha = 0.6f) else Color.White,
        label = "bgColor"
    )

    val borderColor by animateColorAsState(
        targetValue = if (isCompleted) Emerald600 else Amber100,
        label = "borderColor"
    )

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
        border = BorderStroke(1.5.dp, borderColor)
    ) {
        Column(
            modifier = Modifier.padding(18.dp)
        ) {
            // Header Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (isCompleted) Emerald100 else Color(0xFFFEF3C7)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Favorite,
                            contentDescription = null,
                            tint = if (isCompleted) Emerald700 else Color(0xFFD97706),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "دعاء حفظ الأهل والأسرة",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = Slate800
                        )
                        Text(
                            text = "حفظ الزوجة «نوره» والابن «نهار»",
                            fontSize = 11.sp,
                            color = Slate500
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (isCompleted) Emerald700 else Color(0xFFFEF3C7))
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = if (isCompleted) Icons.Default.CheckCircle else Icons.Default.Shield,
                            contentDescription = null,
                            tint = if (isCompleted) Color.White else Color(0xFF92400E),
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (isCompleted) "تمت القراءة 🤲" else "ورد اليوم 🌿",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isCompleted) Color.White else Color(0xFF92400E)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Main Duaa Text Display
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(
                        if (isCompleted) Emerald100.copy(alpha = 0.4f)
                        else Color(0xFFFFFBEB)
                    )
                    .padding(16.dp)
            ) {
                Text(
                    text = "«اللهم احفظ لي زوجتي نوره وابني نهار، وبارك لي في عمرهما وصحتهما، واجعلهم دائماً في حفظك ورعايتك.»",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isCompleted) Slate500 else Color(0xFF78350F),
                    fontFamily = FontFamily.Serif,
                    textAlign = TextAlign.Center,
                    lineHeight = 26.sp,
                    textDecoration = if (isCompleted) TextDecoration.LineThrough else TextDecoration.None,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Interactive Checkbox / Toggle Section
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color(0xFFF8FAFC))
                    .clickable { onToggleCompleted(!isCompleted) }
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = isCompleted,
                    onCheckedChange = { onToggleCompleted(it) },
                    colors = CheckboxDefaults.colors(
                        checkedColor = Emerald700,
                        uncheckedColor = Slate500
                    ),
                    modifier = Modifier.testTag("family_duaa_checkbox")
                )

                Spacer(modifier = Modifier.width(6.dp))

                Column {
                    Text(
                        text = if (isCompleted) "تم قراءة هذا الدعاء وتفعيله اليوم" else "ضغط هنا لإتمام قراءة الدعاء",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isCompleted) Emerald800 else Slate800,
                        textDecoration = if (isCompleted) TextDecoration.LineThrough else TextDecoration.None
                    )
                    Text(
                        text = "يتغير التنسيق والمظهر فور إتمام القراءة",
                        fontSize = 10.sp,
                        color = Slate500
                    )
                }
            }

            // Animated Completion Banner
            AnimatedVisibility(
                visible = isCompleted,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Column {
                    Spacer(modifier = Modifier.height(10.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(Emerald100)
                            .padding(10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "✨ تمت قراءة الدعاء المبارك، حُرِسوا بعين الله التي لا تنام 🤲🌿",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Emerald800,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}

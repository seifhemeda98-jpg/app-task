package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.NightlightRound
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Upcoming
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.DayOfWeekHelper
import com.example.ui.components.AtmosphericBackground
import com.example.ui.components.LiquidGlassCard
import com.example.ui.viewmodel.PlannerUiState

@Composable
fun RemindersScreen(
    uiState: PlannerUiState,
    onNightReminderToggle: (Boolean) -> Unit,
    onOneHourToggle: (Boolean) -> Unit,
    onTomorrowSummaryToggle: (Boolean) -> Unit,
    onNightTimeSelected: (Int, Int) -> Unit,
    onTriggerTestNotification: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    AtmosphericBackground(accentColor = MaterialTheme.colorScheme.tertiary) {
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(top = 16.dp, bottom = 100.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Header Glass Card
            item {
                LiquidGlassCard(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(28.dp),
                    tintColor = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.5f),
                    glowColor = MaterialTheme.colorScheme.tertiary.copy(alpha = 0.2f)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.tertiary.copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.NotificationsActive,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.tertiary,
                                    modifier = Modifier.size(24.dp)
                                )
                            }

                            Column {
                                Text(
                                    text = "التذكيرات والتنبيهات الذكية 🔔",
                                    fontSize = 19.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "اضبط مواعيد تنبيهاتك لتتذكر المذاكرة والتسميع قبل الحصص.",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }

            // Section 1: Reminder Options Glass Card
            item {
                LiquidGlassCard(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(26.dp)
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Text(
                            text = "خيارات التذكير التلقائية:",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        // 1. Night Reminder
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.NightlightRound,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(22.dp)
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = "تذكير المذاكرة الليلي (المسائي)",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp
                                    )
                                    Text(
                                        text = "يفكرك بالليل بالمطلوب والواجب لكل مادة.",
                                        fontSize = 12.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                            Switch(
                                checked = uiState.isNightReminderEnabled,
                                onCheckedChange = onNightReminderToggle,
                                modifier = Modifier.testTag("switch_screen_night_reminder")
                            )
                        }

                        // Night Time Selector Chips
                        if (uiState.isNightReminderEnabled) {
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = "موعد التذكير الليلي:",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                listOf(
                                    Pair(20, 0) to "8:00 م",
                                    Pair(20, 30) to "8:30 م",
                                    Pair(21, 0) to "9:00 م",
                                    Pair(21, 30) to "9:30 م",
                                    Pair(22, 0) to "10:00 م"
                               ).forEach { (time, label) ->
                                    val isSelected = uiState.nightReminderHour == time.first && uiState.nightReminderMinute == time.second
                                    FilterChip(
                                        selected = isSelected,
                                        onClick = { onNightTimeSelected(time.first, time.second) },
                                        label = { Text(label, fontSize = 11.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal) },
                                        shape = RoundedCornerShape(12.dp)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // 2. 1 Hour Before Class Reminder
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Alarm,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.tertiary,
                                    modifier = Modifier.size(22.dp)
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = "تذكير قبل ميعاد الحصة بساعة",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp
                                    )
                                    Text(
                                        text = "ينبهك قبل موعد الحصة لتجهيز كتابك ومراجعة حفظك.",
                                        fontSize = 12.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                            Switch(
                                checked = uiState.isOneHourBeforeEnabled,
                                onCheckedChange = onOneHourToggle,
                                modifier = Modifier.testTag("switch_screen_one_hour_reminder")
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // 3. Tomorrow Summary Reminder
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Upcoming,
                                    contentDescription = null,
                                    tint = Color(0xFF16A34A),
                                    modifier = Modifier.size(22.dp)
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = "تذكير آخر كل يوم بجدول ومطلوبات الغد",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp
                                    )
                                    Text(
                                        text = "ملخص شامل لجدول الغد حتى ترتب حقيبتك ومستلزماتك.",
                                        fontSize = 12.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                            Switch(
                                checked = uiState.isTomorrowSummaryEnabled,
                                onCheckedChange = onTomorrowSummaryToggle,
                                modifier = Modifier.testTag("switch_screen_tomorrow_summary")
                            )
                        }
                    }
                }
            }

            // Section 2: Instant Notification Tester Glass Card
            item {
                LiquidGlassCard(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(26.dp)
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Campaign,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(22.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "اختبار وتجربة الإشعارات الفورية:",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "اضغط على أي زر لتلقي إشعار تجريبي فوري في شريط إشعارات هاتفك للتأكد من الصوت والتنبيه.",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        // Buttons for testing
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Button(
                                onClick = { onTriggerTestNotification("night") },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("btn_test_night_notification"),
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                                shape = RoundedCornerShape(16.dp)
                            ) {
                                Icon(imageVector = Icons.Default.NightlightRound, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("تجربة إشعار المذاكرة الليلي الآن 🌙")
                            }

                            Button(
                                onClick = { onTriggerTestNotification("one_hour") },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("btn_test_one_hour_notification"),
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.tertiary),
                                shape = RoundedCornerShape(16.dp)
                            ) {
                                Icon(imageVector = Icons.Default.Alarm, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("تجربة إشعار قبل الحصة بساعة ⏰")
                            }

                            Button(
                                onClick = { onTriggerTestNotification("tomorrow_summary") },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("btn_test_tomorrow_notification"),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF16A34A)),
                                shape = RoundedCornerShape(16.dp)
                            ) {
                                Icon(imageVector = Icons.Default.Upcoming, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("تجربة ملخص جدول ومطلوبات الغد 📋")
                            }

                            OutlinedButton(
                                onClick = { onTriggerTestNotification("general") },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(16.dp)
                            ) {
                                Text("تجربة إشعار فوري عام 🔔")
                            }
                        }
                    }
                }
            }
        }
    }
}

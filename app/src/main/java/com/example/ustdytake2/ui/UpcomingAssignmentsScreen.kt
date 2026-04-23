package com.example.ustdytake2.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.ustdytake2.ui.theme.Cream50
import com.example.ustdytake2.ui.theme.Ink900
import com.example.ustdytake2.ui.theme.Slate100
import com.example.ustdytake2.ui.theme.UstdyTake2Theme

@Composable
fun UpcomingAssignmentsScreen(
    userName: String,
    reminderPreferences: ReminderPreferences,
    tasks: List<PlannerTask>,
    streak: Int,
    badges: List<String>,
    onToggleTask: (PlannerTask, Boolean) -> Unit,
    onAddTask: () -> Unit,
    onEditTask: (PlannerTask) -> Unit,
    onDeleteTask: (PlannerTask) -> Unit,
    onRestart: () -> Unit,
    modifier: Modifier = Modifier
) {
    val weeklyTasks = remember(tasks) { tasks.filter { it.isDueThisWeek() }.sortedBy { it.dueDateMillis } }
    val days = remember { weekDays() }
    var selectedDay by remember { mutableLongStateOf(days.first()) }
    val selectedTasks = remember(weeklyTasks, selectedDay) { tasksForDay(weeklyTasks, selectedDay) }

    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF8F4ED))
            .padding(20.dp)
    ) {
        val wideLayout = maxWidth > 780.dp

        Column {
            Text(
                text = "Upcoming",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = Ink900
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "A calm view of your week, built around the items you entered.",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(18.dp))

            if (wideLayout) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    DashboardSummaryCard(
                        userName = userName,
                        reminderPreferences = reminderPreferences,
                        weeklyTaskCount = weeklyTasks.size,
                        streak = streak,
                        badges = badges,
                        onAddTask = onAddTask,
                        onRestart = onRestart,
                        modifier = Modifier.weight(1f)
                    )
                    WeekCalendarCard(
                        days = days,
                        tasks = weeklyTasks,
                        selectedDay = selectedDay,
                        onDaySelected = { selectedDay = it },
                        modifier = Modifier.weight(1.2f)
                    )
                }
            } else {
                DashboardSummaryCard(
                    userName = userName,
                    reminderPreferences = reminderPreferences,
                    weeklyTaskCount = weeklyTasks.size,
                    streak = streak,
                    badges = badges,
                    onAddTask = onAddTask,
                    onRestart = onRestart,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(16.dp))
                WeekCalendarCard(
                    days = days,
                    tasks = weeklyTasks,
                    selectedDay = selectedDay,
                    onDaySelected = { selectedDay = it },
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(modifier = Modifier.height(18.dp))
            SelectedDayCard(selectedDay = selectedDay, tasks = selectedTasks)
            Spacer(modifier = Modifier.height(18.dp))
            ChecklistCard(
                tasks = weeklyTasks,
                onToggleTask = onToggleTask,
                onEditTask = onEditTask,
                onDeleteTask = onDeleteTask
            )
        }
    }
}

@Composable
private fun DashboardSummaryCard(
    userName: String,
    reminderPreferences: ReminderPreferences,
    weeklyTaskCount: Int,
    streak: Int,
    badges: List<String>,
    onAddTask: () -> Unit,
    onRestart: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = Cream50)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                text = "Hello, $userName",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "$weeklyTaskCount items are due this week (${weekRangeLabel()}).",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Reminder defaults",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(8.dp))
            ReminderRow("Quizzes", reminderPreferences.quizzes.label)
            ReminderRow("Exams", reminderPreferences.exams.label)
            ReminderRow("Weekly Assignments", reminderPreferences.weeklyAssignments.label)
            ReminderRow("Large Projects", reminderPreferences.largeProjects.label)
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "Streak: $streak day${if (streak == 1) "" else "s"}",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = if (badges.isEmpty()) "Badges: none yet" else "Badges: ${badges.joinToString()}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(18.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Button(onClick = onAddTask, modifier = Modifier.weight(1f)) {
                    Text("Add Item")
                }
                TextButton(onClick = onRestart, modifier = Modifier.weight(1f)) {
                    Text("Start Over")
                }
            }
        }
    }
}

@Composable
private fun ReminderRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, style = MaterialTheme.typography.bodyMedium)
        Text(text = value, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
    }
    Spacer(modifier = Modifier.height(6.dp))
}

@Composable
private fun WeekCalendarCard(
    days: List<Long>,
    tasks: List<PlannerTask>,
    selectedDay: Long,
    onDaySelected: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = Cream50)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Text(
                text = "This Week",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(14.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                days.forEach { day ->
                    val dayTasks = tasksForDay(tasks, day)
                    val isSelected = startOfDay(day) == startOfDay(selectedDay)
                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .clickable { onDaySelected(day) },
                        colors = CardDefaults.cardColors(
                            containerColor = if (isSelected) Slate100 else Color.White
                        ),
                        shape = RoundedCornerShape(18.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(10.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = dayName(day),
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = dayNumber(day),
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            repeat(minOf(dayTasks.size, 3)) { index ->
                                Surface(
                                    color = dayTasks[index].type.color,
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(10.dp)
                                ) {}
                                Spacer(modifier = Modifier.height(4.dp))
                            }
                            if (dayTasks.size > 3) {
                                Text(text = "+${dayTasks.size - 3}", style = MaterialTheme.typography.labelSmall)
                            } else {
                                Spacer(modifier = Modifier.height(12.dp))
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SelectedDayCard(selectedDay: Long, tasks: List<PlannerTask>) {
    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Cream50)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Text(
                text = "Tasks for ${formatLongDate(selectedDay)}",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(10.dp))
            if (tasks.isEmpty()) {
                Text(
                    text = "Nothing due on this day yet.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else {
                tasks.forEach { task ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(text = task.title, fontWeight = FontWeight.Medium)
                            Text(
                                text = task.className,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        TypeBadge(type = task.type)
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
}

@Composable
private fun ChecklistCard(
    tasks: List<PlannerTask>,
    onToggleTask: (PlannerTask, Boolean) -> Unit,
    onEditTask: (PlannerTask) -> Unit,
    onDeleteTask: (PlannerTask) -> Unit
) {
    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Cream50)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Text(
                text = "Assignment Checklist",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(12.dp))
            if (tasks.isEmpty()) {
                Text(
                    text = "No upcoming items yet. Add one from the button above.",
                    style = MaterialTheme.typography.bodyMedium
                )
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    items(tasks, key = { it.id }) { task ->
                        Card(
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            shape = RoundedCornerShape(20.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Checkbox(
                                    checked = task.completed,
                                    onCheckedChange = { onToggleTask(task, it) }
                                )
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(text = task.title, fontWeight = FontWeight.Medium)
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                        TypeBadge(type = task.type)
                                        CourseBadge(course = task.className)
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "Due ${formatLongDate(task.dueDateMillis)}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                Column(horizontalAlignment = Alignment.End) {
                                    TextButton(onClick = { onEditTask(task) }) { Text("Edit") }
                                    TextButton(onClick = { onDeleteTask(task) }) { Text("Delete") }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun TypeBadge(type: TaskType) {
    Surface(
        color = type.color.copy(alpha = 0.22f),
        shape = RoundedCornerShape(999.dp)
    ) {
        Text(
            text = type.label,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
            style = MaterialTheme.typography.labelMedium,
            color = Ink900
        )
    }
}

@Composable
private fun CourseBadge(course: String) {
    Surface(
        color = Slate100,
        shape = RoundedCornerShape(999.dp)
    ) {
        Text(
            text = course,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
            style = MaterialTheme.typography.labelMedium
        )
    }
}

@Preview(showBackground = true, widthDp = 900, heightDp = 1200)
@Composable
private fun UpcomingAssignmentsScreenPreview() {
    UstdyTake2Theme {
        UpcomingAssignmentsScreen(
            userName = "Noah",
            reminderPreferences = ReminderPreferences(),
            tasks = demoTasks(),
            streak = 4,
            badges = listOf("Early Bird", "Consistency"),
            onToggleTask = { _, _ -> },
            onAddTask = {},
            onEditTask = {},
            onDeleteTask = {},
            onRestart = {}
        )
    }
}

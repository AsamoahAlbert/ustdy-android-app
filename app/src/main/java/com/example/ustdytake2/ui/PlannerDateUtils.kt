package com.example.ustdytake2.ui

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

private val shortFormatter = SimpleDateFormat("MMM d", Locale.US)
private val longFormatter = SimpleDateFormat("EEE, MMM d", Locale.US)
private val editorFormatter = SimpleDateFormat("MM/dd/yyyy", Locale.US).apply {
    isLenient = false
}

fun weekDays(): List<Long> {
    val calendar = Calendar.getInstance()
    calendar.firstDayOfWeek = Calendar.SUNDAY
    calendar.set(Calendar.DAY_OF_WEEK, calendar.firstDayOfWeek)
    setToStartOfDay(calendar)
    return List(7) {
        val value = calendar.timeInMillis
        calendar.add(Calendar.DAY_OF_YEAR, 1)
        setToStartOfDay(calendar)
        value
    }
}

fun weekRangeLabel(): String {
    val days = weekDays()
    return "${formatShortDate(days.first())} - ${formatShortDate(days.last())}"
}

fun formatShortDate(timeMillis: Long): String = shortFormatter.format(Date(timeMillis))

fun formatLongDate(timeMillis: Long): String = longFormatter.format(Date(timeMillis))

fun formatEditorDate(timeMillis: Long): String = editorFormatter.format(Date(timeMillis))

fun parseDate(value: String): Long? = runCatching { editorFormatter.parse(value)?.time }.getOrNull()

fun startOfDay(timeMillis: Long): Long {
    val calendar = Calendar.getInstance().apply { this.timeInMillis = timeMillis }
    setToStartOfDay(calendar)
    return calendar.timeInMillis
}

fun endOfDay(timeMillis: Long): Long {
    val calendar = Calendar.getInstance().apply { this.timeInMillis = timeMillis }
    calendar.set(Calendar.HOUR_OF_DAY, 23)
    calendar.set(Calendar.MINUTE, 59)
    calendar.set(Calendar.SECOND, 59)
    calendar.set(Calendar.MILLISECOND, 999)
    return calendar.timeInMillis
}

fun PlannerTask.isDueThisWeek(): Boolean {
    val days = weekDays()
    return dueDateMillis in startOfDay(days.first())..endOfDay(days.last())
}

fun tasksForDay(tasks: List<PlannerTask>, dayMillis: Long): List<PlannerTask> {
    val start = startOfDay(dayMillis)
    val end = endOfDay(dayMillis)
    return tasks.filter { it.dueDateMillis in start..end }.sortedBy { it.dueDateMillis }
}

fun dayName(timeMillis: Long): String = SimpleDateFormat("E", Locale.US).format(Date(timeMillis))

fun dayNumber(timeMillis: Long): String = SimpleDateFormat("d", Locale.US).format(Date(timeMillis))

fun demoTasks(): List<PlannerTask> {
    val days = weekDays()
    return listOf(
        PlannerTask("demo-1", "Reading Assignment #4", TaskType.READING_ASSIGNMENT, days[1], "ENG 1301"),
        PlannerTask("demo-2", "Phase II Report", TaskType.REPORT, days[3], "COSC 6355", reminderDateMillis = days[1]),
        PlannerTask("demo-3", "Discussion Post", TaskType.DISCUSSION_POST, days[4], "HIST 2321"),
        PlannerTask("demo-4", "Discussion Replies", TaskType.DISCUSSION_REPLIES, days[5], "HIST 2321")
    )
}

private fun setToStartOfDay(calendar: Calendar) {
    calendar.set(Calendar.HOUR_OF_DAY, 0)
    calendar.set(Calendar.MINUTE, 0)
    calendar.set(Calendar.SECOND, 0)
    calendar.set(Calendar.MILLISECOND, 0)
}

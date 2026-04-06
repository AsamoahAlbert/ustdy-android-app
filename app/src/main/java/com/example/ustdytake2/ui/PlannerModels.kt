package com.example.ustdytake2.ui

import androidx.compose.ui.graphics.Color
import com.example.ustdytake2.ui.theme.Coral500
import com.example.ustdytake2.ui.theme.Gold400
import com.example.ustdytake2.ui.theme.Ink700
import com.example.ustdytake2.ui.theme.Sage500
import com.example.ustdytake2.ui.theme.Sky500

data class PlannerTask(
    val id: String,
    val title: String,
    val type: TaskType,
    val dueDateMillis: Long,
    val className: String,
    val reminderDateMillis: Long? = null,
    val completed: Boolean = false
)

data class ReminderPreferences(
    val quizzes: ReminderLead = ReminderLead.THREE_DAYS,
    val exams: ReminderLead = ReminderLead.SEVEN_DAYS,
    val weeklyAssignments: ReminderLead = ReminderLead.THREE_DAYS,
    val largeProjects: ReminderLead = ReminderLead.TWO_WEEKS
)

data class OnboardingResult(
    val classCount: Int,
    val reminderPreferences: ReminderPreferences,
    val tasks: List<PlannerTask>
)

data class DeliverableDraft(
    val id: String,
    val title: String = "",
    val type: TaskType = TaskType.WEEKLY_ASSIGNMENT,
    val dueDateText: String = "",
    val classNumber: Int = 1
)

enum class ReminderLead(val label: String) {
    ONE_DAY("1 day"),
    THREE_DAYS("3 days"),
    SEVEN_DAYS("7 days"),
    TWO_WEEKS("2 weeks")
}

enum class TaskType(val label: String, val color: Color) {
    READING_ASSIGNMENT("Reading Assignment", Sky500),
    WEEKLY_ASSIGNMENT("Weekly Assignment", Sky500),
    QUIZ("Quiz", Gold400),
    EXAM("Exam", Coral500),
    LARGE_PROJECT("Large Project", Sage500),
    DISCUSSION_POST("Discussion Post", Coral500),
    DISCUSSION_REPLIES("Discussion Replies", Gold400),
    REPORT("Phase II Report", Sage500),
    CUSTOM_REMINDER("Custom Reminder", Ink700)
}

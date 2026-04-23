package com.example.ustdytake2.ui

import androidx.compose.ui.graphics.Color
import com.example.ustdytake2.model.TaskItem
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
    val completed: Boolean = false,
    val completedAtMillis: Long = 0L,
    val classId: String? = null,
    val isRemote: Boolean = false
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

fun TaskItem.toPlannerTask(classId: String?, className: String): PlannerTask {
    return PlannerTask(
        id = id,
        title = title,
        type = taskTypeFromBackend(type),
        dueDateMillis = dueDate,
        className = className,
        reminderDateMillis = reminderDate.takeIf { it > 0L },
        completed = completed,
        completedAtMillis = completedAt,
        classId = classId,
        isRemote = true
    )
}

fun PlannerTask.toTaskItem(): TaskItem {
    return TaskItem(
        id = id,
        title = title,
        type = type.label,
        dueDate = dueDateMillis,
        completed = completed,
        reminderDate = reminderDateMillis ?: 0L,
        completedAt = completedAtMillis
    )
}

private fun taskTypeFromBackend(value: String): TaskType {
    return TaskType.values().firstOrNull { type ->
        type.label.equals(value, ignoreCase = true)
    } ?: when {
        value.contains("read", ignoreCase = true) -> TaskType.READING_ASSIGNMENT
        value.contains("quiz", ignoreCase = true) -> TaskType.QUIZ
        value.contains("exam", ignoreCase = true) -> TaskType.EXAM
        value.contains("project", ignoreCase = true) -> TaskType.LARGE_PROJECT
        value.contains("report", ignoreCase = true) -> TaskType.REPORT
        value.contains("discussion", ignoreCase = true) && value.contains("reply", ignoreCase = true) ->
            TaskType.DISCUSSION_REPLIES
        value.contains("discussion", ignoreCase = true) -> TaskType.DISCUSSION_POST
        else -> TaskType.WEEKLY_ASSIGNMENT
    }
}

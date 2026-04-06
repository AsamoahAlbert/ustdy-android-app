package com.example.ustdytake2.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.ustdytake2.ui.theme.UstdyTake2Theme
import java.util.UUID

private enum class AppDestination {
    AUTH,
    ONBOARDING,
    DASHBOARD,
    EDITOR
}

@Composable
fun UstdyPlannerApp(modifier: Modifier = Modifier) {
    var destination by remember { mutableStateOf(AppDestination.AUTH) }
    var displayName by remember { mutableStateOf("Student") }
    var reminderPreferences by remember { mutableStateOf(ReminderPreferences()) }
    var plannerTasks by remember { mutableStateOf<List<PlannerTask>>(emptyList()) }
    var taskBeingEdited by remember { mutableStateOf<PlannerTask?>(null) }

    when (destination) {
        AppDestination.AUTH -> {
            LoginScreen(
                onLogin = { identifier, _ ->
                    displayName = identifier.substringBefore("@").replaceFirstChar { it.uppercase() }
                    plannerTasks = emptyList()
                    destination = AppDestination.ONBOARDING
                },
                onCreateAccount = { identifier, _ ->
                    displayName = identifier.substringBefore("@").replaceFirstChar { it.uppercase() }
                    plannerTasks = emptyList()
                    destination = AppDestination.ONBOARDING
                },
                onContinueAsDemo = {
                    displayName = "Demo User"
                    reminderPreferences = ReminderPreferences()
                    plannerTasks = demoTasks()
                    destination = AppDestination.DASHBOARD
                },
                modifier = modifier
            )
        }

        AppDestination.ONBOARDING -> {
            OnboardingWizardScreen(
                onBackToLogin = { destination = AppDestination.AUTH },
                onComplete = { result ->
                    reminderPreferences = result.reminderPreferences
                    plannerTasks = if (result.tasks.isEmpty()) demoTasks() else result.tasks
                    destination = AppDestination.DASHBOARD
                },
                modifier = modifier
            )
        }

        AppDestination.DASHBOARD -> {
            UpcomingAssignmentsScreen(
                userName = displayName,
                reminderPreferences = reminderPreferences,
                tasks = plannerTasks,
                onToggleTask = { taskId, completed ->
                    plannerTasks = plannerTasks.map { task ->
                        if (task.id == taskId) task.copy(completed = completed) else task
                    }
                },
                onAddTask = {
                    taskBeingEdited = null
                    destination = AppDestination.EDITOR
                },
                onEditTask = { task ->
                    taskBeingEdited = task
                    destination = AppDestination.EDITOR
                },
                onDeleteTask = { task ->
                    plannerTasks = plannerTasks.filterNot { it.id == task.id }
                },
                onRestart = { destination = AppDestination.AUTH },
                modifier = modifier
            )
        }

        AppDestination.EDITOR -> {
            AddAssignmentScreen(
                taskToEdit = taskBeingEdited,
                onSave = { savedTask ->
                    plannerTasks = if (taskBeingEdited == null) {
                        plannerTasks + savedTask.copy(id = UUID.randomUUID().toString())
                    } else {
                        plannerTasks.map { task ->
                            if (task.id == savedTask.id) savedTask else task
                        }
                    }
                    destination = AppDestination.DASHBOARD
                },
                onCancel = { destination = AppDestination.DASHBOARD },
                onDelete = { task ->
                    plannerTasks = plannerTasks.filterNot { it.id == task.id }
                    destination = AppDestination.DASHBOARD
                },
                modifier = modifier
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun UstdyPlannerAppPreview() {
    UstdyTake2Theme {
        UstdyPlannerApp()
    }
}

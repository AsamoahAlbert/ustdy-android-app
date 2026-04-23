package com.example.ustdytake2.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.example.ustdytake2.model.ClassItem
import com.example.ustdytake2.viewmodel.AuthState
import com.example.ustdytake2.viewmodel.AuthViewModel
import com.example.ustdytake2.viewmodel.ClassViewModel
import com.example.ustdytake2.viewmodel.GamificationViewModel
import com.example.ustdytake2.viewmodel.TaskViewModel
import java.util.UUID

private enum class AppDestination {
    AUTH,
    ONBOARDING,
    DASHBOARD,
    EDITOR
}

@Composable
fun UstdyPlannerApp(
    authViewModel: AuthViewModel,
    classViewModel: ClassViewModel,
    taskViewModel: TaskViewModel,
    gamificationViewModel: GamificationViewModel,
    modifier: Modifier = Modifier
) {
    val authState by authViewModel.authState.collectAsState()
    val classes by classViewModel.classes.collectAsState()
    val backendTasks by taskViewModel.tasks.collectAsState()
    val gamificationData by gamificationViewModel.gamification.collectAsState()

    var destination by remember { mutableStateOf(AppDestination.AUTH) }
    var displayName by remember { mutableStateOf("Student") }
    var reminderPreferences by remember { mutableStateOf(ReminderPreferences()) }
    var localTasks by remember { mutableStateOf<List<PlannerTask>>(emptyList()) }
    var taskBeingEdited by remember { mutableStateOf<PlannerTask?>(null) }
    var isDemoMode by remember { mutableStateOf(false) }
    var currentUserId by remember { mutableStateOf<String?>(null) }
    var selectedClass by remember { mutableStateOf<ClassItem?>(null) }

    LaunchedEffect(authState) {
        when (val state = authState) {
            is AuthState.Success -> {
                currentUserId = state.userId
                isDemoMode = false
                classViewModel.loadClasses(state.userId)
                gamificationViewModel.loadGamification(state.userId)
                destination = AppDestination.ONBOARDING
            }

            AuthState.Idle -> {
                currentUserId = null
                selectedClass = null
            }

            else -> Unit
        }
    }

    LaunchedEffect(classes, currentUserId) {
        if (!isDemoMode && currentUserId != null && classes.isNotEmpty()) {
            val classToUse = selectedClass?.takeIf { existing ->
                classes.any { it.id == existing.id }
            } ?: classes.first()
            selectedClass = classToUse
            taskViewModel.loadTasks(currentUserId.orEmpty(), classToUse.id)
        }
    }

    val remotePlannerTasks = remember(backendTasks, selectedClass) {
        backendTasks.map { task ->
            task.toPlannerTask(
                classId = selectedClass?.id,
                className = selectedClass?.name.orEmpty().ifBlank { "Class" }
            )
        }
    }
    val plannerTasks = if (isDemoMode) localTasks else remotePlannerTasks + localTasks

    when (destination) {
        AppDestination.AUTH -> {
            LoginScreen(
                authState = authState,
                onLogin = { identifier, password ->
                    displayName = identifier.substringBefore("@").replaceFirstChar { it.uppercase() }
                    localTasks = emptyList()
                    authViewModel.signIn(identifier.trim(), password)
                },
                onCreateAccount = { identifier, password ->
                    displayName = identifier.substringBefore("@").replaceFirstChar { it.uppercase() }
                    localTasks = emptyList()
                    authViewModel.signUp(identifier.trim(), password)
                },
                onContinueAsDemo = {
                    isDemoMode = true
                    displayName = "Demo User"
                    reminderPreferences = ReminderPreferences()
                    localTasks = demoTasks()
                    destination = AppDestination.DASHBOARD
                },
                modifier = modifier
            )
        }

        AppDestination.ONBOARDING -> {
            OnboardingWizardScreen(
                onBackToLogin = {
                    authViewModel.signOut()
                    destination = AppDestination.AUTH
                },
                onComplete = { result ->
                    reminderPreferences = result.reminderPreferences
                    if (isDemoMode) {
                        localTasks = if (result.tasks.isEmpty()) demoTasks() else result.tasks
                    } else {
                        val userId = currentUserId
                        val classId = selectedClass?.id
                        if (userId != null && classId != null) {
                            localTasks = emptyList()
                            result.tasks.forEach { task ->
                                taskViewModel.addTask(userId, classId, task.toTaskItem())
                            }
                        } else {
                            localTasks = result.tasks
                        }
                    }
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
                streak = gamificationData.streak,
                badges = gamificationData.badges,
                onToggleTask = { task, completed ->
                    if (isDemoMode || currentUserId == null || task.classId == null || !task.isRemote) {
                        localTasks = localTasks.map { existing ->
                            if (existing.id == task.id) {
                                existing.copy(
                                    completed = completed,
                                    completedAtMillis = if (completed) System.currentTimeMillis() else 0L
                                )
                            } else {
                                existing
                            }
                        }
                    } else {
                        taskViewModel.markTaskComplete(
                            currentUserId.orEmpty(),
                            task.classId,
                            task.toTaskItem(),
                            completed
                        )
                        gamificationViewModel.loadGamification(currentUserId.orEmpty())
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
                    if (isDemoMode || currentUserId == null || task.classId == null || !task.isRemote) {
                        localTasks = localTasks.filterNot { it.id == task.id }
                    } else {
                        taskViewModel.deleteTask(currentUserId.orEmpty(), task.classId, task.id)
                    }
                },
                onRestart = {
                    authViewModel.signOut()
                    isDemoMode = false
                    localTasks = emptyList()
                    destination = AppDestination.AUTH
                },
                modifier = modifier
            )
        }

        AppDestination.EDITOR -> {
            AddAssignmentScreen(
                taskToEdit = taskBeingEdited,
                onSave = { savedTask ->
                    if (isDemoMode || currentUserId == null || selectedClass?.id == null) {
                        localTasks = if (taskBeingEdited == null) {
                            localTasks + savedTask.copy(id = UUID.randomUUID().toString())
                        } else {
                            localTasks.map { task ->
                                if (task.id == savedTask.id) savedTask else task
                            }
                        }
                    } else {
                        val classId = (savedTask.classId ?: selectedClass?.id).orEmpty()
                        val remoteTask = savedTask.copy(classId = classId, isRemote = true)
                        if (taskBeingEdited?.isRemote == true) {
                            taskViewModel.updateTask(currentUserId.orEmpty(), classId, remoteTask.toTaskItem())
                        } else {
                            taskViewModel.addTask(currentUserId.orEmpty(), classId, remoteTask.toTaskItem())
                        }
                    }
                    destination = AppDestination.DASHBOARD
                },
                onCancel = { destination = AppDestination.DASHBOARD },
                onDelete = { task ->
                    if (isDemoMode || currentUserId == null || task.classId == null || !task.isRemote) {
                        localTasks = localTasks.filterNot { it.id == task.id }
                    } else {
                        taskViewModel.deleteTask(currentUserId.orEmpty(), task.classId, task.id)
                    }
                    destination = AppDestination.DASHBOARD
                },
                modifier = modifier
            )
        }
    }
}

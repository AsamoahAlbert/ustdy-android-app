package com.example.ustdytake2.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.ustdytake2.ui.theme.Cream50
import com.example.ustdytake2.ui.theme.Slate100
import com.example.ustdytake2.ui.theme.UstdyTake2Theme
import java.util.UUID

private enum class WizardStep {
    CLASS_COUNT,
    REMINDERS,
    DELIVERABLES,
    EXTRA_ITEM
}

@Composable
fun OnboardingWizardScreen(
    onBackToLogin: () -> Unit,
    onComplete: (OnboardingResult) -> Unit,
    modifier: Modifier = Modifier
) {
    var step by remember { mutableStateOf(WizardStep.CLASS_COUNT) }
    var classCount by remember { mutableStateOf(4) }
    var reminderPreferences by remember { mutableStateOf(ReminderPreferences()) }
    var currentClassNumber by remember { mutableStateOf(1) }
    var drafts by remember {
        mutableStateOf(listOf(DeliverableDraft(id = UUID.randomUUID().toString(), classNumber = 1)))
    }
    var customReminderName by remember { mutableStateOf("") }
    var customReminderDateText by remember { mutableStateOf("") }
    var infoMessage by remember { mutableStateOf("We'll turn this into your first planning workspace.") }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF8F4ED))
            .verticalScroll(rememberScrollState())
            .padding(20.dp)
    ) {
        Text(
            text = "First-time setup",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Let's shape U-stdy around your semester.",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(18.dp))
        LinearProgressIndicator(
            progress = { (step.ordinal + 1) / 4f },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(10.dp))
        Text(
            text = when (step) {
                WizardStep.CLASS_COUNT -> "Step 1 of 4: Semester load"
                WizardStep.REMINDERS -> "Step 2 of 4: Reminder preferences"
                WizardStep.DELIVERABLES -> "Step 3 of 4: Course deliverables"
                WizardStep.EXTRA_ITEM -> "Step 4 of 4: Custom reminder"
            },
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(20.dp))

        when (step) {
            WizardStep.CLASS_COUNT -> {
                WizardCard(title = "How many classes can we help you keep track of this semester?") {
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        Button(onClick = { if (classCount > 1) classCount -= 1 }) { Text("-") }
                        Box(
                            modifier = Modifier
                                .background(Slate100, RoundedCornerShape(18.dp))
                                .padding(horizontal = 24.dp, vertical = 14.dp)
                        ) {
                            Text(
                                text = classCount.toString(),
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Button(onClick = { if (classCount < 8) classCount += 1 }) { Text("+") }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(onClick = { step = WizardStep.REMINDERS }) {
                        Text("Continue")
                    }
                }
            }

            WizardStep.REMINDERS -> {
                WizardCard(title = "How far in advance do you want to be reminded of:") {
                    ReminderPreferenceEditor(
                        label = "Quizzes",
                        selected = reminderPreferences.quizzes,
                        onSelect = { reminderPreferences = reminderPreferences.copy(quizzes = it) }
                    )
                    ReminderPreferenceEditor(
                        label = "Exams",
                        selected = reminderPreferences.exams,
                        onSelect = { reminderPreferences = reminderPreferences.copy(exams = it) }
                    )
                    ReminderPreferenceEditor(
                        label = "Weekly Assignments",
                        selected = reminderPreferences.weeklyAssignments,
                        onSelect = { reminderPreferences = reminderPreferences.copy(weeklyAssignments = it) }
                    )
                    ReminderPreferenceEditor(
                        label = "Large Projects",
                        selected = reminderPreferences.largeProjects,
                        onSelect = { reminderPreferences = reminderPreferences.copy(largeProjects = it) }
                    )
                    Spacer(modifier = Modifier.height(18.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        TextButton(onClick = { step = WizardStep.CLASS_COUNT }) { Text("Back") }
                        Button(onClick = { step = WizardStep.DELIVERABLES }) { Text("Continue") }
                    }
                }
            }

            WizardStep.DELIVERABLES -> {
                val visibleDrafts = drafts.filter { it.classNumber == currentClassNumber }
                WizardCard(title = "Enter all exams/quizzes/assignments/projects for class $currentClassNumber:") {
                    Text(
                        text = "Add rows for anything already on the syllabus. You can keep it lightweight and edit later.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                    visibleDrafts.forEach { draft ->
                        DeliverableEditorRow(
                            draft = draft,
                            onUpdate = { updated ->
                                drafts = drafts.map { if (it.id == updated.id) updated else it }
                            },
                            onRemove = {
                                drafts = if (drafts.size == 1) drafts else drafts.filterNot { it.id == draft.id }
                            }
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                    }
                    TextButton(onClick = {
                        drafts = drafts + DeliverableDraft(
                            id = UUID.randomUUID().toString(),
                            classNumber = currentClassNumber
                        )
                    }) {
                        Text("Add another deliverable")
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        TextButton(onClick = {
                            if (currentClassNumber > 1) currentClassNumber -= 1 else step = WizardStep.REMINDERS
                        }) {
                            Text("Back")
                        }
                        Button(onClick = {
                            val hasInvalidDate = visibleDrafts.any {
                                it.title.isNotBlank() && it.dueDateText.isNotBlank() && parseDate(it.dueDateText) == null
                            }
                            if (hasInvalidDate) {
                                infoMessage = "One or more due dates are invalid. Use MM/DD/YYYY."
                            } else if (currentClassNumber < classCount) {
                                currentClassNumber += 1
                                if (drafts.none { it.classNumber == currentClassNumber }) {
                                    drafts = drafts + DeliverableDraft(
                                        id = UUID.randomUUID().toString(),
                                        classNumber = currentClassNumber
                                    )
                                }
                            } else {
                                step = WizardStep.EXTRA_ITEM
                            }
                        }) {
                            Text(if (currentClassNumber == classCount) "Continue" else "Next Class")
                        }
                    }
                }
            }

            WizardStep.EXTRA_ITEM -> {
                WizardCard(title = "Add a custom reminder or task before you finish") {
                    Text(
                        text = "Optional but handy for office hours, study sessions, or one-off reminders.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    OutlinedTextField(
                        value = customReminderName,
                        onValueChange = { customReminderName = it },
                        label = { Text("Name") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = customReminderDateText,
                        onValueChange = { customReminderDateText = it },
                        label = { Text("Reminder Date (MM/DD/YYYY)") },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        Button(onClick = {
                            infoMessage = when {
                                customReminderName.isBlank() || parseDate(customReminderDateText) == null ->
                                    "Enter a name and a valid date before saving the reminder."

                                else -> "Reminder saved. You can still edit it later from the dashboard."
                            }
                        }) { Text("Save") }
                        TextButton(onClick = {
                            customReminderName = ""
                            customReminderDateText = ""
                            infoMessage = "Custom reminder removed from setup."
                        }) { Text("Delete") }
                        TextButton(onClick = {
                            customReminderName = ""
                            customReminderDateText = ""
                            infoMessage = "Skipping the optional reminder is okay."
                        }) { Text("Cancel") }
                    }
                    Spacer(modifier = Modifier.height(18.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        TextButton(onClick = { step = WizardStep.DELIVERABLES }) { Text("Back") }
                        Button(onClick = {
                            val tasks = drafts.mapNotNull { draft ->
                                if (draft.title.isBlank() || draft.dueDateText.isBlank()) return@mapNotNull null
                                val dueDate = parseDate(draft.dueDateText) ?: return@mapNotNull null
                                PlannerTask(
                                    id = draft.id,
                                    title = draft.title,
                                    type = draft.type,
                                    dueDateMillis = dueDate,
                                    className = "Class ${draft.classNumber}"
                                )
                            }.toMutableList()
                            val reminderDate = parseDate(customReminderDateText)
                            if (customReminderName.isNotBlank() && reminderDate != null) {
                                tasks += PlannerTask(
                                    id = UUID.randomUUID().toString(),
                                    title = customReminderName,
                                    type = TaskType.CUSTOM_REMINDER,
                                    dueDateMillis = reminderDate,
                                    className = "Personal",
                                    reminderDateMillis = reminderDate
                                )
                            }
                            onComplete(
                                OnboardingResult(
                                    classCount = classCount,
                                    reminderPreferences = reminderPreferences,
                                    tasks = tasks
                                )
                            )
                        }) {
                            Text("Finish Setup")
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        Card(
            shape = RoundedCornerShape(22.dp),
            colors = CardDefaults.cardColors(containerColor = Cream50)
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Text(
                    text = "Setup notes",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(text = infoMessage, style = MaterialTheme.typography.bodyMedium)
                Spacer(modifier = Modifier.height(8.dp))
                TextButton(onClick = onBackToLogin) {
                    Text("Return to login")
                }
            }
        }
    }
}

@Composable
private fun WizardCard(
    title: String,
    content: @Composable () -> Unit
) {
    Card(
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = Cream50)
    ) {
        Column(modifier = Modifier.padding(22.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(18.dp))
            content()
        }
    }
}

@Composable
private fun ReminderPreferenceEditor(
    label: String,
    selected: ReminderLead,
    onSelect: (ReminderLead) -> Unit
) {
    Text(text = label, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Medium)
    Spacer(modifier = Modifier.height(8.dp))
    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        items(ReminderLead.values()) { option ->
            FilterChip(
                selected = selected == option,
                onClick = { onSelect(option) },
                label = { Text(option.label) }
            )
        }
    }
    Spacer(modifier = Modifier.height(14.dp))
}

@Composable
private fun DeliverableEditorRow(
    draft: DeliverableDraft,
    onUpdate: (DeliverableDraft) -> Unit,
    onRemove: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Slate100),
        shape = RoundedCornerShape(22.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            OutlinedTextField(
                value = draft.title,
                onValueChange = { onUpdate(draft.copy(title = it)) },
                label = { Text("Title") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(10.dp))
            Text(text = "Type", style = MaterialTheme.typography.labelLarge)
            Spacer(modifier = Modifier.height(8.dp))
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(
                    listOf(
                        TaskType.QUIZ,
                        TaskType.EXAM,
                        TaskType.WEEKLY_ASSIGNMENT,
                        TaskType.LARGE_PROJECT
                    )
                ) { option ->
                    FilterChip(
                        selected = draft.type == option,
                        onClick = { onUpdate(draft.copy(type = option)) },
                        label = { Text(option.label) }
                    )
                }
            }
            Spacer(modifier = Modifier.height(10.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = draft.dueDateText,
                    onValueChange = { onUpdate(draft.copy(dueDateText = it)) },
                    label = { Text("Due date") },
                    modifier = Modifier.weight(1f),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
                OutlinedTextField(
                    value = "Class ${draft.classNumber}",
                    onValueChange = {},
                    enabled = false,
                    label = { Text("Class") },
                    modifier = Modifier.width(120.dp)
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
            TextButton(onClick = onRemove) {
                Text("Remove item")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun OnboardingWizardScreenPreview() {
    UstdyTake2Theme {
        OnboardingWizardScreen(
            onBackToLogin = {},
            onComplete = {}
        )
    }
}

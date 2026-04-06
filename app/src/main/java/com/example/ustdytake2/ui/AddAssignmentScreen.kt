package com.example.ustdytake2.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
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

@Composable
fun AddAssignmentScreen(
    taskToEdit: PlannerTask?,
    onSave: (PlannerTask) -> Unit,
    onCancel: () -> Unit,
    onDelete: (PlannerTask) -> Unit,
    modifier: Modifier = Modifier
) {
    var title by rememberSaveable { mutableStateOf(taskToEdit?.title.orEmpty()) }
    var className by rememberSaveable { mutableStateOf(taskToEdit?.className.orEmpty()) }
    var dueDateText by rememberSaveable {
        mutableStateOf(taskToEdit?.dueDateMillis?.let(::formatEditorDate).orEmpty())
    }
    var reminderDateText by rememberSaveable {
        mutableStateOf(taskToEdit?.reminderDateMillis?.let(::formatEditorDate).orEmpty())
    }
    var selectedType by remember { mutableStateOf(taskToEdit?.type ?: TaskType.WEEKLY_ASSIGNMENT) }
    var errorMessage by rememberSaveable { mutableStateOf<String?>(null) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF7F2EA))
            .verticalScroll(rememberScrollState())
            .padding(20.dp)
    ) {
        Text(
            text = if (taskToEdit == null) "Add Assignment or Reminder" else "Edit Item",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(10.dp))
        Text(
            text = "Use this screen to add or update anything you want on the week view.",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(20.dp))
        Card(
            colors = CardDefaults.cardColors(containerColor = Cream50),
            shape = RoundedCornerShape(28.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                OutlinedTextField(
                    value = title,
                    onValueChange = {
                        title = it
                        errorMessage = null
                    },
                    label = { Text("Name") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedTextField(
                    value = className,
                    onValueChange = {
                        className = it
                        errorMessage = null
                    },
                    label = { Text("Course") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(14.dp))
                Text(text = "Type", fontWeight = FontWeight.SemiBold)
                Spacer(modifier = Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf(
                        TaskType.READING_ASSIGNMENT,
                        TaskType.REPORT,
                        TaskType.DISCUSSION_POST,
                        TaskType.DISCUSSION_REPLIES
                    ).forEach { option ->
                        FilterChip(
                            selected = selectedType == option,
                            onClick = { selectedType = option },
                            label = { Text(option.label) }
                        )
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = dueDateText,
                        onValueChange = {
                            dueDateText = it
                            errorMessage = null
                        },
                        label = { Text("Due date") },
                        modifier = Modifier.weight(1f),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                    OutlinedTextField(
                        value = reminderDateText,
                        onValueChange = {
                            reminderDateText = it
                            errorMessage = null
                        },
                        label = { Text("Reminder date") },
                        modifier = Modifier.weight(1f),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
                Card(
                    colors = CardDefaults.cardColors(containerColor = Slate100),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Text(
                        text = "Dates use MM/DD/YYYY. Leave the reminder blank if you only need a due date.",
                        modifier = Modifier.padding(14.dp),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
                if (errorMessage != null) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = errorMessage.orEmpty(),
                        color = MaterialTheme.colorScheme.error
                    )
                }
                Spacer(modifier = Modifier.height(18.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Button(
                        onClick = {
                            val dueDate = parseDate(dueDateText)
                            val reminderDate = reminderDateText.takeIf { it.isNotBlank() }?.let(::parseDate)
                            when {
                                title.isBlank() -> errorMessage = "Enter a task name."
                                className.isBlank() -> errorMessage = "Enter a course tag."
                                dueDate == null -> errorMessage = "Enter a valid due date in MM/DD/YYYY."
                                reminderDateText.isNotBlank() && reminderDate == null ->
                                    errorMessage = "Enter a valid reminder date or leave it blank."

                                else -> {
                                    onSave(
                                        PlannerTask(
                                            id = taskToEdit?.id ?: UUID.randomUUID().toString(),
                                            title = title.trim(),
                                            type = selectedType,
                                            dueDateMillis = dueDate,
                                            className = className.trim(),
                                            reminderDateMillis = reminderDate,
                                            completed = taskToEdit?.completed ?: false
                                        )
                                    )
                                }
                            }
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Save")
                    }
                    TextButton(onClick = onCancel, modifier = Modifier.weight(1f)) {
                        Text("Cancel")
                    }
                }
                if (taskToEdit != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    TextButton(onClick = { onDelete(taskToEdit) }) {
                        Text("Delete Item")
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun AddAssignmentScreenPreview() {
    UstdyTake2Theme {
        AddAssignmentScreen(
            taskToEdit = PlannerTask(
                id = "preview",
                title = "Discussion Post",
                type = TaskType.DISCUSSION_POST,
                dueDateMillis = weekDays()[2],
                className = "ENG 1301"
            ),
            onSave = {},
            onCancel = {},
            onDelete = {}
        )
    }
}

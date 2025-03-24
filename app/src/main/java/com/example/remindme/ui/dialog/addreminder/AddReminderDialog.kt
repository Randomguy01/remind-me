package com.example.remindme.ui.dialog.addreminder

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import android.content.res.Configuration.UI_MODE_TYPE_NORMAL
import androidx.annotation.PluralsRes
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.remindme.R
import com.example.remindme.domain.model.Reminder
import com.example.remindme.ui.theme.RemindMeTheme
import kotlinx.coroutines.launch
import kotlinx.datetime.DateTimeUnit
import kotlinx.serialization.Serializable
import kotlin.enums.enumEntries

@Serializable
object AddReminder

/**
 * The [AddReminder] dialog. Allows the user to create a new [Reminder].
 */
@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun AddReminderDialog(
    viewModel: AddReminderViewModel = hiltViewModel(),
    onDismiss: () -> Unit = {},
) {
    // Listen to UI state
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val coroutineScope = rememberCoroutineScope()

    // Material Surface
    Surface(modifier = Modifier.fillMaxSize()) {
        Column {
            // Header
            AddReminderDialogHeader(lockUi = uiState.lockUi, onDismiss = onDismiss, onAdd = {
                coroutineScope.launch {
                    viewModel.lockUi(true)

                    // Validate fields
                    if (viewModel.validate()) {
                        // Create reminder
                        if (viewModel.createReminder()) {
                            // Dismiss dialog
                            onDismiss()
                        }
                    }
                    viewModel.lockUi(false)
                }
            })
            // Content
            AddReminderDialogContent(
                title = viewModel.title,
                isTitleValid = !uiState.showTitleError,
                description = viewModel.description,
                reminderTime = uiState.selectedReminderTime,
                isReminderTimeValid = !uiState.showReminderTimeError,
                lockUi = uiState.lockUi,
                onTitleChanged = viewModel::updateTitle,
                onDescriptionChanged = viewModel::updateDescription,
                onReminderTimeChanged = viewModel::updateReminderTime,
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}

/**
 * The header of the [AddReminderDialog].
 */
@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun AddReminderDialogHeader(
    lockUi: Boolean,
    modifier: Modifier = Modifier,
    onDismiss: () -> Unit = {},
    onAdd: () -> Unit = {},
) {
    TopAppBar(
        navigationIcon = {
            IconButton(onClick = onDismiss) {
                Icon(Icons.Filled.Close, Icons.Filled.Close.name)
            }
        },
        title = {
            Text(
                text = stringResource(R.string.addReminder_header_title),
                style = MaterialTheme.typography.titleLarge,
            )
        },
        actions = {
            TextButton(enabled = !lockUi, onClick = onAdd) {
                Text(
                    text = stringResource(R.string.add),
                    style = MaterialTheme.typography.labelLarge,
                )
            }
        },
        modifier = modifier
    )
}

@PreviewLightDark
@Composable
private fun AddReminderDialogHeaderPreview() {
    RemindMeTheme {
        AddReminderDialogHeader(false)
    }
}

/**
 * The [ReminderTime] options for the [AddReminderDialog].
 */
enum class ReminderTime(val count: Int, val unit: DateTimeUnit, @PluralsRes val unitRes: Int) {
    ONE_MINUTE(1, DateTimeUnit.MINUTE, R.plurals.unit_minute),
    FIVE_MINUTES(5, DateTimeUnit.MINUTE, R.plurals.unit_minute),
    TEN_MINUTES(10, DateTimeUnit.MINUTE, R.plurals.unit_minute),
    ONE_HOUR(1, DateTimeUnit.HOUR, R.plurals.unit_hour)
}

/**
 * The content of the [AddReminderDialog]. Contains [OutlinedTextField]s for the title and
 * description.
 */
@Composable
private fun AddReminderDialogContent(
    title: String,
    isTitleValid: Boolean,
    description: String,
    reminderTime: ReminderTime?,
    lockUi: Boolean,
    isReminderTimeValid: Boolean,
    modifier: Modifier = Modifier,
    onTitleChanged: (String) -> Unit = {},
    onDescriptionChanged: (String) -> Unit = {},
    onReminderTimeChanged: (ReminderTime) -> Unit = {},
) {
    Column(modifier = modifier.padding(horizontal = 24.dp)) {
        // Title TextField
        OutlinedTextField(
            enabled = !lockUi,
            value = title,
            isError = !isTitleValid,
            label = { Text(stringResource(R.string.addReminder_label_title)) },
            supportingText = { Text(stringResource(R.string.addReminder_supportingText_title)) },
            singleLine = true,
            onValueChange = onTitleChanged,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        // Description TextField
        OutlinedTextField(
            enabled = !lockUi,
            value = description,
            label = { Text(stringResource(R.string.addReminder_label_description)) },
            singleLine = true,
            onValueChange = onDescriptionChanged,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(20.dp))
        ReminderPicker(
            options = enumEntries<ReminderTime>(),
            selectedOption = reminderTime,
            showReminderTimeError = !isReminderTimeValid,
            lockUi = lockUi,
            onOptionSelected = onReminderTimeChanged,
        )
    }
}

/**
 * The [ReminderTime] picker. Creates a list of [RadioButton]s with the [options].
 *
 * @param options The [ReminderTime] options to display as [RadioButton]s.
 * @param selectedOption The selected [ReminderTime].
 * @param showReminderTimeError Whether to show an error message for the selected [ReminderTime].
 * @param onOptionSelected Called when a [RadioButton] is clicked.
 */
@Composable
fun ReminderPicker(
    options: List<ReminderTime>,
    selectedOption: ReminderTime?,
    showReminderTimeError: Boolean,
    lockUi: Boolean,
    onOptionSelected: (ReminderTime) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier.selectableGroup()) {
        // Title
        Text(
            text = stringResource(R.string.addReminder_text_reminderTime),
            style = MaterialTheme.typography.titleLarge,
            color = if (showReminderTimeError) MaterialTheme.colorScheme.error else Color.Unspecified
        )
        options.forEach { option ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .selectable(
                        selected = option == selectedOption,
                        role = Role.RadioButton,
                        onClick = {
                            if (!lockUi) {
                                onOptionSelected(option)
                            }
                        },
                    )
                    .padding(horizontal = 16.dp),
            ) {
                // Radio button
                RadioButton(
                    selected = option == selectedOption,
                    onClick = null,
                )
                // Name of option
                Text(
                    text = pluralStringResource(option.unitRes, option.count, option.count),
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(start = 16.dp)
                )
            }
        }
    }
}

@Preview(name = "Light", showBackground = true)
@Preview(name = "Dark", uiMode = UI_MODE_NIGHT_YES or UI_MODE_TYPE_NORMAL)
@Composable
private fun ReminderPickerPreview() {
    RemindMeTheme {
        ReminderPicker(
            options = enumEntries<ReminderTime>(),
            selectedOption = ReminderTime.ONE_MINUTE,
            showReminderTimeError = false,
            lockUi = false,
            onOptionSelected = {}
        )
    }
}

@Preview(name = "Light", showBackground = true)
@Preview(name = "Dark", uiMode = UI_MODE_NIGHT_YES or UI_MODE_TYPE_NORMAL)
@Composable
private fun AddReminderDialogContentPreview() {
    RemindMeTheme {
        AddReminderDialogContent(
            title = "",
            isTitleValid = false,
            description = "",
            reminderTime = ReminderTime.ONE_MINUTE,
            lockUi = false,
            isReminderTimeValid = false,
        )
    }
}



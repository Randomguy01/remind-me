package com.example.remindme.ui.screen.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.example.remindme.domain.model.Reminder
import com.example.remindme.ui.theme.RemindMeTheme
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

/**
 * Displays a list of [ReminderCard]s.
 *
 * @param reminders The list of [Reminder]s to display as [ReminderCard]s.
 * @param onReminderClicked Called when a [ReminderCard] is clicked.
 * @param onReminderDeleted Called when a [ReminderCard] is deleted.
 */
@Composable
fun ReminderList(
    reminders: List<Reminder>,
    modifier: Modifier = Modifier,
    onReminderClicked: (Reminder) -> Unit = {},
    onReminderDeleted: (Reminder) -> Unit = {},
) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(4.dp),
        contentPadding = PaddingValues(horizontal = 8.dp),
        modifier = modifier
    ) {
        items(reminders, key = { it.id }) { reminder ->
            ReminderCard(
                reminder = reminder,
                onReminderClicked = { onReminderClicked(reminder) },
                onReminderDeleted = { onReminderDeleted(reminder) }
            )
        }
    }
}

@PreviewLightDark
@Composable
private fun ReminderListPreview() {
    RemindMeTheme {
        ReminderList(
            reminders = List(5) { index ->
                Reminder(
                    id = index,
                    title = "Reminder ${index + 1}",
                    description = "Description of reminder ${index + 1}",
                    dateTime = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
                )
            }
        )
    }
}

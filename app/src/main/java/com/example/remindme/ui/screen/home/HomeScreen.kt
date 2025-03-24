package com.example.remindme.ui.screen.home

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.remindme.R
import com.example.remindme.domain.model.Reminder
import com.example.remindme.ui.theme.RemindMeTheme
import kotlinx.serialization.Serializable

@Serializable
object Home

/**
 * Home screen of the app. Displays a list of [Reminder]s and a [FloatingActionButton] to add a
 * new [Reminder].
 */
@Composable
fun HomeScreen(
    viewModel: HomeScreenViewModel = hiltViewModel(),
    onAddReminderClick: () -> Unit = {}
) {
    // Listen to UiState
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = { HomeScreenAppBar() },
        floatingActionButton = { HomeScreenFab(onClick = onAddReminderClick) },
    ) { innerPadding ->
        HomeScreenContent(
            reminders = uiState.reminders,
            onReminderDeleted = viewModel::deleteReminder,
            modifier = Modifier.padding(innerPadding),
        )
    }
}

/**
 * Content of the [HomeScreen]. Shows either a [Text] if there are no [Reminder]s or a [ReminderList]
 * if there are.
 */
@Composable
private fun HomeScreenContent(
    reminders: List<Reminder>,
    modifier: Modifier = Modifier,
    onReminderDeleted: (Reminder) -> Unit = {},
) {
    if (reminders.isEmpty()) {
        // No reminders
        Text(
            text = stringResource(R.string.homeScreen_header_noReminders),
            style = MaterialTheme.typography.titleMedium,
            textAlign = TextAlign.Center,
            modifier = modifier.fillMaxWidth(),
        )
    } else {
        ReminderList(
            reminders = reminders,
            onReminderDeleted = onReminderDeleted,
            modifier = modifier,
        )
    }
}

@PreviewLightDark
@Composable
private fun HomeScreenContentPreview() {
    RemindMeTheme {
        HomeScreenContent(reminders = emptyList())
    }
}

/**
 * [CenterAlignedTopAppBar] with the [R.string.app_name] as the title.
 */
@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun HomeScreenAppBar(modifier: Modifier = Modifier) {
    CenterAlignedTopAppBar(
        title = {
            Text(text = stringResource(R.string.app_name))
        },
        modifier = modifier,
    )
}

@PreviewLightDark
@Composable
private fun HomeScreenAppBarPreview() {
    RemindMeTheme {
        HomeScreenAppBar()
    }
}

/**
 * [FloatingActionButton] with the [Icons.Filled.Add] as the icon.
 *
 * @param onClick Called when the button is clicked.
 */
@Composable
private fun HomeScreenFab(modifier: Modifier = Modifier, onClick: () -> Unit = {}) {
    FloatingActionButton(
        onClick = onClick,
        modifier = modifier,
    ) {
        Icon(
            imageVector = Icons.Filled.Add,
            contentDescription = Icons.Filled.Add.name,
        )
    }
}

@Composable
@PreviewLightDark
private fun HomeScreenFabPreview() {
    RemindMeTheme {
        HomeScreenFab()
    }
}

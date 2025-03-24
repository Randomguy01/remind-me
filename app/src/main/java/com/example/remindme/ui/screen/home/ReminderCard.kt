package com.example.remindme.ui.screen.home

import androidx.compose.animation.core.exponentialDecay
import androidx.compose.animation.core.spring
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.gestures.AnchoredDraggableState
import androidx.compose.foundation.gestures.DraggableAnchors
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.anchoredDraggable
import androidx.compose.foundation.gestures.animateTo
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.util.fastForEach
import androidx.compose.ui.util.fastMap
import androidx.compose.ui.util.fastMaxOfOrNull
import com.example.remindme.domain.model.Reminder
import com.example.remindme.ui.screen.home.ReminderCardState.Companion.Saver
import com.example.remindme.ui.theme.RemindMeTheme
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.format.MonthNames
import kotlinx.datetime.format.char
import kotlinx.datetime.toLocalDateTime
import kotlin.math.roundToInt

/**
 * Displays a [Reminder] as a [Card] with all information. [onReminderClicked] is called when the
 * [Card] clicked. [onReminderDeleted] is called when the [Card] is deleted.
 *
 * @param reminder The [Reminder] to display.
 * @param cardState Controls the [AnchoredDraggableState] of the [Card].
 * @param onReminderClicked Called when the reminder is clicked.
 * @param onReminderDeleted Called when the reminder is deleted.
 */
@Composable
@OptIn(ExperimentalFoundationApi::class)
fun ReminderCard(
    reminder: Reminder,
    modifier: Modifier = Modifier,
    cardState: ReminderCardState = rememberReminderCardState(),
    onReminderClicked: () -> Unit = {},
    onReminderDeleted: () -> Unit = {},
) {
    // Get device density for calculating conversion of dp to pixels
    val density = LocalDensity.current

    // Pass density to state
    SideEffect { cardState.density = density }

    // Custom layout allows measuring and constraints
    Layout(
        content = {
            ReminderCardContent(
                reminder = reminder,
                cardState = cardState,
                onReminderClicked = onReminderClicked,
                onReminderDeleted = onReminderDeleted,
            )
        },
        modifier = modifier,
    ) { measurables, constraints ->
        val looseConstraints = constraints.copy(minWidth = 0, minHeight = 0)
        val placeables = measurables.fastMap { it.measure(looseConstraints) }
        val width = placeables.fastMaxOfOrNull { it.width } ?: 0
        val height = placeables.fastMaxOfOrNull { it.height } ?: 0

        layout(width, height) {
            // Update anchors for dragging
            cardState.anchoredDraggableState.updateAnchors(
                DraggableAnchors {
                    ReminderCardValue.Rest at 0f
                    ReminderCardValue.Dragged at -200f
                    ReminderCardValue.Deleted at -width.toFloat()
                }
            )
            placeables.fastForEach { it.placeRelative(0, 0) }
        }
    }

    // Watch card state's target value
    LaunchedEffect(cardState.targetValue) {
        // If dragged to delete
        snapshotFlow { cardState.targetValue }
            .filter { it == ReminderCardValue.Deleted }
            .collect { onReminderDeleted() }
    }
}

@PreviewLightDark
@Composable
private fun ReminderCardPreviewRest() {
    RemindMeTheme {
        ReminderCard(
            Reminder(
                id = -1,
                title = "Finish Remind Me app",
                description = "Create and publish the Remind Me app! Also do something really long that will hopefully just cur off instead of squishing :)",
                dateTime = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
            )
        )
    }
}

@PreviewLightDark
@Composable
private fun ReminderCardPreviewDragged() {
    RemindMeTheme {
        ReminderCard(
            reminder = Reminder(
                id = -1,
                title = "Finish Remind Me app",
                description = "Create and publish the Remind Me app!",
                dateTime = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
            ),
            cardState = rememberReminderCardState(ReminderCardValue.Dragged)
        )
    }
}

/**
 * Displays the content of a [Reminder] as a [Card].
 *
 * @param reminder The [Reminder] to display.
 * @param cardState Controls the [AnchoredDraggableState] of the [Card].
 * @param onReminderClicked Called when the reminder is clicked.
 * @param onReminderDeleted Called when the reminder is deleted.
 */
@Composable
@OptIn(ExperimentalFoundationApi::class)
private fun ReminderCardContent(
    reminder: Reminder,
    cardState: ReminderCardState,
    modifier: Modifier = Modifier,
    onReminderClicked: () -> Unit = {},
    onReminderDeleted: () -> Unit = {},
) {
    // Create coroutine scope for asynchronous operations
    val coroutineScope = rememberCoroutineScope()

    Box {
        IconButton(
            onClick = {
                coroutineScope.launch {
                    cardState.delete()
                    onReminderDeleted()
                }
            },
            modifier = Modifier.align(Alignment.CenterEnd)
        ) {
            Icon(Icons.Filled.Delete, Icons.Filled.Delete.name)
        }
        Card(
            onClick = onReminderClicked,
            modifier = modifier
                .fillMaxWidth()
                .offset {
                    IntOffset(
                        x = cardState.anchoredDraggableState.requireOffset().roundToInt(),
                        y = 0
                    )
                }
                .anchoredDraggable(
                    state = cardState.anchoredDraggableState,
                    orientation = Orientation.Horizontal,
                )
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(8.dp)
            ) {
                Column(horizontalAlignment = Alignment.Start, modifier = Modifier.weight(0.85f)) {
                    // Title
                    Text(
                        text = reminder.title,
                        style = MaterialTheme.typography.titleLarge,
                    )
                    // Only show description if not empty
                    if (reminder.description.isNotBlank()) {
                        Spacer(Modifier.height(4.dp))
                        // Description
                        Text(
                            text = reminder.description,
                            style = MaterialTheme.typography.bodyMedium,
                            overflow = TextOverflow.Ellipsis,
                            maxLines = 5
                        )
                    }
                }
                NotificationIcon(reminder.dateTime, modifier = Modifier.weight(0.15f))
            }
        }

    }
}

/**
 * Create and [remember] a [ReminderCardState].
 *
 * @param initialValue The initial value of the [ReminderCardState]. Defaults to
 * [ReminderCardValue.Rest].
 */
@Composable
fun rememberReminderCardState(
    initialValue: ReminderCardValue = ReminderCardValue.Rest,
): ReminderCardState {
    return rememberSaveable(saver = Saver()) {
        ReminderCardState(initialValue)
    }
}

/**
 * Represents the possible [ReminderCardState] values.
 */
enum class ReminderCardValue {
    /**
     * The [ReminderCard] is at rest and not being dragged. Default value.
     */
    Rest,

    /**
     * The [ReminderCard] has been dragged slightly, revealing the delete button.
     */
    Dragged,

    /**
     * The [ReminderCard] has been fully swiped away and is now deleted.
     */
    Deleted,
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
class ReminderCardState(
    initialValue: ReminderCardValue = ReminderCardValue.Rest,
) {

    /**
     * The [AnchoredDraggableState] of the [ReminderCard].
     */
    internal val anchoredDraggableState = AnchoredDraggableState<ReminderCardValue>(
        initialValue = initialValue,
        positionalThreshold = { distance: Float -> distance * PositionalThreshold },
        velocityThreshold = { with(requireDensity()) { VelocityThreshold.toPx() } },
        snapAnimationSpec = SnapAnimationSpec,
        decayAnimationSpec = DecayAnimationSpec,
    )

    /**
     * The current value of the [ReminderCardState] via [AnchoredDraggableState.currentValue].
     */
    val currentValue: ReminderCardValue
        get() = anchoredDraggableState.currentValue

    /**
     * The target value of the [ReminderCardState] via [AnchoredDraggableState.targetValue].
     */
    val targetValue: ReminderCardValue
        get() = anchoredDraggableState.targetValue

    /**
     * Triggers a swipe to delete.
     */
    suspend fun delete() = anchoredDraggableState.animateTo(ReminderCardValue.Deleted)

    /**
     * Stores the [Density] of the [ReminderCard].
     */
    internal var density: Density? by mutableStateOf(null)

    /**
     * Returns the [Density] of the [ReminderCard] or throws an [IllegalStateException] if it is
     * not set.
     */
    private fun requireDensity() = requireNotNull(density) { "Density is not set" }

    companion object {
        private const val PositionalThreshold = 0.5f
        private val VelocityThreshold = 300.dp
        private val SnapAnimationSpec = spring<Float>()
        private val DecayAnimationSpec = exponentialDecay<Float>()

        /**
         * Custom [Saver] for [ReminderCardState].
         */
        fun Saver() = Saver<ReminderCardState, ReminderCardValue>(
            save = { it.currentValue },
            restore = { ReminderCardState(it) }
        )
    }
}


/**
 * Displays an [Icon] with the date and time of the [Reminder], formatted with
 * [LocalDateTime.Format].
 */
@Composable
private fun NotificationIcon(dateTime: LocalDateTime, modifier: Modifier = Modifier) {
    // MAR 31
    val dateFormat = LocalDateTime.Format {
        monthName(MonthNames.ENGLISH_ABBREVIATED)
        char(' ')
        dayOfMonth()
    }

    // 23:59
    val timeFormat = LocalDateTime.Format {
        hour()
        char(':')
        minute()
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = modifier
    ) {
        // Notification Icon
        Icon(
            imageVector = Icons.Outlined.Notifications,
            contentDescription = Icons.Outlined.Notifications.name,
        )
        // Formatted Date
        Text(
            text = dateFormat.format(dateTime),
            style = MaterialTheme.typography.labelSmall.copy(lineHeight = 4.sp),
        )
        //Formatted Time
        Text(
            text = timeFormat.format(dateTime),
            style = MaterialTheme.typography.labelSmall.copy(lineHeight = 4.sp),
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
private fun NotificationIconPreview() {
    RemindMeTheme {
        NotificationIcon(
            LocalDateTime(
                year = 2025,
                monthNumber = 3,
                dayOfMonth = 31,
                hour = 23,
                minute = 59,
                second = 59,
            )
        )
    }
}

package com.lasso.lassoapp.screens.calendar

import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.Transition
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.EventAvailable
import androidx.compose.material.icons.outlined.EventBusy
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import com.lasso.lassoapp.ui.theme.LassoPrimary
import com.lasso.lassoapp.ui.theme.LassoGray
import com.lasso.lassoapp.ui.theme.LassoSurface
import com.lasso.lassoapp.ui.theme.LassoTextMuted
import com.lasso.lassoapp.ui.theme.LassoTextPrimary

@Composable
internal fun CalendarAddActions(onAddAppointment: () -> Unit, onAddCalendarBlock: () -> Unit, modifier: Modifier = Modifier) {
    var expanded by rememberSaveable { mutableStateOf(false) }
    var pendingAction by remember { mutableStateOf<CalendarAddDestination?>(null) }
    val currentOnAddAppointment by rememberUpdatedState(onAddAppointment)
    val currentOnAddCalendarBlock by rememberUpdatedState(onAddCalendarBlock)
    val menuVisibility = remember { MutableTransitionState(false) }
    menuVisibility.targetState = expanded
    val menuTransition = updateTransition(menuVisibility, label = "Calendar action menu")
    val iconRotation by animateFloatAsState(
        targetValue = if (expanded) 45f else 0f,
        animationSpec = tween(220),
        label = "Calendar add icon rotation",
    )
    LaunchedEffect(menuVisibility.isIdle, menuVisibility.currentState, pendingAction) {
        if (menuVisibility.isIdle && !menuVisibility.currentState) {
            val action = pendingAction
            pendingAction = null
            when (action) {
                CalendarAddDestination.Appointment -> currentOnAddAppointment()
                CalendarAddDestination.CalendarBlock -> currentOnAddCalendarBlock()
                null -> Unit
            }
        }
    }
    Box(modifier) {
        CalendarAddButton(expanded = expanded, iconRotation = iconRotation, onClick = {
            pendingAction = null
            expanded = !expanded
        })
        if (expanded || menuVisibility.currentState || !menuVisibility.isIdle) {
            Popup(
                alignment = Alignment.BottomEnd,
                onDismissRequest = { expanded = false },
                properties = PopupProperties(focusable = true),
            ) {
                Column(
                    modifier = Modifier.widthIn(max = 300.dp),
                    horizontalAlignment = Alignment.End,
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    CalendarAnimatedAddAction(
                        transition = menuTransition,
                        enterDelay = 50,
                        exitDelay = 0,
                        label = "Agregar bloqueo de calendario",
                        icon = Icons.Outlined.EventBusy,
                        iconTint = LassoGray,
                        enabled = expanded,
                        onClick = {
                            pendingAction = CalendarAddDestination.CalendarBlock
                            expanded = false
                        },
                    )
                    CalendarAnimatedAddAction(
                        transition = menuTransition,
                        enterDelay = 0,
                        exitDelay = 50,
                        label = "Agregar cita",
                        icon = Icons.Outlined.EventAvailable,
                        enabled = expanded,
                        onClick = {
                            pendingAction = CalendarAddDestination.Appointment
                            expanded = false
                        },
                    )
                    CalendarAddButton(expanded = expanded, iconRotation = iconRotation, onClick = {
                        pendingAction = null
                        expanded = !expanded
                    })
                }
            }
        }
    }
}

private enum class CalendarAddDestination { Appointment, CalendarBlock }

@Composable
private fun CalendarAddButton(expanded: Boolean, iconRotation: Float, onClick: () -> Unit) {
    FloatingActionButton(
        onClick = onClick,
        containerColor = LassoPrimary,
        contentColor = Color.White,
        shape = CircleShape,
        elevation = FloatingActionButtonDefaults.elevation(
            defaultElevation = 0.dp,
            pressedElevation = 0.dp,
            focusedElevation = 0.dp,
            hoveredElevation = 0.dp,
        ),
        modifier = Modifier.size(56.dp),
    ) {
        Icon(
            imageVector = Icons.Default.Add,
            contentDescription = if (expanded) "Cerrar opciones de calendario" else "Agregar al calendario",
            modifier = Modifier.graphicsLayer { rotationZ = iconRotation },
        )
    }
}

@Composable
private fun CalendarAnimatedAddAction(
    transition: Transition<Boolean>,
    enterDelay: Int,
    exitDelay: Int,
    label: String,
    icon: ImageVector,
    onClick: () -> Unit,
    enabled: Boolean,
    iconTint: Color = LassoPrimary,
) {
    val scale by transition.animateFloat(
        transitionSpec = {
            if (targetState) tween(220, delayMillis = enterDelay)
            else tween(180, delayMillis = exitDelay)
        },
        label = "$label scale",
    ) { visible -> if (visible) 1f else 0f }
    Surface(
        modifier = Modifier.graphicsLayer {
            scaleX = scale
            scaleY = scale
            transformOrigin = TransformOrigin(1f, 0.5f)
        },
        onClick = onClick,
        enabled = enabled,
        shape = RoundedCornerShape(50),
        color = LassoSurface,
        contentColor = if (enabled) LassoTextPrimary else LassoTextMuted,
        shadowElevation = 4.dp,
    ) {
        Row(
            modifier = Modifier.heightIn(min = 48.dp).padding(horizontal = 20.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Icon(icon, contentDescription = null, modifier = Modifier.size(24.dp),
                tint = if (enabled) iconTint else LassoTextMuted)
            Text(label, style = MaterialTheme.typography.labelLarge)
        }
    }
}

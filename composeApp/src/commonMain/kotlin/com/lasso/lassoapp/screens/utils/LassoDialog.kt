package com.lasso.lassoapp.screens.utils

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties

/**
 * Stable screen-sized host prevents animated card height from feeding back into its height limit.
 */
@Composable
internal fun LassoDialogFrame(
    onDismiss: () -> Unit,
    shape: Shape = RoundedCornerShape(24.dp),
    color: Color = MaterialTheme.colorScheme.surface,
    border: BorderStroke? = null,
    expanded: Boolean = false,
    content: @Composable ColumnScope.() -> Unit,
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false),
    ) {
        BoxWithConstraints(
            Modifier.fillMaxSize().imePadding(),
            contentAlignment = Alignment.Center,
        ) {
            // The host fills the window, so native outside detection cannot see the card bounds.
            Box(
                Modifier.matchParentSize().pointerInput(onDismiss) {
                    detectTapGestures { onDismiss() }
                }
            )
            Card(
                modifier =
                    Modifier.padding(horizontal = 16.dp)
                        .widthIn(max = 560.dp)
                        .fillMaxWidth()
                        .then(
                            if (expanded) Modifier.height(maxHeight * .9f)
                            else Modifier.heightIn(max = maxHeight * .9f)
                        )
                        // Consume blank card taps. Descendants still handle their own gestures.
                        .pointerInput(Unit) { detectTapGestures(onTap = {}) },
                shape = shape,
                border = border,
                colors = CardDefaults.cardColors(containerColor = color),
                content = content,
            )
        }
    }
}

@Composable
internal fun DialogContentScaffold(
    onClose: () -> Unit,
    header: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    onBack: (() -> Unit)? = null,
    closeEnabled: Boolean = true,
    actions: (@Composable () -> Unit)? = null,
    scroll: ScrollState = rememberScrollState(),
    body: @Composable ColumnScope.() -> Unit,
) {
    Column(modifier.fillMaxWidth()) {
        Row(Modifier.fillMaxWidth().padding(4.dp), verticalAlignment = Alignment.CenterVertically) {
            if (onBack != null) {
                TextButton(onClick = onBack) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("Volver")
                }
            }
            Spacer(Modifier.weight(1f))
            IconButton(onClick = onClose, enabled = closeEnabled) {
                Icon(
                    Icons.Default.Close,
                    contentDescription = "Cerrar",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
        AdaptiveScrollContent(
            scroll = scroll,
            modifier = Modifier.weight(1f, fill = false),
            header = {
                Box(Modifier.padding(horizontal = 24.dp).padding(top = 8.dp, bottom = 24.dp)) {
                    header()
                }
            },
            actions =
                actions?.let { action ->
                    {
                        Box(
                            Modifier.padding(horizontal = 24.dp)
                                .padding(top = 16.dp, bottom = 24.dp)
                        ) {
                            action()
                        }
                    }
                },
        ) {
            Column(
                Modifier.fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .padding(bottom = if (actions == null) 24.dp else 0.dp),
                content = body,
            )
        }
    }
}

package com.lasso.lassoapp.desktop

import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.FilterQuality
import lassoapp.composeapp.generated.resources.Res
import lassoapp.composeapp.generated.resources.lasso_desktop_wordmark
import org.jetbrains.compose.resources.imageResource

@Composable
internal fun DesktopLassoLogo(modifier: Modifier = Modifier) {
    Image(
        bitmap = imageResource(Res.drawable.lasso_desktop_wordmark),
        contentDescription = "Lasso",
        modifier = modifier,
        // The desktop asset is prefiltered near display size; keep interpolation for DPI scaling.
        filterQuality = FilterQuality.Medium,
    )
}

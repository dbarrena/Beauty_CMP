package com.lasso.lassoapp.utils

import androidx.compose.runtime.Composable

/** Opens a document share sheet. The administrator chooses WhatsApp and its recipient. */
@Composable
expect fun rememberComisionesPdfSharer(): (ComisionesPdfFile) -> Unit

package com.lasso.lassoapp.utils

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import java.awt.Desktop
import java.nio.file.Files

@Composable
actual fun rememberComisionesPdfSharer(): (ComisionesPdfFile) -> Unit = remember {
    { pdf ->
        val file = Files.createTempFile("lasso-comisiones-", ".pdf").toFile()
        file.writeBytes(pdf.bytes)
        file.deleteOnExit()
        if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.OPEN)) {
            Desktop.getDesktop().open(file)
        }
    }
}

package com.lasso.lassoapp.utils

import android.content.ClipData
import android.content.Intent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.FileProvider
import java.io.File

@Composable
actual fun rememberComisionesPdfSharer(): (ComisionesPdfFile) -> Unit {
    val context = LocalContext.current
    return remember(context) {
        { pdf ->
            val directory = File(context.cacheDir, "commission_reports").apply { mkdirs() }
            // Remove old private statements; retain recent files for receiving apps.
            directory.listFiles()?.filter { System.currentTimeMillis() - it.lastModified() > 24 * 60 * 60 * 1000L }
                ?.forEach { it.delete() }
            val file = File(directory, pdf.fileName)
            file.writeBytes(pdf.bytes)
            val uri = FileProvider.getUriForFile(context, "${context.packageName}.commissionfiles", file)
            val intent = Intent(Intent.ACTION_SEND).apply {
                type = "application/pdf"
                putExtra(Intent.EXTRA_STREAM, uri)
                clipData = ClipData.newRawUri("Comisiones", uri)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            context.startActivity(Intent.createChooser(intent, "Compartir comisiones"))
        }
    }
}

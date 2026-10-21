package com.lasso.lassoapp.utils

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.usePinned
import platform.CoreGraphics.CGRectMake
import platform.Foundation.NSData
import platform.Foundation.NSTemporaryDirectory
import platform.Foundation.NSURL
import platform.Foundation.create
import platform.Foundation.writeToFile
import platform.UIKit.UIActivityViewController
import platform.UIKit.UIApplication
import platform.UIKit.popoverPresentationController

@OptIn(ExperimentalForeignApi::class)
@Composable
actual fun rememberComisionesPdfSharer(): (ComisionesPdfFile) -> Unit = remember {
    { pdf ->
        val path = NSTemporaryDirectory() + pdf.fileName
        val data = pdf.bytes.usePinned { NSData.create(bytes = it.addressOf(0), length = pdf.bytes.size.toULong()) }
        check(data.writeToFile(path, atomically = true)) { "No se pudo guardar el PDF." }
        var controller = UIApplication.sharedApplication.keyWindow?.rootViewController
        while (controller?.presentedViewController != null) controller = controller.presentedViewController
        val presenter = checkNotNull(controller) { "No se pudo abrir el menú para compartir." }
        val sheet = UIActivityViewController(listOf(NSURL.fileURLWithPath(path)), null)
        sheet.popoverPresentationController?.let {
            it.sourceView = presenter.view
            it.sourceRect = CGRectMake(0.0, 0.0, 1.0, 1.0)
        }
        presenter.presentViewController(sheet, animated = true, completion = null)
    }
}

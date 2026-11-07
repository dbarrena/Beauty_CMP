package com.lasso.lassoapp.desktop

import com.sun.jna.Native
import com.sun.jna.Pointer
import com.sun.jna.ptr.IntByReference
import com.sun.jna.win32.StdCallLibrary
import java.awt.Window
import java.awt.event.WindowAdapter
import java.awt.event.WindowEvent

internal interface DwmApi : StdCallLibrary {
    fun DwmSetWindowAttribute(window: Pointer, attribute: Int, value: IntByReference, size: Int): Int
}

/** Windows 11 native decoration colors, preserving the system title bar and window controls. */
internal fun installWindowsTitleBarColors(window: Window): WindowAdapter {
    fun applyColors() {
        applyWindowsTitleBarColors(window)
    }
    val listener = object : WindowAdapter() {
        override fun windowOpened(event: WindowEvent) = applyColors()
        override fun windowActivated(event: WindowEvent) = applyColors()
        override fun windowDeactivated(event: WindowEvent) = applyColors()
    }
    window.addWindowListener(listener)
    if (window.isDisplayable) applyColors()
    return listener
}

internal fun applyWindowsTitleBarColors(window: Window): Boolean {
        if (!System.getProperty("os.name").startsWith("Windows", ignoreCase = true) || !window.isDisplayable) return false
        return try {
            val dwm = Native.load("dwmapi", DwmApi::class.java)
            val hwnd = Native.getWindowPointer(window)
            // COLORREF is encoded as 0x00BBGGRR; the supplied sample is #F8F8FA.
            val primary = IntByReference(0x00FAF8F8)
            val foreground = IntByReference(0x003C3D35) // LassoTextPrimary #353D3C
            val border = dwm.DwmSetWindowAttribute(hwnd, 34, primary, 4)
            val caption = dwm.DwmSetWindowAttribute(hwnd, 35, primary, 4)
            val text = dwm.DwmSetWindowAttribute(hwnd, 36, foreground, 4)
            border == 0 && caption == 0 && text == 0
        } catch (error: LinkageError) {
            System.err.println("Unable to apply Lasso window colors: ${error.message}")
            false
        } catch (error: RuntimeException) {
            System.err.println("Unable to apply Lasso window colors: ${error.message}")
            false
        }
}

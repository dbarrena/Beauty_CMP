package com.lasso.lassoapp.desktop

import org.junit.Assume.assumeTrue
import org.junit.Test
import java.awt.EventQueue
import java.awt.Frame
import java.awt.GraphicsEnvironment
import kotlin.test.assertTrue

class WindowsTitleBarTest {
    @Test fun nativeWindowAcceptsLassoColors() {
        assumeTrue(System.getProperty("os.name").startsWith("Windows") && !GraphicsEnvironment.isHeadless())
        var applied = false
        EventQueue.invokeAndWait {
            val window = Frame()
            try {
                window.addNotify()
                applied = applyWindowsTitleBarColors(window)
            } finally {
                window.dispose()
            }
        }
        assertTrue(applied, "Windows did not accept the title bar color attributes")
    }
}

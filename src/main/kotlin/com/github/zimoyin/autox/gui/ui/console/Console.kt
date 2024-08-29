package com.github.zimoyin.autox.gui.ui.console

import com.github.zimoyin.autox.gui.Application
import java.awt.BorderLayout
import java.awt.Dimension
import java.awt.Image
import java.awt.Toolkit
import java.awt.event.ComponentAdapter
import java.awt.event.ComponentEvent
import java.awt.event.WindowAdapter
import java.awt.event.WindowEvent
import javax.swing.*

/**
 *
 * @author : zimo
 * @date : 2024/08/29
 */
class Console {
    private val logArea = JTextArea()
    private lateinit var frame: JFrame


    fun createConsole(parentFrame: JFrame? = null): Console {
        frame = JFrame()
        val imageIcon = ImageIcon(Application::class.java.getResource("/console.png"));
        frame.iconImages = listOf(imageIcon.image.getScaledInstance(80, 80, Image.SCALE_DEFAULT))

        frame.size = Dimension(600, 400)
        frame.title = "Console"
        frame.layout = BorderLayout()

        // Create a JTextArea to display logs
        logArea.isEditable = false
        logArea.lineWrap = true
        logArea.wrapStyleWord = true
        logArea.text = ""
        val scrollPane = JScrollPane(logArea)
        frame.add(scrollPane, BorderLayout.CENTER)

        // Center the dialog on the screen
        val screenSize = Toolkit.getDefaultToolkit().screenSize
        val dialogSize = frame.size
        frame.setLocation(
            (screenSize.width - dialogSize.width) / 2,
            (screenSize.height - dialogSize.height) / 2
        )

        // Handle dialog movement to ensure it stays within the bounds of the main frame
        frame.addComponentListener(object : ComponentAdapter() {
            override fun componentResized(e: ComponentEvent) {
                SwingUtilities.invokeLater {
                    if (parentFrame == null) return@invokeLater
                    val mainBounds = parentFrame.bounds
                    val dialogBounds = frame.bounds
                    if (dialogBounds.x < mainBounds.x) {
                        frame.setLocation(mainBounds.x, dialogBounds.y)
                    }
                    if (dialogBounds.y < mainBounds.y) {
                        frame.setLocation(dialogBounds.x, mainBounds.y)
                    }
                    if (dialogBounds.x + dialogBounds.width > mainBounds.x + mainBounds.width) {
                        frame.setLocation(mainBounds.x + mainBounds.width - dialogBounds.width, dialogBounds.y)
                    }
                    if (dialogBounds.y + dialogBounds.height > mainBounds.y + mainBounds.height) {
                        frame.setLocation(dialogBounds.x, mainBounds.y + mainBounds.height - dialogBounds.height)
                    }
                }
            }
        })

        // Handle dialog closing with a confirmation dialog
        frame.defaultCloseOperation = JDialog.DO_NOTHING_ON_CLOSE
        frame.addWindowListener(object : WindowAdapter() {
            override fun windowClosing(e: WindowEvent) {
                val result = JOptionPane.showConfirmDialog(
                    frame,
                    "关闭控制台后无法再次打开，并且打包任务不会停止，是否继续？",
                    "误操作警告",
                    JOptionPane.YES_NO_OPTION
                )
                if (result == JOptionPane.YES_OPTION) {
                    frame.dispose()
                }
            }
        })

        frame.isVisible = true
        return this
    }

    fun close() {
        frame.isVisible = false
        frame.dispose()
    }

    fun logArea(message: String) {
        SwingUtilities.invokeLater {
            logArea.append("[Build] $message\n\n")
            logArea.caretPosition = logArea.text.length // Auto-scroll to the end
        }
    }
}
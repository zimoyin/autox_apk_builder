package com.github.zimoyin.autox.gui

import com.formdev.flatlaf.FlatDarculaLaf
import com.github.zimoyin.autox.builder.log
import com.github.zimoyin.autox.gui.ui.setting.SettingPanel
import java.awt.Image
import java.awt.event.WindowAdapter
import java.awt.event.WindowEvent
import javax.swing.ImageIcon
import javax.swing.JFrame

/**
 *  Swing 程序入口
 * @author : zimo
 * @date : 2024/08/28
 */
class Application private constructor() : JFrame("AutoX APK Builder - v$GUI_VERSION") {
    companion object {
        var application: Application = FlatDarculaLaf.setup()
            .let { Application() }
            .apply { log("Application initialized") }
            private set

        fun start(config: ApkBuilderPojo? = null) {
            log("AutoX APK Builder：Start")

            val imageIcon = ImageIcon(Application::class.java.getResource("/build.png"));
            application.iconImage = imageIcon.image.getScaledInstance(80, 80, Image.SCALE_DEFAULT)

            // 窗体位于屏幕中间。窗体大小为 高：屏幕的五分之三，宽为 高的等比例
            val screenSize = java.awt.Toolkit.getDefaultToolkit().screenSize
            val width = screenSize.width
            val height = screenSize.height
            val frameWidth = width / 2
            val frameHeight = height * 5 / 6
            application.setBounds(width / 2 - frameWidth / 2, height / 2 - frameHeight / 2, frameWidth, frameHeight)
            log("bounds: ${application.bounds}")
            application.setDefaultCloseOperation(EXIT_ON_CLOSE)
            application.contentPane.add(SettingPanel(config).createPanel())
            application.isResizable = false
            application.addWindowListener(object : WindowAdapter() {
                override fun windowClosing(e: WindowEvent) {
                    log("AutoX APK Builder：Exit")
                }
            })

            application.revalidate()
            application.repaint()
            application.isVisible = true
        }
    }
}
package com.github.zimoyin.autox.gui

import com.formdev.flatlaf.FlatDarculaLaf
import com.github.zimoyin.autox.builder.log
import com.github.zimoyin.autox.builder.tools.toJsonObject
import com.github.zimoyin.autox.builder.tools.writeToFile
import com.github.zimoyin.autox.gui.ui.NotificationWindows
import com.github.zimoyin.autox.gui.ui.setting.SettingPanel
import java.awt.Image
import java.awt.event.WindowAdapter
import java.awt.event.WindowEvent
import java.io.File
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
            val config0 = config ?: ApkBuilderPojo().apply {
                this.workDir = File("./cache").absolutePath
            }
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
            application.contentPane.add(SettingPanel(config0).createPanel())
            application.isResizable = false
            application.addWindowListener(object : WindowAdapter() {
                override fun windowClosing(e: WindowEvent) {
                    log("AutoX APK Builder：Exit")
                }
            })

            application.revalidate()
            application.repaint()
            application.isVisible = true

            if (config == null) {
                if (NotificationWindows.infoConfirmDialog("没有检测配置文件是否在当前工作目录下创建一个配置文件")) {
                    config0.toJsonObject().writeToFile("./apk_builder_config.json")
                    application.contentPane.removeAll()
                    application.contentPane.add(SettingPanel(config0).createPanel())
                    application.revalidate()
                    application.repaint()
                    NotificationWindows.info("配置文件创建完成")
                }
            }
        }
    }
}
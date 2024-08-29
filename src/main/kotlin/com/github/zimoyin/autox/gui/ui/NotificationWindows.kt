package com.github.zimoyin.autox.gui.ui

import javax.swing.JOptionPane

/**
 *
 * @author : zimo
 * @date : 2024/08/29
 */
class NotificationWindows {
    companion object{
        fun error(message: String?) {
            error("Error",message)
        }

        fun error(title:String = "Error", message: String?) {
            JOptionPane.showMessageDialog(
                null,
                "$message",
                title,
                JOptionPane.ERROR_MESSAGE
            )
        }

        fun info(message: String?) {
            info("Info",message)
        }

        fun info(title:String = "Info", message: String?) {
            JOptionPane.showMessageDialog(
                null,
                "$message",
                title,
                JOptionPane.INFORMATION_MESSAGE
            )
        }
    }
}
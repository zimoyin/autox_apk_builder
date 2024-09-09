package com.github.zimoyin.autox.gui.ui

import javax.swing.JOptionPane

/**
 *
 * @author : zimo
 * @date : 2024/08/29
 */
class NotificationWindows {
    companion object {
        fun error(message: String?) {
            error("Error", message)
        }

        fun error(title: String = "Error", message: String?) {
            JOptionPane.showMessageDialog(
                null,
                "$message",
                title,
                JOptionPane.ERROR_MESSAGE
            )
        }

        fun infoConfirmDialog(message: String?): Boolean {
            return infoConfirmDialog("Info", message = message)
        }

        fun infoConfirmDialog(title: String = "Info", message: String?): Boolean {
            val result = JOptionPane.showConfirmDialog(
                null,
                message,  // 消息内容
                title,  // 对话框标题
                JOptionPane.OK_CANCEL_OPTION,  // 显示确定与取消按钮
                JOptionPane.INFORMATION_MESSAGE  // 消息类型
            )

            if (result == JOptionPane.OK_OPTION) {
                return true
            } else if (result == JOptionPane.CANCEL_OPTION) {
                return false
            }
            return false
        }

        fun info(message: String?) {
            info("Info", message)
        }

        fun info(title: String = "Info", message: String?) {
            JOptionPane.showMessageDialog(
                null,
                "$message",
                title,
                JOptionPane.INFORMATION_MESSAGE
            )
        }
    }
}
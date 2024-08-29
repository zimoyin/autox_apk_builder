package com.github.zimoyin.autox.gui.ui

import java.awt.FlowLayout
import java.awt.LayoutManager
import javax.swing.JComponent
import javax.swing.JPanel

/**
 *
 * @author : zimo
 * @date : 2024/08/28
 */
abstract class IPanel(layout: LayoutManager = FlowLayout()) : JPanel(layout) {
    abstract fun createPanel(panel: IPanel = this): JComponent
}
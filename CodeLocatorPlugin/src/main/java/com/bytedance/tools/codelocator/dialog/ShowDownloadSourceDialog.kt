@file:Suppress("JAVA_MODULE_DOES_NOT_EXPORT_PACKAGE")
package com.bytedance.tools.codelocator.dialog

import com.bytedance.tools.codelocator.panels.CodeLocatorWindow
import com.bytedance.tools.codelocator.utils.*
import com.intellij.openapi.progress.ProgressIndicator
import com.intellij.openapi.progress.ProgressManager
import com.intellij.openapi.progress.Task
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.DialogWrapper
import com.intellij.openapi.util.SystemInfo
import sun.font.FontDesignMetrics
import java.awt.Dimension
import javax.swing.Action
import javax.swing.BorderFactory
import javax.swing.Box
import javax.swing.BoxLayout
import javax.swing.JButton
import javax.swing.JComponent
import javax.swing.JLabel
import javax.swing.JPanel

class ShowDownloadSourceDialog(val codeLocatorWindow: CodeLocatorWindow, val project: Project) : DialogWrapper(codeLocatorWindow, true) {

    companion object {

        const val DIALOG_HEIGHT = 320

        const val DIALOG_WIDTH = 480

        fun downloadSourceBg(project: Project) {
            ProgressManager.getInstance().run(object : Task.Backgroundable(project, "正在下载项目所有依赖源码(耗时较长)", true) {
                override fun run(indicator: ProgressIndicator) {
                    try {
                        val projectPath = project.basePath!!
                        val commands = arrayListOf(
                                "cd ${projectPath.replace(" ", "\\ ")}",
                                "./gradlew :JustForCodeIndexModuleRelease:ideaModule"
                        ).joinToString(separator = ";", postfix = "", prefix = "")
                        val execCommand = ShellHelper.execCommand(commands)
                        if (execCommand.resultCode == 0) {
                            ThreadUtils.runOnUIThread {
                                NotificationUtils.showNotification(project, "源码下载完成")
                            }
                        } else {
                            ThreadUtils.runOnUIThread {
                                NotificationUtils.showNotification(
                                        project,
                                        "源码下载出现错误, " + String(execCommand.errorBytes)
                                )
                            }
                        }
                    } catch (t: Throwable) {
                        Log.e("Create Module Error", t)
                    }
                }
            })
        }
    }

    lateinit var dialogContentPanel: JPanel
    lateinit var confirmButton : JButton

    init {
        initContentPanel()
    }

    private fun initContentPanel() {
        title = "CodeLocator"
        dialogContentPanel = JPanel()
        dialogContentPanel.border = BorderFactory.createEmptyBorder(
                CoordinateUtils.DEFAULT_BORDER * 2,
                CoordinateUtils.DEFAULT_BORDER * 2,
                CoordinateUtils.DEFAULT_BORDER * 2,
                CoordinateUtils.DEFAULT_BORDER * 2
        )
        JComponentUtils.setSize(dialogContentPanel,
            DIALOG_WIDTH,
            DIALOG_HEIGHT
        )
        dialogContentPanel.layout = BoxLayout(dialogContentPanel, BoxLayout.Y_AXIS)
        contentPanel.add(dialogContentPanel)

        addOpenButton()
    }

    override fun createCenterPanel(): JComponent? {
        return dialogContentPanel
    }

    override fun createActions(): Array<Action> = emptyArray()

    private fun addOpenButton() {
        var createLabel: JLabel = createLabel("是否后台下载所有aar源码? (耗时较长)")
        val btnText = getBtnText("下载源码")
        confirmButton = JButton(btnText)
        confirmButton.addActionListener {
            Mob.mob(Mob.Action.CLICK, Mob.Button.DOWNLOAD_SOURCE)
            downloadSourceBg(project)
            close(0)
        }
        try {
            val stringWidth = FontDesignMetrics.getMetrics(confirmButton.font).stringWidth(btnText)
            confirmButton.maximumSize = Dimension(stringWidth, 38)
        } catch (t: Throwable) {
            Log.e("getfont width error", t)
        }
        val cancelText = "暂不下载"
        val dontShowText = getBtnText(cancelText!!)
        val dontShowButton = JButton(dontShowText)
        dontShowButton.addActionListener {
            Mob.mob(Mob.Action.CLICK, Mob.Button.CLOSE_DIALOG)
            close(0)
        }
        try {
            val stringWidth = FontDesignMetrics.getMetrics(dontShowButton.font).stringWidth(dontShowText)
            dontShowButton.maximumSize = Dimension(stringWidth, 38)
        } catch (t: Throwable) {
            Log.e("getFont width error", t)
        }

        dialogContentPanel.add(Box.createVerticalStrut(10))

        if (createLabel != null) {
            var horizontalBox = Box.createHorizontalBox()
            horizontalBox.add(Box.createHorizontalGlue())
            horizontalBox.add(createLabel)
            horizontalBox.add(Box.createHorizontalGlue())
            dialogContentPanel.add(horizontalBox)
            dialogContentPanel.add(Box.createVerticalGlue())
        }

        val horizontalBox = Box.createHorizontalBox()
        horizontalBox.add(Box.createHorizontalGlue())

        val buttonBox = Box.createHorizontalBox()
        buttonBox.add(Box.createHorizontalGlue())
        buttonBox.add(dontShowButton)

        if (confirmButton != null) {
            buttonBox.add(Box.createHorizontalStrut(CoordinateUtils.DEFAULT_BORDER * 2))
            buttonBox.add(confirmButton)
        }

        buttonBox.add(Box.createHorizontalGlue())
        buttonBox.maximumSize = Dimension(10086, 38)

        horizontalBox.add(buttonBox)
        horizontalBox.add(Box.createHorizontalGlue())

        dialogContentPanel.add(horizontalBox)
    }

    override fun getPreferredFocusedComponent(): JComponent? {
        return if (SystemInfo.isMac) confirmButton else null
    }

    private fun getBtnText(btnTxt: String) =
            "<html><body style='text-align:center;font-size:12px; padding-left: 12px;padding-right: 12px;padding-top: 8px;padding-bottom: 8px;'>$btnTxt</body></html>"

    private fun createLabel(text: String): JLabel {
        val jLabel = JLabel("<html><body style='text-align:center;font-size:13px;'>$text</body></html>")
        jLabel.maximumSize = Dimension(DIALOG_WIDTH - CoordinateUtils.DEFAULT_BORDER * 4, 10086)
        jLabel.minimumSize = Dimension(DIALOG_WIDTH - CoordinateUtils.DEFAULT_BORDER * 4, DIALOG_HEIGHT - CoordinateUtils.DEFAULT_BORDER * 4 - 48)
        return jLabel
    }
}

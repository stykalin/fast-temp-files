package ru.stykalin.fasttempfiles.action

import com.intellij.icons.AllIcons
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent

class FastTempFilesAction : AnAction({ "Create Temp File" }, AllIcons.Actions.InlayRenameInNoCodeFiles) {
    override fun actionPerformed(e: AnActionEvent) {
        CreateTempFileSwitchScheme().actionPerformed(e)
    }
}
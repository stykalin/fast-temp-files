package ru.stykalin.fasttempfiles.action

import com.intellij.ide.actions.OpenFileAction
import com.intellij.ide.actions.QuickSwitchSchemeAction
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.DataContext
import com.intellij.openapi.actionSystem.DefaultActionGroup
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.IconLoader
import com.intellij.openapi.util.io.FileUtilRt
import ru.stykalin.fasttempfiles.settings.FTFAppSettingsState
import ru.stykalin.fasttempfiles.settings.FTFType

class CreateTempFileSwitchSchemeAction : QuickSwitchSchemeAction(false) {
    override fun getPopupTitle(e: AnActionEvent): String = "Create Temp File"

    override fun fillActions(project: Project, group: DefaultActionGroup, dataContext: DataContext) {
        val showWithName = FTFAppSettingsState.getInstance().showWithName
        FTFAppSettingsState.getInstance().fileTypes.forEach { group.add(getTmpFileAction(fileType = it, showWithName = showWithName)) }
    }

    /** Create action for [fileType] */
    private fun getTmpFileAction(fileType: FTFType, showWithName: Boolean): AnAction {
        val actionName = if (showWithName) fileType.fileName else fileType.extension
        return object : AnAction({ actionName }, ActionIcon) {
            override fun actionPerformed(e: AnActionEvent) {
                val createdTempFile = FileUtilRt.createTempFile(fileType.name, ".${fileType.extension}", true)
                OpenFileAction.openFile(filePath = createdTempFile.path, project = e.project!!)
            }
        }
    }

    companion object {
        @JvmStatic
        private val ActionIcon = IconLoader.getIcon("/icons/action_icon.svg", this::class.java)
    }
}
package ru.stykalin.fasttempfiles.action

import com.intellij.ide.actions.OpenFileAction.Companion.openFile
import com.intellij.ide.actions.QuickSwitchSchemeAction
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.DataContext
import com.intellij.openapi.actionSystem.DefaultActionGroup
import com.intellij.openapi.project.DumbAwareAction
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.IconLoader
import com.intellij.openapi.util.io.FileUtilRt
import ru.stykalin.fasttempfiles.settings.FTFAppSettingsState
import ru.stykalin.fasttempfiles.settings.FTFType
import java.io.File

class CreateTempFileSwitchSchemeAction : QuickSwitchSchemeAction(false) {
    override fun getPopupTitle(e: AnActionEvent): String = "Create Temp File"

    override fun fillActions(project: Project, group: DefaultActionGroup, dataContext: DataContext) {
        val ftfSetting = FTFAppSettingsState.getInstance()
        val showWithName = ftfSetting.showWithName
        if (ftfSetting.showExistedFTF) addExistingTempFiles(project = project, group = group, ftfSetting = ftfSetting)
        ftfSetting.fileTypes.forEach { group.add(getTmpFileAction(fileType = it, showWithName = showWithName)) }
    }

    /** Add group with existing fast temp files (if created) */
    private fun addExistingTempFiles(project: Project, group: DefaultActionGroup, ftfSetting: FTFAppSettingsState) {
        val tempDir = File(FileUtilRt.getTempDirectory())
        val availableFileNamePrefixes = ftfSetting.fileTypes.map { it.name }.distinct()
        val existingFTempFiles = tempDir.listFiles()?.map { it.name }
            ?.filter { fileName -> availableFileNamePrefixes.any { ftfNamePrefix -> fileName.startsWith(ftfNamePrefix) } }
            ?.map { fileName -> File(tempDir, fileName) }
            ?.filter { it.exists() }
            ?.sortedBy { it.lastModified() }
            ?.reversed()
            ?: emptyList()
        if (existingFTempFiles.isNotEmpty()) {
            val existingTempFilesGroup = DefaultActionGroup()
            existingFTempFiles.forEach { ftFile ->
                existingTempFilesGroup.add(
                    DumbAwareAction.create(ftFile.name, ActionExistIcon) { openFile(filePath = ftFile.path, project = project) }
                )
            }
            group.add(existingTempFilesGroup)
            group.addSeparator()
        }
    }

    /** Create action for [fileType] */
    private fun getTmpFileAction(fileType: FTFType, showWithName: Boolean): AnAction {
        val actionName = if (showWithName) fileType.fileName else fileType.extension
        return object : AnAction({ actionName }, ActionIcon) {
            override fun actionPerformed(e: AnActionEvent) {
                val createdTempFile = FileUtilRt.createTempFile(fileType.name, ".${fileType.extension}", true)
                openFile(filePath = createdTempFile.path, project = e.project!!)
            }
        }
    }

    companion object {
        @JvmStatic
        private val ActionIcon = IconLoader.getIcon("/icons/action_icon.svg", this::class.java)

        @JvmStatic
        private val ActionExistIcon = IconLoader.getIcon("/icons/action_exist_icon.svg", this::class.java)
    }
}
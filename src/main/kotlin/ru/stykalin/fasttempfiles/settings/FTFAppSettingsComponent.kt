package ru.stykalin.fasttempfiles.settings

import com.intellij.openapi.ui.DialogPanel
import com.intellij.openapi.ui.Messages.YES
import com.intellij.openapi.ui.Messages.getNoButton
import com.intellij.openapi.ui.Messages.getWarningIcon
import com.intellij.openapi.ui.Messages.getYesButton
import com.intellij.openapi.ui.Messages.showDialog
import com.intellij.ui.TableUtil
import com.intellij.ui.ToolbarDecorator
import com.intellij.ui.dsl.builder.Align
import com.intellij.ui.dsl.builder.bindItem
import com.intellij.ui.dsl.builder.bindSelected
import com.intellij.ui.dsl.builder.panel
import com.intellij.ui.dsl.builder.toNullableProperty
import com.intellij.ui.table.JBTable

class FTFAppSettingsComponent {

    private val settings = FTFAppSettingsState.getInstance()
    private val model: FTFTableModel = FTFTableModel(typeList = settings.fileTypes.toMutableList())
    val ftfSettingPanel: DialogPanel
    var isDataChanged: Boolean = false

    init {
        model.addTableModelListener { isDataChanged = true }
        val table = JBTable(model)
        val fileTypeSettings = ToolbarDecorator.createDecorator(table)
            .setAddAction {
                val row = model.addRow()
                table.editCellAt(row, 1)
            }
            .setRemoveAction { TableUtil.removeSelectedItems(table) }
            .setMoveUpAction { TableUtil.moveSelectedItemsUp(table) }
            .setMoveDownAction { TableUtil.moveSelectedItemsDown(table) }
            .createPanel()
        ftfSettingPanel = panel {
            row {
                checkBox("Show names in list").bindSelected(settings::showWithName)
                contextHelp("Full file name will be shown in Create Temp File dialog", "Names in list")

                button("Load Defaults") {
                    val msg =
                        "You are about to load the default list of file extensions.\n\nPlease note that <b>all your custom values will be lost</b>.\n\nAre you sure you want to continue?"
                    val answer = showDialog(msg, "Warning!", arrayOf<String>(getYesButton(), getNoButton()), 1, getWarningIcon())
                    if (answer == YES) resetData(fileTypes = FTFAppSettingsState.DEFAULT_TYPES)
                }.enabledIf(model.typesChanged)
                contextHelp("This action loads the default list of file extensions.", "Load defaults")
            }
            row {
                checkBox("Show exiting FTFiles in list").bindSelected(settings::showExistingFTF)
                contextHelp("Existing temp files are displayed in 'Create Temp File' dialog", "Existing files in list")

                comboBox(FTFExistingDisplayLocation.entries).bindItem(settings::existingDisplayLocation.toNullableProperty())
                contextHelp(
                    "The location where existing temp files are displayed in the 'Create Temp File' dialog: at the TOP or at the BOTTOM of the list.",
                    "Existing files display location in list"
                )
            }

            row {
                cell(fileTypeSettings).align(Align.FILL)
            }.resizableRow()
        }
    }

    fun saveData() {
        settings.fileTypes = model.typeList
        model.fireTableDataChanged()
        ftfSettingPanel.apply()
    }

    fun resetData(fileTypes: List<FTFType> = settings.fileTypes) {
        model.resetData(fileTypes)
        model.fireTableDataChanged()
        ftfSettingPanel.reset()
    }
}
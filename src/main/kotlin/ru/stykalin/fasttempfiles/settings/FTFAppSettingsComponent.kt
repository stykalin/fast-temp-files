package ru.stykalin.fasttempfiles.settings

import com.intellij.openapi.ui.Messages.*
import com.intellij.ui.TableUtil
import com.intellij.ui.ToolbarDecorator
import com.intellij.ui.components.JBCheckBox
import com.intellij.ui.dsl.builder.Align
import com.intellij.ui.dsl.builder.panel
import com.intellij.ui.table.JBTable
import javax.swing.JPanel

class FTFAppSettingsComponent {

    private val settings = FTFAppSettingsState.getInstance()
    private val model: FTFTableModel = FTFTableModel(typeList = settings.fileTypes.toMutableList())
    private val withName: JBCheckBox = JBCheckBox("Show names in list")
    val panel: JPanel
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
        panel = panel {
            row {
                cell(withName)
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
                cell(fileTypeSettings).align(Align.FILL)
            }.resizableRow()
        }
    }

    fun saveData() {
        settings.fileTypes = model.typeList
        model.fireTableDataChanged()
        settings.showWithName = withName.isSelected
    }

    fun resetData(fileTypes: List<FTFType> = settings.fileTypes) {
        model.resetData(fileTypes)
        model.fireTableDataChanged()
        withName.isSelected = settings.showWithName
    }
}
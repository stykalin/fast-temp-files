package ru.stykalin.fasttempfiles.settings

import com.intellij.ui.layout.ComponentPredicate
import com.intellij.util.ui.ItemRemovable
import ru.stykalin.fasttempfiles.settings.FTFAppSettingsState.Companion.DEFAULT_FILE_NAME
import javax.swing.table.AbstractTableModel

class FTFTableModel(val typeList: MutableList<FTFType>) : AbstractTableModel(), ItemRemovable {

    @Suppress("PrivatePropertyName")
    private val COLUMN_NAMES = listOf("File name", "File extension")

    override fun getValueAt(rowIndex: Int, columnIndex: Int): String {
        return when (columnIndex) {
            0 -> typeList[rowIndex].component1()
            1 -> typeList[rowIndex].component2()
            else -> "UNDEFINED"
        }
    }

    override fun setValueAt(aValue: Any?, rowIndex: Int, columnIndex: Int) {
        when (columnIndex) {
            0 -> typeList[rowIndex].name = aValue.toString()
            1 -> typeList[rowIndex].extension = aValue.toString()
            else -> { /* DO NOTHING */
            }
        }
    }

    override fun getColumnName(column: Int): String {
        return COLUMN_NAMES[column]
    }

    override fun getColumnClass(columnIndex: Int): Class<*> {
        return String::class.java
    }

    override fun getRowCount(): Int {
        return typeList.size
    }

    override fun getColumnCount(): Int {
        return COLUMN_NAMES.size
    }

    override fun isCellEditable(rowIndex: Int, columnIndex: Int): Boolean {
        return true
    }

    fun addRow(): Int {
        typeList.add(EMPTY_ROW.copy())
        fireTableDataChanged()
        return typeList.size - 1
    }

    override fun removeRow(idx: Int) {
        typeList.removeAt(idx)
        fireTableDataChanged()
    }

    fun resetData(elements: List<FTFType>) {
        typeList.clear()
        typeList.addAll(elements)
        fireTableDataChanged()
    }

    companion object {
        private val EMPTY_ROW by lazy { FTFType(name = DEFAULT_FILE_NAME, extension = "") }
    }

}

/** Predicate for tracking that typeList is changed */
val FTFTableModel.typesChanged: ComponentPredicate
    get() = object : ComponentPredicate() {
        override fun addListener(listener: (Boolean) -> Unit) {
            this@typesChanged.addTableModelListener { listener(invoke()) }
        }

        override fun invoke(): Boolean {
            return typeList != FTFAppSettingsState.DEFAULT_TYPES
        }
    }
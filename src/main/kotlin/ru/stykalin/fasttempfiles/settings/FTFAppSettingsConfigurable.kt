package ru.stykalin.fasttempfiles.settings

import com.intellij.openapi.options.Configurable
import javax.swing.JComponent

class FTFAppSettingsConfigurable : Configurable {
    private lateinit var settingsComponent: FTFAppSettingsComponent

    override fun getDisplayName(): String = "AT Helper App Settings"

    override fun createComponent(): JComponent {
        settingsComponent = FTFAppSettingsComponent()
        return settingsComponent.panel
    }

    override fun isModified(): Boolean {
        return settingsComponent.isDataChanged
    }

    override fun apply() {
        settingsComponent.saveData()
    }

    override fun reset() {
        settingsComponent.resetData()
    }
}
package ru.stykalin.fasttempfiles.settings

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.Service
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import com.intellij.util.xmlb.XmlSerializerUtil
import ru.stykalin.fasttempfiles.settings.FTFExistingDisplayLocation.BOTTOM

@State(
    name = "ru.stykalin.fasttempfiles.settings.FTFAppSettingsState",
    storages = [Storage("FTFAppSettingsState.xml")]
)
@Service(Service.Level.APP)
class FTFAppSettingsState(
    var fileTypes: List<FTFType> = DEFAULT_TYPES,
    var showWithName: Boolean = false,
    var showExistingFTF: Boolean = true,
    var existingDisplayLocation: FTFExistingDisplayLocation = BOTTOM
) : PersistentStateComponent<FTFAppSettingsState> {
    override fun getState(): FTFAppSettingsState = this

    override fun loadState(state: FTFAppSettingsState) {
        XmlSerializerUtil.copyBean(state, this)
    }

    companion object {
        fun getInstance(): FTFAppSettingsState = ApplicationManager.getApplication().getService(FTFAppSettingsState::class.java)
        const val DEFAULT_FILE_NAME = "tmp"
        val DEFAULT_TYPES: List<FTFType>
            get() = listOf("txt", "json", "xml", "yml").map { FTFType(name = DEFAULT_FILE_NAME, extension = it) }
    }
}
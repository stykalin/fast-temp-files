package ru.stykalin.fasttempfiles.settings

data class FTFType(
    var name: String = "",
    var extension: String = ""
) {
    val fileName: String get() = "$name.$extension"
}
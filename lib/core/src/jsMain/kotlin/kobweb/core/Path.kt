package kobweb.core

private const val HEX_REGEX = "[0-9A-F]"
private val PATH_REGEX = Regex("""^/(([a-z0-9]|%${HEX_REGEX}${HEX_REGEX})+/?)*$""")

class Path(value: String) {
    companion object {
        fun check(path: String) = Path(path)
    }

    init {
        require(value.matches(PATH_REGEX)) { "URL path not formatted properly: $value"}
    }

    val value = value
    val parts = value.removePrefix("/").split("/")
}

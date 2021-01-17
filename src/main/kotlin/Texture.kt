import kotlin.properties.Delegates

class Texture {
    var id by Delegates.notNull<Int>()
    lateinit var type: String
    lateinit var path: String
}
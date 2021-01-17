import com.hackoeur.jglm.Vec3

class Vertex {
    lateinit var position: Vec3
    lateinit var normal: Vec3
    lateinit var texCoords: Vec2

    companion object {
        fun sizeOf() = 3 * 4 + 3 * 4 + 2 * 4
        fun offsetNormal(): Long = 3 * 4
        fun offsetTexCoords(): Long = 3 * 4 + 3 * 4
    }
}
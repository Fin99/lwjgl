import com.hackoeur.jglm.support.FastMath
import kotlin.math.pow

class Vec2 {
    var x = 0f
    var y = 0f
    val array: FloatArray
        get() {
            return floatArrayOf(this.x, this.y)
        }

    constructor() {
        this.x = 0.0f
        this.y = 0.0f
    }

    constructor(x: Float, y: Float) {
        this.x = x
        this.y = y
    }

    constructor(vec: Vec2) {
        this.x = vec.x
        this.y = vec.y
    }

    fun getDimensions(): Int {
        return 2
    }

    fun getLengthSquared(): Float {
        return x.pow(2) + y.pow(2)
    }

    fun getUnitVector(): Vec2 {
        val sqLength: Float = this.getLengthSquared()
        val invLength = FastMath.invSqrtFast(sqLength)
        return Vec2(this.x * invLength, this.y * invLength)
    }

    fun getNegated(): Vec2 {
        return Vec2(-x, -y)
    }

    fun add(vec: Vec2): Vec2 {
        return Vec2(this.x + vec.x, this.y + vec.y)
    }

    fun subtract(vec: Vec2): Vec2 {
        return Vec2(this.x - vec.x, this.y - vec.y)
    }

//    }

    fun multiply(scalar: Float): Vec2 {
        return Vec2(this.x * scalar, this.y * scalar)
    }

    fun scale(scalar: Float): Vec2 {
        return this.multiply(scalar)
    }

    fun dot(vec: Vec2): Float {
        return this.x * vec.x + this.y * vec.y
    }

//    }

    fun angleInRadians(vec: Vec2): Float {
        val dot: Float = this.dot(vec)
        val lenSq = FastMath.sqrtFast(getLengthSquared() * vec.getLengthSquared())
        return FastMath.acos((dot / lenSq).toDouble()).toFloat()
    }

    fun lerp(vec: Vec2, amount: Float): Vec2 {
        val diff = 1.0f - amount
        return Vec2(diff * this.x + amount * vec.x, diff * this.y + amount * vec.y)
    }

//    }

}

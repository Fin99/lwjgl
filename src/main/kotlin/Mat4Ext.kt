import com.hackoeur.jglm.Mat4
import com.hackoeur.jglm.Vec3
import com.hackoeur.jglm.Vec4
import com.hackoeur.jglm.support.FastMath
import kotlin.math.pow

fun Mat4.rotate(phi: Float, axis: Vec3): Mat4 {
    val cos = FastMath.cos(phi.toDouble()).toFloat()
    val oneMinusCos = 1.0f - cos
    val sin = FastMath.sin(phi.toDouble()).toFloat()
    val x = axis.x
    val y = axis.y
    val z = axis.z
    val v1 = Vec4(
        cos + x.pow(2) * oneMinusCos,
        (x * y) * oneMinusCos - z * sin,
        (x * z) * oneMinusCos + y * sin,
        0f
    )
    val v2 = Vec4(
        (y * x) * oneMinusCos + z * sin,
        cos + y.pow(2) * oneMinusCos,
        (y * z) * oneMinusCos - x * sin,
        0.0F
    )
    val v3 = Vec4(
        (z * x) * oneMinusCos - y * sin,
        (z * y) * oneMinusCos + x * sin,
        cos + z.pow(2) * oneMinusCos,
        0.0F
    )
    val v4 = Vec4(0.0F, 0.0F, 0.0F, 1.0F)
    return this.multiply(Mat4(v1, v2, v3, v4))
}

fun Mat4.scale(axis: Vec3): Mat4 {
    return this.multiply(
        Mat4(
            Vec4(axis.x, 0f, 0f, 0f),
            Vec4(0f, axis.y, 0f, 0f),
            Vec4(0f, 0f, axis.z, 0f),
            Vec4(0f, 0f, 0f, 1f)
        )
    )
}
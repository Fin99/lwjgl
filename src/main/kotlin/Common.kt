import org.lwjgl.BufferUtils
import java.nio.ByteBuffer
import java.nio.FloatBuffer
import java.nio.IntBuffer

fun createFloatBuffer(data: FloatArray): FloatBuffer {
    val buffer = BufferUtils.createFloatBuffer(data.size)
    buffer.put(data)
    buffer.flip()
    return buffer
}

fun createFloatMat4(data: FloatBuffer): FloatBuffer {
    val buffer = BufferUtils.createFloatBuffer(16)
    buffer.put(data)
    buffer.flip()
    return buffer
}

fun createByteBuffer(data: ByteBuffer): ByteBuffer {
    val buffer = BufferUtils.createByteBuffer(data.capacity())
    buffer.put(data)
    buffer.flip()
    return buffer
}

fun createIntBuffer(data: IntArray): IntBuffer {
    val buffer = BufferUtils.createIntBuffer(data.size)
    buffer.put(data)
    buffer.flip()
    return buffer
}

fun createByteBuffer(data: ByteArray): ByteBuffer {
    val buffer = BufferUtils.createByteBuffer(data.size)
    buffer.put(data)
    buffer.flip()
    return buffer
}
import org.lwjgl.BufferUtils
import org.lwjgl.opengl.GL11.*
import org.lwjgl.opengl.GL30
import org.lwjgl.stb.STBImage
import java.io.File
import java.io.IOException
import java.nio.ByteBuffer
import java.nio.FloatBuffer
import java.nio.IntBuffer

fun createFloatBuffer(data: FloatArray): FloatBuffer {
    val buffer = BufferUtils.createFloatBuffer(data.size)
    buffer.put(data)
    buffer.flip()
    return buffer
}

fun createFloatBuffer(data: List<Vertex>): FloatBuffer {
    val buffer = BufferUtils.createFloatBuffer(8 * data.size)
    data.forEach {
        buffer.put(it.position.array)
        buffer.put(it.normal.array)
        buffer.put(it.texCoords.array)
    }
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

fun loadTexture(texturePath: String): Int {
    val texture = GL30.glGenTextures()
    GL30.glBindTexture(GL30.GL_TEXTURE_2D, texture)
    GL30.glTexParameteri(GL30.GL_TEXTURE_2D, GL30.GL_TEXTURE_WRAP_S, GL30.GL_REPEAT)
    GL30.glTexParameteri(GL30.GL_TEXTURE_2D, GL30.GL_TEXTURE_WRAP_T, GL30.GL_REPEAT)
    GL30.glTexParameteri(GL30.GL_TEXTURE_2D, GL30.GL_TEXTURE_MIN_FILTER, GL30.GL_LINEAR)
    GL30.glTexParameteri(GL30.GL_TEXTURE_2D, GL30.GL_TEXTURE_MAG_FILTER, GL30.GL_LINEAR)

    val imageFile = File(texturePath)
    val imageWidth: Int
    val imageHeight: Int

    val widthBuffer = BufferUtils.createIntBuffer(1)
    val heightBuffer = BufferUtils.createIntBuffer(1)
    val comp = BufferUtils.createIntBuffer(1)
    val buffer: ByteBuffer =
        STBImage.stbi_load(imageFile.toString(), widthBuffer, heightBuffer, comp, 3)
            ?: throw IOException(STBImage.stbi_failure_reason())

    imageWidth = widthBuffer[0]
    imageHeight = heightBuffer[0]

    println("Texture $texturePath Width $imageWidth, Height $imageHeight")

//    glPixelStorei(GL_UNPACK_ALIGNMENT, 1);
    glTexImage2D(
        GL30.GL_TEXTURE_2D,
        0,
        GL30.GL_RGB,
        imageWidth,
        imageHeight,
        0,
        GL30.GL_RGB,
        GL30.GL_UNSIGNED_BYTE,
        createByteBuffer(buffer)
    )
    GL30.glGenerateMipmap(GL30.GL_TEXTURE_2D)
    GL30.glBindTexture(GL30.GL_TEXTURE_2D, 0)

    return texture
}

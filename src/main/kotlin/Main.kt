import com.hackoeur.jglm.Mat4
import com.hackoeur.jglm.Matrices.perspective
import com.hackoeur.jglm.Vec3
import org.lwjgl.BufferUtils
import org.lwjgl.Version
import org.lwjgl.glfw.Callbacks.glfwFreeCallbacks
import org.lwjgl.glfw.GLFW.*
import org.lwjgl.glfw.GLFWErrorCallback
import org.lwjgl.opengl.GL
import org.lwjgl.opengl.GL30.*
import org.lwjgl.system.MemoryStack.stackPush
import org.lwjgl.system.MemoryUtil.NULL
import java.nio.ByteBuffer
import java.nio.FloatBuffer
import java.nio.IntBuffer


private var window: Long = 0

var vertices: FloatBuffer = createFloatBuffer(
    floatArrayOf(
        -0.5f, -0.5f, -0.5f,
        0.5f, -0.5f, -0.5f,
        0.5f, 0.5f, -0.5f,
        0.5f, 0.5f, -0.5f,
        -0.5f, 0.5f, -0.5f,
        -0.5f, -0.5f, -0.5f,

        -0.5f, -0.5f, 0.5f,
        0.5f, -0.5f, 0.5f,
        0.5f, 0.5f, 0.5f,
        0.5f, 0.5f, 0.5f,
        -0.5f, 0.5f, 0.5f,
        -0.5f, -0.5f, 0.5f,

        -0.5f, 0.5f, 0.5f,
        -0.5f, 0.5f, -0.5f,
        -0.5f, -0.5f, -0.5f,
        -0.5f, -0.5f, -0.5f,
        -0.5f, -0.5f, 0.5f,
        -0.5f, 0.5f, 0.5f,

        0.5f, 0.5f, 0.5f,
        0.5f, 0.5f, -0.5f,
        0.5f, -0.5f, -0.5f,
        0.5f, -0.5f, -0.5f,
        0.5f, -0.5f, 0.5f,
        0.5f, 0.5f, 0.5f,

        -0.5f, -0.5f, -0.5f,
        0.5f, -0.5f, -0.5f,
        0.5f, -0.5f, 0.5f,
        0.5f, -0.5f, 0.5f,
        -0.5f, -0.5f, 0.5f,
        -0.5f, -0.5f, -0.5f,

        -0.5f, 0.5f, -0.5f,
        0.5f, 0.5f, -0.5f,
        0.5f, 0.5f, 0.5f,
        0.5f, 0.5f, 0.5f,
        -0.5f, 0.5f, 0.5f,
        -0.5f, 0.5f, -0.5f,
    )
)

//var indices = createIntBuffer(
//    intArrayOf(
//        0, 1, 3,   // Первый треугольник
//        1, 2, 3    // Второй треугольник
//    )
//)

var cubePositions: Array<Vec3> = arrayOf(
    Vec3(0.0f, 0.0f, 0.0f),
    Vec3(2.0f, 5.0f, -15.0f),
    Vec3(-1.5f, -2.2f, -2.5f),
    Vec3(-3.8f, -2.0f, -12.3f),
    Vec3(2.4f, -0.4f, -3.5f),
    Vec3(-1.7f, 3.0f, -7.5f),
    Vec3(1.3f, -2.0f, -2.5f),
    Vec3(1.5f, 2.0f, -2.5f),
    Vec3(1.5f, 0.2f, -1.5f),
    Vec3(-1.3f, 1.0f, -1.5f)
)

lateinit var shader: Shader

const val WIDTH = 640
const val HEIGHT = 480

fun main() {
    println("Hello LWJGL " + Version.getVersion() + "!")

    init()
    loop()

    glfwFreeCallbacks(window)
    glfwDestroyWindow(window)

    glfwTerminate()
    glfwSetErrorCallback(null)!!.free()
}

fun init() {
    GLFWErrorCallback.createPrint(System.err).set()

    check(glfwInit()) { "Unable to initialize GLFW" }

    glfwDefaultWindowHints()
    glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE)
    glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE)

    window = glfwCreateWindow(WIDTH, HEIGHT, "Hello World!", NULL, NULL)
    if (window == NULL) throw RuntimeException("Failed to create the GLFW window")

    glfwSetKeyCallback(window) { window: Long, key: Int, scancode: Int, action: Int, mods: Int ->
        if (key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE) glfwSetWindowShouldClose(window, true)
    }

    // Get the thread stack and push a new frame
    stackPush().use { stack ->
        val pWidth = stack.mallocInt(1) // int*
        val pHeight = stack.mallocInt(1) // int*

        // Get the window size passed to glfwCreateWindow
        glfwGetWindowSize(window, pWidth, pHeight)

        // Get the resolution of the primary monitor
        val vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor())

        // Center the window
        glfwSetWindowPos(window, (vidmode!!.width() - pWidth[0]) / 2, (vidmode.height() - pHeight[0]) / 2)

        glfwMakeContextCurrent(window)
        glfwSwapInterval(1)
        glfwShowWindow(window)
    }
}

private fun loop() {
    GL.createCapabilities()
    glClearColor(0f, 0f, 0f, 1.0f)

    shader = Shader("shaders/vertex_shader.gl", "shaders/fragment_shader.gl")
    shader.use()

    val vao = glGenVertexArrays()
    glBindVertexArray(vao)

    val vbo1 = glGenBuffers()
    glBindBuffer(GL_ARRAY_BUFFER, vbo1)
    glBufferData(GL_ARRAY_BUFFER, vertices, GL_STATIC_DRAW)
    glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0)
    glEnableVertexAttribArray(0)
    glBindBuffer(GL_ARRAY_BUFFER, 0)

    glBindVertexArray(0)

    while (!glfwWindowShouldClose(window)) {
        glfwPollEvents()
        glClear(GL_COLOR_BUFFER_BIT or GL_DEPTH_BUFFER_BIT)

        // Create transformations
        val view = Mat4.MAT4_IDENTITY.translate(Vec3(0.0f, 0.0f, -3.0f))
        val projection: Mat4 = perspective(45.0f, WIDTH / HEIGHT.toFloat(), 0.1f, 100.0f)
        // Get their uniform location
        val modelLoc = glGetUniformLocation(shader.program, "model")
        val viewLoc = glGetUniformLocation(shader.program, "view")
        val projLoc = glGetUniformLocation(shader.program, "projection")
        // Pass the matrices to the shader
        glUniformMatrix4fv(viewLoc, false, createFloatMat4(view.buffer))
        // Note: currently we set the projection matrix each frame, but since the projection matrix rarely changes it's often best practice to set it outside the main loop only once.
        glUniformMatrix4fv(projLoc, false, createFloatMat4(projection.buffer))

        glBindVertexArray(vao)
        for (i in 0..9) {
            // Calculate the model matrix for each object and pass it to shader before drawing
            val model = Mat4.MAT4_IDENTITY.translate(cubePositions[i].unitVector)
//            val angle = 20.0f * i
//            model = model.multiply(rotate(angle, Vec3(1.0f, 0.3f, 0.5f)))
            glUniformMatrix4fv(modelLoc, false, createFloatMat4(model.buffer))

            glDrawArrays(GL_TRIANGLES, 0, vertices.capacity())
        }
        glBindVertexArray(0)

        glfwSwapBuffers(window)
    }
}

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

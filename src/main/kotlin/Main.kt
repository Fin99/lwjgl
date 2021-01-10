import org.lwjgl.BufferUtils
import org.lwjgl.Version
import org.lwjgl.glfw.Callbacks.glfwFreeCallbacks
import org.lwjgl.glfw.GLFW.*
import org.lwjgl.glfw.GLFWErrorCallback
import org.lwjgl.opengl.GL
import org.lwjgl.opengl.GL20.*
import org.lwjgl.opengl.GL30.glBindVertexArray
import org.lwjgl.opengl.GL30.glGenVertexArrays
import org.lwjgl.system.MemoryStack.stackPush
import org.lwjgl.system.MemoryUtil.NULL
import java.nio.FloatBuffer
import java.nio.IntBuffer


private var window: Long = 0

var vertices: FloatBuffer = createFloatBuffer(
    floatArrayOf(
        0.5f, 0.5f, 0.0f,  // Верхний правый угол
        0.5f, -0.5f, 0.0f,  // Нижний правый угол
        -0.5f, -0.5f, 0.0f,  // Нижний левый угол
        -0.5f, 0.5f, 0.0f   // Верхний левый угол
    )
)

var indices = createIntBuffer(
    intArrayOf(
        0, 1, 3,   // Первый треугольник
        1, 2, 3    // Второй треугольник
    )
)


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

    window = glfwCreateWindow(640, 480, "Hello World!", NULL, NULL)
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

    val shader = Shader("shaders/vertex_shader.gl", "shaders/fragment_shader.gl")
    shader.use()

    val vao = glGenVertexArrays()
    glBindVertexArray(vao)

    val vbo1 = glGenBuffers()
    glBindBuffer(GL_ARRAY_BUFFER, vbo1)
    glBufferData(GL_ARRAY_BUFFER, vertices, GL_STATIC_DRAW)
    glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0)
    glEnableVertexAttribArray(0)
    glBindBuffer(GL_ARRAY_BUFFER, 0)

    val vbo2 = glGenBuffers()
    glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, vbo2)
    glBufferData(GL_ELEMENT_ARRAY_BUFFER, indices, GL_STATIC_DRAW)
    glBindBuffer(GL_ARRAY_BUFFER, 0)

    glBindVertexArray(0)
//    glPolygonMode(GL_FRONT_AND_BACK, GL_LINE)

    while (!glfwWindowShouldClose(window)) {

        glClear(GL_COLOR_BUFFER_BIT or GL_DEPTH_BUFFER_BIT)

        glBindVertexArray(vao)
        glDrawElements(GL_TRIANGLES, indices.capacity(), GL_UNSIGNED_INT, 0)
        glBindVertexArray(0)

        glfwSwapBuffers(window)

        glfwPollEvents()
    }
}

fun createFloatBuffer(data: FloatArray): FloatBuffer {
    val buffer = BufferUtils.createFloatBuffer(data.size)
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

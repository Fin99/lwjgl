import com.hackoeur.jglm.Mat4
import com.hackoeur.jglm.Matrices.perspective
import com.hackoeur.jglm.Vec3
import com.hackoeur.jglm.Vec4
import com.hackoeur.jglm.support.FastMath.*
import org.lwjgl.BufferUtils
import org.lwjgl.Version
import org.lwjgl.glfw.Callbacks.glfwFreeCallbacks
import org.lwjgl.glfw.GLFW.*
import org.lwjgl.glfw.GLFWErrorCallback
import org.lwjgl.opengl.GL
import org.lwjgl.opengl.GL30.*
import org.lwjgl.stb.STBImage.stbi_failure_reason
import org.lwjgl.stb.STBImage.stbi_load
import org.lwjgl.system.MemoryStack.stackPush
import org.lwjgl.system.MemoryUtil.NULL
import java.io.File
import java.io.IOException
import java.nio.ByteBuffer
import java.nio.FloatBuffer
import java.nio.IntBuffer
import kotlin.math.pow


private var window: Long = 0

var vertices: FloatBuffer = createFloatBuffer(
    floatArrayOf(
        -0.5f, -0.5f, -0.5f, 0.0f, 0.0f,
        0.5f, -0.5f, -0.5f, 1.0f, 0.0f,
        0.5f, 0.5f, -0.5f, 1.0f, 1.0f,
        0.5f, 0.5f, -0.5f, 1.0f, 1.0f,
        -0.5f, 0.5f, -0.5f, 0.0f, 1.0f,
        -0.5f, -0.5f, -0.5f, 0.0f, 0.0f,

        -0.5f, -0.5f, 0.5f, 0.0f, 0.0f,
        0.5f, -0.5f, 0.5f, 1.0f, 0.0f,
        0.5f, 0.5f, 0.5f, 1.0f, 1.0f,
        0.5f, 0.5f, 0.5f, 1.0f, 1.0f,
        -0.5f, 0.5f, 0.5f, 0.0f, 1.0f,
        -0.5f, -0.5f, 0.5f, 0.0f, 0.0f,

        -0.5f, 0.5f, 0.5f, 1.0f, 0.0f,
        -0.5f, 0.5f, -0.5f, 1.0f, 1.0f,
        -0.5f, -0.5f, -0.5f, 0.0f, 1.0f,
        -0.5f, -0.5f, -0.5f, 0.0f, 1.0f,
        -0.5f, -0.5f, 0.5f, 0.0f, 0.0f,
        -0.5f, 0.5f, 0.5f, 1.0f, 0.0f,

        0.5f, 0.5f, 0.5f, 1.0f, 0.0f,
        0.5f, 0.5f, -0.5f, 1.0f, 1.0f,
        0.5f, -0.5f, -0.5f, 0.0f, 1.0f,
        0.5f, -0.5f, -0.5f, 0.0f, 1.0f,
        0.5f, -0.5f, 0.5f, 0.0f, 0.0f,
        0.5f, 0.5f, 0.5f, 1.0f, 0.0f,

        -0.5f, -0.5f, -0.5f, 0.0f, 1.0f,
        0.5f, -0.5f, -0.5f, 1.0f, 1.0f,
        0.5f, -0.5f, 0.5f, 1.0f, 0.0f,
        0.5f, -0.5f, 0.5f, 1.0f, 0.0f,
        -0.5f, -0.5f, 0.5f, 0.0f, 0.0f,
        -0.5f, -0.5f, -0.5f, 0.0f, 1.0f,

        -0.5f, 0.5f, -0.5f, 0.0f, 1.0f,
        0.5f, 0.5f, -0.5f, 1.0f, 1.0f,
        0.5f, 0.5f, 0.5f, 1.0f, 0.0f,
        0.5f, 0.5f, 0.5f, 1.0f, 0.0f,
        -0.5f, 0.5f, 0.5f, 0.0f, 0.0f,
        -0.5f, 0.5f, -0.5f, 0.0f, 1.0f
    )
)
var camera = Camera(Vec3(0.0f, 0.0f, 3.0f))
var keys = BooleanArray(1024) { false }
var firstMouse = true

var deltaTime = 0.0f
var lastFrame = 0.0f
var lastX = 400.0
var lastY = 300.0

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

const val WIDTH = 800
const val HEIGHT = 600

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

    println("W $GLFW_KEY_W")
    println("A $GLFW_KEY_A")
    println("S $GLFW_KEY_S")
    println("D $GLFW_KEY_D")
    glfwSetKeyCallback(window) { window: Long, key: Int, scancode: Int, action: Int, mods: Int ->
        println(key)
        //cout << key << endl;
        if (key == GLFW_KEY_ESCAPE && action == GLFW_PRESS)
            glfwSetWindowShouldClose(window, true)
        if (key in 0..1023) {
            if (action == GLFW_PRESS)
                keys[key] = true
            else if (action == GLFW_RELEASE)
                keys[key] = false
        }
    }

    glfwSetCursorPosCallback(window) { l: Long, xPos: Double, yPos: Double ->
        if (firstMouse) {
            lastX = xPos
            lastY = yPos
            firstMouse = false
        }

        val xoffset = xPos - lastX
        val yoffset = lastY - yPos // Reversed since y-coordinates go from bottom to left


        lastX = xPos
        lastY = yPos

        camera.processMouseMovement(xoffset.toFloat(), yoffset.toFloat())
    }

    glfwSetScrollCallback(window) { _: Long, _: Double, yOffset: Double ->
        camera.processMouseScroll(yOffset.toFloat())
    }


    glfwSetInputMode(window, GLFW_CURSOR, GLFW_CURSOR_DISABLED)

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
    glVertexAttribPointer(0, 3, GL_FLOAT, false, 5 * 4, 0)
    glEnableVertexAttribArray(0)
    glVertexAttribPointer(1, 2, GL_FLOAT, false, 5 * 4, 3 * 4)
    glEnableVertexAttribArray(1)
    glBindBuffer(GL_ARRAY_BUFFER, 0)
    glBindVertexArray(0)

    val texture = glGenTextures()
    glBindTexture(GL_TEXTURE_2D, texture)
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT)
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT)
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR)
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR)


    val imagePath = object {}.javaClass.getResource("texture/smile.png")
    val imageFile = File(imagePath.toURI())
//    val imageBuffer = createByteBuffer(imageFile.readBytes())
//    var image: ByteBuffer
    var imageWidth: Int
    var imageHeight: Int
//    var imageComponents: Int

//    stackPush().use { stack ->
//        val width = stack.mallocInt(1)
//        val height = stack.mallocInt(1)
//        val components = stack.mallocInt(1)
//
//        if (!stbi_info_from_memory(imageBuffer, width, height, components)) {
//            throw RuntimeException("Failed to read image information: " + stbi_failure_reason())
//        } else {
//            println("OK with reason: " + stbi_failure_reason())
//        }
//        // Decode the image
//        image = stbi_load_from_memory(imageBuffer, width, height, components, 0)!!
//        imageWidth = width[0]
//        imageHeight = height[0]
//        imageComponents = components[0]
//    }

    val w = BufferUtils.createIntBuffer(1)
    val h = BufferUtils.createIntBuffer(1)
    val comp = BufferUtils.createIntBuffer(1)
    val buffer: ByteBuffer = stbi_load(imageFile.toString(), w, h, comp, 3) ?: throw IOException(stbi_failure_reason())

    imageWidth = w[0]
    imageHeight = h[0]

    println("Width ${imageWidth}, Height ${imageHeight}, Data ${0}")


    glTexImage2D(
        GL_TEXTURE_2D,
        0,
        GL_RGB,
        imageWidth,
        imageHeight,
        0,
        GL_RGB,
        GL_UNSIGNED_BYTE,
        createByteBuffer(buffer)
    )
    glGenerateMipmap(GL_TEXTURE_2D)
    glBindTexture(GL_TEXTURE_2D, 0)

    glEnable(GL_DEPTH_TEST)

    while (!glfwWindowShouldClose(window)) {
        val currentFrame = glfwGetTime().toFloat()
        deltaTime = currentFrame - lastFrame
        lastFrame = currentFrame

        glfwPollEvents()
        doMovement()
        glClear(GL_COLOR_BUFFER_BIT or GL_DEPTH_BUFFER_BIT)

        glActiveTexture(GL_TEXTURE0)
        glBindTexture(GL_TEXTURE_2D, texture)
        glUniform1i(glGetUniformLocation(shader.program, "ourTexture"), 0)

        // Create transformations
        val view = camera.getViewMatrix()
        val projection: Mat4 = perspective(camera.zoom, WIDTH / HEIGHT.toFloat(), 0.1f, 100.0f)
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
            val angle = 20.0f * i
            val model = Mat4.MAT4_IDENTITY
                .translate(cubePositions[i])
                .rotate(toRadians(angle.toDouble()).toFloat(), Vec3(1.0f, 0.3f, 0.5f))
//                .scale(Vec3(0.1f * i, 0.3f * i, 0.2f * i))
            glUniformMatrix4fv(modelLoc, false, createFloatMat4(model.buffer))

            glDrawArrays(GL_TRIANGLES, 0, 36)
        }
        glBindVertexArray(0)

        glfwSwapBuffers(window)
    }
}

fun doMovement() {
    // Camera controls
    if (keys[GLFW_KEY_W]) camera.processKeyboard(CameraMovement.FORWARD, deltaTime)
    if (keys[GLFW_KEY_S]) camera.processKeyboard(CameraMovement.BACKWARD, deltaTime)
    if (keys[GLFW_KEY_A]) camera.processKeyboard(CameraMovement.LEFT, deltaTime)
    if (keys[GLFW_KEY_D]) camera.processKeyboard(CameraMovement.RIGHT, deltaTime)
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

fun Mat4.rotate(phi: Float, axis: Vec3): Mat4 {
    val cos = cos(phi.toDouble()).toFloat()
    val oneMinusCos = 1.0f - cos
    val sin = sin(phi.toDouble()).toFloat()
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
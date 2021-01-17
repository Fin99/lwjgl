import com.hackoeur.jglm.Mat4
import com.hackoeur.jglm.Matrices.perspective
import com.hackoeur.jglm.Matrices.rotate
import com.hackoeur.jglm.Vec3
import com.hackoeur.jglm.support.FastMath.toRadians
import org.lwjgl.Version
import org.lwjgl.glfw.Callbacks.glfwFreeCallbacks
import org.lwjgl.glfw.GLFW.*
import org.lwjgl.glfw.GLFWErrorCallback
import org.lwjgl.opengl.GL
import org.lwjgl.opengl.GL30.*
import org.lwjgl.system.MemoryStack.stackPush
import org.lwjgl.system.MemoryUtil.NULL
import java.nio.FloatBuffer


private var window: Long = 0

var vertices: FloatBuffer = createFloatBuffer(
    floatArrayOf(
        -0.5f, -0.5f, -0.5f, 0.0f, 0.0f, -1.0f, 0.0f, 0.0f,
        0.5f, -0.5f, -0.5f, 0.0f, 0.0f, -1.0f, 1.0f, 0.0f,
        0.5f, 0.5f, -0.5f, 0.0f, 0.0f, -1.0f, 1.0f, 1.0f,
        0.5f, 0.5f, -0.5f, 0.0f, 0.0f, -1.0f, 1.0f, 1.0f,
        -0.5f, 0.5f, -0.5f, 0.0f, 0.0f, -1.0f, 0.0f, 1.0f,
        -0.5f, -0.5f, -0.5f, 0.0f, 0.0f, -1.0f, 0.0f, 0.0f,

        -0.5f, -0.5f, 0.5f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f,
        0.5f, -0.5f, 0.5f, 0.0f, 0.0f, 1.0f, 1.0f, 0.0f,
        0.5f, 0.5f, 0.5f, 0.0f, 0.0f, 1.0f, 1.0f, 1.0f,
        0.5f, 0.5f, 0.5f, 0.0f, 0.0f, 1.0f, 1.0f, 1.0f,
        -0.5f, 0.5f, 0.5f, 0.0f, 0.0f, 1.0f, 0.0f, 1.0f,
        -0.5f, -0.5f, 0.5f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f,

        -0.5f, 0.5f, 0.5f, -1.0f, 0.0f, 0.0f, 1.0f, 0.0f,
        -0.5f, 0.5f, -0.5f, -1.0f, 0.0f, 0.0f, 1.0f, 1.0f,
        -0.5f, -0.5f, -0.5f, -1.0f, 0.0f, 0.0f, 0.0f, 1.0f,
        -0.5f, -0.5f, -0.5f, -1.0f, 0.0f, 0.0f, 0.0f, 1.0f,
        -0.5f, -0.5f, 0.5f, -1.0f, 0.0f, 0.0f, 0.0f, 0.0f,
        -0.5f, 0.5f, 0.5f, -1.0f, 0.0f, 0.0f, 1.0f, 0.0f,

        0.5f, 0.5f, 0.5f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f,
        0.5f, 0.5f, -0.5f, 1.0f, 0.0f, 0.0f, 1.0f, 1.0f,
        0.5f, -0.5f, -0.5f, 1.0f, 0.0f, 0.0f, 0.0f, 1.0f,
        0.5f, -0.5f, -0.5f, 1.0f, 0.0f, 0.0f, 0.0f, 1.0f,
        0.5f, -0.5f, 0.5f, 1.0f, 0.0f, 0.0f, 0.0f, 0.0f,
        0.5f, 0.5f, 0.5f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f,

        -0.5f, -0.5f, -0.5f, 0.0f, -1.0f, 0.0f, 0.0f, 1.0f,
        0.5f, -0.5f, -0.5f, 0.0f, -1.0f, 0.0f, 1.0f, 1.0f,
        0.5f, -0.5f, 0.5f, 0.0f, -1.0f, 0.0f, 1.0f, 0.0f,
        0.5f, -0.5f, 0.5f, 0.0f, -1.0f, 0.0f, 1.0f, 0.0f,
        -0.5f, -0.5f, 0.5f, 0.0f, -1.0f, 0.0f, 0.0f, 0.0f,
        -0.5f, -0.5f, -0.5f, 0.0f, -1.0f, 0.0f, 0.0f, 1.0f,

        -0.5f, 0.5f, -0.5f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f,
        0.5f, 0.5f, -0.5f, 0.0f, 1.0f, 0.0f, 1.0f, 1.0f,
        0.5f, 0.5f, 0.5f, 0.0f, 1.0f, 0.0f, 1.0f, 0.0f,
        0.5f, 0.5f, 0.5f, 0.0f, 1.0f, 0.0f, 1.0f, 0.0f,
        -0.5f, 0.5f, 0.5f, 0.0f, 1.0f, 0.0f, 0.0f, 0.0f,
        -0.5f, 0.5f, -0.5f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f
    )
)
var camera = Camera(Vec3(0.0f, 0.0f, 3.0f))
var keys = BooleanArray(1024) { false }
var firstMouse = true

var deltaTime = 0.0f
var lastFrame = 0.0f
var lastX = 400.0
var lastY = 300.0

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

var lightPositions: Array<Vec3> = arrayOf(
    Vec3(0.7f, 0.2f, 2.0f),
    Vec3(2.3f, -3.3f, -4.0f),
    Vec3(-4.0f, 2.0f, -12.0f),
    Vec3(0.0f, 0.0f, -3.0f),
)

lateinit var cubeShader: Shader
lateinit var lightShader: Shader

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
    glEnable(GL_DEPTH_TEST)

    cubeShader = Shader("shaders/cube/vertex_shader.gl", "shaders/cube/fragment_shader.gl")

    val ourModel = Model("untitled.obj")

    while (!glfwWindowShouldClose(window)) {
        val currentFrame = glfwGetTime().toFloat()
        deltaTime = currentFrame - lastFrame
        lastFrame = currentFrame

        processInput()
        glClearColor(0.1f, 0.1f, 0.1f, 1.0f)
        glClear(GL_COLOR_BUFFER_BIT or GL_DEPTH_BUFFER_BIT)

        cubeShader.use()
        // view/projection transformations
        var projection =
            perspective(camera.zoom, WIDTH / HEIGHT.toFloat(), 0.1f, 100.0f)
        var view = camera.getViewMatrix()
        cubeShader.setMat4("projection", projection)
        cubeShader.setMat4("view", view)

        var model = Mat4.MAT4_IDENTITY
        model = model.translate(cubePositions[0])
        val angle = 20.0 * 0
        model = model.multiply(rotate(toRadians(angle).toFloat(), Vec3(1.0f, 0.0f, 0.0f)))
        model = model.multiply(rotate(toRadians(angle * 0.3).toFloat(), Vec3(0.0f, 1.0f, 0.0f)))
        model = model.multiply(rotate(toRadians(angle * 0.5).toFloat(), Vec3(0.0f, 0.0f, 1.0f)))
        cubeShader.setMat4("model", model)

        ourModel.draw(cubeShader)

        glfwSwapBuffers(window)
        glfwPollEvents()
    }
}


fun processInput() {
    // Camera controls
    if (keys[GLFW_KEY_W]) camera.processKeyboard(CameraMovement.FORWARD, deltaTime)
    if (keys[GLFW_KEY_S]) camera.processKeyboard(CameraMovement.BACKWARD, deltaTime)
    if (keys[GLFW_KEY_A]) camera.processKeyboard(CameraMovement.LEFT, deltaTime)
    if (keys[GLFW_KEY_D]) camera.processKeyboard(CameraMovement.RIGHT, deltaTime)
    if (keys[GLFW_KEY_Q]) camera.processKeyboard(CameraMovement.RIGHT, deltaTime)
//    if (keys[GLFW_KEY_E]) camera.processKeyboard(CameraMovement.RIGHT, deltaTime)
}
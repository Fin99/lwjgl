import com.hackoeur.jglm.Mat4
import com.hackoeur.jglm.Matrices
import com.hackoeur.jglm.Matrices.perspective
import com.hackoeur.jglm.Vec3
import com.hackoeur.jglm.support.FastMath
import org.lwjgl.Version
import org.lwjgl.glfw.Callbacks.glfwFreeCallbacks
import org.lwjgl.glfw.GLFW.*
import org.lwjgl.glfw.GLFWErrorCallback
import org.lwjgl.opengl.GL
import org.lwjgl.opengl.GL30.*
import org.lwjgl.system.MemoryStack.stackPush
import org.lwjgl.system.MemoryUtil.NULL
import java.nio.FloatBuffer
import kotlin.math.sin


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

var cubePositions = Vec3(-2.0f, 0.0f, -5.0f)

var lightPositions: Array<Vec3> = arrayOf(
    Vec3(-2f, 1f, -5f)
)

lateinit var blenderShader: Shader
lateinit var cubeShader: Shader
lateinit var lightShader: Shader
lateinit var snowShader: Shader

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

    blenderShader = Shader("shaders/blender/vertex_shader.gl", "shaders/blender/fragment_shader.gl")
    cubeShader = Shader("shaders/cube/vertex_shader.gl", "shaders/cube/fragment_shader.gl")
    lightShader = Shader("shaders/light/vertex_shader.gl", "shaders/light/fragment_shader.gl")
    snowShader = Shader(
        "shaders/snow/vertex_shader.gl",
        "shaders/snow/fragment_shader.gl"
    )

    val cubeVao = glGenVertexArrays()
    val vbo1 = glGenBuffers()

    glBindVertexArray(cubeVao)
    glBindBuffer(GL_ARRAY_BUFFER, vbo1)
    glBufferData(GL_ARRAY_BUFFER, vertices, GL_STATIC_DRAW)
    glVertexAttribPointer(0, 3, GL_FLOAT, false, 8 * 4, 0)
    glEnableVertexAttribArray(0)
    glVertexAttribPointer(1, 3, GL_FLOAT, false, 8 * 4, 3 * 4)
    glEnableVertexAttribArray(1)
    glVertexAttribPointer(2, 2, GL_FLOAT, false, 8 * 4, 6 * 4)
    glEnableVertexAttribArray(2)
    glBindBuffer(GL_ARRAY_BUFFER, 0)

    val lightVao = glGenVertexArrays()
    val vbo = glGenBuffers()

    glBindVertexArray(lightVao)
    glBindBuffer(GL_ARRAY_BUFFER, vbo)
    glBufferData(GL_ARRAY_BUFFER, vertices, GL_STATIC_DRAW)
    glVertexAttribPointer(0, 3, GL_FLOAT, false, 8 * 4, 0)
    glEnableVertexAttribArray(0)
    glBindBuffer(GL_ARRAY_BUFFER, 0)

    val diffuseMap: Int = loadTexture(object {}.javaClass.getResource("textures/container2.png").path)
    val specularMap: Int = loadTexture(object {}.javaClass.getResource("textures/container2_specular.png").path)

    cubeShader.use()
    cubeShader.setInt("material.diffuse", 0)
    cubeShader.setInt("material.specular", 1)

    val blenderModel = Model("blender.obj")
    val torModel = Model("tor.obj")
    val sphereModel = Model("sphere.obj")
    val monkeyModel = Model("monkey.obj")
    val snowModel = Model("sphere.obj")

    while (!glfwWindowShouldClose(window)) {
        val currentFrame = glfwGetTime().toFloat()
        deltaTime = currentFrame - lastFrame
        lastFrame = currentFrame

        processInput()
        glClearColor(0.1f, 0.1f, 0.1f, 1.0f)
        glClear(GL_COLOR_BUFFER_BIT or GL_DEPTH_BUFFER_BIT)

        cubeDraw(cubeVao, diffuseMap, specularMap)

        lightDraw(lightVao)

        blenderDraw(blenderModel, Vec3(0f, 0f, -5f))
        blenderDraw(torModel, Vec3(5f, 0f, -5f))
        blenderDraw(sphereModel, Vec3(5f, 5f, -5f))
        blenderDraw(monkeyModel, Vec3(0f, 5f, -5f))

        for (i in 10..1000 step 10)
            snowDraw(snowModel, (i + sin((time + i).toDouble()) * 5).toInt())

        glfwSwapBuffers(window)
        glfwPollEvents()
    }
}

var time = System.currentTimeMillis()

fun snowDraw(snowModel: Model, initHeight: Int) {
    snowShader.use()
    snowShader.setFloat("height", (initHeight - (System.currentTimeMillis() - time) * 0.01).toFloat())
    snowShader.setInt("size", 10)

    // view/projection transformations
    var projection =
        perspective(camera.zoom, WIDTH / HEIGHT.toFloat(), 0.1f, 100.0f)
    var view = camera.getViewMatrix()
    snowShader.setMat4("projection", projection)
    snowShader.setMat4("view", view)

    val translate = Vec3(0f, 0f, 0f)
    val scale = Vec3(0.005f, 0.005f, 0.005f)
    var model = Mat4.MAT4_IDENTITY
    model = model.translate(translate)
    model = model.scale(scale)
    snowShader.setMat4("model", model)

    snowModel.draw(snowShader, true)
}

fun cubeDraw(cubeVao: Int, diffuseMap: Int, specularMap: Int) {
    cubeShader.use()
    cubeShader.setVec3("viewPos", camera.position)
    cubeShader.setFloat("material.shininess", 32.0f)

    cubeShader.setVec3("dirLight.direction", -0.2f, -1.0f, -0.3f)
    cubeShader.setVec3("dirLight.ambient", 0.05f, 0.05f, 0.05f)
    cubeShader.setVec3("dirLight.diffuse", 0.4f, 0.4f, 0.4f)
    cubeShader.setVec3("dirLight.specular", 0.5f, 0.5f, 0.5f)
    // point light 1
    cubeShader.setVec3("pointLights[0].position", lightPositions[0])
    cubeShader.setVec3("pointLights[0].ambient", 0.05f, 0.05f, 0.05f)
    cubeShader.setVec3("pointLights[0].diffuse", 0.8f, 0.8f, 0.8f)
    cubeShader.setVec3("pointLights[0].specular", 1.0f, 1.0f, 1.0f)
    cubeShader.setFloat("pointLights[0].constant", 1.0f)
    cubeShader.setFloat("pointLights[0].linear", 0.09f)
    cubeShader.setFloat("pointLights[0].quadratic", 0.032f)
    // spotLight
    cubeShader.setVec3("spotLight.position", camera.position)
    cubeShader.setVec3("spotLight.direction", camera.front)
    cubeShader.setVec3("spotLight.ambient", 0.0f, 0.0f, 0.0f)
    cubeShader.setVec3("spotLight.diffuse", 1.0f, 1.0f, 1.0f)
    cubeShader.setVec3("spotLight.specular", 1.0f, 1.0f, 1.0f)
    cubeShader.setFloat("spotLight.constant", 1.0f)
    cubeShader.setFloat("spotLight.linear", 0.09f)
    cubeShader.setFloat("spotLight.quadratic", 0.032f)
    cubeShader.setFloat("spotLight.cutOff", FastMath.cos(FastMath.toRadians(12.5)).toFloat())
    cubeShader.setFloat("spotLight.outerCutOff", FastMath.cos(FastMath.toRadians(15.0)).toFloat())

    // view/projection transformations
    var projection =
        perspective(camera.zoom, WIDTH / HEIGHT.toFloat(), 0.1f, 100.0f)
    var view = camera.getViewMatrix()
    cubeShader.setMat4("projection", projection)
    cubeShader.setMat4("view", view)

    // bind diffuse map
    glActiveTexture(GL_TEXTURE0)
    glBindTexture(GL_TEXTURE_2D, diffuseMap)
    // bind specular map
    glActiveTexture(GL_TEXTURE1)
    glBindTexture(GL_TEXTURE_2D, specularMap)

// render containers
    glBindVertexArray(cubeVao)

    var model = Mat4.MAT4_IDENTITY
    model = model.translate(cubePositions)
    val angle = 20.0 * 1

    model = model.multiply(Matrices.rotate(FastMath.toRadians(angle).toFloat(), Vec3(1.0f, 0.0f, 0.0f)))
    model = model.multiply(Matrices.rotate(FastMath.toRadians(angle * 0.3).toFloat(), Vec3(0.0f, 1.0f, 0.0f)))
    model = model.multiply(Matrices.rotate(FastMath.toRadians(angle * 0.5).toFloat(), Vec3(0.0f, 0.0f, 1.0f)))
    cubeShader.setMat4("model", model)

    glDrawArrays(GL_TRIANGLES, 0, 36)
}

fun lightDraw(lightVao: Int) {
    var projection =
        perspective(camera.zoom, WIDTH / HEIGHT.toFloat(), 0.1f, 100.0f)
    var view = camera.getViewMatrix()
    // also draw the lamp object(s)
    lightShader.use()
    lightShader.setMat4("projection", projection)
    lightShader.setMat4("view", view)

    // we now draw as many light bulbs as we have point lights.
    glBindVertexArray(lightVao)
    for (i in 0..0) {
        var model = Mat4.MAT4_IDENTITY
        model = model.translate(lightPositions[i])
        model = model.scale(Vec3(0.2f, 0.2f, 0.2f)) // Make it a smaller cube
        lightShader.setMat4("model", model)
        glDrawArrays(GL_TRIANGLES, 0, 36)
    }
}

fun blenderDraw(blenderModel: Model, translate: Vec3, scale: Vec3 = Vec3(1f, 1f, 1f)) {
    blenderShader.use()
    // view/projection transformations
    var projection =
        perspective(camera.zoom, WIDTH / HEIGHT.toFloat(), 0.1f, 100.0f)
    var view = camera.getViewMatrix()
    blenderShader.setMat4("projection", projection)
    blenderShader.setMat4("view", view)

    var model = Mat4.MAT4_IDENTITY
    model = model.translate(translate)
    model = model.scale(scale)
    blenderShader.setMat4("model", model)

    blenderModel.draw(blenderShader)
}


fun processInput() {
    // Camera controls
    if (keys[GLFW_KEY_W]) camera.processKeyboard(CameraMovement.FORWARD, deltaTime)
    if (keys[GLFW_KEY_S]) camera.processKeyboard(CameraMovement.BACKWARD, deltaTime)
    if (keys[GLFW_KEY_A]) camera.processKeyboard(CameraMovement.LEFT, deltaTime)
    if (keys[GLFW_KEY_D]) camera.processKeyboard(CameraMovement.RIGHT, deltaTime)
    if (keys[GLFW_KEY_Q]) camera.processKeyboard(CameraMovement.RIGHT, deltaTime)
}
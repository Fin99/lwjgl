import com.hackoeur.jglm.Mat4
import com.hackoeur.jglm.Matrices.perspective
import com.hackoeur.jglm.Matrices.rotate
import com.hackoeur.jglm.Vec3
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
    lightShader = Shader("shaders/light/vertex_shader.gl", "shaders/light/fragment_shader.gl")

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

    val diffuseMap: Int = loadTexture("textures/container2.png")
    val specularMap: Int = loadTexture("textures/container2_specular.png")

    cubeShader.use()
    cubeShader.setInt("material.diffuse", 0)
    cubeShader.setInt("material.specular", 1)

    while (!glfwWindowShouldClose(window)) {
        val currentFrame = glfwGetTime().toFloat()
        deltaTime = currentFrame - lastFrame
        lastFrame = currentFrame

        processInput()
        glClearColor(0.1f, 0.1f, 0.1f, 1.0f)
        glClear(GL_COLOR_BUFFER_BIT or GL_DEPTH_BUFFER_BIT)

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
        // point light 2
        cubeShader.setVec3("pointLights[1].position", lightPositions[1])
        cubeShader.setVec3("pointLights[1].ambient", 0.05f, 0.05f, 0.05f)
        cubeShader.setVec3("pointLights[1].diffuse", 0.8f, 0.8f, 0.8f)
        cubeShader.setVec3("pointLights[1].specular", 1.0f, 1.0f, 1.0f)
        cubeShader.setFloat("pointLights[1].constant", 1.0f)
        cubeShader.setFloat("pointLights[1].linear", 0.09f)
        cubeShader.setFloat("pointLights[1].quadratic", 0.032f)
        // point light 3
        cubeShader.setVec3("pointLights[2].position", lightPositions[2])
        cubeShader.setVec3("pointLights[2].ambient", 0.05f, 0.05f, 0.05f)
        cubeShader.setVec3("pointLights[2].diffuse", 0.8f, 0.8f, 0.8f)
        cubeShader.setVec3("pointLights[2].specular", 1.0f, 1.0f, 1.0f)
        cubeShader.setFloat("pointLights[2].constant", 1.0f)
        cubeShader.setFloat("pointLights[2].linear", 0.09f)
        cubeShader.setFloat("pointLights[2].quadratic", 0.032f)
        // point light 4
        cubeShader.setVec3("pointLights[3].position", lightPositions[3])
        cubeShader.setVec3("pointLights[3].ambient", 0.05f, 0.05f, 0.05f)
        cubeShader.setVec3("pointLights[3].diffuse", 0.8f, 0.8f, 0.8f)
        cubeShader.setVec3("pointLights[3].specular", 1.0f, 1.0f, 1.0f)
        cubeShader.setFloat("pointLights[3].constant", 1.0f)
        cubeShader.setFloat("pointLights[3].linear", 0.09f)
        cubeShader.setFloat("pointLights[3].quadratic", 0.032f)
        // spotLight
        cubeShader.setVec3("spotLight.position", camera.position)
        cubeShader.setVec3("spotLight.direction", camera.front)
        cubeShader.setVec3("spotLight.ambient", 0.0f, 0.0f, 0.0f)
        cubeShader.setVec3("spotLight.diffuse", 1.0f, 1.0f, 1.0f)
        cubeShader.setVec3("spotLight.specular", 1.0f, 1.0f, 1.0f)
        cubeShader.setFloat("spotLight.constant", 1.0f)
        cubeShader.setFloat("spotLight.linear", 0.09f)
        cubeShader.setFloat("spotLight.quadratic", 0.032f)
        cubeShader.setFloat("spotLight.cutOff", cos(toRadians(12.5)).toFloat())
        cubeShader.setFloat("spotLight.outerCutOff", cos(toRadians(15.0)).toFloat())

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
        for (i in 0..9) {
            // calculate the model matrix for each object and pass it to shader before drawing
            var model = Mat4.MAT4_IDENTITY
            model = model.translate(cubePositions[i])
            val angle = 20.0 * i

//            model = model.rotate(toRadians(angle).toFloat(), Vec3(1.0f, 0.3f, 0.5f))
            model = model.multiply(rotate(toRadians(angle).toFloat(), Vec3(1.0f, 0.0f, 0.0f)))
            model = model.multiply(rotate(toRadians(angle*0.3).toFloat(), Vec3(0.0f, 1.0f, 0.0f)))
            model = model.multiply(rotate(toRadians(angle*0.5).toFloat(), Vec3(0.0f, 0.0f, 1.0f)))
            cubeShader.setMat4("model", model)

            glDrawArrays(GL_TRIANGLES, 0, 36)
        }

        // also draw the lamp object(s)
        lightShader.use()
        lightShader.setMat4("projection", projection)
        lightShader.setMat4("view", view)

        // we now draw as many light bulbs as we have point lights.
        glBindVertexArray(lightVao)
        for (i in 0..3) {
            var model = Mat4.MAT4_IDENTITY
            model = model.translate(lightPositions[i])
            model = model.scale(Vec3(0.2f, 0.2f, 0.2f)) // Make it a smaller cube
            lightShader.setMat4("model", model)
            glDrawArrays(GL_TRIANGLES, 0, 36)
        }


        // glfw: swap buffers and poll IO events (keys pressed/released, mouse moved etc.)
        // -------------------------------------------------------------------------------
        glfwSwapBuffers(window)
        glfwPollEvents()

//
//
//        glActiveTexture(GL_TEXTURE0)
//        glBindTexture(GL_TEXTURE_2D, texture)
//        glUniform1i(glGetUniformLocation(cubeShader.program, "ourTexture"), 0)
//
//        // Create transformations
//        val view = camera.getViewMatrix()
//        val projection: Mat4 = perspective(camera.zoom, WIDTH / HEIGHT.toFloat(), 0.1f, 100.0f)
//        // Get their uniform location
//        val modelLoc = glGetUniformLocation(cubeShader.program, "model")
//        val viewLoc = glGetUniformLocation(cubeShader.program, "view")
//        val projLoc = glGetUniformLocation(cubeShader.program, "projection")
//        // Pass the matrices to the shader
//        glUniformMatrix4fv(viewLoc, false, createFloatMat4(view.buffer))
//        // Note: currently we set the projection matrix each frame, but since the projection matrix rarely changes it's often best practice to set it outside the main loop only once.
//        glUniformMatrix4fv(projLoc, false, createFloatMat4(projection.buffer))
//
//        glBindVertexArray(cubeVao)
//        for (i in 0..9) {
//            val angle = 20.0f * i
//            val model = Mat4.MAT4_IDENTITY
//                .translate(cubePositions[i])
//                .rotate(toRadians(angle.toDouble()).toFloat(), Vec3(1.0f, 0.3f, 0.5f))
////                .scale(Vec3(0.1f * i, 0.3f * i, 0.2f * i))
//            glUniformMatrix4fv(modelLoc, false, createFloatMat4(model.buffer))
//
//            glDrawArrays(GL_TRIANGLES, 0, 36)
//        }
//        glBindVertexArray(0)
//
//        glfwSwapBuffers(window)
    }
}

fun loadTexture(texturePath: String): Int {
    val texture = glGenTextures()
    glBindTexture(GL_TEXTURE_2D, texture)
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT)
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT)
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR)
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR)

    val imagePath = object {}.javaClass.getResource(texturePath)
    val imageFile = File(imagePath.toURI())
    val imageWidth: Int
    val imageHeight: Int

    val widthBuffer = BufferUtils.createIntBuffer(1)
    val heightBuffer = BufferUtils.createIntBuffer(1)
    val comp = BufferUtils.createIntBuffer(1)
    val buffer: ByteBuffer =
        stbi_load(imageFile.toString(), widthBuffer, heightBuffer, comp, 3) ?: throw IOException(stbi_failure_reason())

    imageWidth = widthBuffer[0]
    imageHeight = heightBuffer[0]

    println("Texture $texturePath Width $imageWidth, Height $imageHeight")

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

    return texture
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
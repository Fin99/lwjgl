import org.lwjgl.opengl.GL20.*

class Shader(vertexPath: String, fragmentPath: String) {
    var program: Int

    init {
        val vertexShader = glCreateShader(GL_VERTEX_SHADER)
        glShaderSource(vertexShader, object {}.javaClass.getResource(vertexPath).readText())
        glCompileShader(vertexShader)
        println(glGetShaderi(vertexShader, GL_COMPILE_STATUS))
        val fragmentShader = glCreateShader(GL_FRAGMENT_SHADER)
        glShaderSource(fragmentShader, object {}.javaClass.getResource(fragmentPath).readText())
        glCompileShader(fragmentShader)
        println(glGetShaderi(fragmentShader, GL_COMPILE_STATUS))
        program = glCreateProgram()
        glAttachShader(program, vertexShader)
        glAttachShader(program, fragmentShader)
        glLinkProgram(program)
        glDeleteShader(vertexShader)
        glDeleteShader(fragmentShader)
    }

    fun use() {
        glUseProgram(program);
    }
}
import com.hackoeur.jglm.Mat4
import com.hackoeur.jglm.Vec3
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

    fun setInt(name: String, value: Int) {
        glUniform1i(glGetUniformLocation(program, name), value)
    }

    fun setFloat(name: String, value: Float) {
        glUniform1f(glGetUniformLocation(program, name), value)
    }

    fun setVec3(name: String, value: Vec3) {
        glUniform3fv(glGetUniformLocation(program, name), createFloatBuffer(value.array))
    }

    fun setVec3(name: String, x: Float, y: Float, z: Float) {
        glUniform3fv(glGetUniformLocation(program, name), createFloatBuffer(floatArrayOf(x, y, z)))
    }

    fun setMat4(name: String, value: Mat4) {
        glUniformMatrix4fv(glGetUniformLocation(program, name), false, createFloatMat4(value.buffer))
    }

    fun use() {
        glUseProgram(program)
    }
}
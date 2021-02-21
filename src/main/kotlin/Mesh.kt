import org.lwjgl.opengl.GL15.*
import org.lwjgl.opengl.GL20.glEnableVertexAttribArray
import org.lwjgl.opengl.GL20.glVertexAttribPointer
import org.lwjgl.opengl.GL30.glBindVertexArray
import org.lwjgl.opengl.GL30.glGenVertexArrays
import org.lwjgl.opengl.GL31.glDrawArraysInstanced
import org.lwjgl.opengl.GL31.glDrawElementsInstanced
import kotlin.properties.Delegates

class Mesh(
    var vertices: List<Vertex>,
    var indices: List<Int>,
    var textures: List<Texture>,
) {
    init {
        setupMesh()
    }

    var vao: Int? = null
    var vbo: Int? = null
    var ebo: Int? = null

    fun draw(shader: Shader) {
        var diffuseNr = 1
        var specularNr = 1
        for (i in textures.indices) {
            glActiveTexture(GL_TEXTURE0 + i) // активируем текстурный блок, до привязки
            // получаем номер текстуры
            var name = textures[i].type
            var number: Int
            if (name == "texture_diffuse")
                number = diffuseNr++ // передаем unsigned int в stream
            else if (name == "texture_specular")
                number = specularNr++ // передаем unsigned int в stream
            else throw IllegalStateException("Invalid texture type ${textures[i].type}")

            shader.setFloat("material.$name$number", i.toFloat())
            glBindTexture(GL_TEXTURE_2D, textures[i].id)
        }
        glActiveTexture(GL_TEXTURE0)

        // отрисовывем полигональную сетку
        glBindVertexArray(vao!!)
        glDrawElementsInstanced(GL_TRIANGLES, indices.size, GL_UNSIGNED_INT, 0, 10)
//        glDrawElements(GL_TRIANGLES, indices.size, GL_UNSIGNED_INT, 0)
        glBindVertexArray(0)
    }

    fun setupMesh() {
        vao = glGenVertexArrays()
        vbo = glGenBuffers()
        ebo = glGenBuffers()

        glBindVertexArray(vao!!)
        glBindBuffer(GL_ARRAY_BUFFER, vbo!!)

        glBufferData(GL_ARRAY_BUFFER, createFloatBuffer(vertices), GL_STATIC_DRAW)

        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, ebo!!)
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, createIntBuffer(indices.toIntArray()), GL_STATIC_DRAW)

        // vertex positions
        glEnableVertexAttribArray(0)
        glVertexAttribPointer(0, 3, GL_FLOAT, false, Vertex.sizeOf(), 0)
        // vertex normals
        glEnableVertexAttribArray(1)
        glVertexAttribPointer(1, 3, GL_FLOAT, false, Vertex.sizeOf(), Vertex.offsetNormal())
        // vertex texture coords
        glEnableVertexAttribArray(2)
        glVertexAttribPointer(2, 2, GL_FLOAT, false, Vertex.sizeOf(), Vertex.offsetTexCoords())

        glBindVertexArray(0)
    }
}
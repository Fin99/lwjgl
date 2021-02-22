import com.hackoeur.jglm.Vec3
import org.lwjgl.assimp.*
import org.lwjgl.assimp.Assimp.*
import java.nio.IntBuffer


class Model(path: String) {
    var meshes: MutableList<Mesh> = mutableListOf()
    lateinit var directory: String

    init {
        loadModel(path)
    }

    fun draw(shader: Shader, snow: Boolean = false) {
        meshes.forEach { it.draw(shader, snow) }
    }

    fun loadModel(path: String) {
        val scene: AIScene = aiImportFile(path, aiProcess_Triangulate or aiProcess_FlipUVs)
            ?: throw IllegalStateException("ERROR::ASSIMP::${aiGetErrorString()}")

        directory = if(path.contains('/')) path.substring(0, path.indexOfLast { it == '/' }) else ""

        processNode(scene.mRootNode()!!, scene)
    }

    fun processNode(node: AINode, scene: AIScene) {
        // обработать все полигональные сетки в узле(если есть)
        for (i in 0 until node.mNumMeshes()) {
            val mesh = AIMesh.create(scene.mMeshes()!![node.mMeshes()!![i]])
            meshes.add(processMesh(mesh, scene))
        }
        // выполнить ту же обработку и для каждого потомка узла
        for (i in 0 until node.mNumChildren()) {
            processNode(AINode.create(node.mChildren()!![i]), scene)
        }
    }

    fun processMesh(mesh: AIMesh, scene: AIScene): Mesh {
        val vertices = mutableListOf<Vertex>()
        val indices = mutableListOf<Int>()
        val textures = mutableListOf<Texture>()

        for (i in 0 until mesh.mNumVertices()) {
            val vertex = Vertex()
            val position = Vec3(
                mesh.mVertices()[i].x(),
                mesh.mVertices()[i].y(),
                mesh.mVertices()[i].z()
            )
            vertex.position = position;
            val normal = Vec3(
                mesh.mNormals()!![i].x(),
                mesh.mNormals()!![i].y(),
                mesh.mNormals()!![i].z()
            )
            vertex.normal = normal;
            if (mesh.mTextureCoords(0) != null) // сетка обладает набором текстурных координат?
            {
                val vec = Vec2();
                vec.x = mesh.mTextureCoords(0)!![i].x();
                vec.y = mesh.mTextureCoords(0)!![i].y();
                vertex.texCoords = vec;
            } else vertex.texCoords = Vec2(0.0f, 0.0f);
            vertices.add(vertex)
        }
        for (i in 0 until mesh.mNumFaces()) {
            val face: AIFace = mesh.mFaces()[i];
            for (j in 0 until face.mNumIndices()) {
                indices.add(face.mIndices()[j]);
            }
        }
        // обработка материала
        if (mesh.mMaterialIndex() >= 0) {
            val material: AIMaterial = AIMaterial.create(scene.mMaterials()!![mesh.mMaterialIndex()]);
            val diffuseMaps: List<Texture> = loadMaterialTextures(material, aiTextureType_DIFFUSE, "texture_diffuse");
            textures.addAll(diffuseMaps);
            val specularMaps: List<Texture> =
                loadMaterialTextures(material, aiTextureType_SPECULAR, "texture_specular");
            textures.addAll(specularMaps);
        }

        return Mesh(vertices, indices, textures)
    }


    fun loadMaterialTextures(mat: AIMaterial, textureType: Int, typeName: String): List<Texture> {
        val textures = mutableListOf<Texture>()

        for (i in 0 until aiGetMaterialTextureCount(mat, textureType)) {
            val texturePath = AIString.calloc()
            aiGetMaterialTexture(
                mat,
                textureType,
                i,
                texturePath,
                null as IntBuffer?,
                null,
                null,
                null,
                null,
                null
            )
            val textPath = texturePath.dataString()

            val textureId = loadTexture(textPath)

            textures.add(
                Texture().apply {
                    id = textureId
                    path = textPath
                    type = typeName
                }
            )
        }
        return textures;
    }

}
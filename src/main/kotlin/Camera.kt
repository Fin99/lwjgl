import com.hackoeur.jglm.Mat4
import com.hackoeur.jglm.Matrices.lookAt
import com.hackoeur.jglm.Vec3
import com.hackoeur.jglm.support.FastMath.*

// Defines several possible options for camera movement. Used as abstraction to stay away from window-system specific input methods
enum class CameraMovement {
    FORWARD,
    BACKWARD,
    LEFT,
    RIGHT
}

// Default camera values
const val YAW = -90.0f
const val PITCH = 0.0f
const val SPEED = 3.0f
const val SENSITIVTY = 0.25f
const val ZOOM = 45.0f


// An abstract camera class that processes input and calculates the corresponding Eular Angles, Vectors and Matrices for use in OpenGL
class Camera {
    // Camera Attributes
    var position: Vec3
    var front: Vec3
    lateinit var up: Vec3
    lateinit var right: Vec3
    var worldUp: Vec3

    // Eular Angles
    var yaw: Float
    var pitch: Float

    // Camera options
    var movementSpeed: Float
    var mouseSensitivity: Float
    var zoom: Float


    // Constructor with vectors
    constructor(
        position: Vec3 = Vec3(0.0f, 0.0f, 0.0f),
        up: Vec3 = Vec3(0.0f, 1.0f, 0.0f),
        yaw: Float = YAW,
        pitch: Float = PITCH
    ) {
        front = Vec3(0.0f, 0.0f, -1.0f)
        movementSpeed = SPEED
        mouseSensitivity = SENSITIVTY
        zoom = ZOOM
        this.position = position
        worldUp = up
        this.yaw = yaw
        this.pitch = pitch
        updateCameraVectors()
    }

    // Constructor with scalar values
    constructor(posX: Float, posY: Float, posZ: Float, upX: Float, upY: Float, upZ: Float, yaw: Float, pitch: Float) {
        front = Vec3(0.0f, 0.0f, -1.0f)
        movementSpeed = SPEED
        mouseSensitivity = SENSITIVTY
        zoom = ZOOM
        position = Vec3(posX, posY, posZ)
        worldUp = Vec3(upX, upY, upZ)
        this.yaw = yaw
        this.pitch = pitch
        updateCameraVectors()
    }

    // Returns the view matrix calculated using Eular Angles and the LookAt Matrix
    fun getViewMatrix(): Mat4 {
        return lookAt(position, position.add(front), up)
    }

    // Processes input received from any keyboard-like input system. Accepts input parameter in the form of camera defined ENUM (to abstract it from windowing systems)
    fun processKeyboard(direction: CameraMovement, deltaTime: Float) {
        val velocity = movementSpeed * deltaTime
        if (direction == CameraMovement.FORWARD)
            position = position.add(front.multiply(velocity))
        if (direction == CameraMovement.BACKWARD)
            position = position.subtract(front.multiply(velocity))
        if (direction == CameraMovement.LEFT)
            position = position.subtract(right.multiply(velocity))
        if (direction == CameraMovement.RIGHT)
            position = position.add(right.multiply(velocity))
        if (direction == CameraMovement.RIGHT)
            position = position.add(right.multiply(velocity))
    }

    // Processes input received from a mouse input system. Expects the offset value in both the x and y direction.
    fun processMouseMovement(xoffset: Float, yoffset: Float, constrainPitch: Boolean = true) {
        val xoffset1 = xoffset * mouseSensitivity
        val yoffset1 = yoffset * mouseSensitivity

        yaw += xoffset1
        pitch += yoffset1

        // Make sure that when pitch is out of bounds, screen doesn't get flipped
        if (constrainPitch) {
            if (pitch > 89.0f)
                pitch = 89.0f
            if (pitch < -89.0f)
                pitch = -89.0f
        }

        // Update Front, Right and Up Vectors using the updated Eular angles
        updateCameraVectors()
    }

    // Processes input received from a mouse scroll-wheel event. Only requires input on the vertical wheel-axis
    fun processMouseScroll(yoffset: Float) {
        if (zoom >= 1.0f && zoom <= 45.0f)
            zoom -= yoffset
        if (zoom <= 1.0f)
            zoom = 1.0f
        if (zoom >= 45.0f)
            zoom = 45.0f
    }

    private fun updateCameraVectors() {
        // Calculate the new Front vector
        val front = Vec3(
            (cos(toRadians(yaw.toDouble())) * cos(toRadians(pitch.toDouble()))).toFloat(),
            sin(toRadians(pitch.toDouble())).toFloat(),
            (sin(toRadians(yaw.toDouble())) * cos(toRadians(pitch.toDouble()))).toFloat()
        )
        this.front = front.unitVector
        // Also re-calculate the Right and Up vector
        right =
            this.front.cross(worldUp).unitVector  // Normalize the vectors, because their length gets closer to 0 the more you look up or down which results in slower movement.
        up = right.cross(this.front).unitVector
    }
}
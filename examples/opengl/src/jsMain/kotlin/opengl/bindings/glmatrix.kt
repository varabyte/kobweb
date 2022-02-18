@file:Suppress("ClassName")

package opengl.bindings

import org.khronos.webgl.Float32Array

open external class ReadonlyVec3 : Float32Array
open external class ReadonlyMat4 : Float32Array

fun Array<Float>.toReadonlyVec3() = unsafeCast<ReadonlyVec3>()

/**
 * The minimal bindings needed to get glMatrix to work with this sample project.
 */
external class glMatrix {
    // See https://glmatrix.net/docs/module-mat4.html for the full API for this class
    class mat4 : ReadonlyMat4 {
        companion object {
            fun create(): mat4
            fun perspective(matrixOut: mat4, fov: Number, aspect: Number, zNear: Number, zFar: Number)

            fun translate(matrixOut: mat4, matrixIn: ReadonlyMat4, translateBy: ReadonlyVec3)
            fun rotate(matrixOut: mat4, matrixIn: ReadonlyMat4, rotateRad: Number, rotateAxis: ReadonlyVec3)

            fun invert(matrixOut: mat4, matrixIn: ReadonlyMat4)
            fun transpose(matrixOut: mat4, matrixIn: ReadonlyMat4)
        }
    }
}
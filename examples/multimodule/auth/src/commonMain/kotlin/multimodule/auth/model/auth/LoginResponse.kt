package multimodule.auth.model.auth

import kotlinx.serialization.Serializable

@Serializable
data class LoginResponse(
    val succeeded: Boolean
)
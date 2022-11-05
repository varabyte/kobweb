package multimodule.auth.model.auth

import kotlinx.serialization.Serializable

@Serializable
data class Account(
    val username: String,
    val password: String,
)
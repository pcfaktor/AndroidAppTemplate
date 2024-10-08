package com.softteco.template.data.auth.dto

import com.softteco.template.data.auth.entity.AuthToken
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AuthTokenDto(
    @SerialName("token")
    val token: String,
)

fun AuthTokenDto.toModel(): AuthToken {
    return AuthToken(token)
}

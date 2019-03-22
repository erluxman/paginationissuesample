package com.example.pagedlistissuesample.repository


data class UserEntity(
    override val id: String,
    val userImageUrl: String,
    val displayName: String,
    val userName: String,
    var following: Boolean,
    override val createdTimestamp: Long
) : Entity {
    companion object {
        fun newInstance() = UserEntity("", "", "", "", false, 0)
    }
}
package com.example.pagedlistissuesample.repository

import com.example.pagedlistissuesample.pagingation.PageRequest
import com.example.pagedlistissuesample.pagingation.PaginationType
import com.example.pagedlistissuesample.util.Base64Encoder
import io.reactivex.Completable
import io.reactivex.Single

class UserRepository constructor(
    private val userDb: UserDb
) {

    fun findFollowedUsers(searchQuery: String, pageRequest: PageRequest): Single<List<UserEntity>> {
        val (pageSize, cursor, paginationType) = pageRequest

        return if (cursor.isEmpty()) userDb.getUsers(searchQuery, pageSize)
        else searchUsers(searchQuery, pageSize, paginationType, cursor)
    }

    private fun searchUsers(
        search: String, pageSize: Int,
        paginationType: PaginationType, cursor: String
    ): Single<List<UserEntity>> {
        val userId = Base64Encoder.decode(cursor)
        return when (paginationType) {
            PaginationType.BEFORE -> userDb.getUsersBefore(search, userId, pageSize)
            PaginationType.AFTER -> userDb.getUsersAfter(search, userId, pageSize)
        }
    }

    fun followUser(userId: String): Completable {
        return userDb.getUser(userId)
            .flatMapCompletable { user ->
                userDb.updateUser(user.copy(following = true))
            }
    }

    fun unFollowUser(userId: String): Completable {
        return userDb.getUser(userId)
            .flatMapCompletable { user ->
                userDb.updateUser(user.copy(following = false))
            }
    }

    companion object {
        fun getInstance(): UserRepository {
            return UserRepository(UserDb.getInstance())
        }
    }
}

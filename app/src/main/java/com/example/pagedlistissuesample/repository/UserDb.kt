package com.example.pagedlistissuesample.repository

import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.rxkotlin.flatMapIterable
import java.security.SecureRandom
import java.util.*

class UserDb constructor(
    private val users: MutableList<UserEntity> = MOCK_USERS

) {

    private fun getUsers(search: String) = Observable.just(users)
        .map { list ->
            list.filter { it.displayName.contains(search) || it.userName.contains(search) }
        }

    fun getUsers(searchQuery: String, pageSize: Int): Single<List<UserEntity>> {
        return getUsers(searchQuery)
            .flatMapIterable()
            .take(pageSize.toLong())
            .toList()
    }

    fun getUsersBefore(searchQuery: String, userId: String, pageSize: Int): Single<List<UserEntity>> {
        return getUsers(searchQuery)
            .map { users ->
                val indexOfFirst = findIndexOfFirst(userId, users)
                findPageBackward(users, indexOfFirst, pageSize)
            }.singleOrError()
    }

    fun getUsersAfter(searchQuery: String, userId: String, pageSize: Int): Single<List<UserEntity>> {
        return getUsers(searchQuery)
            .map { users ->
                val indexOfFirst = findIndexOfFirst(userId, users)
                findPageForward(users, indexOfFirst, pageSize)
            }.singleOrError()
    }

    private fun findIndexOfFirst(userId: String, users: List<UserEntity>): Int {
        return users.indexOfFirst { it.id == userId }
    }

    private fun findPageBackward(
        users: List<UserEntity>, indexOfFirst: Int,
        pageSize: Int
    ): List<UserEntity> {
        return users.dropLast(users.size - indexOfFirst).takeLast(pageSize).toList()
    }

    private fun findPageForward(
        users: List<UserEntity>, indexOfFirst: Int,
        pageSize: Int
    ): List<UserEntity> {
        return users.drop(indexOfFirst).take(pageSize).toList()
    }

    fun getUser(userId: String): Maybe<UserEntity> {
        return Observable.defer {
            val userIndex = users.indexOfFirst { it.id == userId }
            if (userIndex != -1) {
                Observable.just(users[userIndex])
            } else {
                Observable.error(Throwable("Unable to get user with id $userId"))
            }
        }.singleElement()
    }

    fun updateUser(user: UserEntity): Completable {
        return Completable.defer {
            val userIndex = users.indexOfFirst { it.id == user.id }
            if (userIndex != -1) {
                users[userIndex] = user
                Completable.complete()
            } else {
                Completable.error(Throwable("Unable to update user with id ${user.id}"))
            }
        }
    }

    companion object {
        fun getInstance(): UserDb {
            return UserDb()
        }
    }
}

val MOCK_USERS by lazy {
    val secureRandom: Random = SecureRandom()
    MutableList(400) {
        val randomInt = secureRandom.nextInt()
        UserEntity(
            "sdfds$randomInt",
            "https://loremflickr.com/g/300/300/face",
            "$randomInt display name",
            "user name",
            randomInt % 3 != 0,
            System.currentTimeMillis()
        )
    }
}


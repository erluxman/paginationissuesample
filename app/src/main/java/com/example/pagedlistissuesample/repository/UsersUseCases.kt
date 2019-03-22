package com.example.pagedlistissuesample.repository

import androidx.paging.PagedList
import androidx.paging.RxPagedListBuilder
import com.example.pagedlistissuesample.pagingation.SimpleDataSourceFactory
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable

data class UsersUseCases(
    val followUserUseCase: FollowUserUseCase = FollowUserUseCase(
        UserRepository.getInstance()
    ),
    val unFollowUserUseCase: UnfollowUserUseCase = UnfollowUserUseCase(
        UserRepository.getInstance()
    ),
    val searchUsersUseCase: SearchUsersUseCase = SearchUsersUseCase(
        UserRepository.getInstance()
    )
)

class FollowUserUseCase(private val repository: UserRepository) {
    operator fun invoke(userId: String): Completable {
        return repository.followUser(userId)
    }
}

class SearchUsersUseCase(private val repository: UserRepository) {
    operator fun invoke(searchText: String, disposables: CompositeDisposable): Observable<PagedList<UserEntity>> {
        val pageSize = 40
        val initialLoadSizeHint = 50
        val prefetchDistance = 15
        val pageConfig: PagedList.Config = PagedList.Config.Builder()
            .setPageSize(pageSize)
            .setInitialLoadSizeHint(initialLoadSizeHint)
            .setPrefetchDistance(prefetchDistance)
            .setEnablePlaceholders(false)
            .build()
        val dataSource = SimpleDataSourceFactory(disposables) { pageRequest ->
            repository.findFollowedUsers(searchText, pageRequest)
        }
        return RxPagedListBuilder(dataSource, pageConfig).buildObservable()
    }
}

class UnfollowUserUseCase(private val repository: UserRepository) {
    operator fun invoke(userId: String): Completable {
        return repository.unFollowUser(userId)
    }
}
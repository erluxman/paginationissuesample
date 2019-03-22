package com.example.pagedlistissuesample.pagingation

import androidx.paging.ItemKeyedDataSource
import com.example.pagedlistissuesample.repository.Entity
import com.example.pagedlistissuesample.util.Base64Encoder
import io.reactivex.Single
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.rxkotlin.subscribeBy
import timber.log.Timber

class SimpleDataSource<E : Entity>(
    private val disposables: CompositeDisposable,
    private val pagingFun: (PageRequest) -> Single<List<E>>
) : ItemKeyedDataSource<DataSourceKey, E>() {

    override fun loadInitial(params: LoadInitialParams<DataSourceKey>, callback: LoadInitialCallback<E>) {
        val paginationType = if (params.requestedInitialKey != null) PaginationType.BEFORE else PaginationType.AFTER
        loadData(params.requestedLoadSize, params.requestedInitialKey, callback, paginationType)
    }

    override fun loadAfter(params: LoadParams<DataSourceKey>, callback: LoadCallback<E>) {
        loadData(params.requestedLoadSize, params.key, callback, PaginationType.AFTER)
    }

    override fun loadBefore(params: LoadParams<DataSourceKey>, callback: LoadCallback<E>) {
        loadData(params.requestedLoadSize, params.key, callback, PaginationType.BEFORE)
    }

    private fun loadData(
        requestedLoadSize: Int, key: DataSourceKey?, callback: LoadCallback<E>,
        paginationType: PaginationType
    ) {
        val pageSize = if (requestedLoadSize <= 0) DEFAULT_PAGE_SIZE else requestedLoadSize
        val cursor = key?.let { Base64Encoder.encode(it.epochMilliseconds, it.itemId) } ?: ""
        val pageRequest = PageRequest(pageSize, cursor, paginationType)

        disposables += pagingFun(pageRequest)
            .subscribeBy(
                onSuccess = { callback.onResult(it) },
                onError = { Timber.e(it) }
            )
    }

    override fun getKey(item: E) = DataSourceKey(item.createdTimestamp, item.id)

    companion object {
        private const val DEFAULT_PAGE_SIZE = 10
    }
}

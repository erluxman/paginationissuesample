package com.example.pagedlistissuesample.pagingation

import androidx.paging.DataSource
import com.example.pagedlistissuesample.repository.Entity
import io.reactivex.Single
import io.reactivex.disposables.CompositeDisposable

class SimpleDataSourceFactory<E : Entity>(
    private val disposables: CompositeDisposable,
    private val pagingFun: (PageRequest) -> Single<List<E>>
) : DataSource.Factory<DataSourceKey, E>() {

    override fun create(): SimpleDataSource<E> {
        return SimpleDataSource(disposables, pagingFun)
    }
}
package com.example.pagedlistissuesample.ui

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.ToggleButton
import androidx.appcompat.app.AppCompatActivity
import androidx.paging.PagedList
import com.airbnb.epoxy.OnModelClickListener
import com.example.pagedlistissuesample.R
import com.example.pagedlistissuesample.repository.UserEntity
import com.example.pagedlistissuesample.repository.UsersUseCases
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.plusAssign
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.layout_search_bar.*
import timber.log.Timber

class MainActivity : AppCompatActivity() {
    private val useCases: UsersUseCases = UsersUseCases()
    private val disposable = CompositeDisposable()
    private lateinit var searchResult: PagedList<UserEntity>
    private lateinit var pagingEpoxyController: FollowUsersPagingEpoxyController
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        pagingEpoxyController = FollowUsersPagingEpoxyController(userOnModelClickListener)
        epoxyRecyclerView.adapter = pagingEpoxyController.adapter

        search.hint = "Search people"
        search.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                searchUser(s.toString())
            }
        })
    }

    fun searchUser(search: String) {
        disposable += useCases.searchUsersUseCase(search, disposable).subscribe({
            searchResult = it
            pagingEpoxyController.submitList(searchResult)
        }, Timber::e)
    }

    fun followUser(userId: String) {
        disposable += useCases.followUserUseCase(userId).subscribe({
            searchResult.dataSource.invalidate()
        }, Timber::e)
    }

    fun unFollowUser(userId: String) {
        disposable += useCases.unFollowUserUseCase(userId).subscribe({
            searchResult.dataSource.invalidate()
        }, Timber::e)
    }

    override fun onDestroy() {
        super.onDestroy()
        disposable.dispose()
    }

    private val userOnModelClickListener = object : OnModelClickListener<EpoxyFollowUserModel_, FollowUsersEpoxyHolder> {

            override fun onClick(
                model: EpoxyFollowUserModel_, parentView: FollowUsersEpoxyHolder,
                clickedView: View, position: Int
            ) {
                onItemClick(clickedView, model)
            }

            private fun onItemClick(clickedView: View, model: EpoxyFollowUserModel_) {
                if ((clickedView as ToggleButton).isChecked) {
                    followUser(model.userId())
                } else {
                    unFollowUser(model.userId())
                }
            }
        }
}



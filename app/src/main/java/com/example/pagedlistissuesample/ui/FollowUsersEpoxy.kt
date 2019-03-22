package com.example.pagedlistissuesample.ui

import android.graphics.Color
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.ToggleButton
import com.airbnb.epoxy.*
import com.airbnb.epoxy.EpoxyAttribute.Option.DoNotHash
import com.airbnb.epoxy.paging.PagedListEpoxyController
import com.example.pagedlistissuesample.R
import com.example.pagedlistissuesample.repository.UserEntity
import com.example.pagedlistissuesample.util.loadImage
import timber.log.Timber


class FollowUsersPagingEpoxyController(
    private val clickListener: OnModelClickListener<EpoxyFollowUserModel_, FollowUsersEpoxyHolder>
) : PagedListEpoxyController<UserEntity>() {

    override fun buildItemModel(currentPosition: Int, item: UserEntity?): EpoxyModel<*> {
        val followUserModel = EpoxyFollowUserModel_()
        if (item == null) {
            return followUserModel
        }
        return followUserModel.apply {
            Timber.d("buildModels called for ${item.displayName}")
            id("id ${item.id}")
            userId(item.id)
            userImageUrl(item.userImageUrl)
            displayName(item.displayName)
            userName("feeds ${item.userName}")
            following(item.following)
            followClickListener(clickListener)
        }
    }
}

class FollowUsersEpoxyHolder : EpoxyHolder() {

    var userImage: ImageView? = null
    var displayName: TextView? = null
    var userName: TextView? = null
    var follow: ToggleButton? = null

    override fun bindView(itemView: View) {
        userImage = itemView.findViewById(R.id.userImage)
        displayName = itemView.findViewById(R.id.displayName)
        userName = itemView.findViewById(R.id.userName)
        follow = itemView.findViewById(R.id.follow)
        Timber.d("--- FollowUsersEpoxyHolder bindView Called")
    }
}

@EpoxyModelClass(layout = R.layout.item_followed_users)
abstract class EpoxyFollowUserModel : EpoxyModelWithHolder<FollowUsersEpoxyHolder>() {

    @EpoxyAttribute
    var userId: String = ""
    @EpoxyAttribute
    var userImageUrl: String = ""
    @EpoxyAttribute
    var displayName: String = ""
    @EpoxyAttribute
    var userName: String = ""
    @EpoxyAttribute
    var following: Boolean = false
    @EpoxyAttribute(DoNotHash)
    var followClickListener: View.OnClickListener? = null

    override fun bind(holder: FollowUsersEpoxyHolder) {
        holder.userImage?.loadImage(drawable = R.mipmap.ic_launcher_round, circular = true)
        holder.displayName?.text = displayName
        holder.userName?.text = userName
        holder.follow?.isChecked = following
        holder.follow?.text = if (following) "Unfollow " else "Follow"
        holder.follow?.setBackgroundColor(if (following) Color.GRAY else Color.BLUE)
        holder.follow?.setOnClickListener(followClickListener)
    }

    override fun unbind(holder: FollowUsersEpoxyHolder) {
        holder.follow?.setOnClickListener(null)
        super.unbind(holder)
    }
}

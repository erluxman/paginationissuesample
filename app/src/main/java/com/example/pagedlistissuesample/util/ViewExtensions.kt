package com.example.pagedlistissuesample.util

import android.content.Context
import android.view.View
import android.view.View.*
import android.widget.ImageView
import android.widget.Toast
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager
import com.example.pagedlistissuesample.R
import com.kabzeel.android.util.CircleTransform
import com.squareup.picasso.Picasso
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject

fun View.visible(visible: Boolean?) {
    when (visible) {
        true -> this.visibility = VISIBLE
        false -> this.visibility = GONE
        else -> this.visibility = INVISIBLE
    }
}

fun View.gone() {
    if (visibility != View.GONE) {
        visibility = View.GONE
    }
}

fun View.visible() {
    if (visibility != View.VISIBLE) {
        visibility = View.VISIBLE
    }
}

fun View.inVisible() {
    if (visibility != View.INVISIBLE) {
        visibility = View.INVISIBLE
    }
}

fun View.isVisible() = this.visibility == View.VISIBLE


fun Boolean.toViewVisibility(): Int {
    return if (this) {
        View.VISIBLE
    } else {
        View.GONE
    }
}

fun Context.toast(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
}

fun Fragment.toast(message: String, duration: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(context, message, duration).show()
}

fun ImageView.loadImage(
    url: String = "",
    @DrawableRes drawable: Int = R.mipmap.ic_launcher_round,
    placeHolderUrl: String = "",
    @DrawableRes placeHolderDrawable: Int = R.mipmap.ic_launcher_round,
    circular: Boolean = false
) {
    val defDrawable = R.mipmap.ic_launcher_round

    val requestCreator = when {
        url.isNotEmpty() -> Picasso.get().load(url)
        placeHolderUrl.isNotEmpty() -> Picasso.get().load(placeHolderUrl)
        drawable != 0 -> Picasso.get().load(drawable)
        else -> Picasso.get().load(defDrawable) // error image
    }

    if (placeHolderDrawable != 0) {
        requestCreator.placeholder(placeHolderDrawable)
    }

    if (circular) {
        requestCreator.transform(CircleTransform())
    }

    requestCreator.into(this)
}

fun Context.getColorCompat(color: Int) = ContextCompat.getColor(this, color)

fun Fragment.getColorCompat(color: Int) = context?.getColorCompat(color)

fun ViewPager.selectsPage(): Observable<Int> {
    val event = PublishSubject.create<Int>()
    this.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
        override fun onPageScrollStateChanged(state: Int) {
        }

        override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
        }

        override fun onPageSelected(position: Int) {
            event.onNext(position)
        }
    })
    return event
}

/**
 * Emits a Pair of Int -> `@GravityCompat.Gravity` & Boolean -> `isDrawerOpen` state
 * of nav drawer defined by that gravity.Eg:
 * If left drawer just got opened it will emit Pair(first = GravityCompat.START, second = true)
 * If right drawer just got closed it will return Pair(first = GravityCompat.END, second = false)
 */
fun DrawerLayout.emitsDrawerOpenState(): Observable<Pair<Int, Boolean>> {
    val event = PublishSubject.create<Pair<Int, Boolean>>()
    this.addDrawerListener(object : DrawerLayout.DrawerListener {

        var lastOpenedDrawerGravity: Int = 0

        override fun onDrawerStateChanged(newState: Int) {}

        override fun onDrawerSlide(drawerView: View, slideOffset: Float) {}

        override fun onDrawerClosed(drawerView: View) {
            if (lastOpenedDrawerGravity == GravityCompat.START) {
                event.onNext(Pair(GravityCompat.START, false))
            } else if (lastOpenedDrawerGravity == GravityCompat.END) {
                event.onNext(Pair(GravityCompat.END, false))
            }
        }

        override fun onDrawerOpened(drawerView: View) {
            if (isDrawerOpen(GravityCompat.START)) {
                event.onNext(Pair(GravityCompat.START, true))
                lastOpenedDrawerGravity = GravityCompat.START
            } else if (isDrawerOpen(GravityCompat.END)) {
                event.onNext(Pair(GravityCompat.END, true))
                lastOpenedDrawerGravity = GravityCompat.END
            }
        }
    })
    return event
}

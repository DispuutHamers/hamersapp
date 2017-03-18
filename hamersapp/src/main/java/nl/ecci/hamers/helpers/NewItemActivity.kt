package nl.ecci.hamers.helpers

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.DatePicker
import android.widget.ImageView
import nl.ecci.hamers.R

@SuppressLint("Registered")
abstract class NewItemActivity : HamersActivity(), DatePickerDialog.OnDateSetListener {
    var refreshItem: MenuItem? = null

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.new_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.done_button -> {
                refreshItem = item

                /* Attach a rotating ImageView to the refresh item as an ActionView */
                val inflater = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
                val iv = inflater.inflate(R.layout.element_refresh, null) as ImageView

                val rotation = AnimationUtils.loadAnimation(this, R.anim.clockwise_refresh)
                rotation.repeatCount = Animation.INFINITE
                iv.startAnimation(rotation)

                refreshItem!!.actionView = iv

                postItem()
                return true
            }
            else -> return false
        }
    }

    fun disableLoadingAnimation() {
        refreshItem?.actionView?.clearAnimation()
        refreshItem?.actionView = null
    }

    abstract fun postItem()

}

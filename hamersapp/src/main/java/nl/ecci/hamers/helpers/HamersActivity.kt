package nl.ecci.hamers.helpers

import android.annotation.SuppressLint
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.row_detailview.view.*
import kotlinx.android.synthetic.main.row_imageview.view.*
import kotlinx.android.synthetic.main.row_singleview.view.*
import nl.ecci.hamers.R
import nl.ecci.hamers.helpers.PermissionUtils.PERMISSION_REQUEST_CODE


@SuppressLint("Registered")
open class HamersActivity : AppCompatActivity() {

    var toolbar: Toolbar? = null
    var prefs: SharedPreferences? = null

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        prefs = PreferenceManager.getDefaultSharedPreferences(this)
    }

    open fun initToolbar() {
        toolbar = findViewById(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)

        val actionBar = supportActionBar

        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeButtonEnabled(true)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    fun newSingleRow(title: String, viewGroup: ViewGroup): View {
        val view = layoutInflater.inflate(R.layout.row_singleview, viewGroup, false)
        view.row_single_title.text = title
        return view
    }

    fun fillDetailRow(view: View, title: String, description: String?) {
        view.row_detail_title.text = title
        view.row_detail_description.text = description
    }

    fun fillImageRow(view: View, title: String, description: String, imageId: Int) {
        view.row_imageview_title_textview.text = title
        view.row_imageview_subtitle_textview.text = description
        view.row_imageview_image.setImageDrawable(ContextCompat.getDrawable(this, imageId))
    }

}

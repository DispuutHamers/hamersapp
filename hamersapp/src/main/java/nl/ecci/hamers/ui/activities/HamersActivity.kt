package nl.ecci.hamers.ui.activities

import android.annotation.SuppressLint
import android.content.SharedPreferences
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import kotlinx.android.synthetic.main.element_toolbar.*
import kotlinx.android.synthetic.main.row_detailview.view.*
import kotlinx.android.synthetic.main.row_imageview.view.*
import kotlinx.android.synthetic.main.row_singleview.view.*
import nl.ecci.hamers.R

@SuppressLint("Registered")
open class HamersActivity : AppCompatActivity() {

    var prefs: SharedPreferences? = null
    var gson: Gson = GsonBuilder().setDateFormat(MainActivity.dbDF.toPattern()).create()

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        prefs = PreferenceManager.getDefaultSharedPreferences(this)
    }

    open fun initToolbar() {
        setSupportActionBar(toolbar)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)

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

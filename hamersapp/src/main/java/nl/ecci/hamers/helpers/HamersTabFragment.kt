package nl.ecci.hamers.helpers

import android.graphics.Color
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.view.View
import kotlinx.android.synthetic.main.fragment_hamers_tab.*
import nl.ecci.hamers.R

open class HamersTabFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tab_fragment_sliding_tabs.setTabTextColors(ContextCompat.getColor(context, R.color.sliding_tabs_text_normal), ContextCompat.getColor(context, R.color.sliding_tabs_text_selected))
        tab_fragment_sliding_tabs.setSelectedTabIndicatorColor(Color.WHITE)
        tab_fragment_sliding_tabs.setupWithViewPager(tab_fragment_viewpager)
    }

    override fun onPause() {
        super.onPause()
        ActivityCompat.invalidateOptionsMenu(activity)
    }

}

package nl.ecci.hamers.ui.fragments

import android.graphics.Color
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.view.View
import kotlinx.android.synthetic.main.fragment_hamers_tab.*
import nl.ecci.hamers.R
import org.jetbrains.anko.support.v4.act

open class HamersTabFragment : HamersFragment() {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tab_fragment_sliding_tabs.setTabTextColors(
                ContextCompat.getColor(act, R.color.sliding_tabs_text_normal),
                ContextCompat.getColor(act, R.color.sliding_tabs_text_selected)
        )
        tab_fragment_sliding_tabs.setSelectedTabIndicatorColor(Color.WHITE)
        tab_fragment_sliding_tabs.setupWithViewPager(tab_fragment_viewpager)
    }

}

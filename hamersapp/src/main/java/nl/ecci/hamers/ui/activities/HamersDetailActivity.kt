package nl.ecci.hamers.ui.activities

import android.annotation.SuppressLint
import android.os.Bundle
import android.support.v4.widget.SwipeRefreshLayout
import kotlinx.android.synthetic.main.activity_general.*
import nl.ecci.hamers.R

@SuppressLint("Registered")
abstract class HamersDetailActivity : HamersActivity(), SwipeRefreshLayout.OnRefreshListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_general)

        hamers_swipe_container.setColorSchemeResources(android.R.color.holo_red_light)
        hamers_swipe_container.setOnRefreshListener(this)
    }

    fun setRefreshing(bool: Boolean) {
        hamers_swipe_container?.post { hamers_swipe_container?.isRefreshing = bool }
    }
}
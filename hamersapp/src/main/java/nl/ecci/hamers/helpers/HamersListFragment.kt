package nl.ecci.hamers.helpers

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import kotlinx.android.synthetic.main.hamers_list_fragment.*

abstract class HamersListFragment : Fragment(), SwipeRefreshLayout.OnRefreshListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        initSwiper()
    }

    private fun initSwiper() {
        val lm = LinearLayoutManager(activity)
        hamers_recyclerview.layoutManager = lm
        hamers_recyclerview.itemAnimator = DefaultItemAnimator()

        hamers_swipe_container.setColorSchemeResources(android.R.color.holo_red_light)
        hamers_swipe_container.setOnRefreshListener(this)

        hamers_recyclerview.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(view: RecyclerView?, dx: Int, dy: Int) {
                hamers_swipe_container.isEnabled = lm.findFirstCompletelyVisibleItemPosition() == 0
            }
        })
    }

    fun setRefreshing(bool: Boolean) {
        hamers_swipe_container?.post { hamers_swipe_container?.isRefreshing = bool }
    }

    fun notifyAdapter() {
        hamers_recyclerview?.adapter?.notifyDataSetChanged()
    }
}

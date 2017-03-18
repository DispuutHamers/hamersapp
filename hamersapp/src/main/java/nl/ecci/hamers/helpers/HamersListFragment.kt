package nl.ecci.hamers.helpers

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import kotlinx.android.synthetic.main.fragment_hamers_list.*
import android.support.design.widget.CoordinatorLayout

abstract class HamersListFragment : Fragment(), SwipeRefreshLayout.OnRefreshListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        val lm = LinearLayoutManager(activity)
        hamers_list.layoutManager = lm
        hamers_list.itemAnimator = DefaultItemAnimator()

        val params = hamers_fab.layoutParams as CoordinatorLayout.LayoutParams
        params.anchorId = hamers_swipe_container.id
        hamers_fab.layoutParams = params

        hamers_swipe_container.setColorSchemeResources(android.R.color.holo_red_light)
        hamers_swipe_container.setOnRefreshListener(this)

        hamers_list.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(view: RecyclerView?, dx: Int, dy: Int) {
                hamers_swipe_container?.isEnabled = lm.findFirstCompletelyVisibleItemPosition() == 0
            }
        })
    }

    fun setRefreshing(bool: Boolean) {
        hamers_swipe_container?.post { hamers_swipe_container?.isRefreshing = bool }
    }

    fun notifyAdapter() {
        hamers_list?.adapter?.notifyDataSetChanged()
    }
}

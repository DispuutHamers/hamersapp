package nl.ecci.hamers.ui.fragments

import android.os.Bundle
import android.view.View
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import kotlinx.android.synthetic.main.fragment_hamers_list.*

abstract class HamersListFragment : HamersFragment(), SwipeRefreshLayout.OnRefreshListener {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val lm = LinearLayoutManager(activity)
        hamers_list.layoutManager = lm
        hamers_list.itemAnimator = DefaultItemAnimator()

        val params = hamers_fab.layoutParams as CoordinatorLayout.LayoutParams
        params.anchorId = hamers_swipe_container.id
        hamers_fab.layoutParams = params

        hamers_swipe_container.setColorSchemeResources(android.R.color.holo_red_light)
        hamers_swipe_container.setOnRefreshListener(this)

        hamers_list.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
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

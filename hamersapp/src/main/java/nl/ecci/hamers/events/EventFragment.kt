package nl.ecci.hamers.events

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.hamers_tab_fragment.*
import nl.ecci.hamers.R
import nl.ecci.hamers.helpers.HamersTabFragment

class EventFragment : HamersTabFragment() {

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater?.inflate(R.layout.hamers_tab_fragment, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        tab_fragment_viewpager.adapter = EventFragmentAdapter(activity, childFragmentManager)
    }

    override fun onResume() {
        super.onResume()
        activity.title = resources.getString(R.string.navigation_item_events)
    }
}

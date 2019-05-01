package nl.ecci.hamers.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_hamers_tab.*
import nl.ecci.hamers.R
import nl.ecci.hamers.ui.adapters.EventFragmentAdapter

class EventFragment : HamersTabFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
            inflater.inflate(R.layout.fragment_hamers_tab, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        tab_fragment_viewpager.adapter = EventFragmentAdapter(requireContext(), childFragmentManager)
    }

    override fun onResume() {
        super.onResume()
        activity?.title = resources.getString(R.string.navigation_item_events)
    }
}

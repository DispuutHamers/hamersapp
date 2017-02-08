package nl.ecci.hamers.users

import android.graphics.Color
import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.app.ActivityCompat
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v4.view.ViewPager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import nl.ecci.hamers.R

class UserFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        retainInstance = true

        val rootView = inflater!!.inflate(R.layout.user_fragment, container, false)

        val adapter = UserFragmentPagerAdapter(activity, childFragmentManager)
        val viewPager = rootView.findViewById(R.id.user_fragment_viewpager) as ViewPager
        viewPager.adapter = adapter

        val tabLayout = rootView.findViewById(R.id.user_fragment_sliding_tabs) as TabLayout
        tabLayout.setTabTextColors(ContextCompat.getColor(context, R.color.sliding_tabs_text_normal), ContextCompat.getColor(context, R.color.sliding_tabs_text_selected))
        tabLayout.setSelectedTabIndicatorColor(Color.WHITE)
        tabLayout.setupWithViewPager(viewPager)

        return rootView
    }

    override fun onPause() {
        super.onPause()
        ActivityCompat.invalidateOptionsMenu(activity)
    }
}

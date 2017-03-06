package nl.ecci.hamers.users

import android.graphics.Color
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.user_fragment.*
import nl.ecci.hamers.R

class UserFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater?.inflate(R.layout.user_fragment, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        user_fragment_viewpager.adapter = UserFragmentPagerAdapter(activity, childFragmentManager)
        user_fragment_sliding_tabs.setTabTextColors(ContextCompat.getColor(context, R.color.sliding_tabs_text_normal), ContextCompat.getColor(context, R.color.sliding_tabs_text_selected))
        user_fragment_sliding_tabs.setSelectedTabIndicatorColor(Color.WHITE)
        user_fragment_sliding_tabs.setupWithViewPager(user_fragment_viewpager)
    }

    override fun onPause() {
        super.onPause()
        ActivityCompat.invalidateOptionsMenu(activity)
    }

    override fun onResume() {
        super.onResume()
        activity.title = resources.getString(R.string.navigation_item_users)
    }
}

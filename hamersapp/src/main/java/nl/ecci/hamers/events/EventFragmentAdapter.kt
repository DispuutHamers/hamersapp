package nl.ecci.hamers.events

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter

import nl.ecci.hamers.R

internal class EventFragmentAdapter(context: Context, fm: FragmentManager) : FragmentPagerAdapter(fm) {

    private var eventFragmentUpcoming: EventListFragment? = null
    private var eventFragmentAll: EventListFragment? = null
    private var tabTitles = arrayOf(context.resources.getString(R.string.menu_upcoming), context.resources.getString(R.string.menu_all))

    override fun getItem(position: Int): Fragment? {
        when (position) {
            0 -> {
                if (eventFragmentUpcoming == null) {
                    eventFragmentUpcoming = EventListFragment()
                    val argUpcoming = Bundle()
                    argUpcoming.putBoolean(upcoming, true)
                    eventFragmentUpcoming!!.arguments = argUpcoming
                }
                return eventFragmentUpcoming
            }
            1 -> {
                if (eventFragmentAll == null) {
                    eventFragmentAll = EventListFragment()
                    val argAll = Bundle()
                    argAll.putBoolean(upcoming, false)
                    eventFragmentAll!!.arguments = argAll
                }
                return eventFragmentAll
            }
        }
        return null
    }

    override fun getPageTitle(position: Int): CharSequence {
        return tabTitles[position]
    }

    companion object {
        val upcoming = "upcoming"
    }

    override fun getCount(): Int {
        return tabTitles.size
    }
}

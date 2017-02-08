package nl.ecci.hamers.users

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter

import nl.ecci.hamers.R

internal class UserFragmentPagerAdapter(context: Context, fm: FragmentManager) : FragmentPagerAdapter(fm) {

    private var userFragmentAll: UserListFragment? = null
    private var userFragmentEx: UserListFragment? = null
    private var tabTitles: Array<String>? = arrayOf(context.resources.getString(R.string.menu_users), context.resources.getString(R.string.menu_users_ex))

    override fun getCount(): Int {
        return 2
    }

    override fun getItem(position: Int): Fragment? {
        when (position) {
            0 -> {
                if (userFragmentAll == null) {
                    userFragmentAll = UserListFragment()
                    val argAll = Bundle()
                    argAll.putBoolean(exUser, false)
                    userFragmentAll!!.arguments = argAll
                }
                return userFragmentAll
            }
            1 -> {
                if (userFragmentEx == null) {
                    userFragmentEx = UserListFragment()
                    val argEx = Bundle()
                    argEx.putBoolean(exUser, true)
                    userFragmentEx!!.arguments = argEx
                }
                return userFragmentEx
            }
        }
        return null
    }

    override fun getPageTitle(position: Int): CharSequence {
        return tabTitles!![position]
    }

    companion object {
        val exUser = "exUser"
    }
}

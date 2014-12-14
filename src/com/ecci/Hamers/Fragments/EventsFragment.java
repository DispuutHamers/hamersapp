package com.ecci.Hamers.Fragments;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import com.ecci.Hamers.R;

public class EventsFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.events_fragment, container, false);
        ListView events_list = (ListView) view.findViewById(R.id.events_listView);

        // Init swiper
        final SwipeRefreshLayout swipeView = (SwipeRefreshLayout) view.findViewById(R.id.events_swipe_container);
        swipeView.setOnRefreshListener(this);
        swipeView.setColorScheme(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        swipeView.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeView.setRefreshing(true);
                ( new Handler()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        swipeView.setRefreshing(false);

                    }
                }, 3000);
            }
        });

        events_list.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {

            }

            @Override
            public void onScroll(AbsListView absListView, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (firstVisibleItem == 0)
                    swipeView.setEnabled(true);
                else
                    swipeView.setEnabled(false);
            }
        });

        // Dummy list
        final String[] values = new String[]{"Android", "iPhone", "WindowsMobile",
                "Blackberry", "Symbian", "Bada", "WebOS", "Ubuntu", "Windows7", "Max OS X",
                "Linux", "OS/2", "OpenBSD", "FreeBSD", "NetBSD", "Solaris", "HP/UX"};
        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(this.getActivity(),
                android.R.layout.simple_list_item_1, android.R.id.text1, values);
        events_list.setAdapter(adapter); //Set adapter and that's it.

        return view;
    }

    @Override
    public void onRefresh() {
        // Refresh events
    }

}
package com.ecci.Hamers.Fragments;

import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import com.ecci.Hamers.*;
import com.ecci.Hamers.Adapters.EventsAdapter;
import com.ecci.Hamers.Adapters.QuotesAdapter;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class EventFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    public EventFragment() {
        // Empty constructor required for fragment subclasses
    }

    ArrayList<Event> listItems = new ArrayList<Event>();
    ArrayAdapter<Event> adapter;
    SwipeRefreshLayout swipeView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.events_fragment, container, false);
        ListView event_list = (ListView) view.findViewById(R.id.events_listView);

        initSwiper(view, event_list);

        // 1. pass context and data to the custom adapter
        adapter = new EventsAdapter(this.getActivity(), listItems);

        //Set adapter and that's it.
        event_list.setAdapter(adapter);

        return view;
    }

    public void initSwiper(View view, ListView event_list) {
        swipeView = (SwipeRefreshLayout) view.findViewById(R.id.events_swipe_container);
        swipeView.setOnRefreshListener(this);
        swipeView.setColorSchemeResources(android.R.color.holo_red_light);

        swipeView.setOnRefreshListener(this);

        event_list.setOnScrollListener(new AbsListView.OnScrollListener() {
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
    }

    @Override
    public void onRefresh() {
        GetJson g = new GetJson(this, GetJson.EVENT, PreferenceManager.getDefaultSharedPreferences(this.getActivity()));
        g.execute();
    }

    public void populateList(JSONArray json){
        System.out.println(json);

        listItems.clear();
        for(int i = 0; i< json.length(); i++){
            JSONObject temp;
            try {
                temp = json.getJSONObject(i);
                Event tempEvent = new Event(temp.getString("title").toString(), temp.getString("beschrijving").toString(), temp.getString("date"), temp.getString("end_time"));
                listItems.add(tempEvent);
                adapter.notifyDataSetChanged();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        swipeView.setRefreshing(false);
    }
}
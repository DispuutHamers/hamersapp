package com.ecci.Hamers.Fragments;

import android.content.SharedPreferences;
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
import android.widget.Toast;
import com.ecci.Hamers.Adapters.BeersAdapter;
import com.ecci.Hamers.Beer;
import com.ecci.Hamers.GetJson;
import com.ecci.Hamers.R;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class BeerFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    public BeerFragment() {
        // Empty constructor required for fragment subclasses
    }

    ArrayList<Beer> listItems = new ArrayList<Beer>();
    ArrayAdapter<Beer> adapter;
    SwipeRefreshLayout swipeView;
    SharedPreferences prefs;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.beer_fragment, container, false);
        ListView beer_list = (ListView) view.findViewById(R.id.beer_listView);

        initSwiper(view, beer_list);

        // 1. pass context and data to the custom adapter
        adapter = new BeersAdapter(this.getActivity(), listItems);

        //Set adapter and that's it.
        beer_list.setAdapter(adapter);
        return view;
    }

    public void initSwiper(View view, ListView beer_list) {
        swipeView = (SwipeRefreshLayout) view.findViewById(R.id.beer_swipe_container);
        swipeView.setOnRefreshListener(this);
        swipeView.setColorSchemeResources(android.R.color.holo_red_light);

        swipeView.setOnRefreshListener(this);

        beer_list.setOnScrollListener(new AbsListView.OnScrollListener() {
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
        GetJson g = new GetJson(this.getActivity(), this, GetJson.BEER, PreferenceManager.getDefaultSharedPreferences(this.getActivity()), false);
        g.execute();
    }

    public void populateList(SharedPreferences prefs) {
        listItems.clear();
        JSONArray json;
        try {
            if ((json = new JSONArray(prefs.getString("beerData", null))) != null) {
                for (int i = 0; i < json.length(); i++) {
                    JSONObject temp;

                    temp = json.getJSONObject(i);
                    Beer tempBeer = new Beer(temp.getString("name").toString(), temp.getString("soort").toString(),
                            temp.getString("picture"), temp.getString("percentage"), temp.getString("brewer"), temp.getString("country"));
                    listItems.add(tempBeer);
                    if (adapter != null) {
                        adapter.notifyDataSetChanged();
                    }
                    ;
                }
            }
        } catch (JSONException e) {
            if(this.getActivity() != null) {
                Toast.makeText(getActivity(), getString(R.string.toast_downloaderror), Toast.LENGTH_SHORT).show();
            }
        }
        if (swipeView != null) {
            swipeView.setRefreshing(false);
        }
    }
}
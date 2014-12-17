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
import com.ecci.Hamers.Adapters.QuotesAdapter;
import com.ecci.Hamers.GetJson;
import com.ecci.Hamers.Quote;
import com.ecci.Hamers.R;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class QuoteListFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    public QuoteListFragment() {
        // Empty constructor required for fragment subclasses
    }

    ArrayList<Quote> listItems = new ArrayList<Quote>();
    ArrayAdapter<Quote> adapter;
    SwipeRefreshLayout swipeView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.quote_list_fragment, container, false);
        ListView quote_list = (ListView) view.findViewById(R.id.quotes_listView);

        initSwiper(view, quote_list);

        // 1. pass context and data to the custom adapter
        adapter = new QuotesAdapter(this.getActivity(), listItems);

        //Set adapter and that's it.
        quote_list.setAdapter(adapter);

        return view;
    }

    public void initSwiper(View view, ListView quote_list) {
        swipeView = (SwipeRefreshLayout) view.findViewById(R.id.quotes_swipe_container);
        swipeView.setOnRefreshListener(this);
        swipeView.setColorSchemeResources(android.R.color.holo_red_light);

        swipeView.setOnRefreshListener(this);

        quote_list.setOnScrollListener(new AbsListView.OnScrollListener() {
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
        GetJson g = new GetJson(this, GetJson.QUOTE, PreferenceManager.getDefaultSharedPreferences(this.getActivity()));
        g.execute();
    }

    public void populateList(JSONArray json){
        System.out.println(json);

        listItems.clear();
        for(int i = 0; i< json.length(); i++){
            JSONObject temp;
            try {
                temp = json.getJSONObject(i);
                Quote tempQuote = new Quote(temp.getString("text").toString(), temp.getString("created_at").toString(), temp.getInt("user_id"));
                listItems.add(tempQuote);
                adapter.notifyDataSetChanged();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        swipeView.setRefreshing(false);
    }
}
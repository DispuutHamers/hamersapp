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
import com.ecci.Hamers.Adapters.QuotesAdapter;
import com.ecci.Hamers.GetJson;
import com.ecci.Hamers.Quote;
import com.ecci.Hamers.R;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.util.ArrayList;

import static com.ecci.Hamers.MainActivity.parseDate;

public class QuoteListFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    public QuoteListFragment() {
    }

    ArrayList<Quote> listItems = new ArrayList<Quote>();
    ArrayAdapter<Quote> adapter;
    SwipeRefreshLayout swipeView;

    SharedPreferences prefs;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.quote_list_fragment, container, false);
        ListView quote_list = (ListView) view.findViewById(R.id.quotes_listView);

        initSwiper(view, quote_list);

        // 1. pass context and data to the custom adapter
        adapter = new QuotesAdapter(this.getActivity(), listItems);

        // 2. Set adapter and that's it.
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
        prefs = PreferenceManager.getDefaultSharedPreferences(this.getActivity());
        if(prefs.getString("userData", null) != null) {
            GetJson g = new GetJson(this, GetJson.QUOTE, prefs);
            g.execute();
        }
    }

    private JSONObject getUser(int id) {
        JSONArray users;
        try {
            users = new JSONArray(prefs.getString("userData", null));
            for (int i = 0; i < users.length(); i++) {
                try {
                    JSONObject temp = users.getJSONObject(i);
                    if (temp.getInt("id") == id) {
                        return temp;
                    }
                } catch (JSONException e) {
                    return (null);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void populateList(JSONArray json) {
        listItems.clear();
        try {
            for (int i = 0; i < json.length(); i++) {
                JSONObject quote = json.getJSONObject(i);
                JSONObject user;

                String username;
                if ((user = getUser(quote.getInt("user_id"))) != null) {
                    username = user.getString("name");
                } else {
                    username = "unknown user";
                }

                String tempDate = quote.getString("created_at").substring(0, 10);
                String tempTijd = quote.getString("created_at").substring(11, 16);

                String date = tempTijd + " - " + parseDate(tempDate);

                Quote tempQuote = new Quote(username, quote.getString("text").toString(), date, user.getInt("id"));
                listItems.add(tempQuote);
                adapter.notifyDataSetChanged();

            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        swipeView.setRefreshing(false);
    }
}
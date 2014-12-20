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
import com.ecci.Hamers.Adapters.UsersAdapter;
import com.ecci.Hamers.GetJson;
import com.ecci.Hamers.R;
import com.ecci.Hamers.User;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class UserFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    public UserFragment() {
        // Empty constructor required for fragment subclasses
    }

    ArrayList<User> listItems = new ArrayList<User>();
    ArrayAdapter<User> adapter;
    SwipeRefreshLayout swipeView;
    SharedPreferences prefs;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.user_fragment, container, false);
        ListView user_list = (ListView) view.findViewById(R.id.users_listView);

        initSwiper(view, user_list);

        // 1. pass context and data to the custom adapter
        adapter = new UsersAdapter(this.getActivity(), listItems);

        // 2. Set adapter and that's it.
        user_list.setAdapter(adapter);

        return view;
    }

    public void initSwiper(View view, ListView user_list) {
        swipeView = (SwipeRefreshLayout) view.findViewById(R.id.users_swipe_container);
        swipeView.setOnRefreshListener(this);
        swipeView.setColorSchemeResources(android.R.color.holo_red_light);

        swipeView.setOnRefreshListener(this);

        user_list.setOnScrollListener(new AbsListView.OnScrollListener() {
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
        GetJson g = new GetJson(this.getActivity(), this, GetJson.USER, PreferenceManager.getDefaultSharedPreferences(this.getActivity()), false);
        g.execute();
    }

    public void populateList(SharedPreferences prefs){
        JSONArray json;
        try {
        if ((json = new JSONArray(prefs.getString("userData", null))) != null) {
            listItems.clear();
            for (int i = 0; i < json.length(); i++) {
                JSONObject temp;

                    temp = json.getJSONObject(i);
                    User tempUser = new User(temp.getString("name"), temp.getInt("id"), 0, 0);
                    listItems.add(tempUser);
                if(adapter != null){adapter.notifyDataSetChanged();};
            }
        }} catch (JSONException e) {
            Toast.makeText(getActivity(), getString(R.string.toast_downloaderror), Toast.LENGTH_SHORT).show();
        }
        if(swipeView != null) {swipeView.setRefreshing(false);}
    }
}
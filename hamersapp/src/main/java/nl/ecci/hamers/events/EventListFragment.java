package nl.ecci.hamers.events;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

import nl.ecci.hamers.MainActivity;
import nl.ecci.hamers.R;
import nl.ecci.hamers.helpers.HamersFragment;
import nl.ecci.hamers.loader.GetCallback;
import nl.ecci.hamers.loader.Loader;

import static nl.ecci.hamers.MainActivity.prefs;

public class EventListFragment extends HamersFragment {

    private final ArrayList<Event> dataSet = new ArrayList<>();
    private EventListAdapter adapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView event_list;
    private boolean upcoming;

    public EventListFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.hamers_fragment, container, false);
        event_list = (RecyclerView) view.findViewById(R.id.hamers_recyclerview);

        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        event_list.setLayoutManager(mLayoutManager);

        setHasOptionsMenu(true);

        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.hamers_swipe_container);
        initSwiper(event_list, mLayoutManager, swipeRefreshLayout);

        adapter = new EventListAdapter(getActivity(), dataSet);
        event_list.setAdapter(adapter);

        upcoming = getArguments().getBoolean(EventFragmentPagerAdapter.upcoming, false);

        FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.hamers_fab);
        if (fab != null) {
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getActivity(), NewEventActivity.class);
                    startActivityForResult(intent, -1);
                }
            });
        }

        new populateList().execute();
        onRefresh();

        return view;
    }

    @Override
    public void onRefresh() {
        setRefreshing(true);
        if (upcoming) {
            Loader.getData(Loader.UPCOMINGEVENTURL, getContext(), MainActivity.prefs, new GetCallback() {
                @Override
                public void onSuccess(String response) {
                    new populateList().execute(response);
                }

                @Override
                public void onError(VolleyError error) {
                    // Nothing
                }
            });
        } else {
            Loader.getData(Loader.EVENTURL, getContext(), MainActivity.prefs, new GetCallback() {
                @Override
                public void onSuccess(String response) {
                    new populateList().execute(response);
                }

                @Override
                public void onError(VolleyError error) {
                    // Nothing
                }
            });
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.scroll_top:
                event_list.smoothScrollToPosition(0);
                return true;
            default:
                return false;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        onRefresh();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.news_event_menu, menu);
        MenuItem menuItem = menu.findItem(R.id.event_search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(menuItem);
        if (searchView != null) {
            searchView.setQueryHint(getString(R.string.search_hint));
            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String s) {
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String s) {
                    adapter.getFilter().filter(s.toLowerCase());
                    return false;
                }
            });
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        onRefresh();
    }

    private class populateList extends AsyncTask<String, Void, ArrayList<Event>> {
        @Override
        protected final ArrayList<Event> doInBackground(String... params) {
            GsonBuilder gsonBuilder = new GsonBuilder();
            gsonBuilder.setDateFormat(MainActivity.dbDF.toPattern());
            Gson gson = gsonBuilder.create();
            Type type = new TypeToken<ArrayList<Event>>() {
            }.getType();

            if (params.length > 0) {
                return gson.fromJson(params[0], type);
            } else {
                String key = Loader.EVENTURL;
                if (upcoming) {
                    key = Loader.UPCOMINGEVENTURL;
                }
                return gson.fromJson(prefs.getString(key, null), type);
            }
        }

        @Override
        protected void onPostExecute(ArrayList<Event> result) {
            setRefreshing(false);
            if (!result.isEmpty()) {
                dataSet.clear();
                dataSet.addAll(result);
                Collections.reverse(dataSet);
                if (adapter != null) {
                    adapter.notifyDataSetChanged();
                }
            }
        }
    }
}


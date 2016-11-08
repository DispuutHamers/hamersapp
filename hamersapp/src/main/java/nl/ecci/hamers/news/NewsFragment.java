package nl.ecci.hamers.news;

import android.os.AsyncTask;
import android.os.Bundle;
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

import nl.ecci.hamers.MainActivity;
import nl.ecci.hamers.R;
import nl.ecci.hamers.helpers.HamersFragment;
import nl.ecci.hamers.loader.GetCallback;
import nl.ecci.hamers.loader.Loader;

import static nl.ecci.hamers.MainActivity.prefs;

public class NewsFragment extends HamersFragment {

    private final ArrayList<News> dataSet = new ArrayList<>();
    private NewsAdapter adapter;
    private RecyclerView news_list;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.hamers_fragment, container, false);
        news_list = (RecyclerView) view.findViewById(R.id.hamers_recyclerview);

        setHasOptionsMenu(true);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        news_list.setLayoutManager(layoutManager);

        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.hamers_swipe_container);
        initSwiper(news_list, layoutManager, swipeRefreshLayout);

        adapter = new NewsAdapter(dataSet);
        news_list.setAdapter(adapter);

        onRefresh();

        return view;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.scroll_top:
                news_list.smoothScrollToPosition(0);
                return true;
            default:
                return false;
        }
    }

    @Override
    public void onRefresh() {
        setRefreshing(true);
        Loader.getData(Loader.NEWSURL, getContext(), MainActivity.prefs, new GetCallback() {
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

    private class populateList extends AsyncTask<String, Void, ArrayList<News>> {
        @Override
        protected final ArrayList<News> doInBackground(String... params) {
            ArrayList<News> result;
            Type type = new TypeToken<ArrayList<News>>() {
            }.getType();
            GsonBuilder gsonBuilder = new GsonBuilder();
            gsonBuilder.setDateFormat(MainActivity.dbDF.toPattern());
            Gson gson = gsonBuilder.create();

            if (params.length > 0) {
                result = gson.fromJson(params[0], type);
            } else {
                result = gson.fromJson(prefs.getString(Loader.NEWSURL, null), type);
            }
            return result;
        }

        @Override
        protected void onPostExecute(ArrayList<News> result) {
            if (result != null) {
                dataSet.clear();
                dataSet.addAll(result);
                Collections.reverse(dataSet);
                if (adapter != null) {
                    adapter.notifyDataSetChanged();
                }
            }
            setRefreshing(false);
        }
    }
}

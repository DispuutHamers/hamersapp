package nl.ecci.hamers.quotes;

import android.os.AsyncTask;
import android.os.Bundle;
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

import org.json.JSONArray;

import java.lang.reflect.Type;
import java.util.ArrayList;

import nl.ecci.hamers.MainActivity;
import nl.ecci.hamers.R;
import nl.ecci.hamers.helpers.DataManager;
import nl.ecci.hamers.helpers.DividerItemDecoration;
import nl.ecci.hamers.helpers.VolleyCallback;

public class QuoteFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    private final ArrayList<Quote> dataSet = new ArrayList<>();
    private QuoteAdapter adapter;
    private RecyclerView quote_list;
    private SwipeRefreshLayout swipeRefreshLayout;

    public QuoteFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.quote_fragment, container, false);
        quote_list = (RecyclerView) view.findViewById(R.id.quotes_recyclerview);

        setHasOptionsMenu(true);

        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        quote_list.setLayoutManager(mLayoutManager);
        quote_list.addItemDecoration(new DividerItemDecoration(getActivity()));

        adapter = new QuoteAdapter(dataSet, getContext());
        quote_list.setAdapter(adapter);

        initSwiper(view, quote_list, mLayoutManager);

        onRefresh();

        return view;
    }

    private void initSwiper(View view, final RecyclerView event_list, final LinearLayoutManager lm) {
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.quotes_swipe_container);
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setColorSchemeResources(android.R.color.holo_red_light);

        event_list.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrolled(RecyclerView view, int dx, int dy) {
                swipeRefreshLayout.setEnabled(lm.findFirstCompletelyVisibleItemPosition() == 0);
            }
        });
    }

    @Override
    @SuppressWarnings("unchecked")
    public void onRefresh() {
        setRefreshing(true);
        DataManager.getData(new VolleyCallback() {
            @Override
            public void onSuccess() {
                new populateList().execute(dataSet);
            }
            @Override
            public void onError(VolleyError error) {
                // Nothing
            }
        }, getContext(), MainActivity.prefs, DataManager.QUOTEURL, DataManager.QUOTEKEY);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.quote_menu, menu);
        MenuItem menuItem = menu.findItem(R.id.quote_search);
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

    @Override
    public void onResume() {
        super.onResume();
        onRefresh();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.scroll_top:
                scrollTop();
                return true;
            default:
                return false;
        }
    }

    private void scrollTop() {
        quote_list.smoothScrollToPosition(0);
    }

    private void setRefreshing(final Boolean bool) {
        if (swipeRefreshLayout != null) {
            swipeRefreshLayout.post(new Runnable() {
                @Override
                public void run() {
                    swipeRefreshLayout.setRefreshing(bool);
                }
            });
        }
    }

    private class populateList extends AsyncTask<ArrayList<Quote>, Void, ArrayList<Quote>> {
        @SafeVarargs
        @Override
        protected final ArrayList<Quote> doInBackground(ArrayList<Quote>... param) {
            ArrayList<Quote> dataSet = new ArrayList<>();
            JSONArray json;
            if ((json = DataManager.getJsonArray(MainActivity.prefs, DataManager.QUOTEKEY)) != null) {
                GsonBuilder gsonBuilder = new GsonBuilder();
                gsonBuilder.setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
                Gson gson = gsonBuilder.create();

                Type type = new TypeToken<ArrayList<Quote>>() {
                }.getType();
                dataSet = gson.fromJson(json.toString(), type);
            }
            return dataSet;
        }

        @Override
        protected void onPostExecute(ArrayList<Quote> result) {
            if (!result.isEmpty()) {
                dataSet.clear();
                dataSet.addAll(result);
                if (QuoteFragment.this.adapter != null) {
                    QuoteFragment.this.adapter.notifyDataSetChanged();
                }
            }
            setRefreshing(false);
        }
    }
}
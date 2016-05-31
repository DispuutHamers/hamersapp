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
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;

import nl.ecci.hamers.MainActivity;
import nl.ecci.hamers.R;
import nl.ecci.hamers.helpers.DataManager;
import nl.ecci.hamers.helpers.DividerItemDecoration;
import nl.ecci.hamers.users.User;

import static nl.ecci.hamers.MainActivity.parseDate;

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
    public void onRefresh() {
        setRefreshing(true);
        DataManager.getData(getContext(), MainActivity.prefs, DataManager.QUOTEURL, DataManager.QUOTEKEY);
    }

    @SuppressWarnings("unchecked")
    public void populateList() {
        new populateList().execute(dataSet);
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
        populateList();
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

    public class populateList extends AsyncTask<ArrayList<Quote>, Void, ArrayList<Quote>> {
        @SafeVarargs
        @Override
        protected final ArrayList<Quote> doInBackground(ArrayList<Quote>... param) {
            final ArrayList<Quote> dataSet = new ArrayList<>();
            JSONArray json;
            try {
                if ((json = DataManager.getJsonArray(MainActivity.prefs, DataManager.QUOTEKEY)) != null) {
                    for (int i = 0; i < json.length(); i++) {
                        JSONObject quote = json.getJSONObject(i);
                        User user;

                        String username;
                        int id;
                        if ((user = DataManager.getUser(MainActivity.prefs, quote.getInt("user_id"))) != null) {
                            username = user.getName();
                            id = user.getUserID();
                        } else {
                            username = "unknown user";
                            id = -1;
                        }

                        String tempDate = quote.getString("created_at");
                        Date date = parseDate(tempDate);
                        Quote tempQuote = new Quote(username, quote.getString("text"), date, id);
                        dataSet.add(tempQuote);
                    }
                }
            } catch (JSONException ignored) {
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

        @Override
        protected void onPreExecute() {
            setRefreshing(true);
        }
    }
}
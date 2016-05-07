package nl.ecci.hamers.quotes;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
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

import nl.ecci.hamers.R;
import nl.ecci.hamers.helpers.DataManager;
import nl.ecci.hamers.helpers.DividerItemDecoration;

import static nl.ecci.hamers.MainActivity.parseDate;

public class QuoteFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    private final ArrayList<Quote> dataSet = new ArrayList<>();
    private QuoteAdapter adapter;
    private RecyclerView quote_list;
    private SwipeRefreshLayout swipeRefreshLayout;
    private SharedPreferences prefs;

    public QuoteFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.quote_fragment, container, false);
        quote_list = (RecyclerView) view.findViewById(R.id.quotes_recyclerview);

        setHasOptionsMenu(true);

        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        quote_list.setLayoutManager(mLayoutManager);
        quote_list.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL_LIST));

        adapter = new QuoteAdapter(getActivity(), dataSet);
        quote_list.setAdapter(adapter);

        prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());

        initSwiper(view, quote_list, mLayoutManager);

        onRefresh();

        return view;
    }

    private void initSwiper(View view, final RecyclerView event_list, final LinearLayoutManager lm) {
        // SwipeRefreshLayout
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
        DataManager.getData(getContext(), prefs, DataManager.QUOTEURL, DataManager.QUOTEKEY);
    }

    public void populateList(SharedPreferences prefs) {
        dataSet.clear();
        JSONArray json;
        try {
            if ((json = DataManager.getJsonArray(prefs, DataManager.QUOTEKEY)) != null) {
                for (int i = 0; i < json.length(); i++) {
                    JSONObject quote = json.getJSONObject(i);
                    JSONObject user;

                    String username;
                    int id;
                    if ((user = DataManager.getUser(prefs, quote.getInt("user_id"))) != null) {
                        username = user.getString("name");
                        id = user.getInt("id");
                    } else {
                        username = "unknown user";
                        id = -1;
                    }

                    String tempDate = quote.getString("created_at");

                    Date date = parseDate(tempDate);

                    Quote tempQuote = new Quote(username, quote.getString("text"), date, id);
                    dataSet.add(tempQuote);
                    if (adapter != null) {
                        adapter.notifyDataSetChanged();
                    }
                }
            }
        } catch (JSONException e) {
            Toast.makeText(getActivity(), getString(R.string.snackbar_downloaderror), Toast.LENGTH_SHORT).show();
        }
        if (swipeRefreshLayout != null) {
            swipeRefreshLayout.setRefreshing(false);
        }
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
        populateList(prefs);
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
}
package nl.ecci.Hamers.Quotes;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.*;
import android.widget.RelativeLayout;
import android.widget.Toast;
import com.melnykov.fab.FloatingActionButton;
import nl.ecci.Hamers.Helpers.DataManager;
import nl.ecci.Hamers.Helpers.DividerItemDecoration;
import nl.ecci.Hamers.Helpers.GetJson;
import nl.ecci.Hamers.R;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.util.ArrayList;

import static nl.ecci.Hamers.MainActivity.parseDate;

public class QuoteFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    public static RelativeLayout parentLayout;
    private final ArrayList<Quote> dataSet = new ArrayList<>();
    private QuoteAdapter adapter;
    private RecyclerView quote_list;
    private SwipeRefreshLayout swipeView;

    public QuoteFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.quote_list_fragment, container, false);
        quote_list = (RecyclerView) view.findViewById(R.id.quotes_recyclerview);

        parentLayout = (RelativeLayout) view.findViewById(R.id.quote_list_parent);

        setHasOptionsMenu(true);

        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        quote_list.setLayoutManager(mLayoutManager);

        adapter = new QuoteAdapter(this.getActivity(), dataSet);
        quote_list.setAdapter(adapter);
        quote_list.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL_LIST));

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());

        if (prefs.getString("quoteData", null) != null) {
            populateList(prefs);
        } else {
            onRefresh();
        }

        initSwiper(view, quote_list, mLayoutManager);

        // Floating action button
        FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.quote_add_button);
        fab.attachToRecyclerView(quote_list);

        return view;
    }

    private void initSwiper(View view, final RecyclerView event_list, final LinearLayoutManager lm) {
        // SwipeRefreshLayout
        swipeView = (SwipeRefreshLayout) view.findViewById(R.id.quotes_swipe_container);
        swipeView.setOnRefreshListener(this);
        swipeView.setColorSchemeResources(android.R.color.holo_red_light);

        event_list.setOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrolled(RecyclerView view, int dx, int dy) {
                swipeView.setEnabled(lm.findFirstCompletelyVisibleItemPosition() == 0);
            }
        });
    }

    @Override
    public void onRefresh() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this.getActivity());
        if (prefs.getString("userData", null) != null) {
            GetJson g = new GetJson(this.getActivity(), this, GetJson.QUOTEURL, prefs);
            g.execute();
        }
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

                    String tempDate = quote.getString("created_at").substring(0, 10);
                    String tempTijd = quote.getString("created_at").substring(11, 16);

                    String date = tempTijd + " - " + parseDate(tempDate);

                    Quote tempQuote = new Quote(username, quote.getString("text"), date, id);
                    dataSet.add(tempQuote);
                    if (adapter != null) {
                        adapter.notifyDataSetChanged();
                    }
                }
            }
        } catch (JSONException | ParseException e) {
            Toast.makeText(getActivity(), getString(R.string.snackbar_downloaderror), Toast.LENGTH_SHORT).show();
        }
        if (swipeView != null) {
            swipeView.setRefreshing(false);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.event_list_menu, menu);
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
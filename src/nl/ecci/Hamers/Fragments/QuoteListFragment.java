package nl.ecci.Hamers.Fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.*;
import android.widget.*;
import nl.ecci.Hamers.Adapters.QuotesAdapter;
import nl.ecci.Hamers.GetJson;
import nl.ecci.Hamers.Quote;
import nl.ecci.Hamers.R;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.util.ArrayList;

import static nl.ecci.Hamers.MainActivity.parseDate;

public class QuoteListFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    public SwipeRefreshLayout swipeView;
    ArrayList<Quote> listItems = new ArrayList<Quote>();
    ArrayAdapter<Quote> adapter;
    public QuoteListFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.quote_list_fragment, container, false);
        ListView quote_list = (ListView) view.findViewById(R.id.quotes_listView);

        setHasOptionsMenu(true);

        initSwiper(view, quote_list);

        // 1. pass context and data to the custom adapter
        adapter = new QuotesAdapter(this.getActivity(), listItems);

        // 2. Set adapter and that's it.
        quote_list.setAdapter(adapter);
        quote_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                System.out.println("Item: " + id + " at position:" + position);
                DialogFragment newQuoteFragment = new ViewQuoteFragment();
                newQuoteFragment.show(getActivity().getSupportFragmentManager(), "quote");
            }
        });

        return view;
    }

    public void initSwiper(View view, final ListView quote_list) {
        swipeView = (SwipeRefreshLayout) view.findViewById(R.id.quotes_swipe_container);
        swipeView.setOnRefreshListener(this);
        swipeView.setColorSchemeResources(android.R.color.holo_red_light);

        swipeView.setOnRefreshListener(this);

        quote_list.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                boolean enable = false;
                if (quote_list != null && quote_list.getChildCount() > 0) {
                    // check if the first item of the list is visible
                    boolean firstItemVisible = quote_list.getFirstVisiblePosition() == 0;
                    // check if the top of the first item is visible
                    boolean topOfFirstItemVisible = quote_list.getChildAt(0).getTop() == 0;
                    // enabling or disabling the refresh layout
                    enable = firstItemVisible && topOfFirstItemVisible;
                }
                swipeView.setEnabled(enable);
            }
        });
    }

    @Override
    public void onRefresh() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this.getActivity());
        if (prefs.getString("userData", null) != null) {
            GetJson g = new GetJson(this.getActivity(), this, GetJson.QUOTE, prefs, false);
            g.execute();
        }
    }

    private JSONObject getUser(int id, SharedPreferences prefs) {
        JSONArray users;
        try {
            if ((users = new JSONArray(prefs.getString("userData", null))) != null) {
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
            }
        } catch (JSONException e) {
            Toast.makeText(getActivity(), getString(R.string.toast_userloaderror), Toast.LENGTH_SHORT).show();
        }
        return null;
    }

    public void populateList(SharedPreferences prefs) {
        listItems.clear();
        JSONArray json;
        try {
            if ((json = new JSONArray(prefs.getString("quoteData", null))) != null) {
                for (int i = 0; i < json.length(); i++) {
                    JSONObject quote = json.getJSONObject(i);
                    JSONObject user;

                    String username;
                    int id;
                    if ((user = getUser(quote.getInt("user_id"), prefs)) != null) {
                        username = user.getString("name");
                        id = user.getInt("id");
                    } else {
                        username = "unknown user";
                        id = -1;
                    }

                    String tempDate = quote.getString("created_at").substring(0, 10);
                    String tempTijd = quote.getString("created_at").substring(11, 16);

                    String date = tempTijd + " - " + parseDate(tempDate);

                    Quote tempQuote = new Quote(username, quote.getString("text").toString(), date, id);
                    listItems.add(tempQuote);
                    if (adapter != null) {
                        adapter.notifyDataSetChanged();
                    }
                    ;

                }
            }
        } catch (JSONException e) {
            Toast.makeText(getActivity(), getString(R.string.toast_downloaderror), Toast.LENGTH_SHORT).show();
        } catch (ParseException e) {
            Toast.makeText(getActivity(), getString(R.string.toast_downloaderror), Toast.LENGTH_SHORT).show();
        }
        if (swipeView != null) {
            swipeView.setRefreshing(false);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.quote_list_menu, menu);
    }
}
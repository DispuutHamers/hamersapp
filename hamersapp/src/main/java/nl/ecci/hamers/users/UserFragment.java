package nl.ecci.hamers.users;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import nl.ecci.hamers.R;
import nl.ecci.hamers.helpers.DataManager;

public class UserFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    private final ArrayList<User> listItems = new ArrayList<>();
    private final Comparator<User> nameComperator = new Comparator<User>() {
        @Override
        public int compare(User user1, User user2) {

            String name1 = user1.getUsername();
            String name2 = user2.getUsername();

            return name1.compareToIgnoreCase(name2);
        }
    };
    private final Comparator<User> quoteComperator = new Comparator<User>() {
        @Override
        public int compare(User user1, User user2) {

            int quote1 = user1.getQuotecount();
            int quote2 = user2.getQuotecount();

            return ((Integer) quote2).compareTo(quote1);
        }
    };
    private final Comparator<User> reviewComperator = new Comparator<User>() {
        @Override
        public int compare(User user1, User user2) {

            int review1 = user1.getReviewcount();
            int review2 = user2.getReviewcount();

            return ((Integer) review2).compareTo(review1);
        }
    };
    private ArrayAdapter<User> adapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    private SharedPreferences prefs;

    public UserFragment() {
        // Empty constructor required for fragment subclasses
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.user_fragment, container, false);
        ListView user_list = (ListView) view.findViewById(R.id.users_listView);

        setHasOptionsMenu(true);

        adapter = new UserAdapter(this.getActivity(), listItems);
        user_list.setAdapter(adapter);

        prefs = PreferenceManager.getDefaultSharedPreferences(this.getActivity());

        initSwiper(view, user_list);

        sort();

        onRefresh();

        return view;
    }

    private void initSwiper(View view, final ListView user_list) {
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.users_swipe_container);
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setColorSchemeResources(android.R.color.holo_red_light);

        swipeRefreshLayout.setOnRefreshListener(this);

        user_list.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                boolean enable = false;
                if (user_list != null && user_list.getChildCount() > 0) {
                    // check if the first item of the list is visible
                    boolean firstItemVisible = user_list.getFirstVisiblePosition() == 0;
                    // check if the top of the first item is visible
                    boolean topOfFirstItemVisible = user_list.getChildAt(0).getTop() == 0;
                    // enabling or disabling the refresh layout
                    enable = firstItemVisible && topOfFirstItemVisible;
                }
                swipeRefreshLayout.setEnabled(enable);
            }
        });
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.user_menu, menu);
    }

    @Override
    public void onRefresh() {
        DataManager.getData(getContext(), prefs, DataManager.USERURL, DataManager.USERKEY);
    }

    public void populateList(SharedPreferences prefs) {
        JSONArray json;
        try {
            if (prefs != null && (json = DataManager.getJsonArray(prefs, DataManager.USERKEY)) != null) {
                listItems.clear();
                for (int i = 0; i < json.length(); i++) {
                    JSONObject temp;
                    temp = json.getJSONObject(i);
                    User tempUser = new User(temp.getString("name"), temp.getInt("id"), temp.getString("email"), temp.getInt("quotes"), temp.getInt("reviews"));
                    listItems.add(tempUser);
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
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.sort_username:
                sortByUsername();
                return true;
            case R.id.sort_quotes:
                sortbyQuoteCount();
                return true;
            case R.id.sort_reviews:
                sortbyReviewCount();
                return true;
            default:
                return false;
        }
    }

    private void sort() {
        prefs = PreferenceManager.getDefaultSharedPreferences(this.getActivity());
        String sortPref = prefs.getString("userSort", "");
        switch (sortPref) {
            case "name":
                sortByUsername();
                break;
            case "quotecount":
                sortbyQuoteCount();
                break;
            case "reviewcount":
                sortbyReviewCount();
                break;
        }
    }

    private void sortByUsername() {
        Collections.sort(listItems, nameComperator);
        adapter.notifyDataSetChanged();
    }

    private void sortbyQuoteCount() {
        Collections.sort(listItems, quoteComperator);
        adapter.notifyDataSetChanged();
    }

    private void sortbyReviewCount() {
        Collections.sort(listItems, reviewComperator);
        adapter.notifyDataSetChanged();
    }
}

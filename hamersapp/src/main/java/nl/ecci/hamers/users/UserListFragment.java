package nl.ecci.hamers.users;

import android.os.AsyncTask;
import android.os.Bundle;
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

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import nl.ecci.hamers.MainActivity;
import nl.ecci.hamers.R;
import nl.ecci.hamers.helpers.DataManager;

public class UserListFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    private final ArrayList<User> dataSet = new ArrayList<>();
    private ArrayAdapter<User> adapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    private boolean exUser;

    public UserListFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.user_list_fragment, container, false);
        ListView user_list = (ListView) view.findViewById(R.id.users_listView);

        setHasOptionsMenu(true);

        adapter = new UserListAdapter(this.getActivity(), dataSet);
        user_list.setAdapter(adapter);

        initSwiper(view, user_list);

        exUser = getArguments().getBoolean(UserFragmentPagerAdapter.exUser, false);

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
        setRefreshing(true);
        DataManager.getData(getContext(), MainActivity.prefs, DataManager.USERURL, DataManager.USERKEY);
    }

    @SuppressWarnings("unchecked")
    public void populateList() {
        new populateList().execute(dataSet);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.sort_username:
                sortByUsername();
                return true;
            case R.id.sort_quotes:
                sortByQuoteCount();
                return true;
            case R.id.sort_reviews:
                sortByReviewCount();
                return true;
            case R.id.sort_batch:
                sortByBatch();
            default:
                return false;
        }
    }

    private void sort() {
        String sortPref = MainActivity.prefs.getString("userSort", "");
        switch (sortPref) {
            case "name":
                sortByUsername();
                break;
            case "quotecount":
                sortByQuoteCount();
                break;
            case "reviewcount":
                sortByReviewCount();
                break;
        }
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

    private void sortByUsername() {
        final Comparator<User> nameComperator = new Comparator<User>() {
            @Override
            public int compare(User user1, User user2) {
                return user1.getName().compareToIgnoreCase(user2.getName());
            }
        };
        Collections.sort(dataSet, nameComperator);
        adapter.notifyDataSetChanged();
    }

    private void sortByQuoteCount() {
        final Comparator<User> quoteComperator = new Comparator<User>() {
            @Override
            public int compare(User user1, User user2) {
                return user2.getQuoteCount() - user1.getQuoteCount();
            }
        };
        Collections.sort(dataSet, quoteComperator);
        adapter.notifyDataSetChanged();
    }

    private void sortByReviewCount() {
        final Comparator<User> reviewComperator = new Comparator<User>() {
            @Override
            public int compare(User user1, User user2) {
                return user2.getReviewCount() - user1.getReviewCount();
            }
        };
        Collections.sort(dataSet, reviewComperator);
        adapter.notifyDataSetChanged();
    }

    private void sortByBatch() {
        final Comparator<User> batchComperator = new Comparator<User>() {
            @Override
            public int compare(User user1, User user2) {
                return user1.getBatch() - user2.getBatch();
            }
        };
        Collections.sort(dataSet, batchComperator);
        adapter.notifyDataSetChanged();
    }

    private class populateList extends AsyncTask<ArrayList<User>, Void, ArrayList<User>> {
        @SafeVarargs
        @Override
        protected final ArrayList<User> doInBackground(ArrayList<User>... param) {
            ArrayList<User> dataSet = new ArrayList<>();
            JSONArray json;
            if ((json = DataManager.getJsonArray(MainActivity.prefs, DataManager.USERKEY)) != null) {
                GsonBuilder gsonBuilder = new GsonBuilder();
                gsonBuilder.setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
                Gson gson = gsonBuilder.create();
                try {
                    for (int i = 0; i < json.length(); i++) {
                        JSONObject temp = json.getJSONObject(i);
                        User user = gson.fromJson(temp.toString(), User.class);

                        if (exUser && user.getMember() != User.Member.LID) {
                            dataSet.add(user);
                        } else if (!exUser && user.getMember() == User.Member.LID) {
                            dataSet.add(user);
                        }
                    }
                } catch (JSONException ignored) {
                }
            }
            return dataSet;
        }

        @Override
        protected void onPostExecute(ArrayList<User> result) {
            if (!result.isEmpty()) {
                dataSet.clear();
                dataSet.addAll(result);
                if (UserListFragment.this.adapter != null) {
                    UserListFragment.this.adapter.notifyDataSetChanged();
                }
            }
            sort();
            setRefreshing(false);
        }

        @Override
        protected void onPreExecute() {
            setRefreshing(true);
        }
    }
}

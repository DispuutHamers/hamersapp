package nl.ecci.hamers.users;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import nl.ecci.hamers.MainActivity;
import nl.ecci.hamers.R;
import nl.ecci.hamers.helpers.DividerItemDecoration;
import nl.ecci.hamers.helpers.HamersFragment;
import nl.ecci.hamers.loader.GetCallback;
import nl.ecci.hamers.loader.Loader;

import static nl.ecci.hamers.MainActivity.prefs;

public class UserListFragment extends HamersFragment implements SwipeRefreshLayout.OnRefreshListener {

    private final ArrayList<User> dataSet = new ArrayList<>();
    private UserListAdapter adapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    private boolean exUser;

    public UserListFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.user_list_fragment, container, false);
        RecyclerView user_list = (RecyclerView) view.findViewById(R.id.user_list);

        setHasOptionsMenu(true);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        user_list.setLayoutManager(layoutManager);
        user_list.setItemAnimator(new DefaultItemAnimator());
        user_list.addItemDecoration(new DividerItemDecoration(getActivity()));

        adapter = new UserListAdapter(dataSet, getActivity());
        user_list.setAdapter(adapter);

        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.users_swipe_container);
        initSwiper(user_list, layoutManager, swipeRefreshLayout);

        exUser = getArguments().getBoolean(UserFragmentPagerAdapter.exUser, false);

        sort();

        new populateList().execute();
        onRefresh();

        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.user_menu, menu);
    }

    @Override
    public void onRefresh() {
        setRefreshing(true);
        Loader.getData(Loader.USERURL, getContext(), MainActivity.prefs, new GetCallback() {
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

    private class populateList extends AsyncTask<String, Void, ArrayList<User>> {
        @Override
        protected final ArrayList<User> doInBackground(String... params) {
            ArrayList<User> result = new ArrayList<>();
            ArrayList<User> tempList;
            Type type = new TypeToken<ArrayList<User>>() {
            }.getType();
            GsonBuilder gsonBuilder = new GsonBuilder();
            gsonBuilder.setDateFormat(MainActivity.dbDF.toPattern());
            Gson gson = gsonBuilder.create();

            if (params.length > 0) {
                tempList = gson.fromJson(params[0], type);
            } else {
                tempList = gson.fromJson(prefs.getString(Loader.USERURL, null), type);
            }

            if (tempList != null) {
                for (User user : tempList) {
                    if (exUser && user.getMember() != User.Member.LID) {
                        result.add(user);
                    } else if (!exUser && user.getMember() == User.Member.LID) {
                        result.add(user);
                    }
                }
            }
            return result;
        }

        @Override
        protected void onPostExecute(ArrayList<User> result) {
            if (result != null) {
                dataSet.clear();
                dataSet.addAll(result);
                if (UserListFragment.this.adapter != null) {
                    UserListFragment.this.adapter.notifyDataSetChanged();
                }
            }
            sort();
            setRefreshing(false);
        }
    }
}

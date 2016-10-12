package nl.ecci.hamers.beers;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import nl.ecci.hamers.MainActivity;
import nl.ecci.hamers.R;
import nl.ecci.hamers.helpers.AnimateFirstDisplayListener;
import nl.ecci.hamers.helpers.DataManager;
import nl.ecci.hamers.helpers.DividerItemDecoration;
import nl.ecci.hamers.helpers.HamersFragment;
import nl.ecci.hamers.helpers.VolleyCallback;

public class BeerFragment extends HamersFragment {

    private static final Comparator<Beer> nameComparator = new Comparator<Beer>() {
        @Override
        public int compare(Beer beer1, Beer beer2) {
            return beer1.getName().compareToIgnoreCase(beer2.getName());
        }
    };
    private static final Comparator<Beer> ratingComparator = new Comparator<Beer>() {
        @Override
        public int compare(Beer beer1, Beer beer2) {
            String rating1 = beer1.getRating();
            String rating2 = beer2.getRating();

            if (rating1.equals("nog niet bekend")) {
                rating1 = "-1";
            } else if (rating2.equals("nog niet bekend")) {
                rating2 = "-1";
            }
            return rating2.compareToIgnoreCase(rating1);
        }
    };
    private static final Comparator<Beer> dateASCComperator = new Comparator<Beer>() {
        @Override
        public int compare(Beer beer1, Beer beer2) {
            return beer1.getCreatedAt().compareTo(beer2.getCreatedAt());
        }
    };
    private static final Comparator<Beer> dateDESCComperator = new Comparator<Beer>() {
        @Override
        public int compare(Beer beer1, Beer beer2) {
            return beer2.getCreatedAt().compareTo(beer1.getCreatedAt());
        }
    };
    private final ArrayList<Beer> dataSet = new ArrayList<>();
    private BeerAdapter adapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView beer_list;

    public BeerFragment() {
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.beer_fragment, container, false);
        beer_list = (RecyclerView) view.findViewById(R.id.beer_recyclerview);

        setHasOptionsMenu(true);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        beer_list.setLayoutManager(layoutManager);
        beer_list.setItemAnimator(new DefaultItemAnimator());
        beer_list.addItemDecoration(new DividerItemDecoration(getActivity()));

        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.beer_swipe_container);
        initSwiper(beer_list, layoutManager, swipeRefreshLayout);

        adapter = new BeerAdapter(dataSet, getActivity());
        beer_list.setAdapter(adapter);

        FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.beer_create_button);
        if (fab != null) {
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getActivity(), NewBeerActivity.class);
                    startActivity(intent);
                }
            });
        }

        onRefresh();

        sortList();

        return view;
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
        }, getContext(), MainActivity.prefs, DataManager.BEERURL, DataManager.BEERKEY);
        DataManager.getData(new VolleyCallback() {
            @Override
            public void onSuccess() {
                if (BeerFragment.this.adapter != null) {
                    BeerFragment.this.adapter.notifyDataSetChanged();
                }
            }
        }, getContext(), MainActivity.prefs, DataManager.REVIEWURL, DataManager.REVIEWKEY);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.beer_list_menu, menu);
        MenuItem menuItem = menu.findItem(R.id.beer_search);
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
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.scroll_top:
                scrollTop();
                return true;
            case R.id.sort_name:
                sort(nameComparator);
                return true;
            case R.id.sort_rating:
                sort(ratingComparator);
                return true;
            case R.id.sort_date_asc:
                sort(dateASCComperator);
                return true;
            case R.id.sort_date_desc:
                sort(dateDESCComperator);
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

    private void scrollTop() {
        beer_list.smoothScrollToPosition(0);
    }

    private void sortList() {
        if (getActivity() != null)
            if (MainActivity.prefs != null) {
                String sortPref = MainActivity.prefs.getString("beerSort", "");
                switch (sortPref) {
                    case "name":
                        sort(nameComparator);
                        break;
                    case "rating":
                        sort(ratingComparator);
                        break;
                    case "datumASC":
                        sort(dateASCComperator);
                        break;
                    case "datumDESC":
                        sort(dateDESCComperator);
                        break;
                }
            }
    }

    private void sort(Comparator<Beer> comperator) {
        Collections.sort(dataSet, comperator);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        AnimateFirstDisplayListener.displayedImages.clear();
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

    private class populateList extends AsyncTask<ArrayList<Beer>, Void, ArrayList<Beer>> {
        @SafeVarargs
        @Override
        protected final ArrayList<Beer> doInBackground(ArrayList<Beer>... param) {
            ArrayList<Beer> dataSet = new ArrayList<>();
            JSONArray json;
            if ((json = DataManager.getJsonArray(MainActivity.prefs, DataManager.BEERKEY)) != null) {
                GsonBuilder gsonBuilder = new GsonBuilder();
                gsonBuilder.setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
                Gson gson = gsonBuilder.create();

                Type type = new TypeToken<ArrayList<Beer>>() {
                }.getType();
                dataSet = gson.fromJson(json.toString(), type);
            }
            return dataSet;
        }

        @Override
        protected void onPostExecute(ArrayList<Beer> result) {
            if (!result.isEmpty()) {
                dataSet.clear();
                dataSet.addAll(result);
                if (BeerFragment.this.adapter != null) {
                    BeerFragment.this.adapter.notifyDataSetChanged();
                }
            }
            setRefreshing(false);
            sortList();
        }
    }
}
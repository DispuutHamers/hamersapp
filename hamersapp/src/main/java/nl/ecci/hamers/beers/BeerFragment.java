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
import nl.ecci.hamers.helpers.AnimateFirstDisplayListener;
import nl.ecci.hamers.helpers.DividerItemDecoration;
import nl.ecci.hamers.helpers.HamersFragment;
import nl.ecci.hamers.loader.GetCallback;
import nl.ecci.hamers.loader.Loader;

import static nl.ecci.hamers.MainActivity.prefs;

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
    private static final Comparator<Beer> dateASCComparator = new Comparator<Beer>() {
        @Override
        public int compare(Beer beer1, Beer beer2) {
            return beer1.getCreatedAt().compareTo(beer2.getCreatedAt());
        }
    };
    private static final Comparator<Beer> dateDESCComparator = new Comparator<Beer>() {
        @Override
        public int compare(Beer beer1, Beer beer2) {
            return beer2.getCreatedAt().compareTo(beer1.getCreatedAt());
        }
    };
    private final ArrayList<Beer> dataSet = new ArrayList<>();
    private BeerAdapter adapter;
    private RecyclerView beer_list;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.hamers_fragment, container, false);
        beer_list = (RecyclerView) view.findViewById(R.id.hamers_recyclerview);

        setHasOptionsMenu(true);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        beer_list.setLayoutManager(layoutManager);
        beer_list.setItemAnimator(new DefaultItemAnimator());

        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.hamers_swipe_container);
        initSwiper(beer_list, layoutManager, swipeRefreshLayout);

        adapter = new BeerAdapter(dataSet, getActivity());
        beer_list.setAdapter(adapter);

        FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.hamers_fab);
        if (fab != null) {
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startActivityForResult(new Intent(getActivity(), NewBeerActivity.class), 1);
                }
            });
        }

        new populateList().execute();
        onRefresh();

        sortList();

        return view;
    }

    @Override
    public void onRefresh() {
        setRefreshing(true);
        Loader.getData(getContext(), Loader.BEERURL, new GetCallback() {
            @Override
            public void onSuccess(String response) {
                new populateList().execute(response);
            }

            @Override
            public void onError(VolleyError error) {
                // Nothing
            }
        }, null);
        Loader.getData(getContext(), Loader.REVIEWURL, null, null);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.beer_menu, menu);
        MenuItem searchMenuItem = menu.findItem(R.id.beer_search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchMenuItem);
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
                sort(dateASCComparator);
                return true;
            case R.id.sort_date_desc:
                sort(dateDESCComparator);
                return true;
            default:
                return false;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        onRefresh();
        getActivity().setTitle(getResources().getString(R.string.navigation_item_beers));
    }

    private void scrollTop() {
        beer_list.smoothScrollToPosition(0);
    }

    private void sortList() {
        if (getActivity() != null)
            if (prefs != null) {
                String sortPref = prefs.getString("beerSort", "");
                switch (sortPref) {
                    case "rating":
                        sort(ratingComparator);
                        break;
                    case "datumASC":
                        sort(dateASCComparator);
                        break;
                    case "datumDESC":
                        sort(dateDESCComparator);
                        break;
                    default:
                        sort(nameComparator);
                }
            }
    }

    private void sort(Comparator<Beer> comparator) {
        Collections.sort(dataSet, comparator);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        AnimateFirstDisplayListener.displayedImages.clear();
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        onRefresh();
    }

    private class populateList extends AsyncTask<String, Void, ArrayList<Beer>> {
        @Override
        protected final ArrayList<Beer> doInBackground(String... params) {
            ArrayList<Beer> result;
            Type type = new TypeToken<ArrayList<Beer>>() {
            }.getType();
            GsonBuilder gsonBuilder = new GsonBuilder();
            gsonBuilder.setDateFormat(MainActivity.dbDF.toPattern());
            Gson gson = gsonBuilder.create();

            if (params.length > 0) {
                result = gson.fromJson(params[0], type);
            } else {
                result = gson.fromJson(prefs.getString(Loader.BEERURL, null), type);

            }
            return result;
        }

        @Override
        protected void onPostExecute(ArrayList<Beer> result) {
            if (result != null) {
                dataSet.clear();
                dataSet.addAll(result);
                if (adapter != null) {
                    adapter.notifyDataSetChanged();
                }
            }
            setRefreshing(false);
            sortList();
        }
    }
}
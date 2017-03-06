package nl.ecci.hamers.helpers;

import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

public abstract class HamersFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    public SwipeRefreshLayout swipeRefreshLayout;

    protected void initSwiper(final RecyclerView recyclerView, final LinearLayoutManager lm, final SwipeRefreshLayout swl) {
        recyclerView.setLayoutManager(lm);

        swipeRefreshLayout = swl;
        swipeRefreshLayout.setColorSchemeResources(android.R.color.holo_red_light);
        swipeRefreshLayout.setOnRefreshListener(this);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView view, int dx, int dy) {
                swipeRefreshLayout.setEnabled(lm.findFirstCompletelyVisibleItemPosition() == 0);
            }
        });
    }

    public void setRefreshing(final Boolean bool) {
        if (swipeRefreshLayout != null) {
            swipeRefreshLayout.post(new Runnable() {
                @Override
                public void run() {
                    swipeRefreshLayout.setRefreshing(bool);
                }
            });
        }
    }
}

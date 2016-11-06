package nl.ecci.hamers.helpers;

import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

public abstract class HamersFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    protected void initSwiper(final RecyclerView recyclerView, final LinearLayoutManager lm, final SwipeRefreshLayout swipeRefreshLayout) {
        swipeRefreshLayout.setColorSchemeResources(android.R.color.holo_red_light);
        swipeRefreshLayout.setOnRefreshListener(this);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView view, int dx, int dy) {
                boolean enable = false;
                if (recyclerView != null && recyclerView.getChildCount() > 0) {
                    // check if the first item of the list is visible
                    boolean firstItemVisible = lm.findFirstCompletelyVisibleItemPosition() == 0;
                    // check if the top of the first item is visible
                    boolean topOfFirstItemVisible = recyclerView.getChildAt(0).getTop() == 0;
                    // enabling or disabling the refresh layout
                    enable = firstItemVisible && topOfFirstItemVisible;
                }
                swipeRefreshLayout.setEnabled(enable);
            }
        });
    }
}

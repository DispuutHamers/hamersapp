package nl.ecci.Hamers.Helpers;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.widget.ListView;

public class ExpandedListView extends ListView {
    private android.view.ViewGroup.LayoutParams params;
    private int oldCount = 0;

    public ExpandedListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (getCount() != oldCount) {
            int height = 0;
            for (int i = 0; i < getCount(); i++) {
                height += getChildAt(0).getHeight();
                height += getDividerHeight();
            }
            oldCount = getCount();
            params = getLayoutParams();
            params.height = height;
            setLayoutParams(params);
        }

        super.onDraw(canvas);
    }
}
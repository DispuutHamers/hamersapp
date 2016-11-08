package nl.ecci.hamers.helpers;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import nl.ecci.hamers.R;

@SuppressLint("Registered")
public abstract class NewItemActivity extends HamersActivity {
    public MenuItem refreshItem;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.new_menu, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.done_button:
                refreshItem = item;

                /* Attach a rotating ImageView to the refresh item as an ActionView */
                LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                ImageView iv = (ImageView) inflater.inflate(R.layout.refresh_action_view, null);

                Animation rotation = AnimationUtils.loadAnimation(this, R.anim.clockwise_refresh);
                rotation.setRepeatCount(Animation.INFINITE);
                iv.startAnimation(rotation);

                refreshItem.setActionView(iv);

                postItem();
                return true;
            default:
                return false;
        }
    }

    public abstract void postItem();

}

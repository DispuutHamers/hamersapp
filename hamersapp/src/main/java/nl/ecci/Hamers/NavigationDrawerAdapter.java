package nl.ecci.Hamers;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class NavigationDrawerAdapter extends BaseAdapter {

    public static final int QUOTE_FRAGMENT_POSITION = 0;
    public static final int USER_FRAGMENT_POSITION = 1;
    public static final int EVENT_FRAGMENT_POSITION = 2;
    public static final int NEWS_FRAGMENT_POSITION = 3;
    public static final int BEER_FRAGMENT_POSITION = 4;
    public static final int MOTION_FRAGMENT_POSITION = 5;
    public static final int SETTINGS_POSITION = 6;
    private static LayoutInflater inflater = null;
    private List<ItemImage> data;

    public NavigationDrawerAdapter(Activity activity, String navigationTitles[]) {
        data = new ArrayList<NavigationDrawerAdapter.ItemImage>();
        int i = 0;
        for (String navigationTitle : navigationTitles) {
            if (i == QUOTE_FRAGMENT_POSITION)
                data.add(new ItemImage(navigationTitle, R.drawable.quotes));
            else if (i == USER_FRAGMENT_POSITION)
                data.add(new ItemImage(navigationTitle, R.drawable.users));
            else if (i == EVENT_FRAGMENT_POSITION)
                data.add(new ItemImage(navigationTitle, R.drawable.events));
            else if (i == NEWS_FRAGMENT_POSITION)
                data.add(new ItemImage(navigationTitle, R.drawable.ic_newspaper));
            else if (i == BEER_FRAGMENT_POSITION)
                data.add(new ItemImage(navigationTitle, R.drawable.beers));
            else if (i == MOTION_FRAGMENT_POSITION)
                data.add(new ItemImage(navigationTitle, R.drawable.motions));
            else if (i == SETTINGS_POSITION)
                data.add(new ItemImage(navigationTitle, R.drawable.settings));
            i++;
        }
        inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public int getCount() {
        return data.size();
    }

    public Object getItem(int position) {
        return data.get(position);
    }

    public long getItemId(int position) {
        return 0;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        View vi = convertView;
        if (convertView == null)
            vi = inflater.inflate(R.layout.navigation_list_row, parent, false);

        final ItemImage itemImage = data.get(position);
        TextView titleTextView = (TextView) vi.findViewById(R.id.titleTextView);
        ImageView imageImageView = (ImageView) vi.findViewById(R.id.imageImageView);

        titleTextView.setText(itemImage.getTitle());
        imageImageView.setImageResource(itemImage.getImage());

        return vi;
    }

    class ItemImage {
        String title;
        int image;

        public ItemImage(String title, int image) {
            super();
            this.title = title;
            this.image = image;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public int getImage() {
            return image;
        }

        public void setImage(int image) {
            this.image = image;
        }

    }
}
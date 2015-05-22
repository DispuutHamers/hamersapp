package nl.ecci.Hamers.Users;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import nl.ecci.Hamers.Helpers.MD5Util;
import nl.ecci.Hamers.R;

import java.util.ArrayList;

public class UsersAdapter extends ArrayAdapter<User> {

    private final Context context;
    private final ArrayList<User> dataSet;
    private SharedPreferences prefs;
    private ImageLoader imageLoader;
    private DisplayImageOptions options;

    public UsersAdapter(Context context, ArrayList<User> dataSet) {
        super(context, R.layout.user_row, dataSet);

        this.context = context;
        this.dataSet = dataSet;

        // Universal Image Loader
        imageLoader = ImageLoader.getInstance();
        options = new DisplayImageOptions.Builder()
                .resetViewBeforeLoading(true)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .considerExifParams(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .build();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        // 1. Create inflater
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        // 2. Get rowView from inflater
        View rowView = inflater.inflate(R.layout.user_row, parent, false);

        // 3. Get the two text view from the rowView
        TextView username = (TextView) rowView.findViewById(R.id.username);
        TextView quotecount = (TextView) rowView.findViewById(R.id.user_quotecount);
        TextView reviewcount = (TextView) rowView.findViewById(R.id.user_reviewcount);

        // 4. Set the text for textView
        username.setText(dataSet.get(position).getUsername());
        quotecount.setText("Aantal quotes: " + String.valueOf(dataSet.get(position).getQuotecount()));
        reviewcount.setText("Aantal reviews: " + dataSet.get(position).getReviewcount());

        // Image
        ImageView userImage = (ImageView) rowView.findViewById(R.id.user_image);
        String url = "http://gravatar.com/avatar/" + MD5Util.md5Hex(dataSet.get(position).getEmail());
        imageLoader.displayImage(url, userImage, options);

        // 5. return rowView
        return rowView;
    }
}

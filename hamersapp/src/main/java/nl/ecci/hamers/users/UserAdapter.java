package nl.ecci.hamers.users;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;

import nl.ecci.hamers.MainActivity;
import nl.ecci.hamers.R;
import nl.ecci.hamers.helpers.AnimateFirstDisplayListener;
import nl.ecci.hamers.helpers.Utils;

class UserAdapter extends ArrayAdapter<User> {

    private static AnimateFirstDisplayListener animateFirstListener;
    private final Context context;
    private final ArrayList<User> dataSet;
    private final ImageLoader imageLoader;

    public UserAdapter(Context context, ArrayList<User> dataSet) {
        super(context, R.layout.user_row, dataSet);

        this.context = context;
        this.dataSet = dataSet;

        imageLoader = ImageLoader.getInstance();
        animateFirstListener = new AnimateFirstDisplayListener();
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        // 1. Create inflater
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        // 2. Get rowView from inflater
        View rowView = inflater.inflate(R.layout.user_row, parent, false);

        // 3. Get the two text view from the rowView
        TextView username = (TextView) rowView.findViewById(R.id.username);
        TextView quotecount = (TextView) rowView.findViewById(R.id.user_quotecount);
        TextView reviewcount = (TextView) rowView.findViewById(R.id.user_reviewcount);

        // 4. Set the text for textView
        username.setText(dataSet.get(position).getUsername());
        quotecount.setText(String.format("Aantal quotes: %s", String.valueOf(dataSet.get(position).getQuotecount())));
        reviewcount.setText(String.format(MainActivity.locale, "Aantal reviews: %d", dataSet.get(position).getReviewcount()));

        // Image
        final ImageView userImage = (ImageView) rowView.findViewById(R.id.user_image);
        final String url = "http://gravatar.com/avatar/" + Utils.md5Hex(dataSet.get(position).getEmail()) + "?s=200";
        imageLoader.displayImage(url, userImage, animateFirstListener);

        rowView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, SingleUserActivity.class);
                intent.putExtra(User.USER_NAME, dataSet.get(position).getUsername());
                intent.putExtra(User.USER_ID, dataSet.get(position).getUserID());
                intent.putExtra(User.USER_EMAIL, dataSet.get(position).getEmail());
                intent.putExtra(User.USER_QUOTECOUNT, dataSet.get(position).getQuotecount());
                intent.putExtra(User.USER_REVIEWCOUNT, dataSet.get(position).getReviewcount());
                intent.putExtra(User.USER_IMAGE_URL, url);

                context.startActivity(intent);
            }
        });

        // 5. return rowView
        return rowView;
    }
}

package nl.ecci.hamers.users;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
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

import static nl.ecci.hamers.helpers.Utils.convertNicknames;
import static nl.ecci.hamers.helpers.Utils.getGravatarURL;

class UserListAdapter extends ArrayAdapter<User> {

    private static AnimateFirstDisplayListener animateFirstListener;
    private final Context context;
    private final ArrayList<User> dataSet;
    private final ImageLoader imageLoader;

    public UserListAdapter(Context context, ArrayList<User> dataSet) {
        super(context, R.layout.user_row, dataSet);

        this.context = context;
        this.dataSet = dataSet;

        imageLoader = ImageLoader.getInstance();
        animateFirstListener = new AnimateFirstDisplayListener();
    }

    @NonNull
    @Override
    public View getView(final int position, View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View rowView = inflater.inflate(R.layout.user_row, parent, false);

        TextView username = (TextView) rowView.findViewById(R.id.username);
        TextView nickname = (TextView) rowView.findViewById(R.id.user_nickname);
        TextView quoteCount = (TextView) rowView.findViewById(R.id.user_quotecount);
        TextView reviewCount = (TextView) rowView.findViewById(R.id.user_reviewcount);

        username.setText(dataSet.get(position).getName());
        nickname.setText(convertNicknames(dataSet.get(position).getNicknames()));
        quoteCount.setText(String.format("Aantal quotes: %s", String.valueOf(dataSet.get(position).getQuoteCount())));
        reviewCount.setText(String.format(MainActivity.locale, "Aantal reviews: %d", dataSet.get(position).getReviewCount()));

        final ImageView userImage = (ImageView) rowView.findViewById(R.id.user_image);
        String url = getGravatarURL(dataSet.get(position).getEmail());
        imageLoader.displayImage(url, userImage, animateFirstListener);

        rowView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, SingleUserActivity.class);
                intent.putExtra(User.USER_ID, dataSet.get(position).getID());
                context.startActivity(intent);
            }
        });

        return rowView;
    }
}

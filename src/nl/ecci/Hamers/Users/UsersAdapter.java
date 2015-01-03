package nl.ecci.Hamers.Users;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.preference.PreferenceManager;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import nl.ecci.Hamers.R;

import java.util.ArrayList;

public class UsersAdapter extends ArrayAdapter<User> {

    private final Context context;
    private final ArrayList<User> itemsArrayList;
    SharedPreferences prefs;

    public UsersAdapter(Context context, ArrayList<User> itemsArrayList) {

        super(context, R.layout.user_row, itemsArrayList);

        this.context = context;
        this.itemsArrayList = itemsArrayList;
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
        username.setText(itemsArrayList.get(position).getUsername());
        quotecount.setText("Aantal quotes: " + String.valueOf(itemsArrayList.get(position).getQuotecount()));
        reviewcount.setText("Aantal reviews: " + itemsArrayList.get(position).getReviewcount());

        // Image
        prefs =  PreferenceManager.getDefaultSharedPreferences(context);
        byte[] array = Base64.decode(prefs.getString("userpic-" + itemsArrayList.get(position).getUserID(), ""), Base64.DEFAULT);
        Bitmap bmp = BitmapFactory.decodeByteArray(array, 0, array.length);
        ImageView userImage = (ImageView) rowView.findViewById(R.id.user_image);
        userImage.setImageBitmap(bmp);

        // 5. return rowView
        return rowView;
    }
}

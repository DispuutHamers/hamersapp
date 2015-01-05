package nl.ecci.Hamers.Beers;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import nl.ecci.Hamers.R;
import org.json.JSONException;

import java.text.ParseException;
import java.util.ArrayList;

import static nl.ecci.Hamers.Helpers.DataManager.getUser;
import static nl.ecci.Hamers.MainActivity.parseDate;

public class ReviewAdapter extends ArrayAdapter<Review> {

    private final Context context;
    private final ArrayList<Review> itemsArrayList;
    SharedPreferences prefs;

    public ReviewAdapter(Context context, ArrayList<Review> itemsArrayList) {

        super(context, R.layout.review_row, itemsArrayList);

        this.context = context;
        this.itemsArrayList = itemsArrayList;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        // 1. Create inflater
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        // 2. Get rowView from inflater
        View rowView = inflater.inflate(R.layout.review_row, parent, false);

        // 3. Get the two text view from the rowView
        TextView title = (TextView) rowView.findViewById(R.id.review_title);
        TextView body = (TextView) rowView.findViewById(R.id.review_body);
        TextView date = (TextView) rowView.findViewById(R.id.review_date);

        // 4. Set the text for textView

        prefs = PreferenceManager.getDefaultSharedPreferences(this.getContext());
        String name = null;
        try {
            name = getUser(prefs, itemsArrayList.get(position).getUser_id()).getString("name").toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String datum = null;
        try {
            datum = parseDate(itemsArrayList.get(position).getProefdatum());
        } catch (ParseException e) {
            e.printStackTrace();
        }

        title.setText(name + " zei over dit biertje: ");
        body.setText(itemsArrayList.get(position).getDescription());
        date.setText(datum);
        
        return rowView;
    }
}

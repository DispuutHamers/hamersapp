package nl.ecci.Hamers.Beers;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import nl.ecci.Hamers.Helpers.DataManager;
import nl.ecci.Hamers.R;

import java.util.ArrayList;

public class BeersAdapter extends ArrayAdapter<Beer> {

    private final Context context;
    private final ArrayList<Beer> itemsArrayList;
    SharedPreferences prefs;

    public BeersAdapter(Context context, ArrayList<Beer> itemsArrayList) {
        super(context, R.layout.beer_row, itemsArrayList);

        this.context = context;
        this.itemsArrayList = itemsArrayList;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        // 1. Create inflater
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        // 2. Get rowView from inflater
        View rowView = inflater.inflate(R.layout.beer_row, parent, false);

        // 3. Get the two text view from the rowView
        TextView name = (TextView) rowView.findViewById(R.id.beer_name);
        TextView soort = (TextView) rowView.findViewById(R.id.beer_soort);
        ImageView picture = (ImageView) rowView.findViewById(R.id.beer_picture);
        TextView brewer = (TextView) rowView.findViewById(R.id.beer_brewer);
        TextView rating = (TextView) rowView.findViewById(R.id.row_beer_rating);
        TextView info = (TextView) rowView.findViewById(R.id.beer_info);

        // 4. Set the text for textView
        name.setText(itemsArrayList.get(position).getName());
        soort.setText("Soort: " + itemsArrayList.get(position).getSoort());
        brewer.setText("Brouwer: " + itemsArrayList.get(position).getBrewer());
        rating.setText("Cijfer: " + itemsArrayList.get(position).getRating());
        info.setText(itemsArrayList.get(position).getCountry() + " - " + itemsArrayList.get(position).getPercentage());

        // 5. set image
        prefs = PreferenceManager.getDefaultSharedPreferences(context);
        picture.setImageBitmap(DataManager.getBeerImage(prefs, itemsArrayList.get(position).getName()));

        return rowView;
    }
}

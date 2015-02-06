package nl.ecci.Hamers.Beers;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
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
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.beer_row, parent, false);

        TextView name = (TextView) rowView.findViewById(R.id.beer_name);
        TextView soort = (TextView) rowView.findViewById(R.id.beer_soort);
        ImageView picture = (ImageView) rowView.findViewById(R.id.beer_picture);
        TextView brewer = (TextView) rowView.findViewById(R.id.beer_brewer);
        TextView rating = (TextView) rowView.findViewById(R.id.row_beer_rating);
        TextView info = (TextView) rowView.findViewById(R.id.beer_info);

        name.setText(itemsArrayList.get(position).getName());
        soort.setText("Soort: " + itemsArrayList.get(position).getSoort());
        brewer.setText("Brouwer: " + itemsArrayList.get(position).getBrewer());
        info.setText(itemsArrayList.get(position).getCountry() + " - " + itemsArrayList.get(position).getPercentage());

        String cijfer = itemsArrayList.get(position).getRating();
        if (cijfer.equals("null")) {
            rating.setText("Cijfer: nog niet bekend");
        } else {
            rating.setText("Cijfer: " + cijfer);
        }

        prefs = PreferenceManager.getDefaultSharedPreferences(context);
        Bitmap image = DataManager.getBeerImage(prefs, itemsArrayList.get(position).getName() + "-thumb");
        picture.setImageBitmap(image);

        return rowView;
    }
}

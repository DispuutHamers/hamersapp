package com.ecci.Hamers.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.ecci.Hamers.Beer;
import com.ecci.Hamers.Event;
import com.ecci.Hamers.R;
import org.w3c.dom.Text;

import java.util.ArrayList;

public class BeersAdapter extends ArrayAdapter<Beer> {

    private final Context context;
    private final ArrayList<Beer> itemsArrayList;

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
        TextView percentage = (TextView) rowView.findViewById(R.id.beer_percentage);
        TextView brewer = (TextView) rowView.findViewById(R.id.beer_brewer);
        TextView country = (TextView) rowView.findViewById(R.id.beer_country);

        // 4. Set the text for textView
        name.setText(itemsArrayList.get(position).getName());
        soort.setText(itemsArrayList.get(position).getSoort());
        // TODO: Picture
        percentage.setText(itemsArrayList.get(position).getPercentage());
        brewer.setText(itemsArrayList.get(position).getBrewer());
        country.setText(itemsArrayList.get(position).getCountry());
        
        return rowView;
    }
}

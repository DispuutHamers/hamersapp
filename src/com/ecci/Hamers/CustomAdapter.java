package com.ecci.Hamers;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class CustomAdapter extends ArrayAdapter<Quote> {

    private final Context context;
    private final ArrayList<Quote> itemsArrayList;

    public CustomAdapter(Context context, ArrayList<Quote> itemsArrayList) {

        super(context, R.layout.quote_row, itemsArrayList);

        this.context = context;
        this.itemsArrayList = itemsArrayList;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        // 1. Create inflater
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        // 2. Get rowView from inflater
        View rowView = inflater.inflate(R.layout.quote_row, parent, false);

        // 3. Get the two text view from the rowView
        TextView body = (TextView) rowView.findViewById(R.id.quote_body);
        TextView date = (TextView) rowView.findViewById(R.id.quote_date);

        // 4. Set the text for textView
        body.setText(itemsArrayList.get(position).getBody());
        date.setText(itemsArrayList.get(position).getDate());

        // 5. return rowView
        return rowView;
    }
}

package com.ecci.Hamers.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import com.ecci.Hamers.Event;
import com.ecci.Hamers.R;

import java.util.ArrayList;

public class EventsAdapter extends ArrayAdapter<Event> {

    private final Context context;
    private final ArrayList<Event> itemsArrayList;

    public EventsAdapter(Context context, ArrayList<Event> itemsArrayList) {

        super(context, R.layout.event_row, itemsArrayList);

        this.context = context;
        this.itemsArrayList = itemsArrayList;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        // 1. Create inflater
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        // 2. Get rowView from inflater
        View rowView = inflater.inflate(R.layout.event_row, parent, false);

        // 3. Get the two text view from the rowView
        TextView title = (TextView) rowView.findViewById(R.id.event_title);
        TextView beschrijving = (TextView) rowView.findViewById(R.id.event_beschrijving);
        TextView date = (TextView) rowView.findViewById(R.id.event_date);

        // 4. Set the text for textView
        title.setText(itemsArrayList.get(position).getTitle());
        beschrijving.setText(itemsArrayList.get(position).getBeschrijving());
        date.setText(itemsArrayList.get(position).getDate());

        // 5. return rowView
        return rowView;
    }
}

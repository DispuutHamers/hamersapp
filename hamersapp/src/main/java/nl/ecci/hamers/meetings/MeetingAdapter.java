package nl.ecci.hamers.meetings;

import android.content.Context;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import nl.ecci.hamers.R;

public class MeetingAdapter extends RecyclerView.Adapter<MeetingAdapter.ViewHolder> {

    private final Context context;
    private final ArrayList<Meeting> dataSet;

    public MeetingAdapter(ArrayList<Meeting> dataSet, Context context) {
        this.dataSet = dataSet;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.meeting_row, parent, false);

        final ViewHolder vh = new ViewHolder(view);

        vh.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v1) {
                Snackbar.make(view, context.getString(R.string.functionality_added_later), Snackbar.LENGTH_SHORT).show();
            }
        });

        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.subject.setText(dataSet.get(position).getSubject());
        holder.date.setText(String.format("Date: %s", dataSet.get(position).getDate()));
    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        public final View view;
        public final TextView subject;
        public final TextView date;

        public ViewHolder(View view) {
            super(view);
            this.view = view;

            subject = (TextView) view.findViewById(R.id.meeting_subject);
            date = (TextView) view.findViewById(R.id.meeting_date);
        }
    }
}

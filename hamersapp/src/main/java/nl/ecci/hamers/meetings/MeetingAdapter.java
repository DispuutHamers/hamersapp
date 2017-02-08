package nl.ecci.hamers.meetings;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import nl.ecci.hamers.MainActivity;
import nl.ecci.hamers.R;

import static nl.ecci.hamers.helpers.Utils.getOwnUser;

public class MeetingAdapter extends RecyclerView.Adapter<MeetingAdapter.ViewHolder> {

    private final Activity context;
    private final ArrayList<Meeting> dataSet;
    private final int ownID;

    public MeetingAdapter(ArrayList<Meeting> dataSet, Activity context) {
        this.dataSet = dataSet;
        this.context = context;

        ownID = getOwnUser(MainActivity.prefs).getId();
    }

    @Override
    public ViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.meeting_card, parent, false);

        final ViewHolder vh = new ViewHolder(view);

        vh.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v1) {
                try {
                    Intent intent = new Intent(context, SingleMeetingActivity.class);
                    intent.putExtra(Meeting.ID, dataSet.get(vh.getAdapterPosition()).getID());
                    context.startActivity(intent);
                } catch (NullPointerException ignored) {
                }
            }
        });

        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.subject.setText(dataSet.get(position).getSubject());
        if (dataSet.get(position).getDate() != null) {
            holder.date.setText(MainActivity.appDF2.format(dataSet.get(position).getDate()));
        }

        if (dataSet.get(position).getUserID() == ownID) {
            context.registerForContextMenu(holder.view);
        }
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

package nl.ecci.hamers.meetings;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import java.util.ArrayList;

import nl.ecci.hamers.MainActivity;
import nl.ecci.hamers.R;

import static nl.ecci.hamers.helpers.DataManager.getOwnUser;

public class MeetingAdapter extends RecyclerView.Adapter<MeetingAdapter.ViewHolder> {

    private static ImageLoadingListener animateFirstListener;
    private final Context context;
    private final ArrayList<Meeting> dataSet;
    private final ImageLoader imageLoader;
    private final int userID;
    private ArrayList<Meeting> filteredDataSet;

    public MeetingAdapter(ArrayList<Meeting> itemsArrayList, Context context) {
        this.dataSet = itemsArrayList;
        this.filteredDataSet = itemsArrayList;
        this.context = context;
        userID = getOwnUser(MainActivity.prefs).getUserID();

        // Universal Image Loader
        imageLoader = ImageLoader.getInstance();
    }

    @Override
    public ViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.meeting_row, parent, false);

        final ViewHolder vh = new ViewHolder(view);

        vh.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v1) {
//                try {
//                    Meeting meeting = DataManager.getMeeting(MainActivity.prefs, filteredDataSet.get(vh.getAdapterPosition()).getId());
//                    Activity activity = (Activity) context;
//                    String imageTransitionName = context.getString(R.string.transition_single_image);
//                    ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(activity, meetingView, imageTransitionName);
//                    Intent intent = new Intent(context, SingleMeetingActivity.class);
//                    ActivityCompat.startActivity(activity, intent, options.toBundle());
//                } catch (NullPointerException ignored) {
//                }
            }
        });

        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.subject.setText(filteredDataSet.get(position).getSubject());
        holder.date.setText(String.format("Date: %s", filteredDataSet.get(position).getDate()));
    }

    @Override
    public int getItemCount() {
        return filteredDataSet.size();
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

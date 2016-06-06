package nl.ecci.hamers.meetings;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.imageaware.ImageAware;
import com.nostra13.universalimageloader.core.imageaware.ImageViewAware;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import java.util.ArrayList;

import nl.ecci.hamers.MainActivity;
import nl.ecci.hamers.R;
import nl.ecci.hamers.helpers.DataManager;
import nl.ecci.hamers.helpers.SingleImageActivity;

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
//        holder.title.setText(filteredDataSet.get(position).getName());
//        holder.soort.setText(String.format("Soort: %s", filteredDataSet.get(position).getKind()));
//        holder.brewer.setText(String.format("Brouwer: %s", filteredDataSet.get(position).getBrewer()));
//        holder.rating.setText(String.format("Cijfer: %s", filteredDataSet.get(position).getRating()));
//        holder.info.setText((String.format("%s - %s", filteredDataSet.get(position).getCountry(), filteredDataSet.get(position).getPercentage())));
//
//        String imageURL = filteredDataSet.get(position).getImageURL();

    }

    @Override
    public int getItemCount() {
        return filteredDataSet.size();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        public final View view;
//        public final TextView title;
//        public final TextView soort;
//        public final TextView brewer;
//        public final TextView rating;
//        public final TextView info;
//        public final ImageView picture;
//        public final ImageView thumbs;

        public ViewHolder(View view) {
            super(view);
            this.view = view;

//            title = (TextView) view.findViewById(R.id.meeting_name);
//            soort = (TextView) view.findViewById(R.id.meeting_soort);
//            brewer = (TextView) view.findViewById(R.id.meeting_brewer);
//            rating = (TextView) view.findViewById(R.id.row_meeting_rating);
//            info = (TextView) view.findViewById(R.id.meeting_info);
//            picture = (ImageView) view.findViewById(R.id.meeting_image);
//            thumbs = (ImageView) view.findViewById(R.id.meeting_thumbs);
        }
    }
}

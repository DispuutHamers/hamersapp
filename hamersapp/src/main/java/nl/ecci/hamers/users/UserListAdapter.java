package nl.ecci.hamers.users;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;

import nl.ecci.hamers.MainActivity;
import nl.ecci.hamers.R;
import nl.ecci.hamers.helpers.AnimateFirstDisplayListener;

import static nl.ecci.hamers.helpers.Utils.convertNicknames;
import static nl.ecci.hamers.helpers.Utils.getGravatarURL;

class UserListAdapter extends RecyclerView.Adapter<UserListAdapter.ViewHolder> {

    private static AnimateFirstDisplayListener animateFirstListener;
    private final Context context;
    private final ArrayList<User> dataSet;
    private final ImageLoader imageLoader;
    private final Gson gson;

    UserListAdapter(ArrayList<User> dataSet, Context context) {
        this.dataSet = dataSet;
        this.context = context;

        imageLoader = ImageLoader.getInstance();
        animateFirstListener = new AnimateFirstDisplayListener();

        GsonBuilder gsonBuilder = new GsonBuilder();
        gson = gsonBuilder.create();
    }

    @Override
    public UserListAdapter.ViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_row, parent, false);
        final UserListAdapter.ViewHolder vh = new UserListAdapter.ViewHolder(view);

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, SingleUserActivity.class);
                intent.putExtra(User.USER_ID, dataSet.get(vh.getAdapterPosition()).getID());
                context.startActivity(intent);
            }
        });

        return vh;
    }

    @Override
    public void onBindViewHolder(UserListAdapter.ViewHolder holder, final int position) {
        holder.userName.setText(dataSet.get(position).getName());
        holder.userNickname.setText(convertNicknames(dataSet.get(position).getNicknames()));
        holder.userQuoteCount.setText(String.format("Aantal quotes: %s", String.valueOf(dataSet.get(position).getQuoteCount())));
        holder.userReviewCount.setText(String.format(MainActivity.locale, "Aantal reviews: %d", dataSet.get(position).getReviewCount()));

        String url = getGravatarURL(dataSet.get(position).getEmail());
        imageLoader.displayImage(url, holder.userImage, animateFirstListener);
    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        public final View view;
        final ImageView userImage;
        final TextView userName;
        final TextView userNickname;
        final TextView userQuoteCount;
        final TextView userReviewCount;

        ViewHolder(View view) {
            super(view);
            this.view = view;

            userImage = (ImageView) view.findViewById(R.id.user_image);
            userName = (TextView) view.findViewById(R.id.username);
            userNickname = (TextView) view.findViewById(R.id.user_nickname);
            userQuoteCount = (TextView) view.findViewById(R.id.user_quotecount);
            userReviewCount = (TextView) view.findViewById(R.id.user_reviewcount);
        }
    }
}

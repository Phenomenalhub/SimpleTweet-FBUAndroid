package com.codepath.apps.restclienttemplate;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.codepath.apps.restclienttemplate.models.Tweet;

import java.util.List;

public class TweetsAdapter extends RecyclerView.Adapter<TweetsAdapter.ViewHolder>{

    Context context;
    List<Tweet> tweets;
    // pass in the context and list of tweets
    public TweetsAdapter(Context context, List<Tweet> tweets) {
        this.context = context;
        this.tweets = tweets;
    }

    // inflating the layout
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_tweet, parent, false);
        return new ViewHolder(view);
    }
    // bind values based on the position of the element
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // Get the data at position
        Tweet tweet =tweets.get(position);
        // Bind the tweet with the view holder
        holder.bind(tweet);

    }

    @Override
    public int getItemCount() {
        return tweets.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        ImageView ivProfileImage;
        TextView tvBody;
        TextView tvScreenName;
        ImageView ivPostImage;
        TextView ivTime;
        TextView tvFavorite;
        ImageButton ibFavorite;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivProfileImage = itemView.findViewById(R.id.ivProfileImage);
            tvBody = itemView.findViewById(R.id.tvBody);
            tvScreenName = itemView.findViewById(R.id.tvScreenName);
            ivPostImage = itemView.findViewById(R.id.ivPostImage);
            ivTime = itemView.findViewById(R.id.ivTime);
            tvFavorite = itemView.findViewById(R.id.tvFavoriteCount);
            ibFavorite = itemView.findViewById(R.id.ibFavorite);
        }

        public void bind(Tweet tweet) {
            tvBody.setText(tweet.body);
            tvScreenName.setText(tweet.user.screenName);
            ivTime.setText(tweet.getRelativeTimeAgo(tweet.createdAt));
            int radius = 300; // corner radius, higher value = more rounded
            RequestOptions requestOptions = new RequestOptions();
            requestOptions = requestOptions.transforms(new CircleCrop(), new RoundedCorners(30));
            Glide.with(context).load(tweet.user.profileImageUrl).apply(requestOptions).into(ivProfileImage);

            // if the tweet has no media attached set visibility to none
            if (tweet.image.equals("none")){
                ivPostImage.setVisibility(View.GONE);
            }else {
                // load tweet image
                ivPostImage.setVisibility(View.VISIBLE);
                requestOptions = requestOptions.transforms(new CenterCrop(), new RoundedCorners(80));
                Glide.with(context).load(tweet.image).apply(requestOptions).into(ivPostImage);
            }
            ibFavorite.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // if not already favorited
                    //tell twitter i want to favorite this
                    //change the drawable to btn_star_big_on
                    // change the text inside tvFavoriteCount

                    // else if already
                }
            });
        }
    }
    // Clean all elements of the recycler
    public void clear() {
        tweets.clear();
        notifyDataSetChanged();
    }

    // Add a list of items -- change to type used
    public void addAll(List<Tweet> list) {
        tweets.addAll(list);
        notifyDataSetChanged();
    }
}

package com.codepath.apps.restclienttemplate;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.util.Log;
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
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;

import org.parceler.Parcels;

import java.util.List;

import okhttp3.Headers;

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
        ImageButton ibReply;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivProfileImage = itemView.findViewById(R.id.ivProfileImage);
            tvBody = itemView.findViewById(R.id.tvBody);
            tvScreenName = itemView.findViewById(R.id.tvScreenName);
            ivPostImage = itemView.findViewById(R.id.ivPostImage);
            ivTime = itemView.findViewById(R.id.ivTime);
            tvFavorite = itemView.findViewById(R.id.tvFavoriteCount);
            ibFavorite = itemView.findViewById(R.id.ibFavorite);
            ibReply = itemView.findViewById(R.id.ibReply);
        }

        public void bind(Tweet tweet) {
            tvFavorite.setText(String.valueOf(tweet.favoriteCount));
            tvBody.setText(tweet.body);
            tvScreenName.setText(tweet.user.screenName);
            ivTime.setText(tweet.getRelativeTimeAgo(tweet.createdAt));

            ibReply.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, ComposeActivity.class);
                    //intent.putExtra("should_reply_to_tweet", true);
                    //intent.putExtra("id_of_tweet", tweet.id);
                    //intent.putExtra("screename_of_tweet_to_reply_to", tweet.user.screenName);
                    intent.putExtra("tweet_to_reply_to", Parcels.wrap(tweet));
                    ((Activity)context).startActivityForResult(intent, TimelineActivity.REQUEST_CODE);
                    context.startActivity(intent);
                }
            });

            if(tweet.isFavorited) {
                // yellow
                Drawable newImage = context.getDrawable(R.drawable.ic_vector_heart);
                ibFavorite.setImageDrawable(newImage);
            } else {
                //gray
                Drawable newImage = context.getDrawable(R.drawable.ic_vector_heart_stroke);
                ibFavorite.setImageDrawable(newImage);
            }


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
                    if(!tweet.isFavorited) {
                        TwitterApp.getRestClient(context).favorite(tweet.id, new JsonHttpResponseHandler() {
                            @Override
                            public void onSuccess(int statusCode, Headers headers, JSON json) {
                                Log.i("adapter", "this should have been favorited");
                            }

                            @Override
                            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {

                            }
                        });
                        Drawable newImage = context.getDrawable(R.drawable.ic_vector_heart);
                        ibFavorite.setImageDrawable(newImage);
                        tweet.isFavorited = true;
                        tweet.favoriteCount += 1;
                        tvFavorite.setText(String.valueOf(tweet.favoriteCount));

                    } else {
                        Drawable newimage = context.getDrawable(R.drawable.ic_vector_heart_stroke);
                        ibFavorite.setImageDrawable(newimage);
                        tweet.isFavorited = false;
                        tweet.favoriteCount -= 1;
                        tvFavorite.setText(String.valueOf(tweet.favoriteCount));
                        TwitterApp.getRestClient(context).unfavorited(tweet.id, new JsonHttpResponseHandler() {
                            @Override
                            public void onSuccess(int statusCode, Headers headers, JSON json) {
                                Log.i("adapter", "this should have been favorited");
                            }

                            @Override
                            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {

                            }
                        });

                        //tell twitter i want to favorite this
                        //change the drawable to btn_star_big_on
                        // change the text inside tvFavoriteCount

                        // else if already
                    }
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

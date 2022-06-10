package com.codepath.apps.restclienttemplate.models;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Parcel
public class Tweet {
    private static final String TAG = "ivTime";
    public String body;
    public String createdAt;
    public User user;
    public String image;
    public String id;
    public boolean isFavorited;
    public boolean isRetweeted;
    public int favoriteCount;
    public int retweetCount;
    private static final int SECOND_MILLIS = 1000;
    private static final int MINUTE_MILLIS = 60 * SECOND_MILLIS;
    private static final int HOUR_MILLIS = 60 * MINUTE_MILLIS;
    private static final int DAY_MILLIS = 24 * HOUR_MILLIS;


    public Tweet(){

    }

    public static Tweet fromJson(JSONObject jsonObject) throws JSONException {
        if (jsonObject.has("retweeted_status")){
            return null;
        }
        Tweet tweet = new Tweet();
        tweet.isFavorited = jsonObject.getBoolean("favorited");
        tweet.isRetweeted = jsonObject.getBoolean("retweeted");
        tweet.favoriteCount = jsonObject.getInt("favorite_count");
        tweet.retweetCount = jsonObject.getInt("retweet_count");
        tweet.id = jsonObject.getString("id_str");
        tweet.body = jsonObject.getString("text");
        tweet.createdAt = jsonObject.getString("created_at");
        tweet.user = User.fromjson(jsonObject.getJSONObject("user"));

        // creating entities - If
        if(jsonObject.getJSONObject("entities").has("media")){
            //  get the first object in the array of media, find media_url_https in the 2d array
            tweet.image = jsonObject.getJSONObject("entities").getJSONArray("media").getJSONObject(0).getString("media_url_https");
            Log.i("Tweets", tweet.image);
        } else{
            tweet.image = "none";
        }



        return tweet;
    }
    public static List<Tweet>fromjsonArray(JSONArray jsonArray) throws JSONException {
        List<Tweet>tweets = new ArrayList<>();
        for (int i = 0; i < jsonArray.length();i++){
            Tweet newTweet = fromJson(jsonArray.getJSONObject(i));
            if (newTweet != null) {
                tweets.add(newTweet);
            }
        }
        return tweets;
    }

    public String getRelativeTimeAgo(String rawJsonDate) {
        String twitterFormat = "EEE MMM dd HH:mm:ss ZZZZZ yyyy";
        SimpleDateFormat sf = new SimpleDateFormat(twitterFormat, Locale.ENGLISH);
        sf.setLenient(true);

        try {
            long time = sf.parse(rawJsonDate).getTime();
            long now = System.currentTimeMillis();

            final long diff = now - time;
            if (diff < MINUTE_MILLIS) {
                return "just now";
            } else if (diff < 2 * MINUTE_MILLIS) {
                return "a minute ago";
            } else if (diff < 50 * MINUTE_MILLIS) {
                return diff / MINUTE_MILLIS + " m";
            } else if (diff < 90 * MINUTE_MILLIS) {
                return "an hour ago";
            } else if (diff < 24 * HOUR_MILLIS) {
                return diff / HOUR_MILLIS + " h";
            } else if (diff < 48 * HOUR_MILLIS) {
                return "yesterday";
            } else {
                return diff / DAY_MILLIS + " d";
            }
        } catch (ParseException e) {
            Log.i(TAG, "getRelativeTimeAgo failed");
            e.printStackTrace();
        }

        return "";
    }

}

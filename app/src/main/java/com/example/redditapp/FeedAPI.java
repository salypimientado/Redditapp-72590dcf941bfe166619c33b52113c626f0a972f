package com.example.redditapp;

import com.example.redditapp.Model.Feed;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface FeedAPI {

    String Base_URL = "https://old.reddit.com/r/";

    @GET("{feed_name}/.rss")
    Call<Feed> getFeed(@Path("feed_name") String feed_name);

 //   @GET("buildapcsales/.rss")
 //   Call<Feed> getFeed();

}

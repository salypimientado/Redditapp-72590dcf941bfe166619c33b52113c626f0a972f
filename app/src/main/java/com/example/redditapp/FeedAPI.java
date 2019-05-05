package com.example.redditapp;

import com.example.redditapp.Model.Feed;

import retrofit2.Call;
import retrofit2.http.GET;

public interface FeedAPI {

    String Base_URL = "https://old.reddit.com/r/";

    @GET("askreddit/.rss")
    Call<Feed> getFeed();

}

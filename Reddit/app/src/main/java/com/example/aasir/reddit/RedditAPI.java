package com.example.aasir.reddit;

import com.example.aasir.reddit.model.Account.Login;
import com.example.aasir.reddit.model.RedditFeed;

import java.util.Map;

import io.reactivex.Single;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.HeaderMap;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by Aasir on 7/4/2017.
 */

public interface RedditAPI {

    // Retrofit call to retrieve posts within the specified subreddit (feed_name)
    @GET("{subreddit}/.rss")
    Single<RedditFeed> getFeed(@Path("subreddit") String subreddit);

    @POST("{user}")
    Call<Login> signIn(
            @HeaderMap Map<String, String> headers,
            @Path("user") String username,
            @Query("user") String user,
            @Query("password") String password,
            @Query("api_type") String type
    );

}

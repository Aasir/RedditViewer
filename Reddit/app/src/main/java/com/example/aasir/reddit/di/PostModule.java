package com.example.aasir.reddit.di;

import com.example.aasir.reddit.RedditAPI;
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import retrofit2.Retrofit;
import retrofit2.converter.simplexml.SimpleXmlConverterFactory;

/**
 * Created by Aasir on 7/26/2017.
 */
@Module
public class PostModule {

    String mBaseUrl;

    public PostModule(String mBaseUrl) {
        this.mBaseUrl = mBaseUrl;
    }

    @Provides
    @Singleton
    Retrofit provideRetrofit(){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(mBaseUrl)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(SimpleXmlConverterFactory.create())
                .build();

        return retrofit;
    }

    @Provides
    RedditAPI provideFeed(Retrofit retrofit){
        RedditAPI redditAPI = retrofit.create(RedditAPI.class);
        return redditAPI;
    }
}

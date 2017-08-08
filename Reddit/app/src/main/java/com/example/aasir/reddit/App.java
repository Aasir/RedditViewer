package com.example.aasir.reddit;

import android.app.Application;

import com.example.aasir.reddit.di.AppModule;
import com.example.aasir.reddit.di.DaggerPostComponent;
import com.example.aasir.reddit.di.PostComponent;
import com.example.aasir.reddit.di.PostModule;

/**
 * Created by Aasir on 7/26/2017.
 */

public class App extends Application {

    private PostComponent postComponent;
    private static final String BASE_URL = " http://www.reddit.com/r/";

    @Override
    public void onCreate() {
        super.onCreate();

        postComponent = DaggerPostComponent.builder()
                .appModule(new AppModule(this))
                .postModule(new PostModule(BASE_URL))
                .build();
    }

    public PostComponent getPostComponent() {
        return postComponent;
    }
}

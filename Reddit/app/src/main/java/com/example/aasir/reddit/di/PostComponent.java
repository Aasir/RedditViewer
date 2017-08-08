package com.example.aasir.reddit.di;

import com.example.aasir.reddit.ui.MainActivity;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Created by Aasir on 7/26/2017.
 */
@Singleton
@Component(modules = {AppModule.class, PostModule.class})
public interface PostComponent {
    void inject(MainActivity activity);
}

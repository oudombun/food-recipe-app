package com.oudombun.foody.di;

import android.content.Context;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.android.components.ApplicationComponent;
import dagger.hilt.android.qualifiers.ApplicationContext;

@Module
@InstallIn(ApplicationComponent.class)
public abstract class GlideModule {

    @Provides
    @Singleton
    public static RequestManager provideGlideRequestManager(
            @ApplicationContext Context context
    ) {
        return Glide.with(context);
    }
}
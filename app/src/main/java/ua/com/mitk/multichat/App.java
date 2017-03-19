package ua.com.mitk.multichat;


import android.app.Application;

import com.google.firebase.FirebaseApp;

import ua.com.mitk.multichat.di.*;

public class App extends Application {

    public static ApplicationComponent component;

    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseApp.initializeApp(this);
        component = ua.com.mitk.multichat.di.DaggerApplicationComponent.builder()
                .build();
    }

    public static ApplicationComponent getComponent() {
        return component;
    }
}

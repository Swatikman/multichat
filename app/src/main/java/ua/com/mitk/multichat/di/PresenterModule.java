package ua.com.mitk.multichat.di;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import ua.com.mitk.multichat.model.SocketIO;
import ua.com.mitk.multichat.model.Storage;

@Module
public class PresenterModule {

    @Provides
    @Singleton
    public SocketIO provideSocket() {
        return new SocketIO();
    }

    @Provides
    @Singleton
    public Storage providesStorage() {
        return new Storage();
    }

}
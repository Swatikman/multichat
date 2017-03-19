package ua.com.mitk.multichat.di;

import dagger.Module;
import dagger.Provides;
import ua.com.mitk.multichat.presenter.ChatPresenter;
import ua.com.mitk.multichat.presenter.LoginPresenter;

@Module
public class ViewModule {

    @Provides
    public ChatPresenter providesChatPresenter() {
        return new ChatPresenter();
    }

    @Provides
    public LoginPresenter providesLoginPresenter() {
        return new LoginPresenter();
    }

}

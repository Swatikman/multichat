package ua.com.mitk.multichat.di;

import javax.inject.Singleton;

import dagger.Component;
import ua.com.mitk.multichat.model.SocketIO;
import ua.com.mitk.multichat.model.Storage;
import ua.com.mitk.multichat.presenter.ChatPresenter;
import ua.com.mitk.multichat.presenter.LoginPresenter;
import ua.com.mitk.multichat.view.ChatFragment;
import ua.com.mitk.multichat.view.LoginFragment;

@Singleton
@Component(modules={PresenterModule.class, ViewModule.class})
public interface ApplicationComponent extends AppContextComponent {

    void inject(LoginPresenter loginPresenter);

    void inject(ChatPresenter chatPresenter);

    void inject(ChatFragment view);

    void inject(LoginFragment view);

}
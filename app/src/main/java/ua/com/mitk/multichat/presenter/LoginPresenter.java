package ua.com.mitk.multichat.presenter;


import javax.inject.Inject;

import ua.com.mitk.multichat.App;
import ua.com.mitk.multichat.model.SocketIO;
import ua.com.mitk.multichat.view.LoginViewInterface;

public class LoginPresenter implements SocketIO.LoginListener {

    @Inject
    SocketIO mSocketIO;
    private LoginViewInterface mView;

    public LoginPresenter() {
        App.getComponent().inject(this);
        mSocketIO.setLoginListener(this);
    }

    public void onCreate(LoginViewInterface view) {
        this.mView = view;
    }

    public void btnLoginClick(String name) {
        if (checkLogin(name)) {
            mSocketIO.connect(name);
        } else {
            mView.onNotValidLogin();
        }
    }

    private boolean checkLogin(String name) {
        return !(name == null || name.length() < 4);
    }

    @Override
    public void onLoginSuccess(String username) {
        mView.onLoginSuccess();
    }

    @Override
    public void onLoginFailed(String message) {
        mView.onLoginError(message);
    }

    @Override
    public void onServerOffline() {
        mView.onServerOffline();
    }

    @Override
    public void onConnectFailed() {
        mView.onConnectionFailed();
    }

    @Override
    public void onRegisterParseError() {
        mView.onBadResponse();
    }

    public void onDestroy() {
        mSocketIO.removeListener();
    }

}

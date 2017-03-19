package ua.com.mitk.multichat.view;


public interface LoginViewInterface {
    void onLoginSuccess();

    void onLoginError(String error);

    void onServerOffline();

    void onNotValidLogin();

    void onConnectionFailed();

    void onBadResponse();
}

package ua.com.mitk.multichat.view;


import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import javax.inject.Inject;

import ua.com.mitk.multichat.App;
import ua.com.mitk.multichat.presenter.LoginPresenter;
import ua.com.mitk.multichat.R;


/**
 * A simple {@link Fragment} subclass.
 */
public class LoginFragment extends Fragment implements LoginViewInterface, View.OnClickListener {

    private EditText mEtLogin;
    private Handler mHandler;
    @Inject
    LoginPresenter mPresenter;

    public LoginFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_login, container, false);
        mEtLogin = (EditText) view.findViewById(R.id.etLogin);
        Button btnLogin = (Button) view.findViewById(R.id.btnLogin);
        btnLogin.setOnClickListener(this);

        mHandler = new Handler(Looper.getMainLooper());

        App.getComponent().inject(this);
        mPresenter.onCreate(this);
        return view;
    }

    @Override
    public void onLoginSuccess() {
        getFragmentManager().beginTransaction().replace(R.id.container, new ChatFragment()).commit();
    }

    @Override
    public void onLoginError(final String error) {
        mHandler.post(() -> Toast.makeText(getActivity(), error, Toast.LENGTH_SHORT).show());
    }

    @Override
    public void onServerOffline() {
        mHandler.post(() -> Toast.makeText(getActivity(),
                getString(R.string.fragment_login_server_offline), Toast.LENGTH_SHORT).show());
    }

    @Override
    public void onNotValidLogin() {
        mHandler.post(() -> Toast.makeText(getActivity(),
                getString(R.string.fragment_login_bad_username), Toast.LENGTH_SHORT).show());
    }

    @Override
    public void onConnectionFailed() {
        mHandler.post(() -> Toast.makeText(getActivity(),
                getString(R.string.fragment_login_connection_failed), Toast.LENGTH_SHORT).show());
    }

    @Override
    public void onBadResponse() {
        mHandler.post(() -> Toast.makeText(getActivity(),
                getString(R.string.fragment_login_bad_response), Toast.LENGTH_SHORT).show());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mPresenter.onDestroy();
    }

    @Override
    public void onClick(View v) {
        mPresenter.btnLoginClick(mEtLogin.getText().toString());
    }
}

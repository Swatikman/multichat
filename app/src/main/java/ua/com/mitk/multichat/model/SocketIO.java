package ua.com.mitk.multichat.model;


import android.support.annotation.Nullable;

import org.json.JSONObject;

import java.net.URISyntaxException;

import javax.inject.Singleton;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import ua.com.mitk.multichat.JsonUtils;
import ua.com.mitk.multichat.data.Message;
import ua.com.mitk.multichat.data.RegisterResponse;
import ua.com.mitk.multichat.data.SystemMessage;

@Singleton
public class SocketIO {

    private final static String SERVER_URI = "https://android-multichat.herokuapp.com/";

    private final static String EVENT_CONNECT_FAILED = "connect_failed";
    private final static String EVENT_CONNECT_ERROR = "connect_error";
    private final static String EVENT_REGISTER = "register";
    private final static String EVENT_DISCONNECT = "disconnect";
    private final static String EVENT_MESSAGE = "message";
    private final static String EVENT_SYSTEM_MESSAGE = "systemMessage";
    private final static String EVENT_CONNECTION = "connection";

    private LoginListener mLoginListener;
    private MessageListener mMessageListener;
    private String mUsername;
    private Socket mSocket;

    {
        try {
            mSocket = IO.socket(SERVER_URI);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }


    public SocketIO() {
        //init all events listeners

        mSocket.on(EVENT_CONNECT_FAILED, args -> {
            if (mLoginListener != null) {
                mLoginListener.onConnectFailed();
            }
        });

        mSocket.on(EVENT_CONNECT_ERROR, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                //server offline
                if (mLoginListener != null) {
                    mLoginListener.onServerOffline();
                }
                mSocket.disconnect();
            }
        });

        mSocket.on(EVENT_REGISTER, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                handleRegister((String) args[0]);
            }
        });

        mSocket.on(EVENT_DISCONNECT, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                if (mMessageListener != null) {
                    mMessageListener.onDisconnect();
                }
            }
        });

        mSocket.on(EVENT_MESSAGE, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                handleMessage((JSONObject) args[0]);
            }
        });

        mSocket.on(EVENT_SYSTEM_MESSAGE, new Emitter.Listener() {
            @Override
            public void call(final Object... args) {
                handleSystemMessage((String) args[0]);
            }
        });
    }

    private void handleSystemMessage(String responseSystemMessage) {
        if (mMessageListener != null) {
            SystemMessage systemMessage = JsonUtils.parseSystemMessage(responseSystemMessage);
            mMessageListener.onSystemMessage(systemMessage);
        }
    }

    private void handleMessage(JSONObject jsonMessage) {
        final Message message = JsonUtils.parseMessage(jsonMessage);
        if (message.getUsername().equals(mUsername)) {
            message.itsMyMessage();
        }
        if (mMessageListener != null) {
            mMessageListener.onMessage(message);
        }
    }

    private void handleRegister(String serverResponse) {
        RegisterResponse response = JsonUtils.parseRegisterResponse(serverResponse);
        if (response == null) {
            if (mLoginListener != null) {
                mLoginListener.onRegisterParseError();
            }
            return;
        }
        if (response.isSuccess()) {
            final String username = response.getUsername();
            SocketIO.this.mUsername = username;
            if (mLoginListener != null) {
                mLoginListener.onLoginSuccess(username);
            }
        } else {
            final String message = response.getMessage();
            if (mLoginListener != null) {
                mLoginListener.onLoginFailed(message);
            }
        }
    }

    public void connect(final String username) {
        mSocket.off(EVENT_CONNECTION);
        mSocket.on(EVENT_CONNECTION, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                String readyMessage = JsonUtils.createRegisterMessage(username);
                mSocket.emit(EVENT_REGISTER, readyMessage);
            }
        });
        mSocket.connect();
    }

    public void setLoginListener(LoginListener listener) {
        this.mLoginListener = listener;
    }

    public void setMessageListener(MessageListener listener) {
        this.mMessageListener = listener;
    }

    public void removeListener() {
        this.mLoginListener = null;
    }


    public void send(String message) {
        send(message, null);
    }

    public void send(String message, @Nullable String fileKey) {
        String readyMessage = JsonUtils.createMessage(message, fileKey);
        mSocket.emit(EVENT_MESSAGE, readyMessage);
    }

    public interface LoginListener {

        void onLoginSuccess(String username);

        void onLoginFailed(String message);

        void onServerOffline();

        void onConnectFailed();

        void onRegisterParseError();
    }

    public interface MessageListener {

        void onDisconnect();

        void onMessage(Message message);

        void onSystemMessage(SystemMessage message);
    }
}

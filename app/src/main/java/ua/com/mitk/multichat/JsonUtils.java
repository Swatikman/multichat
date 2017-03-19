package ua.com.mitk.multichat;


import android.support.annotation.Nullable;
import android.text.TextUtils;

import org.json.JSONException;
import org.json.JSONObject;

import ua.com.mitk.multichat.data.Message;
import ua.com.mitk.multichat.data.RegisterResponse;
import ua.com.mitk.multichat.data.SystemMessage;

public class JsonUtils {

    private static final String USER = "user";
    private static final String NAME = "name";
    private static final String MESSAGE = "message";
    private static final String FILE_KEY = "file_key";
    private static final String TYPE = "type";

    public static Message parseMessage(JSONObject jsonMessage) {
        Message message = new Message();
        try {
            message.setUsername(jsonMessage.getString(USER));
            message.setMessage(jsonMessage.getString(MESSAGE));
            if (jsonMessage.has(FILE_KEY)) {
                message.setFileKey(jsonMessage.getString(FILE_KEY));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return message;
    }

    public static SystemMessage parseSystemMessage(String message) {
        SystemMessage systemMessage = null;
        try {
            JSONObject jsonMessage = new JSONObject(message);
            systemMessage = new SystemMessage(jsonMessage.getString(MESSAGE));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return systemMessage;
    }

    public static String createRegisterMessage(String username) {
        JSONObject jsonRegister = new JSONObject();
        try {
            jsonRegister.put(NAME, username);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonRegister.toString();
    }

    public static String createMessage(String message, @Nullable String fileKey) {
        JSONObject jsonMessage = new JSONObject();
        try {
            jsonMessage.put(MESSAGE, message);
            if (!TextUtils.isEmpty(fileKey)) {
                jsonMessage.put(FILE_KEY, fileKey);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonMessage.toString();
    }

    public static RegisterResponse parseRegisterResponse(String response) {
        try {
            JSONObject json = new JSONObject(response);
            RegisterResponse registerResponse = new RegisterResponse();
            registerResponse.setResponse(json.getString(TYPE));
            if (json.has(MESSAGE)) {
                registerResponse.setMessage(json.getString(MESSAGE));
            }
            if (json.has(NAME)) {
                registerResponse.setUsername(json.getString(NAME));
            }
            return registerResponse;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
}

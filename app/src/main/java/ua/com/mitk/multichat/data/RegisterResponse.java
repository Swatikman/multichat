package ua.com.mitk.multichat.data;


import android.text.TextUtils;

public class RegisterResponse {

    private static final String SUCCESS = "success";

    private String response;
    private String username;
    //message contains an error
    private String message;

    public void setResponse(String response) {
        this.response = response;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isSuccess() {
        return TextUtils.equals(response, SUCCESS);
    }
}

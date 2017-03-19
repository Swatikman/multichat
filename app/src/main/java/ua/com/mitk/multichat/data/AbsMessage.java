package ua.com.mitk.multichat.data;


import java.io.Serializable;

public abstract class AbsMessage implements Serializable{
    public String message;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}

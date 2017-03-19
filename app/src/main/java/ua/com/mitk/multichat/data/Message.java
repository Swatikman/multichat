package ua.com.mitk.multichat.data;


public class Message extends AbsMessage {
    private String username;
    private String fileKey;
    private boolean isPhoneOwner;

    public Message() {
    }

    public Message(String username, String message) {
        this.message = message;
        this.username = username;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public boolean isMyMessage() {
        return isPhoneOwner;
    }

    public void itsMyMessage() {
        isPhoneOwner = true;
    }

    public void setFileKey(String fileKey) {
        this.fileKey = fileKey;
    }

    public String getFileKey() {
        return fileKey;
    }
}

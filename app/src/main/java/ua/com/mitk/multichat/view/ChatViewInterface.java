package ua.com.mitk.multichat.view;


import java.util.ArrayList;

import ua.com.mitk.multichat.data.AbsMessage;
import ua.com.mitk.multichat.data.Message;
import ua.com.mitk.multichat.data.SystemMessage;

public interface ChatViewInterface {
    void onNewMessage(Message message);

    void onSystemMessage(SystemMessage message);

    void onMessageSent();

    void onSendEmptyMessage();

    void retainList(ArrayList<AbsMessage> messages);

    void onDisconnect();

    void onFileLoadProgress(long bytesTransferred, long totalByteCount);

    void onFileAttached();

    void onFileDetached();

    void onFileUploadStart();

    void onFileDownloadStart();

    void onFileUploadSuccess();

    void onFileDownloadSuccess();

    void onFileUploadFail();

    void onFileDownloadFail();
}

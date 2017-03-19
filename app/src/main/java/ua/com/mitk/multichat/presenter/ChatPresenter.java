package ua.com.mitk.multichat.presenter;


import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;

import java.util.ArrayList;

import javax.inject.Inject;

import ir.sohreco.androidfilechooser.ExternalStorageNotAvailableException;
import ir.sohreco.androidfilechooser.FileChooserDialog;
import ua.com.mitk.multichat.App;
import ua.com.mitk.multichat.data.AbsMessage;
import ua.com.mitk.multichat.data.Message;
import ua.com.mitk.multichat.data.SystemMessage;
import ua.com.mitk.multichat.model.SocketIO;
import ua.com.mitk.multichat.model.Storage;
import ua.com.mitk.multichat.view.ChatViewInterface;

public class ChatPresenter implements SocketIO.MessageListener, FileChooserDialog.ChooserListener {

    private static final String MESSAGES_KEY = "messages";
    private static final String IS_FILE_ATTACHED_KEY = "isFileAttached";
    private static final String ATTACHED_FILE_PATH_KEY = "attachedFilePath";
    private static final String CURRENT_MESSAGE_KEY = "currentMessage";

    private ChatViewInterface mView;
    private ArrayList<AbsMessage> mMessages;
    private boolean mIsFileAttached;
    private String mAttachedFilePath;
    private String mCurrentMessage;
    @Inject
    SocketIO mSocketIO;
    @Inject
    Storage mStorage;

    public ChatPresenter() {
        App.getComponent().inject(this);
        mSocketIO.setMessageListener(this);
        mMessages = new ArrayList<>();
        mCurrentMessage = "";
    }

    public void onCreate(ChatViewInterface view) {
        this.mView = view;
    }

    public void btnSendClick(String text) {
        if (text == null || text.length() < 1) {
            mView.onSendEmptyMessage();
            return;
        }
        mCurrentMessage = text;
        if (mIsFileAttached && mAttachedFilePath != null) {
            uploadFile(mAttachedFilePath);
        } else {
            send(mCurrentMessage);
        }
    }

    @Override
    public void onDisconnect() {
        mView.onDisconnect();
    }

    @Override
    public void onMessage(Message message) {
        mMessages.add(message);
        mView.onNewMessage(message);
    }

    @Override
    public void onSystemMessage(SystemMessage message) {
        mMessages.add(message);
        mView.onSystemMessage(message);
    }

    public void saveInstanceState(Bundle outState) {
        outState.putSerializable(MESSAGES_KEY, mMessages);
        outState.putBoolean(IS_FILE_ATTACHED_KEY, mIsFileAttached);
        if (mAttachedFilePath != null) {
            outState.putString(ATTACHED_FILE_PATH_KEY, mAttachedFilePath);
        }
        if (mCurrentMessage != null) {
            outState.putString(CURRENT_MESSAGE_KEY, mCurrentMessage);
        }
    }

    public void retainInstanceState(Bundle inState) {
        if (inState != null) {
            mMessages = (ArrayList<AbsMessage>) inState.getSerializable(MESSAGES_KEY);
            mIsFileAttached = inState.getBoolean(IS_FILE_ATTACHED_KEY);
            mView.retainList(mMessages);
            if (inState.containsKey(ATTACHED_FILE_PATH_KEY)) {
                mAttachedFilePath = inState.getString(ATTACHED_FILE_PATH_KEY);
            }
            if (inState.containsKey(CURRENT_MESSAGE_KEY)) {
                mCurrentMessage = inState.getString(CURRENT_MESSAGE_KEY);
            }
        }
    }

    public FileChooserDialog btnAttachClick() {
        if (mIsFileAttached) {
            mIsFileAttached = false;
            mAttachedFilePath = null;
            mView.onFileDetached();
            return null;
        }
        try {
            return new FileChooserDialog.Builder(
                    FileChooserDialog.ChooserType.FILE_CHOOSER, this).build();
        } catch (ExternalStorageNotAvailableException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void uploadFile(String path) {
        mStorage.uploadFile(path);
        mView.onFileUploadStart();
        mStorage.addUploadOnProgressListener((taskSnapshot ->
                mView.onFileLoadProgress(taskSnapshot.getBytesTransferred(), taskSnapshot.getTotalByteCount())));

        mStorage.addUploadOnFailureListener(e -> {
            mView.onFileUploadFail();
            e.printStackTrace();
        });

        mStorage.addUploadOnSuccessListener(taskSnapshot -> {
            if (taskSnapshot.getMetadata() != null) {
                send(mCurrentMessage, taskSnapshot.getMetadata().getName());
            }
            mView.onFileUploadSuccess();
        });
    }

    public void downloadFile(String key) {
        String newFilePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                .getAbsolutePath() + "/" + key;
        mStorage.downloadFile(key, newFilePath);
        mView.onFileDownloadStart();

        mStorage.addDownloadOnProgressListener(taskSnapshot ->
                mView.onFileLoadProgress(taskSnapshot.getBytesTransferred(), taskSnapshot.getTotalByteCount()));

        mStorage.addDownloadOnFailureListener(e -> {
            e.printStackTrace();
            mView.onFileDownloadFail();
        });

        mStorage.addDownloadOnSuccessListener(taskSnapshot -> mView.onFileDownloadSuccess());
    }

    public void cancelFileLoading() {
        mStorage.cancel();
    }

    private void send(String message) {
        send(message, null);
    }

    private void send(String message, @Nullable String filekey) {
        if (filekey != null) {
            mSocketIO.send(message, filekey);
        } else {
            mSocketIO.send(message);
        }
        mIsFileAttached = false;
        mAttachedFilePath = null;
        mView.onMessageSent();
    }

    @Override
    public void onSelect(String path) {
        //on file select
        mIsFileAttached = true;
        mAttachedFilePath = path;
        mView.onFileAttached();
    }
}

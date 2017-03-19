package ua.com.mitk.multichat.model;


import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;

public class Storage {

    private final static String STORAGE_URL = "gs://android-multichat.appspot.com" ;
    private UploadTask mUploadTask;
    private FileDownloadTask mDownloadTask;

    private static StorageReference getReference() {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        return storage.getReferenceFromUrl(STORAGE_URL);
    }

    public void downloadFile(String key, String path) {
        StorageReference ref = getReference();
        File newFile = new File(path);
        StorageReference child = ref.child(key);
        mDownloadTask = child.getFile(newFile);
    }

    public void uploadFile(String path) {
        StorageReference ref = getReference();
        File f = new File(path);
        Uri fileUri = Uri.fromFile(f);
        StorageReference fileRef = ref.child(fileUri.getLastPathSegment());
        mUploadTask = fileRef.putFile(fileUri);
    }

    public void addDownloadOnProgressListener(OnProgressListener<FileDownloadTask.TaskSnapshot> onProgressListener) {
        mDownloadTask.addOnProgressListener(onProgressListener);
    }

    public void addUploadOnProgressListener(OnProgressListener<UploadTask.TaskSnapshot> onProgressListener) {
        mUploadTask.addOnProgressListener(onProgressListener);
    }

    public void addDownloadOnFailureListener(OnFailureListener onFailureListener) {
        mDownloadTask.addOnFailureListener(onFailureListener);
    }

    public void addUploadOnFailureListener(OnFailureListener onFailureListener) {
        mUploadTask.addOnFailureListener(onFailureListener);
    }

    public void addDownloadOnSuccessListener(OnSuccessListener<FileDownloadTask.TaskSnapshot> onSuccessListener) {
        mDownloadTask.addOnSuccessListener(onSuccessListener);
    }

    public void addUploadOnSuccessListener(OnSuccessListener<UploadTask.TaskSnapshot> onSuccessListener) {
        mUploadTask.addOnSuccessListener(onSuccessListener);
    }

    public void cancel() {
        if (mUploadTask != null) {
            mUploadTask.cancel();
        }
        if (mDownloadTask != null) {
            mDownloadTask.cancel();
        }
    }
}

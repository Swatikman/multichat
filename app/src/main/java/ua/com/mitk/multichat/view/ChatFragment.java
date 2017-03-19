package ua.com.mitk.multichat.view;


import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import java.util.ArrayList;

import javax.inject.Inject;

import ir.sohreco.androidfilechooser.FileChooserDialog;
import ua.com.mitk.multichat.App;
import ua.com.mitk.multichat.data.AbsMessage;
import ua.com.mitk.multichat.presenter.ChatPresenter;
import ua.com.mitk.multichat.data.Message;
import ua.com.mitk.multichat.R;
import ua.com.mitk.multichat.data.SystemMessage;


/**
 * A simple {@link Fragment} subclass.
 */
public class ChatFragment extends Fragment implements ChatViewInterface, View.OnClickListener,
        ChatAdapter.OnFileClickListener {

    @Inject
    ChatPresenter mPresenter;
    private RecyclerView mRvMessages;

    private EditText mEtMessage;
    private ChatAdapter mChatAdapter;
    private ImageButton mIbtnAttach;
    private ProgressDialog mProgressDialog;
    private Handler mHandler;

    public ChatFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_chat, container, false);
        Button btnSend = (Button) view.findViewById(R.id.btnSend);
        btnSend.setOnClickListener(this);
        mRvMessages = (RecyclerView) view.findViewById(R.id.lvMessages);
        mEtMessage = (EditText) view.findViewById(R.id.etMessage);
        mIbtnAttach = (ImageButton) view.findViewById(R.id.ibtnAttach);
        mIbtnAttach.setOnClickListener(this);

        mChatAdapter = new ChatAdapter();
        mChatAdapter.setFileClickListener(this);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setStackFromEnd(true);
        mRvMessages.setLayoutManager(layoutManager);
        mRvMessages.setAdapter(mChatAdapter);

        App.getComponent().inject(this);
        mPresenter.onCreate(this);
        mPresenter.retainInstanceState(savedInstanceState);

        mHandler = new Handler(Looper.getMainLooper());

        return view;
    }

    @Override
    public void onNewMessage(Message message) {
        mHandler.post(() -> {
            mChatAdapter.add(message);
            mRvMessages.scrollToPosition(mChatAdapter.getItemCount() - 1);
            mChatAdapter.notifyDataSetChanged();
        });
    }

    @Override
    public void onSystemMessage(SystemMessage message) {
        mHandler.post(() -> {
            mChatAdapter.add(message);
            mRvMessages.scrollToPosition(mChatAdapter.getItemCount() - 1);
            mChatAdapter.notifyDataSetChanged();
        });

    }

    @Override
    public void onMessageSent() {
        mHandler.post(() -> mIbtnAttach.setImageResource(android.R.drawable.ic_menu_upload));
    }

    @Override
    public void onSendEmptyMessage() {
        mHandler.post(() -> Toast.makeText(getActivity(),
                getString(R.string.fragment_chat_message_not_valid), Toast.LENGTH_SHORT).show());
    }

    @Override
    public void onDisconnect() {
        mHandler.post(() -> {
            Toast.makeText(getActivity(), getString(R.string.fragment_chat_disconnect), Toast.LENGTH_SHORT).show();
            getFragmentManager().beginTransaction().replace(R.id.container, new LoginFragment()).commit();
        });

    }

    @Override
    public void onFileClick(String key) {
        mPresenter.downloadFile(key);
    }

    @Override
    public void onFileLoadProgress(long bytesTransferred, long totalByteCount) {
        mHandler.post(() -> updateProgressDialog(bytesTransferred, totalByteCount));
    }

    private void updateProgressDialog(long bytesTransferred, long totalByteCount) {
        if (mProgressDialog != null) {
            mProgressDialog.setMax((int) totalByteCount);
            mProgressDialog.setProgress((int) bytesTransferred);
        }
    }

    private void showProgressDialog(String message) {
        mProgressDialog = new ProgressDialog(getActivity());
        mProgressDialog.setTitle("");
        mProgressDialog.setMessage(message);
        mProgressDialog.setIndeterminate(false);
        mProgressDialog.setCancelable(false);
        mProgressDialog.setProgress(0);
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        mProgressDialog.setButton(DialogInterface.BUTTON_NEGATIVE, getString(android.R.string.cancel),
                (dialogInterface, i) -> {
                    mPresenter.cancelFileLoading();
                });
        mProgressDialog.show();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnSend:
                mPresenter.btnSendClick(mEtMessage.getText().toString());
                mEtMessage.setText("");
                break;
            case R.id.ibtnAttach:
                FileChooserDialog dialog = mPresenter.btnAttachClick();
                if (dialog != null) {
                    dialog.show(getChildFragmentManager(), null);
                }
                break;
        }

    }

    @Override
    public void retainList(ArrayList<AbsMessage> messages) {
        mChatAdapter.add(messages);
        mRvMessages.scrollToPosition(mChatAdapter.getItemCount() - 1);
        mChatAdapter.notifyDataSetChanged();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mPresenter.saveInstanceState(outState);
    }

    @Override
    public void onFileAttached() {
        mIbtnAttach.setImageResource(android.R.drawable.ic_menu_close_clear_cancel);
    }


    @Override
    public void onFileDetached() {
        mIbtnAttach.setImageResource(android.R.drawable.ic_menu_upload);
    }

    @Override
    public void onFileUploadStart() {
        showProgressDialog(getString(R.string.fragment_chat_file_upload_message));
    }

    @Override
    public void onFileDownloadStart() {
        showProgressDialog(getString(R.string.fragment_chat_file_download_message));
    }

    @Override
    public void onFileUploadSuccess() {
        hideDialogWithToast(getString(R.string.fragment_chat_file_upload_success));
    }

    @Override
    public void onFileDownloadSuccess() {
        hideDialogWithToast(getString(R.string.fragment_chat_file_download_success));
    }

    @Override
    public void onFileUploadFail() {
        hideDialogWithToast(getString(R.string.fragment_chat_file_upload_fail));
    }

    @Override
    public void onFileDownloadFail() {
        hideDialogWithToast(getString(R.string.fragment_chat_file_download_fail));
    }

    private void hideDialogWithToast(String message) {
        mHandler.post(() -> {
            if (mProgressDialog != null) {
                mProgressDialog.dismiss();
                Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
            }
        });

    }
}

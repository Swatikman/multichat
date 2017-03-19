package ua.com.mitk.multichat.view;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import ua.com.mitk.multichat.R;
import ua.com.mitk.multichat.data.AbsMessage;
import ua.com.mitk.multichat.data.Message;
import ua.com.mitk.multichat.data.SystemMessage;


public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int MESSAGE_MY = 0;
    private static final int MESSAGE_USER = 1;
    private static final int MESSAGE_SYSTEM = 2;

    private ArrayList<AbsMessage> mItems;
    private OnFileClickListener mFileClickListener;

    public ChatAdapter(ArrayList<AbsMessage> items) {
        this.mItems = items;
    }

    public ChatAdapter() {
        this.mItems = new ArrayList<>();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v;
        switch (viewType) {
            case MESSAGE_MY:
                v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message_my, parent, false);
                return new MessageViewHolder(v);
            case MESSAGE_USER:
                v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message_others, parent, false);
                return new MessageViewHolder(v);
            case MESSAGE_SYSTEM:
                v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message_system, parent, false);
                return new SystemMessageViewHolder(v);
        }
        return null;
    }

    @Override
    public int getItemViewType(int position) {
        if (mItems.get(position) instanceof Message) {
            return ((Message) mItems.get(position)).isMyMessage() ? MESSAGE_MY : MESSAGE_USER;
        } else if (mItems.get(position) instanceof SystemMessage) {
            return MESSAGE_SYSTEM;
        }
        return super.getItemViewType(position);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof MessageViewHolder) {

            MessageViewHolder h = (MessageViewHolder) holder;
            final Message message = (Message) mItems.get(position);
            h.tvName.setText(message.getUsername());
            h.tvMessage.setText(message.getMessage());
            if (message.getFileKey() != null) {
                h.tvAttachedFile.setVisibility(View.VISIBLE);
                h.tvAttachedFile.setText(message.getFileKey());
                h.tvAttachedFile.setOnClickListener(view -> {
                    if (mFileClickListener != null) {
                        mFileClickListener.onFileClick(message.getFileKey());
                    }
                });
            } else {
                h.tvAttachedFile.setVisibility(View.GONE);
            }

        } else if (holder instanceof SystemMessageViewHolder) {
            SystemMessageViewHolder h = (SystemMessageViewHolder) holder;
            h.tvMessage.setText(mItems.get(position).getMessage());
        }
    }

    public void setFileClickListener(OnFileClickListener fileClickListener) {
        this.mFileClickListener = fileClickListener;
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    public void add(AbsMessage message) {
        mItems.add(message);
        notifyItemInserted(mItems.size() - 1);
    }

    public void add(ArrayList<AbsMessage> messages) {
        mItems.addAll(messages);
        notifyDataSetChanged();
    }

    private class MessageViewHolder extends RecyclerView.ViewHolder {
        private TextView tvName;
        private TextView tvMessage;
        private TextView tvAttachedFile;

        private MessageViewHolder(View itemView) {
            super(itemView);
            tvName = (TextView) itemView.findViewById(R.id.tvName);
            tvMessage = (TextView) itemView.findViewById(R.id.tvMessage);
            tvAttachedFile = (TextView) itemView.findViewById(R.id.tvAttachedFile);
        }
    }

    private class SystemMessageViewHolder extends RecyclerView.ViewHolder {
        private TextView tvMessage;

        private SystemMessageViewHolder(View itemView) {
            super(itemView);
            tvMessage = (TextView) itemView.findViewById(R.id.tvMessage);
        }
    }

    public interface OnFileClickListener {
        void onFileClick(String key);
    }
}

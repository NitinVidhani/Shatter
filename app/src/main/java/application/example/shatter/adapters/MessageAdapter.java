package application.example.shatter.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

import application.example.shatter.R;
import application.example.shatter.model.Chat;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {

    public static final int MSG_TYPE_LEFT = 0;
    public static final int MSG_TYPE_RIGHT = 1;

    Context context;
    List<Chat> chatList;
    LayoutInflater inflater;

    FirebaseUser fUser;

    public MessageAdapter(Context context, List<Chat> chatList) {
        this.context = context;
        this.chatList = chatList;
        inflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == MSG_TYPE_LEFT) {
            View view = inflater.inflate(R.layout.chat_item_left, parent, false);
            return new MessageViewHolder(view);
        } else {
            View view = inflater.inflate(R.layout.chat_item_right, parent, false);
            return new MessageViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {

        Chat mChat = chatList.get(position);

        holder.textViewMessage.setText(mChat.getMessage());
        holder.textViewTimeStamp.setText(mChat.getTimeStamp());


    }

    @Override
    public int getItemCount() {
        return chatList.size();
    }

    public static class MessageViewHolder extends RecyclerView.ViewHolder {

        public TextView textViewMessage;
        public TextView textViewTimeStamp;

        public MessageViewHolder(@NonNull View itemView) {
            super(itemView);

            textViewMessage = itemView.findViewById(R.id.text_view_message);
            textViewTimeStamp = itemView.findViewById(R.id.text_view_timestamp);
        }
    }

    @Override
    public int getItemViewType(int position) {

        fUser = FirebaseAuth.getInstance().getCurrentUser();
        if (chatList.get(position).getSender().equals(fUser.getUid())) {
            return MSG_TYPE_RIGHT;
        } else {
            return MSG_TYPE_LEFT;
        }

    }
}

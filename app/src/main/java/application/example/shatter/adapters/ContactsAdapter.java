package application.example.shatter.adapters;

import android.content.Intent;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

import application.example.shatter.MessageActivity;
import application.example.shatter.R;
import application.example.shatter.model.User;
import de.hdodenhof.circleimageview.CircleImageView;

public class ContactsAdapter extends RecyclerView.Adapter<ContactsAdapter.ContactViewHolder> {

    private static final String TAG = "ContactsAdapter";

    private Context context;
    private List<User> usersList;
    private List<String> contactNames;
    private LayoutInflater inflater;

    public ContactsAdapter(Context context, List<User> usersList, List<String> contactNames) {
        this.context = context;
        this.usersList = usersList;
        this.contactNames = contactNames;

        inflater = LayoutInflater.from(context);

    }

    @NonNull
    @Override
    public ContactViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.e(TAG, "onCreateViewHolder: " + usersList.size());
        View view = inflater.inflate(R.layout.contact_item_layout, parent, false);

        return new ContactViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ContactViewHolder holder, final int position) {
        final User user = usersList.get(position);
        String name = contactNames.get(position);

        holder.mTextViewName.setText(name);


        if (user.getUserAbout().equals(null)) {
            holder.mTextViewAbout.setVisibility(View.GONE);
        } else {
            holder.mTextViewAbout.setVisibility(View.VISIBLE);
            holder.mTextViewAbout.setText(user.getUserAbout());
        }

        if (!user.getImageUrl().equals("default")) {
            Picasso.get().load(user.getImageUrl()).placeholder(R.drawable.ic_default_profile).into(holder.profileImageView);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, MessageActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra("recieverUid", user.getUid());
                intent.putExtra("recieverName", contactNames.get(position));
                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return usersList.size();
    }

    public static class ContactViewHolder extends RecyclerView.ViewHolder {

        CircleImageView profileImageView;
        TextView mTextViewName;
        TextView mTextViewAbout;

        public ContactViewHolder(@NonNull View itemView) {
            super(itemView);

            profileImageView = itemView.findViewById(R.id.circleImageViewProfile);
            mTextViewName = itemView.findViewById(R.id.textViewName);
            mTextViewAbout = itemView.findViewById(R.id.textViewAbout);

        }
    }

}

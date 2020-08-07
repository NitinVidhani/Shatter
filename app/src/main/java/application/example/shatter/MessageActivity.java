package application.example.shatter;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import application.example.shatter.adapters.MessageAdapter;
import application.example.shatter.model.Chat;
import application.example.shatter.model.User;
import de.hdodenhof.circleimageview.CircleImageView;

public class MessageActivity extends AppCompatActivity {

    private static final String TAG = "MessageActivity";

    private Toolbar toolbar;

    private String recieverUid;
    private String recieverName;
    private List<Chat> chatList;

    private CircleImageView profileImageView;
    private TextView textViewName;
    private TextView textViewStatus;

    private DatabaseReference mUserDatabaseRef;
    private DatabaseReference mChatDatabaseRef;
    private FirebaseUser fUser;

    private EditText editTextMessage;
    private RecyclerView recyclerViewMessages;
    private MessageAdapter messageAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        toolbar = findViewById(R.id.toolbar_message);
        setSupportActionBar(toolbar);
//        getSupportActionBar().setIcon(R.drawable.ic_back);


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        Intent intent = getIntent();

        recieverUid = intent.getStringExtra("recieverUid");

        fUser = FirebaseAuth.getInstance().getCurrentUser();

        recieverName = intent.getStringExtra("recieverName");
        mUserDatabaseRef = FirebaseDatabase.getInstance().getReference("Users").child(recieverUid);
        mUserDatabaseRef.keepSynced(true);

        mChatDatabaseRef = FirebaseDatabase.getInstance().getReference("Chats");
        mChatDatabaseRef.keepSynced(true);

        editTextMessage = findViewById(R.id.edit_text_message);

        // List of messages
        recyclerViewMessages = findViewById(R.id.recycler_view_messages);
        recyclerViewMessages.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setStackFromEnd(true);
        recyclerViewMessages.setLayoutManager(linearLayoutManager);

        profileImageView = findViewById(R.id.image_view_profile);
        textViewName = findViewById(R.id.text_view_name);
        textViewName.setText(recieverName);

        textViewStatus = findViewById(R.id.text_view_status);

        mUserDatabaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User reciever = snapshot.getValue(User.class);
                Log.e(TAG, "onDataChange: " + reciever );
                textViewStatus.setText(reciever.getStatus());
                assert reciever != null;
                if (!reciever.getImageUrl().equals("default")) {
                    Picasso.get().load(reciever.getImageUrl()).fit().into(profileImageView);
                    Log.e(TAG, "onDataChange: " + reciever.getImageUrl() );
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        readMessages();

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == android.R.id.home) {
            onBackPressed();
        }

        return true;

    }

    public void sendMessage(View view) {
        String message = editTextMessage.getText().toString();

        if (!TextUtils.isEmpty(message)) {

            Chat chat = new Chat();
            chat.setMessage(message);
            chat.setSender(fUser.getUid());
            chat.setReciever(recieverUid);
            chat.setRecieverName(recieverName);

            mChatDatabaseRef.push().setValue(chat);

            editTextMessage.setText("");

        } else {
            Toast.makeText(this, "Message can't be empty", Toast.LENGTH_SHORT).show();
        }

    }

    private void readMessages() {
        chatList = new ArrayList<>();

        mChatDatabaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                chatList.clear();
                for (DataSnapshot chats : snapshot.getChildren()) {
                    Chat chat = chats.getValue(Chat.class);

                    if (chat.getSender().equals(fUser.getUid()) && chat.getReciever().equals(recieverUid) ||
                        chat.getSender().equals(recieverUid) && chat.getReciever().equals(fUser.getUid())) {
                        chatList.add(chat);
                    }

                    messageAdapter = new MessageAdapter(MessageActivity.this, chatList);
                    recyclerViewMessages.setAdapter(messageAdapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

}
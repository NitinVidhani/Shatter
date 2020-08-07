package application.example.shatter.fragments;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import application.example.shatter.R;
import application.example.shatter.adapters.ContactsAdapter;
import application.example.shatter.helper.ContactHelper;
import application.example.shatter.model.Chat;
import application.example.shatter.model.User;

public class ChatsFragment extends Fragment {

    private Context context;

    private static final String TAG = "ChatsFragment";

    private RecyclerView recyclerViewRecentUsers;

    private FirebaseAuth mAuth;
    private FirebaseUser fUser;
    FirebaseDatabase database;
    private DatabaseReference mChatDatabaseRef;
    private DatabaseReference mUserDatabaseRef;

    private List<User> userList;
    private List<String> userIdList;
    private List<String> userNames;

    private ContactsAdapter adapter;

    public ChatsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_chats, container, false);

        context = container.getContext();

        recyclerViewRecentUsers = view.findViewById(R.id.recycler_view_recent_users);
        recyclerViewRecentUsers.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
        recyclerViewRecentUsers.setLayoutManager(linearLayoutManager);

        database = FirebaseDatabase.getInstance();

        mAuth = FirebaseAuth.getInstance();
        fUser = mAuth.getCurrentUser();
        mChatDatabaseRef = database.getReference("Chats");
        mChatDatabaseRef.keepSynced(true);

        mUserDatabaseRef = database.getReference("Users");
        mUserDatabaseRef.keepSynced(true);

        userIdList = new ArrayList<>();

        mChatDatabaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Set<String> userIdSet = new HashSet<>();

                for (DataSnapshot chatList : snapshot.getChildren()) {
                    Chat chat = chatList.getValue(Chat.class);

                    assert chat != null;
                    if (chat.getReciever().equals(fUser.getUid())) {
                        userIdSet.add(chat.getSender());
                    }
                    if (chat.getSender().equals(fUser.getUid())) {
                        userIdSet.add(chat.getReciever());
                    }

                }
                userIdList.addAll(userIdSet);
                loadUsers();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        return view;

    }

    private void loadUsers() {

        userList = new ArrayList<>();
        userNames = new ArrayList<>();
        final ContactHelper contactHelper = new ContactHelper(context);

        mUserDatabaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot userSnapshot : snapshot.getChildren()) {
                    User user = userSnapshot.getValue(User.class);

                    if (userIdList.contains(user.getUid())) {
                        userList.add(user);
                        userNames.add(contactHelper.getNameByPhone(user.getPhoneNo()));
                    }

                    Log.e(TAG, "onDataChange: " + userList);
                    Log.e(TAG, "onDataChange: " + userNames);

                    adapter = new ContactsAdapter(context, userList, userNames);
                    recyclerViewRecentUsers.setAdapter(adapter);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

//    private void loadUsers() {
//        userList = new ArrayList<>();
//        userNames = new ArrayList<>();
//
//        final ContactHelper contactHelper = new ContactHelper(context);
//
//        mUserDatabaseRef.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//
////                userList.clear();
////                userNames.clear();
//
//                for (DataSnapshot users : snapshot.getChildren()) {
//                    User user = users.getValue(User.class);
//
////                    for (String id : userIdList) {
////                        if (user.getUid().equals(id)) {
////                            if (!userList.isEmpty()) {
////                                for(User user1 : userList) {
////                                    if (!user1.getUid().equals(id)) {
////                                        userList.add(user);
////                                        userNames.add(contactHelper.getNameByPhone(user.getPhoneNo()));
////                                    }
////                                }
////                            } else {
////                                userList.add(user);
////                                userNames.add(contactHelper.getNameByPhone(user.getPhoneNo()));
////                            }
////                        }
////                    }
//
//                    Log.e(TAG, "onDataChange: " + user);
//                    Log.e(TAG, "onDataChange: " + userIdList);
//
//                    for (String id : userIdList) {
//                        if (user.getUid().equals(id)) {
//                            if (!userList.isEmpty()) {
//                                for (User user1 : userList) {
//                                    if (user1.getUid().equals(id)) {
//                                        Log.e(TAG, "onDataChange: " + user1);
//                                        userList.add(user1);
//                                        userNames.add(contactHelper.getNameByPhone(user1.getPhoneNo()));
//                                    }
//                                }
//                            } else {
//                                userList.add(user);
//                                userNames.add(contactHelper.getNameByPhone(user.getPhoneNo()));
//                            }
//
//                        }
//                    }
//
//                }
//
//                Log.e(TAG, "loadUsers: " + userList );
//                adapter = new ContactsAdapter(getContext(), userList, userNames);
//                recyclerViewRecentUsers.setAdapter(adapter);
//
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });
//
//
//    }
}
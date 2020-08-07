package application.example.shatter;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import application.example.shatter.adapters.ContactsAdapter;
import application.example.shatter.model.ContactDetails;
import application.example.shatter.model.User;

public class ContactsActivity extends AppCompatActivity {

    private static final String TAG = "ContactsActivity";

    private Toolbar toolbar;

    private List<ContactDetails> contactList;
    private List<User> usersList;
    private List<String> contactNames;

    private DatabaseReference mDatabaseRef;

    private RecyclerView mContactRecyclerView;
    private ContactsAdapter contactsAdapter;

    private AdView mAdView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);
        // Placing ad unit
        placeAdUnit();

        // Setup toolbar
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Select contacts");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        contactList = new ArrayList<>();
        usersList = new ArrayList<>();
        contactNames = new ArrayList<>();

        // Getting all contacts from phone
        contactList = loadContacts();

        mContactRecyclerView = findViewById(R.id.contactsRecyclerView);
        mContactRecyclerView.setHasFixedSize(true);
        mContactRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Getting all the users from firebase which are in contacts and also name in different list
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("Users");
        loadUsers();


    }

    private void loadUsers() {

        mDatabaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()) {
                    User mUser = ds.getValue(User.class);

                    Log.e(TAG, "onDataChange: " + mUser);
                    for (ContactDetails cd : contactList) {
                        if (cd.getContactNumber().equals(mUser.getPhoneNo())) {
                            usersList.add(mUser);
                            Log.e(TAG, "onDataChange: " + mUser);
                            contactNames.add(cd.getContactName());
                            if (contactsAdapter != null) {
                                contactsAdapter.notifyDataSetChanged();
                            }
                        }
                    }
                }
                Log.e(TAG, "onDataChange: ");

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        contactsAdapter = new ContactsAdapter(this, usersList, contactNames);
        Log.e(TAG, "loadUsers: usersList :- " + usersList );

        mContactRecyclerView.setAdapter(contactsAdapter);


    }

    private List<ContactDetails> loadContacts() {

        ContentResolver contentResolver = getContentResolver();
        Uri uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
        String[] projection = {ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME, ContactsContract.CommonDataKinds.Phone.NUMBER};

        Cursor cursor = contentResolver.query(uri, projection, null, null, null);

        if (cursor != null) {
            while (cursor.moveToNext()) {
                String name = cursor.getString(0);
                String number = cursor.getString(1);
                String no = new StringBuilder(number)
                        .reverse()
                        .toString()
                        .replace("-", "")
                        .replace("(", "")
                        .replace(")", "")
                        .replace(" ", "");

                if (no.length() > 10) {
                    number = new StringBuilder(no.substring(0, 10)).reverse().toString();
                } else {
                    number = new StringBuilder(no).reverse().toString();
                }

                contactList.add(new ContactDetails(name, number));

            }

        }
        assert cursor != null;
        cursor.close();

        return contactList;

    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return true;
    }

    private void placeAdUnit() {

        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });
        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

    }
}
package application.example.shatter;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import application.example.shatter.adapters.ViewPagerAdapter;
import application.example.shatter.fragments.CallsFragment;
import application.example.shatter.fragments.ChatsFragment;
import application.example.shatter.model.User;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;

    private ViewPagerAdapter viewPagerAdapter;

    private FirebaseAuth mAuth;
    private FirebaseUser fUser;
    private DatabaseReference mUserDatabaseRef;

    private User mUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Setting the toolbar
        toolbar = findViewById(R.id.toolbarMain);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Shatter");
        // Toolbar completed


        mAuth = FirebaseAuth.getInstance();
        fUser = mAuth.getCurrentUser();
        mUserDatabaseRef = FirebaseDatabase.getInstance().getReference("Users").child(fUser.getUid());

        mUserDatabaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mUser = snapshot.getValue(User.class);
                Log.e(TAG, "onDataChange: " + mUser);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        tabLayout = findViewById(R.id.tabLayout);
        viewPager = findViewById(R.id.viewPager);

        viewPagerAdapter = new ViewPagerAdapter(this,getSupportFragmentManager());
        viewPagerAdapter.addFragment(new ChatsFragment(), "CHATS");
//        viewPagerAdapter.addFragment(new CallsFragment(), "CALLS");

        viewPager.setAdapter(viewPagerAdapter);
        tabLayout.setupWithViewPager(viewPager);

    }

    public void showContacts(View view) {

        Dexter.withContext(this)
                .withPermission(Manifest.permission.READ_CONTACTS)
                .withListener(new PermissionListener() {
                    @Override public void onPermissionGranted(PermissionGrantedResponse response) {

//                        startActivity(new Intent(MainActivity.this, ContactsActivity.class));
                        startActivity(new Intent(MainActivity.this, ContactsActivity.class));
                    }
                    @Override public void onPermissionDenied(PermissionDeniedResponse response) {

                        Snackbar.make(findViewById(android.R.id.content), "You denied the permission", Snackbar.LENGTH_SHORT).show();

                    }
                    @Override public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {

                        token.continuePermissionRequest();

                    }
                }).check();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        getMenuInflater().inflate(R.menu.main_menu, menu);

        return true;

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch(item.getItemId()) {

            case R.id.new_group_option:
                // TODO Create new group
                break;

            case R.id.settings_option:
                // TODO Create settings activity
                Intent intent = new Intent(this, ProfileActivity.class);
                intent.putExtra("settings", true);
                intent.putExtra("number", mUser.getPhoneNo());
                intent.putExtra("countryCode", mUser.getCountryCode());
                intent.putExtra("name", mUser.getUserName());
                intent.putExtra("about", mUser.getUserAbout());
                intent.putExtra("imageUrl", mUser.getImageUrl());
                startActivity(intent);
                break;

            case R.id.logout_options:
                mAuth.signOut();
                startActivity(new Intent(MainActivity.this, LoginActivity.class));
                finish();

        }

        return true;

    }

    @Override
    protected void onStart() {
        super.onStart();

        Log.e(TAG, "onStart: ");

        mUserDatabaseRef.child("status").setValue("online");

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        DateFormat dateFormat = new SimpleDateFormat("hh:mm aa");
        String time = dateFormat.format(new Date()).toString();

        Date date = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault());
        String formattedDate = df.format(date);

        Log.e(TAG, "onDestroy: " + formattedDate + " time " + time );

        mUserDatabaseRef.child("status").setValue("last seen " + date + " at " + time);
    }
}
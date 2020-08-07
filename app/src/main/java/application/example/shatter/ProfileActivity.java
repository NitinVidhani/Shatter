package application.example.shatter;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import application.example.shatter.model.User;
import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {

    private Toolbar toolbar;

    private CircleImageView profileImage;
    private TextInputLayout textName;
    private TextInputLayout textAbout;
    private TextInputLayout textNumber;
    private ProgressBar uploadProgressBar;


    private String phoneNo;
    private String number;
    private String countryCode;
    private boolean settings;

    private FirebaseUser mUser;
    private DatabaseReference mDatabaseRef;
    private StorageReference mStorageRef;
    private FirebaseAuth mAuth;

    private static final int REQUEST_IMAGE = 1000;
    private static final String TAG = "ProfileActivity";

    private User user;
    private Uri mImageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Enter Details");

        number = getIntent().getStringExtra("number");
        countryCode = getIntent().getStringExtra("countryCode");
        phoneNo = countryCode + number;
        settings = getIntent().getBooleanExtra("settings", false);

        profileImage = findViewById(R.id.profileImage);
        textName = findViewById(R.id.textName);
        textAbout = findViewById(R.id.textAbout);
        textNumber = findViewById(R.id.textNumber);
        uploadProgressBar = findViewById(R.id.profile_progress_bar);
        textNumber.getEditText().setText(phoneNo);

        if (settings) {
            String name = getIntent().getStringExtra("name");
            String about = getIntent().getStringExtra("about");
            String imageUrl = getIntent().getStringExtra("imageUrl");
            textName.getEditText().setText(name);
            textAbout.getEditText().setText(about);
            textName.getEditText().setEnabled(false);
            Picasso.get().load(imageUrl).placeholder(R.drawable.ic_imgchooser).into(profileImage);
        }

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("Users").child(mUser.getUid());
        mStorageRef = FirebaseStorage.getInstance().getReference("profile_images");

        Log.e("TAG", "onCreate: " + mUser.getPhoneNumber() + "UID: " + mUser.getUid());

        user = new User();
        user.setImageUrl("default");
        user.setUserAbout(null);
        user.setUid(mUser.getUid());
    }


    public void setProfilePicture(View view) {

        Intent pickImageIntent = new Intent();
        pickImageIntent.setType("image/*");
        pickImageIntent.setAction(Intent.ACTION_GET_CONTENT);

        startActivityForResult(Intent.createChooser(pickImageIntent, "Select Image"), REQUEST_IMAGE);


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE && resultCode == RESULT_OK && data != null && data.getData() != null) {
            mImageUri = data.getData();

            uploadProgressBar.setVisibility(View.VISIBLE);
            uploadFile();

        }
    }

    private String getFileExtension(Uri uri) {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeType = MimeTypeMap.getSingleton();
        return mimeType.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    private void uploadFile() {

        if (mImageUri != null) {
            final StorageReference fileReference = mStorageRef.child(mUser.getUid() + "." + getFileExtension(mImageUri));

//            fileReference.putFile(mImageUri)
//                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
//                        @Override
//                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//                            uploadProgressBar.setVisibility(View.GONE);
//                            Picasso.get().load(mImageUri).into(profileImage);
//                            Toast.makeText(ProfileActivity.this, "Upload Successful", Toast.LENGTH_SHORT).show();
//                            String imageUrl = taskSnapshot.getMetadata().getReference().getDownloadUrl().toString();
//                            user.setImageUrl(imageUrl);
//
//                        }
//                    })
//                    .addOnFailureListener(new OnFailureListener() {
//                        @Override
//                        public void onFailure(@NonNull Exception e) {
//                            uploadProgressBar.setVisibility(View.GONE);
//                            Toast.makeText(ProfileActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
//                        }
//                    })
//                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
//                        @Override
//                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
//                            double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
//                            uploadProgressBar.setProgress((int) progress);
//                        }
//                    });

            UploadTask uploadTask = fileReference.putFile(mImageUri);

            Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }
                    Log.e(TAG, "then: ");
                    return fileReference.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        Uri downloadUri = task.getResult();
                        user.setImageUrl(downloadUri.toString());
                        uploadProgressBar.setVisibility(View.GONE);
                        Picasso.get().load(mImageUri).placeholder(R.drawable.ic_imgchooser).into(profileImage);
                        Log.e(TAG, "onComplete: " + downloadUri.toString());
                    }
                }
            });

        } else {
            Toast.makeText(this, "No file selected", Toast.LENGTH_SHORT).show();
        }
    }

    public void saveProfileDetails(View view) {

        String name = textName.getEditText().getText().toString();
        String about = textAbout.getEditText().getText().toString();

        if (TextUtils.isEmpty(name)) {
            textName.setError("This field can't be empty");
        } else if (TextUtils.isEmpty(about)) {
            textAbout.setError("This field can't be empty");
        } else {

            user.setUserName(name);
            user.setUserAbout(about);
            user.setPhoneNo(number);
            user.setCountryCode(countryCode);
            user.setStatus("online");
            mDatabaseRef.setValue(user);

            startActivity(new Intent(getApplicationContext(), MainActivity.class));
            finish();
        }
    }

    @Override
    public void onBackPressed() {

        if (!settings) {
            mAuth.signOut();
            mDatabaseRef.removeValue();
        }

        super.onBackPressed();
    }
}


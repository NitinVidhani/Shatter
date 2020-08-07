package application.example.shatter;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class OtpActivity extends AppCompatActivity {

    private static final String TAG = "OtpActivity";
    
    private ProgressBar progressBar;
    private TextInputLayout otpLayout;
    private TextView resendCode;
    private ImageButton btnVerify;
    private TextInputEditText otpEditText;

    private String number;
    private String phoneNo;
    private String countryCode;

    private String verificationCode;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp);

        btnVerify = findViewById(R.id.btnVerify);
        resendCode = findViewById(R.id.textViewResend);

        otpLayout = findViewById(R.id.textInputLayoutOtp);
        progressBar = findViewById(R.id.progressBar);
        otpEditText = findViewById(R.id.editTextOtp);

        number = getIntent().getStringExtra("phoneNo");
        countryCode = getIntent().getStringExtra("countryCode");
        phoneNo = countryCode + number;

        Log.e(TAG, "onCreate: " + countryCode );

        mAuth = FirebaseAuth.getInstance();

        resendCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendVerificationCode();
            }
        });

        btnVerify.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String otp = otpEditText.getText().toString();

                Toast.makeText(OtpActivity.this, otp, Toast.LENGTH_SHORT).show();
                if (otp.isEmpty()) {
                    Toast.makeText(OtpActivity.this, "Enter OTP", Toast.LENGTH_SHORT).show();
                } else if(otp.length() != 6) {
                    Toast.makeText(OtpActivity.this, "Incorrect OTP", Toast.LENGTH_SHORT).show();
                } else {
                    progressBar.setVisibility(View.VISIBLE);
                    PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationCode, otp);
                    signInWithPhoneAuthCredential(credential);
                }
            }
        });

        sendVerificationCode();

    }

    private void sendVerificationCode() {

        new CountDownTimer(60000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                resendCode.setText("" + millisUntilFinished / 1000);
            }

            @Override
            public void onFinish() {
                resendCode.setText("Resend");
                resendCode.setTextColor(getResources().getColor(R.color.colorPrimary));
                resendCode.setEnabled(true);
            }
        }.start();

        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNo,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                this,               // Activity (for callback binding)
                mCallbacks);        // OnVerificationStateChangedCallbacks
    }





    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

        @Override
        public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            verificationCode = s;
        }

        @Override
        public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
            progressBar.setVisibility(View.INVISIBLE);
            signInWithPhoneAuthCredential(phoneAuthCredential);

        }

        @Override
        public void onVerificationFailed(@NonNull FirebaseException e) {

            Log.e(TAG, "onVerificationFailed: " + e.getMessage() );
            Toast.makeText(OtpActivity.this, "Verification failed! Try Again", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(getApplicationContext(), LoginActivity.class));

        }
    };


    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Intent intent = new Intent(OtpActivity.this, ProfileActivity.class);
                            intent.putExtra("settings", false);
                            intent.putExtra("number", number);
                            intent.putExtra("countryCode", countryCode);
                            startActivity(intent);
                            finish();

                            FirebaseUser user = task.getResult().getUser();
                            
                        } else {
                            // Sign in failed, display a message and update the UI
                            Toast.makeText(OtpActivity.this, "Login Failed in method", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

}
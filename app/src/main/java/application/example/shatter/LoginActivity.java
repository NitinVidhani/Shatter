package application.example.shatter;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;
import com.hbb20.CountryCodePicker;

public class LoginActivity extends AppCompatActivity {

    private TextInputLayout textInputLayoutNumber;
    private CountryCodePicker ccp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        textInputLayoutNumber = findViewById(R.id.textInputLayout);

        ccp = findViewById(R.id.ccp);
        ccp.registerCarrierNumberEditText(textInputLayoutNumber.getEditText());

    }

    public void sendVerificationCode(View view) {

        String numberWithoutCountryCode = textInputLayoutNumber.getEditText().getText().toString();
        if (numberWithoutCountryCode.isEmpty()) {

            textInputLayoutNumber.setError("This field can't be empty");

        } else if (numberWithoutCountryCode.length() != 10) {
            textInputLayoutNumber.setError("Invalid phone number");
        } else {
            String phoneNo = textInputLayoutNumber.getEditText().getText().toString();
            String countryCode = ccp.getSelectedCountryCodeWithPlus();
            Toast.makeText(this, phoneNo, Toast.LENGTH_SHORT).show();
            startActivity(new Intent(LoginActivity.this, OtpActivity.class)
                    .putExtra("phoneNo", phoneNo)
                    .putExtra("countryCode", countryCode));
            finish();
        }
    }

}
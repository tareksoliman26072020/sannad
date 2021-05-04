package com.Sannad.SannadApp.LoginAndRegister;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.Sannad.SannadApp.R;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.hbb20.CountryCodePicker;

import static com.Sannad.SannadApp.R.string.Phone_number_does_not_exist;

public class ForgetPasswordActivity extends AppCompatActivity {

    ImageView backBtn;
    Button next ;
    TextView tileText;
    TextInputEditText phoneNumber;
    CountryCodePicker countryCodePicker;
    ProgressBar progressBar ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);

        phoneNumber = findViewById(R.id.restPhoneNumber);
        countryCodePicker = findViewById(R.id.resetCountryCodePicker);
        progressBar = findViewById(R.id.resetPasswordProgressBar);
    }

    /*Call the next activity*/
    public void nextResetPassword(View view) {
        if (!isNetworkAvailable()) {
            Toast.makeText(this, R.string.no_internet_Connection, Toast.LENGTH_SHORT).show();
            return;
        }

        if (!validatePhoneNumber()) {
            return;
        }

        progressBar.setVisibility(View.VISIBLE);


        //Get complete phone number
        String _getUserEnteredPhoneNumber = phoneNumber.getText().toString().trim();
        //Remove first zero if entered!
        if (_getUserEnteredPhoneNumber.charAt(0) == '0') {
            _getUserEnteredPhoneNumber = _getUserEnteredPhoneNumber.substring(1);
        }
        //Complete phone number
        final String _phoneNo = "+" + countryCodePicker.getFullNumber() + _getUserEnteredPhoneNumber;



        // Database Query
        Query checkUser = FirebaseDatabase.getInstance().getReference("Users").orderByChild("phone").equalTo(_phoneNo);

        checkUser.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    // if the data exist
                    phoneNumber.setError(null);
                    Intent intent = new Intent(getApplicationContext(), VerifyOTPActivity.class);
                    // here is the trick we are calling the same VerifyOTPActivity for creating a  new user and also for updating the password . to distinguish  we pass  whattodo throw putextr

                    intent.putExtra("phone", _phoneNo);
                    intent.putExtra("whatToDo","updateUserPassword");
                    startActivity(intent);
                    finish();
                    progressBar.setVisibility(View.GONE);



                } else {
                    progressBar.setVisibility(View.GONE);
                    String phone_number_does_not_exist = getResources().getString(R.string.Phone_number_does_not_exist);
                    phoneNumber.setError(phone_number_does_not_exist);
                    phoneNumber.requestFocus();
                    Toast.makeText(ForgetPasswordActivity.this, phone_number_does_not_exist, Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
            }


        });






    }



    /**/
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private boolean validatePhoneNumber() {
        String val = phoneNumber.getText().toString().trim();

        if (val.isEmpty()) {
            phoneNumber.setError(getString(R.string.Enter_Valid_Phone_Number));
            return false;
        }
        else {
            phoneNumber.setError(null);
            //phoneNumber.setErrorEnabled(false);
            return true;
        }
    }





}
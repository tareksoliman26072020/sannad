package com.Sannad.SannadApp.LoginAndRegister;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.Sannad.SannadApp.Activity.MainActivity;
import com.Sannad.SannadApp.Activity.MainChatActivity;
import com.Sannad.SannadApp.Activity.TestingActivity;
import com.Sannad.SannadApp.Database.SharedPmanager;
import com.Sannad.SannadApp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.hbb20.CountryCodePicker;

public class LoginByPhoneActivity extends AppCompatActivity {

    TextInputEditText phoneNumber;
    TextInputLayout  mPasswordView;
    CountryCodePicker countryCodePicker;
    ProgressBar progressBar;
    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_by_phone);

        countryCodePicker = findViewById(R.id.loginCountryCodePicker);
        phoneNumber = findViewById(R.id.loginPhoneNumber);
        mPasswordView = findViewById(R.id.login_password);
        progressBar = findViewById(R.id.loginProgressBar);
        mAuth = FirebaseAuth.getInstance();



    }

    public void LoginByPhone(View view) {





    }




    //Validate Phone Number

    /** Validate Phone Number */
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


    /**Check if the password meets  the security requirements */
    private boolean validatePassword() {
        String val = mPasswordView.getEditText().getText().toString().trim();

        if (val.isEmpty()) {
            phoneNumber.setError(getString(R.string.wrong_password));
            return false;
        }
        else {
            phoneNumber.setError(null);
            //phoneNumber.setErrorEnabled(false);
            return true;
        }
    }

    /** Check for network connectivity */
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }





    /** Sign In an Existing User */
    public void signInExistingUser(View view) {

        if (!isNetworkAvailable()) {

            Toast.makeText(this, R.string.no_internet_Connection, Toast.LENGTH_SHORT).show();
            return;
        }



        if (!validatePhoneNumber()|| !validatePassword()) {
            return;
        }


        progressBar.setVisibility(View.VISIBLE);

        final String _password = mPasswordView.getEditText().getText().toString().trim();
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

                    // get to the userpassword
                    String systemPassword=snapshot.child(_phoneNo).child("password").getValue(String.class);

                    // check if the entered password is correct
                    if (systemPassword.equals(_password)) {

                        // Check if user is signed in (non-null) and update UI accordingly.
                        FirebaseUser currentUser = mAuth.getCurrentUser();

                        //check if the user has is logged on , if he is not logged in move him to the login activity
                        if (currentUser == null) {
                            Intent intent = new Intent(getApplicationContext(), VerifyOTPActivity.class);
                            intent.putExtra("phone", _phoneNo);
                            intent.putExtra("whatToDo","sendUserToMainActivity");
                            startActivity(intent);
                            finish();
                            progressBar.setVisibility(View.GONE);


                        }
                        else {

                            mPasswordView.setError(null);

                            //Get Users Date from firebase

                            String _username = snapshot.child(_phoneNo).child("username").getValue(String.class);
                            String _password = snapshot.child(_phoneNo).child("password").getValue(String.class);
                            String _email = snapshot.child(_phoneNo).child("email").getValue(String.class);
                            String _gender = snapshot.child(_phoneNo).child("gender").getValue(String.class);
                            String _dateOfbirth = snapshot.child(_phoneNo).child("date").getValue(String.class);
                            String _phone = snapshot.child(_phoneNo).child("phone").getValue(String.class);

//                            Toast.makeText(LoginByPhoneActivity.this, _username + _password + _phone, Toast.LENGTH_SHORT).show();

//                        // create a session
//                        SharedPmanager sharedPmanager = new SharedPmanager(LoginByPhoneActivity.this);
//                        sharedPmanager.createLoginPreference(_email,_username,_password,_gender,_dateOfbirth,_phone );

                            progressBar.setVisibility(View.GONE);
                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                            startActivity(intent);
                            finish();

                        }




                    } else {
                        progressBar.setVisibility(View.GONE);

                        Toast.makeText(LoginByPhoneActivity.this, "Password is not correct", Toast.LENGTH_SHORT).show();
                       // Toast.makeText(LoginByPhoneActivity.this, "Password is not correct password is "+ systemPassword, Toast.LENGTH_SHORT).show();
                    }

                }

                else {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(LoginByPhoneActivity.this, "This Phone Number does not Exist", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(LoginByPhoneActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });

    }



    /* Executed when Register button pressed  to register a new user */
    public void registerNewUser(View v) {

        if (!isNetworkAvailable()) {

            Toast.makeText(this, R.string.no_internet_Connection, Toast.LENGTH_SHORT).show();
            return;
        }

        FirebaseUser currentUser = mAuth.getCurrentUser();

        //check if the user has is logged on , if he is not logged in move him to the login activity
        if (currentUser != null) {
            FirebaseAuth.getInstance().signOut();

            Toast.makeText(this, "SignOut", Toast.LENGTH_SHORT).show();
        }

        Intent intent = new Intent(this, RegisterActivity.class);
        finish();
        startActivity(intent);
    }



    /* Move to Forget Password Activity  */
    public void goToForgetPasswordActivity(View view) {


        if (!isNetworkAvailable()) {

            Toast.makeText(this, R.string.no_internet_Connection, Toast.LENGTH_SHORT).show();
            return;
        }

        Intent intent = new Intent(getApplicationContext(), ForgetPasswordActivity.class);
        startActivity(intent);


    }




}
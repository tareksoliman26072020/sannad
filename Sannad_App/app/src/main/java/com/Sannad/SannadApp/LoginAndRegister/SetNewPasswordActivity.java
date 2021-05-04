package com.Sannad.SannadApp.LoginAndRegister;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.Sannad.SannadApp.R;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SetNewPasswordActivity extends AppCompatActivity {

    private TextInputLayout mPasswordView;
    private TextInputLayout mConfirmPasswordView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_new_password);

        mPasswordView = findViewById(R.id.setNewPasswordActivity_newPassword);
        mConfirmPasswordView = findViewById(R.id.setNewPasswordActivity_confirm_password);

        Log.d("TAGTAG", "onCreate: From SetNewPassword");

    }

    public void UpdatePassword(View view) {


        if (!isNetworkAvailable()) {

            Toast.makeText(this, R.string.no_internet_Connection, Toast.LENGTH_SHORT).show();
            return;
        }

        if (!isPasswordValid()) {
            Toast.makeText(this, R.string.password_is_not_valid, Toast.LENGTH_SHORT).show();
            return;
        }


        String _newPassword = mPasswordView.getEditText().getText().toString().trim();
        String _phoneNumber = getIntent().getStringExtra("phone");


        //Update Data in Firebase and in Sessions
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.child(_phoneNumber).child("password").setValue(_newPassword);
        startActivity(new Intent(getApplicationContext(), ForgetPasswordSuccessMessageActivity.class));
        finish();




//        Intent intent = new Intent(getApplicationContext(),ForgetPasswordSuccessMessageActivity.class);
//        startActivity(intent);
//        finish();

    }


    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private boolean isPasswordValid() {
        // check for a valid password (minimum 6 characters) and the password has to be the same as in the conformed password

        String _password = mPasswordView.getEditText().getText().toString().trim();
        String _confirmedPassword = mConfirmPasswordView.getEditText().getText().toString().trim();

        return _confirmedPassword.equals(_password) && _password.length()>6 ;



    }
}
package com.Sannad.SannadApp.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.Sannad.SannadApp.Database.SharedPmanager;
import com.Sannad.SannadApp.R;

import java.util.HashMap;

public class TestingActivity extends AppCompatActivity {

    TextView mTextView ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_testing);

        mTextView= findViewById(R.id.testingInputText);

        SharedPmanager sharedPmanager = new SharedPmanager(this);
        HashMap<String,String> userDetails = sharedPmanager.getUserDetailFromPreference();

        String username = userDetails.get(SharedPmanager.KEY_USERNAME);
        String phone = userDetails.get(SharedPmanager.KEY_PHONE);


        Log.d("islogin", "onCreate: "+sharedPmanager.checkLogin());

        if (sharedPmanager.checkLogin()) {
//            Toast.makeText(this, "Loged in ", Toast.LENGTH_SHORT).show();
        }
        
        mTextView.setText(username + ": "+phone);



    }
}
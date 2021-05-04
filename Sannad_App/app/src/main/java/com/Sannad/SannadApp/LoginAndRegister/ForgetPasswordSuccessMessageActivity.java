package com.Sannad.SannadApp.LoginAndRegister;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.Sannad.SannadApp.R;

public class ForgetPasswordSuccessMessageActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password_success_message);
    }

    /* Move to LoginbyPhone Activity*/
    public void Login(View view) {
        Intent intent = new Intent(getApplication(), LoginByPhoneActivity.class);
        startActivity(intent);
        finish();

    }
}
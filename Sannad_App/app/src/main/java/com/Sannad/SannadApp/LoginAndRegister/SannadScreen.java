package com.Sannad.SannadApp.LoginAndRegister;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.Sannad.SannadApp.Activity.MainActivity;
import com.Sannad.SannadApp.R;

public class SannadScreen extends AppCompatActivity {

    private static int ScreenTimer = 2000;

    View logo_Letter;
    View logo_name;

    //animation
    Animation sideAnim, bottomAnim;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sannad_screen);

        logo_Letter = findViewById(R.id.screenTextView1);
        logo_name = findViewById(R.id.screenTextView2);


        //Animation
        sideAnim = AnimationUtils.loadAnimation(this, R.anim.side_animation);
        bottomAnim = AnimationUtils.loadAnimation(this, R.anim.bottom_animation);

        // setting Animation on the Elements
        logo_name.setAnimation(sideAnim);
        logo_Letter.setAnimation(bottomAnim);


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                finish();


            }
        },ScreenTimer);

    }
}
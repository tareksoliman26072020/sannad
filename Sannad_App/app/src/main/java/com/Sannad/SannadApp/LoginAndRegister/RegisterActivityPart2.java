package com.Sannad.SannadApp.LoginAndRegister;


import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.Sannad.SannadApp.R;

import java.util.Calendar;

public class RegisterActivityPart2 extends AppCompatActivity {

    ImageView backBtn;
    Button next ;
    TextView tileText;

    RadioGroup radioGroup ;
    RadioButton selectedGender;
    DatePicker datePicker;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_part2);

        backBtn = findViewById(R.id.register_back_button);
        next = findViewById(R.id.register_next_button);
        tileText = findViewById(R.id.register_titleText);

        radioGroup = findViewById(R.id.radioGroup);
        datePicker = findViewById(R.id.agePicker);








    }

    public  void  NextSignUp(View view) {

        if (!isNetworkAvailable()) {

            Toast.makeText(this, R.string.no_internet_Connection, Toast.LENGTH_SHORT).show();
            return;
        }


        //check for validation in not valid then return and do not execute the code
        if (!validateGender() | !validateAge()) {
            return;
        }

        selectedGender = findViewById(radioGroup.getCheckedRadioButtonId());


        String _gender =selectedGender.getText().toString();
        int year = datePicker.getYear();
        int month = datePicker.getMonth()+1;
        int day  = datePicker.getDayOfMonth();

        String _date= day+"/"+month+"/"+year;


        //getting data from intent
        String _email = getIntent().getStringExtra("email");
        String  _username = getIntent().getStringExtra("username");
        String  _password = getIntent().getStringExtra("password");


       // Toast.makeText(this, _email+" "+ _username + " "+ _password , Toast.LENGTH_SHORT).show();

        Intent intent = new Intent(getApplicationContext() , RegisterActivitypart3.class);

        //Passing   fields to the next activity
        intent.putExtra("email", _email);
        intent.putExtra("username", _username);
        intent.putExtra("password", _password);
        intent.putExtra("gender", _gender);
        intent.putExtra("date", _date);


        //Adding Transition
        Pair[] pairs = new Pair[2];
       // pairs[0]= new Pair<View,String>(backBtn,"transition_back_btn");
        pairs[0]= new Pair<View,String>(tileText,"transition_TitleText");
        pairs[1]= new Pair<View,String>(next,"transition_next_btn");

        //check if the Api is 21 or  higher
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            ActivityOptions activityOptions = ActivityOptions.makeSceneTransitionAnimation(RegisterActivityPart2.this, pairs);
            startActivity(intent, activityOptions.toBundle());
            finish();
        } else {
            // not animation for API less than 21
            startActivity(intent);
            finish();
        }





    }


    //Validate Gender
    private boolean validateGender() {

        if (radioGroup.getCheckedRadioButtonId() == -1) {  // -1 means none of the radiobutton has been selected
            Toast.makeText(this, R.string.select_Gender, Toast.LENGTH_SHORT).show();
            return false;
        } else {
            return true;
        }
    }


    //Validate Age
    private boolean validateAge() {
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        int userAge = datePicker.getYear();
        int isAgeValid = currentYear - userAge;

        if (isAgeValid < 12) {
            Toast.makeText(this, R.string.agehasTobe, Toast.LENGTH_SHORT).show();
            return false;
        } else
            return true;
    }


    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }



}
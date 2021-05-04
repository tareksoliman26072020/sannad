package com.Sannad.SannadApp.LoginAndRegister;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.Sannad.SannadApp.R;
import com.Sannad.SannadApp.TrackerService;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.hbb20.CountryCodePicker;


public class RegisterActivitypart3 extends AppCompatActivity {


    ImageView backBtn;
    Button next ;
    TextView tileText;
    private TextInputEditText phoneNumber;
    CountryCodePicker countryCodePicker;
    ProgressBar progressBar ;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_part3);

        backBtn = findViewById(R.id.register_back_button);
        next = findViewById(R.id.register_next_button);
        tileText = findViewById(R.id.register_titleText);
        phoneNumber= (TextInputEditText) findViewById(R.id.LoginPhoneNumber);
        countryCodePicker = findViewById(R.id.countryCodePicker);

        progressBar = findViewById(R.id.register3ProgressBar);



    }

    public  void  NextSignUp(View view) {

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
                    // if the number is already  exist
                    phoneNumber.setError(" That Phone Number is already registered,  If you forget your password you can click on reset  in the login page to reset you password");
                    progressBar.setVisibility(View.GONE);





                } else {

                    progressBar.setVisibility(View.GONE);



                    //getting data from intent
                    String _email = getIntent().getStringExtra("email");
                    String  _username = getIntent().getStringExtra("username");
                    String  _password = getIntent().getStringExtra("password");
                    String   _gender = getIntent().getStringExtra("gender");
                    String  _date = getIntent().getStringExtra("date");


                    // Toast.makeText(this, _email+" + "+_username+" + "+_password+" + "+_gender+" + "+_date, Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(getApplicationContext() , VerifyOTPActivity.class);

                    //Passing   fields to the next activity
                    intent.putExtra("email", _email);
                    intent.putExtra("username", _username);
                    intent.putExtra("password", _password);
                    intent.putExtra("gender", _gender);
                    intent.putExtra("date", _date);
                    intent.putExtra("phone", _phoneNo);
                    intent.putExtra("whatToDo","registernewUser");




                    //Adding Transition
                    Pair[] pairs = new Pair[2];
                    //pairs[0]= new Pair<View,String>(backBtn,"transition_back_btn");
                    pairs[0]= new Pair<View,String>(tileText,"transition_TitleText");
                    pairs[1]= new Pair<View,String>(next,"transition_next_btn");

                    //check if the Api is 21 or  higher
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                        ActivityOptions activityOptions = ActivityOptions.makeSceneTransitionAnimation(RegisterActivitypart3.this, pairs);
                        startActivity(intent, activityOptions.toBundle());
                        finish();
                    } else {
                        // not animation for API less than 21
                        startActivity(intent);
                        finish();
                    }





                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
            }


        });








    }

    //Validate Phone Number

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


    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }





}
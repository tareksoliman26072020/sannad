package com.Sannad.SannadApp.LoginAndRegister;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityOptions;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.Sannad.SannadApp.Activity.AccountSettingActivity;
import com.Sannad.SannadApp.R;
import com.Sannad.SannadApp.TrackerService;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import pub.devrel.easypermissions.EasyPermissions;


public class RegisterActivity extends AppCompatActivity {

    // Constants
    public static final String CHAT_PREFS = "ChatPreferences";
    public static final String DISPLAY_NAME_KEY = "username";

    public String TAG = "SannadReg";

    // UI references.
    private TextInputLayout mEmailView;
    private TextInputLayout mUsernameView;
    private TextInputLayout mPasswordView;
    private TextInputLayout mConfirmPasswordView;

    ImageView backBtn;
    Button next ;
    TextView tileText;


    // Firebase instance variables
    private FirebaseAuth mAuth;




    Toolbar mToolbar;
    //ProgressDailog
    private ProgressDialog mProgressDialog;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

//        //Toolbar setup
//        mToolbar = (Toolbar) findViewById(R.id.register_toolbar);
//        setSupportActionBar(mToolbar);
//        getSupportActionBar().setTitle("Create Account");
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mProgressDialog = new ProgressDialog(this);


        mEmailView =  findViewById(R.id.register_email);
        mPasswordView = findViewById(R.id.register_password);
        mConfirmPasswordView =  findViewById(R.id.register_confirm_password);
        mUsernameView =  findViewById(R.id.register_username);

        backBtn = findViewById(R.id.register_back_button);
        next = findViewById(R.id.register_next_button);
        tileText = findViewById(R.id.register_titleText);


        //enable gps
        if (! ((LocationManager) getSystemService(Context.LOCATION_SERVICE)).isProviderEnabled(LocationManager.GPS_PROVIDER)) {

            final AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(R.string.gps_request_message)
                    .setCancelable(false)
                    .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(final DialogInterface dialog, final int id) {
                            startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                        }
                    })
                    .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                        public void onClick(final DialogInterface dialog, final int id) {
                            dialog.cancel();
                        }
                    });
            final AlertDialog alert = builder.create();
            alert.show();
        }else {
            Toast.makeText(this, R.string.gps_enabled, Toast.LENGTH_SHORT).show();
        }

        //allow permission location
        String [] perms =new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};
        if(EasyPermissions.hasPermissions(this, perms)) {
            Toast.makeText(this, R.string.permission_granted, Toast.LENGTH_SHORT).show();
        }
        else {
            final AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(getString(R.string.gpsRequestPermission) +
                    getString(R.string.gps_note))
                    .setCancelable(false)
                    .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(final DialogInterface dialog, final int id) {
                            ActivityCompat.requestPermissions(RegisterActivity.this, new String[]{
                                    Manifest.permission.ACCESS_FINE_LOCATION,
                                    Manifest.permission.ACCESS_COARSE_LOCATION,}, 1);
                        }
                    })
                    .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                        public void onClick(final DialogInterface dialog, final int id) {
                            dialog.cancel();
                        }
                    });
            final AlertDialog alert = builder.create();
            alert.show();
        }


//        // Keyboard sign in action
//        mConfirmPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
//            @Override
//            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
//                if (id == R.integer.register_form_finished || id == EditorInfo.IME_NULL) {
//                    attemptRegistration();
//                    return true;
//                }
//                return false;
//            }
//        });

        // Get hold of an instance of FirebaseAuth
        mAuth = FirebaseAuth.getInstance();

        String x = "test";


        View backToRegister = findViewById(R.id.register_back_button);

        backToRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {




                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);



                startActivity(intent);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                finish();


            }
        });
    }

    // Executed when Sign Up button is pressed.
    public void signUp(View v) throws IOException {
        attemptRegistration();
    }

    /*Move to RegisterActivityPart2 */
    public  void  NextSignUp(View view) {

        if (!isNetworkAvailable()) {

            Toast.makeText(this, R.string.no_internet_Connection, Toast.LENGTH_SHORT).show();
            return;
        }



        attemptRegistration();






    }
    /* Register Validation*/
    private void attemptRegistration()  {

        // Reset errors displayed in the form.
        mEmailView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String email = mEmailView.getEditText().getText().toString().trim();
        String password = mPasswordView.getEditText().getText().toString().trim();
        String enteredUsername = mUsernameView.getEditText().getText().toString().trim();


        boolean cancel = false;
        View focusView = null;




        if (TextUtils.isEmpty(enteredUsername) || !isUsernameValid(enteredUsername)) {
            mPasswordView.setError(getString(R.string.error_invalid_username));
            focusView = mUsernameView;
            cancel = true;
        }


        // Check for a valid password, if the user entered one.
        if (TextUtils.isEmpty(password) || !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        if (TextUtils.isEmpty(enteredUsername)) {
            mUsernameView.setError(getString(R.string.error_field_required));
            focusView = mUsernameView;
            cancel = true;
        }
        // Check for a valid email address.
        else if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        } else if (!isEmailValid(email)) {

            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.

            focusView.requestFocus();
        }

        else {
                // if everything goes well create the user in Firebase
//            mProgressDialog.setTitle("Registering a new Account");
//            mProgressDialog.show();
//            mProgressDialog.setMessage("Please wait ");
//            mProgressDialog.setCanceledOnTouchOutside(false);
                //createFirebaseUser();


            Intent intent = new Intent(this , RegisterActivityPart2.class);
            String _email = mEmailView.getEditText().getText().toString().trim();
            String _password = mPasswordView.getEditText().getText().toString().trim();
            String _enteredUsername = mUsernameView.getEditText().getText().toString().trim();
            //Passing   fields to the next activity
            intent.putExtra("email", _email);
            intent.putExtra("username", _enteredUsername);
            intent.putExtra("password", _password);



            //Adding Transition
            Pair[] pairs = new Pair[3];
            pairs[0]= new Pair<View,String>(backBtn,"transition_back_btn");
            pairs[1]= new Pair<View,String>(tileText,"transition_TitleText");
            pairs[2]= new Pair<View,String>(next,"transition_next_btn");

            //check if the Api is 21 or  higher
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                ActivityOptions activityOptions = ActivityOptions.makeSceneTransitionAnimation(RegisterActivity.this, pairs);
                startActivity(intent, activityOptions.toBundle());
                finish();
            } else {
                // not animation for API less than 21
                startActivity(intent);
                finish();
            }



        }

    }



    /*  Validate the email*/
    private boolean isEmailValid(String email) {
        /*
        The Method takes a string as an input and then checks  whether the email Valid or not. it will return ture if the email is valid and false otherwise .
        A valid email address has four parts:
            Recipient name
            @ symbol
            Domain name
            Top-level domain

         */
    //    String regex = "^[\\w-_\\.+]*[\\w-_\\.]\\@([\\w]+\\.)+[\\w]+[\\w]$";
        String regex = "^([a-zA-Z0-9_\\-\\.]+)@([a-zA-Z0-9_\\-\\.]+)\\.([a-zA-Z]{2,5})$";
        return email.matches(regex);

    }

    /* Validate the Password*/
    private boolean isPasswordValid(String password) {
        // check for a valid password (minimum 6 characters) and the password has to be the same as in the conformed password

        String confirmedPassword = mConfirmPasswordView.getEditText().getText().toString().trim();

        return confirmedPassword.equals(password) && password.length()>6 ;



    }

    //Validate UserName

    private boolean isUsernameValid(String enteredUsername) {

       // username max size
        return enteredUsername.length() < 30;
    }

    /*Check for Network connectivity*/
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }




    // Create a Firebase user

    private void createFirebaseUser() {
        //this method will take care of setting up a new user on Firebase Server
        String email = mEmailView.getEditText().getText().toString().trim();
        String password = mPasswordView.getEditText().getText().toString().trim();

        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                // this method will report back to us if creating a user on firebase has been successful or there has been an error

                Log.d(TAG, "createUser on Complete: " + task.isSuccessful());

                if (!task.isSuccessful()) {

                    Log.i(TAG, "onComplete: user creation failed");
                    showErrorDailog("Registration attempt Failed !!");
                } else {
                    // we only want to save the username after the user has been successfully created on Firebase.
                    saveDisplayName();

                    Toast.makeText(RegisterActivity.this, "User has been created successfully ", Toast.LENGTH_SHORT).show();

                    // Navigate the user to the MainActivity
                    Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);

                    // to clear off all off the previous activities on your activity  stack . by doing so when we hit the back button it will bring us to the desktop . if we did not use this , it will bring us back to the Register Activity
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    finish();
                    startActivity(intent);
                }
            }
        });


    }







    /* Save the Display name */
    private void saveDisplayName() {
        //save DisplayName to Firebase
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String displayName = mUsernameView.getEditText().getText().toString().trim();
        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setDisplayName(displayName).build();

        user.updateProfile(profileUpdates)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "User profile updated.");
                        }
                    }
                });

        //-->> May we don't need to Save the display name to Shared Preferences, because it is allready saved above in Firebase database
        //  Saving the display name to Shared Preferences
        //we used SharedPreferences to save data locally. SharedPreferences are way of saving simple bases of infromation as a {key: value}  pair
        SharedPreferences preferences = getSharedPreferences(CHAT_PREFS, 0);

        // to make use of our shared preferences object, we first have to inform it that it is going to be edited
        preferences.edit()
                .putString(DISPLAY_NAME_KEY,displayName)   // putString will provide the data in the form of a key value pair.The key is going to be a string constant that we have supplied at the Top of this file and the value is what the user typed as username
                .apply(); //  to commit our data and save the information to the device
    }

    /* Show error Dialog */
    private  void showErrorDailog(String message) {
        // This Method will  create an alert dialog to show in case registration failed


        new AlertDialog.Builder(this).setTitle(":(")
                .setMessage(message)
                .setPositiveButton(android.R.string.ok, null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show()
        ;

    }



}

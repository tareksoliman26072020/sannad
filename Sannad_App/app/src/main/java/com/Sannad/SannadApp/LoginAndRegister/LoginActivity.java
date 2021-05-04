package com.Sannad.SannadApp.LoginAndRegister;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.Sannad.SannadApp.Activity.MainActivity;
import com.Sannad.SannadApp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

/** This class is for the login activity */
public class LoginActivity extends AppCompatActivity {

    /** Firebase authentication token */
    private FirebaseAuth mAuth ;


    private TextInputLayout mEmailView;

    /** Password box */
    private TextInputLayout mPasswordView;

    /** Progress dialog */
    private ProgressDialog mProgressDialog;

    /** Called on activity creation */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //Toolbar set
        mProgressDialog = new ProgressDialog(this);



        mEmailView =  findViewById(R.id.login_email);
        mPasswordView =  findViewById(R.id.login_password);

//        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
//            @Override
//            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
//                if (id == R.integer.login || id == EditorInfo.IME_NULL) {
//                    attemptLogin();
//                    return true;
//                }
//                return false;
//            }
//        });

        //  Grab an instance of FirebaseAuth
        mAuth = FirebaseAuth.getInstance();
        String uid = mAuth.getUid();
        Log.i("gettingUID", "onCreate: "+mAuth.getUid());

        findViewById(R.id.forgotPasswordButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(getApplicationContext(), ForgetPasswordActivity.class);
                startActivity(intent);


            }
        });




    }

    /** Forgot password */
    private void forgotMyPassword(){

    }


    // Executed when Sign in button pressed
    public void signInExistingUser(View v)   {
        attemptLogin();
    }

    /* Executed when Register button pressed */
    public void registerNewUser(View v) {
        Intent intent = new Intent(this, RegisterActivity.class);
        finish();
        startActivity(intent);
    }


    /* Login Validation */
    private void attemptLogin() {

        String email = mEmailView.getEditText().getText().toString().trim();
        String password = mPasswordView.getEditText().getText().toString().trim();

        Toast.makeText(this, email, Toast.LENGTH_SHORT).show();

        //check if the email or the password is empty , if either of these conditions is true then we want to stop executing the code
        if (email.equals("")|| password.equals("")) {
            return;
        }


//        Using  FirebaseAuth to sign in with email & password
        mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                mProgressDialog.setTitle("Login . . .");
                mProgressDialog.show();

                if (!task.isSuccessful()) {
                    // show 
                    showErrorDailog(" Login attempt failed !!");
                    mProgressDialog.hide();

                }
                else {

                    // show message and go to the main activity
                    Toast.makeText(LoginActivity.this, "Login is Succeed ", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);


                    // to clear off all off the previous activities on your activity  stack . by doing so when we hit the back button it will bring us to the desktop . if we did not use this , it will bring us back to the login activity
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);

                    finish();
                    startActivity(intent);
                }
                    
                    
            }
        });
        

      



    }

    
    // Show error on screen with an alert dialog

    private  void showErrorDailog(String message) {
        // This Method will  create an alert dialog to show in case Login failed


        new AlertDialog.Builder(this).setTitle(":(")
                .setMessage(message)
                .setPositiveButton(android.R.string.ok, null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show()
        ;

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.sign_menu, menu);

        return  true;

    }


    //Option Menu
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        super.onOptionsItemSelected(item);

        if (item.getItemId()==R.id.exitButton) {
            Toast.makeText(this, "Have a nice day", Toast.LENGTH_SHORT).show();
            FirebaseAuth.getInstance().signOut();

            finishAndRemoveTask();

        }

        return true;
    }




}
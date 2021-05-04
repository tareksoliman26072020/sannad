package com.Sannad.SannadApp.Activity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.Sannad.SannadApp.ChatListAdapter;
import com.Sannad.SannadApp.Model.FirebaseMessage;
import com.Sannad.SannadApp.Model.GlobalStatic;
import com.Sannad.SannadApp.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


/**
 * Chat activity class
 */
public class MainChatActivity extends AppCompatActivity {

    /** Chat list view */
    private ListView mChatListView;

    /** Input text box */
    private EditText mInputText;

    /** Send button */
    private ImageButton mSendButton;

    /** Database reference */
    private DatabaseReference mDatabaseReference;

    /** Chat adapter */
    private ChatListAdapter mAdapter;

    // No idea wtf this is
    public String TAG = "SannadMain";

    /** Toolbar */
    private Toolbar mToolbar;

    /** Message */
    private FirebaseMessage message;

    /** Other user phone number */
    private String otherUserPhoneNumber;

    /** Return to main activity */
    @Override
    public void onBackPressed(){
        startActivity(new Intent(getApplicationContext(),MainActivity.class));
        finish();
    }

    /** On create activity */
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_chat);

        mToolbar = (Toolbar) findViewById(R.id.main_app_bar);
        setSupportActionBar(mToolbar);

        getSupportActionBar().setTitle("Sannad");
        mInputText = (EditText) findViewById(R.id.messageInput);
        mSendButton = (ImageButton) findViewById(R.id.sendButton);
        mChatListView = (ListView) findViewById(R.id.chat_list_view);
        mInputText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                sendMessage();
                return true;
            }
        });
        mSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage();
            }
        });
        otherUserPhoneNumber = getIntent().getStringExtra("otherUser");
        mDatabaseReference = FirebaseDatabase.getInstance().getReference("Users").child(GlobalStatic.myPhoneNumber).child("Chatrooms").child(otherUserPhoneNumber).child("Messages");
    }

    /** Send a new message */
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void sendMessage() {
        String input = mInputText.getText().toString();
        LocalDateTime localDateTime = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        if (!input.isEmpty()) {
            message = new FirebaseMessage(GlobalStatic.myPhoneNumber,input,localDateTime.format(formatter));
            FirebaseDatabase.getInstance().getReference("Users").child(GlobalStatic.myPhoneNumber).child("Chatrooms").child(otherUserPhoneNumber).child("Messages").push().setValue(message);
            FirebaseDatabase.getInstance().getReference("Users").child(GlobalStatic.myPhoneNumber).child("Chatrooms").child(otherUserPhoneNumber).child("LastMessage").setValue(message.getMessageText());
            FirebaseDatabase.getInstance().getReference("Users").child(otherUserPhoneNumber).child("Chatrooms").child(GlobalStatic.myPhoneNumber).child("Messages").push().setValue(message);
            FirebaseDatabase.getInstance().getReference("Users").child(otherUserPhoneNumber).child("Chatrooms").child(GlobalStatic.myPhoneNumber).child("LastMessage").setValue(message.getMessageText());
            // Clear message box
            mInputText.setText("");
        }
    }

    /** Start adapter */
    @Override
    public void onStart() {
        super.onStart();
        mAdapter = new ChatListAdapter(this, mDatabaseReference);
        mChatListView.setAdapter(mAdapter);
    }

    /** Stop and clear adapter */
    @Override
    public void onStop() {
        super.onStop();

        // Remove the Firebase event listener on the adapter.
        mAdapter.cleanup();

    }

}

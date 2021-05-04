package com.Sannad.SannadApp.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.Sannad.SannadApp.Model.FirebaseMessage;
import com.Sannad.SannadApp.Model.GlobalStatic;
import com.Sannad.SannadApp.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import lombok.SneakyThrows;

public class ShowingRequestActivity extends AppCompatActivity {

    /** Request user username */
    private String username = "";

    /** When the request */
    private String time = "";

    /** Phone number of request user */
    private String phoneNumber = "";

    /** My phone number */
    private String myPhoneNumber = GlobalStatic.myPhoneNumber;

    /** Firebase */
    private FirebaseDatabase database = FirebaseDatabase.getInstance();

    /** Database reference */
    private DatabaseReference databaseReference = database.getReference("Users");

    /** On activity creation */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_showing_request);

        Intent intent = getIntent();
        Bundle b = intent.getExtras();

        final TextView showLess = (TextView)findViewById(R.id.requestShowLessExtended);
        //hide show less
        showLess.setVisibility(View.GONE);

        //get request details from home page (CustomRequestListAdapter)
        if(b!=null){
            //get request username
            this.username = (String)b.get("username");
            ((TextView)findViewById(R.id.requestUsernameExtended)).setText(username);

            //get date
            this.time = (String)b.get("time");
            ((TextView)findViewById(R.id.requestDateExtended)).setText(GlobalStatic.getDate(time));

            //get profile pic
            //access bucket "users_images" then name the image "<myPhoneNumber>.jpg"
            this.phoneNumber = (String)b.get("phonenumber");
            FirebaseDatabase.getInstance().getReference("Users").child(phoneNumber).
                    addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            ImageView profile_pic = (ImageView) findViewById(R.id.requestUserImageExtended);
                            String profile_pic_uri = snapshot.child("requestImageUrl").getValue(String.class);
                            Picasso.get().load(Uri.parse(profile_pic_uri)).fit().into(profile_pic);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

            //get request title
            String title = (String)b.get("title");
            ((TextView)findViewById(R.id.requestTitleExtended)).setText(title);

            //get request text
            final String originalText = (String)b.get("text");
            String text = (String)b.get("text");
            final TextView showMore = (TextView)findViewById(R.id.requestShowMoreExtended);
            //show only part of long texts
            if(text.length() > 414){
                text = text.substring(0,413) + "....";
            }
            //hide "show more" when text is not long
            else
                showMore.setVisibility(View.GONE);
            ((TextView)findViewById(R.id.requestTextExtended)).setText(text);

            //get request image
            String imageUrl = (String)b.get("image");
            ImageView image = (ImageView)findViewById(R.id.requestImageExtended);
            if(imageUrl.equals(GlobalStatic.noImage))
                image.setVisibility(View.GONE);
            else
                Picasso.get()
                        .load(imageUrl)
                        .into(image);
            //listener for "show more"
            showMore.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showMore.setVisibility(View.GONE);
                    ((TextView)findViewById(R.id.requestTextExtended)).setText(originalText);
                    showLess.setVisibility(View.VISIBLE);
                }
            });

            //listener for "show less"
            showLess.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showLess.setVisibility(View.GONE);
                    String text = originalText.substring(0,413) + "....";
                    ((TextView)findViewById(R.id.requestTextExtended)).setText(text);
                    showMore.setVisibility(View.VISIBLE);
                }
            });
        }
    }

    /** Contact user */
    public void clickContactUser(final View view){

        //Toast.makeText(this, "meow", Toast.LENGTH_SHORT).show();
        // get pop_up_message.xml view
        LayoutInflater li = LayoutInflater.from(this);
        View promptsView = li.inflate(R.layout.pop_up_message, null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

        // set pop_up_message.xml to alertdialog builder
        alertDialogBuilder.setView(promptsView);

        //message to be sent to user
        final EditText userInput = (EditText) promptsView.findViewById(R.id.messageText);

        // set dialog message
        alertDialogBuilder.
                setCancelable(false).
                setPositiveButton("send",
                        new DialogInterface.OnClickListener() {
                            @SneakyThrows
                            @RequiresApi(api = Build.VERSION_CODES.O)
                            public void onClick(DialogInterface dialog, int id) {
                                if (!phoneNumber.equals(myPhoneNumber)) {
                                    String messageText = userInput.getText().toString();
                                    LocalDateTime localDateTime = LocalDateTime.now();
                                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
                                    FirebaseMessage message = new FirebaseMessage(myPhoneNumber,messageText, localDateTime.format(formatter));
                                    // Set data for yourself
                                    databaseReference.child(myPhoneNumber).child("Chatrooms").child(phoneNumber).child("Messages").push().setValue(message);
                                    databaseReference.child(myPhoneNumber).child("Chatrooms").child(phoneNumber).child("LastMessage").setValue(messageText);
                                    databaseReference.child(myPhoneNumber).child("Chatrooms").child(phoneNumber).child("OtherUserImageUrl")
                                            .setValue("https://firebasestorage.googleapis.com/v0/b/sannadapp.appspot.com/o/no_personal_image%2Faccount_no_personal_image.png?alt=media&token=bddd98f3-a943-4074-bf64-a18ebe127035");
                                    // Set data for other user
                                    databaseReference.child(phoneNumber).child("Chatrooms").child(myPhoneNumber).child("Messages").push().setValue(message);
                                    databaseReference.child(phoneNumber).child("Chatrooms").child(myPhoneNumber).child("LastMessage").setValue(messageText);
                                    databaseReference.child(phoneNumber).child("Chatrooms").child(myPhoneNumber).child("OtherUserImageUrl")
                                            .setValue("https://firebasestorage.googleapis.com/v0/b/sannadapp.appspot.com/o/no_personal_image%2Faccount_no_personal_image.png?alt=media&token=bddd98f3-a943-4074-bf64-a18ebe127035");
                                    Intent intent = new Intent(view.getContext(),MainChatActivity.class);
                                    intent.putExtra("otherUser",phoneNumber);
                                    startActivity(intent);
                                    finish();
                                }
                            }
                        })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                dialog.cancel();
                            }
                        });

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();

    }

    @Override
    public void onBackPressed() {
        finish();
        startActivity(new Intent(this, MainActivity.class));
    }
}
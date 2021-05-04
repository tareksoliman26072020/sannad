package com.Sannad.SannadApp.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.Sannad.SannadApp.Model.GlobalStatic;
import com.Sannad.SannadApp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.concurrent.atomic.AtomicInteger;

public class ShowingMyRequestActivity extends AppCompatActivity {

    private String username = "";
    private String title = "";
    private String text = "";
    private String time = "";
    private String key = "";
    private Bitmap imageBitmap;
    private String myPhoneNumber = FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber();
    @SuppressLint("WrongThread")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_showing_my_request);

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

            //get profile picture
            FirebaseDatabase.getInstance().getReference("Users").child(myPhoneNumber).
                    addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            ImageView profile_pic_comp = (ImageView) findViewById(R.id.requestUserImageExtended);
                            String profile_pic_uri = snapshot.child("requestImageUrl").getValue(String.class);
                            Picasso.get().load(Uri.parse(profile_pic_uri)).fit().into(profile_pic_comp);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

            ImageView requestImage = (ImageView) findViewById(R.id.requestUserImageExtended);

            //get request title
            title = (String)b.get("title");
            ((TextView)findViewById(R.id.requestTitleExtended)).setText(title);

            //get request text
            final String originalText = (String)b.get("text");
            text = (String)b.get("text");
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
            final ImageView image = (ImageView)findViewById(R.id.requestImageExtended);
            if(!imageUrl.equals(GlobalStatic.noImage)){
                Picasso.get()
                        .load(imageUrl)
                        .into(image);

                try{
                    imageBitmap = ((BitmapDrawable)image.getDrawable()).getBitmap();
                    imageBitmap = Bitmap.createScaledBitmap(imageBitmap, 300, 300, false);
                } catch(NullPointerException e){
                    Log.d("exception","bitmap unable to be loaded");
                }

            }
            else
                image.setVisibility(View.GONE);

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
            key = (String) b.get("key");
            if(key != null)
                Log.d("key",key);
        }

        //edit request button
        Button edit_request_button = (Button)findViewById(R.id.edit_request_button);
        if(!GlobalStatic.listViewChosen){
            edit_request_button.setText("Access request from list view to edit the request");
            edit_request_button.setTextSize(12);
        }

        else
            edit_request_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(ShowingMyRequestActivity.this,EditRequestActivity.class);
                    intent.putExtra("title",title);
                    intent.putExtra("text",text);
                    intent.putExtra("bitmap",imageBitmap);
                    if(key.length() != 0)
                        intent.putExtra("requestkey",key);
                    startActivity(intent);
                }
            });
    }

    @Override
    public void onBackPressed() {
        finish();
        startActivity(new Intent(this,MainActivity.class));
    }
}
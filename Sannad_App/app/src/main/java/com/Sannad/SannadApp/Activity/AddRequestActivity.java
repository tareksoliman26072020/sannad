package com.Sannad.SannadApp.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.Sannad.SannadApp.LoginAndRegister.LoginActivity;
import com.Sannad.SannadApp.LoginAndRegister.LoginByPhoneActivity;
import com.Sannad.SannadApp.Model.GlobalStatic;
import com.Sannad.SannadApp.Model.Request;
import com.Sannad.SannadApp.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.iceteck.silicompressorr.FileUtils;
import com.iceteck.silicompressorr.SiliCompressor;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * A simple {@link AppCompatActivity} subclass for Adding Request to Firebase Database.
 */
public class AddRequestActivity extends AppCompatActivity {

    /** The Toolbar on the AddRequestsActivity.*/
    private Toolbar mToolbar;

    /** Database Reference.*/
    private DatabaseReference mDatabaseReference;

    /** The title of the request.*/
    private TextView titel;

    /** The desciption of the request.*/
    private TextView description;

    /** The time of the request.*/
    private String time;

    /** The Storage Reference of the Firebase.*/
    private StorageReference storageReference;

    /** The storage task.*/
    private StorageTask storageTask;

    /** The path of the image-request.*/
    private Uri filePath;

    /** The Firebase Authentication.*/
    private FirebaseAuth firebaseAuth;

    /** On create activity.*/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_request);
        mToolbar = (Toolbar) findViewById(R.id.request_app_bar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Sannad");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mDatabaseReference = FirebaseDatabase.getInstance().getReference("Users");
        storageReference = FirebaseStorage.getInstance().getReference("Images");
        firebaseAuth = FirebaseAuth.getInstance();

    }

    /** On activty result for crping the image-request.*/
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        ImageView imageView = findViewById(R.id.viewImage);
        if(requestCode == GlobalStatic.PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null )
        {
            filePath = data.getData();
            //crope the imge
            CropImage.activity(filePath).start(this);

        }

        //set the croped image to the imageview
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if(resultCode==RESULT_OK) {
                imageView.setImageURI(result.getUri());
                filePath = result.getUri();
            }
        }
    }

    /** Add the request to the Firebase database.*/
    public void addRequestToDatabase(){
        titel = findViewById(R.id.title_editText);
        description = findViewById(R.id.description_editText);

        Date date = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        time =formatter.format(date);

        if(titel.getText().toString().trim().length()==0 || description.getText().toString().trim().length()==0 ){
            Toast.makeText(this,"Required to fill the title and the description",Toast.LENGTH_LONG).show();
            return; }

        if(filePath==null){
            filePath=Uri.parse(GlobalStatic.noImage);
            Request request = new Request(titel.getText().toString(),description.getText().toString(),time,filePath.toString(),firebaseAuth.getCurrentUser().getPhoneNumber());
            //Go back to MY REQUESTS page
            mDatabaseReference.child(firebaseAuth.getCurrentUser().getPhoneNumber()).child("Requests").push().setValue(request);
            Intent intent = new Intent(this,MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            return;
        }

        //compress image
        File file = new File(SiliCompressor.with(this)
                .compress(FileUtils.getPath(this,filePath),new File(this.getCacheDir(),"temp")));
        Uri uri = Uri.fromFile(file);

        //upload
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        String extention = mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));

        final StorageReference storageRef = storageReference.child(System.currentTimeMillis() +"."+extention);
        storageTask= storageRef.putFile(uri);

        storageTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // Get a URL to the uploaded content

                storageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {

                        Request request = new Request(titel.getText().toString(),description.getText().toString(),time,uri.toString(),firebaseAuth.getCurrentUser().getPhoneNumber());
                        mDatabaseReference.child(firebaseAuth.getCurrentUser().getPhoneNumber()).child("Requests").push().setValue(request);
                    }
                });
            }
        });

        storageTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
                // ...
            }
        });


        Toast.makeText(this,"Added Request successfully",Toast.LENGTH_SHORT).show();

        //Go back to MY REQUESTS page
        Intent intent = new Intent(this,MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    /** Strat a new activtiy to choose an image from the local storage.*/
    public void chooseImage(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, GlobalStatic.PICK_IMAGE_REQUEST);
    }

    /** on back pressed.*/
    public void backToHomePage(View view){
        onBackPressed();
    }

    /** On create option menu for the AddRequestActivity.*/
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.add_request_menu,menu);
        menu.findItem(R.id.mainChangeViewButten).setTitle(GlobalStatic.listViewChosen ? "grid view" : "list view");
        return true;
    }

    /** On option item selected for the AddRequestActivity.*/
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        super.onOptionsItemSelected(item);

        if (item.getItemId()==R.id.mainLogoutButten) {
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(AddRequestActivity.this, LoginByPhoneActivity.class);
            startActivity(intent);
            finish();
        }
        else if (item.getItemId() == R.id.mainAccountSettingButton) { //TODO
            Intent intent = new Intent(this,AccountSettingActivity.class);
            startActivity(intent);
        }

        else if (item.getItemId() == R.id.sendReqest) {

            addRequestToDatabase();

        }

        else if (item.getItemId() == R.id.addPhoto) {

            chooseImage();

        }

        else {
            GlobalStatic.listViewChosen = !GlobalStatic.listViewChosen;
            Intent intent = new Intent(this,MainActivity.class);
            finish();
            startActivity(intent);
        }

        return true;
    }
}
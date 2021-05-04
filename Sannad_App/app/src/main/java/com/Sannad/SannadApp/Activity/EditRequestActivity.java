package com.Sannad.SannadApp.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.Sannad.SannadApp.LoginAndRegister.LoginActivity;
import com.Sannad.SannadApp.Model.GlobalStatic;
import com.Sannad.SannadApp.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.iceteck.silicompressorr.FileUtils;
import com.iceteck.silicompressorr.SiliCompressor;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

public class EditRequestActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private EditText title;
    private EditText text;
    private ImageView requestImage;
    private Uri filePath;
    private String key;

    public static int reload = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_request);

        mToolbar = (Toolbar) findViewById(R.id.request_app_bar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Sannad");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        title = (EditText) findViewById(R.id.title_editText);
        text = (EditText) findViewById(R.id.description_editText);
        requestImage = (ImageView)findViewById(R.id.viewImage);

        Intent intent = getIntent();
        Bundle b = intent.getExtras();
        if(b!=null){
            title.setText((String)b.get("title"));
            text.setText((String)b.get("text"));
            Bitmap bitmap = (Bitmap)b.get("bitmap");
            if(bitmap != null)
                requestImage.setImageBitmap((Bitmap) intent.getParcelableExtra("bitmap"));
            key = (String)b.get("requestkey");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.add_request_menu,menu);
        menu.findItem(R.id.mainChangeViewButten).setTitle(GlobalStatic.listViewChosen ? "grid view" : "list view");
        return true;
    }

    private void chooseImage(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), GlobalStatic.PICK_IMAGE_REQUEST);
    }

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

        //set the croed image to the imageview
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if(resultCode==RESULT_OK) {
                imageView.setImageURI(result.getUri());
                filePath = result.getUri();
            }
        }
    }

    private void editRequestInDatabase() {
        if (title.getText().toString().length() == 0 || text.getText().toString().length() == 0) {
            Toast.makeText(this,"an empty title or text is not allowed",Toast.LENGTH_SHORT).show();
        }

        //compress image
        File file = new File(SiliCompressor.with(this)
                .compress(FileUtils.getPath(this, filePath), new File(this.getCacheDir(), "temp")));
        Uri uri = Uri.fromFile(file);

        //upload
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        String extention = mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));

        //upload and update
        final StorageReference storageRef = FirebaseStorage.getInstance().getReference("Images").child(System.currentTimeMillis() + "." + extention);
        storageRef.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                storageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        //FirebaseStorage.getInstance().getReferenceFromUrl(url);
                        if (key != null) { // only in listview
                            GlobalStatic.myDatabaseRef.child("Requests").child(key).child("title").setValue(title.getText().toString());
                            GlobalStatic.myDatabaseRef.child("Requests").child(key).child("text").setValue(text.getText().toString());
                            GlobalStatic.myDatabaseRef.child("Requests").child(key).child("phoneNumber").setValue(GlobalStatic.myPhoneNumber);
                            GlobalStatic.myDatabaseRef.child("Requests").child(key).child("time").setValue(new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(new Date()));
                            GlobalStatic.myDatabaseRef.child("Requests").child(key).child("imageUrl").setValue(uri.toString());
                        }
                    }
                });
            }
        });
        MainActivity.viewPagerSetCurrentItem(2);
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        super.onOptionsItemSelected(item);

        if (item.getItemId()==R.id.mainLogoutButten) {
            Toast.makeText(this, "Logout", Toast.LENGTH_SHORT).show();
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(EditRequestActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        }
        else if (item.getItemId() == R.id.mainAccountSettingButton) {
            Intent intent = new Intent(this,AccountSettingActivity.class);
            startActivity(intent);
            Toast.makeText(this, "Setting", Toast.LENGTH_SHORT).show();
        }

        else if (item.getItemId() == R.id.sendReqest) {
            editRequestInDatabase();
            //Toast.makeText(this,"updating request",Toast.LENGTH_SHORT).show();

        }

        else if (item.getItemId() == R.id.addPhoto) {
            chooseImage();
        }

        else {
            onBackPressed();
        }

        return true;
    }
}
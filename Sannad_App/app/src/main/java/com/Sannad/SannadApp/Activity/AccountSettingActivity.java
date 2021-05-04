package com.Sannad.SannadApp.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.Sannad.SannadApp.Fragment.ProfilePicFragment;
import com.Sannad.SannadApp.LoginAndRegister.RegisterActivity;
import com.Sannad.SannadApp.Model.GlobalStatic;
import com.Sannad.SannadApp.Model.Request;
import com.Sannad.SannadApp.R;
import com.Sannad.SannadApp.TrackerService;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import lombok.SneakyThrows;
import pub.devrel.easypermissions.EasyPermissions;

public class AccountSettingActivity extends AppCompatActivity {

    //progress of seekbar
    private AtomicInteger prog = new AtomicInteger(0);

    //file path of image from gallery
    private Uri filePath;

    //outer container for profile picture
    private FrameLayout profile_pic_container;

    //access bucket "users_images" then name the image "<myPhoneNumber>.jpg"
    final StorageReference storageRef = FirebaseStorage.getInstance().getReference("users_images").child(GlobalStatic.myPhoneNumber + ".jpg");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_setting);

        //hide inner framelayout: profile_pic_container
        profile_pic_container = (FrameLayout) findViewById(R.id.profile_pic_container);
        profile_pic_container.setVisibility(View.GONE);

         //set username, address
        GlobalStatic.myDatabaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                TextView username = (TextView) findViewById(R.id.username);
                username.setText(snapshot.child("username").getValue(String.class));

                 TextView adresse =(TextView) findViewById(R.id.address);
                 adresse.setText(snapshot.child("adresse").getValue(String.class));

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        //set personal image from firebase: child accountImageUrl
        GlobalStatic.myDatabaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String accountImageUrl = (String)snapshot.child("accountImageUrl").getValue(String.class);
                Uri uri = Uri.parse(accountImageUrl);
                ImageView imageView = (ImageView) findViewById(R.id.image);
                Picasso.get().load(uri).fit().into(imageView);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void editClick(View view){
        // get pop_up_edit_account.xml view
        LayoutInflater li = LayoutInflater.from(this);
        final View promptsView = li.inflate(R.layout.pop_up_edit_account, null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

        // set pop_up_edit_account.xml to alertdialog builder
        alertDialogBuilder.setView(promptsView);

        final CheckBox check_username = (CheckBox) promptsView.findViewById(R.id.change_username);
        final CheckBox check_password = (CheckBox) promptsView.findViewById(R.id.change_password);
        final CheckBox check_location = (CheckBox) promptsView.findViewById(R.id.change_location);

        final EditText username = (EditText) promptsView.findViewById(R.id.username);
        username.setVisibility(View.GONE);

        final LinearLayout password_layout = (LinearLayout) promptsView.findViewById(R.id.password_layout);
        final EditText old_password = (EditText) promptsView.findViewById(R.id.old_password);
        final EditText new_password = (EditText) promptsView.findViewById(R.id.new_password);
        final EditText repeat_password = (EditText) promptsView.findViewById(R.id.repeat_password);
        password_layout.setVisibility(View.GONE);

        final AtomicReference<String> oldPassword = new AtomicReference<>();
        FirebaseDatabase.getInstance().getReference("Users").child(GlobalStatic.myPhoneNumber).
                addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        oldPassword.set(snapshot.child("password").getValue(String.class));
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        final AtomicBoolean v1 = new AtomicBoolean();
        final AtomicBoolean v2 = new AtomicBoolean();

        //show password textFields
        check_password.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    password_layout.setVisibility(View.VISIBLE);
                    v1.set(true);
                }
                else{
                    password_layout.setVisibility(View.GONE);
                    v1.set(false);
                }
            }
        });

        //show username textfield
        check_username.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    username.setVisibility(View.VISIBLE);
                    v2.set(true);
                }
                else{
                    username.setVisibility(View.GONE);
                    v2.set(false);
                }
            }
        });

        //changed arranges for going back home page if username or password is changed.
        final AtomicBoolean changed = new AtomicBoolean(false);

        // set the alert dialog (pop-up window)
        alertDialogBuilder
                .setCancelable(true)
                .setPositiveButton("Ok",
                        new DialogInterface.OnClickListener() {
                            @SneakyThrows
                            public void onClick(DialogInterface dialog, int id) {
                                //value tells whether password and username checkboxes are checked
                                String value = fromBool(v2.get())+fromBool(v1.get());

                                //if true, user is trying to change password
                                if(value.charAt(1) == '1'){

                                    //old password is wrong
                                    if(!old_password.getText().toString().equals(oldPassword.get()))
                                        Toast.makeText(getApplicationContext(),"The old password is wrong",Toast.LENGTH_LONG).show();
                                    //new password is empty
                                    else if(new_password.getText().toString().equals(""))
                                        Toast.makeText(getApplicationContext(),"The new password should not be empty",Toast.LENGTH_LONG).show();
                                    //password does not match
                                    else if (!new_password.getText().toString().equals(repeat_password.getText().toString()))
                                        Toast.makeText(getApplicationContext(),"The password does not match",Toast.LENGTH_LONG).show();
                                    //new password is short
                                    else if(new_password.getText().toString().length()<6)
                                        Toast.makeText(getApplicationContext(),"The password should at least be 6 characters long",Toast.LENGTH_LONG).show();
                                    else{
                                        FirebaseDatabase.getInstance().getReference("Users").child(GlobalStatic.myPhoneNumber).
                                                child("password").setValue(new_password.getText().toString());
                                        ////////////////////////
                                        changed.set(true);
                                    }
                                }
                                //if true, the user is trying to change the username
                                if(value.charAt(0) == '1'){
                                    if(!username.getText().toString().equals("") && username.getText().toString() != null){
                                        FirebaseDatabase.getInstance().getReference("Users").
                                                child(GlobalStatic.myPhoneNumber).child("username").setValue(username.getText().toString());
                                        changed.set(true);
                                    }
                                    else
                                        Toast.makeText(getApplicationContext(),"The new unsername should not be empty",Toast.LENGTH_LONG).show();
                                }

                                if(check_location.isChecked()){

                                    //get location
                                        TrackerService trackerService = new TrackerService(AccountSettingActivity.this);

                                        if(trackerService.getLatitude()==null || trackerService.getLongitude()==null){
                                            Toast.makeText(AccountSettingActivity.this, "Your GPS is still determining your current location, please try again.", Toast.LENGTH_LONG).show();
                                            return;
                                        }

                                        //show addresse

                                        Geocoder geocoder;
                                        List<Address> addresses;
                                        geocoder = new Geocoder(AccountSettingActivity.this, Locale.getDefault());

                                        addresses = geocoder.getFromLocation(trackerService.getLatitude(), trackerService.getLongitude(), 1);

                                        String adresse = addresses.get(0).getAddressLine(0);
                                        if (adresse != null || adresse != "") {
                                            GlobalStatic.myDatabaseRef.child("adresse").setValue(adresse);
                                        }

                                        String postalCode = addresses.get(0).getPostalCode();
                                        if(postalCode != null || postalCode != ""){
                                            GlobalStatic.myDatabaseRef.child("postalCode").setValue(postalCode);
                                        }
                                        String city = addresses.get(0).getLocality();
                                        if(city != null || city != ""){
                                            GlobalStatic.myDatabaseRef.child("city").setValue(city);
                                        }
                                    String country = addresses.get(0).getCountryName();
                                    if(country != null || country != ""){
                                        GlobalStatic.myDatabaseRef.child("country").setValue(country);
                                    }
                                        Toast.makeText(AccountSettingActivity.this, "Your location is successfully updated", Toast.LENGTH_SHORT).show();
                                        changed.set(true);
                                }

                                //if true, go back to MainActivity
                                if(changed.get())
                                    AccountSettingActivity.this.onBackPressed();
                            }

                            private String fromBool(boolean b) {
                                return b ? "1" : "0";
                            }
                        })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                dialog.cancel();
                            }
                        });

        check_location.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){

                    //enalbe GPS
                    if (! ((LocationManager) getSystemService(Context.LOCATION_SERVICE)).isProviderEnabled(LocationManager.GPS_PROVIDER)) {

                        final androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(AccountSettingActivity.this);
                        builder.setMessage("You need to enable your GPS to access your new location.")
                                .setCancelable(false)
                                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    @SneakyThrows
                                    public void onClick(final DialogInterface dialog, final int id) {
                                        startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                                        //if there is no permission
                                        if(!EasyPermissions.hasPermissions(AccountSettingActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION,}))
                                            Toast.makeText(AccountSettingActivity.this, "Your location will not be updated, because your location permission is disabled.", Toast.LENGTH_LONG).show();
                                    }
                                })
                                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                    public void onClick(final DialogInterface dialog, final int id) {
                                        dialog.cancel();
                                        check_location.setChecked(false);
                                        Toast.makeText(AccountSettingActivity.this, "Your location was not updated", Toast.LENGTH_SHORT).show();
                                    }
                                });
                        final androidx.appcompat.app.AlertDialog alert = builder.create();
                        alert.show();

                    }else {
                        Toast.makeText(AccountSettingActivity.this, "Your GPS is already enabled", Toast.LENGTH_SHORT).show();
                    }

                    //allow permission location
                    String [] perms =new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION,};
                    if(EasyPermissions.hasPermissions(AccountSettingActivity.this, perms)) {
                        Toast.makeText(AccountSettingActivity.this, "Locaton permission is already granted", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        final androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(AccountSettingActivity.this);
                        builder.setMessage("Please grant the location permission!!\n \n" +
                                "Note: It is important to allow location permission and after that to enable your GPS so that requests of users can be filtered, depending on the locations of users.")
                                .setCancelable(false)
                                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    public void onClick(final DialogInterface dialog, final int id) {
                                        ActivityCompat.requestPermissions(AccountSettingActivity.this, new String[]{
                                                Manifest.permission.ACCESS_FINE_LOCATION,
                                                Manifest.permission.ACCESS_COARSE_LOCATION,}, 1);
                                    }
                                })
                                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                    public void onClick(final DialogInterface dialog, final int id) {
                                        dialog.cancel();
                                        check_location.setChecked(false);
                                    }
                                });
                        final androidx.appcompat.app.AlertDialog alert = builder.create();
                        alert.show();
                    }

                }
            }
        });


        // create alert dialog (pop-up window)
        AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();
    }

    //when image is clicked
    public void clickImage(View view){
        final String[] rows = new String[]{"show profile picture","change profile picture","delete profile picture"};
        // get pop_up_image.xml view
        LayoutInflater li = LayoutInflater.from(this);
        View promptsView = li.inflate(R.layout.pop_up_image, null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

        // set pop_up_image.xml to alertdialog builder
        alertDialogBuilder.setView(promptsView);

        ListView listView = promptsView.findViewById(R.id.listviewclickimage);
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, R.layout.row_pop_up_account_settings_image, R.id.item, rows);
        listView.setDivider(null);
        listView.setAdapter(arrayAdapter);


        // create alert dialog
        final AlertDialog alertDialog = alertDialogBuilder.create();

        //when row is clicked
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                alertDialog.cancel();

                if(position == 0){
                    GlobalStatic.myDatabaseRef.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            String imageUri = snapshot.child("accountImageUrl").getValue(String.class);
                            if(imageUri.equals(GlobalStatic.accountNoPersonalImage))
                                Toast.makeText(getApplicationContext(),"no profile pic to be shown",Toast.LENGTH_SHORT).show();
                            else{
                                profile_pic_container.setVisibility(View.VISIBLE);
                                loadFragment(ProfilePicFragment.newInstance());
                                Button close = (Button) findViewById(R.id.profile_pic_close);
                                close.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        profile_pic_container.setVisibility(View.GONE);
                                    }
                                });
                            }
                        }

                        private void loadFragment(Fragment fragment){
                            FragmentManager fm = getSupportFragmentManager();
                            FragmentTransaction ft = fm.beginTransaction();
                            ft.replace(R.id.profile_pic_container,fragment);
                            ft.commit();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }

                if(position == 1){
                    //access gallery on phone
                    Intent intent = new Intent();
                    intent.setType("image/*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(intent, GlobalStatic.PICK_IMAGE_REQUEST);
                }
                if(position == 2){
                    storageRef.delete();
                    //make node noimage
                    GlobalStatic.myDatabaseRef.child("accountImageUrl").setValue(GlobalStatic.accountNoPersonalImage);
                    GlobalStatic.myDatabaseRef.child("requestImageUrl").setValue(GlobalStatic.requestNoPersonalImage);
                }
            }
        });

        // show it
        alertDialog.show();
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        Intent intent = new Intent(this,MainActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        ImageView imageView = findViewById(R.id.image);
        if (requestCode == GlobalStatic.PICK_IMAGE_REQUEST &&
            resultCode == RESULT_OK &&
            data != null &&
            data.getData() != null) {

            filePath = data.getData();

            //crope the image
            CropImage.activity(filePath).setFixAspectRatio(true).start(this);
        }

        //set the croped image to the imageview
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if(resultCode==RESULT_OK) {
                imageView.setImageURI(result.getUri());
                filePath = result.getUri();

                // Get the image from an ImageView as bytes
                imageView.setDrawingCacheEnabled(true);
                imageView.buildDrawingCache();
                Bitmap bitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                byte[] dataa = baos.toByteArray();

                //upload the image to bucket "users_images"
                UploadTask uploadTask = storageRef.putBytes(dataa);
                uploadTask.addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Handle unsuccessful uploads
                    }
                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        //get link from bucket: "users_images"
                        UploadTask uploadTask = storageRef.putFile(filePath);
                        uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                            @Override
                            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                                if (!task.isSuccessful()) {
                                    throw task.getException();
                                }

                                // Continue with the task to get the download URL
                                return storageRef.getDownloadUrl();
                            }
                        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                            @Override
                            public void onComplete(@NonNull Task<Uri> task) {
                                if (task.isSuccessful()) {
                                    //the wanted download link of the image
                                    Uri downloadUri = task.getResult();
                                    GlobalStatic.myDatabaseRef.child("accountImageUrl").setValue(downloadUri.toString());
                                    GlobalStatic.myDatabaseRef.child("requestImageUrl").setValue(downloadUri.toString());
                                } else {
                                    // Handle failures
                                }
                            }
                        });
                    }
                });

            }
        }
    }
}
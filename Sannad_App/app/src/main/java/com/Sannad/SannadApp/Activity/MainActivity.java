package com.Sannad.SannadApp.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import androidx.appcompat.widget.SearchView;

import android.widget.ImageButton;
import android.widget.Toast;

import com.Sannad.SannadApp.Fragment.MyRequestsGridViewFragment;
import com.Sannad.SannadApp.Fragment.MyRequestsListViewFragment;
import com.Sannad.SannadApp.Fragment.RequestsGridViewFragment;
import com.Sannad.SannadApp.Fragment.RequestsListViewFragment;
import com.Sannad.SannadApp.LoginAndRegister.LoginByPhoneActivity;
import com.Sannad.SannadApp.Model.GlobalStatic;
import com.Sannad.SannadApp.Model.Request;
import com.Sannad.SannadApp.PagerAdapter;
import com.Sannad.SannadApp.R;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

import io.realm.Realm;

public class MainActivity extends AppCompatActivity {

    /** Database Reference.*/
    private DatabaseReference mDatabaseReference;

    /** Firebase Authentication.*/
    private FirebaseAuth mAuth;

    private static ViewPager mViewPager;
    private static PagerAdapter mPagerAdapter;
    private TabLayout mTabLayout;
    Toolbar mToolbar;

    public static PagerAdapter getPagerAdapter(){
        return mPagerAdapter;
    }

    /**On create.*/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /** DO NOT REMOVE THIS OR I KILL U AIGHT? */
        try{
            Realm.init(this);
        } catch(IllegalStateException e){
            startActivity(new Intent(this,MainActivity.class));
        }

        setContentView(R.layout.activity_main);

        mToolbar = (Toolbar) findViewById(R.id.main_app_bar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Sannad");
        mToolbar.setTitleTextColor(Color.WHITE);


        //initialize the FirebaseAuth instance
        mAuth = FirebaseAuth.getInstance();

        //Tabs
        mViewPager = findViewById(R.id.myViewPager);

        //set fragment, that after 3 swiping right will not be updated.
        mViewPager.setOffscreenPageLimit(3);
        mPagerAdapter = new PagerAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(mPagerAdapter);
        mTabLayout = findViewById(R.id.main_tabs);
        mTabLayout.setupWithViewPager(mViewPager);

    }

    /**On Start.*/
    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();

        //check if the user has is logged on , if he is not logged in move him to the login activity
        if (currentUser == null) {
            Intent intent = new Intent(MainActivity.this, LoginByPhoneActivity.class);
            startActivity(intent);
            finish();

        }
        getUserInfromatiokn();

    }

    public static void viewPagerSetCurrentItem(int item){
        mViewPager.setCurrentItem(item);
    }

    /**On back pressed.*/
    @Override
    public void onBackPressed() {
        if (mViewPager.getCurrentItem() == 0) {
            // If the user is currently looking at the first step, allow the system to handle the
            // Back button. This calls finish() on this activity and pops the back stack.
            super.onBackPressed();
        } else {
            // Otherwise, select the first step = 0.
            mViewPager.setCurrentItem(0);
        }
    }



    public void getUserInfromatiokn() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String user = FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber();

            mDatabaseReference = FirebaseDatabase.getInstance().getReference("Users").child(user);
            // Name, email address, and profile photo Url

            mDatabaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    mPagerAdapter = new PagerAdapter(getSupportFragmentManager());
                    mViewPager.setAdapter(mPagerAdapter);
                    String name = snapshot.child("username").getValue(String.class);
                    String email = snapshot.child("email").getValue(String.class);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }

    /**create the option menu for the Main Activity.*/
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.main_menu,menu);
        menu.findItem(R.id.mainChangeViewButten).setTitle(GlobalStatic.listViewChosen ? "grid view" : "list view");
        MenuItem nMenuItem = menu.findItem(R.id.search);
        SearchView searchView = (SearchView) nMenuItem.getActionView();
        searchView.setQueryHint("Search Here!");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                if(mViewPager.getCurrentItem()==0) {
                    if(GlobalStatic.listViewChosen){
                        //filter the request in the RequestsFragment.
                        if(RequestsListViewFragment.adapter!=null)
                            RequestsListViewFragment.adapter.getFilter().filter(s);
                        //set my requests-list to be full
                        if(MyRequestsListViewFragment.adapter!=null)
                            MyRequestsListViewFragment.adapter.getFilter().filter("");
                    }
                    else{
                        //filter the request in the RequestsFragment.
                        if(RequestsGridViewFragment.adapter!=null)
                            RequestsGridViewFragment.adapter.getFilter().filter(s);
                        //set my requests-list to be full
                        if(MyRequestsGridViewFragment.adapter!=null)
                            MyRequestsGridViewFragment.adapter.getFilter().filter("");
                    }
                }else if(mViewPager.getCurrentItem()==2){
                    if(GlobalStatic.listViewChosen){
                        //filter my request in my RequestsFragment.
                        if(MyRequestsListViewFragment.adapter!=null)
                            MyRequestsListViewFragment.adapter.getFilter().filter(s);
                        //set the requests-list to be full
                        if(RequestsListViewFragment.adapter!=null)
                            RequestsListViewFragment.adapter.getFilter().filter("");
                    }
                    else{
                        //filter my request in my RequestsFragment.
                        if(MyRequestsGridViewFragment.adapter!=null)
                            MyRequestsGridViewFragment.adapter.getFilter().filter(s);
                        //set the requests-list to be full
                        if(RequestsGridViewFragment.adapter!=null)
                            RequestsGridViewFragment.adapter.getFilter().filter("");
                    }
                }
                return true;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    /**On option item selected in the Main Activity.*/
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        super.onOptionsItemSelected(item);

        if (item.getItemId()==R.id.mainLogoutButten) {
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(MainActivity.this, LoginByPhoneActivity.class);
            startActivity(intent);
            finish();
        }
        else if (item.getItemId() == R.id.mainAccountSettingButton) {
            Intent intent = new Intent(this,AccountSettingActivity.class);
            startActivity(intent);
        }
        else if(item.getItemId() == R.id.mainChangeViewButten){
            GlobalStatic.listViewChosen = !GlobalStatic.listViewChosen;
            Intent intent = new Intent(this,MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }
        else if(item.getItemId() == R.id.filter){
            // create a Dialog
            AlertDialog.Builder builder = new AlertDialog.Builder(this);

            // set the multiChoice of checkbox . Or there is singalChoiceItems.
            builder.setTitle("How do you want to filter the requests?");
            String[] filterType = {"By my surroundings.", "By my city."};

            builder.setItems(filterType, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int item) {
                    switch (item){
                        case 0:
                            filterRequestsByLocation(0);
                            break;
                        case 1:
                            filterRequestsByLocation(1);
                            break;
                        default:
                            break;
                    }
                }
            });
            builder.create().show();
        }
        return true;
    }

    /** Filter the requests in the requests fragment by the location.
     * @param item - can be only 0 for the filtering by my surroundings or 1 for filtering by my city.*/
    private void filterRequestsByLocation(int item){
        if(item==0) {
            if (GlobalStatic.listViewChosen) {
                if (RequestsListViewFragment.adapter != null) {
                    final ArrayList<Request>  requests = RequestsListViewFragment.adapter.getRequests();
                    GlobalStatic.myDatabaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            String postalCode= snapshot.child("postalCode").getValue(String.class);
                            if(postalCode.equals("-1") || postalCode==null || postalCode.equals("") ){
                                Toast.makeText(MainActivity.this, "Your location is still not determined, please update your location from your account setting and then try again to filter the requests", Toast.LENGTH_LONG).show();
                                return;
                            }
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {}
                    });
                    for (final Request request : requests) {
                        GlobalStatic.myDatabaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull final DataSnapshot snapshot1) {
                                final String postalCode= snapshot1.child("postalCode").getValue(String.class);
                                final String city= snapshot1.child("city").getValue(String.class);
                                final String country= snapshot1.child("country").getValue(String.class);
                                DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference("Users");
                                databaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        String countryRequest= snapshot.child(request.getPhoneNumber()).child("country").getValue(String.class);
                                        String cityRequest= snapshot.child(request.getPhoneNumber()).child("city").getValue(String.class);
                                        String postalCodeRequest= snapshot.child(request.getPhoneNumber()).child("postalCode").getValue(String.class);

                                        Integer postalCodeR= Integer.valueOf(postalCodeRequest);
                                        Integer myPostalCode= Integer.valueOf(postalCode);

                                        //filter in ListviewFragment sorted by postal Code (surroundings ) (Neighbors)
                                        if((postalCode.equals(postalCodeRequest) || (postalCodeR-myPostalCode<=5 && postalCodeR-myPostalCode>=-5)) && city.equals(cityRequest)){
                                            GlobalStatic.references.add(snapshot.child(request.getPhoneNumber()).getKey());
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
                            }
                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {}
                        });
                    }
                }
            } else {
                if (RequestsGridViewFragment.adapter != null) {
                    final ArrayList<Request> requests = RequestsGridViewFragment.adapter.getRequests();
                    GlobalStatic.myDatabaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            String postalCode= snapshot.child("postalCode").getValue(String.class);
                            if(postalCode.equals("-1") || postalCode==null || postalCode.equals("") ){
                                Toast.makeText(MainActivity.this, "Your location is still not determined, please update your location from your account setting and then try again to filter the requests", Toast.LENGTH_LONG).show();
                                return;
                            }
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {}
                    });
                    for (final Request request : requests) {
                        GlobalStatic.myDatabaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull final DataSnapshot snapshot1) {
                                final String postalCode= snapshot1.child("postalCode").getValue(String.class);
                                final String city= snapshot1.child("city").getValue(String.class);
                                final String country= snapshot1.child("country").getValue(String.class);
                                DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference("Users");
                                databaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        String countryRequest= snapshot.child(request.getPhoneNumber()).child("country").getValue(String.class);
                                        String cityRequest= snapshot.child(request.getPhoneNumber()).child("city").getValue(String.class);
                                        String postalCodeRequest= snapshot.child(request.getPhoneNumber()).child("postalCode").getValue(String.class);

                                        Integer postalCodeR= Integer.valueOf(postalCodeRequest);
                                        Integer myPostalCode= Integer.valueOf(postalCode);

                                        //filter in GridviewFragment sorted by postal Code (Neighbors)

                                        if((postalCode.equals(postalCodeRequest) || (postalCodeR-myPostalCode<=5 && postalCodeR-myPostalCode>=-5))  && city.equals(cityRequest)){
                                            GlobalStatic.references.add(snapshot.child(request.getPhoneNumber()).getKey());
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
                            }
                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {}
                        });
                    }
                }
            }
        }else if(item==1){
            if (GlobalStatic.listViewChosen) {
                if (RequestsListViewFragment.adapter != null) {
                    final ArrayList<Request> requests = RequestsListViewFragment.adapter.getRequests();
                    GlobalStatic.myDatabaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            String city= snapshot.child("city").getValue(String.class);
                            if(city.equals("No city loaded") || city==null || city.equals("")){
                                Toast.makeText(MainActivity.this, "Your location is still not determined, please update your location from your account setting and then try again to filter the requests", Toast.LENGTH_LONG).show();
                                return;
                            }
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {}
                    });
                    for (final Request request : requests) {
                        GlobalStatic.myDatabaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull final DataSnapshot snapshot1) {
                                final String postalCode= snapshot1.child("postalCode").getValue(String.class);
                                final String city= snapshot1.child("city").getValue(String.class);
                                final String country= snapshot1.child("country").getValue(String.class);
                                DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference("Users");
                                databaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        String countryRequest= snapshot.child(request.getPhoneNumber()).child("country").getValue(String.class);
                                        String cityRequest= snapshot.child(request.getPhoneNumber()).child("city").getValue(String.class);
                                        String postalCodeRequest= snapshot.child(request.getPhoneNumber()).child("postalCode").getValue(String.class);

                                        //filter in ListviewFragment sorted by city
                                        if(country.equals(countryRequest) && city.equals(cityRequest)){
                                            GlobalStatic.references.add(snapshot.child(request.getPhoneNumber()).getKey());
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
                            }
                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {}
                        });
                    }
                }
            } else {
                if (RequestsGridViewFragment.adapter != null) {
                    final ArrayList<Request> requests = RequestsGridViewFragment.adapter.getRequests();
                    GlobalStatic.myDatabaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            String city= snapshot.child("city").getValue(String.class);
                            if(city.equals("No city loaded") || city==null || city.equals("")){
                                Toast.makeText(MainActivity.this, "Your location is still not determined, please update your location from your account setting and then try again to filter the requests", Toast.LENGTH_LONG).show();
                                return;
                            }
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {}
                    });
                    for (final Request request : requests) {
                        GlobalStatic.myDatabaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull final DataSnapshot snapshot1) {
                                final String postalCode= snapshot1.child("postalCode").getValue(String.class);
                                final String city= snapshot1.child("city").getValue(String.class);
                                final String country= snapshot1.child("country").getValue(String.class);
                                DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference("Users");
                                databaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        String countryRequest= snapshot.child(request.getPhoneNumber()).child("country").getValue(String.class);
                                        String cityRequest= snapshot.child(request.getPhoneNumber()).child("city").getValue(String.class);
                                        String postalCodeRequest= snapshot.child(request.getPhoneNumber()).child("postalCode").getValue(String.class);


                                        //filter in GridviewFragment sorted by city
                                        if(country.equals(countryRequest) && city.equals(cityRequest)){
                                            GlobalStatic.references.add(snapshot.child(request.getPhoneNumber()).getKey());
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
                            }
                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {}
                        });
                    }
                }
            }
        }
        startActivity(new Intent(this, FilteredRequestsActivity.class));
    }


    /** start AddRequestActivity.
     * @param  view the view.*/
    @RequiresApi(api = Build.VERSION_CODES.M)
    public void addRequest(View view) {
        //change the color of the add button
        ImageButton addButton = findViewById(R.id.add_Button);
        addButton.setAlpha(150);
        Intent intent = new Intent(this, AddRequestActivity.class);
        startActivity(intent);

    }

}

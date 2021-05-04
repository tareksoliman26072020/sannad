package com.Sannad.SannadApp.Fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageButton;

import com.Sannad.SannadApp.CustomRequestGridViewAdapter;
import com.Sannad.SannadApp.Model.Request;
import com.Sannad.SannadApp.Model.User;
import com.Sannad.SannadApp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;


@NoArgsConstructor(access = AccessLevel.PUBLIC)
public class RequestsGridViewFragment extends Fragment {

    /** Gridview */
    private GridView gridView;

    /**Database Reference.*/
    private DatabaseReference mDatabaseReference;

    /** Firebase Authentication.*/
    private FirebaseAuth firebaseAuth;

    /**Adapter.*/
    public static CustomRequestGridViewAdapter adapter;

    /** Requests.*/
    ArrayList<Request> requests;
    final ArrayList<User> users = new ArrayList<>();

    /** On create.*/
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        firebaseAuth=FirebaseAuth.getInstance();
    }

    /** On start.*/
    @Override
    public void onStart() {
        super.onStart();
        //set the add-button to the orginal Color
        ImageButton addButton = getActivity().findViewById(R.id.add_Button);
        addButton.setAlpha(255);
    }

    /** On Create view.*/
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_grid_view_requests, container, false);
    }

    /** On activity created, here the data are fetched from Firebase database to RequestsGridViewFragment.*/
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        requests = new ArrayList<>();

        mDatabaseReference = FirebaseDatabase.getInstance().getReference("Users");

        mDatabaseReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                final String phone= dataSnapshot.child("phone").getValue(String.class);
                final String username = dataSnapshot.child("username").getValue(String.class);

                DatabaseReference requestsRef = dataSnapshot.child("Requests").getRef();
                requestsRef.addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                        String title = snapshot.child("title").getValue(String.class);
                        String description = snapshot.child("text").getValue(String.class);
                        String url = snapshot.child("imageUrl").getValue(String.class);
                        String time = snapshot.child("time").getValue(String.class);
                        String phoneNumber=snapshot.child("phoneNumber").getValue(String.class);
                        boolean toBeAdded = true;
                        for(Request re : requests){
                            String inTi1 = re.getTitle();
                            String inTe = re.getText();
                            String inTi2 = re.getTime();
                            String inUrl = re.getImageUrl();
                            String inPho = re.getPhoneNumber();
                            if(inTi1.equals(title) && inTe.equals(description) && inTi2.equals(time) && inUrl.equals(url) && inPho.equals(phoneNumber)){
                                toBeAdded = false;
                                return;
                            }
                        }
                        if(toBeAdded){
                            requests.add(0,new Request(title, description,time,url,phoneNumber));
                            users.add(0,new User(username, phone));
                        }
                        try {
                            gridView = (GridView) getActivity().findViewById(R.id.requestsgridview);
                            adapter = new CustomRequestGridViewAdapter(getActivity(), requests, users);
                            gridView.setAdapter(adapter);
                            adapter.notifyDataSetChanged();
                            
                        }catch (NullPointerException e){
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                    }

                    @Override
                    public void onChildRemoved(@NonNull DataSnapshot snapshot) {

                    }

                    @Override
                    public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
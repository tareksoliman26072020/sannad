package com.Sannad.SannadApp.Fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;

import com.Sannad.SannadApp.Activity.MainActivity;
import com.Sannad.SannadApp.Activity.ShowingMyRequestActivity;
import com.Sannad.SannadApp.Activity.ShowingRequestActivity;
import com.Sannad.SannadApp.CustomRequestListViewAdapter;
import com.Sannad.SannadApp.Model.GlobalStatic;
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

public class RequestsListViewFragment extends Fragment {

    /** Listview */
    private ListView listView;

    /** Database Ref. */
    private DatabaseReference mDatabaseReference;

    /** Firebase Authentication. */
    FirebaseAuth firebaseAuth;

    /** Adapter */
    public static CustomRequestListViewAdapter adapter;

    /** Requests */
    ArrayList<Request> requests;

    /** On create */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        firebaseAuth=FirebaseAuth.getInstance();
    }

    /** on Start.*/
    @Override
    public void onStart() {
        super.onStart();
        //set the add-button to the orginal Color
        ImageButton addButton = getActivity().findViewById(R.id.add_Button);
        addButton.setAlpha(255);
    }

    /** on create view */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_requests_list_view, container, false);
    }

    /** On activity created, here the data are fetched from Firebase database to RequestsListViewFragment.*/
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        requests = new ArrayList<>();
        final ArrayList<User> users = new ArrayList<>();

        mDatabaseReference = FirebaseDatabase.getInstance().getReference("Users");
        mDatabaseReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String previousChildName) {
                final String phone= dataSnapshot.child("phone").getValue(String.class);
                final String username = dataSnapshot.child("username").getValue(String.class);

                DatabaseReference requestsRef = dataSnapshot.child("Requests").getRef();
                requestsRef.addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull final DataSnapshot snapshot, @Nullable String previousChildName) {
                        String title = snapshot.child("title").getValue(String.class);
                        String description = snapshot.child("text").getValue(String.class);
                        String url = snapshot.child("imageUrl").getValue(String.class);
                        String time = snapshot.child("time").getValue(String.class);
                        String phoneNumber = snapshot.child("phoneNumber").getValue(String.class);
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
                            users.add(0,new User(username,phone));
                        }

                        try {
                            listView = (ListView) getActivity().findViewById(R.id.requestslistview);
                            listView.setDivider(null);
                            adapter = new CustomRequestListViewAdapter(getActivity(), requests, users);
                            listView.setAdapter(adapter);
                            adapter.notifyDataSetChanged();
                            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                    Request request = (Request) adapter.getItem(position);
                                    Intent intent;
                                    //if this is a personal request:
                                    if(GlobalStatic.myPhoneNumber.equals(users.get(position).getMobileNumber()))
                                        intent = new Intent(getContext(), ShowingMyRequestActivity.class);
                                    else
                                        intent = new Intent(getContext(), ShowingRequestActivity.class);
                                    intent.putExtra("phonenumber",users.get(position).getMobileNumber());
                                    intent.putExtra("username", users.get(position).getUsername());
                                    intent.putExtra("time",((Request)adapter.getItem(position)).getTime());
                                    intent.putExtra("title", (request.getTitle()));
                                    intent.putExtra("text", request.getText());
                                    intent.putExtra("image", request.getImageUrl());
                                    intent.putExtra("key",snapshot.getKey());

                                    getContext().startActivity(intent);
                                    ((Activity)getContext()).finish();
                                }
                            });
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
}
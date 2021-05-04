package com.Sannad.SannadApp.Fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.Sannad.SannadApp.Activity.MainActivity;
import com.Sannad.SannadApp.Activity.ShowingMyRequestActivity;
import com.Sannad.SannadApp.CustomMyRequestListViewAdapter;
import com.Sannad.SannadApp.Model.GlobalStatic;
import com.Sannad.SannadApp.Model.Request;
import com.Sannad.SannadApp.Model.User;
import com.Sannad.SannadApp.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class MyRequestsListViewFragment extends Fragment {

    /** Listview.*/
    private ListView listView;

    /** Database reference.*/
    private DatabaseReference mDatabaseReference;

    /** Firebase Authentication.*/
    private FirebaseAuth firebaseAuth;

    /** Adapter.*/
    public static CustomMyRequestListViewAdapter adapter;

    /**on create*/
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.firebaseAuth = FirebaseAuth.getInstance();
    }

    /** On create view.*/
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_my_requests_list_view, container, false);
    }

    /** On activity created, here the data are fetched from Firebase database to MyRequestsListViewFragment.*/
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // this arraylist should be not as attribute (global) to avoid double of reloading the arraylist.
        final ArrayList<Request> requests = new ArrayList<>();
        final ArrayList<User> users = new ArrayList<>();

        mDatabaseReference = FirebaseDatabase.getInstance().getReference("Users");

        mDatabaseReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String previousChildName) {
                final String currentPhoneNumber =  firebaseAuth.getCurrentUser().getPhoneNumber();
                String phone = dataSnapshot.child("phone").getValue(String.class);
                final String username = dataSnapshot.child("username").getValue(String.class);

                if(!currentPhoneNumber.equals(phone)) {
                    return;
                }
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
                            users.add(0,new User(username));
                        }

                        try {
                            listView = (ListView) getActivity().findViewById(R.id.personalrequestslistview);
                            adapter = new CustomMyRequestListViewAdapter(getActivity(), requests, users);
                            listView.setAdapter(adapter);

                            //When request on personal requests tab is clicked
                            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                    Intent intent = new Intent(getContext(), ShowingMyRequestActivity.class);
                                    intent.putExtra("username", users.get(position).getUsername());
                                    intent.putExtra("time",((Request)adapter.getItem(position)).getTime());
                                    intent.putExtra("title", ((Request) adapter.getItem(position)).getTitle());
                                    intent.putExtra("text", ((Request) adapter.getItem(position)).getText());
                                    intent.putExtra("image", ((Request) adapter.getItem(position)).getImageUrl());
                                    intent.putExtra("key",snapshot.getKey());

                                    getContext().startActivity(intent);
                                    ((Activity)getContext()).finish();
                                }
                            });
                            //longclick listener for request
                            listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                                @Override
                                public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                                    //contents of listview in AlertDialog
                                    final String[] listItem = new String[]{"delete request"};

                                    // get long_click_request.xml view
                                    final LayoutInflater li = LayoutInflater.from(getContext());
                                    View promptsView = li.inflate(R.layout.pop_up_long_click_request, null);
                                    ListView listView = (ListView) promptsView.findViewById(R.id.listviewlongclickrequest);
                                    final ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(),R.layout.long_click_request_item, R.id.item, listItem);
                                    listView.setAdapter(adapter);

                                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext());

                                    // set long_click_request.xml to alertdialog builder
                                    alertDialogBuilder.setView(promptsView);

                                    // create alert dialog
                                    final AlertDialog alertDialog = alertDialogBuilder.create();

                                    // show it
                                    alertDialog.show();

                                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                        @Override
                                        public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                                            alertDialog.cancel();
                                            DatabaseReference mDatabaseReference = FirebaseDatabase.getInstance().getReference("Users");
                                            final String currentPhoneNumber =  FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber();
                                            mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                    for (DataSnapshot ss : snapshot.getChildren()) {
                                                        String phone= ss.child("phone").getValue(String.class);
                                                        if(!currentPhoneNumber.equals(phone))
                                                            continue;
                                                        DatabaseReference requestsRef = ss.child("Requests").getRef();
                                                        requestsRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                                            @Override
                                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                                for(DataSnapshot ss : snapshot.getChildren()){
                                                                    String title = ss.child("title").getValue(String.class);
                                                                    String text = ss.child("text").getValue(String.class);
                                                                    String url = ss.child("imageUrl").getValue(String.class);
                                                                    if(title.equals(requests.get(position).getTitle()) &&
                                                                            text.equals(requests.get(position).getText())){
                                                                        ss.getRef().removeValue();
                                                                        if(!url.equals(GlobalStatic.noImage)){
                                                                            StorageReference photoRef = FirebaseStorage.getInstance().getReferenceFromUrl(url);
                                                                            photoRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                @Override
                                                                                public void onSuccess(Void aVoid) {
                                                                                    // File deleted successfully
                                                                                }
                                                                            }).addOnFailureListener(new OnFailureListener() {
                                                                                @Override
                                                                                public void onFailure(@NonNull Exception exception) {
                                                                                    // Uh-oh, an error occurred!
                                                                                }
                                                                            });
                                                                        }

                                                                        //re-initiate ViewPager
                                                                        ViewPager viewPage = getActivity().findViewById(R.id.myViewPager);
                                                                        viewPage.setAdapter(MainActivity.getPagerAdapter());
                                                                        //switch to MY REQUESTS page
                                                                        MainActivity.viewPagerSetCurrentItem(2);
                                                                    }
                                                                }
                                                            }

                                                            @Override
                                                            public void onCancelled(@NonNull DatabaseError error) {

                                                            }
                                                        });
                                                    }
                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError error) {

                                                }
                                            });
                                            alertDialog.cancel();
                                        }
                                    });
                                    return true;
                                }
                            });
                        } catch(NullPointerException e){
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
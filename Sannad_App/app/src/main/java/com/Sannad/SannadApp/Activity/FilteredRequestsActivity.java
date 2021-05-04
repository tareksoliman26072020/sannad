package com.Sannad.SannadApp.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Filter;
import android.widget.ListView;
import android.widget.TextView;

import com.Sannad.SannadApp.CustomFilteredRequestsAdapter;
import com.Sannad.SannadApp.Model.GlobalStatic;
import com.Sannad.SannadApp.Model.Request;
import com.Sannad.SannadApp.Model.User;
import com.Sannad.SannadApp.R;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class FilteredRequestsActivity extends AppCompatActivity {

    private DatabaseReference mDatabaseReference = FirebaseDatabase.getInstance().getReference("Users");
    ListView listview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filtered_requests);

        ArrayList<String> temp = GlobalStatic.references;

        final ArrayList<Request> requests = new ArrayList<>();
        final ArrayList<User> users = new ArrayList<>();
        final CustomFilteredRequestsAdapter adapter = new CustomFilteredRequestsAdapter(requests,users,getApplicationContext());


        for(int i = 0; i<temp.size(); i++){
            final DatabaseReference userRef = mDatabaseReference.child(temp.get(i));
            //Log.d("userRef" , userRef.toString());
            final DatabaseReference requestRef = userRef.child("Requests");
            //Log.d("requestRef",requestRef.toString());

            userRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    final String email = snapshot.child("email").getValue(String.class);
                    final String phone = snapshot.child("phone").getValue(String.class);
                    final String userImageUrl = snapshot.child("requestImageUrl").getValue(String.class);
                    final String username = snapshot.child("username").getValue(String.class);

                    requestRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for(DataSnapshot ss : snapshot.getChildren()){
                                Log.d("child",ss.getKey());
                                final String requestImageUrl = ss.child("imageUrl").getValue(String.class);
                                final String text = ss.child("text").getValue(String.class);
                                final String time = ss.child("time").getValue(String.class);
                                final String title = ss.child("title").getValue(String.class);

                                boolean canBeAdded = true;
                                for(Request re : requests){
                                    String im = re.getImageUrl();
                                    String ti1 = re.getTitle();
                                    String ti2 = re.getTime();
                                    String te = re.getText();
                                    String ph = re.getPhoneNumber();
                                    if(im.equals(requestImageUrl) && ti1.equals(title) && ti2.equals(time) && te.equals(text) && ph.equals(phone))
                                        canBeAdded = false;
                                }
                                if(canBeAdded){
                                    requests.add(0,new Request(title, text,time,requestImageUrl,phone));
                                    users.add(0,new User(username,phone));
                                } canBeAdded = true;

                                listview = (ListView) findViewById(R.id.filtered_list_view);
                                listview.setDivider(null);
                                listview.setAdapter(adapter);

                                listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                        Request request = (Request) adapter.getItem(position);
                                        Intent intent;
                                        //if this is a personal request:
                                        if(GlobalStatic.myPhoneNumber.equals(users.get(position).getMobileNumber()))
                                            intent = new Intent(FilteredRequestsActivity.this, ShowingMyRequestActivity.class);
                                        else
                                            intent = new Intent(getApplicationContext(), ShowingRequestActivity.class);
                                        intent.putExtra("phonenumber",users.get(position).getMobileNumber());
                                        intent.putExtra("username", users.get(position).getUsername());
                                        intent.putExtra("time",((Request)adapter.getItem(position)).getTime());
                                        intent.putExtra("title", (request.getTitle()));
                                        intent.putExtra("text", request.getText());
                                        intent.putExtra("image", request.getImageUrl());

                                        getApplicationContext().startActivity(intent);
                                    }
                                });
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }

    @Override
    public void onBackPressed() {
        GlobalStatic.references.clear();
        super.onBackPressed();
    }
}
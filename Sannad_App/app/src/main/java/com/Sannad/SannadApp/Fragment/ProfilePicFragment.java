package com.Sannad.SannadApp.Fragment;

import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.Sannad.SannadApp.Model.GlobalStatic;
import com.Sannad.SannadApp.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class ProfilePicFragment extends Fragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        GlobalStatic.myDatabaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ImageView imageView = getActivity().findViewById(R.id.shown_profile_pic);
                String imageUri = snapshot.child("accountImageUrl").getValue(String.class);
                Picasso.get().load(Uri.parse(imageUri)).fit().into(imageView);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile_pic, container, false);
    }

    public static Fragment newInstance()
    {
        ProfilePicFragment myFragment = new ProfilePicFragment();
        return myFragment;
    }
}
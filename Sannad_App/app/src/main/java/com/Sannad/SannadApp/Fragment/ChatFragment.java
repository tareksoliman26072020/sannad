package com.Sannad.SannadApp.Fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.Sannad.SannadApp.Activity.MainActivity;
import com.Sannad.SannadApp.Activity.MainChatActivity;
import com.Sannad.SannadApp.Model.FirebaseChatroom;
import com.Sannad.SannadApp.Model.GlobalStatic;
import com.Sannad.SannadApp.R;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;


/**
 * A simple {@link Fragment} subclass.
 */
@NoArgsConstructor(access = AccessLevel.PUBLIC)
public class ChatFragment extends Fragment {

    /** Database reference */
    private DatabaseReference mDatabaseReference;

    /** Recycler view adapter */
    private ChatroomRecyclerView chatroomRecyclerView;

    /** Recycler view */
    private RecyclerView recyclerView;

    /** Firebase ChatRoom list */
    private List<FirebaseChatroom> chatrooms = new ArrayList<>();

    /** On create view */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_chat, container, false);
    }

    /** On activity created */
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        recyclerView = Objects.requireNonNull(getView()).findViewById(R.id.chatroomRecycler);
        chatroomRecyclerView = new ChatroomRecyclerView(chatrooms);
        recyclerView.setAdapter(chatroomRecyclerView);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        mDatabaseReference = FirebaseDatabase.getInstance().getReference("Users").child(GlobalStatic.myPhoneNumber).child("Chatrooms");
        mDatabaseReference.addChildEventListener(new ChildEventListener() {
            @SneakyThrows
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                String phoneNumber = dataSnapshot.getKey();
                String imageUrl = dataSnapshot.child("OtherUserImageUrl").getValue(String.class);
                String lastMessage = dataSnapshot.child("LastMessage").getValue(String.class);
                if (imageUrl == null || lastMessage == null){
                    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users");
                    databaseReference.child(GlobalStatic.myPhoneNumber).child("Chatrooms").child(phoneNumber).child("LastMessage").setValue("hello");
                    databaseReference.child(phoneNumber).child("Chatrooms").child(GlobalStatic.myPhoneNumber).child("LastMessage").setValue("hello");
                    databaseReference.child(GlobalStatic.myPhoneNumber).child("Chatrooms").child(phoneNumber).child("OtherUserImageUrl")
                            .setValue("https://firebasestorage.googleapis.com/v0/b/sannadapp.appspot.com/o/no_personal_image%2Faccount_no_personal_image.png?alt=media&token=bddd98f3-a943-4074-bf64-a18ebe127035");
                    databaseReference.child(phoneNumber).child("Chatrooms").child(GlobalStatic.myPhoneNumber).child("OtherUserImageUrl")
                            .setValue("https://firebasestorage.googleapis.com/v0/b/sannadapp.appspot.com/o/no_personal_image%2Faccount_no_personal_image.png?alt=media&token=bddd98f3-a943-4074-bf64-a18ebe127035");
                imageUrl = "https://firebasestorage.googleapis.com/v0/b/sannadapp.appspot.com/o/no_personal_image%2Faccount_no_personal_image.png?alt=media&token=bddd98f3-a943-4074-bf64-a18ebe127035";
                lastMessage = "hello";
                }
                chatroomRecyclerView.update(new FirebaseChatroom(phoneNumber,lastMessage,imageUrl));
                recyclerView.setAdapter(chatroomRecyclerView);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                List<FirebaseChatroom> chatrooms = chatroomRecyclerView.getChatrooms();
                String phoneNumber = dataSnapshot.getKey();
                String imageUrl = dataSnapshot.child("OtherUserImageUrl").getValue(String.class);
                String lastMessage = dataSnapshot.child("LastMessage").getValue(String.class);
                for (FirebaseChatroom c : chatrooms){
                    if (c.getOtherUserPhoneNumber().equals(phoneNumber)){
                        c.setLastMessage(lastMessage);
                        c.setImageUrl(imageUrl);
                        break;
                    }
                }
                chatroomRecyclerView.updateUsingList(chatrooms);
                recyclerView.setAdapter(chatroomRecyclerView);
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

    /** ChatRoom recycler view */
    private class ChatroomRecyclerView extends RecyclerView.Adapter<ChatroomRecyclerView.ViewHolder> {

        /** ChatRooms */
        @Getter
        private List<FirebaseChatroom> chatrooms;

        /** Constructor */
        public ChatroomRecyclerView(List<FirebaseChatroom> chatrooms){
            this.chatrooms = new ArrayList<>(new HashSet<>(chatrooms));
        }

        /** On create */
        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chatroom, parent, false);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String number = chatrooms.get((Integer) view.getTag()).getOtherUserPhoneNumber();
                    Intent intent = new Intent(view.getContext(), MainChatActivity.class);
                    intent.putExtra("otherUser",number);
                    startActivity(intent);
                }
            });
            return new ViewHolder(view);
        }

        /** On bind */
        @SuppressLint("SetTextI18n")
        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            holder.name.setText(chatrooms.get(position).getOtherUserPhoneNumber());
            holder.lastMessage.setText(chatrooms.get(position).getLastMessage());
            Picasso.get().load(chatrooms.get(position).getImageUrl()).into(holder.profilePicture);
            holder.itemView.setTag(position);
        }

        /** Update the recycler view */
        public void update(FirebaseChatroom chatroom){
            this.chatrooms.add(chatroom);
            this.chatrooms = new ArrayList<>(new HashSet<>(chatrooms));
            notifyDataSetChanged();
        }

        /** Return size of chatRoom list*/
        @Override
        public int getItemCount() {
            return chatrooms.size();
        }

        /** Update using new chatroom list
         * @param chatrooms - the chatroom list */
        public void updateUsingList(List<FirebaseChatroom> chatrooms){
            this.chatrooms = chatrooms;
            this.chatrooms = new ArrayList<>(new HashSet<>(this.chatrooms));
            notifyDataSetChanged();
        }

        /** ViewHolder */
        class ViewHolder extends RecyclerView.ViewHolder {

            /** Contact name */
            private TextView name;

            /** Contact profile picture */
            private ImageView profilePicture;

            /** Last message */
            private TextView lastMessage;

            /** Constructor */
            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                name = itemView.findViewById(R.id.chatroomUsername);
                profilePicture = itemView.findViewById(R.id.chatroomImageView);
                lastMessage = itemView.findViewById(R.id.chatroomLastMessage);
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if(item.getItemId()==R.id.showHome){
            MainActivity.viewPagerSetCurrentItem(1);
            getActivity().onBackPressed();
        }

        return super.onOptionsItemSelected(item);
    }
}

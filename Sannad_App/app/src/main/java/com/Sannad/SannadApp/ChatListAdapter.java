package com.Sannad.SannadApp;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.os.VibrationEffect;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.Sannad.SannadApp.Model.FirebaseMessage;
import com.Sannad.SannadApp.Model.GlobalStatic;
import com.Sannad.SannadApp.Model.Message;
import com.Sannad.SannadApp.Model.User;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import android.os.Vibrator;

import org.w3c.dom.Text;

import java.util.ArrayList;

/** Chat list adapter */
public class ChatListAdapter extends BaseAdapter {

    public String TAG = "ChatListAdapter";

    /** Parent activity */
    private Activity mActivity;

    /** Database reference */
    private DatabaseReference mDatabaseReference;

    /** Data fetched from firebase */
    private ArrayList <DataSnapshot> mSnapshotArrayList;

    private ChildEventListener mListener = new ChildEventListener() {
        //to detect if there is a new chat message in firebase Server we have to use a listener
        @Override
        public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            mSnapshotArrayList.add(dataSnapshot);
            notifyDataSetChanged();
            //TODO re-add vibrator
        }

        /** When a message is edited */
        @Override
        public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

        }

        /** When a message is deleted */
        @Override
        public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

        }

        @Override
        public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    };

    /** Constructor
     * @param activity - the parent activity
     * @param ref - database reference */
    public ChatListAdapter(Activity activity, DatabaseReference ref) {
        mActivity = activity;
        mDatabaseReference = ref;
        mDatabaseReference.addChildEventListener(mListener);
        mSnapshotArrayList = new ArrayList<>();
    }

    /** ViewHolder */
    static class ViewHolder {

        /** Who sent the message */
        TextView authorName;

        /** Message content */
        TextView body;

        /** Layout parameters */
        LinearLayout.LayoutParams mParams;

    }

    /** Returns the amount of messages in this chatRoom*/
    @Override
    public int getCount() {
        return mSnapshotArrayList.size();
    }

    @Override
    public FirebaseMessage getItem(int position) {
        DataSnapshot snapshot = mSnapshotArrayList.get(position);
        return snapshot.getValue(FirebaseMessage.class);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            // Create a new row from scratch
            LayoutInflater inflater = (LayoutInflater) mActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.chat_msg_row, parent, false);
            final ViewHolder holder = new ViewHolder();
            holder.authorName = (TextView) convertView.findViewById(R.id.author);
            holder.body = (TextView) convertView.findViewById(R.id.message);
            holder.mParams = (LinearLayout.LayoutParams) holder.authorName.getLayoutParams();
            convertView.setTag(holder);  //to temporarily store our view holder in the convert view . so that we can reuse it later and avoid calling findviewbyid
        }
        final FirebaseMessage message = getItem(position);
        final  ViewHolder holder = (ViewHolder) convertView.getTag();
        boolean isMe = message.getPhoneNumber().equals(GlobalStatic.myPhoneNumber);
        setChatRowAppearance(isMe,holder);
        String author = "";
        if (isMe){
            author = "You";
        }
        else {
            author = message.getPhoneNumber();
        }
        holder.authorName.setText(author);
        String msg = message.getMessageText();
        holder.body.setText(msg);

        // if convertView not equal to null then just return  the convertView
        return convertView;
    }

    //this method will do all the styling of the chat messages for us
    private void setChatRowAppearance(boolean isItMe, ViewHolder holder) {
        if (isItMe) {

            holder.mParams.gravity = Gravity.END;
            holder.authorName.setTextColor(Color.GREEN);
            holder.body.setBackgroundResource(R.drawable.bubble2);
            //Log.i(TAG, "setChatRowAppearance: it is me");

        } else {
            
            holder.mParams.gravity = Gravity.START;
            holder.authorName.setTextColor(Color.BLUE);

            holder.body.setBackgroundResource(R.drawable.bubble1);


        }

        holder.authorName.setLayoutParams(holder.mParams);
        holder.body.setLayoutParams(holder.mParams);
    }



    public void cleanup() {
        // to stop checking for new events on the database so that free up resources when we do not need them anymore
        mDatabaseReference.removeEventListener(mListener);

    }

    public void myVibrator() {

        // Get instance of Vibrator from current Context
        Vibrator v = (Vibrator) mActivity.getSystemService(Context.VIBRATOR_SERVICE);

        // Vibrate for 400 milliseconds
        v.vibrate(400);

    }

}

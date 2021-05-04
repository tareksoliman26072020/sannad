package com.Sannad.SannadApp;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.ViewPager;

import com.Sannad.SannadApp.Activity.MainActivity;
import com.Sannad.SannadApp.Activity.ShowingMyRequestActivity;
import com.Sannad.SannadApp.Model.GlobalStatic;
import com.Sannad.SannadApp.Model.Request;
import com.Sannad.SannadApp.Model.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**The adapter of MyRequestGridView.*/
public class CustomMyRequestGridViewAdapter extends BaseAdapter implements Filterable {

    /**The context (MainActivity)*/
    private Context context;

    /**Requests*/
    private ArrayList<Request> requests;

    /**Users*/
    private final ArrayList<User> users;


    private LayoutInflater inflater;

    /**Cope of Requests for filtering in search bar.*/
    private ArrayList<Request> requestsFull;

    /**The filter for seaching bar.*/
    private Filter filter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<Request> filteredRequests= new ArrayList<>();
            if(constraint==null || constraint.length()== 0 ){
                filteredRequests.addAll(requestsFull);
            }else{
                String filterPattern = constraint.toString().toLowerCase().trim();
                String[] words = filterPattern.split("\\s+");
                for (int i = 0; i < words.length; i++) {
                    words[i] = words[i].replaceAll("[^\\w]", "");

                    for (Request request : requestsFull) {
                        if (request.getTitle().toLowerCase().contains(words[i]) || request.getText().toLowerCase().contains(words[i])) {
                            if(!filteredRequests.contains(request))
                                filteredRequests.add(request);
                        }
                    }
                }
            }
            FilterResults filterResults = new FilterResults();
            filterResults.values=filteredRequests;
            return filterResults;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            requests.clear();
            requests.addAll((List)results.values);
            requests = new ArrayList<>(new HashSet<>(requests));
            notifyDataSetChanged();
        }
    };

    public CustomMyRequestGridViewAdapter(Context context, List<Request> requests, List<User> users) {
        this.context = context;
        this.requests = new ArrayList<>(new HashSet<>(requests));
        this.users = new ArrayList<>(users);
        this.requestsFull= new ArrayList<>(requests);  // copy of the requests
        this.inflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return this.requests.size();
    }

    @Override
    public Object getItem(int position) {
        return this.requests.get(position);
    }

    @Override
    public long getItemId(int position) {
        //TODO: what is the unique id for every request in the grid?
        return 0;
    }

    public View getView(final int position, View convertView, ViewGroup parent) {
        final Holder holder;

        View gridView;

        if (convertView == null) {

            convertView = this.inflater.inflate(R.layout.single_item_grid_view, null);
            holder = new Holder();

            holder.image = (ImageButton) convertView.findViewById(R.id.requestImage);
            holder.title = (TextView) convertView.findViewById(R.id.requestTitle);
            holder.text = (TextView) convertView.findViewById(R.id.requestText);

            convertView.setTag(holder);

        } else
            holder = (Holder) convertView.getTag();

        final String url = ((Request)this.getItem(position)).getImageUrl();
        Picasso.get()
                .load(url)
                .resize(400,400).into(holder.image);
        holder.title.setText(((Request)this.getItem(position)).getTitle());
        String text = ((Request)this.getItem(position)).getText();
        if(text.length()>GlobalStatic.allowedTextLength_gridview){
            text = text.substring(0,GlobalStatic.allowedTextLength_gridview-4) + "....";
        }
        holder.text.setText(text);
        String title = ((Request)this.getItem(position)).getTitle();
        if(title.length()>GlobalStatic.allowedTitleLength_gridview)
            title = title.substring(0,GlobalStatic.allowedTitleLength_gridview-4) + "....";
        holder.title.setText(title);

        //When request on personal requests tab is clicked
        holder.image.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(final View v) {
                Picasso.get().load(url).into(new Target() {
                    @Override
                    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                        Intent intent = new Intent(v.getContext(), ShowingMyRequestActivity.class);
                        intent.putExtra("username",users.get(position).getUsername());
                        intent.putExtra("time",((Request)getItem(position)).getTime());
                        intent.putExtra("title",((Request)getItem(position)).getTitle());
                        intent.putExtra("text",((Request)getItem(position)).getText());
                        intent.putExtra("image",((Request)getItem(position)).getImageUrl());

                        v.getContext().startActivity(intent);
                        ((Activity)v.getContext()).finish();
                    }

                    @Override
                    public void onBitmapFailed(Exception e, Drawable errorDrawable) {

                    }

                    @Override
                    public void onPrepareLoad(Drawable placeHolderDrawable) {

                    }
                });
            }
        });

        //longclick listener for request
        holder.image.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                //contents of listview in AlertDialog
                final String[] listItem = new String[]{"delete request"};

                // get long_click_request.xml view
                final LayoutInflater li = LayoutInflater.from(context);
                View promptsView = li.inflate(R.layout.pop_up_long_click_request, null);
                ListView listView = (ListView) promptsView.findViewById(R.id.listviewlongclickrequest);
                final ArrayAdapter<String> adapter = new ArrayAdapter<String>(context,R.layout.long_click_request_item, R.id.item, listItem);
                listView.setAdapter(adapter);

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);

                // set long_click_request.xml to alertdialog builder
                alertDialogBuilder.setView(promptsView);

                // create alert dialog
                final AlertDialog alertDialog = alertDialogBuilder.create();

                // show it
                alertDialog.show();

                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
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
                                                if(title.equals(holder.title.getText().toString()) &&
                                                        text.equals(holder.text.getText().toString())){
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
                                                    ViewPager viewPage = ((Activity)context).findViewById(R.id.myViewPager);
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
        return convertView;
    }

    @Override
    public Filter getFilter() {
        return filter;
    }

    class Holder{
        ImageButton image;
        TextView title;
        TextView text;
    }

}

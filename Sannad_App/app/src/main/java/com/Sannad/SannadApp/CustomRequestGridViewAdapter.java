package com.Sannad.SannadApp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import com.Sannad.SannadApp.Activity.ShowingMyRequestActivity;
import com.Sannad.SannadApp.Activity.ShowingRequestActivity;
import com.Sannad.SannadApp.Model.GlobalStatic;
import com.Sannad.SannadApp.Model.Request;
import com.Sannad.SannadApp.Model.User;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

/**The adapter of RequestGridView.*/
public class CustomRequestGridViewAdapter extends BaseAdapter implements Filterable {

    /**Tehe context (MainActivity).*/
    private Context context;

    /**Requests.*/
    private final ArrayList<Request> requests;

    /**Users.*/
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
            notifyDataSetChanged();
        }
    };

    public CustomRequestGridViewAdapter(Context context, List<Request> requests, List<User> users) {
        this.context = context;
        this.requests = new ArrayList<>(requests);
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
                .load(((Request)this.getItem(position)).getImageUrl())
                .resize(400,400).into(holder.image);
        holder.title.setText(((Request)this.getItem(position)).getTitle());
        String text = ((Request)this.getItem(position)).getText();
        if(text.length()> GlobalStatic.allowedTextLength_gridview)
            text = text.substring(0,GlobalStatic.allowedTextLength_gridview-4) + "....";
        holder.text.setText(text);
        String title = ((Request)this.getItem(position)).getTitle();
        if(title.length()>GlobalStatic.allowedTitleLength_gridview)
            title = title.substring(0,GlobalStatic.allowedTitleLength_gridview-4) + "....";
        holder.title.setText(title);

        //When request on home page is clicked
        holder.image.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(final View v) {
                Picasso.get().load(url).into(new Target() {
                    @Override
                    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                        Intent intent;
                        //if this is a personal request
                        if(GlobalStatic.myPhoneNumber.equals(users.get(position).getMobileNumber()))
                            intent = new Intent(v.getContext(), ShowingMyRequestActivity.class);
                        else
                            intent = new Intent(v.getContext(), ShowingRequestActivity.class);
                        intent.putExtra("username",users.get(position).getUsername());
                        intent.putExtra("phonenumber",users.get(position).getMobileNumber());
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

        return convertView;
    }

    @Override
    public Filter getFilter() {
        return filter;
    }

    public ArrayList<Request> getRequests() {
        return requests;
    }

    class Holder{
        ImageButton image;
        TextView title;
        TextView text;
    }
}
package com.Sannad.SannadApp;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.Sannad.SannadApp.Model.GlobalStatic;
import com.Sannad.SannadApp.Model.Request;
import com.Sannad.SannadApp.Model.User;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class CustomFilteredRequestsAdapter extends BaseAdapter {

    private ArrayList<Request> requests;
    private ArrayList<User> users;
    private Context context;
    private LayoutInflater inflater;

    public CustomFilteredRequestsAdapter(ArrayList<Request> requests, ArrayList<User> users, Context context){
        Log.d("Requests",requests.toString());
        this.requests = requests;
        this.users = users;
        this.context = context;
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
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final Holder holder;

        if (convertView == null) {

            convertView = this.inflater.inflate(R.layout.single_item_list_view, null);
            holder = new Holder();

            holder.image = (ImageView) convertView.findViewById(R.id.image_single_item_list_view);
            holder.date = (TextView) convertView.findViewById(R.id.date_single_item_list_view);
            holder.title = (TextView) convertView.findViewById(R.id.title_single_item_list_view);
            holder.text = (TextView) convertView.findViewById(R.id.text_single_item_list_view);

            convertView.setTag(holder);
        } else
            holder = (Holder) convertView.getTag();

        final String url = ((Request)this.getItem(position)).getImageUrl();
        Picasso.get()
                .load(((Request)this.getItem(position)).getImageUrl())
                .resize(200,200).into(holder.image);
        holder.date.setText(GlobalStatic.getDate(((Request)this.getItem(position)).getTime()));
        holder.title.setText(((Request)this.getItem(position)).getTitle());
        String text = ((Request)this.getItem(position)).getText();
        if(text.length()>GlobalStatic.allowedTextLength_listview)
            text = text.substring(0,GlobalStatic.allowedTextLength_listview-4) + "....";
        holder.text.setText(text);
        String title = ((Request)this.getItem(position)).getTitle();
        if(title.length()>GlobalStatic.allowedTitleLength_listview)
            title = title.substring(0,GlobalStatic.allowedTitleLength_listview-4) + "....";
        holder.title.setText(title);


        return convertView;
    }

    class Holder{
        ImageView image;
        TextView title;
        TextView text;
        TextView date;
    }
}

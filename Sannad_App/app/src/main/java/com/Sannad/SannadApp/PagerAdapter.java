package com.Sannad.SannadApp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.Sannad.SannadApp.Fragment.ChatFragment;
import com.Sannad.SannadApp.Fragment.MyRequestsGridViewFragment;
import com.Sannad.SannadApp.Fragment.MyRequestsListViewFragment;
import com.Sannad.SannadApp.Fragment.RequestsGridViewFragment;
import com.Sannad.SannadApp.Fragment.RequestsListViewFragment;
import com.Sannad.SannadApp.Model.GlobalStatic;

public class PagerAdapter extends FragmentPagerAdapter {
    public PagerAdapter(@NonNull FragmentManager fm) {
        super(fm);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {


        switch (position) {
            case 0 :
                if(!GlobalStatic.listViewChosen)
                    return new RequestsGridViewFragment();
                else
                    return new RequestsListViewFragment();
            case 1:
                return new ChatFragment();
            case 2:
                if(!GlobalStatic.listViewChosen)
                    return new MyRequestsGridViewFragment();
                else
                    return new MyRequestsListViewFragment();
        }

        return null;
    }

    @Override
    public int getCount() {

        // Because we have 3 tabs
        return 3;
    }


    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {

        switch (position) {
            case 0:
                return "Requests";
            case 1:
                return "Chats";
            case 2:
                return "My Requests";


            default:
                return null;
        }
    }
}

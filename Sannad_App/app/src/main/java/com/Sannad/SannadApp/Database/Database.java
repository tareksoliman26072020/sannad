package com.Sannad.SannadApp.Database;

import android.content.Context;
import android.content.SharedPreferences;

import com.Sannad.SannadApp.Model.Chatroom;
import com.Sannad.SannadApp.Model.Message;
import com.Sannad.SannadApp.Model.User;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;

/**
 * This class manages the database
 */
public class Database {

    /**
     * Database singleton
     */
    private static Database database = null;

    /**
     * Realm Instance
     */
    private Realm realm = Realm.getDefaultInstance();

    /**
     * Constructor
     */
    public static Database getInstane() {
        if (database == null) {
            database = new Database();
        }
        return database;
    }

    /**
     * Cache the current user
     *
     * @param user - the current user
     * @param context - the application context
     * @throws IllegalArgumentException on failure
     */
    public void createUserCache(User user, Context context) throws IllegalArgumentException {
        try {
            SharedPreferences preferences = context.getSharedPreferences("preferences",0);
            SharedPreferences.Editor editor = preferences.edit();
            realm.copyToRealmOrUpdate(user);
            createMessageCache(user);
            editor.putInt("ID",user.getId());
            editor.apply();
        }
        catch (Exception e){
            e.printStackTrace();
            throw new IllegalArgumentException();
        }
    }

    /**
     * Create cache of a user's messages
     *
     * @param user - the user whose data to cache
     * @throws IllegalArgumentException on failure
     */
    private void createMessageCache(User user) throws IllegalArgumentException {
        try {
            for (Chatroom c : user.getChatrooms()){
                for (Message m : c.getMessages()){
                    realm.copyToRealmOrUpdate(m);
                }
                realm.copyToRealmOrUpdate(c);
            }
        }
        catch (Exception e){
            e.printStackTrace();
            throw new IllegalArgumentException();
        }
    }

    /** Fetch the user using his ID stored locally
     * @param context - the application context
     * @return the user
     * @throws IllegalArgumentException on failure */
    public User fetchUserCache(Context context) throws IllegalArgumentException{
        try {
            SharedPreferences preferences = context.getSharedPreferences("preferences",0);
            int id = preferences.getInt("ID",0);
            if (id == 0){
                throw new Exception("ILLEGAL ID");
            }
            else{
                List<User> users = realm.where(User.class).findAll();
                for (User u : users){
                    if (u.getId()==id){
                        return u;
                    }
                }
                throw new NullPointerException();
            }
        }
        catch (Exception e){
            e.printStackTrace();
            throw new IllegalArgumentException();
        }
    }

    /**
     * Load the user's cached messages
     *
     * @param user - the user whose messages to fetch
     * @return a list of all the user's chatrooms
     * @throws IllegalArgumentException on failure
     */
    public List<Chatroom> fetchMessageCache(User user) throws IllegalArgumentException {
        try {
            List<Chatroom> chatrooms = user.getChatrooms();
            return new ArrayList<>(chatrooms);
        }
        catch (Exception e){
            e.printStackTrace();
            throw new IllegalArgumentException();
        }
    }

    /** Delete local cache
     * @param context - the application context
     * @throws IllegalArgumentException on failure */
    public void deleteCache(Context context) throws IllegalArgumentException {
        try {
            SharedPreferences sharedPreferences = context.getSharedPreferences("preferences",0);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.remove("ID");
            editor.apply();
            realm.delete(Message.class);
            realm.delete(Chatroom.class);
            realm.delete(User.class);
        }
        catch (Exception e){
            e.printStackTrace();
            throw new IllegalArgumentException();
        }
    }
}

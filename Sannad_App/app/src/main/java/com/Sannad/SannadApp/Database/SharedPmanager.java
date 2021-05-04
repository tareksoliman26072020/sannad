package com.Sannad.SannadApp.Database;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashMap;

public class SharedPmanager {

    SharedPreferences userPreferences;
    SharedPreferences.Editor mEditor;
    Context mContext;


    private static final String IS_LOGIN = "IsLoggedIn";

    public static final String KEY_USERNAME = "username";
    public static final String KEY_PASSWORD = "password";
    public static final String KEY_PHONE = "phone";
    public static final String KEY_EMAIL = "email";
    public static final String KEY_GENDER = "gender";
    public static final String KEY_DATE = "date";


    public SharedPmanager(Context _context) {

        mContext = _context;
        userPreferences = _context.getSharedPreferences("userLoginPreference", Context.MODE_PRIVATE);
        mEditor = userPreferences.edit();

    }


    public void createLoginPreference(String email, String username, String password, String gender, String date, String phone) {
        mEditor.putBoolean(IS_LOGIN, true);

        mEditor.putString(KEY_USERNAME, username);
        mEditor.putString(KEY_PASSWORD, password);
        mEditor.putString(KEY_EMAIL, email);
        mEditor.putString(KEY_GENDER, gender);
        mEditor.putString(KEY_PHONE, phone);
        mEditor.putString(KEY_DATE, date);

        mEditor.commit();

    }

    public HashMap<String, String> getUserDetailFromPreference() {

        HashMap<String, String> userData = new HashMap<String, String>();
        userData.put(KEY_USERNAME, userPreferences.getString(KEY_USERNAME, null));
        userData.put(KEY_PASSWORD, userPreferences.getString(KEY_PASSWORD, null));
        userData.put(KEY_EMAIL, userPreferences.getString(KEY_EMAIL, null));
        userData.put(KEY_GENDER, userPreferences.getString(KEY_GENDER, null));
        userData.put(KEY_PHONE, userPreferences.getString(KEY_PHONE, null));
        userData.put(KEY_DATE, userPreferences.getString(KEY_DATE, null));

        return  userData;
    }

    public boolean checkLogin() {
        if (userPreferences.getBoolean(IS_LOGIN, false)) {
            return true;

        } else {
            return false;
        }
    }

    public void logoutUserFromSession() {
        mEditor.clear();
        mEditor.commit();
    }

}

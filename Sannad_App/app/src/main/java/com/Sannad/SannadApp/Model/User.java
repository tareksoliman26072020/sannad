package com.Sannad.SannadApp.Model;

import java.util.List;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

/** This class contains the user's details */
@RequiredArgsConstructor(access = AccessLevel.PUBLIC)
//@NoArgsConstructor(access = AccessLevel.PUBLIC)
@Getter
@Setter
public class User extends RealmObject {

    /** ID */
    @PrimaryKey
    @NonNull
    private int id;

    /** Username */
    @NonNull
    private String username;

    /** Name */
    private String name;

    /** Surname */
    private String surname;

    /** Phone number */
    @NonNull
    private String mobileNumber;

    /** Email address */
    private String email;

    /** Chatrooms */
    @NonNull
    private RealmList<Chatroom> chatrooms;

    public User(final String username){
        this.id = 0;
        this.username = username;
        this.name = "";
        this.surname = "";
        this.mobileNumber = "";
        this.email = "";
    }
    public User(){
        this.id = 0;
        this.username = "";
        this.name = "";
        this.surname = "";
        this.mobileNumber = "";
        this.email = "";
    }
    public User(final String username, final String phoneNumber){
        this.id = 0;
        this.username = username;
        this.name = "";
        this.surname = "";
        this.mobileNumber = phoneNumber;
        this.email = "";
    }
}

package com.Sannad.SannadApp.Model;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

/** This class contains a message */
@RequiredArgsConstructor(access = AccessLevel.PUBLIC)
@NoArgsConstructor(access = AccessLevel.PUBLIC)
@Getter
@Setter
public class Message extends RealmObject {

    /** ID */
    @PrimaryKey
    @NonNull
    private int id;

    /** Message text */
    @NonNull
    private String message;

    /** Id of the user who sent the message */
    @NonNull
    private int userID;
}

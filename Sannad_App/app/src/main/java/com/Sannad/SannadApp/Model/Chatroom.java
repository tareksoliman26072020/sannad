package com.Sannad.SannadApp.Model;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

/** This class manages chatrooms and contains the messages for each */
@RequiredArgsConstructor(access = AccessLevel.PUBLIC)
@NoArgsConstructor(access = AccessLevel.PUBLIC)
@AllArgsConstructor(access = AccessLevel.PUBLIC)
@Getter
@Setter
public class Chatroom extends RealmObject {

    /** ID */
    @PrimaryKey
    private int id;

    /** Name of the other user */
    @NonNull
    private String otherUser;

    /** The other user's ID, used for communication */
    @NonNull
    private int otherUserID;

    /** The other user's profile picture */
    @NonNull
    private byte[] profilePic;

    /** List of messages */
    @NonNull
    private RealmList<Message> messages;
}

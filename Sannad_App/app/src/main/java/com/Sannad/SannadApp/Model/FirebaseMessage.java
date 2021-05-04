package com.Sannad.SannadApp.Model;

import java.util.UUID;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

/** Used to store messages in firebase */
@RequiredArgsConstructor(access = AccessLevel.PUBLIC)
@NoArgsConstructor(access = AccessLevel.PUBLIC)
@Getter
@Setter
public class FirebaseMessage {

    /** ID */
    private long id = UUID.randomUUID().getMostSignificantBits() & Long.MAX_VALUE;

    /** Phone number of user that sent the message */
    @NonNull
    private String phoneNumber;

    /** Message text */
    @NonNull
    private String messageText;

    /** Time of sending the message */
    @NonNull
    private String time;
}

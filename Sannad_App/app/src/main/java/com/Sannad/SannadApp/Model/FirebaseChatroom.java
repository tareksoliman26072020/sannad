package com.Sannad.SannadApp.Model;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

/** Used when fetching the chatrooms */

/** Used to store chatrooms in firebase */
@NoArgsConstructor(access = AccessLevel.PUBLIC)
@RequiredArgsConstructor(access = AccessLevel.PUBLIC)
@Getter
@Setter
public class FirebaseChatroom {

    /** Other user phone number */
    @NonNull
    private String otherUserPhoneNumber;

    /** Last message */
    @NonNull
    private String lastMessage;

    /** Other user image url */
    @NonNull
    private String imageUrl;
}

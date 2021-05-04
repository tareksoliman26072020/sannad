package com.Sannad.SannadApp.Model;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@RequiredArgsConstructor(access = AccessLevel.PUBLIC)
@NoArgsConstructor(access = AccessLevel.PUBLIC)
@Getter
@Setter
public class Request {
    @NonNull
    private String title;
    @NonNull
    private String text;
    @NonNull
    private String time;
    @NonNull
    private String imageUrl;
    @NonNull
    private String phoneNumber;
}

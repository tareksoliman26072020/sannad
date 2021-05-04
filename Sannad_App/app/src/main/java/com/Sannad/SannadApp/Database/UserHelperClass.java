package com.Sannad.SannadApp.Database;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserHelperClass {
    private String email, username, password, gender, date, phone, adresse, requestImageUrl, accountImageUrl, postalCode, city, country;

    /** required empty constructor.*/
    public UserHelperClass() {}

    public UserHelperClass(String email, String username, String password, String gender, String date, String phone, String adresse, String requestImageUrl, String accountImageUrl, String postalCode, String city, String country) {
        this.email = email;
        this.username = username;
        this.password = password;
        this.gender = gender;
        this.date = date;
        this.phone = phone;
        this.adresse = adresse;
        this.requestImageUrl = requestImageUrl;
        this.accountImageUrl = accountImageUrl;
        this.postalCode = postalCode;
        this.city = city;
        this.country = country;
    }
}

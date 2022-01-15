package com.s2dioapps.divinepolytechniccollege.login;

import android.net.Uri;

import java.sql.Blob;

public class ProfileModel {

    public ProfileModel(String photo, String name, String email) {
        this.photo = photo;
        this.name = name;
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    String name;
    String email;
    String photo;


}

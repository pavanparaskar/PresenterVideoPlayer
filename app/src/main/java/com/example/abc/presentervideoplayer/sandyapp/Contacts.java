package com.example.abc.presentervideoplayer.sandyapp;

/**
 * Created by ABC on 16-11-2018.
 */

public class Contacts {

    public String name, status, image;

    public Contacts()

    {

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }


    public Contacts(String name, String status, String image) {
        this.name = name;
        this.status = status;
        this.image = image;
    }


}
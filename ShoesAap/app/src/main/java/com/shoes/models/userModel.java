package com.shoes.models;
public class userModel {
    String name;
    String email;
    String password;

    String address;
    String number;
    String profileImg;

    public userModel(){
    }



public userModel(String name, String email, String address, String number, String profileImg){


    this.name = name;
    this.email = email;
    this.address=address;
    this.number=number;
    this.profileImg=profileImg;



}

    public userModel(String userName, String userEmail, String userPassword) {

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

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }


    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getProfileImg() {
        return profileImg;
    }

    public void setProfileImg(String profileImg) {
        this.profileImg = profileImg;
    }

}

package com.example.resturant.models;

public class ModelShop {
    private String email,uid,name,phone,timestamp,address,accountType,ShopOpen,ProfileImg;

    public ModelShop() {
    }

    public ModelShop(String email, String uid, String name, String phone, String timestamp, String address, String accountType, String shopOpen, String profileImg) {
        this.email = email;
        this.uid = uid;
        this.name = name;
        this.phone = phone;
        this.timestamp = timestamp;
        this.address = address;
        this.accountType = accountType;
        ShopOpen = shopOpen;
        ProfileImg = profileImg;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getAccountType() {
        return accountType;
    }

    public void setAccountType(String accountType) {
        this.accountType = accountType;
    }

    public String getShopOpen() {
        return ShopOpen;
    }

    public void setShopOpen(String shopOpen) {
        ShopOpen = shopOpen;
    }

    public String getProfileImg() {
        return ProfileImg;
    }

    public void setProfileImg(String profileImg) {
        ProfileImg = profileImg;
    }
}

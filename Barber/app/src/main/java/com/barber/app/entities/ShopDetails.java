package com.barber.app.entities;

import java.util.List;

public class ShopDetails {

    private String shopName;
    private String city;
    private int phoneNum;
    private int phoneNumAlt;
    private OpeningHours[] openingHours;
    private ShopImages[] galleryLinks;
    private String providerServices;
    private SocialMediaInfo socialMediaInfo;
    private Address shopAddress;
    private List<ShopWorkers> shopWorkers;

    public ShopDetails() {
    }

    public ShopDetails(String shopName, String city, Address shopAddress) {
        setShopName(shopName);
        setCity(city);
        setShopAddress(shopAddress);
    }

    public int getPhoneNum() {
        return phoneNum;
    }

    public void setPhoneNum(int phoneNum) {
        this.phoneNum = phoneNum;
    }

    public int getPhoneNumAlt() {
        return phoneNumAlt;
    }

    public void setPhoneNumAlt(int phoneNumAlt) {
        this.phoneNumAlt = phoneNumAlt;
    }


    public List<ShopWorkers> getShopWorkers() {
        return shopWorkers;
    }

    public void setShopWorkers(List<ShopWorkers> shopWorkers) {
        this.shopWorkers = shopWorkers;
    }

    public String getShopName() {
        return shopName;
    }

    public void setShopName(String shopName) {
        this.shopName = shopName;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public OpeningHours[] getOpeningHours() {
        return openingHours;
    }

    public void setOpeningHours(OpeningHours[] openingHours) {
        this.openingHours = openingHours;
    }

    public ShopImages[]  getGalleryLinks() {
        return galleryLinks;
    }

    public void setGalleryLinks(ShopImages[]  galleryLinks) {
        this.galleryLinks = galleryLinks;
    }


    public String getProviderServices() {
        return providerServices;
    }

    public void setProviderServices(String providerServices) {
        this.providerServices = providerServices;
    }


    public Address getShopAddress() {
        return shopAddress;
    }

    public void setShopAddress(Address shopAddress) {
        this.shopAddress = shopAddress;
    }


    public SocialMediaInfo getSocialMediaInfo() {
        return socialMediaInfo;
    }

    public void setSocialMediaInfo(SocialMediaInfo socialMediaInfo) {
        this.socialMediaInfo = socialMediaInfo;
    }
}


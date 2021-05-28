package com.barber.app.entities;


public class Address {

    private double shopLatitude;
    private double shopLongitude;
    private String streetName;
    private int postalCode;
    private String buildingNum;
    private String area;

    public Address() {
    }

    public Address(double shopLatitude, double shopLongitude) {
        setShopLatitude(shopLatitude);
        setShopLongitude(shopLongitude);
    }

    public double getShopLatitude() {
        return shopLatitude;
    }

    public void setShopLatitude(double shopLatitude) {
        this.shopLatitude = shopLatitude;
    }

    public double getShopLongitude() {
        return shopLongitude;
    }

    public void setShopLongitude(double shopLongitude) {
        this.shopLongitude = shopLongitude;
    }

    public String getStreetName() {
        return streetName;
    }

    public void setStreetName(String streetName) {
        this.streetName = streetName;
    }

    public int getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(int postalCode) {
        this.postalCode = postalCode;
    }

    public String getBuildingNum() {
        return buildingNum;
    }

    public void setBuildingNum(String buildingNum) {
        this.buildingNum = buildingNum;
    }


    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

}

package com.barber.app.entities;


public class Shop {
    private long shopId;
    private String statusMessage;
    private String imageLink;
    private int rankID;
    private String logoLink;
    private boolean recentlyAdded;
    private ShopDetails shopDetails;

    public Shop() {
    }

    public Shop(long shopId, String imageLink, String logoLink, boolean recentlyAdded, ShopDetails shopDetails) {
        setShopId(shopId);
        setLogoLink(logoLink);
        setImageLink(imageLink);
        setRecentlyAdded(recentlyAdded);
        setShopDetails(shopDetails);
    }

    public ShopDetails getShopDetails() {
        return shopDetails;
    }

    public void setShopDetails(ShopDetails shopDetails) {
        this.shopDetails = shopDetails;
    }

    public long getShopId() {
        return shopId;
    }

    public void setShopId(long shopId) {
        this.shopId = shopId;
    }

    public String getStatusMessage() {
        return statusMessage;
    }

    public void setStatusMessage(String statusMessage) {
        this.statusMessage = statusMessage;
    }

    public String getImageLink() {
        return imageLink;
    }

    public void setImageLink(String imageLink) {
        this.imageLink = imageLink;
    }

    public int getRankID() {
        return rankID;
    }

    public void setRankID(int rankID) {
        this.rankID = rankID;
    }

    public String getLogoLink() {
        return logoLink;
    }

    public void setLogoLink(String logoLink) {
        this.logoLink = logoLink;
    }

    public boolean isRecentlyAdded() {
        return recentlyAdded;
    }

    public void setRecentlyAdded(boolean recentlyAdded) {
        this.recentlyAdded = recentlyAdded;
    }
}

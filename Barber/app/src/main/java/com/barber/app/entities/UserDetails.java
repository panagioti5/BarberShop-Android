package com.barber.app.entities;

public class UserDetails {

    private String userEmail;
    private String userToken;
    private String userOldToken;
    private String name;
    private long phoneNumber;
    private boolean acceptsNotifications;
    private boolean acceptsPushNotifications;


    public UserDetails() {
    }

    public UserDetails(String userEmail, String userToken, String name, long phoneNumber, boolean acceptsNotifications, boolean acceptsPushNotifications) {
        setUserEmail(userEmail);
        setUserToken(userToken);
        setName(name);
        setPhoneNumber(phoneNumber);
        setAcceptsNotifications(acceptsNotifications);
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getUserToken() {
        return userToken;
    }

    public void setUserToken(String userToken) {
        this.userToken = userToken;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(long phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public boolean isAcceptsNotifications() {
        return acceptsNotifications;
    }

    public void setAcceptsNotifications(boolean acceptsNotifications) {
        this.acceptsNotifications = acceptsNotifications;
    }

    public boolean isAcceptsPushNotifications() {
        return acceptsPushNotifications;
    }

    public void setAcceptsPushNotifications(boolean acceptsPushNotifications) {
        this.acceptsPushNotifications = acceptsPushNotifications;
    }

    public String getUserOldToken() {
        return userOldToken;
    }

    public void setUserOldToken(String userOldToken) {
        this.userOldToken = userOldToken;
    }
}

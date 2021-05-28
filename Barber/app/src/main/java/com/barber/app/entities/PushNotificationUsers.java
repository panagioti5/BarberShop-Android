package com.barber.app.entities;

public class PushNotificationUsers {
    private String pushNotificationID;
    private String userEmail;
    private boolean acceptsPushNotifications;
    private boolean signedIn;

    public PushNotificationUsers(String pushNotificationID, String userEmail, boolean acceptsPushNotifications, boolean signedIn) {
        this.pushNotificationID = pushNotificationID;
        this.userEmail = userEmail;
        this.acceptsPushNotifications = acceptsPushNotifications;
        this.signedIn = signedIn;
    }

    public boolean isSignedIn() {
        return signedIn;
    }

    public void setSignedIn(boolean signedIn) {
        this.signedIn = signedIn;
    }

    public PushNotificationUsers() {
    }

    public String getPushNotificationID() {
        return pushNotificationID;
    }

    public void setPushNotificationID(String pushNotificationID) {
        this.pushNotificationID = pushNotificationID;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public boolean getAcceptsPushNotifications() {
        return acceptsPushNotifications;
    }

    public void setAcceptsPushNotifications(boolean acceptsPushNotifications) {
        this.acceptsPushNotifications = acceptsPushNotifications;
    }
}

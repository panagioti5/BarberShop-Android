package com.barber.app;

import android.content.Intent;
import android.content.SharedPreferences;

import com.barber.app.DAOImpl.firetoken.FireTokenDaoImpl;
import com.barber.app.codes.OperationCode;
import com.barber.app.constants.Constants;
import com.barber.app.dao.FireTokenDaoIntf;
import com.barber.app.utils.UtilsImpl;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;


public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private static final String TAG = "MyFirebaseMessagingService";

    public MyFirebaseMessagingService() {
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        String click_action = remoteMessage.getData().get("body");
        Intent newsPage = new Intent(this, MainShopsActivity.class);
        newsPage.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        newsPage.putExtra(Constants.EXTRA_SESSION_ID_ACTIVITY_TITLE, click_action);
        startActivity(newsPage);
    }


    /**
     * Called if InstanceID token is updated. This may occur if the security of
     * the previous token had been compromised. Note that this is called when the InstanceID token
     * is initially generated so this is where you would retrieve the token.
     */
    @Override
    public void onNewToken(String token) {
        SharedPreferences.Editor editor = getSharedPreferences(Constants.FIRE_TOKEN, MODE_PRIVATE).edit();
        editor.putString("token", token);
        editor.apply();
        sendTokenToAppServer(token);
    }

    private OperationCode sendTokenToAppServer(String tokenToRegister) {
        boolean isUserSignedIn = UtilsImpl.isUserSignedIn(this);
        try {
            FireTokenDaoIntf token = new FireTokenDaoImpl(this.getApplicationContext(), isUserSignedIn, isUserSignedIn);
            return token.register();
        } catch (Exception e) {
            return OperationCode.OperationFailed;
        }
    }
}

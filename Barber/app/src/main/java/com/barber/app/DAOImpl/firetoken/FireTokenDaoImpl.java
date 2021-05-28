package com.barber.app.DAOImpl.firetoken;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.os.StrictMode;

import com.barber.app.codes.OperationCode;
import com.barber.app.constants.Constants;
import com.barber.app.dao.FireTokenDaoIntf;
import com.barber.app.entities.PushNotificationUsers;
import com.barber.app.utils.UtilsImpl;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Properties;

import okhttp3.OkHttpClient;

import static android.content.Context.MODE_PRIVATE;

public class FireTokenDaoImpl implements FireTokenDaoIntf {

    private final OkHttpClient httpClient = new OkHttpClient();
    private final Context context;
    private final boolean full;
    private final boolean isSignedIn;

    public FireTokenDaoImpl(Context activity, boolean full, boolean isSignedIn) {
        this.context = activity;
        this.full = full;
        this.isSignedIn = isSignedIn;
    }

    @Override
    public OperationCode register() {
        try {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
            Properties properties = new Properties();

            AssetManager assetManager = context.getAssets();
            InputStream inputStream = assetManager.open("config.properties");
            properties.load(inputStream);
            URL url = new URL(properties.getProperty("url") + properties.getProperty("pushNotification"));

            PushNotificationUsers pushNotificationUsers = new PushNotificationUsers();


            SharedPreferences prefs = context.getSharedPreferences(Constants.FIRE_TOKEN, MODE_PRIVATE);
            String token = prefs.getString("token", "");


            if (!UtilsImpl.isNotNullOrEmpty(token)) {
                //NO NEED TO CALL API
                return OperationCode.OperationSuccess;
            }

            SharedPreferences notificationPrefs = context.getSharedPreferences("NOTIFICATIONS", MODE_PRIVATE);
            String userAcceptsNotifications = notificationPrefs.getString(Constants.ACCEPTS_PUSH_NOTIFICATIONS, "");

            SharedPreferences accountPrefs = context.getSharedPreferences(Constants.SHOP_PACK, MODE_PRIVATE);
            String userEmail = accountPrefs.getString(Constants.CONNECTED_ACCOUNT, "");

            String str = null;

            pushNotificationUsers.setAcceptsPushNotifications(userAcceptsNotifications.equals("AN") ? true : false);
            pushNotificationUsers.setPushNotificationID(token);
            pushNotificationUsers.setUserEmail(null != userEmail && !userEmail.isEmpty() ? userEmail : null);
            pushNotificationUsers.setSignedIn(isSignedIn);


            str = "{\n" +
                    "        \"pushNotificationID\": \"" + pushNotificationUsers.getPushNotificationID() + "\",\n" +
                    "        \"userEmail\": \"" + pushNotificationUsers.getUserEmail() + "\",\n" +
                    "        \"acceptsPushNotifications\": \"" + pushNotificationUsers.getAcceptsPushNotifications() + "\",\n" +
                    "        \"signedIn\": \"" + pushNotificationUsers.isSignedIn() + "\"\n" +
                    "}";

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod(Constants.METHOD_POST);
            conn.setRequestProperty("Content-Type", "application/json");
            byte[] outputInBytes = str.getBytes("UTF-8");
            OutputStream os = conn.getOutputStream();
            os.write(outputInBytes);
            os.close();


            if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
                throw new RuntimeException("Failed : HTTP error code : "
                        + conn.getResponseCode());
            }

            BufferedReader br = new BufferedReader(new InputStreamReader(
                    (conn.getInputStream())));

            String output;
            while ((output = br.readLine()) != null) {
                System.out.println(output);
            }

            conn.disconnect();

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("TokenNotUpdated");
        }
        return OperationCode.OperationSuccess;
    }
}



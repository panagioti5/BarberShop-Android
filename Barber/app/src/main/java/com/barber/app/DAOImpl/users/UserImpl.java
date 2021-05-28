package com.barber.app.DAOImpl.users;

import android.app.Activity;
import android.content.Context;
import android.content.res.AssetManager;

import com.barber.app.codes.OperationCode;
import com.barber.app.constants.Constants;
import com.barber.app.dao.UserDAO;
import com.barber.app.entities.UserDetails;
import com.barber.app.utils.EncryptorDecryptor;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Properties;

public class UserImpl implements UserDAO {


    @Override
    public OperationCode login(Context context, String email, String password) throws Exception {
        try {
            Properties properties = new Properties();

            AssetManager assetManager = context.getAssets();
            InputStream inputStream = assetManager.open("config.properties");
            properties.load(inputStream);

            URL url = new URL(properties.getProperty("url") + properties.getProperty("login"));

            String str = " {\n" +
                    "        \"userEmail\": \"" + email + "\",\n" +
                    "        \"userToken\": \"" + EncryptorDecryptor.encrypt(password, Constants.KEY) + "\"\n" +
                    "       \n" +
                    "    }";

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod(Constants.METHOD_POST);
            conn.setRequestProperty("Content-Type", "application/json");
            byte[] outputInBytes = str.getBytes("UTF-8");
            OutputStream os = conn.getOutputStream();
            os.write(outputInBytes);
            os.close();

            if (conn.getResponseCode() == Constants.NOT_FOUND_RESPONSE_CODE) {
                throw new Exception(Constants.INVALID_CREDENTIALS);
            }
            if (conn.getResponseCode() == Constants.LOCKED) {
                throw new Exception(Constants.LOCKED_MSG);
            }

            if (conn.getResponseCode() != Constants.SUCCESS_RESPONSE_CODE) {
                throw new Exception(Constants.SOMETHING_WENT_WRONG_NET);
            }

            conn.disconnect();
            return OperationCode.OperationSuccess;
        } catch (Exception e) {
            e.printStackTrace();
            if (e.getMessage().contains("Unable to resolve host") || e.getMessage().contains("Failed to connect")) {
                throw new Exception(Constants.SOMETHING_WENT_WRONG_NET);
            } else if (e.getMessage().contains(Constants.LOCKED_MSG)) {
                throw new Exception(Constants.LOCKED_MSG);
            } else if (e.getMessage().contains(Constants.SOMETHING_WENT_WRONG_TRY_AGAIN)) {
                throw new Exception(Constants.SOMETHING_WENT_WRONG_TRY_AGAIN);
            } else {
                throw new Exception(Constants.INVALID_CREDENTIALS);
            }
        }
    }

    @Override
    public OperationCode register(Context context, String email, String password, String name, String phoneNum, boolean isEmailNotificationsEnabled, boolean isPushNotificationsEnabled) throws Exception {
        try {
            Properties properties = new Properties();

            AssetManager assetManager = context.getAssets();
            InputStream inputStream = assetManager.open("config.properties");
            properties.load(inputStream);

            String str = "{\n" +
                    "    \"userEmail\": \"" + email + "\",\n" +
                    "    \"userToken\": \"" + EncryptorDecryptor.encrypt(password, Constants.KEY) + "\",\n" +
                    "    \"name\": \"" + name + "\",\n" +
                    "    \"phoneNumber\": " + phoneNum + ",\n" +
                    "    \"acceptsNotifications\": " + isEmailNotificationsEnabled + ",\n" +
                    "    \"acceptsPushNotifications\": " + isPushNotificationsEnabled + "\n" +
                    "}";

            URL url = new URL(properties.getProperty("url") + properties.getProperty("register"));

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod(Constants.METHOD_POST);
            conn.setRequestProperty("Content-Type", "application/json");
            byte[] outputInBytes = str.getBytes("UTF-8");
            OutputStream os = conn.getOutputStream();
            os.write(outputInBytes);
            os.close();

            if (conn.getResponseCode() == Constants.BAD_REQUEST_CODE) {
                throw new Exception(Constants.EMAIL_ALREADY_REGISTERED);
            }

            if (conn.getResponseCode() != Constants.SUCCESS_RESPONSE_CODE) {
                throw new Exception(Constants.SOMETHING_WENT_WRONG_NET);
            }
            conn.disconnect();
            return OperationCode.OperationSuccess;
        } catch (Exception e) {
            if (e.getMessage().contains("Unable to resolve host") || e.getMessage().contains("Failed to connect")) {
                throw new Exception(Constants.SOMETHING_WENT_WRONG_NET);
            } else if (e.getMessage().contains(Constants.EMAIL_ALREADY_REGISTERED)) {
                throw new Exception(Constants.EMAIL_ALREADY_REGISTERED);
            } else if (e.getMessage().contains(Constants.SOMETHING_WENT_WRONG_TRY_AGAIN)) {
                throw new Exception(Constants.SOMETHING_WENT_WRONG_TRY_AGAIN);
            } else {
                throw new Exception(Constants.SOMETHING_WENT_WRONG_NET);
            }
        }
    }

    @Override
    public OperationCode resolveLostMail(Context context, String email) throws Exception {
        try {
            Properties properties = new Properties();

            AssetManager assetManager = context.getAssets();
            InputStream inputStream = assetManager.open("config.properties");
            properties.load(inputStream);
            String str = email;

            URL url = new URL(properties.getProperty("url") + properties.getProperty("logInResolver"));

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod(Constants.METHOD_POST);
            conn.setRequestProperty("Content-Type", "application/text");
            byte[] outputInBytes = str.getBytes("UTF-8");
            OutputStream os = conn.getOutputStream();
            os.write(outputInBytes);
            os.close();

            if (conn.getResponseCode() != Constants.SUCCESS_RESPONSE_CODE) {
                throw new Exception("Mail does not exist");
            }
            conn.disconnect();
            return OperationCode.OperationSuccess;
        } catch (Exception e) {
            if (e.getMessage().contains("Unable to resolve host") || e.getMessage().contains("Failed to connect")) {
                throw new Exception(Constants.SOMETHING_WENT_WRONG_NET);
            }
            return OperationCode.OperationSuccess;
        }
    }

    @Override
    public UserDetails getUserDetails(Context context, String email, String userPass) throws Exception {
        try {
            Properties properties = new Properties();

            AssetManager assetManager = context.getAssets();
            InputStream inputStream = assetManager.open("config.properties");
            properties.load(inputStream);

            URL url = new URL(properties.getProperty("url") + properties.getProperty("userDetails"));

            UserDetails userDetails;


            String str = "{\n" +
                    "        \"userEmail\": \"" + email + "\",\n" +
                    "        \"userToken\": \"" + EncryptorDecryptor.encrypt(userPass, Constants.KEY) + "\"\n" +
                    "}";

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod(Constants.METHOD_POST);
            conn.setRequestProperty("Content-Type", "application/json");
            byte[] outputInBytes = str.getBytes("UTF-8");
            OutputStream os = conn.getOutputStream();
            os.write(outputInBytes);
            os.close();


            if (conn.getResponseCode() != Constants.SUCCESS_RESPONSE_CODE) {
                throw new Exception(Constants.SOMETHING_WENT_WRONG_NET);
            }

            InputStreamReader in = new InputStreamReader(conn.getInputStream());
            BufferedReader br = new BufferedReader(in);
            String output = Constants.EMPTY_STRING;
            StringBuilder sb = new StringBuilder();

            while ((output = br.readLine()) != null) {
                sb.append(output);
            }

            if (sb == null || sb.toString().isEmpty()) {
                throw new Exception(Constants.NOTHING_TO_SHOW);
            }
            if (conn.getResponseCode() != Constants.SUCCESS_RESPONSE_CODE) {
                throw new Exception(Constants.WRONG_RESPONSE);
            }

            userDetails = new Gson().fromJson(sb.toString(), new TypeToken<UserDetails>() {
            }.getType());
            conn.disconnect();
            return userDetails;
        } catch (Exception e) {
            if (e.getMessage().contains("Unable to resolve host") || e.getMessage().contains("Failed to connect")) {
                throw new Exception(Constants.SOMETHING_WENT_WRONG_NET);
            } else if (e.getMessage().contains(Constants.SOMETHING_WENT_WRONG_TRY_AGAIN)) {
                throw new Exception(Constants.SOMETHING_WENT_WRONG_TRY_AGAIN);
            } else {
                throw new Exception(Constants.WRONG_RESPONSE);
            }
        }
    }

    @Override
    public OperationCode sendResetMail(Context context, String email) throws Exception {
        try {
            Properties properties = new Properties();

            AssetManager assetManager = context.getAssets();
            InputStream inputStream = assetManager.open("config.properties");
            properties.load(inputStream);

            URL url = new URL(properties.getProperty("url") + properties.getProperty("forgotPassword"));

            String str = " {\n" +
                    "        \"userEmail\": \"" + email + "\"\n" +
                    "}";

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod(Constants.METHOD_POST);
            conn.setRequestProperty("Content-Type", "application/json");
            byte[] outputInBytes = str.getBytes("UTF-8");
            OutputStream os = conn.getOutputStream();
            os.write(outputInBytes);
            os.close();

            if (conn.getResponseCode() != Constants.SUCCESS_RESPONSE_CODE) {
                throw new Exception(Constants.SOMETHING_WENT_WRONG_NET);
            }
            conn.disconnect();
            return OperationCode.OperationSuccess;
        } catch (Exception e) {
            if (e.getMessage().contains("Unable to resolve host") || e.getMessage().contains("Failed to connect")) {
                throw new Exception(Constants.SOMETHING_WENT_WRONG_NET);
            } else {
                return OperationCode.OperationSuccess;
            }
        }
    }

    @Override
    public OperationCode resetPass(Activity context, String code, String userEmail, String password) throws Exception {
        try {
            Properties properties = new Properties();

            AssetManager assetManager = context.getAssets();
            InputStream inputStream = assetManager.open("config.properties");
            properties.load(inputStream);

            URL url = new URL(properties.getProperty("url") + properties.getProperty("updateToken"));

            String str = "{\n" +
                    "        \"code\": \"" + code + "\",\n" +
                    "        \"email\": \"" + userEmail + "\",\n" +
                    "        \"newToken\": \"" + EncryptorDecryptor.encrypt(password, Constants.KEY) + "\"\n" +
                    "}";

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod(Constants.METHOD_POST);
            conn.setRequestProperty("Content-Type", "application/json");
            byte[] outputInBytes = str.getBytes("UTF-8");
            OutputStream os = conn.getOutputStream();
            os.write(outputInBytes);
            os.close();

            if (conn.getResponseCode() != Constants.SUCCESS_RESPONSE_CODE) {
                throw new Exception(Constants.SOMETHING_WENT_WRONG_NET);
            }
            conn.disconnect();
            return OperationCode.OperationSuccess;
        } catch (Exception e) {
            if (e.getMessage().contains("Unable to resolve host") || e.getMessage().contains("Failed to connect")) {
                throw new Exception(Constants.SOMETHING_WENT_WRONG_NET);
            } else if (e.getMessage().contains(Constants.SOMETHING_WENT_WRONG_TRY_AGAIN)) {
                throw new Exception(Constants.SOMETHING_WENT_WRONG_TRY_AGAIN);
            } else {
                throw new Exception(Constants.INVALID_EMAIL);
            }
        }
    }

    @Override
    public OperationCode updateDetails(Activity context, UserDetails userDetails) throws Exception {
        try {
            Properties properties = new Properties();

            AssetManager assetManager = context.getAssets();
            InputStream inputStream = assetManager.open("config.properties");
            properties.load(inputStream);

            URL url = new URL(properties.getProperty("url") + properties.getProperty("userDetailsUpdate"));

            String str = "{\n" +
                    "    \"userEmail\": \"" + userDetails.getUserEmail() + "\",\n" +
                    "    \"userToken\": \"" + EncryptorDecryptor.encrypt(userDetails.getUserToken(), Constants.KEY) + "\",\n" +
                    "    \"name\": \"" + userDetails.getName() + "\",\n" +
                    "    \"phoneNumber\": \"" + userDetails.getPhoneNumber() + "\",\n" +
                    "    \"acceptsNotifications\": \"" + userDetails.isAcceptsNotifications() + "\",\n" +
                    "    \"acceptsPushNotifications\": \"" + userDetails.isAcceptsPushNotifications() + "\",\n" +
                    "    \"userOldToken\": \"" + EncryptorDecryptor.encrypt(userDetails.getUserOldToken(), Constants.KEY) + "\"\n" +
                    "}";


            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod(Constants.METHOD_POST);
            conn.setRequestProperty("Content-Type", "application/json");
            byte[] outputInBytes = str.getBytes("UTF-8");
            OutputStream os = conn.getOutputStream();
            os.write(outputInBytes);
            os.close();

            if (conn.getResponseCode() != Constants.SUCCESS_RESPONSE_CODE) {
                throw new Exception(Constants.SOMETHING_WENT_WRONG_NET);
            }
            conn.disconnect();
            return OperationCode.OperationSuccess;
        } catch (Exception e) {
            if (e.getMessage().contains("Unable to resolve host") || e.getMessage().contains("Failed to connect")) {
                throw new Exception(Constants.SOMETHING_WENT_WRONG_NET);
            } else {
                throw new Exception(Constants.SOMETHING_WENT_WRONG_TRY_AGAIN);
            }
        }
    }
}

package com.barber.app.dao;

import android.app.Activity;
import android.content.Context;

import com.barber.app.codes.OperationCode;
import com.barber.app.entities.Shop;
import com.barber.app.entities.UserDetails;

import java.util.List;

public interface UserDAO {

    public OperationCode login(Context context, String email, String password) throws Exception;

    public OperationCode register(Context context, String email, String password, String name, String phonenNum, boolean isEmailNotificationsEnabled, boolean isPushNotificationsEnabled) throws Exception;

    public OperationCode resolveLostMail(Context context, String email) throws Exception;

    public UserDetails getUserDetails(Context context, String email, String pass) throws Exception;

    public OperationCode sendResetMail(Context context, String email) throws Exception;

    public OperationCode resetPass(Activity context, String code, String userEmail, String password) throws Exception;

    public OperationCode updateDetails(Activity context, UserDetails userDetails) throws Exception;

}

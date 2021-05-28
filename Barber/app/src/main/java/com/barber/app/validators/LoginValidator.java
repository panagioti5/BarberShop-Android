package com.barber.app.validators;

import android.app.Activity;

import com.barber.app.codes.OperationCode;

public class LoginValidator extends Validator {

    private String userEmail;
    private String userPassword;
    private Activity activity;

    @Override
    public OperationCode validate() throws Exception {

        EmailValidator emailValidator = new EmailValidator();
        emailValidator.setMail(getUserEmail());
        emailValidator.validate();

        PasswordValidator passwordValidator = new PasswordValidator();
        passwordValidator.setToken(getUserPassword());
        passwordValidator.validate();


        return OperationCode.OperationSuccess;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public String getUserPassword() {
        return userPassword;
    }

    public void setLoginEmail(String email) {
        this.userEmail = email;
    }

    public void setLoginPassword(String password) {
        this.userPassword = password;
    }

    public Activity getContext() {
        return this.activity;
    }

    public void setContext(Activity loginActivity) {
        this.activity = loginActivity;
    }
}

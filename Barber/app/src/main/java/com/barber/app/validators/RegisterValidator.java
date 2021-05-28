package com.barber.app.validators;

import com.barber.app.RegisterActivity;
import com.barber.app.codes.OperationCode;

public class RegisterValidator extends Validator {


    private RegisterActivity activity;
    private String email;
    private String name;
    private String token;
    private String phone;
    private boolean agree;

    @Override
    public OperationCode validate() throws Exception {

        NameValidator nameValidator = new NameValidator();
        nameValidator.setName(getName());
        nameValidator.validate();

        EmailValidator emailValidator = new EmailValidator();
        emailValidator.setMail(getEmail());
        emailValidator.validate();

        PasswordValidator passwordValidator = new PasswordValidator();
        passwordValidator.setToken(getToken());
        passwordValidator.validate();

        PhoneValidator phoneValidator = new PhoneValidator();
        phoneValidator.setPhoneNum(getPhone());
        phoneValidator.validate();

        if (!isAgree()) {
            throw new Exception("Please accept privacy policy & terms of use");
        }

        return OperationCode.OperationSuccess;
    }

    public void setContext(RegisterActivity registerActivity) {
        this.activity = registerActivity;

    }

    public RegisterActivity getContext() {
        return this.activity;

    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public boolean isAgree() {
        return agree;
    }

    public void setAgree(boolean agree) {
        this.agree = agree;
    }
}

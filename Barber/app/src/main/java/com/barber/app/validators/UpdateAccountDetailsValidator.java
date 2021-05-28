package com.barber.app.validators;

import com.barber.app.codes.OperationCode;


public class UpdateAccountDetailsValidator extends Validator {
    private String userPassword;
    private String phoneNumber;
    private String name;


    @Override
    public OperationCode validate() throws Exception {
        NameValidator nameValidator = new NameValidator();
        nameValidator.setName(getName());
        nameValidator.validate();

        PasswordValidator passwordValidator = new PasswordValidator();
        passwordValidator.setToken(getUserPassword());
        passwordValidator.validate();

        PhoneValidator phoneValidator = new PhoneValidator();
        phoneValidator.setPhoneNum(getPhoneNumber());
        phoneValidator.validate();

        return OperationCode.OperationSuccess;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUserPassword() {
        return userPassword;
    }

    public void setLoginPassword(String password) {
        this.userPassword = password;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}


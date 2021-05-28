package com.barber.app.validators;

import com.barber.app.codes.OperationCode;
import com.barber.app.utils.UtilsImpl;

public class PasswordValidator extends Validator {
    private String token;
    private final int passwordMaxLength = 100;

    @Override
    public OperationCode validate() throws Exception {
        if (!UtilsImpl.isNotNullOrEmptyPassword(getToken())) {
            throw new Exception("Password cannot be empty");
        }
        if (isPasswordShort(getToken())) {
            throw new Exception("Password field too short");
        }
        if (getToken().length() > passwordMaxLength) {
            throw new Exception("Password field too big. MAX 100 characters");
        }

        return OperationCode.OperationSuccess;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getToken() {
        return token;
    }

    public boolean isPasswordShort(String value) {
        final int length = 5;
        return value.length() <= length;
    }
}

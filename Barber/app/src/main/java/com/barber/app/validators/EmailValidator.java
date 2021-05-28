package com.barber.app.validators;

import android.util.Patterns;

import com.barber.app.codes.OperationCode;
import com.barber.app.utils.UtilsImpl;

public class EmailValidator extends Validator {
    private String mail;
    private final int emailMaxLength = 100;


    @Override
    public OperationCode validate() throws Exception {
        if (!UtilsImpl.isNotNullOrEmpty(getMail()) || !Patterns.EMAIL_ADDRESS.matcher(getMail()).matches()) {
            throw new Exception("Email address has invalid format.");
        }
        if (getMail().length() > emailMaxLength) {
            throw new Exception("Email too big. MAX 100 characters.");
        }
        return OperationCode.OperationSuccess;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }
}

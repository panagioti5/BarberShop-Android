package com.barber.app.validators;

import com.barber.app.codes.OperationCode;

public class ForgotPasswordValidator extends Validator {

    private String email;

    @Override
    public OperationCode validate() throws Exception {

        EmailValidator emailValidator = new EmailValidator();
        emailValidator.setMail(getEmail());
        emailValidator.validate();
        return OperationCode.OperationSuccess;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}

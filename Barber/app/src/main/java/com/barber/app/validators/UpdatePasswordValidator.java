package com.barber.app.validators;

import com.barber.app.codes.OperationCode;

public class UpdatePasswordValidator extends Validator {

    private String newToken;
    private String resetCode;

    @Override
    public OperationCode validate() throws Exception {

        ResetCodeValidator resetCodeValidator = new ResetCodeValidator();
        resetCodeValidator.setResetCode(getResetCode());
        resetCodeValidator.validate();

        PasswordValidator passwordValidator = new PasswordValidator();
        passwordValidator.setToken(getNewToken());
        passwordValidator.validate();

        return OperationCode.OperationSuccess;
    }

    public String getNewToken() {
        return newToken;
    }

    public void setNewToken(String newToken) {
        this.newToken = newToken;
    }

    public String getResetCode() {
        return resetCode;
    }

    public void setResetCode(String resetCode) {
        this.resetCode = resetCode;
    }
}

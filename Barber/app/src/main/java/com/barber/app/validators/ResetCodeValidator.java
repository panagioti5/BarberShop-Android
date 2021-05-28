package com.barber.app.validators;

import com.barber.app.codes.OperationCode;
import com.barber.app.utils.UtilsImpl;

public class ResetCodeValidator extends Validator {
    private String resetCode;


    @Override
    public OperationCode validate() throws Exception {

        if (!UtilsImpl.isNotNullOrEmpty(getResetCode())) {
            throw new Exception("Reset Code cannot be empty.");
        }
        if (getResetCode().length() > 5) {
            throw new Exception("Reset Code invalid.");
        }
        return OperationCode.OperationSuccess;
    }

    public String getResetCode() {
        return resetCode;
    }

    public void setResetCode(String resetCode) {
        this.resetCode = resetCode;
    }
}

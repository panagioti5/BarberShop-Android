package com.barber.app.validators;

import com.barber.app.codes.OperationCode;
import com.barber.app.utils.UtilsImpl;

public class PhoneValidator extends Validator {

    private String phoneNum;

    public String getPhoneNum() {
        return phoneNum;
    }

    public void setPhoneNum(String phoneNum) {
        this.phoneNum = phoneNum;
    }

    @Override
    public OperationCode validate() throws Exception {

        if (!UtilsImpl.isNotNullOrEmpty(getPhoneNum())) {
            throw new Exception("Phone Number has invalid format");
        }

        if (!(getPhoneNum().length() >= 8 && getPhoneNum().length() <= 14 && (getPhoneNum().startsWith("99")
                || getPhoneNum().startsWith("96") || getPhoneNum().startsWith("95") || getPhoneNum().startsWith("97")))) {
            throw new Exception("Phone Number has invalid format");
        }
        try {
            Long.parseLong(getPhoneNum());
        } catch (NumberFormatException exception) {
            throw new Exception("Phone Number has invalid format");
        }

        return OperationCode.OperationSuccess;
    }
}

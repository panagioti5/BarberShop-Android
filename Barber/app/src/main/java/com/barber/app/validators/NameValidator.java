package com.barber.app.validators;

import com.barber.app.codes.OperationCode;
import com.barber.app.utils.UtilsImpl;

public class NameValidator extends Validator {
    private String name;
    private final int nameMaxLength = 50;


    @Override
    public OperationCode validate() throws Exception {
        if (!UtilsImpl.isNotNullOrEmpty(getName())) {
            throw new Exception("Name cannot be empty");
        }
        if (!UtilsImpl.isValueWithoutSpecialCharacters(getName())) {
            throw new Exception("Name cannot contain special characters or numbers");
        }
        if (getName().length() > nameMaxLength) {
            throw new Exception("Name too big. MAX 50 characters.");
        }
        return OperationCode.OperationSuccess;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}

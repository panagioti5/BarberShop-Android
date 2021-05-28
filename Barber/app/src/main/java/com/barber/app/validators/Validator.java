package com.barber.app.validators;

import com.barber.app.codes.OperationCode;

public abstract class Validator {

    public abstract OperationCode validate() throws Exception;
}

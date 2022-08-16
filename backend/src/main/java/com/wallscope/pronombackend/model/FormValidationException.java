package com.wallscope.pronombackend.model;

import java.util.ArrayList;

public class FormValidationException extends Exception {
    public FormValidationException(String message) {
        super(message);
    }

    public void throwOrAdd(boolean add, ArrayList<FormValidationException> list) throws FormValidationException {
        if (add) {
            list.add(this);
        } else {
            throw this;
        }
    }
}

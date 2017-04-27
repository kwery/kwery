package com.kwery.services.mail;

import java.util.List;

public class InvalidEmailException extends RuntimeException {
    protected List<String> invalids;

    public InvalidEmailException(List<String> invalids) {
        this.invalids = invalids;
    }

    public List<String> getInvalids() {
        return invalids;
    }
}

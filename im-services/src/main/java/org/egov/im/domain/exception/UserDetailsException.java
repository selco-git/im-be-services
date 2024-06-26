package org.egov.im.domain.exception;

import lombok.Getter;

@Getter
public class UserDetailsException extends RuntimeException {

    public UserDetailsException() {
        super("Error while fetching user details");
    }

}


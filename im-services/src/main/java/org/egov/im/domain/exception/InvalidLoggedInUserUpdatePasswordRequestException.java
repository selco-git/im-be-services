package org.egov.im.domain.exception;

import org.egov.im.domain.model.LoggedInUserUpdatePasswordRequest;

import lombok.Getter;

public class InvalidLoggedInUserUpdatePasswordRequestException extends RuntimeException {
    private static final long serialVersionUID = 6391424774009868054L;
    @Getter
    private final LoggedInUserUpdatePasswordRequest request;

    public InvalidLoggedInUserUpdatePasswordRequestException(LoggedInUserUpdatePasswordRequest updatePassword) {
        this.request = updatePassword;
    }
}

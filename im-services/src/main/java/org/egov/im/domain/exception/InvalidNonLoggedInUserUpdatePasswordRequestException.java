package org.egov.im.domain.exception;

import org.egov.im.domain.model.NonLoggedInUserUpdatePasswordRequest;

import lombok.Getter;

public class InvalidNonLoggedInUserUpdatePasswordRequestException extends RuntimeException {
    private static final long serialVersionUID = -371650760688252507L;
    @Getter
    private NonLoggedInUserUpdatePasswordRequest model;

    public InvalidNonLoggedInUserUpdatePasswordRequestException(NonLoggedInUserUpdatePasswordRequest model) {
        this.model = model;
    }
}

package org.egov.im.domain.exception;

import org.egov.im.domain.model.ValidateRequest;

import lombok.Getter;

public class InvalidTokenValidateRequestException extends RuntimeException {

    private static final long serialVersionUID = -8041669529008165462L;
    @Getter
    private ValidateRequest validateRequest;

    public InvalidTokenValidateRequestException(ValidateRequest validateRequest) {
        this.validateRequest = validateRequest;
    }
}


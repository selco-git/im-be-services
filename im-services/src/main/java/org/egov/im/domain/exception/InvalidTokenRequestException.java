package org.egov.im.domain.exception;

import org.egov.im.domain.model.TokenRequest;

import lombok.Getter;

public class InvalidTokenRequestException extends RuntimeException {
    private static final long serialVersionUID = -1900986732529893867L;

    @Getter
    private TokenRequest tokenRequest;

    public InvalidTokenRequestException(TokenRequest tokenRequest) {

        this.tokenRequest = tokenRequest;
    }
}



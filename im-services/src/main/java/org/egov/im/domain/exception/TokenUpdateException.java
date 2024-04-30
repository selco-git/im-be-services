package org.egov.im.domain.exception;

import org.egov.im.domain.model.Token;

import lombok.Getter;

public class TokenUpdateException extends RuntimeException {

    private static final long serialVersionUID = -5189733065290610351L;
    @Getter
    private Token token;

    public TokenUpdateException(Token token) {
        this.token = token;
    }
}

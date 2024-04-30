package org.egov.im.web.controllers;

import org.egov.common.contract.response.ErrorResponse;
import org.egov.im.domain.exception.InvalidTokenRequestException;
import org.egov.im.domain.exception.InvalidTokenSearchCriteriaException;
import org.egov.im.domain.exception.InvalidTokenValidateRequestException;
import org.egov.im.domain.exception.TokenAlreadyUsedException;
import org.egov.im.domain.exception.TokenUpdateException;
import org.egov.im.domain.exception.TokenValidationFailureException;
import org.egov.im.web.adapters.errors.TokenAlreadyUsedFailureAdapter;
import org.egov.im.web.adapters.errors.TokenRequestErrorAdapter;
import org.egov.im.web.adapters.errors.TokenSearchErrorAdapter;
import org.egov.im.web.adapters.errors.TokenUpdateErrorAdapter;
import org.egov.im.web.adapters.errors.TokenValidationFailureAdapter;
import org.egov.im.web.adapters.errors.TokenValidationRequestErrorAdapter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@ControllerAdvice
@RestController
public class CustomControllerAdvice {

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(InvalidTokenRequestException.class)
    public ErrorResponse handleInvalidSearchTypeException(InvalidTokenRequestException ex) {
        return new TokenRequestErrorAdapter().adapt(ex.getTokenRequest());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(InvalidTokenValidateRequestException.class)
    public ErrorResponse handleInvalidSearchTypeException(InvalidTokenValidateRequestException ex) {
        return new TokenValidationRequestErrorAdapter().adapt(ex.getValidateRequest());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(TokenUpdateException.class)
    public ErrorResponse handleTokenUpdateException(TokenUpdateException ex) {
        return new TokenUpdateErrorAdapter().adapt(ex.getToken());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(InvalidTokenSearchCriteriaException.class)
    public ErrorResponse handleInvalidSearchException(InvalidTokenSearchCriteriaException ex) {
        return new TokenSearchErrorAdapter().adapt(ex.getTokenSearchCriteria());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(TokenValidationFailureException.class)
    public ErrorResponse handleTokenValidationFailureException() {
        return new TokenValidationFailureAdapter().adapt(null);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(TokenAlreadyUsedException.class)
    public ErrorResponse handleTokenTokenAlreadyUsedException() {
        return new TokenAlreadyUsedFailureAdapter().adapt(null);
    }

}

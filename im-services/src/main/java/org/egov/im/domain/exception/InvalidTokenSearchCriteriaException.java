package org.egov.im.domain.exception;

import org.egov.im.domain.model.TokenSearchCriteria;

import lombok.Getter;

public class InvalidTokenSearchCriteriaException extends RuntimeException {

    private static final long serialVersionUID = 3634242817213671136L;
    @Getter
    private TokenSearchCriteria tokenSearchCriteria;

    public InvalidTokenSearchCriteriaException(TokenSearchCriteria tokenSearchCriteria) {
        this.tokenSearchCriteria = tokenSearchCriteria;
    }
}

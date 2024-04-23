package org.egov.im.domain.exception;

import org.egov.im.entity.User;

import lombok.Getter;

@Getter
public class InvalidUserCreateException extends RuntimeException {

    private static final long serialVersionUID = -761312648494992125L;
    private User user;

    public InvalidUserCreateException(User user) {
        this.user = user;
    }

}


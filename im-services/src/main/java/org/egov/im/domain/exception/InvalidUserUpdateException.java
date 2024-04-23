package org.egov.im.domain.exception;

import org.egov.im.entity.User;

import lombok.Getter;

public class InvalidUserUpdateException extends RuntimeException {

    private static final long serialVersionUID = 580361940613077431L;
    @Getter
    private User user;

    public InvalidUserUpdateException(User user) {
        this.user = user;
    }

}

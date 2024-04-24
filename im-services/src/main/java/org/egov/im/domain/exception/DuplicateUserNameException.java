package org.egov.im.domain.exception;

import org.egov.im.domain.model.UserSearchCriteria;

import lombok.Getter;

public class DuplicateUserNameException extends RuntimeException {

    private static final long serialVersionUID = -6903761146294214595L;
    @Getter
    private UserSearchCriteria userSearchCriteria;

    public DuplicateUserNameException(UserSearchCriteria userSearchCriteria) {
        this.userSearchCriteria = userSearchCriteria;
    }

}

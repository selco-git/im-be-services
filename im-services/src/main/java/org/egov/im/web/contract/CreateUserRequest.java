package org.egov.im.web.contract;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.egov.im.entity.User;
import org.egov.im.web.models.RequestInfo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
public class CreateUserRequest {
    private RequestInfo requestInfo;

    @NotNull
    @Valid
    private User user;

    public User toDomain(boolean isCreate) {
        return user.toDomain(loggedInUserId(),loggedInUserUuid(), isCreate);
    }

    // TODO Update libraries to have uuid in request info
    private Long loggedInUserId() {
        return requestInfo.getUserInfo() == null ? null : requestInfo.getUserInfo().getId();
    }
    private String loggedInUserUuid() {
        return requestInfo.getUserInfo() == null ? null : requestInfo.getUserInfo().getUuid();
    }

}



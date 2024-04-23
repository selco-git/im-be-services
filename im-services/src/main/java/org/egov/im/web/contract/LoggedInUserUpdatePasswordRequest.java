package org.egov.im.web.contract;

import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import org.egov.im.config.UserServiceConstants;
import org.egov.im.domain.model.enums.UserType;
import org.egov.im.web.models.RequestInfo;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class
LoggedInUserUpdatePasswordRequest {
    @JsonProperty("RequestInfo")
    private RequestInfo requestInfo;
    private String existingPassword;
    private String newPassword;

    @Pattern(regexp = UserServiceConstants.PATTERN_TENANT)
    @Size(max = 256)
    private String tenantId;
    private UserType type;

    public org.egov.im.domain.model.LoggedInUserUpdatePasswordRequest toDomain() {
        return org.egov.im.domain.model.LoggedInUserUpdatePasswordRequest.builder()
                .existingPassword(existingPassword)
                .newPassword(newPassword)
                .userName(getUsername())
                .tenantId(tenantId)
                .type(type)
                .build();
    }

    private String getUsername() {
        return requestInfo == null || requestInfo.getUserInfo() == null ? null : requestInfo.getUserInfo().getUsername();
    }
}


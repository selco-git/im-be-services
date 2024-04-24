package org.egov.im.web.contract;

import javax.validation.constraints.Size;

import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import org.codehaus.jackson.annotate.JsonProperty;
import org.egov.im.config.UserServiceConstants;
import org.egov.im.domain.model.enums.UserType;
import org.egov.im.web.models.RequestInfo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/*
	Update password request by non logged in user
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode
@ToString
public class NonLoggedInUserUpdatePasswordRequest {

    @JsonProperty("RequestInfo")
    private RequestInfo requestInfo;

    private String otpReference;

    @Size(max = 64)
    private String userName;
    private String newPassword;

    @Pattern(regexp = UserServiceConstants.PATTERN_TENANT)
    @Size(max = 256)
    private String tenantId;
    private UserType type;

    public org.egov.im.domain.model.NonLoggedInUserUpdatePasswordRequest toDomain() {
        return org.egov.im.domain.model.NonLoggedInUserUpdatePasswordRequest.builder().otpReference(otpReference)
                .userName(userName).newPassword(newPassword).type(type).tenantId(tenantId).build();
    }
}

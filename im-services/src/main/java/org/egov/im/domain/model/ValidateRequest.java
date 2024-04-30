package org.egov.im.domain.model;

import static org.springframework.util.StringUtils.isEmpty;

import org.egov.im.domain.exception.InvalidTokenValidateRequestException;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@AllArgsConstructor
@Builder
@EqualsAndHashCode
@Getter
public class ValidateRequest {
    private String tenantId;
    private String otp;
    private String identity;


    public void validate() {
        if (isTenantIdAbsent() || isOtpAbsent() || isIdentityAbsent()) {
            throw new InvalidTokenValidateRequestException(this);
        }
    }

    public boolean isIdentityAbsent() {
        return isEmpty(identity);
    }

    public boolean isOtpAbsent() {
        return isEmpty(otp);
    }

    public boolean isTenantIdAbsent() {
        return isEmpty(tenantId);
    }
}

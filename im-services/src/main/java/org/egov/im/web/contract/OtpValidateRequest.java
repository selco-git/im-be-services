package org.egov.im.web.contract;



import javax.validation.Valid;

import org.egov.im.domain.model.ValidateRequest;
import org.egov.im.web.models.RequestInfo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@AllArgsConstructor
@Builder
@ToString
public class OtpValidateRequest {
    @JsonProperty("RequestInfo")
    private RequestInfo requestInfo;

    @JsonProperty("otp")
    @Valid
    private Otp otp;

    public ValidateRequest toDomainValidateRequest() {
        return ValidateRequest.builder()
                .tenantId(getTenantId())
                .identity(getIdentity())
                .otp(getOtp())
                .build();
    }

    @JsonIgnore
    private String getIdentity() {
        return otp != null ? otp.getIdentity() : null;
    }

    @JsonIgnore
    private String getOtp() {
        return otp != null ? otp.getOtp() : null;
    }

    @JsonIgnore
    private String getTenantId() {
        return otp != null ? otp.getTenantId() : null;
    }
}


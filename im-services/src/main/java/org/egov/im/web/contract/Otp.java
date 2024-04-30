package org.egov.im.web.contract;

import javax.validation.constraints.Size;

import org.egov.im.domain.model.Token;
import org.egov.im.domain.model.enums.UserType;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;


@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
@Setter
public class Otp {
    @Size(max = 128)
    private String otp;
    @JsonProperty("UUID")
    @Size(max = 36)
    private String uuid;
    @Size(max = 100)
    private String identity;
    @Size(max = 256)
    private String tenantId;
    @JsonProperty("isValidationSuccessful")
    private boolean validationSuccessful;
    private String userType;


    public Otp(Token token) {
        otp = token.getNumber();
        uuid = token.getUuid();
        identity = token.getIdentity();
        tenantId = token.getTenantId();
        validationSuccessful = token.isValidated();
    }
    
    public boolean isValidationComplete(String mobileNumber) {
        return validationSuccessful && identity.equals(mobileNumber);
    }
}

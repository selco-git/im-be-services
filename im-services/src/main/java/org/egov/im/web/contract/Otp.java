package org.egov.im.web.contract;

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
    private String otp;
    @JsonProperty("UUID")

    private String uuid;
    private String identity;
    private String tenantId;
    private String userType;

    @JsonProperty("isValidationSuccessful")
    private boolean validationSuccessful;
}

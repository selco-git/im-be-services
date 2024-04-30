package org.egov.im.web.contract;

import org.egov.common.contract.response.ResponseInfo;
import org.egov.im.domain.model.Token;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Builder
public class OtpResponse {
    private ResponseInfo responseInfo;
    private Otp otp;

    public OtpResponse(Token token) {
        if (token != null) {
            otp = new Otp(token);
        }
    }
    
    public boolean isValidationComplete(String mobileNumber) {
        return otp != null && otp.isValidationComplete(mobileNumber);
    }
}





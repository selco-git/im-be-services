package org.egov.im.persistence.repository;

import org.egov.im.domain.model.OtpValidationRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.RestTemplate;

import lombok.extern.slf4j.Slf4j;

@Repository
@Slf4j
public class OtpRepository {
    private final RestTemplate restTemplate;
 
    

    @Autowired
    public OtpRepository(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    /**
     * rest-call to egov-otp to check otp validation is complete or not.
     *
     * @param request
     * @return
     */
    public boolean isOtpValidationComplete(OtpValidationRequest request) {
//        Otp otp = Otp.builder().tenantId(request.getTenantId()).uuid(request.getOtpReference()).build();
//        OtpRequest otpRequest = new OtpRequest(otp);
//        OtpResponse otpResponse = restTemplate.postForObject(otpSearchEndpoint, otpRequest, OtpResponse.class);
//        return otpResponse.isValidationComplete(request.getMobileNumber());
    	return true;
    }


    /**
     * rest call to egov-otp to validate the otp.
     *
     * @param request
     * @return
     */
//    public boolean validateOtp(OtpValidateRequest request) {
//        try {
//            OtpResponse otpResponse = restTemplate.postForObject(otpValidateEndpoint, request, OtpResponse.class);
//            if (null != otpResponse && null != otpResponse.getOtp())
//                return otpResponse.getOtp().isValidationSuccessful();
//            else
//                return false;
//        } catch (HttpClientErrorException e) {
//            log.error("Otp validation failed", e);
//            throw new ServiceCallException(e.getResponseBodyAsString());
//        }
//    }
}



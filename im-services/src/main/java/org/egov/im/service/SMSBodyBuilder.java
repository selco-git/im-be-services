package org.egov.im.service;

import org.egov.im.web.models.Sms;
import org.springframework.util.MultiValueMap;

public interface SMSBodyBuilder {

    MultiValueMap<String, String> getSmsRequestBody(Sms sms);

}

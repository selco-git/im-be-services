package org.egov.im.config;

import org.egov.im.web.models.Sms;
import org.springframework.stereotype.Component;

public interface SMSService {
    void sendSMS(Sms sms);
}


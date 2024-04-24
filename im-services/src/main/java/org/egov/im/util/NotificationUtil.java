package org.egov.im.util;

import static org.egov.im.util.IMConstants.NOTIFICATION_LOCALE;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.egov.common.utils.MultiStateInstanceUtil;
import org.egov.im.config.IMConfiguration;
import org.egov.im.producer.Producer;
import org.egov.im.repository.ServiceRequestRepository;
import org.egov.im.service.EventService;
import org.egov.im.web.models.RequestInfo;
import org.egov.im.web.models.Notification.EventRequest;
import org.egov.im.web.models.Notification.SMSRequest;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.RestTemplate;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class NotificationUtil {

    @Autowired
    private ServiceRequestRepository serviceRequestRepository;

    @Autowired
    private IMConfiguration config;

    @Autowired
    private Producer producer;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private MultiStateInstanceUtil centralInstanceUtil;
    
    @Autowired
    private EventService eventService;
    
 


    /**
     *
     * @param tenantId Tenant ID
     * @param requestInfo Request Info object
     * @param module Module name
     * @return Return Localisation Message
     */
    public String getLocalizationMessages(String tenantId, RequestInfo requestInfo,String module) {
        @SuppressWarnings("rawtypes")
        LinkedHashMap responseMap = (LinkedHashMap) serviceRequestRepository.fetchResult(getUri(tenantId, requestInfo, module),
                requestInfo);
        return new JSONObject(responseMap).toString();
    }

    /**
     *
     * @param tenantId Tenant ID
     * @param requestInfo Request Info object
     * @param module Module name
     * @return Return uri
     */
    public StringBuilder getUri(String tenantId, RequestInfo requestInfo, String module) {

        /*if (config.getIsLocalizationStateLevel())
            tenantId= centralInstanceUtil.getStateLevelTenant(tenantId);*/
        tenantId= centralInstanceUtil.getStateLevelTenant(tenantId);
        log.info("tenantId after calling central instance method :"+ tenantId);
        String locale = NOTIFICATION_LOCALE;
        if (!StringUtils.isEmpty(requestInfo.getMsgId()) && requestInfo.getMsgId().split("|").length >= 2)
            locale = requestInfo.getMsgId().split("\\|")[1];
        StringBuilder uri = new StringBuilder();
        uri.append(config.getLocalizationHost()).append(config.getLocalizationContextPath())
                .append(config.getLocalizationSearchEndpoint()).append("?").append("locale=").append(locale)
                .append("&tenantId=").append(tenantId).append("&module=").append(module);

        return uri;
    }

    /**
     *
     * @param action Action
     * @param applicationStatus Application Status
     * @param roles CITIZEN or EMPLOYEE
     * @param localizationMessage Localisation Message
     * @return Return Customized Message based on localisation code
     */
    public String getCustomizedMsg(String action, String applicationStatus, String roles, Map<String,String> localizationMessage) {
        String notificationCode = "";
        notificationCode=notificationCode.concat("IM_").concat(roles.toUpperCase()).concat("_").concat(action.toUpperCase()).concat("_").concat(applicationStatus.toUpperCase()).concat("_SMS_MESSAGE");

        
        String message = localizationMessage.get(notificationCode);

        return message;
    }

    /**
     *
     * @param roles EMPLOYEE or CITIZEN
     * @param localizationMessage Localisation Message
     * @return Return localisation message based on default code
     */
    public String getDefaultMsg(String roles, Map<String,String> localizationMessage) {
    	 String notificationCode = "";
         notificationCode=notificationCode.concat("IM_").concat("DEFAULT_").concat(roles.toUpperCase()).concat("_SMS_MESSAGE");

        String message = localizationMessage.get(notificationCode);

        return message;
    }

    /**
     * Send the SMSRequest on the SMSNotification kafka topic
     * @param smsRequestList The list of SMSRequest to be sent
     */
    public void sendSMS(String tenantId, List<SMSRequest> smsRequestList) {
        if (config.getIsSMSEnabled()) {
            if (CollectionUtils.isEmpty(smsRequestList)) {
                log.info("Messages from localization couldn't be fetched!");
                return;
            }
            for (SMSRequest smsRequest : smsRequestList) {
            	//smsNotificationListener.process(smsRequest);
                log.info("Messages: " + smsRequest.getMessage());
            }
        }
    }

    /**
     * Pushes the event request to Kafka Queue.
     *
     * @param request EventRequest Object
     */
    public void sendEventNotification(String tenantId, EventRequest request) {
        eventService.save(request.getEvents().get(0));
    }

    /**
     *
     * @param actualURL Actual URL
     * @return Shortened URL
     */
    public String getShortnerURL(String actualURL) {
        HashMap<String,String> body = new HashMap<>();
        body.put("url",actualURL);
        StringBuilder builder = new StringBuilder(config.getUrlShortnerHost());
        builder.append(config.getUrlShortnerEndpoint());
        String res = restTemplate.postForObject(builder.toString(), body, String.class);

        if(StringUtils.isEmpty(res)){
            log.error("URL_SHORTENING_ERROR","Unable to shorten url: "+actualURL); ;
            return actualURL;
        }
        else return res;
    }

    /**
     *
     * @param localizationMessage Localisation Code
     * @param notificationCode Notification Code
     * @return Return Customized Message
     */
    public String getCustomizedMsgForPlaceholder(Map<String,String> localizationMessage,String notificationCode) {
       
        String message = localizationMessage.get(notificationCode);
        return message;
    }

}

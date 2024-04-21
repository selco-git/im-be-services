package org.egov.im.service;

import static org.egov.im.util.IMConstants.APPLY;
import static org.egov.im.util.IMConstants.ASSIGN;
import static org.egov.im.util.IMConstants.CITIZEN;
import static org.egov.im.util.IMConstants.CLOSED_AFTER_REJECTION;
import static org.egov.im.util.IMConstants.CLOSED_AFTER_RESOLUTION;
import static org.egov.im.util.IMConstants.COMMON_MODULE;
import static org.egov.im.util.IMConstants.DATE_PATTERN;
import static org.egov.im.util.IMConstants.EMPLOYEE;
import static org.egov.im.util.IMConstants.NOTIFICATION_ENABLE_FOR_STATUS;
import static org.egov.im.util.IMConstants.PENDINGATLME;
import static org.egov.im.util.IMConstants.PENDINGFORASSIGNMENT;
import static org.egov.im.util.IMConstants.PENDING_FOR_REASSIGNMENT;
import static org.egov.im.util.IMConstants.PGR_WF_REOPEN;
import static org.egov.im.util.IMConstants.PGR_WF_RESOLVE;
import static org.egov.im.util.IMConstants.RATE;
import static org.egov.im.util.IMConstants.REASSIGN;
import static org.egov.im.util.IMConstants.REJECT;
import static org.egov.im.util.IMConstants.REJECTED;
import static org.egov.im.util.IMConstants.RESOLVED;
import static org.egov.im.util.IMConstants.USREVENTS_EVENT_NAME;
import static org.egov.im.util.IMConstants.USREVENTS_EVENT_POSTEDBY;
import static org.egov.im.util.IMConstants.USREVENTS_EVENT_TYPE;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.egov.common.utils.MultiStateInstanceUtil;
import org.egov.im.config.IMConfiguration;
import org.egov.im.config.SMSService;
import org.egov.im.entity.Event;
import org.egov.im.entity.ProcessInstance;
import org.egov.im.entity.Recepient;
import org.egov.im.entity.Role;
import org.egov.im.entity.User;
import org.egov.im.repository.IdGenRepository;
import org.egov.im.repository.ServiceRequestRepository;
import org.egov.im.util.IMUtils;
import org.egov.im.util.NotificationUtil;
import org.egov.im.util.WorkflowUtil;
import org.egov.im.web.models.Category;
import org.egov.im.web.models.IncidentRequest;
import org.egov.im.web.models.IncidentWrapper;
import org.egov.im.web.models.RequestInfo;
import org.egov.im.web.models.RequestInfoWrapper;
import org.egov.im.web.models.Notification.EventRequest;
import org.egov.im.web.models.Notification.SMSRequest;
import org.egov.im.web.models.workflow.ProcessInstanceResponse;
import org.egov.tracer.model.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class NotificationService {

    @Autowired
    private IMConfiguration config;

    @Autowired
    private NotificationUtil notificationUtil;

    @Autowired
    private WorkflowService workflowService;

    @Autowired
    private ServiceRequestRepository serviceRequestRepository;

    @Autowired
    private ObjectMapper mapper;
    
    @Autowired
    private IMConfiguration imConfiguration;

    @Autowired
    private MultiStateInstanceUtil centralInstanceUtil;
    
    private SMSService smsService;

  

    public void process(IncidentRequest request, String topic) {
        try {
            log.info("request for notification :" + request);
            String tenantId = request.getIncident().getTenantId();
            IncidentWrapper incidentWrapper = IncidentWrapper.builder().incident(request.getIncident()).workflow(request.getWorkflow()).build();
            String applicationStatus = request.getIncident().getApplicationStatus();
            String action = request.getWorkflow().getAction();


            if (!(NOTIFICATION_ENABLE_FOR_STATUS.contains(action+"_"+applicationStatus))) {
                log.info("Notification Disabled For State :" + applicationStatus);
                return;
            }

            Map<String, List<String>> finalMessage = getFinalMessage(request, topic, applicationStatus);
            String reporterMobileNumber = request.getIncident().getReporterr().getMobileNumber();
            String employeeMobileNumber = null;

//            if(applicationStatus.equalsIgnoreCase(PENDINGFORASSIGNMENT) && action.equalsIgnoreCase(PGR_WF_REOPEN)) {
//                ProcessInstance processInstance = getEmployeeName(incidentWrapper.getIncident().getTenantId(),incidentWrapper.getIncident().getIncidentId(),request.getRequestInfo(),ASSIGN);
//                employeeMobileNumber = processInstance.getAssignes().get(0).getMobileNumber();
//            }
//            else if(applicationStatus.equalsIgnoreCase(PENDINGFORASSIGNMENT) && action.equalsIgnoreCase(APPLY)) {
//                employeeMobileNumber = null;
//            }
//            else if(applicationStatus.equalsIgnoreCase(REJECTED) && action.equalsIgnoreCase(REJECT)) {
//                employeeMobileNumber = null;
//            }
//            else  if (applicationStatus.equalsIgnoreCase(RESOLVED)  && action.equalsIgnoreCase(PGR_WF_RESOLVE)){
//                ProcessInstance processInstance = getEmployeeName(incidentWrapper.getIncident().getTenantId(),incidentWrapper.getIncident().getIncidentId(),request.getRequestInfo(),ASSIGN);
//                employeeMobileNumber = processInstance.getAssignes().get(0).getMobileNumber();
//            }
//            else  if ((applicationStatus.equalsIgnoreCase(CLOSED_AFTER_RESOLUTION) || applicationStatus.equalsIgnoreCase(CLOSED_AFTER_REJECTION)) && action.equalsIgnoreCase(RATE)) {
//                ProcessInstance processInstance = getEmployeeName(incidentWrapper.getIncident().getTenantId(),incidentWrapper.getIncident().getIncidentId(),request.getRequestInfo(),ASSIGN);
//                employeeMobileNumber = processInstance.getAssignes().get(0).getMobileNumber();
//            }
//            else if ((applicationStatus.equalsIgnoreCase(PENDINGATLME) && action.equalsIgnoreCase(ASSIGN)) || (applicationStatus.equalsIgnoreCase(PENDING_FOR_REASSIGNMENT) && action.equalsIgnoreCase(REASSIGN))){
//                employeeMobileNumber = fetchUserByUUID(request.getWorkflow().getAssignes().get(0), request.getRequestInfo(), request.getIncident().getTenantId()).getMobileNumber();
//            }
//            else if(applicationStatus.equalsIgnoreCase(PENDINGATLME) && action.equalsIgnoreCase(REASSIGN))
//            {
//                employeeMobileNumber = fetchUserByUUID(request.getWorkflow().getAssignes().get(0), request.getRequestInfo(), request.getIncident().getTenantId()).getMobileNumber();
//            }
//            else {
//                employeeMobileNumber = fetchUserByUUID(request.getIncident().getCreatedBy(), request.getRequestInfo(), request.getIncident().getTenantId()).getMobileNumber();
//            }

            if(!StringUtils.isEmpty(finalMessage)) {
                if (config.getIsUserEventsNotificationEnabled() != null && config.getIsUserEventsNotificationEnabled()) {
                    for (Map.Entry<String, List<String>> entry : finalMessage.entrySet()) {
                        for (String msg : entry.getValue()) {
                            EventRequest eventRequest = enrichEventRequest(request, msg);
                            if (eventRequest != null) {
                                notificationUtil.sendEventNotification(tenantId, eventRequest);
                            }
                        }
                    }
                }

                if (config.getIsSMSEnabled() != null && config.getIsSMSEnabled()) {

                    for (Map.Entry<String, List<String>> entry : finalMessage.entrySet()) {

                        if (entry.getKey().equalsIgnoreCase(CITIZEN)) {
                            for (String msg : entry.getValue()) {
                                List<SMSRequest> smsRequests = new ArrayList<>();
                                smsRequests = enrichSmsRequest(reporterMobileNumber, msg);
                                if (!CollectionUtils.isEmpty(smsRequests)) {
//                                	 for (SMSRequest smsrequest : smsRequests) {
//                                    	 if (smsrequest.getExpiryTime() != null && smsrequest.getCategory() == Category.OTP) {
//                                             Long expiryTime = smsrequest.getExpiryTime();
//                                             Long currentTime = System.currentTimeMillis();
//                                             if (expiryTime < currentTime) {
//                                                 log.info("OTP Expired");
//                                                 //if (!StringUtils.isEmpty(expiredSmsTopic))
//                                                    // kafkaTemplate.send(expiredSmsTopic, request);
//                                             } else {
//                                                 smsService.sendSMS(smsrequest.toDomain());
//                                             }
//                                         } else {
//                                             smsService.sendSMS(smsrequest.toDomain());
//                                         }
//                                        log.info("Messages: " + smsrequest.getMessage());
//                                    }
                                    notificationUtil.sendSMS(tenantId, smsRequests);

                                }

                                }
                            }
               else {
                            for (String msg : entry.getValue()) {
                                List<SMSRequest> smsRequests = new ArrayList<>();
                                smsRequests = enrichSmsRequest(reporterMobileNumber, msg);
                                if (!CollectionUtils.isEmpty(smsRequests)) {
                                    notificationUtil.sendSMS(tenantId, smsRequests);
                                }
                            }
                        }
                    }

                }


            }

        } catch (Exception ex) {
            log.error("Error occured while processing the record from topic : " + topic, ex);
        }
    }

    /**
     *
     * @param request im Request
     * @param topic Topic Name
     * @param applicationStatus Application Status
     * @return Returns list of SMSRequest
     */
    private Map<String, List<String>> getFinalMessage(IncidentRequest request, String topic, String applicationStatus) {
        String tenantId = request.getIncident().getTenantId();
        Map<String,String> localizationMessage = imConfiguration.getEgovIMMsgList();
        IncidentWrapper incidentWrapper = IncidentWrapper.builder().incident(request.getIncident()).workflow(request.getWorkflow()).build();
        Map<String, List<String>> message = new HashMap<>();

        String messageForCitizen = null;
        String messageForEmployee = null;
        String defaultMessage = null;

        String localisedStatus = null;
        
        if(incidentWrapper.getIncident().getApplicationStatus().equalsIgnoreCase(PENDINGFORASSIGNMENT))
        	localisedStatus="Acknowledged";
        
        /**
         * Confirmation SMS to citizens, when they will raise any complaint
         */
        if(incidentWrapper.getIncident().getApplicationStatus().equalsIgnoreCase(PENDINGFORASSIGNMENT) && incidentWrapper.getWorkflow().getAction().equalsIgnoreCase(APPLY)) {
            messageForCitizen = notificationUtil.getCustomizedMsg(request.getWorkflow().getAction(), applicationStatus, CITIZEN, localizationMessage);
            if (messageForCitizen == null) {
                log.info("No message Found For Citizen");
                return null;
            }

            defaultMessage = notificationUtil.getDefaultMsg(CITIZEN, localizationMessage);
            if (defaultMessage == null) {
                log.info("No default message Found For Topic : " + topic);
                return null;
            }

            if (defaultMessage.contains("{status}"))
                defaultMessage = defaultMessage.replace("{status}", localisedStatus);


        }
        /**
         * SMS to citizens and employee both, when a complaint is assigned to an employee
         */
        if(incidentWrapper.getIncident().getApplicationStatus().equalsIgnoreCase(PENDINGATLME) && incidentWrapper.getWorkflow().getAction().equalsIgnoreCase(ASSIGN)) {
            messageForCitizen = notificationUtil.getCustomizedMsg(request.getWorkflow().getAction(), applicationStatus, CITIZEN, localizationMessage);
            if (messageForCitizen == null) {
                log.info("No message Found For Citizen On Topic : " + topic);
                return null;
            }

            messageForEmployee = notificationUtil.getCustomizedMsg(request.getWorkflow().getAction(), applicationStatus, EMPLOYEE, localizationMessage);
            if (messageForEmployee == null) {
                log.info("No message Found For Employee On Topic : " + topic);
                return null;
            }

            defaultMessage = notificationUtil.getDefaultMsg(CITIZEN, localizationMessage);
            if (defaultMessage == null) {
                log.info("No default message Found For Topic : " + topic);
                return null;
            }

            if(defaultMessage.contains("{status}"))
                defaultMessage = defaultMessage.replace("{status}", localisedStatus);

            if (messageForCitizen.contains("{emp_name}"))
                messageForCitizen = messageForCitizen.replace("{emp_name}", fetchUserByUUID(request.getWorkflow().getAssignes().get(0), request.getRequestInfo(), request.getIncident().getTenantId()).getName());

            if (messageForEmployee.contains("{emp_name}"))
                messageForEmployee = messageForEmployee.replace("{emp_name}", fetchUserByUUID(request.getWorkflow().getAssignes().get(0), request.getRequestInfo(), request.getIncident().getTenantId()).getName());

            if(messageForEmployee.contains("{ao_designation}")){
                String localisationMessageForPlaceholder =  notificationUtil.getLocalizationMessages(request.getIncident().getTenantId(), request.getRequestInfo(),COMMON_MODULE);
                String path = "$..messages[?(@.code==\"COMMON_MASTERS_DESIGNATION_AO\")].message";

                try {
                    ArrayList<String> messageObj = JsonPath.parse(localisationMessageForPlaceholder).read(path);
                    if(messageObj != null && messageObj.size() > 0) {
                        messageForEmployee = messageForEmployee.replace("{ao_designation}", messageObj.get(0));
                    }
                } catch (Exception e) {
                    log.warn("Fetching from localization failed", e);
                }
            }
        }

        /**
         * SMS to citizens and employee, when the complaint is re-assigned to an employee
         */
        if(incidentWrapper.getIncident().getApplicationStatus().equalsIgnoreCase(PENDING_FOR_REASSIGNMENT) && incidentWrapper.getWorkflow().getAction().equalsIgnoreCase(REASSIGN)){
            messageForCitizen = notificationUtil.getCustomizedMsg(request.getWorkflow().getAction(), applicationStatus, CITIZEN, localizationMessage);
            if (messageForCitizen == null) {
                log.info("No message Found For Citizen On Topic : " + topic);
                return null;
            }

            messageForEmployee = notificationUtil.getCustomizedMsg(request.getWorkflow().getAction(), applicationStatus, EMPLOYEE, localizationMessage);
            if (messageForEmployee == null) {
                log.info("No message Found For Employee On Topic : " + topic);
                return null;
            }

            defaultMessage = notificationUtil.getDefaultMsg(CITIZEN, localizationMessage);
            if (defaultMessage == null) {
                log.info("No default message Found For Topic : " + topic);
                return null;
            }

            if(defaultMessage.contains("{status}"))
                defaultMessage = defaultMessage.replace("{status}", localisedStatus);

            if (messageForCitizen.contains("{emp_name}"))
                messageForCitizen = messageForCitizen.replace("{emp_name}", fetchUserByUUID(request.getWorkflow().getAssignes().get(0), request.getRequestInfo(), request.getIncident().getTenantId()).getName());


            if (messageForEmployee.contains("{emp_name}"))
                messageForEmployee = messageForEmployee.replace("{emp_name}", fetchUserByUUID(request.getRequestInfo().getUserInfo().getUuid(), request.getRequestInfo(), request.getIncident().getTenantId()).getName());

            if(messageForEmployee.contains("{ao_designation}")){
                String localisationMessageForPlaceholder =  notificationUtil.getLocalizationMessages(request.getIncident().getTenantId(), request.getRequestInfo(),COMMON_MODULE);
                String path = "$..messages[?(@.code==\"COMMON_MASTERS_DESIGNATION_AO\")].message";

                try {
                    ArrayList<String> messageObj = JsonPath.parse(localisationMessageForPlaceholder).read(path);
                    if(messageObj != null && messageObj.size() > 0) {
                        messageForEmployee = messageForEmployee.replace("{ao_designation}", messageObj.get(0));
                    }
                } catch (Exception e) {
                    log.warn("Fetching from localization failed", e);
                }
            }
        }

        /**
         * SMS to citizens, when complaint got rejected with reason
         */
        if(incidentWrapper.getIncident().getApplicationStatus().equalsIgnoreCase(REJECTED) && incidentWrapper.getWorkflow().getAction().equalsIgnoreCase(REJECT)) {
            messageForCitizen = notificationUtil.getCustomizedMsg(request.getWorkflow().getAction(), applicationStatus, CITIZEN, localizationMessage);
            if (messageForCitizen == null) {
                log.info("No message Found For Citizen On Topic : " + topic);
                return null;
            }

            defaultMessage = notificationUtil.getDefaultMsg(CITIZEN, localizationMessage);
            if (defaultMessage == null) {
                log.info("No default message Found For Topic : " + topic);
                return null;
            }

            if(defaultMessage.contains("{status}"))
                defaultMessage = defaultMessage.replace("{status}", localisedStatus);

            if (messageForCitizen.contains("{additional_comments}"))
                messageForCitizen = messageForCitizen.replace("{additional_comments}", incidentWrapper.getWorkflow().getComments());
        }

        /**
         * SMS to citizens and employee, when the complaint has been re-opened on citizen request
         */
        if(incidentWrapper.getIncident().getApplicationStatus().equalsIgnoreCase(PENDINGFORASSIGNMENT) && incidentWrapper.getWorkflow().getAction().equalsIgnoreCase(PGR_WF_REOPEN)) {
            messageForCitizen = notificationUtil.getCustomizedMsg(request.getWorkflow().getAction(), applicationStatus, CITIZEN, localizationMessage);
            if (messageForCitizen == null) {
                log.info("No message Found For Citizen On Topic : " + topic);
                return null;
            }

            messageForEmployee = notificationUtil.getCustomizedMsg(request.getWorkflow().getAction(), applicationStatus, EMPLOYEE, localizationMessage);
            if (messageForEmployee == null) {
                log.info("No message Found For Employee On Topic : " + topic);
                return null;
            }

            defaultMessage = notificationUtil.getDefaultMsg(CITIZEN, localizationMessage);
            if (defaultMessage == null) {
                log.info("No default message Found For Topic : " + topic);
                return null;
            }

            ProcessInstance processInstance = getEmployeeName(incidentWrapper.getIncident().getTenantId(),incidentWrapper.getIncident().getIncidentId(),request.getRequestInfo(),ASSIGN);

            if(defaultMessage.contains("{status}"))
                defaultMessage = defaultMessage.replace("{status}", localisedStatus);

            if (messageForEmployee.contains("{emp_name}"))
                messageForEmployee = messageForEmployee.replace("{emp_name}", processInstance.getAssignes().get(0).getName());
        }

        /**
         * SMS to citizens, when complaint got resolved
         */
        if(incidentWrapper.getIncident().getApplicationStatus().equalsIgnoreCase(RESOLVED) && incidentWrapper.getWorkflow().getAction().equalsIgnoreCase(PGR_WF_RESOLVE)) {
            messageForCitizen = notificationUtil.getCustomizedMsg(request.getWorkflow().getAction(), applicationStatus, CITIZEN, localizationMessage);
            if (messageForCitizen == null) {
                log.info("No message Found For Citizen On Topic : " + topic);
                return null;
            }

            defaultMessage = notificationUtil.getDefaultMsg(CITIZEN, localizationMessage);
            if (defaultMessage == null) {
                log.info("No default message Found For Topic : " + topic);
                return null;
            }

            ProcessInstance processInstance = getEmployeeName(incidentWrapper.getIncident().getTenantId(),incidentWrapper.getIncident().getIncidentId(),request.getRequestInfo(),ASSIGN);

            if(defaultMessage.contains("{status}"))
                defaultMessage = defaultMessage.replace("{status}", localisedStatus);

            if (messageForCitizen.contains("{emp_name}"))
                messageForCitizen = messageForCitizen.replace("{emp_name}", processInstance.getAssignes().get(0).getName());
        }

        /**
         * SMS to citizens and employee, when the complaint has been re-opened on citizen request
         */
        if((incidentWrapper.getIncident().getApplicationStatus().equalsIgnoreCase(CLOSED_AFTER_RESOLUTION) ||
        		incidentWrapper.getIncident().getApplicationStatus().equalsIgnoreCase(CLOSED_AFTER_REJECTION)) &&
        		incidentWrapper.getWorkflow().getAction().equalsIgnoreCase(RATE)) {
            messageForEmployee = notificationUtil.getCustomizedMsg(request.getWorkflow().getAction(), applicationStatus, EMPLOYEE, localizationMessage);
            if (messageForEmployee == null) {
                log.info("No message Found For Employee On Topic : " + topic);
                return null;
            }

            defaultMessage = notificationUtil.getDefaultMsg(CITIZEN, localizationMessage);
            if (defaultMessage == null) {
                log.info("No default message Found For Topic : " + topic);
                return null;
            }

            ProcessInstance processInstance = getEmployeeName(incidentWrapper.getIncident().getTenantId(),incidentWrapper.getIncident().getIncidentId(),request.getRequestInfo(),ASSIGN);

            if(defaultMessage.contains("{status}"))
                defaultMessage = defaultMessage.replace("{status}", localisedStatus);


            if(messageForEmployee.contains("{rating}"))
                messageForEmployee=messageForEmployee.replace("{rating}",incidentWrapper.getIncident().getRating().toString());

            if (messageForEmployee.contains("{emp_name}"))
                messageForEmployee = messageForEmployee.replace("{emp_name}", processInstance.getAssignes().get(0).getName());
        }

        /**
         * SMS to citizens and employee, when the complaint is re-assigned to LME
         */
        if(incidentWrapper.getIncident().getApplicationStatus().equalsIgnoreCase(PENDINGATLME) && incidentWrapper.getWorkflow().getAction().equalsIgnoreCase(REASSIGN)){
            messageForCitizen = notificationUtil.getCustomizedMsg(request.getWorkflow().getAction(), applicationStatus, CITIZEN, localizationMessage);
            if (messageForCitizen == null) {
                log.info("No message Found For Citizen On Topic : " + topic);
                return null;
            }

            messageForEmployee = notificationUtil.getCustomizedMsg(request.getWorkflow().getAction(), applicationStatus, EMPLOYEE, localizationMessage);
            if (messageForEmployee == null) {
                log.info("No message Found For Employee On Topic : " + topic);
                return null;
            }

            defaultMessage = notificationUtil.getDefaultMsg(CITIZEN, localizationMessage);
            if (defaultMessage == null) {
                log.info("No default message Found For Topic : " + topic);
                return null;
            }

            if(defaultMessage.contains("{status}"))
                defaultMessage = defaultMessage.replace("{status}", localisedStatus);
            
            if (messageForCitizen.contains("{emp_name}"))
                messageForCitizen = messageForCitizen.replace("{emp_name}", fetchUserByUUID(request.getWorkflow().getAssignes().get(0), request.getRequestInfo(), request.getIncident().getTenantId()).getName());

            if (messageForEmployee.contains("{emp_name}"))
                messageForEmployee = messageForEmployee.replace("{emp_name}", fetchUserByUUID(request.getRequestInfo().getUserInfo().getUuid(), request.getRequestInfo(), request.getIncident().getTenantId()).getName());

            if(messageForEmployee.contains("{ao_designation}")){
                String localisationMessageForPlaceholder =  notificationUtil.getLocalizationMessages(request.getIncident().getTenantId(), request.getRequestInfo(),COMMON_MODULE);
                String path = "$..messages[?(@.code==\"COMMON_MASTERS_DESIGNATION_AO\")].message";

                try {
                    ArrayList<String> messageObj = JsonPath.parse(localisationMessageForPlaceholder).read(path);
                    if(messageObj != null && messageObj.size() > 0) {
                        messageForEmployee = messageForEmployee.replace("{ao_designation}", messageObj.get(0));
                    }
                } catch (Exception e) {
                    log.warn("Fetching from localization failed", e);
                }
            }
        }

        Long createdTime = incidentWrapper.getIncident().getCreatedTime();
        LocalDate date = Instant.ofEpochMilli(createdTime > 10 ? createdTime : createdTime * 1000)
                .atZone(ZoneId.systemDefault()).toLocalDate();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_PATTERN);

        String appLink = "";
        //notificationUtil.getShortnerURL(config.getMobileDownloadLink());

        if(messageForCitizen != null) {
            messageForCitizen = messageForCitizen.replace("{id}", incidentWrapper.getIncident().getIncidentId());
            messageForCitizen = messageForCitizen.replace("{date}", date.format(formatter));
            messageForCitizen = messageForCitizen.replace("{download_link}", appLink);
        }

        if(messageForEmployee != null) {
            messageForEmployee = messageForEmployee.replace("{id}", incidentWrapper.getIncident().getIncidentId());
            messageForEmployee = messageForEmployee.replace("{date}", date.format(formatter));
            messageForEmployee = messageForEmployee.replace("{download_link}", appLink);
        }


        message.put(CITIZEN, Arrays.asList(new String[] {messageForCitizen, defaultMessage}));
        message.put(EMPLOYEE, Arrays.asList(messageForEmployee));

        return message;
    }

    /**
     * Fetches User Object based on the UUID.
     *
     * @param uuidstring - UUID of User
     * @param requestInfo - Request Info Object
     * @param tenantId - Tenant Id
     * @return - Returns User object with given UUID
     */
    public User fetchUserByUUID(String uuidstring, RequestInfo requestInfo, String tenantId) {
        User userInfoCopy = requestInfo.getUserInfo();

        User userInfo = getInternalMicroserviceUser(tenantId);

        requestInfo.setUserInfo(userInfo);

        StringBuilder uri = new StringBuilder();
        uri.append(config.getUserHost()).append(config.getUserSearchEndpoint());
        Map<String, Object> userSearchRequest = new HashMap<>();
        userSearchRequest.put("RequestInfo", requestInfo);
        userSearchRequest.put("tenantId", tenantId);
        userSearchRequest.put("userType", "EMPLOYEE");
        Set<String> uuid = new HashSet<>() ;
        uuid.add(uuidstring);
        userSearchRequest.put("uuid", uuid);
        User user = null;
        try {
            LinkedHashMap<String, Object> responseMap = (LinkedHashMap<String, Object>) serviceRequestRepository.fetchResult(uri, userSearchRequest);
            List<LinkedHashMap<String, Object>> users = (List<LinkedHashMap<String, Object>>) responseMap.get("user");
            String dobFormat = "yyyy-MM-dd";
            parseResponse(responseMap,dobFormat);
            user = 	mapper.convertValue(users.get(0), User.class);

        }catch(Exception e) {
            log.error("Exception while trying parse user object: ",e);
        }

        requestInfo.setUserInfo(userInfoCopy);
        return user;
    }

    /**
     * Parses date formats to long for all users in responseMap
     * @param responeMap LinkedHashMap got from user api response
     */
    private void parseResponse(LinkedHashMap responeMap,String dobFormat){
        List<LinkedHashMap> users = (List<LinkedHashMap>)responeMap.get("user");
        String formatForDate = "dd-MM-yyyy HH:mm:ss";
        if(users!=null){
            users.forEach( map -> {
                        map.put("createdDate",dateTolong((String)map.get("createdDate"),formatForDate));
                        if((String)map.get("lastModifiedDate")!=null)
                            map.put("lastModifiedDate",dateTolong((String)map.get("lastModifiedDate"),formatForDate));
                        if((String)map.get("dob")!=null)
                            map.put("dob",dateTolong((String)map.get("dob"),dobFormat));
                        if((String)map.get("pwdExpiryDate")!=null)
                            map.put("pwdExpiryDate",dateTolong((String)map.get("pwdExpiryDate"),formatForDate));
                    }
            );
        }
    }

    /**
     * Converts date to long
     * @param date date to be parsed
     * @param format Format of the date
     * @return Long value of date
     */
    private Long dateTolong(String date,String format){
        SimpleDateFormat simpleDateFormatObject = new SimpleDateFormat(format);
        Date returnDate = null;
        try {
            returnDate = simpleDateFormatObject.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return  returnDate.getTime();
    }

    public ProcessInstance getEmployeeName(String tenantId, String IncidentId, RequestInfo requestInfo,String action){
        ProcessInstance processInstanceToReturn = new ProcessInstance();
        User userInfoCopy = requestInfo.getUserInfo();

        User userInfo = getInternalMicroserviceUser(tenantId);

        requestInfo.setUserInfo(userInfo);

        RequestInfoWrapper requestInfoWrapper = RequestInfoWrapper.builder().requestInfo(requestInfo).build();
        StringBuilder URL = workflowService.getprocessInstanceSearchURL(tenantId,IncidentId);
        URL.append("&").append("history=true");

        Object result = serviceRequestRepository.fetchResult(URL, requestInfoWrapper);
        ProcessInstanceResponse processInstanceResponse = null;
        try {
            processInstanceResponse = mapper.convertValue(result, ProcessInstanceResponse.class);
        } catch (IllegalArgumentException e) {
            throw new CustomException("PARSING ERROR", "Failed to parse response of workflow processInstance search");
        }
        if (CollectionUtils.isEmpty(processInstanceResponse.getProcessInstances()))
            throw new CustomException("WORKFLOW_NOT_FOUND", "The workflow object is not found");

        for(ProcessInstance processInstance:processInstanceResponse.getProcessInstances()){
            if(processInstance.getAction().equalsIgnoreCase(action))
                processInstanceToReturn= processInstance;
        }
        requestInfo.setUserInfo(userInfoCopy);
        return processInstanceToReturn;
    }

   
    private List<SMSRequest> enrichSmsRequest(String mobileNumber, String finalMessage) {
        List<SMSRequest> smsRequest = new ArrayList<>();
        SMSRequest req = SMSRequest.builder().mobileNumber(mobileNumber).message(finalMessage).build();
        smsRequest.add(req);
        return smsRequest;
    }

    private EventRequest enrichEventRequest(IncidentRequest request, String finalMessage) {
        String tenantId = request.getIncident().getTenantId();
        String mobileNumber = request.getIncident().getReporterr().getMobileNumber();

//        Map<String, String> mapOfPhoneNoAndUUIDs = fetchUserUUIDs(mobileNumber, request.getRequestInfo(),tenantId);
//
//        if (CollectionUtils.isEmpty(mapOfPhoneNoAndUUIDs.keySet())) {
//            log.info("UUID search failed!");
//        }

        List<Event> events = new ArrayList<>();
        List<String> toUsers = new ArrayList<>();
        toUsers.add(request.getIncident().getReporterr().getUuid());

		/*
		 * Actions action = null;
		 * if(request.getWorkflow().getAction().equals("RESOLVE")) {
		 * 
		 * List<ActionItem> items = new ArrayList<>(); String rateLink = ""; String
		 * reopenLink = ""; String rateUrl = config.getRateLink(); String reopenUrl =
		 * config.getReopenLink(); rateLink = rateUrl.replace("{application-id}",
		 * request.getIncident().getIncidentId()); reopenLink =
		 * reopenUrl.replace("{application-id}", request.getIncident().getIncidentId());
		 * rateLink = getUiAppHost(tenantId) + rateLink; reopenLink =
		 * getUiAppHost(tenantId) + reopenLink; ActionItem rateItem =
		 * ActionItem.builder().actionUrl(rateLink).code(config.getRateCode()).build();
		 * ActionItem reopenItem =
		 * ActionItem.builder().actionUrl(reopenLink).code(config.getReopenCode()).build
		 * (); items.add(rateItem); items.add(reopenItem);
		 * 
		 * action = Actions.builder().actionUrls(items).build(); }
		 */
        Recepient recepient = Recepient.builder().toUsers(toUsers).toRoles(null).build();
        events.add(Event.builder().id(UUID.randomUUID().toString()).tenantId(tenantId).description(finalMessage).eventType(USREVENTS_EVENT_TYPE)
                .name(USREVENTS_EVENT_NAME).postedBy(USREVENTS_EVENT_POSTEDBY)
                .source("WEBAPP").recepient(recepient.toString()).build());

        if (!CollectionUtils.isEmpty(events)) {
            return EventRequest.builder().requestInfo(request.getRequestInfo()).events(events).build();
        } else {
            return null;
        }
    }

    /**
     * Fetches UUIDs of CITIZEN based on the phone number.
     *
     * @param mobileNumber - Mobile Numbers
     * @param requestInfo - Request Information
     * @param tenantId - Tenant Id
     * @return Returns List of MobileNumbers and UUIDs
     */
    public Map<String, String> fetchUserUUIDs(String mobileNumber, RequestInfo requestInfo, String tenantId) {
        Map<String, String> mapOfPhoneNoAndUUIDs = new HashMap<>();
        StringBuilder uri = new StringBuilder();
        uri.append(config.getUserHost()).append(config.getUserSearchEndpoint());
        Map<String, Object> userSearchRequest = new HashMap<>();
        userSearchRequest.put("RequestInfo", requestInfo);
        userSearchRequest.put("tenantId", tenantId);
        userSearchRequest.put("userType", "CITIZEN");
        userSearchRequest.put("userName", mobileNumber);
        try {
            Object user = serviceRequestRepository.fetchResult(uri, userSearchRequest);
            if(null != user) {
                String uuid = JsonPath.read(user, "$.user[0].uuid");
                mapOfPhoneNoAndUUIDs.put(mobileNumber, uuid);
            }else {
                log.error("Service returned null while fetching user for username - "+mobileNumber);
            }
        }catch(Exception e) {
            log.error("Exception while fetching user for username - "+mobileNumber);
            log.error("Exception trace: ",e);
        }

        return mapOfPhoneNoAndUUIDs;
    }

    private User getInternalMicroserviceUser(String tenantId)
    {
        //Creating role with INTERNAL_MICROSERVICE_ROLE
        Role role = Role.builder()
                .name("Internal Microservice Role").code("INTERNAL_MICROSERVICE_ROLE")
                .tenantId(tenantId).build();

        //Creating userinfo with uuid and role of internal micro service role
        User userInfo = User.builder()
                .uuid(config.getEgovInternalMicroserviceUserUuid())
                .emptype("SYSTEM")
                .roles(Collections.singletonList(role)).id(0L).build();

        return userInfo;
    }

    public String getUiAppHost(String tenantId)
    {
        String stateLevelTenantId = centralInstanceUtil.getStateLevelTenant(tenantId);
        return config.getUiAppHostMap().get(stateLevelTenantId);
    }

}
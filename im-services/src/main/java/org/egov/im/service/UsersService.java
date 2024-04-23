package org.egov.im.service;


import static org.egov.im.util.IMConstants.USERTYPE_CITIZEN;
import static org.egov.im.util.IMConstants.USERTYPE_EMPLOYEE;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.egov.im.config.IMConfiguration;
import org.egov.im.config.WorkflowConfig;
import org.egov.im.entity.User;
import org.egov.im.entity.Users;
import org.egov.im.repository.ServiceRequestRepository;
import org.egov.im.repository.UsersRepository;
import org.egov.im.util.UserUtils;
import org.egov.im.web.contract.CreateUserRequest;
import org.egov.im.web.contract.UserDetailResponse;
import org.egov.im.web.contract.UserSearchRequest;
import org.egov.im.web.contract.UserSearchResponse;
import org.egov.im.web.models.IncidentRequest;
import org.egov.im.web.models.IncidentWrapper;
import org.egov.im.web.models.RequestInfo;
import org.egov.im.web.models.RequestSearchCriteria;
import org.egov.tracer.model.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

@org.springframework.stereotype.Service
@Slf4j
public class UsersService {


    private UserUtils userUtils;

    private IMConfiguration config;
    
    private WorkflowConfig workflowConfig;

    private ServiceRequestRepository serviceRequestRepository;
    
    private ObjectMapper mapper;
    
    private UsersRepository userRepository;

    @Autowired
    public UsersService(UserUtils userUtils,IMConfiguration config,WorkflowConfig workflowConfig,ServiceRequestRepository serviceRequestRepository,
    		ObjectMapper mapper) {
        this.userUtils = userUtils;
        this.config = config;
        this.workflowConfig=workflowConfig;
        this.serviceRequestRepository=serviceRequestRepository;
        this.mapper=mapper;
    }

    public Users save(Users user)
    {
    	return userRepository.save(user);
    }
    
    /**
     * Calls user service to enrich user from search or upsert user
     * @param request
     */
    public void callUserService(IncidentRequest request){

        if(!StringUtils.isEmpty(request.getIncident().getAccountId()))
            enrichUser(request);
        else if(request.getIncident().getReporterr()!=null)
            upsertUser(request);

    }

    /**
     * Calls user search to fetch the list of user and enriches it in serviceWrappers
     * @param serviceWrappers
     */
    public void enrichUsers(List<IncidentWrapper> incidentWrappers){

        Set<String> uuids = new HashSet<>();

        incidentWrappers.forEach(incidentWrapper -> {
            uuids.add(incidentWrapper.getIncident().getAccountId());
        });

        Map<String, User> idToUserMap = searchBulkUserCitizen(new LinkedList<>(uuids));

        incidentWrappers.forEach(incidentWrapper -> {
        	incidentWrapper.getIncident().setReporterr(idToUserMap.get(incidentWrapper.getIncident().getAccountId()));
        });

    }
    
    public Map<String,User> enrichUserss(List<String> uuids){

    
        Map<String, User> idToUserMap = searchBulkUser(new LinkedList<>(uuids));

       return idToUserMap;

    }


    /**
     * Creates or updates the user based on if the user exists. The user existance is searched based on userName = mobileNumber
     * If the there is already a user with that mobileNumber, the existing user is updated
     * @param request
     */
    private void upsertUser(IncidentRequest request){

        User user = request.getIncident().getReporterr();
        String tenantId = request.getIncident().getTenantId();
        User userServiceResponse = null;

        // Search on mobile number as user name
        UserDetailResponse userDetailResponse = searchUser(userUtils.getStateLevelTenant(tenantId),null, user.getMobileNumber());
        if (!userDetailResponse.getUser().isEmpty()) {
            User userFromSearch = userDetailResponse.getUser().get(0);
            if(!user.getName().equalsIgnoreCase(userFromSearch.getName())){
                userServiceResponse = updateUser(request.getRequestInfo(),user,userFromSearch);
            }
            else userServiceResponse = userDetailResponse.getUser().get(0);
        }
        else {
            userServiceResponse = createUser(request.getRequestInfo(),tenantId,user);
        }

        // Enrich the accountId
        request.getIncident().setAccountId(userServiceResponse.getUuid());
    }


    /**
     * Calls user search to fetch a user and enriches it in request
     * @param request
     */
    private void enrichUser(IncidentRequest request){

        RequestInfo requestInfo = request.getRequestInfo();
        String accountId = request.getIncident().getAccountId();
        String tenantId = request.getIncident().getTenantId();

        UserDetailResponse userDetailResponse = searchUser(tenantId,accountId,null);

        if(userDetailResponse.getUser().isEmpty())
            throw new CustomException("INVALID_ACCOUNTID","No user exist for the given accountId");

        else request.getIncident().setReporterr(userDetailResponse.getUser().get(0));

    }

    /**
     * Creates the user from the given userInfo by calling user service
     * @param requestInfo
     * @param tenantId
     * @param userInfo
     * @return
     */
    private User createUser(RequestInfo requestInfo,String tenantId, User userInfo) {

        userUtils.addUserDefaultFields(userInfo.getMobileNumber(),tenantId, userInfo);
     
        UserDetailResponse userDetailResponse = userUtils.userCallCreate(new CreateUserRequest(requestInfo, userInfo), "create");

        return userDetailResponse.getUser().get(0);

    }

    /**
     * Updates the given user by calling user service
     * @param requestInfo
     * @param user
     * @param userFromSearch
     * @return
     */
    private User updateUser(RequestInfo requestInfo,User user,User userFromSearch) {

        userFromSearch.setName(user.getName());

 
        UserDetailResponse userDetailResponse = userUtils.userCallUpdate(new CreateUserRequest(requestInfo, userFromSearch), "update");

        return userDetailResponse.getUser().get(0);

    }

    /**
     * calls the user search API based on the given accountId and userName
     * @param stateLevelTenant
     * @param accountId
     * @param userName
     * @return
     */
    private UserDetailResponse searchUser(String stateLevelTenant, String accountId, String userName){

        UserSearchRequest userSearchRequest =new UserSearchRequest();
        userSearchRequest.setActive(true);
        userSearchRequest.setUserType(USERTYPE_CITIZEN);
        userSearchRequest.setTenantId(stateLevelTenant);

        if(StringUtils.isEmpty(accountId) && StringUtils.isEmpty(userName))
            return null;

        if(!StringUtils.isEmpty(accountId))
            userSearchRequest.setUuid(Collections.singletonList(accountId));

        if(!StringUtils.isEmpty(userName))
            userSearchRequest.setUserName(userName);

        return userUtils.userCallSearch(userSearchRequest,"search");
        
    }
    /**
     * calls the user search API based on the given list of user uuids
     * @param uuids
     * @return
     */
    private Map<String,User> searchBulkUser(List<String> uuids){

        UserSearchRequest userSearchRequest =new UserSearchRequest();
        userSearchRequest.setActive(true);
        userSearchRequest.setUserType(USERTYPE_EMPLOYEE);


        if(!CollectionUtils.isEmpty(uuids))
            userSearchRequest.setUuid(uuids);


        UserDetailResponse userDetailResponse = userUtils.userCallSearch(userSearchRequest,"search");
        List<User> users = userDetailResponse.getUser();

        if(CollectionUtils.isEmpty(users))
            throw new CustomException("USER_NOT_FOUND","No user found for the uuids");

        Map<String,User> idToUserMap = users.stream().collect(Collectors.toMap(User::getUuid, Function.identity()));

        return idToUserMap;
    }
    
    
    private Map<String,User> searchBulkUserCitizen(List<String> uuids){

        UserSearchRequest userSearchRequest =new UserSearchRequest();
        userSearchRequest.setActive(true);
        userSearchRequest.setUserType(USERTYPE_CITIZEN);


        if(!CollectionUtils.isEmpty(uuids))
            userSearchRequest.setUuid(uuids);


        UserDetailResponse userDetailResponse = userUtils.userCallSearch(userSearchRequest,"search");
        List<User> users = userDetailResponse.getUser();

        if(CollectionUtils.isEmpty(users))
            throw new CustomException("USER_NOT_FOUND","No user found for the uuids");

        Map<String,User> idToUserMap = users.stream().collect(Collectors.toMap(User::getUuid, Function.identity()));

        return idToUserMap;
    }

    /**
     * Enriches the list of userUuids associated with the mobileNumber in the search criteria
     * @param tenantId
     * @param criteria
     */
    public void enrichUserIds(String tenantId, RequestSearchCriteria criteria){

        String mobileNumber = criteria.getMobileNumber();

        UserSearchRequest userSearchRequest =new UserSearchRequest();
        userSearchRequest.setActive(true);
        userSearchRequest.setUserType(USERTYPE_CITIZEN);
        userSearchRequest.setTenantId(tenantId);
        userSearchRequest.setMobileNumber(mobileNumber);

        UserDetailResponse userDetailResponse = userUtils.userCallSearch(userSearchRequest,"search");
        List<User> users = userDetailResponse.getUser();

        Set<String> userIds = users.stream().map(User::getUuid).collect(Collectors.toSet());
        criteria.setUserIds(userIds);
    }




    /**
     * Calls search api of user to fetch the user for the given uuid
     * @param uuids The list of uuid of the user's
     * @return OwnerInfo of the user with the given uuid
     */
    public Map<String,User> searchUser(RequestInfo requestInfo,List<String> uuids){
        UserSearchRequest userSearchRequest =new UserSearchRequest();
        userSearchRequest.setRequestInfo(requestInfo);
        userSearchRequest.setUuid(uuids);
        StringBuilder url = new StringBuilder("");
        userSearchRequest.setUserType(USERTYPE_EMPLOYEE);
        UserDetailResponse userDetailResponse = userUtils.userCallSearch(userSearchRequest,"search");
        if(CollectionUtils.isEmpty(userDetailResponse.getUser()))
            throw new CustomException("INVALID USER","No user found for the uuids: "+uuids);
        Map<String, User> idToUserMap = new HashMap<>();
        userDetailResponse.getUser().forEach(user -> {
            idToUserMap.put(user.getUuid(),user);
        });
        return idToUserMap;
    }
    
    /**
     * Returns UserDetailResponse by calling user service with given uri and object
     * @param userRequest Request object for user service
     * @param uri The address of the endpoint
     * @return Response from user service as parsed as userDetailResponse
     */
//    private UserDetailResponse userCall(Object userRequest, StringBuilder uri) {
//    	UserSearchRequest userSearchRequest =new UserSearchRequest();
//        userSearchRequest.setActive(true);
//        userSearchRequest.setUserType(USERTYPE_CITIZEN);
//
//
//        if(!CollectionUtils.isEmpty(uuids))
//            userSearchRequest.setUuid(uuids);
//
//
//        UserDetailResponse userDetailResponse = userUtils.userCallSearch(userSearchRequest,"search");
//        List<User> users = userDetailResponse.getUser();
//
//            return userDetailResponse;
//        }
//        catch(IllegalArgumentException  e)
//        {
//            throw new CustomException("IllegalArgumentException","ObjectMapper not able to convertValue in userCall");
//        }
//    }

    /**
     * Parses date formats to long for all users in responseMap
     * @param responeMap LinkedHashMap got from user api response
     */
    private void parseResponse(LinkedHashMap responeMap,String dobFormat){
        List<LinkedHashMap> users = (List<LinkedHashMap>)responeMap.get("user");
        String format1 = "dd-MM-yyyy HH:mm:ss";
        if(users!=null){
            users.forEach( map -> {
                        map.put("createdDate",dateTolong((String)map.get("createdDate"),format1));
                        if((String)map.get("lastModifiedDate")!=null)
                            map.put("lastModifiedDate",dateTolong((String)map.get("lastModifiedDate"),format1));
                        if((String)map.get("dob")!=null)
                            map.put("dob",dateTolong((String)map.get("dob"),dobFormat));
                        if((String)map.get("pwdExpiryDate")!=null)
                            map.put("pwdExpiryDate",dateTolong((String)map.get("pwdExpiryDate"),format1));
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
        SimpleDateFormat f = new SimpleDateFormat(format);
        Date d = null;
        try {
            d = f.parse(date);
        } catch (ParseException e) {
            log.error("Error while parsing user date",e);
        }
        return  d.getTime();
    }










}

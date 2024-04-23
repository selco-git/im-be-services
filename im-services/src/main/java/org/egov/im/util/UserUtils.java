package org.egov.im.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.egov.common.utils.MultiStateInstanceUtil;
import org.egov.im.config.IMConfiguration;
import org.egov.im.domain.model.UpdateRequest;
import org.egov.im.domain.model.UpdateResponse;
import org.egov.im.domain.model.UserSearchCriteria;
import org.egov.im.domain.service.UserService;
import org.egov.im.entity.Role;
import org.egov.im.entity.User;
import org.egov.im.repository.ServiceRequestRepository;
import org.egov.im.web.contract.CreateUserRequest;
import org.egov.im.web.contract.UserDetailResponse;
import org.egov.im.web.contract.UserSearchRequest;
import org.egov.im.web.contract.UserSearchResponse;
import org.egov.im.web.contract.UserSearchResponseContent;
import org.egov.im.web.models.ResponseInfo;
import org.egov.tracer.model.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class UserUtils {


    private ObjectMapper mapper;

    private ServiceRequestRepository serviceRequestRepository;

    private IMConfiguration config;

    @Autowired
    private MultiStateInstanceUtil centralInstanceUtil;
    
    @Autowired
    private UserService userService;

    @Autowired
    public UserUtils(ObjectMapper mapper, ServiceRequestRepository serviceRequestRepository, 
    		UserService userService,IMConfiguration config) {
        this.mapper = mapper;
        this.userService=userService;
        this.serviceRequestRepository = serviceRequestRepository;
        this.config = config;
    }

    /**
     * Returns UserDetailResponse by calling user service with given uri and object
     * @param userRequest Request object for user service
     * @param uri The address of the endpoint
     * @return Response from user service as parsed as userDetailResponse
     */

    public UserDetailResponse userCallCreate(CreateUserRequest userRequest, String uri) {
          
            User user = userRequest.toDomain(true);
            user.setOtpValidationMandatory(false);
            final User newUser = userService.createUser(user, userRequest.getRequestInfo());
            return createResponse(newUser);

    }
        
        
        public UserDetailResponse userCallUpdate(CreateUserRequest userRequest, String uri) {
            String dobFormat = null;
            dobFormat = "dd-MM-yyyy HH:mm:ss";
            User user = userRequest.toDomain(false);
            final User updatedUser = userService.updateWithoutOtpValidation(user, userRequest.getRequestInfo());
            parseResponse(user,dobFormat);

            return createResponse(updatedUser);
        }
        
        public UserDetailResponse userCallSearch(UserSearchRequest request, String uri) {
        	 UserSearchCriteria searchCriteria = request.toDomain();


             List<User> userModels = userService.searchUsers(searchCriteria, false, request.getRequestInfo());
            
             ResponseInfo responseInfo = ResponseInfo.builder().status(String.valueOf(HttpStatus.OK.value())).build();

            return new UserDetailResponse(responseInfo, userModels);
        }    
      

/**
 * Parses date formats to long for all users in responseMap
 * @param responeMap LinkedHashMap got from user api response
 */

    public void parseResponse(User user, String dobFormat){
        String format1 = "dd-MM-yyyy HH:mm:ss";
      
                        //user.setCreatedDate(dateTolong(user.getCreatedDate().toString(),format1));
//                        if((String)map.get("lastModifiedDate")!=null)
//                            map.put("lastModifiedDate",dateTolong((String)map.get("lastModifiedDate"),format1));
//                        if((String)map.get("dob")!=null)
//                            map.put("dob",dateTolong((String)map.get("dob"),dobFormat));
//                        if((String)map.get("pwdExpiryDate")!=null)
//                            map.put("pwdExpiryDate",dateTolong((String)map.get("pwdExpiryDate"),format1));
//                    }
//            );
//        }
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
            throw new CustomException("INVALID_DATE_FORMAT","Failed to parse date format in user");
        }
        return  d.getTime();
    }

    /**
     * enriches the userInfo with statelevel tenantId and other fields
     * @param mobileNumber
     * @param tenantId
     * @param userInfo
     */
    public void addUserDefaultFields(String mobileNumber,String tenantId, User userInfo){
        Role role = getCitizenRole(tenantId);
        userInfo.setRoles(Collections.singletonList(role));
        userInfo.setType("CITIZEN");
        userInfo.setUsername(mobileNumber);
    }

    /**
     * Returns role object for citizen
     * @param tenantId
     * @return
     */
    private Role getCitizenRole(String tenantId){
        Role role = new Role();
        role.setCode("CITIZEN");
        role.setName("Citizen");
        role.setTenantId(getStateLevelTenant(tenantId));
        return role;
    }

    public String getStateLevelTenant(String tenantId){
       /* return tenantId.split("\\.")[0];*/
        log.info("tenantId"+ tenantId);
        return centralInstanceUtil.getStateLevelTenant(tenantId);
    }
    private UserDetailResponse createResponse(User newUser) {
        ResponseInfo responseInfo = ResponseInfo.builder().status(String.valueOf(HttpStatus.OK.value())).build();
        return new UserDetailResponse(responseInfo, Collections.singletonList(newUser));
    }

    private UpdateResponse createResponseforUpdate(User newUser) {
        UpdateRequest updateRequest = new UpdateRequest(newUser);
        ResponseInfo responseInfo = ResponseInfo.builder().status(String.valueOf(HttpStatus.OK.value())).build();
        return new UpdateResponse(responseInfo, Collections.singletonList(updateRequest));
    }

}

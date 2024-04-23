package org.egov.im.web.contract;

import java.util.Collections;
import java.util.List;

import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import org.egov.im.config.UserServiceConstants;
import org.egov.im.domain.model.UserSearchCriteria;
import org.egov.im.web.models.RequestInfo;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class UserSearchRequest {

    @JsonProperty("RequestInfo")
    private RequestInfo requestInfo;

    @JsonProperty("id")
    private List<Long> id;

    @JsonProperty("uuid")
    private List<String> uuid;

    @Size(max = 64)
    @JsonProperty("userName")
    private String userName;

    @Size(max = 100)
    @JsonProperty("name")
    private String name;

    @Pattern(regexp = UserServiceConstants.PATTERN_MOBILE)
    @JsonProperty("mobileNumber")
    private String mobileNumber;

    @Size(max = 20)
    @JsonProperty("aadhaarNumber")
    private String aadhaarNumber;

    @Size(max = 10)
    @JsonProperty("pan")
    private String pan;

    @Size(max = 128)
    @JsonProperty("emailId")
    private String emailId;

    @JsonProperty("fuzzyLogic")
    private boolean fuzzyLogic;

    @JsonProperty("active")
    @Setter
    private Boolean active;

    @Pattern(regexp = UserServiceConstants.PATTERN_TENANT)
    @Size(max = 256)
    @JsonProperty("tenantId")
    private String tenantId;

    @JsonProperty("pageSize")
    private int pageSize;

    @JsonProperty("pageNumber")
    private int pageNumber = 0;

    @JsonProperty("sort")
    private List<String> sort = Collections.singletonList("name");

    @Size(max = 50)
    @JsonProperty("userType")
    private String userType;

    @JsonProperty("roleCodes")
    private List<String> roleCodes;

    public UserSearchCriteria toDomain() {
        return UserSearchCriteria.builder()
                .id(id)
                .userName(userName)
                .name(name)
                .mobileNumber(mobileNumber)
//				.pan(pan)
                .emailId(emailId)
                .fuzzyLogic(fuzzyLogic)
                .active(active)
                .limit(pageSize)
                .offset(pageNumber)
                .sort(sort)
                .type(userType)
                .tenantId(tenantId)
                .roleCodes(roleCodes)
                .uuid(uuid)
                .build();
    }
}

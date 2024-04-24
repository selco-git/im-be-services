package org.egov.im.persistence.dto;

import java.util.List;

import org.egov.im.web.models.RequestInfo;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ActionRequest {
    @JsonProperty("RequestInfo")
    private RequestInfo requestInfo;
    @JsonProperty("roleCodes")
    private List<String> roleCodes;
    @JsonProperty("tenantId")
    private String tenantId;
    @JsonProperty("actionMaster")
    private String actionMaster;
}

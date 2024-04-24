package org.egov.im.web.models.workflow;


import org.egov.im.web.models.RequestInfo;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class StatusCountRequest {

    @JsonProperty("RequestInfo")
    private RequestInfo requestInfo;

    @JsonProperty("ProcessInstanceSearchCriteria")
    private ProcessInstanceSearchCriteria processInstanceSearchCriteria;
}

package org.egov.im.web.models;

import javax.validation.Valid;

import org.egov.im.entity.Incident;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class IncidentWrapper {


    @Valid
    @NonNull
    @JsonProperty("incident")
    private Incident incident = null;

    @Valid
    @JsonProperty("workflow")
    private Workflow workflow = null;

}

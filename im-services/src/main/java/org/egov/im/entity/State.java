package org.egov.im.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.egov.im.entity.Action;
import org.egov.im.web.models.AuditDetails;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.springframework.validation.annotation.Validated;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * A Object holds the basic data for a Trade License
 */
@ApiModel(description = "A Object holds the basic data for a Trade License")
@Validated
@javax.annotation.Generated(value = "org.egov.codegen.SpringBootCodegen", date = "2018-12-04T11:26:25.532+05:30")

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
@EqualsAndHashCode(of = {"tenantId","businessServiceId","state"})
@Entity
@Table(name="eg_wf_state_v2")
public class State  implements Serializable {

	    @Id
        @Size(max=256)
        @JsonProperty("uuid")
        @Column(name="uuid")
        private String uuid;

        @Size(max=256)
        @JsonProperty("tenantId")
        @Column(name="tenantid")
        private String tenantId;
        
     
        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "businessserviceid",  referencedColumnName = "uuid",nullable = false)
        private BusinessService businessService;

        @JsonProperty("sla")
        @Column(name="sla")
        private Long sla;

        @Size(max=256)
        @JsonProperty("state")
        @Column(name="state")
        private String state;

        @Size(max=256)
        @JsonProperty("applicationStatus")
        @Column(name="applicationstatus")
        private String applicationStatus;

        @JsonProperty("docUploadRequired")
        @Column(name="docuploadrequired")
        private Boolean docUploadRequired;

        @JsonProperty("isStartState")
        @Column(name="isstartstate")
        private Boolean isStartState;

        @JsonProperty("isTerminateState")
        @Column(name="isterminatestate")
        private Boolean isTerminateState;

        @JsonProperty("isStateUpdatable")
        @Column(name="isstateupdatable")
        private Boolean isStateUpdatable;

        @JsonProperty("actions")
        @Valid
        @ElementCollection(targetClass = Action.class)
        @OneToMany(fetch = FetchType.LAZY,cascade =  CascadeType.ALL,mappedBy = "currentStatee")
        @OnDelete(action=OnDeleteAction.CASCADE)
        private List<Action> actions;

        @Transient
        private AuditDetails auditDetails;
        
        @JsonProperty("createdBy")
	    @Column(name="createdby")
        private String createdBy = null;

        @JsonProperty("lastModifiedBy")
	    @Column(name="lastmodifiedby")
        private String lastModifiedBy = null;

        @JsonProperty("createdTime")
	    @Column(name="createdtime")
        private Long createdTime = null;

        @JsonProperty("lastModifiedTime")
	    @Column(name="lastmodifiedtime")
        private Long lastModifiedTime = null;


        public State addActionsItem(Action actionsItem) {
            if (this.actions == null) {
            this.actions = new ArrayList<>();
            }
        this.actions.add(actionsItem);
        return this;
        }

}


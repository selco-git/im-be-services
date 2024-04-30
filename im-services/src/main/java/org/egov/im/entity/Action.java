package org.egov.im.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.Valid;
import javax.validation.constraints.Size;

import org.egov.im.web.models.AuditDetails;
import org.hibernate.validator.constraints.SafeHtml;
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
@Entity
@Table(name="eg_wf_action_v2")
@EqualsAndHashCode(of = {"currentState","action"})
public class Action {

        @Size(max=256)
        @Id
        @Column(name="uuid")
        @JsonProperty("uuid")
        private String uuid;
        
        
        @SafeHtml
        @JsonProperty("tenantId")
        @Column(name="tenantid")
        private String tenantId = null;

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "currentstate",  referencedColumnName = "uuid",nullable = false)
        private State currentStatee;
        
        @Transient
        @Size(max=256)
        @JsonProperty("currentState")
        private String currentState;
        
        @Size(max=256)
        @JsonProperty("action")
        @Column(name="action")
        private String action;

        @Size(max=256)
        @JsonProperty("nextState")
        @Column(name="nextstate")
        private String nextState;

        @JsonProperty("active")
        @Column(name="active")
        private Boolean active;

        
        @Column(name="roles")
        private String roles;
        
        @Transient
        @Size(max=1024)
        @JsonProperty("role")
        @Valid
        private List<String> role;
        
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


        public Action addRolesItem(String rolesItem) {
            if (this.role == null) {
            this.role = new ArrayList<>();
            }
        this.role.add(rolesItem);
        return this;
        }

}


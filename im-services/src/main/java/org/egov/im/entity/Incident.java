package org.egov.im.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import org.egov.im.annotation.CharacterConstraint;
import org.egov.im.entity.User;
import org.egov.im.web.models.Priority;
import org.hibernate.validator.constraints.SafeHtml;
import org.springframework.validation.annotation.Validated;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Instance of Service request raised for a particular service. As per extension propsed in the Service definition \&quot;attributes\&quot; carry the input values requried by metadata definition in the structure as described by the corresponding schema.  * Any one of &#39;address&#39; or &#39;(lat and lang)&#39; or &#39;addressid&#39; is mandatory 
 */
@ApiModel(description = "Instance of Service request raised for a particular service. As per extension propsed in the Service definition \"attributes\" carry the input values requried by metadata definition in the structure as described by the corresponding schema.  * Any one of 'address' or '(lat and lang)' or 'addressid' is mandatory ")
@Validated
@javax.annotation.Generated(value = "org.egov.codegen.SpringBootCodegen", date = "2020-07-15T11:35:33.568+05:30")

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "eg_incident_v2")
public class Incident {
	
	    @NotNull
        @SafeHtml
        @JsonProperty("incidentType")
		@Column(name="incidenttype")
        private String incidentType = null;
	    
	    
	    @SafeHtml
        @Id
        @JsonProperty("id")
	    @Column(name="id")
        private String id = null;
		
		@NotNull
        @SafeHtml
        @JsonProperty("requestType")
	    @Column(name="requesttype")
        private String requestType = null;
		
	    @SafeHtml
	    @JsonProperty("tenantId")
		@Transient
		private String tenantId = null;
		 
		@NotNull
        @SafeHtml
        @JsonProperty("environment")
	    @Column(name="environment")
        private String environment = null;
		
		@CharacterConstraint(size = 600)
        @JsonProperty("summary")
		@Column(name="summary")
        private String summary = null;

        @SafeHtml
        @JsonProperty("description")
	    @Column(name="description")
        private String description = null;
        
        @SafeHtml
        @JsonProperty("pendingreason")
	    @Column(name="pendingreason")
        private String pendingreason = null;
	       
        @Transient
        @ManyToOne(fetch = FetchType.LAZY ,targetEntity = User.class)
        @JoinColumn(name = "reporterr",  referencedColumnName = "uuid",nullable = false)
        @JsonProperty("reporterr")
        private User reporterr = null;		

        @SafeHtml
        @JsonProperty("linkedIssue")
	    @Column(name="linkedissue")
        private String linkedIssue = null;
		
		
        @NotNull
        @JsonProperty("priority")
	    @Column(name="priority")
        private Priority priority = Priority.LOW;
		
		@SafeHtml
        @JsonProperty("impact")
	    @Column(name="impact")
        private String impact = null;
		
		@SafeHtml
        @JsonProperty("urgency")
	    @Column(name="urgency")
        private String urgency = null;
		
		@SafeHtml
        @JsonProperty("affectedServices")
	    @Column(name="affectedservices")
        private String affectedServices = null;
		
    
        @SafeHtml
        @JsonProperty("incidentId")
	    @Column(name="incidentid")
        private String incidentId = null;


        @SafeHtml
        @JsonProperty("accountId")
	    @Column(name="accountid")
        private String accountId = null;

        @Max(5)
        @Min(1)
        @JsonProperty("rating")
	    @Column(name="rating")
        private Integer rating ;

//        @CharacterConstraint(size = 600)
//        @JsonProperty("additionalDetails")
//	    @Column(name="additionalDetails")
//        private Object additionalDetails = null;

        @SafeHtml
        @JsonProperty("applicationStatus")
	    @Column(name="applicationstatus")
        private String applicationStatus = null;

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

}

package org.egov.im.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.egov.im.web.models.AuditDetails;
import org.hibernate.validator.constraints.SafeHtml;
import org.springframework.validation.annotation.Validated;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * This object holds list of documents attached during the transaciton for a property
 */
@ApiModel(description = "This object holds list of documents attached during the transaciton for a property")
@Validated
@javax.annotation.Generated(value = "org.egov.codegen.SpringBootCodegen", date = "2020-07-15T11:35:33.568+05:30")

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name="eg_wf_document_v2")
public class Document implements Serializable{
        @SafeHtml
        @JsonProperty("id")
	    @Column(name="id")
        @Id
        private String id = null;
        
        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "processinstanceid",  referencedColumnName = "id",nullable = false)
        private ProcessInstance processInstance;
        
        
        @SafeHtml
	    @Column(name="documenttype")
        @JsonProperty("documentType")
        private String documentType = null;
        
        
        @SafeHtml
	    @Column(name="tenantid")
        @JsonProperty("tenantId")
        private String tenantId = null;

        @SafeHtml
	    @Column(name="filestoreid")
        @JsonProperty("fileStoreId")
        private String fileStoreId = null;

        @SafeHtml
	    @Column(name="documentuid")
        @JsonProperty("documentUid")
        private String documentUid = null;

        @JsonProperty("active")
	    @Column(name="active")
        private Boolean active = null;

        @Transient
        @JsonProperty("auditDetails")
        private AuditDetails auditDetails = null;
        
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


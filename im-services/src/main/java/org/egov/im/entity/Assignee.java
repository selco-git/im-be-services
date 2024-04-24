package org.egov.im.entity;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
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

import org.egov.im.entity.User;
import org.egov.im.web.models.AuditDetails;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
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
@EqualsAndHashCode(of = {"id"})
@ToString
@Entity
@Table(name="eg_wf_assignee_v2")
public class Assignee   {

        @Size(max=64)
        @JsonProperty("id")
        @Id
        @Column(name="id")
        private String id = null;
     

        @NotNull
        @Column(name="processinstanceid")
        @JsonProperty("processinstanceid")
//        @ManyToOne(fetch = FetchType.LAZY)
//        @JoinColumn(name = "processinstanceid",  referencedColumnName = "id",nullable = false)
        private String processinstanceid = null;

        @SafeHtml
        @JsonProperty("tenantId")
        @Column(name="tenantid")
        private String tenantId = null;
        
      
        @JsonProperty("assignee")
        @Column(name="assignee")
//        @ManyToOne(fetch = FetchType.LAZY)
//        @JoinColumn(name = "assignee",  referencedColumnName = "uuid",nullable = false)
        private String assignee = null;
    
        
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


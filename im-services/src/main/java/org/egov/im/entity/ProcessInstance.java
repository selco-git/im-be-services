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
@Table(name="eg_wf_processinstance_v2")
public class ProcessInstance   {

        @Size(max=64)
        @JsonProperty("id")
        @Id
        @Column(name="id")
        private String id = null;

        @NotNull
        @Size(max=128)
        @JsonProperty("businessService")
        @Column(name="businessservice")
        private String businessService = null;

        @SafeHtml
        @JsonProperty("tenantId")
        @Column(name="tenantid")
        private String tenantId = null;
        
        @NotNull
        @Size(max=128)
        @JsonProperty("businessId")
        @Column(name="businessid")
        private String businessId = null;

        @NotNull
        @Size(max=128)
        @JsonProperty("action")
        @Column(name="action")
        private String action = null;

        @NotNull
        @Size(max=64)
        @JsonProperty("moduleName")
        @Column(name="modulename")
        private String moduleName = null;

        @JsonProperty("status")
        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "status",  referencedColumnName = "uuid",nullable = false)
        private State state = null;

        @Size(max=1024)
        @JsonProperty("comment")
        @Column(name="comment")
        private String comment = null;

        @JsonProperty("documents")
        @Valid
        @OneToMany(fetch = FetchType.LAZY,cascade =  CascadeType.ALL,mappedBy = "processInstance")
        @OnDelete(action=OnDeleteAction.CASCADE)
        private List<Document> documents = null;

        @JsonProperty("assigner")
        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "assigner",  referencedColumnName = "uuid",nullable = false)
        private User assigner = null;

        @JsonProperty("assignes")
        @Transient
        private List<User> assignes = null;

        @JsonProperty("stateSla")
        @Column(name="statesla")
        private Long stateSla = null;

        @JsonProperty("businesssServiceSla")
        @Column(name="businessservicesla")
        private Long businesssServiceSla = null;

        @JsonProperty("previousStatus")
        @Size(max=128)
        @Column(name="previousstatus")
        private String previousStatus = null;


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

        @JsonProperty("rating")
        @Column(name="rating")
        private Integer rating = null;

        @JsonProperty("escalated")
        @Column(name="escalated")
        private Boolean escalated = false;


        public ProcessInstance addDocumentsItem(Document documentsItem) {
            if (this.documents == null) {
            this.documents = new ArrayList<>();
            }
            if(!this.documents.contains(documentsItem))
                this.documents.add(documentsItem);

        return this;
        }

        public ProcessInstance addUsersItem(User usersItem) {
                if (this.assignes == null) {
                        this.assignes = new ArrayList<>();
                }
                if(!this.assignes.contains(usersItem))
                        this.assignes.add(usersItem);

                return this;
        }

}


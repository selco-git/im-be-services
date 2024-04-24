package org.egov.im.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.SafeHtml;
import org.springframework.validation.annotation.Validated;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRawValue;

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
@Table(name="eg_usrevents_events")
@EqualsAndHashCode(of = {"id"})
public class Event  implements Serializable {

        @Size(max=256)
        @Id
        @Column(name="id")
        @JsonProperty("id")
        private String id;
        
        
        @SafeHtml
        @JsonProperty("tenantId")
        @Column(name="tenantid")
        private String tenantId = null;

        @Size(max=256)
        @JsonProperty("source")
        @Column(name="source")
        private String source;

        @JsonProperty("eventtype")
        @Column(name="eventtype")
        private String eventType;

        @JsonProperty("category")
        @Column(name="category")
        private String category;
        
        @JsonProperty("name")
        @Column(name="name")
        private String name;
        
        @JsonProperty("postedby")
	    @Column(name="postedby")
        private String postedBy = null;
        
        @JsonProperty("referenceid")
	    @Column(name="referenceid")
        private String referenceid = null;
        
        
        @JsonProperty("description")
	    @Column(name="description")
        private String description = null;
        
        @JsonProperty("status")
	    @Column(name="status")
        private String status = null;

        @JsonProperty("createdBy")
	    @Column(name="createdby")
        private String createdBy = null;
        
        
        @Column(name = "recepient", columnDefinition = "json")
        @JsonRawValue
        private String recepient = null;
       

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


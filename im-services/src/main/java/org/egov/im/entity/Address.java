package org.egov.im.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Table;

import org.egov.im.annotation.CharacterConstraint;
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
 * Representation of a address. Indiavidual APIs may choose to extend from this using allOf if more details needed to be added in their case. 
 */
@ApiModel(description = "Representation of a address. Indiavidual APIs may choose to extend from this using allOf if more details needed to be added in their case. ")
@Validated
@javax.annotation.Generated(value = "org.egov.codegen.SpringBootCodegen", date = "2020-07-15T11:35:33.568+05:30")

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Address   {
	
	
	  	@SafeHtml
      	@JsonProperty("id")
	  	@Id
      	@Column(name="id")
	  	private String id = null;
	
      
        @SafeHtml
        @JsonProperty("doorNo")
        @Column(name="doorNo")
        private String doorNo = null;

        @SafeHtml
        @JsonProperty("plotNo")
        @Column(name="plotNo")
        private String plotNo = null;
        
        @SafeHtml
        @JsonProperty("tenantId")
        @Column(name="tenantId")
        private String tenantId = null;

        @SafeHtml
        @JsonProperty("buildingName")
        @Column(name="buildingName")
        private String buildingName = null;

        @SafeHtml
        @JsonProperty("street")
        @Column(name="street")
        private String street = null;

     

        @SafeHtml
        @JsonProperty("landmark")
        @Column(name="landmark")
        private String landmark = null;

        @SafeHtml
        @JsonProperty("city")
        @Column(name="city")
        private String city = null;

        @SafeHtml
        @JsonProperty("district")
        @Column(name="district")
        private String district = null;

        @SafeHtml
        @JsonProperty("region")
        @Column(name="region")
        private String region = null;

        @SafeHtml
        @JsonProperty("state")
        @Column(name="state")
        private String state = null;

        @SafeHtml
        @JsonProperty("country")
        @Column(name="country")
        private String country = null;

        @SafeHtml
        @JsonProperty("pincode")
        @Column(name="pincode")
        private String pincode = null;

       

}
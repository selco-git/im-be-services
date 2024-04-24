package org.egov.im.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.commons.lang3.StringUtils;
import org.egov.im.domain.model.enums.AddressType;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name="eg_user_address")
@EqualsAndHashCode(of = {"id"})
public class Address {
	
	@JsonProperty("pinCode")
	@Column(name="pincode")
    private String pinCode;
	
	@JsonProperty("city")
	@Column(name="city")
    private String city;
    
    
    @JsonProperty("address")
    @Column(name="address")
    private String address;
    
    @JsonProperty("version")
    @Column(name="version")
    private String version;
    
    @JsonProperty("type")
    @Column(name="type")
    private AddressType type;
    
    @JsonProperty("id")
    @Id
    @Column(name="id")
    private Long id;
    
    @JsonProperty("tenantId")
    @Column(name="tenantid")
    private String tenantId;
    
    @JsonProperty("userid")
    @Column(name="userid")
    private Long userid;
    
    @Transient
    private String userId;
    
   @Transient
    private String addressType;
   
    @JsonProperty("lastmodifiedby")
    @Column(name="lastmodifiedby")
    private Long LastModifiedBy;
    
    @JsonProperty("lastmodifieddate")
    @Column(name="lastmodifieddate")
    private Date LastModifiedDate;

    boolean isInvalid() {
        return isPinCodeInvalid()
                || isCityInvalid()
                || isAddressInvalid();
    }

    boolean isNotEmpty() {
        return StringUtils.isNotEmpty(pinCode)
                || StringUtils.isNotEmpty(city)
                || StringUtils.isNotEmpty(address);
    }

    boolean isPinCodeInvalid() {
        return pinCode != null && pinCode.length() > 10;
    }

    boolean isCityInvalid() {
        return city != null && city.length() > 300;
    }

    boolean isAddressInvalid() {
        return address != null && address.length() > 300;
    }
}

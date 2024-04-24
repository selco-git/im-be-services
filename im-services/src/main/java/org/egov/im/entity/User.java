package org.egov.im.entity;

import static org.springframework.util.ObjectUtils.isEmpty;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import org.apache.commons.lang3.time.DateUtils;
import org.egov.im.config.UserServiceConstants;
import org.egov.im.domain.exception.InvalidUserCreateException;
import org.egov.im.domain.exception.InvalidUserUpdateException;
import org.egov.im.domain.model.OtpValidationRequest;
import org.egov.im.domain.model.enums.BloodGroup;
import org.egov.im.domain.model.enums.Gender;
import org.egov.im.domain.model.enums.GuardianRelation;
import org.egov.im.domain.model.enums.UserType;
import org.hibernate.validator.constraints.Email;
import org.springframework.util.CollectionUtils;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@AllArgsConstructor
@Getter
@Setter
@ToString
@Builder(toBuilder = true)
@NoArgsConstructor
@Entity
@Table(name="eg_user")
public class User {

	@Column(name="id")
	@Id
    private Long id;
	
	@Column(name="uuid")
    private String uuid;

	@Column(name="tenantid")
    @Pattern(regexp = UserServiceConstants.PATTERN_TENANT)
    @Size(max = 50)
    private String tenantId;
	
	@Column(name="username")
    private String username;
	
	@Column(name="title")	
    private String title;
	
	@Column(name="password")
    private String password;
	
	@Column(name="salutation")
    private String salutation;

	@Column(name="guardian")
    @Pattern(regexp = UserServiceConstants.PATTERN_NAME)
    private String guardian;
	
	@Column(name="guardianrelation")
    private GuardianRelation guardianRelation;

    @Pattern(regexp = UserServiceConstants.PATTERN_NAME)
    @Size(max = 50)
	@Column(name="name")
    private String name;
    
	@Column(name="gender")
    private Gender gender;
	
	@Column(name="mobilenumber")
    private String mobileNumber;

    @Email
	@Column(name="emailid")
    private String emailId;
    
	@Column(name="altcontactnumber")
    private String altContactNumber;
	
	@Column(name="pan")
    private String pan;
	
	@Column(name="aadhaarnumber")
    private String aadhaarNumber;
	
	 @NotNull
     @Valid
     @Transient
     @JsonProperty("permanentAddress")
//	 @OneToOne(fetch = FetchType.LAZY,cascade =  CascadeType.ALL,mappedBy = "user")
//     @OnDelete(action=OnDeleteAction.CASCADE)
    private Address permanentAddress;
	
	@NotNull
    @Valid
    @Transient
    @JsonProperty("correspondenceAddress")
//    @OneToOne(fetch = FetchType.LAZY,cascade =  CascadeType.ALL,mappedBy = "user")
//    @OnDelete(action=OnDeleteAction.CASCADE)
    private Address correspondenceAddress;
	
	
//	@Transient
//	private String permanentCity;
//	
//	@Transient
//	private String permanentPinCode;
//	
//	@Transient
//	private String permanentAddress;
//	
//	@Transient
//	private String correspondenceCity;
//	
//	@Transient
//	private String correspondencePinCode;
//	
//	@Transient
//	private String correspondenceAddress;
	
	@Column(name="active")
    private Boolean active;

	@Transient
	private List<Role> roles;
	
    @Column(name="dob")
    private Date dob;
    
	@Column(name="pwdexpirydate")
    private Date passwordExpiryDate;
	
	@Column(name="locale")
    private String locale = "en_IN";
	
	@Column(name="types")
    @JsonProperty("type")
    private String type;
	
	@Column(name="bloodgroup")
    private String bloodGroup;
	
	@Column(name="identificationmark")
    private String identificationMark;
	
	@Column(name="signature")
    private String signature;
	
	@Column(name="photo")
    private String photo;
	
	@Column(name="accountlocked")
    private Boolean accountLocked;
	
	@Column(name="accountlockeddate")
    private Long accountLockedDate;
	
	@Column(name="lastmodifieddate")
    private Date lastModifiedDate;
	
	@Column(name="createddate")
    private Date createdDate;
	
	@Transient
	private String otpReference;
	
	@Column(name="createdby")
    private Long createdBy;
	
	@Column(name="lastmodifiedby")
    private Long lastModifiedBy;
	
	@Transient
    private Long loggedInUserId;
	
	@Transient
    private String loggedInUserUuid;
	
	@Transient
    private boolean otpValidationMandatory;
	
	@Transient
    private boolean mobileValidationMandatory = true;
	
	@Column(name="alternatemobilenumber")
    private String alternateMobileNumber;

    

    public User addRolesItem(Role roleItem) {
        if (this.roles == null) {
            this.roles = new ArrayList<>();
        }
        this.roles.add(roleItem);
        return this;
    }

    public void validateNewUser() {
        validateNewUser(true);
    }

    public void validateNewUser(boolean createUserValidateName) {
        if (isUsernameAbsent()
                || (createUserValidateName && isNameAbsent())
                || isMobileNumberAbsent()
                || isActiveIndicatorAbsent()
                || isTypeAbsent()
                || isPermanentAddressInvalid()
                || isCorrespondenceAddressInvalid()
                || isRolesAbsent()
                || isOtpReferenceAbsent()
                || isTenantIdAbsent()) {
            throw new InvalidUserCreateException(this);
        }
    }

    public void validateUserModification() {
        if (isPermanentAddressInvalid()
                || isCorrespondenceAddressInvalid()
                || isTenantIdAbsent()
        ) {
            throw new InvalidUserUpdateException(this);
        }
    }

    @JsonIgnore
    public boolean isCorrespondenceAddressInvalid() {
        return correspondenceAddress != null && correspondenceAddress.isInvalid();
    }

    @JsonIgnore
    public boolean isPermanentAddressInvalid() {
        return permanentAddress != null && permanentAddress.isInvalid();
    }

    @JsonIgnore
    public boolean isOtpReferenceAbsent() {
        return otpValidationMandatory && isEmpty(otpReference);
    }

    @JsonIgnore
    public boolean isTypeAbsent() {
        return isEmpty(type);
    }

    @JsonIgnore
    public boolean isActiveIndicatorAbsent() {
        return isEmpty(active);
    }

    @JsonIgnore
    public boolean isMobileNumberAbsent() {
        return mobileValidationMandatory && isEmpty(mobileNumber);
    }

    @JsonIgnore
    public boolean isNameAbsent() {
        return isEmpty(name);
    }

    @JsonIgnore
    public boolean isUsernameAbsent() {
        return isEmpty(username);
    }

    @JsonIgnore
    public boolean isTenantIdAbsent() {
        return isEmpty(tenantId);
    }

    @JsonIgnore
    public boolean isPasswordAbsent() {
        return isEmpty(password);
    }

    @JsonIgnore
    public boolean isRolesAbsent() {
        return CollectionUtils.isEmpty(roles) || roles.stream().anyMatch(r -> isEmpty(r.getCode()));
    }

    @JsonIgnore
    public boolean isIdAbsent() {
        return id == null;
    }

    public void nullifySensitiveFields() {
        username = null;
        type = null;
        mobileNumber = null;
        password = null;
        passwordExpiryDate = null;
        roles = null;
        accountLocked = null;
        accountLockedDate = null;
    }

    @JsonIgnore
    public boolean isLoggedInUserDifferentFromUpdatedUser() {
        return !id.equals(loggedInUserId) || !uuid.equals(loggedInUserUuid);
    }

    public void setRoleToCitizen() {
        type = UserType.CITIZEN.toString();
        roles = Collections.singletonList(Role.getCitizenRole());
    }

    public void updatePassword(String newPassword) {
        password = newPassword;
    }

    @JsonIgnore
    public OtpValidationRequest getOtpValidationRequest() {
        return OtpValidationRequest.builder()
                .mobileNumber(mobileNumber)
                .tenantId(tenantId)
                .otpReference(otpReference)
                .build();
    }

    @JsonIgnore
    public List<Address> getPermanentAndCorrespondenceAddresses() {
        final ArrayList<Address> addresses = new ArrayList<>();
        if (correspondenceAddress != null && correspondenceAddress.isNotEmpty()) {
            addresses.add(correspondenceAddress);
        }
        if (permanentAddress != null && permanentAddress.isNotEmpty()) {
            addresses.add(permanentAddress);
        }
        return addresses;
    }

    public void setDefaultPasswordExpiry(int expiryInDays) {
        if (passwordExpiryDate == null) {
            passwordExpiryDate = DateUtils.addDays(new Date(), expiryInDays);
        }
    }

    public void setActive(boolean isActive) {
        active = isActive;
    }
    
    @JsonIgnore
    public User toDomain(Long loggedInUserId, String loggedInUserUuid, boolean isCreate) {
        BloodGroup bloodGroup = null;
        try {
            if (this.bloodGroup != null)
                bloodGroup = BloodGroup.valueOf(this.bloodGroup.toUpperCase());
        } catch (Exception e) {
            bloodGroup = BloodGroup.fromValue(this.bloodGroup);
        }
        return User.builder()
                .uuid(this.uuid)
                .id(this.id)
                .name(this.name)
                .username(this.username)
                .salutation(this.salutation)
                .mobileNumber(this.mobileNumber)
                .emailId(this.emailId)
                .altContactNumber(this.altContactNumber)
                .pan(this.pan)
                .aadhaarNumber(this.aadhaarNumber)
                .active(isActive(isCreate))
                .dob(this.dob)
                .passwordExpiryDate(this.passwordExpiryDate)
                .locale(this.locale)
                .type(this.type)
                .accountLocked(isAccountLocked(isCreate))
                .accountLockedDate(this.accountLockedDate)
                .signature(this.signature)
                .photo(this.photo)
                .identificationMark(this.identificationMark)
                .gender(this.gender != null ? Gender.valueOf(this.gender.toString().toUpperCase()) : null)
                .bloodGroup(this.bloodGroup)
                .lastModifiedDate(new Date())
                .createdDate(new Date())
                .otpReference(this.otpReference)
                .tenantId(this.tenantId)
                .password(this.password)
                .roles(this.roles)
                .loggedInUserId(loggedInUserId)
                .loggedInUserUuid(loggedInUserUuid)
                .permanentAddress(this.permanentAddress)
                .correspondenceAddress(this.correspondenceAddress)
                .guardian(this.guardian)
                .guardianRelation(this.guardianRelation).alternateMobileNumber(this.alternateMobileNumber)
                .build();
    }
    
    
    private Boolean isActive(boolean isCreate) {
       
        return this.active;
    }

    private Boolean isAccountLocked(boolean isCreate) {
        if (this.accountLocked == null && isCreate) {
            return false;
        }
        return this.accountLocked;
    }

//    private Address toDomainPermanentAddress() {
//        return Address.builder()
//                .type(AddressType.PERMANENT)
//                .city(permanentCity)
//                .pinCode(permanentPinCode)
//                .address(permanentAddress)
//                .build();
//    }
//
//    private Address toDomainCorrespondenceAddress() {
//        return Address.builder()
//                .type(AddressType.CORRESPONDENCE)
//                .city(correspondenceCity)
//                .pinCode(correspondencePinCode)
//                .address(correspondenceAddress)
//                .build();
//    }




}



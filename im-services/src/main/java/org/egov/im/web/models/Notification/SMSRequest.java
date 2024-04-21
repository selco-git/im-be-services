package org.egov.im.web.models.Notification;

import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import org.egov.im.web.models.Category;
import org.egov.im.web.models.Sms;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;


@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class SMSRequest {
	  @Pattern(regexp = "^[0-9]{10}$", message = "MobileNumber should be 10 digit number")
	    private String mobileNumber;

	    @Size(max = 1000)
	    private String message;
	    private Category category;
	    private Long expiryTime;

	    //Unused for future upgrades
	    private String locale;
	    private String tenantId;
	    private String email;
	    private String[] users;
	    private String templateId;

	    public Sms toDomain() {
	        if (category == null) {
	        	return new Sms(mobileNumber, message, null, expiryTime, templateId);
	        } else {
	        	return new Sms(mobileNumber, message, null, expiryTime, templateId);
	        }
	    }
}

package org.egov.im.web.models;
import static org.apache.commons.lang3.StringUtils.isNotEmpty;

import java.util.Locale.Category;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
@Getter
@AllArgsConstructor
@EqualsAndHashCode
@ToString
@Setter
public class Sms {
    private String mobileNumber;
    private String message;
    private Category category;
    private Long expiryTime;
    private String templateId;
    public boolean isValid() {
        return isNotEmpty(mobileNumber) && isNotEmpty(message);
    }
}
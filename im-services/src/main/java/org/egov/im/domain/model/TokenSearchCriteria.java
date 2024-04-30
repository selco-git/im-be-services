package org.egov.im.domain.model;

import static org.springframework.util.StringUtils.isEmpty;

import org.egov.im.domain.exception.InvalidTokenSearchCriteriaException;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@AllArgsConstructor
@EqualsAndHashCode
public class TokenSearchCriteria {
    private String uuid;
    private String tenantId;

    public void validate() {
        if (isIdAbsent() || isTenantIdAbsent()) {
            throw new InvalidTokenSearchCriteriaException(this);
        }
    }

    public boolean isIdAbsent() {
        return isEmpty(uuid);
    }

    public boolean isTenantIdAbsent() {
        return isEmpty(tenantId);
    }
}

package org.egov.im.domain.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TokenWrapper {

    @JsonProperty("access_token")
    private String accessToken;

}

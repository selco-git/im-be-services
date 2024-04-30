package org.egov.im.domain.service;

import java.util.UUID;
import static org.apache.commons.lang3.RandomStringUtils.randomNumeric;

import org.apache.commons.lang3.StringUtils;
import org.egov.im.domain.exception.InvalidAccessTokenException;
import org.egov.im.domain.exception.TokenValidationFailureException;
import org.egov.im.domain.model.SecureUser;
import org.egov.im.domain.model.Token;
import org.egov.im.domain.model.TokenRequest;
import org.egov.im.domain.model.TokenSearchCriteria;
import org.egov.im.domain.model.Tokens;
import org.egov.im.domain.model.UserDetail;
import org.egov.im.domain.model.ValidateRequest;
import org.egov.im.repository.TokenRepository;
import org.egov.im.util.OtpConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class TokenService {

    private TokenStore tokenStore;


    @Value("${roles.state.level.enabled}")
    private boolean isRoleStateLevel;



    private TokenRepository tokenRepository;

    private OtpConfiguration otpConfiguration;

    private PasswordEncoder passwordEncoder;

    @Autowired
    public TokenService(TokenRepository tokenRepository, PasswordEncoder passwordEncoder, OtpConfiguration otpConfiguration,TokenStore tokenStore) {
        this.tokenRepository = tokenRepository;
        this.passwordEncoder = passwordEncoder;
        this.otpConfiguration = otpConfiguration;
		        this.tokenStore = tokenStore;

    }

    public Token create(TokenRequest tokenRequest) {
        tokenRequest.validate();

        String originalOtp = randomNumeric(otpConfiguration.getOtpLength());
        String encryptedOtp = originalOtp;

        if (otpConfiguration.isEncryptOTP()){
            encryptedOtp = passwordEncoder.encode(originalOtp);
        }

        Token token = Token.builder().uuid(UUID.randomUUID().toString()).tenantId(tokenRequest.getTenantId())
                .identity(tokenRequest.getIdentity()).number(encryptedOtp)
                .timeToLiveInSeconds(otpConfiguration.getTtl()).build();
        token = tokenRepository.save(token);
        token.setNumber(originalOtp);
        return token;
    }

    public Token validate(ValidateRequest validateRequest) {
        validateRequest.validate();

        Tokens tokens = tokenRepository.findByIdentityAndTenantId(validateRequest);

        if (tokens == null || tokens.getTokens().isEmpty())
            throw new TokenValidationFailureException();

        for (Token t: tokens.getTokens()) {

            if (!otpConfiguration.isEncryptOTP() && validateRequest.getOtp().equalsIgnoreCase(t.getNumber())
             || (otpConfiguration.isEncryptOTP()  && passwordEncoder.matches(validateRequest.getOtp(), t.getNumber()))) {
                tokenRepository.markAsValidated(t);
                return t;
            }
        }
        throw new TokenValidationFailureException();
    }

    public Token search(TokenSearchCriteria searchCriteria) {
        return tokenRepository.findBy(searchCriteria);
    }


    /**
     * Get UserDetails By AccessToken
     *
     * @param accessToken
     * @return
     */
    public UserDetail getUser(String accessToken) {
        if (StringUtils.isEmpty(accessToken)) {
            throw new InvalidAccessTokenException();
        }

        OAuth2Authentication authentication = tokenStore.readAuthentication(accessToken);

        if (authentication == null) {
            throw new InvalidAccessTokenException();
        }

        SecureUser secureUser = ((SecureUser) authentication.getPrincipal());
        return new UserDetail(secureUser, null);
//		String tenantId = null;
//		if (isRoleStateLevel && (secureUser.getTenantId() != null && secureUser.getTenantId().contains(".")))
//			tenantId = secureUser.getTenantId().split("\\.")[0];
//		else
//			tenantId = secureUser.getTenantId();
//
//		List<Action> actions = actionRestRepository.getActionByRoleCodes(secureUser.getRoleCodes(), tenantId);
//		log.info("returning STATE-LEVEL roleactions for tenant: "+tenantId);
//		return new UserDetail(secureUser, actions);
    }
}
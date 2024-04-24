package org.egov.im;

import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import org.cache2k.extra.spring.SpringCache2kCacheManager;
import org.egov.common.utils.MultiStateInstanceUtil;
import org.egov.im.web.security.CustomAuthenticationKeyGenerator;
import org.egov.tracer.config.TracerConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.redis.RedisTokenStore;
import org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import redis.clients.jedis.JedisShardInfo;

import org.springframework.http.MediaType;

@SpringBootApplication(scanBasePackages={
		"org.egov.im", "org,egov.im.service"})
@EnableCaching
@EnableJpaRepositories
@ComponentScan({"org.egov.im","org.egov.im.service"})
@EntityScan(basePackages="org.egov.im.entity")
@EnableConfigurationProperties
@Import({TracerConfiguration.class, MultiStateInstanceUtil.class})
public class IMApp{
	
    private static final String DATE_FORMAT = "dd-MM-yyyy HH:mm:ss";


        @Value("${app.timezone}")
        private String timeZone;

        @Value("${cache.expiry.workflow.minutes}")
        private int workflowExpiry;
        
        @Value("${spring.redis.host}")
        private String host;

        @Autowired
        private CustomAuthenticationKeyGenerator customAuthenticationKeyGenerator;

        
        @Bean
        public ObjectMapper objectMapper(){
            return new ObjectMapper()
                    .configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true)
                    .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                    .setTimeZone(TimeZone.getTimeZone(timeZone));
        }

        public static void main(String[] args) throws Exception {
            SpringApplication.run(IMApp.class, args);
        }

        @Bean
        @Profile("!test")
        public CacheManager cacheManager(){
            return new SpringCache2kCacheManager().addCaches(b->b.name("businessService").expireAfterWrite(workflowExpiry, TimeUnit.MINUTES)
                    .entryCapacity(10)).addCaches(b->b.name("roleTenantAndStatusesMapping").expireAfterWrite(workflowExpiry, TimeUnit.MINUTES)
                    .entryCapacity(10));
        }
        
        

        @Bean
        public WebMvcConfigurerAdapter webMvcConfigurerAdapter() {
            return new WebMvcConfigurerAdapter() {

                @Override
                public void configureContentNegotiation(ContentNegotiationConfigurer configurer) {
                    configurer.defaultContentType(MediaType.APPLICATION_JSON_UTF8);
                }
            };
        }

        @Bean
        public MappingJackson2HttpMessageConverter jacksonConverter() {
            MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
            ObjectMapper mapper = new ObjectMapper();
            mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
            mapper.configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true);
            mapper.setDateFormat(new SimpleDateFormat(DATE_FORMAT, Locale.ENGLISH));
            mapper.setTimeZone(TimeZone.getTimeZone(timeZone));
            converter.setObjectMapper(mapper);
            return converter;
        }

        @Bean
        public PasswordEncoder passwordEncoder() {
            return new BCryptPasswordEncoder();
        }

        @Bean
        public TokenStore tokenStore() {
            RedisTokenStore redisTokenStore = new RedisTokenStore(connectionFactory());
            redisTokenStore.setAuthenticationKeyGenerator(customAuthenticationKeyGenerator);
            return redisTokenStore;
        }

        @Bean
        public JedisConnectionFactory connectionFactory() {
            return new JedisConnectionFactory(new JedisShardInfo(host));
        }

        
       
    }

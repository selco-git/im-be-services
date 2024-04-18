package org.egov.im;

import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import org.cache2k.extra.spring.SpringCache2kCacheManager;
import org.egov.common.utils.MultiStateInstanceUtil;
import org.egov.tracer.config.TracerConfiguration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootApplication
@EnableCaching
@EntityScan(basePackages="org.egov.im.entity")
@Import({TracerConfiguration.class, MultiStateInstanceUtil.class})
public class IMApp{

        @Value("${app.timezone}")
        private String timeZone;

        @Value("${cache.expiry.workflow.minutes}")
        private int workflowExpiry;
        
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
        
       
    }

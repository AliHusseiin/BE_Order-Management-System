package com.ejada.oms.core.config.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Main application configuration properties
 */
@Data
@Component
@ConfigurationProperties(prefix = "app")
public class ApplicationProperties {
    
    private PaginationProperties pagination = new PaginationProperties();
    private ApiProperties api = new ApiProperties();
    private SecurityProperties security = new SecurityProperties();
    
    @Data
    public static class PaginationProperties {
        private int defaultPageSize;
        private int maxPageSize;
        private String defaultSortDirection;
        private List<String> allowedSortProperties;
    }
    
    @Data
    public static class ApiProperties {
        private String version;
        private EndpointsProperties endpoints = new EndpointsProperties();
        
        @Data
        public static class EndpointsProperties {
            private String auth;
            private String products;
            private String customers;
            private String orders;
            private String invoices;
        }
    }
    
    @Data
    public static class SecurityProperties {
        private JwtProperties jwt = new JwtProperties();
        
        @Data
        public static class JwtProperties {
            private String secret;
            private long expiration;
            private String issuer;
        }
    }
}
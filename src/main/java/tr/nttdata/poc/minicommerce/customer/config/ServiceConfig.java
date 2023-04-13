package tr.nttdata.poc.minicommerce.customer.config;

import java.util.List;
import java.util.Map;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.*;

@Configuration
@ConfigurationProperties(prefix = "mini-commerce-customer")
@Getter
@Setter
@ToString
public class ServiceConfig {
    private String msg;
    private String buildVersion;
    private Map<String, String> mailDetails;
    private List<String> activeBranches;
}

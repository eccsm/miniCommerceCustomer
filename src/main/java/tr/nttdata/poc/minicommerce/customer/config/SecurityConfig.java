package tr.nttdata.poc.minicommerce.customer.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.cors.CorsConfiguration;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.ignoringRequestMatchers("/**"));
        http.csrf(csrf -> csrf.disable());
        http.cors(cors -> cors.disable());
        http.headers(headers -> headers.frameOptions().disable());

        CorsConfiguration corsConfig = new CorsConfiguration();
        corsConfig.addAllowedOrigin("*");
        // Allow any origin
        corsConfig.addAllowedHeader("*");
        // Allow any header
        corsConfig.addAllowedMethod("*");
        // Allow any method // Disable CSRF (Cross-Site Request Forgery) protection
        http.csrf().disable().cors().configurationSource(request -> corsConfig).and().authorizeRequests().anyRequest().permitAll();

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}
  /*   @Bean
    public AbstractLdapAuthenticationProvider ldapAuthenticationProvider() {
        DefaultLdapAuthoritiesPopulator authoritiesPopulator = new DefaultLdapAuthoritiesPopulator(
                contextSource(), "ou=Groups,dc=test,dc=com");
        authoritiesPopulator.setGroupRoleAttribute("cn");
        authoritiesPopulator.setGroupSearchFilter("member={0}");

        LdapAuthenticationProvider authenticationProvider = new LdapAuthenticationProvider(
                bindAuthenticator(), authoritiesPopulator);
        authenticationProvider.setUserDetailsContextMapper(userDetailsContextMapper());
        return authenticationProvider;
    }

    @Bean
    public AuthenticationProvider activeDirectoryLdapAuthenticationProvider() {
        ActiveDirectoryLdapAuthenticationProvider provider = new ActiveDirectoryLdapAuthenticationProvider(
                "my-openldap", "ldap://hostname:389");
        provider.setConvertSubErrorCodesToExceptions(true);
        provider.setUseAuthenticationRequestCredentials(true);

        return provider;
    }

    @Bean
    public AbstractLdapAuthenticator bindAuthenticator() {
        BindAuthenticator authenticator = new BindAuthenticator(contextSource());
        authenticator
                .setUserSearch(new FilterBasedLdapUserSearch("ou=Users,dc=test,dc=com", "(cn={0})", contextSource()));
        return authenticator;
    }

    @Bean
    public DefaultSpringSecurityContextSource contextSource() {
        DefaultSpringSecurityContextSource contextSource = new DefaultSpringSecurityContextSource(
                "ldap://localhost:389");
        contextSource.setUserDn("cn=myadmin");
        contextSource.setPassword("mypassword");
        contextSource.afterPropertiesSet();
        return contextSource;
    }

    @Bean
    public UserDetailsContextMapper userDetailsContextMapper() {
        return new LdapUserDetailsMapper();
    }*/





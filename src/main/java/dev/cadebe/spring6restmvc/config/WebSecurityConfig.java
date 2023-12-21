package dev.cadebe.spring6restmvc.config;

import org.springframework.boot.actuate.autoconfigure.security.servlet.EndpointRequest;
import org.springframework.boot.actuate.health.HealthEndpoint;
import org.springframework.boot.actuate.info.InfoEndpoint;
import org.springframework.boot.actuate.metrics.MetricsEndpoint;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

import static dev.cadebe.spring6restmvc.config.SecurityRoles.*;
import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig {
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                .authorizeHttpRequests(requests ->
                        requests
                                .requestMatchers("/api/v?/customer").hasRole(ADMIN.name())
                                .requestMatchers("/api/v?/beers/**").permitAll()
                                .requestMatchers(EndpointRequest.to(HealthEndpoint.class)).hasAnyRole(ADMIN.name(), ACTUATOR.name())
                                .requestMatchers(EndpointRequest.to(MetricsEndpoint.class)).hasAnyRole(ADMIN.name(), ACTUATOR.name())
                                .requestMatchers(EndpointRequest.to(InfoEndpoint.class)).hasAnyRole(ADMIN.name(), ACTUATOR.name())
                                .requestMatchers(EndpointRequest.toAnyEndpoint()).hasRole(ADMIN.name())
                                .anyRequest().authenticated()

                )
                .httpBasic(withDefaults())
                .csrf(CsrfConfigurer::disable);

        return http.build();
    }

    @Bean
    public UserDetailsService userDetailsService(PasswordEncoder passwordEncoder) {
        UserDetails admin = User.withUsername("admin")
                .password(passwordEncoder.encode("admin"))
                .roles(ADMIN.name())
                .build();

        UserDetails user = User.withUsername("user")
                .password(passwordEncoder.encode("user"))
                .roles(USER.name())
                .build();

        UserDetails actuator = User.withUsername("actuator")
                .password(passwordEncoder.encode("actuator"))
                .roles(ACTUATOR.name())
                .build();

        return new InMemoryUserDetailsManager(admin, actuator, user);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        PasswordEncoder encoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
        return encoder;
    }
}

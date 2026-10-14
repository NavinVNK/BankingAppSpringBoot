package com.bank.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // In-Memory User Details Service for Bank Personnel and Customers
    @Bean
    public UserDetailsService userDetailsService(PasswordEncoder passwordEncoder) {
        UserDetails clerk = User.builder()
                .username("clerk1")
                .password(passwordEncoder.encode("clerkPass123"))
                .roles("CLERK")
                .build();

        UserDetails admin = User.builder()
                .username("admin1")
                .password(passwordEncoder.encode("adminPass123"))
                .roles("ADMIN", "CLERK")
                .build();

        UserDetails customer = User.builder()
                .username("customer1")
                .password(passwordEncoder.encode("customerPass123"))
                .roles("CUSTOMER")
                .build();

        return new InMemoryUserDetailsManager(clerk, admin, customer);
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // 1. Disable CSRF for stateless REST APIs
            .csrf(csrf -> csrf.disable())

            // 2. Set Stateless Session Management (Basic Auth standard)
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

            // 3. Define URL-based Authorization Rules
            .authorizeHttpRequests(auth -> auth
                // Allow access to H2 Console if enabled
                .requestMatchers("/h2-console/**").permitAll()

                // Account Creation: CLERK and ADMIN only
                .requestMatchers(HttpMethod.POST, "/api/v1/accounts").hasAnyRole("CLERK", "ADMIN")

                // Account Closure: ADMIN only
                .requestMatchers(HttpMethod.DELETE, "/api/v1/accounts/**").hasRole("ADMIN")

                // Financial Operations (Credit/Debit/FD creation): CLERK and ADMIN
                .requestMatchers(HttpMethod.POST, "/api/v1/accounts/*/credit").hasAnyRole("CLERK", "ADMIN")
                .requestMatchers(HttpMethod.POST, "/api/v1/accounts/*/debit").hasAnyRole("CLERK", "ADMIN")
                .requestMatchers(HttpMethod.POST, "/api/v1/accounts/*/fixed-deposits").hasAnyRole("CLERK", "ADMIN")

                // Read Operations (View account details & FDs): CLERK, ADMIN, and CUSTOMER
                .requestMatchers(HttpMethod.GET, "/api/v1/accounts/**").hasAnyRole("CLERK", "ADMIN", "CUSTOMER")

                // Any other request must be authenticated
                .anyRequest().authenticated()
            )

            // 4. Enable HTTP Basic Authentication
            .httpBasic(Customizer.withDefaults());

        return http.build();
    }
}

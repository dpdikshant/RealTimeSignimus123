package com.signimusTask.config;


import com.signimusTask.Security.JwtAuthTokenFilter;
import com.signimusTask.Security.JwtUtils;
import com.signimusTask.service.UserDetailsServiceImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

	 @Autowired
	    private JwtUtils jwtUtils;

	    @Autowired
	    private UserDetailsServiceImpl userDetailsService;

    public SecurityConfig(JwtUtils jwtUtils, UserDetailsServiceImpl userDetailsService) {
        this.jwtUtils = jwtUtils;
        this.userDetailsService = userDetailsService;
    }

    @Bean
    public JwtAuthTokenFilter authenticationJwtTokenFilter() {
        return new JwtAuthTokenFilter(jwtUtils, userDetailsService);
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

    	  http
          .csrf(csrf -> csrf.disable())  // Disable CSRF for APIs
          .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))  // Stateless session management
          .authorizeHttpRequests(auth -> auth
              .requestMatchers("/", "/auth/login", "/auth/register", "/dashboard", "/scenario", "/sensors", "/settings", "/assistant").permitAll()
              .requestMatchers("/api/auth/**").permitAll()  // Open authentication endpoints
              .requestMatchers("/api/admin/**").hasRole("ADMIN")  // Admin access only
              .requestMatchers("/api/user/**").hasRole("USER")    // User access only
              .anyRequest().authenticated()  // Authenticate all other requests
          )
          .addFilterBefore(authenticationJwtTokenFilter(), UsernamePasswordAuthenticationFilter.class);  // Add JWT filter

      return http.build();
  }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
        AuthenticationManagerBuilder auth = http.getSharedObject(AuthenticationManagerBuilder.class);
        auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());
        return auth.build();
    }
}




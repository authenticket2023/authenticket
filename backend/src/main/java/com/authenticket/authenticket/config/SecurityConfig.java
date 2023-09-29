package com.authenticket.authenticket.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthFilter;
    private final AuthenticationProvider authenticationProvider;

    @Autowired
    public SecurityConfig(JwtAuthenticationFilter jwtAuthFilter,
                          AuthenticationProvider authenticationProvider) {
        this.jwtAuthFilter = jwtAuthFilter;
        this.authenticationProvider = authenticationProvider;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                .cors(Customizer.withDefaults())//by default use bean of corsConfigurationSource
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/admin/**").hasAuthority("ADMIN")

                        .requestMatchers("/api/aws/**").permitAll()

                        .requestMatchers(HttpMethod.GET, "/api/artist/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/artist/**").hasAuthority("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/artist/**").hasAuthority("ADMIN")

                        .requestMatchers("/api/auth/**").permitAll()

                        .requestMatchers("api/public/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/event").hasAuthority( "ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/event/featured").hasAuthority( "ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/event").hasAuthority(  "ORGANISER")
                        .requestMatchers(HttpMethod.POST, "/api/event").hasAuthority( "ORGANISER")
                        .requestMatchers(HttpMethod.PUT, "/api/event/{eventId}").hasAnyAuthority(  "ADMIN","ORGANISER")
                        .requestMatchers(HttpMethod.PUT, "/api/event/updateTicketCategory").hasAuthority(  "ORGANISER")
                        .requestMatchers(HttpMethod.PUT, "/api/event/addTicketCategory").hasAuthority(  "ORGANISER")
                        .requestMatchers(HttpMethod.PUT, "/api/event/addArtistToEvent").hasAuthority(  "ORGANISER")

                        .requestMatchers(HttpMethod.GET,"/api/event-organiser").hasAuthority("ADMIN")
                        .requestMatchers(HttpMethod.PUT,"/api/event-organiser").hasAuthority("ORGANISER")
                        .requestMatchers(HttpMethod.GET,"/api/event-organiser/{organiserId}").permitAll()
                        .requestMatchers(HttpMethod.PUT,"/api/event-organiser/delete").hasAnyAuthority("ADMIN","ORGANISER")//waiting for yikai
                        .requestMatchers(HttpMethod.DELETE,"/api/event-organiser/{organiserId}").hasAnyAuthority("ADMIN","ORGANISER")
                        .requestMatchers(HttpMethod.PUT,"/api/event-organiser/image").hasAuthority("ORGANISER")
                        .requestMatchers(HttpMethod.PUT,"/api/event-organiser/events/{organiserId}").hasAuthority("ORGANISER")

                        .requestMatchers("/api/event-type/**").hasAnyAuthority("ADMIN", "ORGANISER")

                        .requestMatchers("/api/order/**").hasAuthority("USER")

                        .requestMatchers("/api/ticket-category/**").hasAuthority("ADMIN")
                        .requestMatchers("/api/ticket/**").hasAuthority("ADMIN")

                        .requestMatchers(HttpMethod.PUT, "/api/user/updateUserProfile").hasAuthority("USER")
                        .requestMatchers(HttpMethod.PUT, "/api/user/updateUserImage").hasAuthority("USER")
                        .requestMatchers(HttpMethod.GET,"/api/user/{userId}").hasAnyAuthority("ADMIN", "USER")
                        .requestMatchers(HttpMethod.PUT, "/api/user/{userId}").hasAnyAuthority("ADMIN", "USER")
                        .requestMatchers(HttpMethod.GET, "/api/user").hasAuthority("ADMIN")

                        .requestMatchers(HttpMethod.GET, "/api/venue/**").permitAll()
                        .requestMatchers(HttpMethod.PUT,"/api/venue/update").hasAuthority("ADMIN")
                        .requestMatchers(HttpMethod.POST,"/api/venue").hasAuthority("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/venue/*").hasAuthority("ADMIN")

                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()
                        .anyRequest().authenticated())
                .sessionManagement(sess -> sess.sessionCreationPolicy(
                        SessionCreationPolicy.STATELESS
                ))
                .authenticationProvider(authenticationProvider)
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
                .httpBasic(Customizer.withDefaults());


        return http.build();
    }
}

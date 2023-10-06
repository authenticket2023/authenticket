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

    private final CustomBasicAuthenticationEntryPoint customBasicAuthenticationEntryPoint;

    @Autowired
    public SecurityConfig(JwtAuthenticationFilter jwtAuthFilter,
                          AuthenticationProvider authenticationProvider,
                          CustomBasicAuthenticationEntryPoint customBasicAuthenticationEntryPoint) {
        this.jwtAuthFilter = jwtAuthFilter;
        this.authenticationProvider = authenticationProvider;
        this.customBasicAuthenticationEntryPoint = customBasicAuthenticationEntryPoint;
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
                        .requestMatchers(HttpMethod.PUT, "/api/event/delete").hasAnyAuthority(  "ADMIN","ORGANISER")
                        .requestMatchers(HttpMethod.PUT, "/api/event/updateTicketCategory").hasAuthority(  "ORGANISER")
                        .requestMatchers(HttpMethod.PUT, "/api/event/addTicketCategory").hasAuthority(  "ORGANISER")
                        .requestMatchers(HttpMethod.PUT, "/api/event/addArtistToEvent").hasAuthority(  "ORGANISER")
                        .requestMatchers(HttpMethod.PUT, "/api/event/indicateInterest").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/event/hasTickets").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/event/isPresaleEvent").permitAll()

                        .requestMatchers(HttpMethod.GET,"/api/event-organiser").hasAuthority("ADMIN")
                        .requestMatchers(HttpMethod.PUT,"/api/event-organiser").hasAuthority("ORGANISER")
                        .requestMatchers(HttpMethod.GET,"/api/event-organiser/{organiserId}").permitAll()
                        .requestMatchers(HttpMethod.PUT,"/api/event-organiser/delete").hasAnyAuthority("ADMIN","ORGANISER")
                        .requestMatchers(HttpMethod.DELETE,"/api/event-organiser/{organiserId}").hasAnyAuthority("ADMIN","ORGANISER")
                        .requestMatchers(HttpMethod.PUT,"/api/event-organiser/image").hasAuthority("ORGANISER")
                        .requestMatchers(HttpMethod.PUT,"/api/event-organiser/events/{organiserId}").hasAuthority("ORGANISER")

                        .requestMatchers("/api/event-type/**").hasAnyAuthority("ADMIN", "ORGANISER")



                        .requestMatchers(HttpMethod.GET,"/api/order/complete/{orderId}").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/order/cancel/{orderId}").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/order/testPDF").hasAuthority("USER")
                        .requestMatchers(HttpMethod.GET, "/api/order/testPDF2").hasAuthority("USER")
                        .requestMatchers(HttpMethod.GET, "/api/order/{orderId}").hasAuthority("USER")
                        .requestMatchers(HttpMethod.GET, "/api/order/user/{userId}").hasAuthority("USER")
                        .requestMatchers(HttpMethod.GET, "/api/order").hasAuthority("USER")
                        .requestMatchers(HttpMethod.GET, "/api/order/find-user").hasAuthority("USER")
                        .requestMatchers(HttpMethod.POST, "/api/order").hasAuthority("USER")
                        .requestMatchers(HttpMethod.PUT, "/api/order").hasAuthority("USER")
                        .requestMatchers(HttpMethod.PUT, "/api/order/add-ticket").hasAuthority("USER")
                        .requestMatchers(HttpMethod.DELETE, "/api/order/{orderId}").hasAuthority("USER")


                        .requestMatchers("/api/ticket-category/**").hasAuthority("ADMIN")

                        .requestMatchers("/api/ticket/**").hasAnyAuthority("USER", "ADMIN")

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
                .httpBasic(httpBasic -> httpBasic.authenticationEntryPoint(customBasicAuthenticationEntryPoint));


        return http.build();
    }
}

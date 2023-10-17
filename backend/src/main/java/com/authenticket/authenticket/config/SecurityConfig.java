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
                        .requestMatchers("/api/v2/admin/**").hasAuthority("ADMIN")

                        .requestMatchers("/api/v2/aws/**").permitAll()

                        .requestMatchers(HttpMethod.GET, "/api/v2/artist/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/v2/artist/**").hasAuthority("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/v2/artist/**").hasAuthority("ADMIN")

                        .requestMatchers("/api/v2/auth/**").permitAll()

                        .requestMatchers("/api/v2/public/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v2/event").hasAuthority("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/v2/event/featured").hasAuthority( "ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/v2/event").hasAuthority("ORGANISER")
                        .requestMatchers(HttpMethod.POST, "/api/v2/event").hasAuthority("ORGANISER")
                        .requestMatchers(HttpMethod.PUT, "/api/v2/event/delete").hasAnyAuthority("ADMIN","ORGANISER")
                        .requestMatchers(HttpMethod.PUT, "/api/v2/event/update-artist").hasAuthority("ORGANISER")
                        .requestMatchers(HttpMethod.PUT, "/api/v2/event/interest").hasAuthority("USER")
                        .requestMatchers(HttpMethod.GET, "/api/v2/event/available").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v2/event/presale-event").permitAll()// to be review (isPresaleEvent)
                        .requestMatchers(HttpMethod.GET, "/api/v2/event/presale-status").permitAll()//to be reviewed (checkPresaleStatus)
                        .requestMatchers(HttpMethod.GET, "/api/v2/event/user-selected").hasAuthority("USER")//to be reviewed (checkIfUserSelected)
                        .requestMatchers(HttpMethod.GET, "/api/v2/event/selected-users").hasAuthority("ADMIN")//to be reviewed (getEventSelectedUsers)

                        .requestMatchers(HttpMethod.GET,"/api/v2/event-organiser").hasAuthority("ADMIN")
                        .requestMatchers(HttpMethod.PUT,"/api/v2/event-organiser").hasAuthority("ORGANISER")
                        .requestMatchers(HttpMethod.GET,"/api/v2/event-organiser/{organiserId}").permitAll()
                        .requestMatchers(HttpMethod.PUT,"/api/v2/event-organiser/delete").hasAnyAuthority("ADMIN","ORGANISER")
                        .requestMatchers(HttpMethod.PUT,"/api/v2/event-organiser/image").hasAuthority("ORGANISER")
                        .requestMatchers(HttpMethod.GET,"/api/v2/event-organiser/events/{organiserId}").hasAuthority("ORGANISER")

                        .requestMatchers(HttpMethod.GET,"/api/v2/event-type").permitAll()
                        .requestMatchers(HttpMethod.POST,"/api/v2/event-type").hasAuthority("ADMIN")//to be reviewed (saveEventType)

//                        .requestMatchers(HttpMethod.PUT,"/api/v2/order/complete/**").permitAll()
//                        .requestMatchers(HttpMethod.PUT, "/api/v2/order/cancel/**").permitAll()
                        .requestMatchers( "/api/v2/order/**").hasAnyAuthority("USER", "ADMIN") //to be reviewed (all order services)

                        .requestMatchers(HttpMethod.POST, "/api/v2/section/ticket-details").hasAuthority("USER")//to be reviewed (findTicketDetailsBySection)
                        .requestMatchers(HttpMethod.POST, "/api/v2/section").hasAuthority("ADMIN")

                        .requestMatchers("/api/v2/ticket-category/**").permitAll() //to be reviewed (all ticket category, remove all but GET)

                        .requestMatchers(HttpMethod.GET, "/api/v2/ticket").hasAuthority("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/v2/ticket/{ticketId}").hasAuthority("USER")
                        .requestMatchers(HttpMethod.POST,"/api/v2/ticket").hasAuthority("USER")

                        .requestMatchers(HttpMethod.GET, "/api/v2/user/interested-events").hasAuthority("USER")
                        .requestMatchers(HttpMethod.PUT, "/api/v2/user").hasAuthority("USER")
                        .requestMatchers(HttpMethod.PUT, "/api/v2/user/image").hasAuthority("USER")
                        .requestMatchers(HttpMethod.GET,"/api/v2/user/{userId}").hasAnyAuthority("ADMIN", "USER")
                        .requestMatchers(HttpMethod.PUT, "/api/v2/user/delete/{userId}").hasAnyAuthority("ADMIN", "USER")
                        .requestMatchers(HttpMethod.GET, "/api/v2/user").hasAuthority("ADMIN")


                        .requestMatchers(HttpMethod.GET, "/api/v2/venue/**").permitAll()
                        .requestMatchers(HttpMethod.PUT,"/api/v2/venue").hasAuthority("ADMIN")
                        .requestMatchers(HttpMethod.POST,"/api/v2/venue").hasAuthority("ADMIN")

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

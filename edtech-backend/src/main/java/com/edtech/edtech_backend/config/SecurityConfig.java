package com.edtech.edtech_backend.config;

import com.edtech.edtech_backend.security.JwtAuthenticationFilter;
import com.edtech.edtech_backend.security.jwt.AuthEntryPointJwt;
import com.edtech.edtech_backend.security.jwt.JwtUtils;
import com.edtech.edtech_backend.security.service.UserDetailsServiceImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.http.HttpMethod;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;

import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;

import org.springframework.security.config.http.SessionCreationPolicy;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    private final UserDetailsServiceImpl userDetailsService;
    private final AuthEntryPointJwt unauthorizedHandler;
    private final JwtUtils jwtUtils;

    @Autowired
    public SecurityConfig(
            UserDetailsServiceImpl userDetailsService,
            AuthEntryPointJwt unauthorizedHandler,
            JwtUtils jwtUtils) {

        this.userDetailsService = userDetailsService;
        this.unauthorizedHandler = unauthorizedHandler;
        this.jwtUtils = jwtUtils;
    }


    // =========================================================
    // JWT FILTER
    // =========================================================

    @Bean
    public JwtAuthenticationFilter authenticationJwtTokenFilter() {
        return new JwtAuthenticationFilter(
                jwtUtils,
                userDetailsService
        );
    }


    // =========================================================
    // AUTHENTICATION PROVIDER
    // =========================================================

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {

        DaoAuthenticationProvider authProvider =
                new DaoAuthenticationProvider();

        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());

        return authProvider;
    }


    // =========================================================
    // AUTHENTICATION MANAGER
    // =========================================================

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration authConfig) throws Exception {

        return authConfig.getAuthenticationManager();
    }


    // =========================================================
    // PASSWORD ENCODER
    // =========================================================

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }


    // =========================================================
    // CORS
    // =========================================================

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {

        CorsConfiguration configuration =
                new CorsConfiguration();

        configuration.setAllowedOrigins(
                Arrays.asList(
                        "http://localhost:3001",
                        "http://localhost:3000"
                )
        );

        configuration.setAllowedMethods(
                Arrays.asList(
                        "GET",
                        "POST",
                        "PUT",
                        "DELETE",
                        "OPTIONS"
                )
        );

        configuration.setAllowedHeaders(
                Arrays.asList("*")
        );

        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source =
                new UrlBasedCorsConfigurationSource();

        source.registerCorsConfiguration(
                "/**",
                configuration
        );

        return source;
    }


    // =========================================================
    // SECURITY FILTER CHAIN
    // =========================================================

    @Bean
    public SecurityFilterChain filterChain(
            HttpSecurity http) throws Exception {

        http
                .cors(cors -> {})
                .csrf(AbstractHttpConfigurer::disable)

                .exceptionHandling(exception ->
                        exception.authenticationEntryPoint(
                                unauthorizedHandler
                        )
                )

                .sessionManagement(session ->
                        session.sessionCreationPolicy(
                                SessionCreationPolicy.STATELESS
                        )
                )

                .authorizeHttpRequests(auth -> auth

                        // -------------------------------------------------
                        // OPTIONS / CORS PREFLIGHT
                        // -------------------------------------------------
                        .requestMatchers(
                                HttpMethod.OPTIONS,
                                "/**"
                        ).permitAll()


                        // -------------------------------------------------
                        // AUTH APIs
                        // -------------------------------------------------
                        .requestMatchers(HttpMethod.POST,
                                 "/api/auth/register",
                                "/api/auth/verify",
                                "/api/auth/login"
                        ).permitAll()

                        .requestMatchers(HttpMethod.OPTIONS,
                                "/api/auth/register",
                                "/api/auth/verify",
                                "/api/auth/login"
                        ).permitAll()

                        .requestMatchers(HttpMethod.GET,
                                "/api/auth/**"
                        ).permitAll()

                        .requestMatchers("/api/auth/**").permitAll()


                        // -------------------------------------------------
                        // PUBLIC CATEGORY APIs
                        // -------------------------------------------------
                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/categories/**"
                        ).permitAll()


                        // -------------------------------------------------
                        // PUBLIC COURSE APIs
                        // -------------------------------------------------
                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/courses/**"
                        ).permitAll()

                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/subscriptions/my-subscription"
                        ).hasAnyRole("STUDENT", "ADMIN")

                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/subscriptions/my-courses"
                        ).hasAnyRole("STUDENT", "ADMIN")

                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/subscriptions/course/**"
                        ).hasAnyRole("STUDENT", "ADMIN")

                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/quizzes/course/**"
                        ).hasAnyRole("STUDENT", "ADMIN")

                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/content/videos/**"
                        ).hasAnyRole("STUDENT", "ADMIN")

                        .requestMatchers(
                                HttpMethod.POST,
                                "/api/content/videos"
                        ).hasRole("ADMIN")

                        .requestMatchers(
                                HttpMethod.POST,
                                "/api/uploads/quizzes/{quizId}/submit",
                                "/api/test-papers"
                        ).hasAnyRole("STUDENT", "ADMIN")

                        // -------------------------------------------------
                        // R2 DOWNLOAD URL
                        //
                        // TEMPORARILY PUBLIC FOR TESTING
                        // -------------------------------------------------
                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/files/generate-download-url"
                        ).permitAll()


                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/courses/**",
                                "/api/videos/course/**"
                        ).permitAll()

                        // -------------------------------------------------
                        // TEST SERIES (student visible; admin managed)
                        // -------------------------------------------------
                        .requestMatchers(HttpMethod.GET, "/api/test-series", "/api/test-series/**", "/api/test-papers/**")
                        .hasAnyRole("STUDENT", "ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/students/test-series/subscriptions")
                        .hasAnyRole("STUDENT", "ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/students/test-series/subscriptions/**", "/api/admin/test-series/subscriptions/**")
                        .hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/admin/test-series/subscriptions/activate", "/api/test-series", "/api/test-series/**", "/api/test-papers/**")
                        .hasAnyRole("ADMIN","STUDENT")
                        .requestMatchers(HttpMethod.PUT, "/api/test-series/**", "/api/test-papers/**", "/api/test-paper-questions/**")
                        .hasAnyRole("ADMIN" , "STUDENT")
                        .requestMatchers(HttpMethod.DELETE, "/api/test-series/**", "/api/test-papers/**", "/api/test-paper-questions/**")
                        .hasRole("ADMIN")

                        // -------------------------------------------------
                        // EVERYTHING ELSE REQUIRES LOGIN
                        // -------------------------------------------------
                        .anyRequest().authenticated()
                );


        // Authentication provider
        http.authenticationProvider(
                authenticationProvider()
        );


        // JWT filter
        http.addFilterBefore(
                authenticationJwtTokenFilter(),
                UsernamePasswordAuthenticationFilter.class
        );


        return http.build();
    }
}
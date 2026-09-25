package com.edtech.edtech_backend.security;

import com.edtech.edtech_backend.security.jwt.JwtUtils;
import com.edtech.edtech_backend.security.service.UserDetailsServiceImpl;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    private final JwtUtils jwtUtils;
    private final UserDetailsServiceImpl userDetailsService;

    @Autowired
    public JwtAuthenticationFilter(JwtUtils jwtUtils, UserDetailsServiceImpl userDetailsService) {
        this.jwtUtils = jwtUtils;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String path = request.getRequestURI();
        if (path.startsWith("/api/auth/")) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            String authHeader = request.getHeader("Authorization");
            boolean headerPresent = StringUtils.hasText(authHeader);
            boolean bearerPresent = headerPresent && authHeader.startsWith("Bearer ");

            logger.info("Authorization header present = {} | Bearer token present = {} | request = {}",
                    headerPresent, bearerPresent, request.getRequestURI());

            if (!headerPresent) {
                logger.warn("Authorization header missing for request: {}", request.getRequestURI());
                filterChain.doFilter(request, response);
                return;
            }

            if (!bearerPresent) {
                logger.warn("Authorization header present but no Bearer token for request: {}", request.getRequestURI());
                filterChain.doFilter(request, response);
                return;
            }

            String jwt = authHeader.substring(7).trim();
            if (!StringUtils.hasText(jwt)) {
                logger.warn("Bearer token is empty for request: {}", request.getRequestURI());
                filterChain.doFilter(request, response);
                return;
            }

            boolean isValid = jwtUtils.validateJwtToken(jwt);
            logger.info("JWT validation result = {} | request = {}", isValid, request.getRequestURI());

            if (isValid) {
                String username = jwtUtils.getUserNameFromJwtToken(jwt);
                logger.info("Authenticated username = {}", username);

                UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                if (userDetails != null && !userDetails.getAuthorities().isEmpty()) {
                    logger.info("Authenticated role = {}", userDetails.getAuthorities().iterator().next().getAuthority());
                }

                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                SecurityContextHolder.getContext().setAuthentication(authentication);
                logger.info("Authentication stored in SecurityContextHolder for user: {}", username);
            }
        } catch (Exception e) {
            logger.error("Cannot set user authentication: {}", e.getMessage(), e);
        }

        filterChain.doFilter(request, response);
    }

    private String parseJwt(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (StringUtils.hasText(authHeader) && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7).trim();
        }
        return null;
    }
}

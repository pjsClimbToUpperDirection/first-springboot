package com.example.demo123.component;


import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@Component
@Order(Ordered.LOWEST_PRECEDENCE)
public class jwtRequestFilter extends OncePerRequestFilter {
    // https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/web/filter/OncePerRequestFilter.html
    private final UserDetailsService userDetailsService;
    private final jwtUtil jwtUtil;

    public jwtRequestFilter(UserDetailsService userDetailsService, jwtUtil jwtUtil) {
        this.userDetailsService = userDetailsService;
        this.jwtUtil = jwtUtil;
    }

    @Override
    public void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException, JwtException {

        final String authorizationHeader = request.getHeader("Authorization"); // 토큰 헤더

        String username = null;
        String jwt = null;

        try {
            if (authorizationHeader != null) {
                jwt = authorizationHeader;
                username = jwtUtil.extractUsername(jwt); // jwt 에서 사용자 이름 추출
            }

            // jwt 토큰에서 사용자 이름을 추출하였으나 아직 인증되지 않은 경우
            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {

                UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);

                if (jwtUtil.validateToken(jwt, userDetails)) {
                    UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(
                            userDetails, null, userDetails.getAuthorities());
                    usernamePasswordAuthenticationToken
                            .setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    // https://docs.spring.io/spring-security/site/docs/4.0.x/apidocs/org/springframework/security/core/context/SecurityContext.html
                    SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
                    logger.info("usernamePasswordAuthenticationToken was issued");
                }
            }
        } catch (JwtException | IllegalArgumentException e) {
            throw new JwtException(e.getMessage());
        } catch (Exception e) {
            throw new JwtException("unKnown Exception in jwt authentication process");
        }
        chain.doFilter(request, response);
    }
}


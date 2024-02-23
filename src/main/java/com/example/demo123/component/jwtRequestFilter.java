package com.example.demo123.component;


import com.example.demo123.data.dao.LoginStatusDao;
import com.example.demo123.service.RefreshTokenVerifier;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.sql.SQLException;

@Slf4j
@PropertySource("classpath:application.properties")
@Component
public class jwtRequestFilter extends OncePerRequestFilter {
    // https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/web/filter/OncePerRequestFilter.html
    private final UserDetailsService userDetailsService;
    private final jwtUtil jwtUtil;
    private final LoginStatusDao loginStatusDao;

    @Value("${jwt.validedPeriod}")
    private Integer validedPeriod;

    public jwtRequestFilter(UserDetailsService userDetailsService, jwtUtil jwtUtil, LoginStatusDao loginStatusDao) {
        this.userDetailsService = userDetailsService;
        this.jwtUtil = jwtUtil;
        this.loginStatusDao = loginStatusDao;
    }
    @Override
    public void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        String username = null;
        String jwt = null;


        final String authorizationHeader = request.getHeader("Authorization"); // 토큰 헤더
        final String refreshHeader = request.getHeader("refresh"); // 리프래시 토큰

        try {
            // jwt, refresh 토큰이 전부 제공되어야 이하 절차가 진행됨
            if (authorizationHeader != null && refreshHeader != null) {
                jwt = authorizationHeader;
                try {
                    username = jwtUtil.extractUsername(jwt); // jwt 에서 사용자 이름 추출
                } catch (ExpiredJwtException e) {
                    if (!jwtUtil.isTokenExpired(refreshHeader)) { // 리프래시 토큰이 만료되지 않은 경우
                        if (loginStatusDao.checkLoginStatus(refreshHeader)) { // 리프래시 토큰이 조회될 시 실행
                            username = e.getClaims().getSubject();
                            final UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                            jwt = jwtUtil.generateJwt(null, userDetails, validedPeriod);

                            response.addHeader("newJwtToken", jwt); // 응답 헤더를 통하여 새로운 jwt 전송
                        }
                    }
                }
            }

            // 사용자 이름을 추출하였으나 아직 인증되지 않은 경우
            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                usernamePasswordTokenProvider(jwt, userDetails, request);
            }
        } catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException e) {
            log.warn("Invalid JWT Token", e);
        } catch (ExpiredJwtException e) {
            // jwt 만료는 상단 try catch 에서 대응하므로 여기서는 리프래시 토큰 검증 과정에서 기한 만료로 인해 발생할지 모르는 예외를 처리한다.
            log.warn("Expired Refresh Token: ", e);
        } catch (UnsupportedJwtException e) {
            log.warn("Unsupported JWT Token: ", e);
        } catch (JwtException | IllegalArgumentException e) {
            log.warn("other JWT Exception: ", e);
        } catch (SQLException e) {
            log.warn("SqlException: ", e);
        }
        chain.doFilter(request, response);
    }

    private void usernamePasswordTokenProvider(String jwt, UserDetails userDetails, HttpServletRequest request) {
        if (jwtUtil.validateToken(jwt, userDetails)) { // 이름 추출, 만료 여부 확인
            UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(
                    userDetails, null, userDetails.getAuthorities());
            usernamePasswordAuthenticationToken
                    .setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            // https://docs.spring.io/spring-security/site/docs/4.0.x/apidocs/org/springframework/security/core/context/SecurityContext.html
            SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
            logger.info("usernamePasswordAuthenticationToken was issued");
        }
    }
}


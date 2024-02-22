package com.example.demo123.component;


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

@Slf4j
@PropertySource("classpath:application.properties")
@Component
public class jwtRequestFilter extends OncePerRequestFilter {
    // https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/web/filter/OncePerRequestFilter.html
    private final UserDetailsService userDetailsService;
    private final jwtUtil jwtUtil;
    private final RefreshTokenVerifier refreshTokenVerifier;
    private String username = null;
    private String jwt = null;

    @Value("${jwt.validedPeriod}")
    private Integer validedPeriod;

    public jwtRequestFilter(UserDetailsService userDetailsService, jwtUtil jwtUtil, RefreshTokenVerifier refreshTokenVerifier) {
        this.userDetailsService = userDetailsService;
        this.jwtUtil = jwtUtil;
        this.refreshTokenVerifier = refreshTokenVerifier;
    }
    @Override
    public void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        final String authorizationHeader = request.getHeader("Authorization"); // 토큰 헤더
        final String refreshHeader = request.getHeader("refresh"); // 리프래시 토큰


        try {
            if (authorizationHeader != null && jwt == null) {
                jwt = authorizationHeader;
                username = jwtUtil.extractUsername(jwt); // jwt 에서 사용자 이름 추출
            }

            // jwt 토큰에서 사용자 이름을 추출하였으나 아직 인증되지 않은 경우
            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);
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
        } catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException e) {
            log.warn("Invalid JWT Token", e);
        } catch (ExpiredJwtException e) { //todo 이 영역에 refresh token 관련 로직 작성
            log.warn("Expired JWT Token", e);
            // todo 요청 시 리프래시 토큰 또한 같이 보내는 메커니즘 구현 후 다시 테스트해 보기, 그 전까지는 작동이 되지 않음
            /*String issuer = refreshTokenVerifier.verification(refreshHeader);
            if (username == null && !issuer.isEmpty()) { // true 반환시 리프래시 토큰이 조회됨
                // jwt 재발급
                jwt = jwtUtil.generateJwt(null, userDetailsService.loadUserByUsername(jwtUtil.extractUsername(issuer)), validedPeriod);
                doFilterInternal(request, response, chain);
            } */
        } catch (UnsupportedJwtException e) {
            log.warn("Unsupported JWT Token", e);
        } catch (JwtException | IllegalArgumentException e) {
            log.warn("other JWT Exception", e);
        }
        // 객체 요소들에 영향을 끼치는 함수이므로 코드 수정 전과 같은 동작을 보장하려면 실행 시 마다 필드를 초기화한다.
        username = null;
        jwt = null;

        chain.doFilter(request, response);
    }
}


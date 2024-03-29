package com.example.demo123.component.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.lang.Nullable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.Map;
import java.util.function.Function;

// 토큰 생성, 검증하는 유틸리티 클래스
@Component
@PropertySource("classpath:application.properties")
public class jwtUtil {

    public jwtUtil(@Value("${jwt.secretKey}") String secret_Key) {
        this.key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret_Key));
    }

    private final SecretKey key;
    // 토큰에서 사용자 이름을 추출하는 함수
    public String extractUsername(String token) throws JwtException {
        return extractClaim(token, Claims::getSubject);
    }
    // 사용자 정보를 기반으로 JWT 토큰을 생성하는 함수
    // 주 역할을 하는 메서드는 private 접근자를 가지므로 해당 메서드는 유일한 접근 경로로서 기능이 추가될수 있다
    public String generateJwt(@Nullable Map<String, Object> claims, UserDetails userDetails, Integer validedPeriod) {
        return createToken(claims, userDetails.getUsername(), validedPeriod);
    }
    public String generateRefresh(@Nullable Map<String, Object> claims, Integer validedPeriod) {
        return createToken(claims, null, validedPeriod);
    }
    // 다목적 임시 토큰 발급, 클래임을 반드시 하나 이상 작성할 것
    public String generateTempToken(Map<String, Object> claims, String subject, Integer validedPeriod) {
        return createToken(claims, subject, validedPeriod);
    }
    public String extractSpecificClaim(String token, String keyOfClaim) {
        return Jwts.parser() // return Classes.newInstance("io.jsonwebtoken.impl.DefaultJwtParserBuilder")
                .verifyWith(key) // Sets the signature verification SecretKey used to verify all encountered JWS signatures
                .build()
                .parseSignedClaims(token) // io.jsonwebtoken.JwtException 예외 발생 가능성 (토큰이 유효하지 않은 등의 이유)
                .getPayload().get(keyOfClaim, String.class); // 본문 추출, 타 함수에서 사용
    }
    // 토큰의 유효성을 검증하는 함수 -> 최초로 외부 요청을 받아 필요한 메서드들을 사용, 검증을 수행함
    public Boolean validateToken(String token, UserDetails userDetails) throws JwtException {
        final String username = extractUsername(token); // 토큰이 유효하지 않을 시 extractAllClaims 에서 예외
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }
    // 토큰이 만료되었는지 확인하는 함수
    public Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }







    // 토큰에서 만료 일자를 추출하는 함수
    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    // 토큰에서 claim 추출 후 resolve
    private  <T> T extractClaim(String token, Function<Claims, T> claimsResolver) throws JwtException {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims); // claim 에 '해당 함수' 적용
    }

    // JWT 토큰이 포함된 요청이 들어올 시 토큰 검증을 위한 claim 의 본문을 추출하는 함수
    private Claims extractAllClaims(String token) throws JwtException {
        return Jwts.parser() // return Classes.newInstance("io.jsonwebtoken.impl.DefaultJwtParserBuilder")
                .verifyWith(key) // Sets the signature verification SecretKey used to verify all encountered JWS signatures
                .build()
                .parseSignedClaims(token) // io.jsonwebtoken.JwtException 예외 발생 가능성 (토큰이 유효하지 않은 등의 이유)
                .getPayload(); // 본문 추출, 타 함수에서 사용
    }

    // 클레임과 사용자 이름을 기반으로 JWT(JWS) 토큰을 생성하는 함수
    private String createToken(@Nullable Map<String, Object> claims, @Nullable String subject, Integer validedPeriod) {
        return Jwts.builder()

                .header()
                // 이 영역에 헤더 관련 설정 작성
                .and()
                .subject(subject)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + 1000L * validedPeriod)) // 토큰 만료 시간 설정 (현재는 100초)
                .claims(claims) // builder 패턴 메서드로 주어지지 않는 claim 설정
                .signWith(key, Jwts.SIG.HS256).compact();
    }
}

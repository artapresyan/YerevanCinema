package com.example.YerevanCinema.services.implementations;

import com.example.YerevanCinema.enums.UserRole;
import com.example.YerevanCinema.services.JwtTokenService;
import org.springframework.security.core.GrantedAuthority;
import io.jsonwebtoken.*;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Date;
import java.util.Set;
import java.util.stream.Collectors;

public class JwtTokenServiceImpl implements JwtTokenService {

    private static final String SECRET_KEY = "mySecuredKey";
    private static final String TOKEN_ID = "jwt_valid_token";

    @Override
    public String getCustomerJwtToken(String username) {
        Set<SimpleGrantedAuthority> grantedAuthorities = UserRole.CUSTOMER.getAuthorities();
        String token = Jwts
                .builder()
                .setId(TOKEN_ID)
                .setSubject(username)
                .claim("authorities",
                        grantedAuthorities.stream()
                                .map(GrantedAuthority::getAuthority)
                                .collect(Collectors.toSet()))
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 6000000))
                .signWith(SignatureAlgorithm.HS256,
                        SECRET_KEY.getBytes()).compact();

        return "Bearer " + token;
    }

    @Override
    public String getAdminJwtToken(String username) {
        Set<SimpleGrantedAuthority> grantedAuthorities = UserRole.ADMIN.getAuthorities();
        String token = Jwts
                .builder()
                .setId(TOKEN_ID)
                .setSubject(username)
                .claim("authorities",
                        grantedAuthorities.stream()
                                .map(GrantedAuthority::getAuthority)
                                .collect(Collectors.toSet()))
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 6000000))
                .signWith(SignatureAlgorithm.HS256,
                        SECRET_KEY.getBytes()).compact();

        return "Bearer " + token;
    }
}

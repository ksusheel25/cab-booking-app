package com.skumar.driver_service.security;

import com.skumar.driver_service.entity.Driver;
import com.skumar.driver_service.repository.DriverRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.crypto.SecretKey;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final DriverRepository driverRepository;

    @Value("${app.jwt.secret}")
    private String jwtSecret;

    public JwtAuthenticationFilter(DriverRepository driverRepository) {
        this.driverRepository = driverRepository;
    }

    public String getJwtSecret() {
        return jwtSecret;
    }

    public void setJwtSecret(String jwtSecret) {
        this.jwtSecret = jwtSecret;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        
        String authHeader = request.getHeader("Authorization");
        
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String jwt = authHeader.substring(7);
        SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes());

        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(jwt)
                    .getBody();

            String email = claims.getSubject();
            Optional<Driver> driverOpt = driverRepository.findByEmail(email);

            if (driverOpt.isPresent()) {
                Driver driver = driverOpt.get();
                var authorities = List.of(new SimpleGrantedAuthority(driver.getRole().name()));
                
                var authentication = new UsernamePasswordAuthenticationToken(
                    driver,
                    null,
                    authorities
                );
                
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch (Exception e) {
            // Invalid token, continue without authentication
        }

        filterChain.doFilter(request, response);
    }
}
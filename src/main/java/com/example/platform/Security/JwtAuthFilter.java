package com.example.platform.Security;

import com.example.platform.util.JwtUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    public JwtAuthFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String path = request.getServletPath();
        if (path.equals("/users/logintoken2") ||
                path.equals("/users/login") ||
                path.equals("/api/platforms/login")||
                path.equals("/api/investors/saveinvestor")

        )
        {
            filterChain.doFilter(request, response);
            return; // login endpoint'lerini JWT doğrulamasından muaf bırak
        }
        final String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response); // Token yoksa devam et
            return;
        }

        String jwtToken = authHeader.substring(7);

        try {
            Claims claims = jwtUtil.extractAllClaims(jwtToken);

            String subject = claims.getSubject(); // kullanıcı için username, platform için apiKey
            List<String> roles = claims.get("role", List.class);

            if (subject != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UsernamePasswordAuthenticationToken authToken;

                if (claims.containsKey("platformId")) {
                    List<SimpleGrantedAuthority> authorities = new ArrayList<>();
                    authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
                    if (roles != null && !roles.isEmpty()) {
                        authorities.addAll(roles.stream()
                                .map(SimpleGrantedAuthority::new)
                                .toList());
                    }
                    authToken = new UsernamePasswordAuthenticationToken(subject, null, authorities);
                } else if (roles != null && !roles.isEmpty()) {
                    authToken = new UsernamePasswordAuthenticationToken(
                            subject,
                            null,
                            roles.stream().map(SimpleGrantedAuthority::new).toList()
                    );
                } else {
                    authToken = new UsernamePasswordAuthenticationToken(subject, null, Collections.emptyList());
                }


                SecurityContextHolder.getContext().setAuthentication(authToken);
            }


        } catch (ExpiredJwtException e) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("JWT süresi dolmuş");
            return;
        } catch (SignatureException | MalformedJwtException e) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("JWT imzası geçersiz veya token hatalı");
            return;
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Geçersiz JWT");
            return;
        }

        filterChain.doFilter(request, response);
    }

}

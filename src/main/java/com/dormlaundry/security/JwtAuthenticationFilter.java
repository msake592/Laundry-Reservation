package com.dormlaundry.security;

import com.dormlaundry.service.CustomUserDetailsService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final CustomUserDetailsService userDetailsService;

    public JwtAuthenticationFilter(
            JwtService jwtService,
            CustomUserDetailsService userDetailsService
    ) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getServletPath();

        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }

        return path.equals("/")
                || path.equals("/index.html")
                || path.equals("/favicon.ico")
                || path.endsWith(".html")
                || path.endsWith(".css")
                || path.endsWith(".js")
                || path.startsWith("/css/")
                || path.startsWith("/js/")
                || path.startsWith("/images/")
                || path.equals("/auth/register")
                || path.equals("/auth/login");
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            filterChain.doFilter(request, response);
            return;
        }

        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            System.out.println("JWT FILTER: No Bearer token found for " + request.getMethod() + " " + request.getServletPath());
            filterChain.doFilter(request, response);
            return;
        }

        String jwt = authHeader.substring(7);
        String username = jwtService.extractUsername(jwt);

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);

            System.out.println("JWT FILTER: Request = " + request.getMethod() + " " + request.getServletPath());
            System.out.println("JWT FILTER: Username = " + userDetails.getUsername());
            System.out.println("JWT FILTER: Authorities = " + userDetails.getAuthorities());

            if (jwtService.isTokenValid(jwt, userDetails)) {
                System.out.println("JWT FILTER: Token is valid");

                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(
                                userDetails,
                                null,
                                userDetails.getAuthorities()
                        );

                authToken.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request)
                );

                SecurityContextHolder.getContext().setAuthentication(authToken);
                System.out.println("JWT FILTER: Authentication set = " + SecurityContextHolder.getContext().getAuthentication());
            } else {
                System.out.println("JWT FILTER: Token is NOT valid");
            }
        }

        filterChain.doFilter(request, response);
    }
}
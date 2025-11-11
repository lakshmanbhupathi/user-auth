package com.lakshman.user_auth.filter;

import com.lakshman.user_auth.entity.Session;
import com.lakshman.user_auth.entity.User;
import com.lakshman.user_auth.repository.SessionRepository;
import com.lakshman.user_auth.repository.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Optional;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    SessionRepository sessionRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    JwtTokenProvider jwtTokenProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        try {
            final String jwt = extractJwtFromRequest(request);

            if (StringUtils.hasText(jwt) && jwtTokenProvider.validateToken(jwt)) {

                final Long userId = jwtTokenProvider.getUserIdFromToken(jwt);
                Optional<User> userOpt = userRepository.findById(userId);


                final Long sessionId = jwtTokenProvider.getSessionIdFromToken(jwt);
                Optional<Session> sessionOpt = sessionRepository.findById(sessionId);

                if (userOpt.isPresent() && sessionOpt.isPresent()) {
                    Session session = sessionOpt.get();

                    // Check if session is active and not expired
                    if (session.getIsActive() && session.getExpiresAt().isAfter(LocalDateTime.now())) {
                        User user = userOpt.get();

                        // Update last accessed time
                        session.setLastAccessedAt(LocalDateTime.now());
                        sessionRepository.save(session);

                        UserDetails userDetails = org.springframework.security.core.userdetails.User
                                .withUsername(user.getUsername())
                                .password(user.getPassword())
                                .authorities("USER")
                                .build();

                        UsernamePasswordAuthenticationToken authentication =
                                new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                        // Store userId in request attribute for later use
                        request.setAttribute("userId", userId);

                        SecurityContextHolder.getContext().setAuthentication(authentication);
                    }
                }
            }

        } catch (Exception e) {
            logger.error("unauthenticated request, skipping auth context population");
        }

        filterChain.doFilter(request, response);
    }

    private String extractJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}

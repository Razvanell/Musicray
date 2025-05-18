package razvanell.musicrays.security;

import jakarta.annotation.Nonnull;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import razvanell.musicrays.model.user.UserRepository;

import java.io.IOException;

/**
 * This class identifies the token in the request and validates it
 */
@Component
@AllArgsConstructor
public class JwtTokenFilter extends OncePerRequestFilter {

    private final JwtTokenUtil jwtTokenUtil;
    private final UserRepository userRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, @Nonnull HttpServletResponse response, @Nonnull FilterChain chain) throws ServletException, IOException {
        // Get authorization header and validate it
        final String header = request.getHeader(HttpHeaders.AUTHORIZATION);

        // If header is missing or doesn't start with "Bearer ", just continue the filter chain without authentication
        if (header == null || !header.startsWith("Bearer ")) {
            chain.doFilter(request, response);
            return;
        }

        // Extract JWT token from header ("Bearer <token>")
        final String token = header.substring(7).trim();

        try {
            // Validate the token (e.g., check signature, expiry, etc.)
            if (!jwtTokenUtil.validate(token)) {
                chain.doFilter(request, response);
                return;
            }

            String username = jwtTokenUtil.getUsername(token);
            UserDetails userDetails = userRepository.findByEmail(username).orElse(null);

            // If user not found, continue filter chain without authentication
            if (userDetails == null) {
                chain.doFilter(request, response);
                return;
            }

            // Create authentication token using user details and authorities
            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

            // Set additional details from the request (IP, session ID, etc.)
            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

            // Set authentication in the Spring Security context
            SecurityContextHolder.getContext().setAuthentication(authentication);

        } catch (Exception ex) {
            // In case of any exception (token parsing, DB, etc.) continue filter chain without authentication
            // Optionally log the exception here
        }

        // Continue filter chain
        chain.doFilter(request, response);
    }
}

package net.parkvision.parkvisionbackend.config;

import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
public class WebSocketAuthChannelInterceptor implements ChannelInterceptor {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    public WebSocketAuthChannelInterceptor(JwtService jwtService, UserDetailsService userDetailsService) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
    }

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        final StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(message);

        if (headerAccessor.getCommand() == StompCommand.CONNECT) {

            final String authHeader = headerAccessor.getFirstNativeHeader("Authorization");

            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                throw new RuntimeException("Received no or incorrect token");
            }
            final String jwt = authHeader.substring(7);

            final String userEmail = jwtService.extractUsername(jwt);

            UserDetails userDetails = userDetailsService.loadUserByUsername(userEmail);

            if (userDetails == null) {
                throw new RuntimeException(String.format("User with provided username: %s doesn't exist", userEmail));
            }

            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                    userDetails,
                    null,
                    userDetails.getAuthorities()
            );

            SecurityContextHolder.getContext().setAuthentication(authToken);
            authToken.eraseCredentials();
            headerAccessor.setUser(authToken);
        }
        return message;
    }
}

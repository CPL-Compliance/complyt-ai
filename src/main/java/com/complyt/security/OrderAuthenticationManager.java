package com.complyt.security;

import com.complyt.domain.security.User;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class OrderAuthenticationManager {

    public boolean clientIdMatches(Authentication authentication, ObjectId clientId) {
        User authenticatedUser = (User) authentication.getPrincipal();

        log.debug("Auth user Client ID: " + authenticatedUser.getClientId() + "; Client ID: " + clientId);

        return authenticatedUser.getClientId().equals(clientId);
    }
}

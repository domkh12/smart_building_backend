package edu.npic.smartBuilding.util;

import edu.npic.smartBuilding.domain.Role;
import edu.npic.smartBuilding.domain.User;
import edu.npic.smartBuilding.features.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Component
@RequiredArgsConstructor
public class AuthUtil {
    private final UserRepository userRepository;

    private Jwt getJwtFromAuthentication() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof Jwt)) {
            System.out.println("Principal: " + (authentication == null ? "null" : authentication.getPrincipal().toString()));
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Not authenticated user");
        }
        return (Jwt) authentication.getPrincipal();
    }


    private String getEmailFromAuthentication() {
        Jwt jwt = getJwtFromAuthentication();
        return jwt.getId();
    }

    public Integer loggedUserId() {
        return loggedUser().getId();
    }

    public User loggedUser() {
        String email = getEmailFromAuthentication();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
    }

    public List<Role> loggedUserRoles() {
        return loggedUser().getRoles();
    }

    public String loggedUserUuid() {
        return loggedUser().getUuid();
    }

    public List<Long> roomIdOfLoggedUser() {
        Jwt jwt = getJwtFromAuthentication();
        return jwt.getClaim("roomId");
    }

    public boolean isAdminLoggedUser() {
        return loggedUserRoles().stream().anyMatch(role -> role.getName().equalsIgnoreCase("ADMIN"));
    }

    public boolean isManagerLoggedUser() {
        return loggedUserRoles().stream().anyMatch(role -> role.getName().equalsIgnoreCase("MANAGER"));
    }

    public boolean isUserLoggedUser() {
        return loggedUserRoles().stream().anyMatch(role -> role.getName().equalsIgnoreCase("USER"));
    }
}
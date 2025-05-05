package edu.npic.smartBuilding.config;

import edu.npic.smartBuilding.domain.Role;
import edu.npic.smartBuilding.domain.User;
import edu.npic.smartBuilding.features.role.RoleRepository;
import edu.npic.smartBuilding.features.signUpMethod.SignUpMethodRepository;
import edu.npic.smartBuilding.features.user.UserService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class OAuth2LoginSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {

//    private final UserService userService;
//    private final RoleRepository roleRepository;
//    private JwtEncoder jwtEncoderRefreshToken;
//    private final SignUpMethodRepository signUpMethodRepository;
//
//    @Autowired
//    @Qualifier("jwtEncoderRefreshToken")
//    public void setJwtEnCoderRefreshToken(JwtEncoder jwtEnCoderRefreshToken){
//        this.jwtEncoderRefreshToken = jwtEnCoderRefreshToken;
//    }
//
//    @Value("${frontend.url}")
//    private String frontendUrl;
//
//    String username;
//    String idAttributeKey;
//    @Override
//    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws ServletException, IOException {
//
//        DefaultOAuth2User principal = (DefaultOAuth2User) authentication.getPrincipal();
//        OAuth2AuthenticationToken oAuth2AuthenticationToken = (OAuth2AuthenticationToken) authentication;
//        String registrationId = oAuth2AuthenticationToken.getAuthorizedClientRegistrationId();
//        log.info("token data: {}", oAuth2AuthenticationToken);
//        if ("github".equals(registrationId) || "google".equals(registrationId) || "azure".equals(registrationId)) {
////          DefaultOAuth2User principal = (DefaultOAuth2User) authentication.getPrincipal();
//            Map<String, Object> attributes = principal.getAttributes();
//            log.info(attributes.toString());
//            String email = attributes.getOrDefault("email", "").toString();
//            String name = attributes.getOrDefault("name", "").toString();
//
//            if("github".equals(registrationId)) {
//                username = attributes.getOrDefault("login", "").toString();
//                idAttributeKey = "id";
//            }
//            else if("google".equals(registrationId)) {
//                username = email.split("@")[0];
//                idAttributeKey = "sub";
//            }else if("azure".equals(oAuth2AuthenticationToken.getAuthorizedClientRegistrationId())) {
//                username = attributes.getOrDefault("name", "").toString();
//                idAttributeKey = "sub";
//            }else {
//                username = "";
//                idAttributeKey = "id";
//            }
//            log.info("Hello OAuth: " + email + ":" + name + ":" + username + ":" + idAttributeKey);
//
//            userService.findByEmail(email).ifPresentOrElse(
//                    user -> {
//                        DefaultOAuth2User oAuth2User = new DefaultOAuth2User(
//                                List.of(new SimpleGrantedAuthority(user.getRoles().stream().findFirst().toString())),
//                                attributes,
//                                idAttributeKey
//                        );
//                        Authentication securityAuth = new OAuth2AuthenticationToken(
//                                oAuth2User,
//                                List.of(new SimpleGrantedAuthority(user.getRoles().stream().findFirst().toString())),
//                                oAuth2AuthenticationToken.getAuthorizedClientRegistrationId()
//                        );
//                        SecurityContextHolder.getContext().setAuthentication(securityAuth);
//                    }, ()->{
//                        User newUser = new User();
//                        Optional<Role> userRole = roleRepository.findByName("ADMIN");
//                        if(userRole.isPresent()) {
//                            newUser.setRoles(List.of(userRole.get()));
//                        }else{
//                            throw new RuntimeException("Default role not found");
//                        }
//
//                        newUser.setEmail(email);
//                        newUser.setFullName(username);
//                        newUser.setSignUpMethod(signUpMethodRepository.findByName("AZURE").get());
//                        newUser.setCreatedAt(LocalDateTime.now());
//                        newUser.setIsVerified(true);
//                        newUser.setIsAccountNonLocked(true);
//                        newUser.setIsAccountNonExpired(true);
//                        newUser.setIsCredentialsNonExpired(true);
//                        newUser.setIsDeleted(true);
//                        newUser.setUuid(UUID.randomUUID().toString());
//                        newUser.setIsOnline(false);
//                        newUser.setIsTwoFactorEnabled(false);
//                        userService.registerUser(newUser);
//
//                        DefaultOAuth2User oauthUser = new DefaultOAuth2User(
//                                List.of(new SimpleGrantedAuthority(newUser.getRoles().stream().findFirst().toString())),
//                                        attributes,
//                                        idAttributeKey
//                        );
//                        Authentication securityAuth = new OAuth2AuthenticationToken(
//                                oauthUser,
//                                List.of(new SimpleGrantedAuthority(newUser.getRoles().stream().findFirst().toString())),
//                                oAuth2AuthenticationToken.getAuthorizedClientRegistrationId()
//                        );
//                        SecurityContextHolder.getContext().setAuthentication(securityAuth);
//                    }
//            );
//        }
//
//        this.setAlwaysUseDefaultTargetUrl(true);
//
//        // JWT TOKEN LOGIC
//        DefaultOAuth2User oauth2User = (DefaultOAuth2User) authentication.getPrincipal();
//        Map<String, Object> attributes = oauth2User.getAttributes();
//
//        // Extract necessary attributes
//        String email = (String) attributes.get("email");
//        User user = userService.findByEmail(email).orElseThrow(
//                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "user not found")
//        );
//        System.out.println("OAuth2LoginSuccessHandler: " + username + " : " + email);
//
//        String authorities = oauth2User.getAuthorities().stream()
//                .map(authority -> new SimpleGrantedAuthority(authority.getAuthority()))
//                .map(SimpleGrantedAuthority::toString)
//                .collect(Collectors.joining(" "));
//
//        String joinedRoles = user.getRoles().stream()
//                .map(role -> "ROLE_" + role.getName())
//                .collect(Collectors.joining(" "));
//
//        String scope = authorities + " " + joinedRoles;
//
//        log.info("scope :" + scope);
//
//        // create access token clains set
//        Instant now = Instant.now();
//
//        // Create refresh token claims set
//        JwtClaimsSet jwtClaimsSetRefreshToken = JwtClaimsSet.builder()
//                .id(email)
//                .issuedAt(now)
//                .issuer("web")
//                .audience(List.of("nextjs","reactjs"))
//                .subject("Refresh Token")
//                .expiresAt(now.plus(7, ChronoUnit.DAYS))
//                .build();
//
//        JwtEncoderParameters jwtEncoderParametersRefreshToken = JwtEncoderParameters.from(jwtClaimsSetRefreshToken);
//        Jwt jwtRefreshToken = jwtEncoderRefreshToken.encode(jwtEncoderParametersRefreshToken);
//        String refreshToken = jwtRefreshToken.getTokenValue();
//
//        // Set refresh token as an httpOnly cookie
//        ResponseCookie refreshTokenCookie = ResponseCookie.from("refreshToken", refreshToken)
//                .httpOnly(true)
//                .secure(true)
//                .path("/")
//                .maxAge(7 * 24 * 60 * 60)
//                .build();
//
//        response.addHeader(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString());
//
//        // Redirect to the frontend with the JWT token
//        String targetUrl = UriComponentsBuilder.fromUriString(frontendUrl + "/oauth2/redirect")
//                .build().toUriString();
//        this.setDefaultTargetUrl(targetUrl);
//        super.onAuthenticationSuccess(request, response, authentication);
//
//    }
}

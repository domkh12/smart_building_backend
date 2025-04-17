package edu.npic.smartBuilding.features.auth;

import com.warrenstrange.googleauth.GoogleAuthenticatorKey;
import edu.npic.smartBuilding.domain.*;
import edu.npic.smartBuilding.features.auth.dto.*;
import edu.npic.smartBuilding.features.totp.TotpService;
import edu.npic.smartBuilding.features.user.UserRepository;
import edu.npic.smartBuilding.features.user.UserService;
import edu.npic.smartBuilding.features.user.dto.CreateUserRegister;
import edu.npic.smartBuilding.features.user.dto.UserDetailResponse;
import edu.npic.smartBuilding.mapper.UserMapper;
import edu.npic.smartBuilding.util.AuthUtil;
import edu.npic.smartBuilding.util.JwtUtils;
import edu.npic.smartBuilding.util.RandomOtp;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.servlet.http.Cookie;
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
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.security.oauth2.server.resource.authentication.BearerTokenAuthenticationToken;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationProvider;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService{

    private final String TOKEN_TYPE = "Bearer";

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final EmailVerificationRepository emailVerificationRepository;
    private final JavaMailSenderImpl mailSender;
    private final DaoAuthenticationProvider daoAuthenticationProvider;
    private final JwtEncoder jwtEncoder;
    private JwtEncoder jwtEncoderRefreshToken;
    private final PasswordEncoder passwordEncoder;
    private final JwtAuthenticationProvider jwtAuthenticationProvider;
    private final AuthUtil authUtil;
    private final UserService userService;
    private final TotpService totpService;
    private final JwtUtils jwtUtils;
    @Value("${spring.mail.username}")
    private String adminMail;

    @Value("${backend.domain}")
    private String backendDomain;

    @Autowired
    @Qualifier("jwtEncoderRefreshToken")
    public void setJwtEnCoderRefreshToken(JwtEncoder jwtEnCoderRefreshToken){
        this.jwtEncoderRefreshToken = jwtEnCoderRefreshToken;
    }

    @Override
    public ResponseEntity<Void> logout(HttpServletResponse response) {
        System.out.println(ResponseCookie.from(TOKEN_TYPE).maxAge(Duration.ofDays(1)));
        // Clear the refresh token cookie
        ResponseCookie clearRefreshTokenCookie = ResponseCookie.from("refreshToken", "")
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(0)
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, clearRefreshTokenCookie.toString());

        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<JwtResponse> refreshToken(HttpServletRequest request, HttpServletResponse response) {

        String refreshToken = null;
        log.info(Arrays.toString(request.getCookies()));
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if ("refreshToken".equals(cookie.getName())) {
                    refreshToken = cookie.getValue();
                    break;
                }
            }
        }

        if (refreshToken == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Refresh token is missing");
        }

        log.info("Refresh token request: {}", refreshToken);
        Authentication auth = new BearerTokenAuthenticationToken(refreshToken);
        auth = jwtAuthenticationProvider.authenticate(auth);

        String scope = auth.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(" "));
        log.info("log4");
        log.info("New Scope: {}", scope);
        log.info("Auth: {}", auth);

        Instant now = Instant.now();

        Jwt jwt = (Jwt) auth.getPrincipal();
        User user = userRepository.findByEmail(jwt.getId()).orElseThrow();

        List<Integer> roomId = user.getRooms().stream().map(Room::getId).toList();

//        List<Integer> roomIds = new ArrayList<>();
//        String siteUuid = jwt.getClaim("roomId");
//        if (siteUuid != null){
//            sitesUuid = List.of(siteUuid);
//        } else {
//            sitesUuid = user.getFloors().stream().map(site -> site.getUuid()).toList();
//        }

        // Create access token claims set
        JwtClaimsSet jwtClaimsSet = JwtClaimsSet.builder()
                .id(jwt.getId())
                .issuedAt(now)
                .issuer("web")
                .audience(List.of("nextjs", "reactjs"))
                .subject("Access Token")
                .expiresAt(now.plus(15, ChronoUnit.MINUTES))
                .claim("scope", scope)
                .claim("id",user.getId())
                .claim("isEnabledTwoFA",user.getIsTwoFactorEnabled())
                .claim("roomId", roomId)
                .build();

        JwtEncoderParameters jwtEncoderParameters = JwtEncoderParameters.from(jwtClaimsSet);
        Jwt encodedJwt = jwtEncoder.encode(jwtEncoderParameters);

        String accessToken = encodedJwt.getTokenValue();

        if (Duration.between(Instant.now(), jwt.getExpiresAt()).toDays() < 2) {
            // Create refresh token claims set
            JwtClaimsSet jwtClaimsSetRefreshToken = JwtClaimsSet.builder()
                    .id(auth.getName())
                    .issuedAt(now)
                    .issuer("web")
                    .audience(List.of("nextjs", "reactjs"))
                    .subject("Refresh Token")
                    .expiresAt(now.plus(7, ChronoUnit.DAYS))
                    .build();
            JwtEncoderParameters jwtEncoderParametersRefreshToken = JwtEncoderParameters.from(jwtClaimsSetRefreshToken);
            Jwt jwtRefreshToken = jwtEncoderRefreshToken.encode(jwtEncoderParametersRefreshToken);
            refreshToken = jwtRefreshToken.getTokenValue();

            // Set new refresh token as an httpOnly cookie
            ResponseCookie refreshTokenCookie = ResponseCookie.from("refreshToken", refreshToken)
                    .httpOnly(true)
                    .secure(true)
                    .path("/")
                    .maxAge(7 * 24 * 60 * 60) // 7 days
                    .build();

            response.addHeader(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString());
        }

        return ResponseEntity.ok(JwtResponse.builder()
                .tokenType(TOKEN_TYPE)
                .accessToken(accessToken)
                .build());
    }

    @Override
    public void verify(VerifyRequest verifyRequest, String token) {

        User user = userRepository.findByEmail(verifyRequest.email()).orElseThrow(
                () -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Invalid email"
                )
        );

        EmailVerification emailVerification = emailVerificationRepository.findByUser(user).orElseThrow(
                () -> new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED,
                        "Verification failed"
                )
        );

        if (!emailVerification.getVerificationCode().equals(verifyRequest.verificationCode())){
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Verification Code invalid"
            );
        }

        if(LocalTime.now().isAfter(emailVerification.getExpiryTime())){
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Verification Expired"
            );
        };
        user.setIsDeleted(false);
        user.setIsVerified(true);
        userRepository.save(user);
    }

    @Override
    public ResponseEntity<JwtResponse> login(LoginRequest loginRequest, HttpServletResponse response) {
        System.out.println("email:"+loginRequest.email());

        Authentication auth = new UsernamePasswordAuthenticationToken(
                loginRequest.email(),
                loginRequest.password()
        );

        User user = userRepository.findByEmail(loginRequest.email())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        List<Integer> roomId = user.getRooms().stream().map(Room::getId).toList();

        auth = daoAuthenticationProvider.authenticate(auth);

        // prepare SCOPE
        log.info("Authorities: {}", auth.getAuthorities());

        // ADMIN MANAGER USER

        String scope = auth.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(" "));
        ;
        log.info("SCOPE : {}", scope);

        Instant now = Instant.now();

        // Create access token claims set
        JwtClaimsSet jwtClaimsSet = JwtClaimsSet.builder()
                .id(auth.getName())
                .issuedAt(now)
                .issuer("web")
                .audience(List.of("nextjs","reactjs"))
                .subject("Access Token")
                .expiresAt(now.plus(15, ChronoUnit.MINUTES))
                .claim("scope", scope)
                .claim("id", user.getId())
                .claim("isEnabledTwoFA",user.getIsTwoFactorEnabled())
                .claim("roomId", roomId)
                .build();

        // Create refresh token claims set
        JwtClaimsSet jwtClaimsSetRefreshToken = JwtClaimsSet.builder()
                .id(auth.getName())
                .issuedAt(now)
                .issuer("web")
                .audience(List.of("nextjs","reactjs"))
                .subject("Refresh Token")
                .expiresAt(now.plus(7, ChronoUnit.DAYS))
                .build();

        JwtEncoderParameters jwtEncoderParameters = JwtEncoderParameters.from(jwtClaimsSet);
        Jwt jwt = jwtEncoder.encode(jwtEncoderParameters);

        JwtEncoderParameters jwtEncoderParametersRefreshToken = JwtEncoderParameters.from(jwtClaimsSetRefreshToken);
        Jwt jwtRefreshToken = jwtEncoderRefreshToken.encode(jwtEncoderParametersRefreshToken);

        String accessToken = jwt.getTokenValue();
        String refreshToken = jwtRefreshToken.getTokenValue();

        // Set refresh token as an httpOnly cookie
        ResponseCookie refreshTokenCookie = ResponseCookie.from("refreshToken", refreshToken)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(7 * 24 * 60 * 60)
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString());

        return ResponseEntity.ok(JwtResponse.builder()
                .tokenType("Bearer")
                .accessToken(accessToken)
                .build());
    }

    @Override
    public void register(CreateUserRegister createUserRegister) throws MessagingException {

        if (userRepository.existsByEmail(createUserRegister.email())){
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Email already exists!"
            );
        }

        if (!createUserRegister.password().equals(createUserRegister.confirmPassword())){
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Passwords and confirm passwords do not match!"
            );
        }

        User user = userMapper.fromCreateUserRegister(createUserRegister);
        user.setUuid(UUID.randomUUID().toString());
        user.setPassword(passwordEncoder.encode(createUserRegister.password()));
        user.setCreatedAt(LocalDateTime.now());
        user.setIsVerified(false);
        user.setProfileImage("default-avatar.jpg");
        user.setIsAccountNonExpired(true);
        user.setIsAccountNonLocked(true);
        user.setIsCredentialsNonExpired(true);
        user.setIsDeleted(false);
        List<Role> roles = new ArrayList<>();
        roles.add(Role.builder().name("USER").build());
        user.setRoles(roles);
        user.setIsOnline(false);

        userRepository.save(user);

        EmailVerification emailVerification = new EmailVerification();
        emailVerification.setEmail(user.getEmail());
        emailVerification.setExpiryTime(LocalTime.now().plusMinutes(3));
        emailVerification.setUser(user);
        emailVerification.setVerificationCode(RandomOtp.generateSecurityCode());
        emailVerificationRepository.save(emailVerification);

        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage);
        helper.setSubject("Email Verification - SPS");
        helper.setTo(user.getEmail());
        helper.setFrom(adminMail);
        helper.setText(emailVerification.getVerificationCode());

        mailSender.send(mimeMessage);
    }

    @Override
    public QrCodeResponse enable2FA() {
        Integer userId = authUtil.loggedUserId();
        GoogleAuthenticatorKey secret = userService.generate2FASecret(userId);
        String qrCodeUrl = totpService.getQrCodeUrl(secret, userService.getUserById(userId).getEmail());
        log.info("QR Code Url: " + qrCodeUrl);
        return QrCodeResponse.builder()
                .qrCodeUrl(qrCodeUrl)
                .build();
    }

    @Override
    public MessageResponse disable2FA() {
        Integer userId = authUtil.loggedUserId();
        userService.disable2FA(userId);
        return MessageResponse.builder()
                .message("Disabled 2FA")
                .build();
    }

    @Override
    public MessageResponse verify2FA(Integer code) {
        Integer userId = authUtil.loggedUserId();
        boolean isValid = userService.validate2FACode(userId, code);
        if (isValid){
            userService.enable2FA(userId);
            return MessageResponse.builder()
                    .message("Verified 2FA")
                    .build();
        }else {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid 2FA code");
        }
    }

    @Override
    public MessageResponse verify2FALogin(Integer code, String token, HttpServletResponse response) {
        String email = jwtUtils.getEmailFromToken(token);
        User user = userRepository.findByEmail(email).get();
        boolean isValid = userService.validate2FACode(user.getId(), code);
        if (isValid){

            userService.enable2FA(user.getId());
            // create access token clains set
            Instant now = Instant.now();

            // Create refresh token claims set
            JwtClaimsSet jwtClaimsSetRefreshToken = JwtClaimsSet.builder()
                    .id(email)
                    .issuedAt(now)
                    .issuer("web")
                    .audience(List.of("nextjs","reactjs"))
                    .subject("Refresh Token")
                    .expiresAt(now.plus(7, ChronoUnit.DAYS))
                    .build();

            JwtEncoderParameters jwtEncoderParametersRefreshToken = JwtEncoderParameters.from(jwtClaimsSetRefreshToken);
            Jwt jwtRefreshToken = jwtEncoderRefreshToken.encode(jwtEncoderParametersRefreshToken);
            String refreshToken = jwtRefreshToken.getTokenValue();

            // Set refresh token as an httpOnly cookie
            ResponseCookie refreshTokenCookie = ResponseCookie.from("refreshToken", refreshToken)
                    .httpOnly(true)
                    .secure(true)
                    .path("/")
                    .maxAge(7 * 24 * 60 * 60)
                    .build();

            response.addHeader(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString());
            return MessageResponse.builder()
                    .message("Verified 2FA")
                    .build();
        }else {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid 2FA code");
        }
    }

    @Override
    public UserDetailResponse getProfileUser() {
        User user = authUtil.loggedUser();
        return userMapper.toUserDetailResponse(user);
    }

    @Override
    public MessageResponse verifySites(String uuid, String token, HttpServletResponse response) {

//        String email = jwtUtils.getEmailFromToken(token);
//        User user = userRepository.findByEmail(email).get();
//
//        boolean isValidSite = user.getFloors().stream()
//                .anyMatch(site -> site.getUuid().equals(uuid));
//
//        if (!isValidSite){
//            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid site uuid");
//        }
//
//        Instant now = Instant.now();
//
//        // Create refresh token claims set
//        JwtClaimsSet jwtClaimsSetRefreshToken = JwtClaimsSet.builder()
//                .id(user.getEmail())
//                .issuedAt(now)
//                .issuer("web")
//                .audience(List.of("nextjs","reactjs"))
//                .subject("Refresh Token")
//                .expiresAt(now.plus(7, ChronoUnit.DAYS))
//                .claim("site", uuid)
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
//        return MessageResponse.builder()
//                .message("Verified site!")
//                .build();

        return null;
    }
}

package edu.npic.smartBuilding.features.auth;

import edu.npic.smartBuilding.features.auth.dto.*;
import edu.npic.smartBuilding.features.user.dto.CreateUserRegister;
import edu.npic.smartBuilding.features.user.dto.UserDetailResponse;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/verify-sites")
    @ResponseStatus(HttpStatus.CREATED)
    MessageResponse verifySites(@RequestParam String uuid,@RequestParam String token, HttpServletResponse response){
        return authService.verifySites(uuid, token, response);
    }

    @PreAuthorize("hasAnyAuthority('ROLE_MANAGER', 'ROLE_ADMIN', 'ROLE_USER')")
    @GetMapping("/profiles")
    UserDetailResponse getProfileUser(){
        return authService.getProfileUser();
    }

    @PostMapping("/logout")
    ResponseEntity<Void> logout(HttpServletResponse response) {
       return authService.logout(response);
    }

    @GetMapping("/refresh")
    ResponseEntity<JwtResponse> refreshToken(HttpServletRequest request, HttpServletResponse response){
        return authService.refreshToken(request, response);
    }

    @PostMapping("/verify")
    void verify(@Valid @RequestBody VerifyRequest verifyRequest, @RequestParam String token){
        authService.verify(verifyRequest, token);
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/registers")
    void register(@Valid @RequestBody CreateUserRegister createUserRegister) throws MessagingException {
        authService.register(createUserRegister);
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/login")
    ResponseEntity<JwtResponse> login(@Valid @RequestBody LoginRequest loginRequest, HttpServletResponse response){
        return authService.login(loginRequest, response);
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @PostMapping("/enable-2fa")
    @ResponseStatus(HttpStatus.CREATED)
    QrCodeResponse enable2FA(){
        return authService.enable2FA();
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @PostMapping("/disable-2fa")
    @ResponseStatus(HttpStatus.CREATED)
    MessageResponse disable2FA(){
        return authService.disable2FA();
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @PostMapping("/verify-2fa")
    @ResponseStatus(HttpStatus.CREATED)
    MessageResponse verify2FA(@RequestParam Integer code){
        return authService.verify2FA(code);
    }

    @PostMapping("/verify-2fa-login")
    @ResponseStatus(HttpStatus.CREATED)
    MessageResponse verify2FALogin(
            @RequestParam Integer code,
            @RequestParam String token,
            HttpServletResponse response){
        return authService.verify2FALogin(code,token,response);
    }

}

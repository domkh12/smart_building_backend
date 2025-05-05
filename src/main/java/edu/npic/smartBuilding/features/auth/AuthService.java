package edu.npic.smartBuilding.features.auth;

import edu.npic.smartBuilding.features.auth.dto.*;
import edu.npic.smartBuilding.features.user.dto.CreateUserRegister;
import edu.npic.smartBuilding.features.user.dto.UpdateProfileUserRequest;
import edu.npic.smartBuilding.features.user.dto.UserDetailResponse;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;

public interface AuthService {

    MessageResponse resetPassword(ResetPasswordRequest resetPasswordRequest);

    MessageResponse forgotPassword(String email);

    UserDetailResponse updateProfileUser(UpdateProfileUserRequest updateProfileUserRequest);

    ResponseEntity<Void> logout(HttpServletResponse response);

    ResponseEntity<JwtResponse> refreshToken(HttpServletRequest request, HttpServletResponse response);

    ResponseEntity<JwtResponse> login(LoginRequest loginRequest, HttpServletResponse response);

    void register(CreateUserRegister createUserRegister) throws MessagingException;

    void verify(VerifyRequest verifyRequest, String token);

    QrCodeResponse enable2FA();

    MessageResponse disable2FA();

    MessageResponse verify2FA(Integer code);

    JwtResponse verify2FALogin(Integer code, String email, HttpServletResponse response);

    UserDetailResponse getProfileUser();

    MessageResponse verifySites(String uuid, String token, HttpServletResponse response);

    void changePassword(ChangePasswordRequest changePasswordRequest);
}

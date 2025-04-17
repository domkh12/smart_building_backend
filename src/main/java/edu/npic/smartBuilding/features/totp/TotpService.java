package edu.npic.smartBuilding.features.totp;

import com.warrenstrange.googleauth.GoogleAuthenticatorKey;

public interface TotpService {

    GoogleAuthenticatorKey generateSecret();

    String getQrCodeUrl(GoogleAuthenticatorKey secret, String email);

    Boolean verifyCode(String secret, int code);

}

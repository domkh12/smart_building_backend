package edu.npic.smartBuilding.features.totp;

import com.warrenstrange.googleauth.GoogleAuthenticator;
import com.warrenstrange.googleauth.GoogleAuthenticatorKey;
import com.warrenstrange.googleauth.GoogleAuthenticatorQRGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class TotpServiceImpl implements TotpService{

    private final GoogleAuthenticator gAuth;

    public TotpServiceImpl() {
        gAuth = new GoogleAuthenticator();
    }

    @Override
    public GoogleAuthenticatorKey generateSecret(){
        return gAuth.createCredentials();
    }

    @Override
    public String getQrCodeUrl(GoogleAuthenticatorKey secret, String email){
        return GoogleAuthenticatorQRGenerator.getOtpAuthURL("SmartBuildingNpic", email, secret);
    }

    @Override
    public Boolean verifyCode(String secret, int code){
        Boolean verified = gAuth.authorize(secret, code);
        log.info("Verify code: {}", verified);
        return verified;
    }

}

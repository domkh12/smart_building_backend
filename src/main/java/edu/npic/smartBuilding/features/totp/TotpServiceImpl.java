package edu.npic.smartBuilding.features.totp;

import com.warrenstrange.googleauth.GoogleAuthenticator;
import com.warrenstrange.googleauth.GoogleAuthenticatorKey;
import com.warrenstrange.googleauth.GoogleAuthenticatorQRGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
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
        return GoogleAuthenticatorQRGenerator.getOtpAuthURL("SPS", email, secret);
    }

    @Override
    public Boolean verifyCode(String secret, int code){
        return gAuth.authorize(secret, code);
    }

}

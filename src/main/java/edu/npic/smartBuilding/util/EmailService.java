package edu.npic.smartBuilding.util;

import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender javaMailSender;

    public void sendPasswordResetEmail(String to, String resetUrl){
        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
        simpleMailMessage.setTo(to);
        simpleMailMessage.setSubject("Password reset from SPS-NPIC");
        simpleMailMessage.setText("This is password reset email request :  "+ resetUrl);
        javaMailSender.send(simpleMailMessage);
    }

}

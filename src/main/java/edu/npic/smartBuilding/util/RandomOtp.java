package edu.npic.smartBuilding.util;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Random;

@Setter
@Getter
@NoArgsConstructor
public class RandomOtp {
    public static String generateSecurityCode() {
        Random random = new Random();
        int securityCode = 100000 + random.nextInt(900000);  // Generates a random number between 100000 and 999999
        return String.valueOf(securityCode);
    }
}

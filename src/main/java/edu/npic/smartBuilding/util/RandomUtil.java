package edu.npic.smartBuilding.util;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Setter
@Getter
@NoArgsConstructor
public class RandomUtil {

    public static String randomUuidToken(){
        return UUID.randomUUID().toString();
    }
}

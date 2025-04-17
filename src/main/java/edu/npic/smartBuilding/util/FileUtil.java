package edu.npic.smartBuilding.util;

import java.util.UUID;

public class FileUtil {

    public static String generateFileName(String originalFilename) {
        String newFilename = UUID.randomUUID().toString();
        String extension = extractExtension(originalFilename);
        return String.format("%s.%s", newFilename, extension);
    }

    public static String extractExtension(String originalFilename) {
        int lastDotIndex = originalFilename.lastIndexOf(".");
        return originalFilename.substring(lastDotIndex + 1);
    }

}

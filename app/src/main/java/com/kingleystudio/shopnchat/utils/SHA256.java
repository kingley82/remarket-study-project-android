package com.kingleystudio.shopnchat.utils;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class SHA256 {
    public static String hash(String text) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hash_result = digest.digest(text.getBytes(StandardCharsets.US_ASCII));
        return new String(hash_result, StandardCharsets.US_ASCII);
    }
}

package com.bankcardsystem.bankcardsystem.util;

import org.springframework.stereotype.Service;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Service
public class EncryptionService {

    private static final Charset UTF_8 = StandardCharsets.UTF_8;

    public String encrypt(String plainText) {
        if (plainText == null) return null;
        return Base64.getEncoder().encodeToString(plainText.getBytes(UTF_8));
    }

    public String decrypt(String encodedText) {
        if (encodedText == null) return null;
        return new String(Base64.getDecoder().decode(encodedText), UTF_8);
    }
}

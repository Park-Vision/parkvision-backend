package net.parkvision.parkvisionbackend.config;

import io.jsonwebtoken.io.Decoders;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;


public class MessageEncryptor {

    private static final String ALGORITHM = "AES";
    private static final String TRANSFORMATION = "AES/CBC/PKCS5Padding";

    public static String encryptMessage(String message, String key) throws Exception {
        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        SecretKeySpec secretKeySpec = new SecretKeySpec(decodeBase64(key), ALGORITHM);
        IvParameterSpec ivParameterSpec = generateIV();

        cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, ivParameterSpec);
        byte[] encryptedBytes = cipher.doFinal(message.getBytes(StandardCharsets.UTF_8));

        return encodeBase64(ivParameterSpec.getIV()) + ":" + encodeBase64(encryptedBytes);
    }

    public static String decryptMessage(String encryptedMessage, String key) throws Exception {
        String[] parts = encryptedMessage.split(":");
        byte[] iv = decodeBase64(parts[0]);
        byte[] encryptedBytes = decodeBase64(parts[1]);
        System.out.println(new String(iv,StandardCharsets.UTF_8));
        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        SecretKeySpec secretKeySpec = new SecretKeySpec(decodeBase64(key), ALGORITHM);
        IvParameterSpec ivParameterSpec = new IvParameterSpec(iv);

        cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, ivParameterSpec);
        byte[] decryptedBytes = cipher.doFinal(encryptedBytes);

        return new String(decryptedBytes, StandardCharsets.UTF_8);
    }


    private static IvParameterSpec generateIV() {
        byte[] iv = new byte[16];
        new SecureRandom().nextBytes(iv);
        String ivBase64 = Base64.getEncoder().encodeToString(iv);
        System.out.println(ivBase64);
        return new IvParameterSpec(iv);
    }

    private static byte[] decodeBase64(String encoded) {
        return Base64.getDecoder().decode(encoded);
    }


    public static String generateKey() {
        try {
            KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");

            keyGenerator.init(128);
            SecretKey secretKey = keyGenerator.generateKey();


            return encodeBase64(secretKey.getEncoded());
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            throw new RuntimeException("Error generating drone key");
        }
    }

    private static String encodeBase64(byte[] bytes) {
        return Base64.getEncoder().encodeToString(bytes);
    }
}

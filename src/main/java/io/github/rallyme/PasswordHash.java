package io.github.rallyme;

import java.security.SecureRandom;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.SecretKeyFactory;
import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import javax.xml.bind.DatatypeConverter;

public class PasswordHash {

    public static final int SALT_LEN = 24; // bytes
    public static final int HASH_LEN = 24; // bytes
    public static final int ITERATIONS = 5000;

    private static byte[] fromHex(String hex) {
        return DatatypeConverter.parseHexBinary(hex);
    }

    private static String toHex(byte[] bytes) {
        return DatatypeConverter.printHexBinary(bytes);
    }

    private static boolean hashEquals(byte[] a, byte[] b) {
        int diff = a.length ^ b.length;
        for(int i = 0; i < a.length && i < b.length; i++) {
            diff |= a[i] ^ b[i];
        }

        return diff == 0;
    }

    private static byte[] pbkdf2(char[] password, byte[] salt, int iterations, int bytes) {
        try {
            PBEKeySpec spec = new PBEKeySpec(password, salt, iterations);
            SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
            return skf.generateSecret(spec).getEncoded();
        } catch(NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new RuntimeException(e);
        }
    }

    public static String hashPassword(char[] password) {
        // Generate a random salt
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[SALT_BYTES];
        random.nextBytes(salt);

        // Hash the password
        byte[] hash = pbkdf2(password, salt, ITERATIONS, HASH_LEN);
        // Create pbkdf2 format
        return ITERATIONS + ":" + toHex(salt) + ":" +  toHex(hash);
    }

    public static boolean validatePassword(char[] password, String pbkdf2Expected) {
        String[] params = pbkdf2Expected.split(":");
        int iterations = Integer.parseInt(params[0]);
        byte[] salt = fromHex(params[1]);
        byte[] hashExpected = fromHex(params[2]);

        byte[] hashGenerated = pbkdf2(password, salt, iterations, hashExpected.length);
        return hashEquals(hashExpected, hashGenerated);
    }

}

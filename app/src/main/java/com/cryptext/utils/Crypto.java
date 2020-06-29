package com.cryptext.utils;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;

import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

public class Crypto {

    private static final int IV_SIZE = 16;
    //for encrypt with AES 256 bit
    private static final int KEY_SIZE = 256;
    private static Crypto singleton = new Crypto();

    //private Crypto() { }


    /**
     * Returns the singleton.
     *
     * @return singleton of Crypto
     */
    public static Crypto getInstance() {
        return singleton;
    }

    /**
     * Generate a random initialization vector.
     *
     * @return random generated IV
     */
    byte[] generateIv() {
        final SecureRandom random = new SecureRandom();
        final byte[] ivBytes = new byte[IV_SIZE];
        random.nextBytes(ivBytes);
        return ivBytes;
    }
    //https://tools.ietf.org/html/rfc2898

    byte[] deriveKey(String password, byte[] salt) throws NoSuchAlgorithmException, InvalidKeySpecException {
        //PBKDF2WithHmacSHA512
        SecretKeyFactory kf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA512");
        KeySpec specs = new PBEKeySpec(password.toCharArray(), salt, 20000, KEY_SIZE);//make password more secure using salt
        SecretKey key = kf.generateSecret(specs);

        return key.getEncoded();
    }

}
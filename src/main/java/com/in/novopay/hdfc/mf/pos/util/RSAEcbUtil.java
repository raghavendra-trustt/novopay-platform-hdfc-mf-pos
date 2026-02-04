package com.in.novopay.hdfc.mf.pos.util;

import lombok.Data;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import java.security.*;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.Base64;

@Component
@Data
public class RSAEcbUtil {

    RSAPublicKey publicKey;
    RSAPrivateKey privateKey;

    static {
        Security.addProvider(new BouncyCastleProvider());
    }
    /** The Constant RSA_ECB_PKCS1_PADDING. */
    private static final String RSA_ECB_OAEP_PADDING = "RSA/ECB/OAEPWithSHA-256AndMGF1Padding";
    private static final String BC = "BC";
    private static final String RSA = "RSA";

    // Generate RSA Key Pair
    /*public KeyPair generateRSAKeyPair(int keySize) throws NoSuchAlgorithmException {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(keySize);
        return keyPairGenerator.generateKeyPair();
    }*/

    private String encode(byte[] data){
        return Base64.getEncoder().encodeToString(data);
    }

    private byte[] decode(String data) {
        return Base64.getDecoder().decode(data);
    }

    // Encrypt using RSA/ECB/OAEP
    public  byte[] encrypt(String plaintext) throws Exception {
        Cipher cipher = Cipher.getInstance(RSA_ECB_OAEP_PADDING, BC);
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        return cipher.doFinal(plaintext.getBytes());
    }

    // Decrypt using RSA/ECB/OAEP
    public byte[] decrypt(String data) throws Exception {
        byte[] ciphertext = Base64.getDecoder().decode(data);
        Cipher cipher = Cipher.getInstance(RSA_ECB_OAEP_PADDING, BC);
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        return cipher.doFinal(ciphertext);
    }

    /*public static void main(String[] args) {
        try {
            // Step 1: Generate RSA Key Pair


            // Step 2: Encrypt a message
            String plaintext = "197JEw96bI1jpk+x104rcw==";
            byte[] ciphertext = encrypt(publicKey, plaintext);
            System.out.println("Ciphertext (Base64 encoded): " + Base64.getEncoder().encodeToString(ciphertext));

            // Step 3: Decrypt the message
            String decryptedText = decrypt(privateKey, ciphertext);
            System.out.println("Decrypted Text: " + decryptedText);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }*/

    public KeyPair generateRSAKkeyPair() throws NoSuchAlgorithmException {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(RSA);
        SecureRandom rnd = new SecureRandom();
        keyPairGenerator.initialize(2048, rnd);
        KeyPair keyPair = keyPairGenerator.genKeyPair();
        publicKey = (RSAPublicKey) keyPair.getPublic();
        privateKey = (RSAPrivateKey) keyPair.getPrivate();
        return keyPair;
    }
}


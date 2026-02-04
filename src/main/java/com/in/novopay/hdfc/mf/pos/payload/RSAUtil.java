package com.in.novopay.hdfc.mf.pos.payload;

import lombok.Data;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Base64;

@Component
@Data
public class RSAUtil {

    private static final String RSA = "RSA";

    PrivateKey privateKey;
    PublicKey publicKey;

    // Generating public & private keys
    // using RSA algorithm.
    public KeyPair generateRSAKkeyPair() throws Exception
    {
       /* SecureRandom secureRandom = new SecureRandom();
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(RSA);

        keyPairGenerator.initialize(1024, secureRandom);
        KeyPair keyPair = keyPairGenerator.generateKeyPair();*/
        KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
        generator.initialize(1024);
        KeyPair keyPair = generator.generateKeyPair();
        privateKey = keyPair.getPrivate();
        publicKey = keyPair.getPublic();
        return keyPair;
    }

    /*public String getPublicKey() {
        return new String(Base64.getEncoder().encode(publicKey.getEncoded()));
    }

    public String getPrivateKey() {
        return new String(Base64.getEncoder().encode(privateKey.getEncoded()));
    }*/

    public String rsaEncrypt(String data) throws Exception {
        byte[] actualText = data.getBytes();
        return encode(encrypt(actualText));
    }

    public String rsaDecrypt(String data) throws Exception {
        return new String(decode(new String(decrypt(data))));
    }

    public byte[] encrypt(byte[] data) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        return cipher.doFinal(data);
    }

    private String encode(byte[] data){
        return Base64.getEncoder().encodeToString(data);
    }

    private byte[] decode(String data) {
        return Base64.getDecoder().decode(data);
    }

    public byte[] decrypt(String data) throws Exception {
        byte[] actualText = decode(data);
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        return cipher.doFinal(actualText);
    }

}

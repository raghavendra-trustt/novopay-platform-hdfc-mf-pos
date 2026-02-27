package com.in.novopay.hdfc.mf.pos.util;

import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;

@Component
public class TripleDESUtil {

    private final String ALGORITHM = "DESede/ECB/NoPadding";
    private final String CHARSET = "UTF-8";

    public String encrypt(String plainText, String hexKey) throws Exception {
        byte[] plainTextBytes = padPlainText(plainText.getBytes(CHARSET));
        byte[] keyBytes = adjustKeyLength(hexStringToByteArray(hexKey)); // Adjust the key length
        SecretKey key = new SecretKeySpec(keyBytes, "DESede");

        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, key);

        return DatatypeConverter.printHexBinary(cipher.doFinal(plainTextBytes));
    }

    public String decrypt(String hexCipherText, String hexKey) throws Exception {
        byte[] cipherText = hexStringToByteArray(hexCipherText);
        byte[] keyBytes = adjustKeyLength(hexStringToByteArray(hexKey)); // Adjust the key length
        SecretKey key = new SecretKeySpec(keyBytes, "DESede");

        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, key);

        return DatatypeConverter.printHexBinary(cipher.doFinal(cipherText));
    }

    /*public static void main(String[] args) throws Exception {
        String dtmk = "E2D16FDED24111FA508DBE9960D1787E"; // 32 hex chars = 16 bytes
        String encTmk = "26CE789E12C4DDBCDDDE17F7F7692BE9"; // Encrypted key
        String tmk = decrypt(encTmk, dtmk);
        System.out.println("TMK: " + tmk);

        String encTdk = "BBFA72A996800A6C4A9D0CAFF13651F4";
        String tdk = decrypt(encTdk, tmk);
        System.out.println("TDK: " + tdk);

        String plainTract2data = "6521660100053857D30121260360010F";

        String encryptedTract2Data = encrypt(plainTract2data, tdk);
        System.out.println("Encrypted Track2: " + encryptedTract2Data);

        String decryptedText = decrypt(encryptedTract2Data, tdk);
        System.out.println("Decrypted Hex Text: " + decryptedText);
        System.out.println("Decrypted Ascii Text: " + convertHexStringToAscii(decryptedText));
    }*/

    // Adjust key to ensure it's 24 bytes
    private byte[] adjustKeyLength(byte[] keyBytes) {
        if (keyBytes.length == 16) {
            byte[] extendedKey = new byte[24];
            System.arraycopy(keyBytes, 0, extendedKey, 0, 16);
            System.arraycopy(keyBytes, 0, extendedKey, 16, 8); // Repeat first 8 bytes
            return extendedKey;
        }
        return keyBytes;
    }

    public byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4) + Character.digit(s.charAt(i + 1), 16));
        }
        return data;
    }

    public byte[] padPlainText(byte[] plainText) {
        int length = plainText.length;
        int paddingLength = 8 - (length % 8);
        byte[] paddedPlainText = new byte[length + paddingLength];
        System.arraycopy(plainText, 0, paddedPlainText, 0, length);
        return paddedPlainText;
    }

    public String convertHexStringToAscii(String hexString) {
        StringBuilder output = new StringBuilder();
        for (int i = 0; i < hexString.length(); i += 2) {
            String str = hexString.substring(i, i + 2);
            output.append((char) Integer.parseInt(str, 16));
        }
        return output.toString();
    }

}

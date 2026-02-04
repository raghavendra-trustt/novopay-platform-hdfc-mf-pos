package com.in.novopay.mf.hdfc;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class AESKCVGenerator {
    public static void main(String[] args) throws Exception {
        // Hex key provided
        String hexKey = "3438336362623738623465343262373865653832363539363630636637653539";
        byte[] keyBytes = hexStringToByteArray(hexKey);

        // AES-128 key
        SecretKey key = new SecretKeySpec(keyBytes, "AES");

        // Encrypt 16 zero bytes
        byte[] zeroBlock = new byte[16];
        Cipher cipher = Cipher.getInstance("AES/ECB/NoPadding");
        cipher.init(Cipher.ENCRYPT_MODE, key);
        byte[] encrypted = cipher.doFinal(zeroBlock);

        // Take first 6 bytes (or 3–4 depending on spec) as KCV
        byte[] kcv = new byte[6];
        System.arraycopy(encrypted, 0, kcv, 0, 6);

        System.out.println("KCV: " + bytesToHex(kcv));
    }

    private static byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i+1), 16));
        }
        return data;
    }

    private static String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02X", b));
        }
        return sb.toString();
    }
}


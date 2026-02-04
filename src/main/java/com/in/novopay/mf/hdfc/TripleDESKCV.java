package com.in.novopay.mf.hdfc;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class TripleDESKCV {
    public static void main(String[] args) throws Exception {
        // Example: 16-byte double-length 3DES key (hex string)
        String hexKey = "3438336362623738623465343262373865653832363539363630636637653539";
        byte[] keyBytes = hexStringToByteArray(hexKey);

        // Normalize to 24 bytes (3DES requires 24 bytes)
        byte[] fullKey = normalize3DesKey(keyBytes);

        SecretKey key = new SecretKeySpec(fullKey, "DESede");

        // Encrypt 8 zero bytes
        byte[] zeroBlock = new byte[8];
        Cipher cipher = Cipher.getInstance("DESede/ECB/NoPadding");
        cipher.init(Cipher.ENCRYPT_MODE, key);
        byte[] encrypted = cipher.doFinal(zeroBlock);

        // Take first 6 bytes as KCV
        byte[] kcv = new byte[6];
        System.arraycopy(encrypted, 0, kcv, 0, 6);

        System.out.println("KCV: " + bytesToHex(kcv));
    }

    private static byte[] normalize3DesKey(byte[] k) {
        if (k.length == 24) return k; // triple-length
        if (k.length == 16) { // double-length → append first 8
            byte[] out = new byte[24];
            System.arraycopy(k, 0, out, 0, 16);
            System.arraycopy(k, 0, out, 16, 8);
            return out;
        }
        if (k.length == 8) { // single-length → repeat 3 times
            byte[] out = new byte[24];
            System.arraycopy(k, 0, out, 0, 8);
            System.arraycopy(k, 0, out, 8, 8);
            System.arraycopy(k, 0, out, 16, 8);
            return out;
        }
        throw new IllegalArgumentException("3DES key must be 8, 16, or 24 bytes");
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
        for (byte b : bytes) sb.append(String.format("%02X", b));
        return sb.toString();
    }
}

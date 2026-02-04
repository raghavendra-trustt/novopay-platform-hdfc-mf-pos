package com.in.novopay.mf.hdfc;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class KcvUtil {

    public static void main(String[] args) throws Exception {
        // Example input (double-encoded hex string)
        String input = "3438336362623738623465343262373865653832363539363630636637653539";

        // Decode properly
        byte[] keyBytes = decodePossiblyDoubleEncodedHex(input);

        // Normalize to 24 bytes for 3DES
        byte[] fullKey = normalize3DesKey(keyBytes);

        // Encrypt 8 zero bytes
        SecretKey key = new SecretKeySpec(fullKey, "DESede");
        Cipher cipher = Cipher.getInstance("DESede/ECB/NoPadding");
        cipher.init(Cipher.ENCRYPT_MODE, key);
        byte[] encrypted = cipher.doFinal(new byte[8]);

        // Take first 6 bytes as KCV
        byte[] kcv = Arrays.copyOfRange(encrypted, 0, 6);
        System.out.println("KCV: " + bytesToHex(kcv));
    }

    // Detects if the first decode yields ASCII hex, then decodes again
    private static byte[] decodePossiblyDoubleEncodedHex(String s) {
        byte[] first = hexToBytes(s);
        String ascii = new String(first, StandardCharsets.US_ASCII).trim();
        if (ascii.matches("(?i)^[0-9a-f]+$") && ascii.length() % 2 == 0) {
            return hexToBytes(ascii); // second decode
        }
        return first; // single decode
    }

    // Normalize 3DES key to 24 bytes
    private static byte[] normalize3DesKey(byte[] k) {
        if (k.length == 24) return k;
        if (k.length == 16) {
            byte[] out = new byte[24];
            System.arraycopy(k, 0, out, 0, 16);
            System.arraycopy(k, 0, out, 16, 8);
            return out;
        }
        if (k.length == 8) {
            byte[] out = new byte[24];
            System.arraycopy(k, 0, out, 0, 8);
            System.arraycopy(k, 0, out, 8, 8);
            System.arraycopy(k, 0, out, 16, 8);
            return out;
        }
        throw new IllegalArgumentException("3DES key must be 8, 16, or 24 bytes; got " + k.length);
    }

    private static byte[] hexToBytes(String s) {
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

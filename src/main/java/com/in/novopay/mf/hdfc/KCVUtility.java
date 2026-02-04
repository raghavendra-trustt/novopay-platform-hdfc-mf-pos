package com.in.novopay.mf.hdfc;

import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.Arrays;

public class KCVUtility {
    public static void main(String[] args) throws Exception {
        // Example 3DES key (16 or 24 bytes depending on double/triple length)
        byte[] keyBytes = hexStringToByteArray("DCA68732FA5C3D7C4FADE87D4C35F15A"); // 16-byte double-length key

        // For 3DES, if you have 16 bytes, duplicate the first 8 to make 24 bytes
        byte[] fullKey = new byte[24];
        System.arraycopy(keyBytes, 0, fullKey, 0, 16);
        System.arraycopy(keyBytes, 0, fullKey, 16, 8);

        SecretKey key = new SecretKeySpec(fullKey, "DESede");

        // Encrypt a block of zeros
        byte[] zeroBlock = new byte[8]; // 8 bytes of zeros
        Cipher cipher = Cipher.getInstance("DESede/ECB/NoPadding");
        cipher.init(Cipher.ENCRYPT_MODE, key);
        byte[] encrypted = cipher.doFinal(zeroBlock);

        // Take the first 4 bytes as the KCV
        byte[] kcv = new byte[4];
        System.arraycopy(encrypted, 0, kcv, 0, 4);

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


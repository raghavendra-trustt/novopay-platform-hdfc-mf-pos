package com.in.novopay.hdfc.mf.pos.util;

import utils.BytesUtil;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * 安全相关的工具，含有软安全算法
 *
 * @author Jack
 */
public class SecurityUtil {

    /**
     * 做3DES加密
     *
     * @param byteKey
     * @param data
     * @return
     */
    public static byte[] threeDes(byte[] byteKey, byte[] data) {
        // 生成密钥
        SecretKey deskey = new SecretKeySpec(byteKey, "DESede");
        // 加密
        try {
            Cipher c = Cipher.getInstance("DESede/ECB/NoPadding");
            c.init(Cipher.ENCRYPT_MODE, deskey);
            return c.doFinal(data);
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static byte[] threeDesCBC(byte[] byteKey, byte[] data, String transformation, byte[] iv) {
        // 生成密钥
        SecretKey deskey = new SecretKeySpec(byteKey, "DESede");
        // 加密
        try {
            //PKCS5Padding //DES/ECB/NoPadding
            Cipher c = Cipher.getInstance(transformation);
            if (iv != null) {
                c.init(Cipher.ENCRYPT_MODE, deskey, new IvParameterSpec(iv));
            } else {
                c.init(Cipher.ENCRYPT_MODE, deskey);
            }
            return c.doFinal(data);
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static byte[] decrypt(byte[] byteKey, byte[] packagedMessage) throws Exception {
        int blockSize = packagedMessage.length / 8;
        byte[] encryptedMess = new byte[blockSize * 8];
        for (int i = 0; i < blockSize; ++i) {
            byte[] temp = new byte[8];
            byte[] temp2 = new byte[8];
            System.arraycopy(packagedMessage, i * 8, temp, 0, 8);
            temp2 = threeUnDes(byteKey, temp);
            System.arraycopy(temp2, 0, encryptedMess, i * 8, 8);
        }
        return encryptedMess;
    }


    /**
     * 做3DES解密
     *
     * @param byteKey
     * @param data
     * @return
     */
    public static byte[] threeUnDes(byte[] byteKey, byte[] data) {
        // 生成密钥
        SecretKey deskey = new SecretKeySpec(byteKey, "DESede");
        // 加密
        try {
            Cipher c = Cipher.getInstance("DESede/ECB/NoPadding");
            c.init(Cipher.DECRYPT_MODE, deskey);
            return c.doFinal(data);
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * 做DES加密
     *
     * @param byteKey
     * @param data
     * @return
     */
    public static byte[] des(byte[] byteKey, byte[] data) {
        // 生成密钥
        SecretKey deskey = new SecretKeySpec(byteKey, "DES");
        // 加密
        try {
            Cipher c = Cipher.getInstance("DES/ECB/NoPadding");
            c.init(Cipher.ENCRYPT_MODE, deskey);
            return c.doFinal(data);
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 做DES解密
     *
     * @param byteKey
     * @param data
     * @return
     */
    public static byte[] undes(byte[] byteKey, byte[] data) {
        // 生成密钥
        SecretKey deskey = new SecretKeySpec(byteKey, "DES");
        // 加密
        try {
            Cipher c = Cipher.getInstance("DES/ECB/NoPadding");
            c.init(Cipher.DECRYPT_MODE, deskey);
            return c.doFinal(data);
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 计算SHA-1
     *
     * @param data
     * @return
     */
    public static byte[] calcSHA1(byte[] data) {
        MessageDigest sha1Digest = null;
        try {
            sha1Digest = MessageDigest.getInstance("SHA-1");
        } catch (NoSuchAlgorithmException e) {
            return null;
        }
        sha1Digest.update(data);
        return sha1Digest.digest();
    }

    /**
     * 双倍长des加密
     *
     * @param byteKey 密钥
     * @param data    数据
     * @return 加密后数据
     * @throws Exception
     */
    public static byte[] doubleDes(byte[] byteKey, byte[] data) {
        //使用密钥的前16字节，对数据DATA进行加密，得到加密的结果TMP1;
        byte[] temp1 = des(BytesUtil.subBytes(byteKey, 0, 8), data);
        //使用密钥的后16字节，对第一的计算结果TMP1，进行解密，得到解密的结果TMP2
        byte[] temp2 = undes(BytesUtil.subBytes(byteKey, 8, 8), temp1);
        //再次使用密钥的前16字节，对第二次的计算结果TMP2，进行加密，得到加密的结果DEST。DEST就为最终的结果
        byte[] dest = des(BytesUtil.subBytes(byteKey, 0, 8), temp2);
        return dest;
    }


    /**
     * 双倍长des解密
     *
     * @param byteKey 密钥
     * @param data    数据
     * @return 解密后数据
     * @throws Exception
     */
    public static byte[] doubleUnDes(byte[] byteKey, byte[] data) {
        //使用密钥的前16字节，对数据DATA进行解密，得到加密的结果TMP1;
        byte[] temp1 = undes(BytesUtil.subBytes(byteKey, 0, 8), data);
        //使用密钥的后16字节，对第一的计算结果TMP1，进行加密，得到加密的结果TMP2
        byte[] temp2 = des(BytesUtil.subBytes(byteKey, 8, 8), temp1);
        //再次使用密钥的前16字节，对第二次的计算结果TMP2，进行解密，得到解密的结果DEST。DEST就为最终的结果
        byte[] dest = undes(BytesUtil.subBytes(byteKey, 0, 8), temp2);
        return dest;
    }
}

package com.anju.springboot.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

/**
 * @Author: ZhongZhen
 * @PackageName: com.xx.springboot.utils
 * @ClassName: HashUtils
 * @Time: 2023/8/17 22:27
 * @Description: TODO
 * @Version: 1.0
 */
@Slf4j
@Component
public class HashUtils {

    @Autowired
    private RedisUtil redisUtil;

    public String sha256Hash(String password, String salt) {
        try {

            if (salt == null) {
                salt = getSalt();
                redisUtil.set("Anju_salt",salt);
            }

            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] encodedHash = digest.digest((salt + password).getBytes(StandardCharsets.UTF_8));
            return bytesToHex(encodedHash);
        } catch (NoSuchAlgorithmException e) {
            log.error("错误信息：{}",e.getMessage());
            return null;
        }
    }

    /**
     * 随机生成一个10位的盐
     */
    public String getSalt() {
        char[] chars = ("ABCDEFGHIJKLMNOPQRSTUVWXYZabcderfhijklmnopqrstuvwxyz" + "1234567890!@#$%^&*()_+").toCharArray();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 10; i++) {
            char aChar = chars[new Random().nextInt(chars.length)];
            sb.append(aChar);
        }
        return sb.toString();
    }

    /**
     * 将字节数组转换为十六进制字符串
     * @param bytes
     * @return
     */
    private String bytesToHex(byte[] bytes) {
        StringBuilder result = new StringBuilder();
        for (byte b : bytes) {
            result.append(String.format("%02x", b));
        }
        return result.toString();
    }

    public boolean isPasswordValid(String inputPassword, String storedHashedPassword,String salt) {
        String hashedInputPassword = sha256Hash(inputPassword,salt);
        System.out.println("hashedInputPassword = " + hashedInputPassword);
        assert hashedInputPassword != null;
        return hashedInputPassword.equals(storedHashedPassword);
    }



}

package kr.co.cleaning.core.utils;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class AESUtil {

    private static SecretKeySpec secretKey;
    private static final String ALGORITHM = "AES";

    public AESUtil(String key) {
        AESUtil.secretKey = new SecretKeySpec(key.getBytes(), ALGORITHM);
    }

    public static String encrypt(String plainText) throws Exception {

    	Cipher cipher		= Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        byte[] encrypted	= cipher.doFinal(plainText.getBytes());

        return Base64.getEncoder().encodeToString(encrypted);
    }

    public static String decrypt(String cipherText) throws Exception {

        // 빈 문자열 또는 유효하지 않은 Base64 문자열이면 빈 문자열 반환
        if (cipherText == null || cipherText.trim().isEmpty() || cipherText.trim().length() < 4) {
            return "";
        }

        try {
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            byte[] decoded = Base64.getDecoder().decode(cipherText.trim());
            return new String(cipher.doFinal(decoded));
        } catch (IllegalArgumentException e) {
            // Base64 디코딩 실패 시 빈 문자열 반환
            return "";
        }
    }

    public static String urlEnc(String plainText) throws Exception {
    	String value	= SUtils.nvl(plainText);
    	value			= encrypt(value);
    	value			= URLEncoder.encode(value, StandardCharsets.UTF_8.toString());
    	return value;
    }

    public static String urlDec(String cipherText) throws Exception {

        String value = SUtils.nvl(cipherText);

        // 빈 문자열이면 빈 문자열 반환 (Base64 디코딩 오류 방지)
        if (SUtils.isNvl(value)) {
            return "";
        }

        // URL 인코딩된 경우만 decode
        if (value.contains("%")) {
            value = URLDecoder.decode(value, StandardCharsets.UTF_8.toString());
        }

        return decrypt(value);
    }

}

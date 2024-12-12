package com.event.cia103g1springboot.ezpay.ezpaycontroller;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Map;
import java.util.TreeMap;

public class CheckMacValueGenerator {
    private final String hashKey;
    private final String hashIv;

    // 建構子
    public CheckMacValueGenerator(String hashKey, String hashIv) {
        this.hashKey = hashKey;
        this.hashIv = hashIv;
    }

    // 生成 CheckMacValue 的方法
    public String generateCheckMacValue(Map<String, String> data) {
        // 1. 將參數依照參數名稱按照字母順序排序
        TreeMap<String, String> sortedData = new TreeMap<>(data);

        // 2. 組合參數名稱與參數值
        StringBuilder checkString = new StringBuilder();
        checkString.append("HashKey=").append(hashKey);

        for (Map.Entry<String, String> entry : sortedData.entrySet()) {
            checkString.append("&").append(entry.getKey()).append("=").append(entry.getValue());
        }

        checkString.append("&HashIV=").append(hashIv);

        // 3. 將字串進行 URL encode
        String urlEncodedString;
        try {
            urlEncodedString = URLEncoder.encode(checkString.toString(), "UTF-8").toLowerCase();
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("URL encode 失敗", e);
        }

        // 4. 將 encode 後的字串轉換為 SHA256
        String sha256;
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(urlEncodedString.getBytes());
            sha256 = bytesToHex(md.digest());
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 計算失敗", e);
        }

        // 5. 轉大寫
        return sha256.toUpperCase();
    }

    // 輔助方法：將 byte 陣列轉換為 16 進位字串
    private String bytesToHex(byte[] bytes) {
        StringBuilder result = new StringBuilder();
        for (byte b : bytes) {
            result.append(String.format("%02x", b));
        }
        return result.toString();
    }
}
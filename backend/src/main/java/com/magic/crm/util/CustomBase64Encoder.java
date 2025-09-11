package com.magic.crm.util;

public class CustomBase64Encoder {

    // 私有字符表（与 JS 的 _keyStr 一致）
    private static final String KEY_STR = "9876543210+XYZDEFGHIJKLMNOPQRSTUVWABC/abcdefghijklmnopqrstuvwxyz=";

    /**
     * 编码输入字符串为自定义 Base64 格式
     * 
     * @param input 原始字符串
     * @return 编码后的字符串
     */
    public static String encode(String input) {
        // String utf8Input = utf8Encode(input); // 模拟 JS 的 _utf8_encode
        byte[] data = input.getBytes();
        var output = new char[(data.length + 2) / 3 * 4];

        int j = 0;
        int c1, c2, c3;
        int d1, d2, d3, d4;
        for (var i = 0; i < data.length; i += 3) {
            c1 = data[i] & 0xFF;
            c2 = 0;
            c3 = 0;
            if ((i + 1) < data.length) {
                c2 = data[i + 1] & 0xFF;
                if ((i + 2) < data.length) {
                    c3 = data[i + 2] & 0xFF;
                }
            }
            d1 = c1 >> 2;
            d2 = ((c1 & 3) << 4) | (c2 >> 4);
            d3 = ((c2 & 15) << 2) | (c3 >> 6);
            d4 = (c3 & 63);

            if ((i + 1) >= data.length) {
                d3 = 64;
                d4 = 64;
            } else if ((i + 2) >= data.length) {
                d4 = 64;
            }
            output[j++] = KEY_STR.charAt(d1);
            output[j++] = KEY_STR.charAt(d2);
            output[j++] = KEY_STR.charAt(d3);
            output[j++] = KEY_STR.charAt(d4);
        }

        return String.valueOf(output);
    }

}
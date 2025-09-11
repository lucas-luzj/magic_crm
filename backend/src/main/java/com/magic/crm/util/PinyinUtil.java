package com.magic.crm.util;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.HanyuPinyinVCharType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

/**
 * 拼音工具类
 * 使用pinyin4j库获取中文字符的拼音首字母
 */
public class PinyinUtil {

    private static final HanyuPinyinOutputFormat FORMAT = new HanyuPinyinOutputFormat();

    static {
        // 设置拼音输出格式
        FORMAT.setCaseType(HanyuPinyinCaseType.UPPERCASE);
        FORMAT.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
        FORMAT.setVCharType(HanyuPinyinVCharType.WITH_V);
    }

    /**
     * 获取中文字符串的拼音首字母
     * 
     * @param chinese 中文字符串
     * @return 拼音首字母字符串
     */
    public static String getFirstLetters(String chinese) {
        if (chinese == null || chinese.isEmpty()) {
            return "";
        }

        StringBuilder result = new StringBuilder();
        for (char c : chinese.toCharArray()) {
            if (isChinese(c)) {
                result.append(getFirstLetter(c));
            } else if (Character.isLetter(c)) {
                result.append(Character.toUpperCase(c));
            } else if (Character.isDigit(c)) {
                result.append(c);
            }
            // 其他字符（标点符号等）忽略
        }
        return result.toString();
    }

    /**
     * 获取中文字符串的精确拼音首字母（使用pinyin4j库）
     * 
     * @param chinese 中文字符串
     * @return 拼音首字母字符串
     */
    public static String getAccurateFirstLetters(String chinese) {
        return getFirstLetters(chinese);
    }

    /**
     * 获取中文字符串的完整拼音
     * 
     * @param chinese 中文字符串
     * @return 完整拼音字符串，以空格分隔
     */
    public static String getFullPinyin(String chinese) {
        if (chinese == null || chinese.isEmpty()) {
            return "";
        }

        StringBuilder result = new StringBuilder();
        for (char c : chinese.toCharArray()) {
            if (isChinese(c)) {
                try {
                    String[] pinyinArray = PinyinHelper.toHanyuPinyinStringArray(c, FORMAT);
                    if (pinyinArray != null && pinyinArray.length > 0) {
                        result.append(pinyinArray[0]).append(" ");
                    } else {
                        result.append(c).append(" ");
                    }
                } catch (BadHanyuPinyinOutputFormatCombination e) {
                    result.append(c).append(" ");
                }
            } else {
                result.append(c).append(" ");
            }
        }
        return result.toString().trim();
    }

    /**
     * 获取单个中文字符的拼音首字母
     * 
     * @param c 中文字符
     * @return 拼音首字母
     */
    private static char getFirstLetter(char c) {
        try {
            String[] pinyinArray = PinyinHelper.toHanyuPinyinStringArray(c, FORMAT);
            if (pinyinArray != null && pinyinArray.length > 0) {
                return pinyinArray[0].charAt(0);
            }
        } catch (BadHanyuPinyinOutputFormatCombination e) {
            // 如果转换失败，使用Unicode范围估算
            return getUnicodeBasedLetter(c);
        }

        // 如果没有拼音，使用Unicode范围估算
        return getUnicodeBasedLetter(c);
    }

    /**
     * 基于Unicode范围获取拼音首字母（备用方案）
     */
    private static char getUnicodeBasedLetter(char c) {
        int unicode = (int) c;

        if (unicode >= 0x4E00 && unicode <= 0x9FFF) {
            // 简化的Unicode范围映射
            if (unicode >= 0x4E00 && unicode <= 0x4FFF)
                return 'A';
            if (unicode >= 0x5000 && unicode <= 0x51FF)
                return 'B';
            if (unicode >= 0x5200 && unicode <= 0x53FF)
                return 'C';
            if (unicode >= 0x5400 && unicode <= 0x55FF)
                return 'D';
            if (unicode >= 0x5600 && unicode <= 0x57FF)
                return 'F';
            if (unicode >= 0x5800 && unicode <= 0x59FF)
                return 'G';
            if (unicode >= 0x5A00 && unicode <= 0x5BFF)
                return 'H';
            if (unicode >= 0x5C00 && unicode <= 0x5DFF)
                return 'J';
            if (unicode >= 0x5E00 && unicode <= 0x5FFF)
                return 'K';
            if (unicode >= 0x6000 && unicode <= 0x61FF)
                return 'L';
            if (unicode >= 0x6200 && unicode <= 0x63FF)
                return 'M';
            if (unicode >= 0x6400 && unicode <= 0x65FF)
                return 'N';
            if (unicode >= 0x6600 && unicode <= 0x67FF)
                return 'P';
            if (unicode >= 0x6800 && unicode <= 0x69FF)
                return 'Q';
            if (unicode >= 0x6A00 && unicode <= 0x6BFF)
                return 'R';
            if (unicode >= 0x6C00 && unicode <= 0x6DFF)
                return 'S';
            if (unicode >= 0x6E00 && unicode <= 0x6FFF)
                return 'T';
            if (unicode >= 0x7000 && unicode <= 0x71FF)
                return 'W';
            if (unicode >= 0x7200 && unicode <= 0x73FF)
                return 'X';
            if (unicode >= 0x7400 && unicode <= 0x75FF)
                return 'Y';
            if (unicode >= 0x7600 && unicode <= 0x9FFF)
                return 'Z';
        }

        return 'A'; // 默认返回A
    }

    /**
     * 判断字符是否为中文
     * 
     * @param c 字符
     * @return 是否为中文
     */
    private static boolean isChinese(char c) {
        return c >= 0x4E00 && c <= 0x9FFF;
    }
}
package com.magic.crm.util;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * 拼音工具类测试
 */
public class PinyinUtilTest {

    @Test
    public void testGetFirstLetters() {
        // 测试常见姓名
        assertEquals("ZS", PinyinUtil.getFirstLetters("张三"));
        assertEquals("LS", PinyinUtil.getFirstLetters("李四"));
        assertEquals("WW", PinyinUtil.getFirstLetters("王五"));
        assertEquals("ZL", PinyinUtil.getFirstLetters("赵六"));

        // 测试混合中英文
        assertEquals("ZSANA", PinyinUtil.getFirstLetters("张sanA"));
        assertEquals("ABC", PinyinUtil.getFirstLetters("abc"));

        // 测试空字符串和null
        assertEquals("", PinyinUtil.getFirstLetters(""));
        assertEquals("", PinyinUtil.getFirstLetters(null));

        // 测试复杂姓名
        assertEquals("OYX", PinyinUtil.getFirstLetters("欧阳修"));
        assertEquals("SMQ", PinyinUtil.getFirstLetters("司马迁"));
    }

    @Test
    public void testGetAccurateFirstLetters() {
        // 测试精确拼音首字母（应该与getFirstLetters结果一致）
        assertEquals("ZS", PinyinUtil.getAccurateFirstLetters("张三"));
        assertEquals("LS", PinyinUtil.getAccurateFirstLetters("李四"));
        assertEquals("WW", PinyinUtil.getAccurateFirstLetters("王五"));
    }

    @Test
    public void testGetFullPinyin() {
        // 测试完整拼音
        String fullPinyin = PinyinUtil.getFullPinyin("张三");
        assertNotNull(fullPinyin);
        assertTrue(fullPinyin.contains("ZHANG"));
        assertTrue(fullPinyin.contains("SAN"));

        // 测试单个字符
        String singleChar = PinyinUtil.getFullPinyin("中");
        assertNotNull(singleChar);
        assertTrue(singleChar.length() > 0);

        // 测试空字符串
        assertEquals("", PinyinUtil.getFullPinyin(""));
        assertEquals("", PinyinUtil.getFullPinyin(null));
    }

    @Test
    public void testSpecialCharacters() {
        // 测试数字和特殊字符
        assertEquals("123", PinyinUtil.getFirstLetters("123"));
        assertEquals("", PinyinUtil.getFirstLetters("!@#"));

        // 测试混合内容
        assertEquals("Z123S", PinyinUtil.getFirstLetters("张123三"));
    }
}
package com.xiaowu.frameworkjava.utils;

import org.apache.commons.lang3.StringUtils;
import org.springframework.util.AntPathMatcher;

public class StringUtil {


    /**
     * 判断url是否与规则匹配
     *
     * 匹配规则：
     * 精确匹配
     * 匹配规则中包含 ? 表示任意单个字符;
     * 匹配规则中包含 * 表示一层路径内的任意字符串，不可跨层级;
     * 匹配规则中包含 ** 表示任意层路径的任意字符，可跨层级
     *
     * @param pattern 匹配规则
     * @param url 需要匹配的url
     * @return 是否匹配
     */
    public static boolean isMatch(String pattern, String url) {
        if (StringUtils.isEmpty(url) || StringUtils.isEmpty(pattern)) {
            return false;
        }
        AntPathMatcher matcher = new AntPathMatcher();
        return matcher.match(pattern, url);
    }
}

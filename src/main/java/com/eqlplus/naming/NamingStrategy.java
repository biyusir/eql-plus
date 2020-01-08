package com.eqlplus.naming;

import lombok.Data;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Data
/**
 * 命名策略
 */
public class NamingStrategy {

    /**
     * 小驼峰
     *
     * @param name
     * @return
     */
    public String lowerCamel(String name) {
        String[] arrayLowerCamel = name.toLowerCase().split("_");

        List<String> result = new ArrayList<>();
        Arrays.stream(arrayLowerCamel).forEach(oneString -> {
            if (result.size() == 0) {
                result.add(oneString);
                return;
            }

            result.add(firstStringUpCaseCamel(oneString));
        });

        return StringUtils.join(result, "");
    }

    /**
     * 大驼峰
     */
    public String bigCamel(String name) {
        String[] arrayLowerCamel = name.toLowerCase().split("_");

        List<String> result = new ArrayList<>();
        Arrays.stream(arrayLowerCamel).forEach(oneString -> {
            result.add(firstStringUpCaseCamel(oneString));
        });

        return StringUtils.join(result, "");
    }

    /**
     * 仅仅第一个首字母大写
     */
    public String firstStringUpCaseCamel(String name) {
        if (StringUtils.isNotBlank(name)) {
            return name.substring(0, 1).toUpperCase() + name.substring(1);
        }
        return name;
    }

    private static Pattern humpPattern = Pattern.compile("[A-Z]");

    /**
     * 大驼峰转下划线
     */
    public String smallCamelToUnderline(String name) {
        StringBuffer sb = new StringBuffer();
        String first = name.substring(0, 1);
        String other = name.substring(1);
        Matcher matcher = humpPattern.matcher(other);
        while (matcher.find()) {
            matcher.appendReplacement(sb, "_" + matcher.group(0).toLowerCase());
        }
        matcher.appendTail(sb);
        return first + sb.toString();
    }

    public static void main(String[] args) {
        System.out.println(new NamingStrategy().smallCamelToUnderline("AAA"));
    }
}

package com.naming;

import lombok.Data;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Data
public class NamingStrategy {

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


    public String bigCamel(String name) {
        String[] arrayLowerCamel = name.toLowerCase().split("_");

        List<String> result = new ArrayList<>();
        Arrays.stream(arrayLowerCamel).forEach(oneString -> {
            result.add(firstStringUpCaseCamel(oneString));
        });

        return StringUtils.join(result, "");
    }

    public String firstStringUpCaseCamel(String name) {
        if (StringUtils.isNotBlank(name)) {
            return name.substring(0, 1).toUpperCase() + name.substring(1);
        }
        return name;
    }


}

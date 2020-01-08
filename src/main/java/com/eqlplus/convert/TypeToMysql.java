package com.eqlplus.convert;

import com.eqlplus.base.ToDbType;

public class TypeToMysql  {

    public static ToDbType processTypeConvert(Class<?> fieldType) {
        String lowerCase = fieldType.getSimpleName().toLowerCase();
        if (lowerCase.equals("Integer".toLowerCase())){
            return ToDbType.INTEGER;
        }
        if (lowerCase.equals("STRING".toLowerCase())){
            return ToDbType.VARCHAR;
        }
        if (lowerCase.equals("BOOLEAN".toLowerCase())){
            return ToDbType.BOOLEAN;
        }
        if (lowerCase.equals("BigDecimal".toLowerCase())){
            return ToDbType.BIG_DECIMAL;
        }
        if (lowerCase.toLowerCase().equals("float".toLowerCase())){
            return ToDbType.BIG_DECIMAL;
        }
        if (lowerCase.equals("double".toLowerCase())){
            return ToDbType.DOUBLE;
        }
        if (lowerCase.equals("date")){
            return ToDbType.DATE;
        }
        return ToDbType.VARCHAR;
    }
}

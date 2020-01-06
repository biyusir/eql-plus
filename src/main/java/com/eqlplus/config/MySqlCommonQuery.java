package com.eqlplus.config;

import lombok.Data;

@Data
public class MySqlCommonQuery {

    public static String tablesSql() {
        return "show table status WHERE 1=1 ";
    }

    public static String queryTablesColumn(){
        return "Name";
    }

    public static String tableFieldsSql() {
        return "show full fields from `%s`";
    }

    public static String tableType(){
        return "Type";
    }

    public static String tableComment(){
        return "Comment";
    }

    public static String tableField(){
        return "Field";
    }
}

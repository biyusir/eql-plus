package com.eqlplus.base;

import lombok.Getter;

public enum ToDbType {

    INTEGER("Integer", true,true),
    LONG("Long", true,true),
    FLOAT("Float", true,false),
    DOUBLE("double", true,true),
    BOOLEAN("tinyint(1)", false,false),
    VARCHAR("VARCHAR", true,false),
    BLOB("Blob", false,true),
    CLOB("Clob", false,true),
    BYTE_ARRAY("byte[]", false,false),
    DATE("Date", false,false),
    BIG_INTEGER("BigInteger", true,true),
    BIG_DECIMAL("decimal", true,true);

    @Getter private String type;
    @Getter private boolean supportSize;
    @Getter private boolean supportUnsigned;

    ToDbType(String type, boolean supportSize,boolean supportUnsigned) {
        this.type = type;
        this.supportSize = supportSize;
        this.supportUnsigned = supportUnsigned;
    }
}

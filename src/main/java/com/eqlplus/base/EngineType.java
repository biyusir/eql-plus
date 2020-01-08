package com.eqlplus.base;

/**
 * 引擎枚举类型
 */
public enum EngineType {
    INNODB("INNODB");
    String engineName;

    EngineType(String engineName) {
        this.engineName = engineName;
    }
}

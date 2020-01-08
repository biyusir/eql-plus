package com.eqlplus.config;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GlobalConfig {
    //基础包  src.main.java
    private String basePackage;
    //指定存储service包位置
    private String servicePackage;
    //指定存储dao包位置
    private String daoPackage;
    //指定存储bean包位置
    private String beanPackage;
    //指定存储controller包位置
    private String controllerPackage;
    //指定存储dto包位置
    private String dtoPackage;

    public String getServicePackage() {
        return servicePackage;
    }

    public String getDaoPackage() {
        return daoPackage;
    }

    public String getBeanPackage() {
        return beanPackage;
    }

    public String getControllerPackage() {
        return controllerPackage;
    }

    public String getDtoPackage() {
        return dtoPackage;
    }

    public String getBasePackage() {
        return basePackage;
    }
}

package com.config;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GlobalBeanConfig implements GlobalConfig {
    //基础包  src.main.java
    private String basePackage;

    private String servicePackage;

    private String daoPackage;

    private String beanService;

    private String controllerPackage;


    //要保存的servicePackage
    @Override
    public String getServicePackage() {
        return servicePackage;
    }

    @Override
    public String getDaoPackage() {
        return daoPackage;
    }

    @Override
    public String getBeanPackage() {
        return beanService;
    }

    @Override
    public String getControllerPackage() {
        return controllerPackage;
    }

    public String getBasePackage() {
        return basePackage;
    }
}

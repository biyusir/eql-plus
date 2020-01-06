package com.eqlplus.example;

import com.alibaba.druid.pool.DruidDataSource;
import com.eqlplus.config.GlobalBeanConfig;
import com.eqlplus.config.GlobalConfig;
import com.eqlplus.config.RequireConfig;
import com.eqlplus.run.AutoGenerate;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public class Demo1 {
    public static void main(String[] args) {
        log.info("开始执行程序..........");
        List<String> specialTables = new ArrayList<>();
        RequireConfig requireConfig = RequireConfig.builder()
                .needBean(true)
                .needComment(true)
                .needController(true)
                .needDao(true)
                .needService(true)
                .needDto(true)
                .specialTables(specialTables)
                .build();

        DruidDataSource dataSource = new DruidDataSource();
        dataSource.setUrl("jdbc:mysql://rm-m5ei8exr4705z138aso.mysql.rds.aliyuncs.com/blog?serverTimezone=Asia/Shanghai&characterEncoding=utf-8");
        dataSource.setUsername("root");
        dataSource.setPassword("Bibi330202");

        GlobalConfig globalBeanConfig = GlobalBeanConfig.builder()
                .basePackage("src.main.java")
                .beanService("com.eqlplus.beans")
                .servicePackage("com.eqlplus.service")
                .controllerPackage("com.eqlplus.controller")
                .daoPackage("com.eqlplus.dao")
                .dtoPackage("com.eqlplus.dto")
                .build();
        AutoGenerate autoGenerate = new AutoGenerate(requireConfig, globalBeanConfig, dataSource);

        autoGenerate.queryTablesAndExecute();
        log.info("执行程序结束..........");
    }
}
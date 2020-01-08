package com.eqlplus.annotation;

import com.eqlplus.base.EngineType;

import java.lang.annotation.*;

/**
 * 被注解的bean会反向生成table
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface TableGenerate {
    String comment() default "";//表注解

    EngineType engine() default EngineType.INNODB;  //默认innodb

    String charset() default "utf8mb4";             //默认utf8mb4

    boolean writeToDocSql() default false; //是否将该建表语句放到doc/doc.sql中

    boolean writeAppend() default false;    //写入文件是追加吗

    boolean reCreate() default false; //如果表存在要不要重新创建
}

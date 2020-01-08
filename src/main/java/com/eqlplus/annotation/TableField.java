package com.eqlplus.annotation;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface TableField {

    int size() default 0;   //长度

    boolean isUnsigned() default true;

    String comment() default ""; //注解

    boolean notNull() default false;    //是否非空

    boolean isPrimaryKey() default false; //是否主键
}

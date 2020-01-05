package com.run;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.fastjson.JSONObject;
import com.base.IColumnType;
import com.config.*;
import com.convert.MySqlTypeConvert;
import com.generate.JavaFileWriter;
import com.naming.NamingStrategy;
import lombok.extern.slf4j.Slf4j;
import lombok.val;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class AutoGenerate {
    private RequireConfig config;
    private GlobalConfig globalBeanConfig;
    private NamingStrategy namingStrategy = new NamingStrategy();

    private DataSource dataSource;
    private List<TableInfo> tableInfos;

    public AutoGenerate(RequireConfig config, GlobalConfig globalBeanConfig, DataSource dataSource) {
        this.config = config;
        this.globalBeanConfig = globalBeanConfig;
        this.dataSource = dataSource;
    }

    public void queryTableInfo(String className) {
        String queryTable = String.format(MySqlCommonQuery.tableFieldsSql(), className);
        List<TableInfo> tableInfos = new ArrayList<>();
        try (val connection = this.dataSource.getConnection();
             val statement = connection.createStatement();
             val result = statement.executeQuery(queryTable)) {

            while (result.next()) {
                val builder = TableInfo.builder();

                builder.fileType(result.getString(MySqlCommonQuery.tableType()));

                builder.fieldName(namingStrategy.lowerCamel(result.getString(MySqlCommonQuery.tableField().toLowerCase())));

                builder.comment(result.getString(MySqlCommonQuery.tableComment()));

                val tableInfo = builder.build();
                tableInfos.add(tableInfo);
            }
            this.tableInfos = tableInfos;
        } catch (SQLException e) {
            log.info("创建表信息中间异常..........");
            e.printStackTrace();
        }
    }

    public void execute(String className) {
        className = namingStrategy.firstStringUpCaseCamel(className);
        this.queryTableInfo(className);
        log.info("开始执行程序..........");
        if (config.isNeedBean()) {
            className = namingStrategy.bigCamel(className);
            log.info("开始创建bean..........");
            this.createBean(className);
            if (config.isNeedComment()) {
                log.info("开始创建bean注释..........");
            }
            if (config.isNeedDao()) {
                this.createDao(className);
                log.info("开始创建dao..........");
            }
            if (config.isNeedService()) {
                this.createService(className);
                log.info("开始创建service方法..........");
            }
            if (config.isNeedController()) {
                this.createController(className);
                log.info("开始创建controller..........");
                if (config.isNeedDto()) {
                    log.info("开始创建dto..........");
                }
            }
        }
    }

    public void createDao(String className) {
        List<String> importNames = new ArrayList<>();
        importNames.add("java.util.List");
        importNames.add(this.globalBeanConfig.getBeanPackage() + "." + className);


        JavaFileWriter javaFileWriter = new JavaFileWriter();

        JSONObject jsonObject = new JSONObject();

        jsonObject.put("annotations", "");
        jsonObject.put("importNames", importNames);

        List<Object> fields = new ArrayList<>();
        jsonObject.put("fields", fields);

        List<Object> methods = new ArrayList<>();
        methods.add("List<" + className + "> query" + className + "List();");
        jsonObject.put("methods", methods);

        javaFileWriter.generateJava(className,
                "dao.vm",
                this.globalBeanConfig.getBasePackage(),
                this.globalBeanConfig.getDaoPackage(),
                "Dao",
                jsonObject);
        log.info("创建dao..." + className + ".....完毕");
    }

    public void createBean(String className) {
        MySqlTypeConvert mySqlTypeConvert = new MySqlTypeConvert();
        this.tableInfos.forEach(tableInfo -> {
            IColumnType iColumnType = mySqlTypeConvert.processTypeConvert(tableInfo.getFileType());
            tableInfo.setSqlType(iColumnType.getPkg() == null ? iColumnType.getType() : iColumnType.getPkg());
        });

        List<String> importNames = new ArrayList<>();
        importNames.add("lombok.*");

        List<String> fields = new ArrayList<>();
        this.tableInfos.forEach(o -> fields.add(o.getSqlType() + " " + o.getFieldName() + "; //" + o.getComment()));

        JavaFileWriter javaFileWriter = new JavaFileWriter();

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("annotations", "@Data @AllArgsConstructor @NoArgsConstructor @Builder");
        jsonObject.put("importNames", importNames);
        jsonObject.put("fields", fields);

        jsonObject.put("methods", new ArrayList<>());

        javaFileWriter.generateJava(className,
                "beans.vm",
                this.globalBeanConfig.getBasePackage(),
                this.globalBeanConfig.getBeanPackage(),
                "",
                jsonObject);

        log.info("创建bean..." + className + ".....完毕");
    }

    public void createService(String className) {
        List<String> importNames = new ArrayList<>();
        importNames.add("java.util.List");
        importNames.add(this.globalBeanConfig.getBeanPackage() + "." + className);
        importNames.add("org.springframework.stereotype.Service");
        importNames.add("org.springframework.beans.factory.annotation.Autowired");
        importNames.add(this.globalBeanConfig.getDaoPackage() + "." + className + "Dao");
        List<String> fields = new ArrayList<>();
        fields.add("@Autowired " + className + "Dao dao");

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("annotations", "@Service");
        jsonObject.put("importNames", importNames);
        jsonObject.put("fields", fields);

        List<Object> methods = new ArrayList<>();
        methods.add("public List<" + className + "> query" + className + "List(){\n\t return dao.query" + className + "List();\n\t}");
        jsonObject.put("methods", methods);

        JavaFileWriter javaFileWriter = new JavaFileWriter();

        javaFileWriter.generateJava(className
                , "service.vm"
                , this.globalBeanConfig.getBasePackage()
                , this.globalBeanConfig.getServicePackage()
                , "Service"
                , jsonObject);
        log.info("创建service..." + className + ".....完毕");
    }


    public void createController(String className) {
        List<String> importNames = new ArrayList<>();
        importNames.add("java.util.List");
        importNames.add(this.globalBeanConfig.getBeanPackage() + "." + className);
        importNames.add("org.springframework.web.bind.annotation.RequestMapping");
        importNames.add("org.springframework.web.bind.annotation.RestController");
        importNames.add("org.springframework.beans.factory.annotation.Autowired");

        importNames.add(this.globalBeanConfig.getServicePackage() + "." + className + "Service");
        List<String> fields = new ArrayList<>();
        fields.add("@Autowired " + className + "Service service");

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("annotations", "@RestController\n@RequestMapping(\"/" + className + "Controller\")");
        jsonObject.put("importNames", importNames);
        jsonObject.put("fields", fields);

        List<Object> methods = new ArrayList<>();
        methods.add("@RequestMapping(\"/query" + className + "List\")\n\tpublic List<" + className + "> query" + className + "List(){\n\t return service.query" + className + "List();\n\t}");
        jsonObject.put("methods", methods);

        JavaFileWriter javaFileWriter = new JavaFileWriter();
        javaFileWriter.generateJava(className,
                "controller.vm",
                this.globalBeanConfig.getBasePackage(),
                this.globalBeanConfig.getControllerPackage(),
                "Controller",
                jsonObject);

        log.info("创建controller..." + className + ".....完毕'");

    }

    public static void main(String[] args) {
        RequireConfig requireConfig = RequireConfig.builder()
                .needBean(true)
                .needComment(true)
                .needController(true)
                .needDao(true)
                .needService(true)
                .needDto(true)
                .build();

        DruidDataSource dataSource = new DruidDataSource();
        dataSource.setUrl("jdbc:mysql://rm-m5ei8exr4705z138aso.mysql.rds.aliyuncs.com/blog?serverTimezone=Asia/Shanghai&characterEncoding=utf-8");
        dataSource.setUsername("root");
        dataSource.setPassword("Bibi330202");

        GlobalConfig globalBeanConfig = GlobalBeanConfig.builder()
                .basePackage("src.main.java")
                .beanService("com.beans")
                .servicePackage("com.service")
                .controllerPackage("com.controller")
                .daoPackage("com.dao")
                .build();
        AutoGenerate autoGenerate = new AutoGenerate(requireConfig, globalBeanConfig, dataSource);

        autoGenerate.execute("tag");

    }

}

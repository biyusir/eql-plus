package com.eqlplus.run;

import com.alibaba.fastjson.JSONObject;
import com.eqlplus.base.IColumnType;
import com.eqlplus.config.GlobalConfig;
import com.eqlplus.config.MySqlCommonQuery;
import com.eqlplus.config.RequireConfig;
import com.eqlplus.config.TableInfo;
import com.eqlplus.convert.MySqlTypeConvert;
import com.eqlplus.generate.JavaFileWriter;
import com.eqlplus.naming.NamingStrategy;
import lombok.extern.slf4j.Slf4j;
import lombok.val;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Slf4j@SuppressWarnings("access")
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

    public void execute() {
        if (this.config.getSpecialTables().size() != 0) {
            this.config.getSpecialTables().forEach(this::execute);
            return;
        }
        String queryTables = MySqlCommonQuery.tablesSql();
        List<String> tables = new ArrayList<>();

        try (val connection = this.dataSource.getConnection();
             val statement = connection.createStatement();
             val result = statement.executeQuery(queryTables)) {

            while (result.next()) {
                tables.add(result.getString(MySqlCommonQuery.queryTablesColumn()));
            }
        } catch (SQLException e) {
            log.info("创建表信息中间异常..........");
            e.printStackTrace();
        }

        tables.forEach(this::execute);
    }


    private void execute(String className) {
        className = namingStrategy.firstStringUpCaseCamel(className);
        if (config.isNeedBean()) {
            this.queryTableInfo(className);
            className = namingStrategy.bigCamel(className);
            this.createBean(className);
            if (config.isNeedDao()) {
                this.createDao(className);
            }
            if (config.isNeedService()) {
                this.createService(className);
            }
            if (config.isNeedDto()) {
                this.createDto(className);
                if (config.isNeedController()) {
                    this.createController(className);
                }
            }
        }
    }

    public void createDto(String className) {
        log.info("开始创建" + className + "Dto..........");
        List<String> importNames = new ArrayList<>();
        importNames.add("lombok.*");
        importNames.add(this.globalBeanConfig.getBeanPackage() + "." + className);

        JavaFileWriter javaFileWriter = new JavaFileWriter();

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("annotations", "@Data @AllArgsConstructor @NoArgsConstructor @Builder");
        jsonObject.put("importNames", importNames);
        jsonObject.put("fields", this.tableInfos);

        jsonObject.put("methods", new ArrayList<>());

        javaFileWriter.generateJava(className,
                "dto.vm",
                this.globalBeanConfig.getBasePackage(),
                this.globalBeanConfig.getDtoPackage(),
                "Dto",
                jsonObject,
                config);

        log.info("创建Dto..." + className + "Dto.....完毕");
    }

    public void createDao(String className) {
        log.info("开始创建" + className + "dao..........");
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
                jsonObject,
                config);
        log.info("创建dao..." + className + "Dao.....完毕");
    }

    public void createBean(String className) {
        log.info("开始创建" + className + "Bean..........");
        MySqlTypeConvert mySqlTypeConvert = new MySqlTypeConvert();
        this.tableInfos.forEach(tableInfo -> {
            IColumnType iColumnType = mySqlTypeConvert.processTypeConvert(tableInfo.getFileType());
            tableInfo.setSqlType(iColumnType.getPkg() == null ? iColumnType.getType() : iColumnType.getPkg());
        });

        List<String> importNames = new ArrayList<>();
        importNames.add("lombok.*");

        List<String> fields = new ArrayList<>();
        this.tableInfos.forEach(o -> fields.add(o.getSqlType() + " " + o.getFieldName() + ";" + (config.isNeedComment() ? ("//" + o.getComment()) : "")));

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
                jsonObject,
                config);

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
                , jsonObject,
                config);
        log.info("创建service..." + className + "Service.....完毕");
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
                jsonObject,
                config);

        log.info("创建controller..." + className + "Controller.....完毕'");

    }

}

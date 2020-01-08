package com.eqlplus.excute;

import com.eqlplus.annotation.TableField;
import com.eqlplus.annotation.TableGenerate;
import com.eqlplus.base.ToDbType;
import com.eqlplus.config.MySqlCommonQuery;
import com.eqlplus.convert.TypeToMysql;
import com.eqlplus.naming.NamingStrategy;
import lombok.extern.slf4j.Slf4j;

import javax.sql.DataSource;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * 表格生成器
 */
@Slf4j
public class TableGenerator {

    private DataSource dataSource;

    public TableGenerator(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    /**
     * 反向生成一个数据库
     */
    public void generateTable(Class<?> className) {
        NamingStrategy namingStrategy = new NamingStrategy();
        TableGenerate tableGenerate = className.getAnnotation(TableGenerate.class);
        String tableNames = namingStrategy.smallCamelToUnderline(className.getSimpleName());

        String tableIsExistsQuery = MySqlCommonQuery.tableIsExists();
        try (Connection connection = this.dataSource.getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(String.format(tableIsExistsQuery, tableNames))) {
            if (resultSet.next() && !tableGenerate.reCreate()) {
                log.info(String.format("表存在,并且设置的reCreate为%s", tableGenerate.reCreate()));
                return;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        log.info(String.format("开始生成数据表,表名为%s", namingStrategy.smallCamelToUnderline(className.getSimpleName())));

        StringBuilder keyString = new StringBuilder();
        StringBuilder createSql = new StringBuilder();
        StringBuilder dropSql = new StringBuilder();

        if (tableGenerate.reCreate()) {
            dropSql.append("drop table if exists ").append(namingStrategy.smallCamelToUnderline(className.getSimpleName())).append(";\n");
        }
        createSql.append("create table ").append(namingStrategy.smallCamelToUnderline(className.getSimpleName())).append("(\n");

        int annotationCount = 0;
        boolean hasPrimaryKey = false;

        for (Field declaredField : className.getDeclaredFields()) {
            TableField tableField = declaredField.getAnnotation(TableField.class);

            if (tableField == null) continue;

            annotationCount++;
            if (tableField.isPrimaryKey()) hasPrimaryKey = true;
            ToDbType toDbType = TypeToMysql.processTypeConvert(declaredField.getType());

            if (tableField.isPrimaryKey())
                keyString.append("`").append(namingStrategy.smallCamelToUnderline(declaredField.getName())).append("`").append(",");

            createSql.append("\t").append(namingStrategy.smallCamelToUnderline(declaredField.getName())).append(" ")
                    .append(toDbType.getType());
            if (toDbType.isSupportSize()) {
                createSql.append("(").append(tableField.size()).append(")");
            }

            createSql.append(" ");
            if (toDbType.isSupportUnsigned() && tableField.isUnsigned()) {
                createSql.append("unsigned").append(" ");
            }

            createSql.append(tableField.notNull() || tableField.isPrimaryKey() ? "NOT NULL" : "DEFAULT NULL").append(" ");
            createSql.append("COMMENT").append(" ").append("'").append(tableField.comment()).append("'")
                    .append(",")
                    .append("\n");
        }
        if (!hasPrimaryKey) throw new NullPointerException("没有主键");

        if (annotationCount == 0) throw new NullPointerException("必须有@TableField注解");

        String keys = keyString.toString();
        createSql.append("\tPRIMARY KEY(").append(keys, 0, keys.length() - 1).append(")\n").append(")");

        createSql.append("ENGINE = ").append(tableGenerate.engine().name()).append(" DEFAULT CHARSET = ").append(tableGenerate.charset()).append(" COMMENT ='").append(tableGenerate.comment()).append("'").append(";");

        try (Connection connection = this.dataSource.getConnection();
             Statement statement = connection.createStatement()) {
            if (tableGenerate.reCreate()) {
                statement.addBatch(dropSql.toString());
            }
            if (tableGenerate.writeToDocSql()) write(tableGenerate, dropSql.toString() + createSql.toString());

            statement.addBatch(createSql.toString());

            statement.executeBatch();

        } catch (SQLException | IOException e) {
            e.printStackTrace();
        }

        log.info("生成数据库结束");
    }

    protected void write(TableGenerate tableGenerate, String createSql) throws IOException {
        File file = new File("doc");
        if (!file.exists()) file.mkdir();

        log.info("开始写入文件doc.sql");
        FileWriter fileWriter = new FileWriter("doc/doc.sql", tableGenerate.writeAppend());
        fileWriter.write((createSql + "\n").toUpperCase());
        fileWriter.flush();
        fileWriter.close();
        log.info("写入文件doc.sql完成");

    }
}

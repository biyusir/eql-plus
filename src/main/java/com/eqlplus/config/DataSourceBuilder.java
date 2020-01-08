package com.eqlplus.config;

import com.alibaba.druid.pool.DruidDataSource;
import lombok.Data;

import javax.sql.DataSource;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

@Data
public class DataSourceBuilder {

    private String jdbcUrl;

    private  String userName;
    private  String password;

    private DataSourceBuilder(){}

    public static DataSourceBuilder builder(){
        return new DataSourceBuilder();
    }

    public DataSourceBuilder jdbcUrl(String jdbcUrl){
        this.jdbcUrl = jdbcUrl;
        return this;
    }

    public DataSourceBuilder userName(String userName){
        this.userName = userName;
        return this;
    }

    public DataSourceBuilder password(String password){
        this.password = password;
        return this;
    }

    public DataSource build(){
        DruidDataSource druidDataSource = new DruidDataSource();
        druidDataSource.setUrl(this.jdbcUrl);
        druidDataSource.setUsername(this.userName);
        druidDataSource.setPassword(this.password);
        return druidDataSource;
    }

    public DataSource buildByDiamond(String diamondFileName) throws IOException {
        Properties properties = new Properties();
        String userHome = System.getProperty("user.home");
        String diamondFile = userHome+"/.diamond-client/config-data/EqlConfig/"+ diamondFileName;

        FileReader fileReader = new FileReader(diamondFile);
        properties.load(fileReader);
        this.jdbcUrl = (String) properties.get("url");
        this.password = (String) properties.get("password");
        this.userName = (String) properties.get("username");

        return build();

    }
}

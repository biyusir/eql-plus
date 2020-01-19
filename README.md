## eql-plus文档
* 自动代码生成器

### maven依赖
* <font color=red>`注意，1.1-1.4为测试版本，存在bug`</font>,使用1.5版本
```xml
 <dependency>
     <groupId>com.github.biyusir</groupId>
     <artifactId>eql-plus</artifactId>
     <version>1.5</version>
</dependency>
```

### 自动生成代码（EqlPlus）
`com.eqlplus.config.DataSourceBuilder` 提供了快捷生成DataSource的方法
```java
 DataSource dataSource = DataSourceBuilder.builder()
                .jdbcUrl("jdbc:mysql://localhost:3306/blog")
                .password("330202")
                .userName("root")
                .build();

 new EqlPlus(dataSource).execute();
```
* 可以自动生成controller,service,bean,dto,dao

#### 方法

* execute();	                     //生成所有表
* execute(String className);         //指定单表
* execute(List<String> configTable); //传入一个list(存储表名)

#### 运行结果
```sh
INFO [main] - {dataSource-1} inited
INFO [main] - 开始创建TopicBean..........

INFO [main] - 创建bean...Topic.....完毕
INFO [main] - 开始创建Topicdao..........

INFO [main] - 创建dao...TopicDao.....完毕

INFO [main] - 创建service...TopicService.....完毕
INFO [main] - 开始创建TopicDto..........

INFO [main] - 创建Dto...TopicDto.....完毕

INFO [main] - 创建controller...TopicController.....完毕'
```
#### 自定义
##### GlobalConfig
```java
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
```
##### RequireConfig
```java
 // 是否创建dao层
private boolean needDao;
// 是否创建bean
private boolean needBean;
// 是否创建注释
private boolean needComment;
// 是否创建service
private boolean needService;
// 是否创建controller
private boolean needController;
// 是否创建dto
private boolean needDto;

// 如果文件存在，是否覆盖
private boolean needRewrite;
```
##### 构造函数
```java
public EqlPlus(RequireConfig config, GlobalConfig globalBeanConfig, DataSource dataSource) {
        this.config = config;
        this.globalBeanConfig = globalBeanConfig;
        this.dataSource = dataSource;
    }
```
* 实现自定义

package com.eqlplus.generate;

import com.alibaba.fastjson.JSONObject;
import com.eqlplus.config.GlobalBeanConfig;
import com.eqlplus.config.RequireConfig;
import lombok.Cleanup;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;

import java.io.File;
import java.io.FileWriter;
import java.io.StringWriter;
import java.util.List;

@Slf4j
public class JavaFileWriter extends AbstractJavaEngine {

    @SuppressWarnings("unchecked")
    public void generateJava(String className,
                             String templateName,
                             String basePackage,
                             String packageName,
                             String type,
                             JSONObject jsonObject,
                             RequireConfig config) {
        this.generateBean(className,
                templateName,
                basePackage,
                packageName,
                jsonObject.getString("annotations"),
                type,
                (List<String>) jsonObject.get("importNames"),
                (List<String>) jsonObject.get("fields"),
                (List<String>) jsonObject.get("methods"),
                config);
    }

    @SneakyThrows
    void generateBean(String className,
                      String templateName,
                      String basePackage,
                      String packageName,
                      String annotations,
                      String type,
                      List<String> importNames,
                      List<String> fields,
                      List<String> methods,
                      RequireConfig config) {

        String packagePath = packageName.replace(".", "/");
        super.checkAndGenerateDir(basePackage, packageName);

        String savePackagePath = basePackage.replace(".", "/") + "/" + packagePath + "/";

        File file = new File(savePackagePath + className + type + ".java");

        if (file.exists() && config.isNeedRewrite()) {
            log.info("%s文件已经存在，config.needRewrite = %s", className + type, config.isNeedRewrite());
            return;
        }
        VelocityEngine velocityEngine = super.init();

        Template template = velocityEngine.getTemplate(templateName);

        VelocityContext velocityContext = new VelocityContext();
        velocityContext.put("package", packageName);
        velocityContext.put("imports", importNames);
        velocityContext.put("className", className);
        velocityContext.put("annotations", annotations);
        velocityContext.put("fields", fields);
        velocityContext.put("methods", methods);

        StringWriter stringWriter = new StringWriter();
        template.merge(velocityContext, stringWriter);

        @Cleanup FileWriter fileWriter = new FileWriter(savePackagePath + className + type + ".java");
        fileWriter.write(stringWriter.toString());
    }

}

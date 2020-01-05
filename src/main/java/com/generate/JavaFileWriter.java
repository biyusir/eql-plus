package com.generate;

import com.alibaba.fastjson.JSONObject;
import lombok.Cleanup;
import lombok.SneakyThrows;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;

import java.io.FileWriter;
import java.io.StringWriter;
import java.util.List;

public class JavaFileWriter extends AbstractJavaEngine {

    @Override
    @SuppressWarnings("unchecked")
    public void generateJava(String className, String templateName, String basePackage, String packageName,String type, JSONObject jsonObject) {
        this.generateBean(className,
                templateName,
                basePackage,
                packageName,
                jsonObject.getString("annotations"),
                type,
                (List<String>) jsonObject.get("importNames"),
                (List<String>) jsonObject.get("fields"),
                (List<String>) jsonObject.get("methods"));
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
                      List<String> methods) {
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

        String packagePath = packageName.replace(".", "/");
        super.checkAndGenerateDir(basePackage, packageName);

        String savePackagePath = basePackage.replace(".", "/") + "/" + packagePath + "/";

        @Cleanup FileWriter fileWriter = new FileWriter(savePackagePath + className + type + ".java");
        fileWriter.write(stringWriter.toString());
    }
}

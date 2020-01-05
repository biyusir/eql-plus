package com.eqlplus.generate;

import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;

import java.io.File;
import java.util.List;

public abstract class AbstractJavaEngine {

    public VelocityEngine init() {
        VelocityEngine velocityEngine = new VelocityEngine();
        velocityEngine.setProperty(RuntimeConstants.RESOURCE_LOADER, "classpath");
        velocityEngine.setProperty("classpath.resource.loader.class", ClasspathResourceLoader.class.getName());
        velocityEngine.init();
        return velocityEngine;
    }

    abstract void generateBean(String className,
                               String basePackage,
                               String packageName,
                               String annotations,
                               String templateName,
                               String type,
                               List<String> fields,
                               List<String> methods,
                               List<String> importNames);

    void checkAndGenerateDir(String basePackage, String packageName) {
        StringBuilder packageAppendPath = new StringBuilder();
        System.out.println(packageAppendPath.toString());
        String[] packagePath = (basePackage + "." + packageName).split("\\.");
        for (String s : packagePath) {
            packageAppendPath.append(s);
            File file = new File(packageAppendPath.toString());
            if (!file.exists()) {
                file.mkdirs();
            }
            packageAppendPath.append("/");
        }

    }
}

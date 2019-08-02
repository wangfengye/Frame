package com.example.ann_compiler_butterknife;



import com.example.ann_butterknife.Route;
import com.google.auto.service.AutoService;

import java.io.IOException;
import java.io.Writer;
import java.util.HashSet;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.tools.JavaFileObject;

/**
 * Created by maple on 2019/8/1 14:59
 */
@AutoService(Processor.class)
public class RouteProcessor extends AbstractProcessor {
    private static final String packageName="com.maple.arouter.apt";//生成代码的包路径
    private Filer filer;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);
        filer = processingEnvironment.getFiler();
    }

    /**
     * 声明检测的注解.
     */
    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> types = new HashSet<>();
        types.add(Route.class.getCanonicalName());
        return types;
    }

    /**
     * 声明支持的Java 版本
     */
    @Override
    public SourceVersion getSupportedSourceVersion() {
        return processingEnv.getSourceVersion();
    }

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
        Set<? extends Element> fields = roundEnvironment.getElementsAnnotatedWith(Route.class);
        Writer writer = null;
        if (fields.size() <= 0) return false;


        try {

            String className = "RouteImpl" + System.currentTimeMillis();
            JavaFileObject file = filer.createSourceFile(packageName+"."+ className);
            writer = file.openWriter();
            writer.write("package "+packageName+";\n");
            writer.write("public class " + className + " implements com.maple.arouter.IRouter{\n");
            writer.write("  @Override\n");
            writer.write("  public void putActivity() {\n");
            for (Element field : fields) {
                TypeElement field1 = (TypeElement) field;
                if(field1==null)continue;
                writer.write("  com.maple.arouter.ARouter.getInstance().addRoute(\""
                        + field1.getAnnotation(Route.class).value()
                        + "\","
                        + field1.getQualifiedName()
                        + ".class);\n");
            }
            writer.write("  }\n}\n");

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return false;
    }
}

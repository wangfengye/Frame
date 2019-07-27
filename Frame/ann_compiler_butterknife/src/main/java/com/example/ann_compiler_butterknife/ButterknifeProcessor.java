package com.example.ann_compiler_butterknife;

import com.example.ann_butterknife.BindClick;
import com.example.ann_butterknife.BindView;
import com.google.auto.service.AutoService;

import java.io.IOException;
import java.io.Writer;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.tools.JavaFileObject;

@AutoService(Processor.class)
public class ButterknifeProcessor extends AbstractProcessor {
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
        types.add(BindView.class.getCanonicalName());
        return types;
    }

    /**
     * 声明支持的Java 版本
     */
    @Override
    public SourceVersion getSupportedSourceVersion() {
        return processingEnv.getSourceVersion();
    }

    /**
     * 创建文件
     *
     * @param set
     * @param roundEnvironment
     * @return
     */
    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
        //Set<? extends Element> fields =set;
        for (TypeElement e : set) {
            System.out.println(e.getSimpleName());
        }
        Set<? extends Element> fields = roundEnvironment.getElementsAnnotatedWith(BindView.class);//所有注解的变量
        Set<? extends Element> methods = roundEnvironment.getElementsAnnotatedWith(BindClick.class);//所有注解的变量
        Map<String, List<Element>> map = new HashMap<>();//按类分组
        // 变量
        for (Element element : fields) {
            String clazz = element.getEnclosingElement().getSimpleName().toString();
            List<Element> list = null;
            if (map.get(clazz) == null) {
                list = new ArrayList<>();
                map.put(clazz, list);
            } else {
                list = map.get(clazz);
            }
            list.add(element);
        }
        // 方法
        for (Element element : methods) {
            String clazz = element.getEnclosingElement().getSimpleName().toString();
            List<Element> list = null;
            if (map.get(clazz) == null) {
                list = new ArrayList<>();
                map.put(clazz, list);
            } else {
                list = map.get(clazz);
            }
            list.add(element);
        }
        if (map.size() <= 0) {
            return false;
        }
        Writer writer = null;
        Iterator<String> iterator = map.keySet().iterator();
        while (iterator.hasNext()) {
            String clazz = iterator.next();
            List<Element> elements = map.get(clazz);
            TypeElement enclosingElement = (TypeElement) elements.get(0).getEnclosingElement();
            String packageName = processingEnv.getElementUtils().getPackageOf(enclosingElement).toString();
            try {
                JavaFileObject file = filer.createSourceFile(packageName + "." + clazz + "_ViewBinding");
                writer = file.openWriter();
                writer.write("package " + packageName + ";\n");
                writer.write("import com.example.ann_butterknife.IBinder;\n");
                writer.write("public class " + clazz + "_ViewBinding implements IBinder<" + packageName + "." + clazz + ">{\n");
                writer.write("@Override\n" +
                        "public void bind(final " + packageName + "." + clazz + " target){\n");
                for (Element element : elements) {
                    //短路或,以下操作成功一个,后续不执行
                    boolean writed = writeBindView(writer, element) || writeBindClick(writer, element);

                }
                writer.write("}\n}\n");
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    if (writer != null) writer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return false;
    }

    private boolean writeBindView(Writer writer, Element element) throws IOException {
        BindView annotation = element.getAnnotation(BindView.class);
        if (annotation == null) return false;
        writer.write("target." + element.getSimpleName().toString()
                + "=(" + element.asType() + ")target.findViewById(" + annotation.value() + ");\n");
        return true;

    }

    private boolean writeBindClick(Writer writer, Element element) throws IOException {
        BindClick annotation = element.getAnnotation(BindClick.class);
        if (annotation == null) return false;
        writer.write("target.findViewById(" + annotation.value() + ").setOnClickListener(new android.view.View.OnClickListener() {\n"
                + "@Override\n"
                + "public void onClick(android.view.View v) {\n"
                + "target." + element.getSimpleName() + "(v);"
                + "}\n});\n");
        return true;
    }
}

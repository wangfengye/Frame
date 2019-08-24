package com.maple.jsonframework;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by maple on 2019/8/24 20:53
 */
public class FastJson {
    public static String toJson(Object object) {
        StringBuffer jsonBuffer = new StringBuffer();

        if (object instanceof List<?>) {
            addListToBuffer(jsonBuffer, (List<?>) object);
        } else {
            addObjectToJson(jsonBuffer, object);
        }
        return jsonBuffer.toString();
    }

    /**
     * 解析对象.
     *
     * @param jsonBuffer 字符串编辑
     * @param o          解析对象
     */
    private static void addObjectToJson(StringBuffer jsonBuffer, Object o) {
        jsonBuffer.append('{');
        List<Field> fields = new ArrayList<>();
        getAllFields(o.getClass(), fields);
        for (int i = 0; i < fields.size(); i++) {
            Field field = fields.get(i);
            String fieldName = field.getName();
            try {
                Method method = getGetMethod(o.getClass(), fieldName);
                Object value = method.invoke(o);
                jsonBuffer.append("\"").append(fieldName).append("\"").append(':');
                if (value == null) {
                    jsonBuffer.append("null");
                    continue;
                } else {
                    if (value instanceof Integer
                            || value instanceof Double
                            || value instanceof Long
                            || value instanceof Boolean) {
                        jsonBuffer.append(value.toString());
                    } else if (value instanceof String) {
                        jsonBuffer.append("\"").append(value).append("\"");
                    } else if (value instanceof List<?>) {
                        addListToBuffer(jsonBuffer, (List<?>) value);
                    } else {
                        addObjectToJson(jsonBuffer, value);
                    }
                }
                if (i < fields.size() - 1) jsonBuffer.append(',');
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        jsonBuffer.append('}');
    }

    private static void addListToBuffer(StringBuffer jsonBuffer, List<?> list) {
        jsonBuffer.append('[');
        for (int i = 0; i < list.size(); i++) {
            addObjectToJson(jsonBuffer, list.get(i));
            if (i < list.size() - 1) jsonBuffer.append(',');
        }
        jsonBuffer.append(']');
    }

    /**
     * 获取 getter, 兼容 isXX
     *
     * @param aClass    class
     * @param fieldName 变量名称
     * @return method
     */
    private static Method getGetMethod(Class<?> aClass, String fieldName) {
        Method method = null;
        try {
            method = aClass.getMethod("get" + ((char) (fieldName.charAt(0) - 32)) + fieldName.substring(1));
        } catch (NoSuchMethodException e) {
            try {
                method = aClass.getMethod("is" + ((char) (fieldName.charAt(0) - 32)) + fieldName.substring(1));
            } catch (NoSuchMethodException e1) {
                e1.printStackTrace();
            }
        }
        if (method == null) throw new RuntimeException(fieldName + "---getGetMethod 获取不到");
        return method;
    }

    /**
     * 获取所有成员变量
     * Object类型不获取,final不获取
     *
     * @param aClass 类型
     * @param fields field 存储位置
     */
    private static void getAllFields(Class<?> aClass, List<Field> fields) {
        if (fields == null) fields = new ArrayList<>();
        while (aClass.getSuperclass() != null) {//递归到Object类;
            Field[] fieldsArray = aClass.getDeclaredFields();
            for (Field field : fieldsArray) {
                if (!Modifier.isFinal(field.getModifiers())) {//不是final修饰才添加
                    fields.add(field);
                }
            }
            aClass = aClass.getSuperclass();
        }
    }
}

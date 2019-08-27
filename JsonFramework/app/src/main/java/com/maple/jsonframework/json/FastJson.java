package com.maple.jsonframework.json;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by maple on 2019/8/24 20:53
 */
public class FastJson {
    /**
     * @param json  json
     * @param clazz 类类型
     * @return 对象
     */
    public static <T> T parseObject(String json, Class<T> clazz) {
        T object = null;
        if (json.charAt(0) == '{') {//jsonObject
            try {
                object = toObject(new JSONObject(json), clazz);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            throw new RuntimeException("非法json");
        }
        return object;
    }

    private static <T> T toObject(JSONObject jsonObject, Type cla) {
        Object object = null;
        Class<?> clazz= (Class<?>) cla;
        try {
            object = clazz.newInstance();
            Iterator<?> iterator = jsonObject.keys();
            List<Field> fields = getAllFields(clazz, null);
            while (iterator.hasNext()) {
                String key = (String) iterator.next();
                Object fieldValue = null;
                int i = 0;
                for (; i < fields.size(); i++) {
                    Field field = fields.get(i);
                    if (field.getName().equalsIgnoreCase(key)) {
                        field.setAccessible(true);
                        fieldValue = getFieldValue(field, jsonObject, key, null);
                        if (fieldValue != null) {
                            field.set(object, fieldValue);
                        }
                        field.setAccessible(false);
                        break;
                    }
                }
                // if (i > 0) fields.remove(i - 1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return (T) object;
    }

    private static Object getFieldValue(Field field, JSONObject jsonObject, String key, Type valueClass) throws JSONException {
        Object tmp = jsonObject.get(key);
        Log.i("TAG", "tmp: "+tmp.getClass().getSimpleName()+" --- "+tmp.toString());
        Object fieldValue = null;
        Class<?> fieldClass;
        if (field == null) {
            fieldClass = (Class<?>) valueClass;
        } else {
            fieldClass = field.getType();
        }
        if (fieldClass.getSimpleName().equals("int")
                || fieldClass.getSimpleName().equals("Integer")) {
            fieldValue = jsonObject.getInt(key);
        } else if (fieldClass.getSimpleName().equals("String")) {
            fieldValue = jsonObject.getString(key);
        } else if (fieldClass.getSimpleName().equals("long")
                || fieldClass.getSimpleName().equals("Long")) {
            fieldValue = jsonObject.getLong(key);
        } else if (fieldClass.getSimpleName().equals("double")
                || fieldClass.getSimpleName().equals("Double")) {
            fieldValue = jsonObject.getDouble(key);
        } else if (fieldClass.getSimpleName().equals("boolean")
                || fieldClass.getSimpleName().equals("Boolean")) {
            fieldValue = jsonObject.getBoolean(key);
        } else if (List.class.isAssignableFrom(fieldClass)) {
            if (valueClass == null) valueClass = field.getGenericType();
            fieldValue = toList(jsonObject.getJSONArray(key), valueClass);
        } else if (Map.class.isAssignableFrom(fieldClass)) {
            if (valueClass == null) valueClass = field.getGenericType();
            fieldValue = toMap(jsonObject.getJSONObject(key), valueClass);
        } else {
            fieldValue = toObject(jsonObject.getJSONObject(key), fieldClass);
        }
        return fieldValue;
    }

    private static Object toMap(JSONObject jsonObject, Type type) throws JSONException {

        Map<Object, Object> map = new HashMap<>();
        Iterator<?> iterator = jsonObject.keys();
        if (!(type instanceof ParameterizedType)) throw new RuntimeException("泛型异常");
        Type[] itemClasses = ((ParameterizedType) type).getActualTypeArguments();
        Class<?> keyClass = (Class<?>) itemClasses[0];
        Class<?> valueClass = (Class<?>) itemClasses[1];
        while (iterator.hasNext()) {
            String key = (String) iterator.next();
            Object o = getFieldValue(null, jsonObject, key, valueClass);
            map.put(key, o);
        }
        return map;
    }

    private static Object toList(JSONArray jsonArray, Type type) throws JSONException {
        List<Object> list = new ArrayList<>();

        if (!(type instanceof ParameterizedType)) {
            throw new RuntimeException("泛型异常");
        }
        //List<?> 当前类所实现的泛型类.
        Type clazz = ((ParameterizedType) type).getActualTypeArguments()[0];
        for (int i = 0; i < jsonArray.length(); i++) {
            Object o = getFieldValue(jsonArray, i, clazz);
            list.add(o);
        }
        return list;
    }

    private static Object getFieldValue(JSONArray jsonArray, int key, Type valueClass) throws JSONException {
        Object fieldValue = null;  Class<?> fieldClass;
        try {
            fieldClass = (Class<?>) valueClass;
        }catch (Exception e){
            return null;
        }

        if (fieldClass.getSimpleName().equals("int")
                || fieldClass.getSimpleName().equals("Integer")) {
            fieldValue = jsonArray.getInt(key);
        } else if (fieldClass.getSimpleName().equals("String")) {
            fieldValue = jsonArray.getString(key);
        } else if (fieldClass.getSimpleName().equals("long")
                || fieldClass.getSimpleName().equals("Long")) {
            fieldValue = jsonArray.getLong(key);
        } else if (fieldClass.getSimpleName().equals("double")
                || fieldClass.getSimpleName().equals("Double")) {
            fieldValue = jsonArray.getDouble(key);
        } else if (fieldClass.getSimpleName().equals("boolean")
                || fieldClass.getSimpleName().equals("Boolean")) {
            fieldValue = jsonArray.getBoolean(key);
        } else if (List.class.isAssignableFrom(fieldClass)) {
            fieldValue = toList(jsonArray.getJSONArray(key), valueClass);
        } else if (Map.class.isAssignableFrom(fieldClass)) {
            fieldValue = toMap(jsonArray.getJSONObject(key), valueClass);
        } else {
            fieldValue = toObject(jsonArray.getJSONObject(key), valueClass);
        }
        return fieldValue;

    }


    /*******************************************************************************************************/

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
                if (value == null) {//空字段跳过
                    continue;
                }
                addData(jsonBuffer, fieldName, value);

                if (i < fields.size() - 1) jsonBuffer.append(',');
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        jsonBuffer.append('}');
    }

    /**
     * addMap数据.
     *
     * @param jsonBuffer 缓存字符串
     * @param fieldName  键 键为空时插入的是集合
     * @param value      值
     */
    private static void addData(StringBuffer jsonBuffer, String fieldName, Object value) {
        if (fieldName!=null){
        jsonBuffer.append("\"").append(fieldName).append("\"").append(':');}

        if (value instanceof Integer
                || value instanceof Double
                || value instanceof Long
                || value instanceof Boolean) {
            jsonBuffer.append(value.toString());
        } else if (value instanceof String) {
            jsonBuffer.append("\"").append(value).append("\"");
        } else if (value instanceof List<?>) {
            addListToBuffer(jsonBuffer, (List<?>) value);
        } else if (value instanceof Map<?, ?>) {
            addMapToBuffer(jsonBuffer, (Map<?, ?>) value);
        } else {
            addObjectToJson(jsonBuffer, value);
        }
    }

    private static void addMapToBuffer(StringBuffer jsonBuffer, Map<?, ?> value) {
        jsonBuffer.append('{');

        for (Map.Entry<?, ?> entry : value.entrySet()) {
            addData(jsonBuffer, entry.getKey().toString(), entry.getValue());
            jsonBuffer.append(',');
        }
        if (value.size() > 0) jsonBuffer.deleteCharAt(jsonBuffer.length() - 1);//移除末尾0;
        jsonBuffer.append('}');
    }

    private static void addListToBuffer(StringBuffer jsonBuffer, List<?> list) {
        jsonBuffer.append('[');
        for (int i = 0; i < list.size(); i++) {
            addData(jsonBuffer, null,list.get(i));
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
    private static List<Field> getAllFields(Class<?> aClass, List<Field> fields) {
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
        return fields;
    }
}

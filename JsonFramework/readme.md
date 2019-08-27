### json解析框架

* 泛型解析,
> 通过 `((ParameterizedType) type).getActualTypeArguments()`

* 最外层的泛型,由于xx.class,不支持写死泛型.所以执行时无法获取最外层泛型,通过匿名内部类传入参数
> 调用 `  Type tp = b.getClass().getGenericSuperclass(); Type type=  ((ParameterizedType) tp).getActualTypeArguments()[0];`获取泛型类型

* 数据类型必须具有强一致性.例如map值必须指定类类型,不能使用Object(FastJson解决了这个问题,自己实现JsonObject的解析.)
> 解析框架中通过class基本数据,集合,对象,若使用Object会导致,解析异常.

* 新的思路, 元素JSONObject.get()方法可以获取数据的真实类型.JsonObject构造时,会自动解析判断对应的数据类型,组成linkedHashMap;
我们可以通过这个获取数据的类型.

* 嵌套泛型 `ParameterizedTypeImpl`是fastJson继承并重写`ParameterizedType`用于处理嵌套泛型的类,
* 我们自己实现的还不支持嵌套泛型
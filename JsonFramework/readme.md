## json解析框架

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

### FlatBuffer高效序列化协议
> FlatBuffers与其他库不同之处就在于它使用二进制缓冲文件来表示层次数据，这样它们就可以被直接访问而不需解析与拆包;
* Json解析中会产生大量临时对象,造成内存抖动,在一些大些的高响应要求项目中无法满足需求.
* FlatBuffer 直接转化二进制处理了这个问题.内存消耗极小,速度极快.
* 缺点: 有代码侵入,需要生成代码;生成数据无可读性,难以debug;


#### FastJson bug

* version< 1.2.60,后台版的FastJson库,在解析以\\x结尾的字符串,未进行检测直接向后获取两位,获取到结束符.开始无限循环,内存爆炸
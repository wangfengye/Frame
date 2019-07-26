* java方法执行时间3~10ns.

### 阿里sopHix原理
> 基本原理,native层操作,动态替换方法区指针,指向新加载方法,支持实时热更新,更新包极小,兼容性较Tinker方案略差,
不支持9.0
* class加载时机//
	* 构造函数
	* 反射
	* native反射
	* 反序列化
	

* art 加载类
	1. FindClass
	2. DefineClass
	3. LoadClass
	4. 从dex文件填充信息

* 打Dex包
	1. 进入build-tools/任意版本/
	2. dx --dex --output [输出路径(包含生成的文件名:a.dex)] [class文件路径(需要包含包路径即: 类路径E:\tmp\com\maple\s\a.class,填写E:\tmp\)]
	
	
#### 异常

> library "/storage/emulated/0/oat/arm/sophix.odex" ("/storage/emulated/0/oat/arm/sophix.odex") needed or dlopened by "/system/lib/libart.so" is not accessible for the namespace: [name="(default)", ld_library_paths="", default_library_paths="/system/lib", permitted_paths="/system/lib/drm:/system/lib/extractors:/system/lib/hw:/system/product/lib:/system/framework:/system/app:/system/priv-app:/vendor/framework:/vendor/app:/vendor/priv-app:/odm/framework:/odm/app:/odm/priv-app:/oem/app:/system/product/framework:/system/product/app:/system/product/priv-app:/data:/mnt/expand"]

* 出现在android9.0上 DexFile废弃加载外部dex文件

> 数组下标越界,修复后,调用修复接口出现

* `dex_cache_resolved_methods_`dex_cache_resolved_types_`这两个变量超出int32限制
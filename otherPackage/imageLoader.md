## 图片加载[uml](other.odj)
#### todo
* Bitmap 读取流时mark reset问题

#### ImageLoader
* 高并发,图片加载优先级.
* 分级缓存. 内存缓存,外置卡缓存,网络下载.
* 缓存策略扩展
* 异常,及默认展位图
* 图片压缩自适应

* 任务存储队列(ProprietyBlokedQueue)
	* 阻塞队列,线程安全,支持优先级.
	* 加载请求需要增加id字段用来进行优先级比较
	* 请求创建存在并发,需要使用原子类的id.
* 多线程并发框架设计
	* 一般采用单例+阻塞队列+线程池的方式实现并发,
	* 本次框架,数组保存线程,轮询执行:固定了工作线程数,持续运行.
* http协议下的加载图片
	* http网络请求获取inputStream 通过Bitmap静态函数转成bitmap.
	* bitmap缩放,两次读流
		1. 设置`inJustDecodeBounds=true`只获取宽高.
		2. 获取view的宽高(通过LayoutParams取,防止view未加载时,无法直接获取宽高),计算缩放比例
		3. 设置option缩放比例,读取图片.(PS:第二次读流需要重置流)
			* 重置流操作 `mark`标记,`reset`返回标记位置 ,标记位置需要传入流长度`avaiable()`而inputStream没有实现`avaiable()`,因而采取先下载,在使用文件流转Bitmap.
* 缓存策略
	* 缓存淘汰算法 LruCache
		* 新数据,头插
		* 查缓存数据, 移动至头部
		* 缓存超过阈值,删除尾部部分数据.
		* android实现使用了LinkedHashMap,并开始`accessOrder`功能,每次get()触发排序.将访问元素移动到最后.
	* 双缓存机制,同时使用内存缓存,外存缓存.存两份数据
		* 内存缓存采用LruCache算法,建议缓存最大值为空闲内存的1/8;
		* 外存专用`DiskLruCache`,路径用md5摘要,避免特殊字符.
			* DiskLruCache.class 在okhttp,glide中都有使用.
* 图片展示
	* 请求开始给图片加tag,请求完成判断tag是否改变,未改变展示图片,防止,请求期间设置其他图片后,重复显示图片(常见状况:RecyclerView复用ImageView导致的图片错乱,加tag后正常 )
	
#### Glide分析[UML](other.mdj)

* 生命周期感知,通过添加关联的空Fragment来接收生命周期函数回调.
* `ActivityFragmentLifecycle`,空fragment持有该对象,并传递生命周期.
* 外置缓存路径,缓存目录/image_manager_disk_cache,默认最大缓存250m
* 多级缓存, 弱应用缓存(正在使用的图片,防止lru算法丢弃图片),Lru内存缓存,Disk缓存. 图片错位处理:view和对应请求封装为一个类,view上绑定请求存在(表明view有新的图片请求)则取消请求,回收资源

* HttpUrlFetcher 处理网络图片,设置重试5次
* 存储的键值对key值是通过`EngineKeyFactory`专门创建.
* Glide存在多个线程池:newDiskCacheExecutor(执行本地缓存任务),newAnimationExecutor(Gif处理),newSourceExecutor (网络请求:最多持有线程数都是依据 CPU 核数来决定的，至少是4个)newUnlimitedSourceExecutor (线程上限为Integer.Max,类似OkHttp的线程池)

* Bitmap `ResourceDecoder`解析数据时,如果需要的Bitmap大小和回收的bitmap相同,则进行复用.

* 解析时预设了最大解析大小5M. 重写的流, 执行mark后,Bitmap获取头大小时,会缓存解析的数据,空间不够时自动扩容,最大5m,第二次真正解析流时执行reset回到头部.
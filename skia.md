## 图片压缩[参考](https://www.cmonbaby.com/posts/images_compress.html)
* Android原生压缩`Bitmap.compass()`进行质量/尺寸压缩
* 鲁班压缩(Java版本)
* native层开启jpeg的Huffman压缩.`jpeg_sturct.arith_code = false;jpeg_sturct.optimize_coding = true;`
> 对于JPEG图片的压缩.了Android默认使用的是libjpeg库进行压缩的，不过在Android7.0+发生了一点点变化，主要是做了两点优化
 1. 内部使用的JPEG压缩库改为libjpeg-turbo，这是一个基于libjpeg的涡轮增压库，主要的一特点就是速度比libjpeg快;
 2. 使用Huffman编码替代Arithmetic编码;
 因此,7.0 后无需进行native层压缩.(Ps;部分厂商修改源码 导致或优先开启该功能)


* 设置图片格式 png>jpeg>webp
* 质量压缩  bitmap.compress(format,quality,baos)对png无效
* 采样率压缩 BitmapFactory.Options.inSampleSize;
* 缩放压缩.  canvas.drawBitmap(bitmap,null,rectF,null)
* JNI开启JPEG的哈夫曼压缩.


#### 遇到的问题

* ADB 安装 INSTALL_FAILED_TEST_ONLY 问题
	* AndroidManifest.xml文件中添加了属性testOnly=true.android studio 快捷编译时默认添加
		* adb intall -t apk,强制安装
		* 修改该参数
	* 两个apk使用同一个签名, 不同apk可以使用同一个jks,但其中alias不能相同.

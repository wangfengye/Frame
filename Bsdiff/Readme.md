#### 增量更新
>[Bsdiff下载地址](http://www.daemonology.net/bsdiff/)

##### 后端生成差分包(liunx服务器)
> res下资源包,两个版本的apk(需要带签名的)

1. 将资源包上传到服务器
2. 使用`tar xvf [压缩包路径]`解压文件
3. 进入 bzip2 目录下 使用`sudo make install` 载入程式
4. 进入 bsdiff 目录下 使用 make 编译
    * MakeFile文件报错
    * 文件13,15行未缩进
    * bzip2依赖等问题
    * 若无法使用make编译,也可以通过 `gcc bsdiff.c -lbz2 -o bsdiff`,`gcc bspatch.c -lbz2 -o bspatch` 手动编译
5. 生成差分包 `./sdiff [旧包] [新包] [差分包]`

##### android 端使用(NDK)
1. 编写JNI 代码 调用 `baspatch.c`的main函数合并包,(注意参数顺序)
2. 完成后安装新包,设计的android7.0后的新权限,配置FileProvider

#### 遇到问题
* As升级3.4后编译失败,
> 处理 ,升级gradle(gradle-wrapper.properties,project的build.gradle)

* 用了不安全的函数:添加宏定义
* 用了过时的函数:添加宏定义 __CRT_NONSTDC_NO_DEPRECATE
* sdl报错,关闭VS严格的代码检查
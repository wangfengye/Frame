# [FFmpeg](https://blog.csdn.net/leixiaohua1020/article/details/44220151)
> Fast Forward Mpeg音视频处理框架,[官网](http://ffmpeg.org/),[编译好的版本](https://ffmpeg.zeranoe.com/builds/)

## 音视频基础.
* 常见封装格式FLV,AVI,MP4
* 声音默认采用率:44100
* H.264视频编码.

## win使用
* 下载dev版的build包,使用其中include目录头文件,lib目录依赖包
* vsstudio 引入头文件, lib库
* 部分头文件缺失 添加avconfig.h到libavutil目录

## linux使用
* NDK安装
	* 下载linux下ndk文件[地址](https://developer.android.com/ndk/downloads/)
	* 上传linux服务器
	* 授予 权限 `chmod 777 -r 目录`
	* 解压ndk文件
		* 处理windows上传的文件乱码问题 `unzip -O cp936 压缩包名`;
	* 环境变量
		* 修改全局环境变量`~/.bashRc`
		```
		export NDKROOT=[安装目录]
		export PATH=$NDKROOT:$PATH

		```
		* 更新环境变量 `source ~/.bashrc`.
		* 修改错误导致所有命令失效,解决方法 `export PATH=/usr/local/sbin:/usr/local/bin:/sbin:/bin:/usr/sbin:/usr/bin:/root/bin`,恢复命令
* ffmpeg编译.
	1. 下载官方包,上传linux服务器
	2. `tar -jxvf 包名` 解压
	3. 修改 configure中build配置,目的是修改生成的so名称
	4. 编写编译文件[参考](https://juejin.im/post/5d831333f265da03c61e8a28#heading-7)[编译文件](./resources/build_android.sh)
	5. 执行编译(windows上传文件需要dos2unix进行格式转换)
* android使用
    1. 开始使用api=29编译,运行提示 fread_unlock()找不到,需要minVersion>28才能跑.
    2. 视频解码,
    3. 视频播放,使用ffmpeg的scale库做yuv->rgb的转换
        * yuv->rgb库linux编译(暂时没成功呢)
            1. 下载源代码 ` git clone https://github.com/lemenkov/libyuv`
            2. 安装对应版本cmake: 下载包,解压,配置环境变量
            3. yuv目录下新建build,目录下执行 `cmake ..`, `make`.
    4.音频解码播放
* JNI 异常处理
    1. 查看异常,部分错误日志不在本项目下需要切换到`No Filters`中查找,找到backtrace中首个异常地址,即出现异常最开始的地方.
    异常地址可使用步骤3反查.
    2. 使用ndk-stack(ndk包中)分析地址(可以跳过这步)
        ```
        adb logcat | ndk-stack -sym [so目录]
        ```
    3. 使用addr2line(ndk包中)e分析异常出现的位置
        ```
        arm-linux-androideabi-addr2line -e [so文件] [异常地址]
        ```
## OpenSL ES（Open Sound Library embedded system）
* 引入系统OpenSLES库,需在Cmake中添加
* 引入 transcode转码工具,这次选择的是源码引入,将自己需要的源文件复制到项目中
* 使用transcode 进行转码,
* opensl播放[流程](./openSl_ES.png)
    1. 打开文件
    2. 创建OpenSL ES引擎对象并实例化.
    3. 获取引擎接口
    4. 创建混音器.实例化混音器.
    5. 创建缓存区保存音频数据.
    6. 创建带缓冲队列的音频播放器,并实例化.
    7. 获取缓冲区队列接口,进行顺序播放
    8. 注册音频播放器回调函数,回调中读取缓存区大小的数据插入缓冲队列
    9. 获取Play Interface,通过SetPlayState函数来启动播放音乐.
    10. 主动调用一次回调函数,插入一块数据,开始播放,
    11. 回调中判断无数据可读即停止播放,关闭文件.
    12. 其中开关文件,读取文件使用wavlib库函数,
## 常见问题
* jni获取自定义的java类,需要在主线程执行.子线程需要反射时,一般先在主线程获取,设为全局引用,供子线程使用.
* 视频花屏
    1. Surface不支持(极小概率)
    2. 解码出的视频格式非yuv.
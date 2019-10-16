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

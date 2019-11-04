# 音视频采集
## 直播流程
1. 采集(视频,音频)
2. 编码(H264-X264,AAC-FAAC)
3. 推流(rtmp-dumprtmp)

### x264 编译
> 官方提供的编译时基于gcc的,新版AndroidNDK,移除了gcc编译器,使用clang编译.[编写clang编译脚本]
(./resource/build_android.sh)

### 直播服务端搭建
* nginx rtmp-moudle[教程](https://www.jianshu.com/p/4ed63b041bd9)
* red5

### 客户端编写
>  [参考协议文档](resource/H264直播总结.docx)
* 视频下x264编码
* 音频 faac编码
* 推送 rtmp 协议


### todo
* 三个库的编译工作,之前是使用gcc,新版ndk去除了gcc,只保留clang,而库一般只提供了gcc的编译脚本.尝试使用
clang
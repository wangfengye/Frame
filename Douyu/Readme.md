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



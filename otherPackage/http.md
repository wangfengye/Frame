## 网络框架

####下载
* Range: bytes=0-1199 //配置下载起止位置,用于断点续传,多线程下载.
* 断点续传中,后续请求返回码规范值为206;

* 对手动暂停,自动暂停设置不同的恢复优先级
* 下载缓存:1. 内存缓存:下载框架中的集合;2. sqlite中的下载记录


* 大型项目数据库,主数据库+不同用户独立的数据库.

#### 案例,下载硬件升级包,支持断点续传(无线雷达)
* 下载信息持久化:sqlite数据库.
* 文件完整性校验,md5,通过response头信息发送
* 断点实现基于range设置下载起止点
* 重写retrofit下载时的流读取,暴露一个
接口用于显示下载进度.
* 文件目录检查创建,文件名重复后缀时间戳
* 数据库字段:id,下载起止时间,下载进度,请求url,文件下载地址,文件大小,下载进度.
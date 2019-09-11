#### 性能优化(app moudle下有部分测试)

* 加固
* 过度渲染


##### SVG[官方文档](https://developer.android.google.cn/studio/write/vector-asset-studio) 
* xml定义,根节点为<svg>
*  support-v7兼容(5.0+)` vectorDrawables.useSupportLibrary=true`


##### Tint着色器

* `android:tint="@color/colorAccent"`
* 结合 drawable中的select控制点击变色

##### 指定资源,(主要用于屏蔽不需要的语言)
> `resConfigs('zh-rCN')`

##### so处理,减少打包的架构类型
* 现在手机架构,基本以arm系列为主,同系列基本兼容,因此通常只打包一个armeabi. 模拟器(x86为主)
```
android {
	...
    defaultConfig {
		...
        ndk{
          abiFilter("armeabi")//只保留最主流的armeabi
        }
    }
}
```

##### 移除无用资源
* `refactor -> remove unused resources` 物理删除,谨慎使用!!!
* `analyze -> run inspection by name -> 输入unused resources` 查找未引用资源

##### 混淆

##### [资源压缩](https://developer.android.google.cn/studio/build/shrink-code)
* `shrinkResources true` gradle 开启资源压缩(删除未引用资源)
* keep文件,指定未使用的资源保留

##### 图片资源 webp
> 有损压缩格式, 比png,jpeg损失的质量少很多.压缩比例更大,已集成至andoridstudio

* androidstudio 图片右击选择 convert to webP
* 非必要资源,动态下载.

##### [压缩对齐,res资源混淆,7zip压缩](https://blog.csdn.net/xiangzhihong8/article/details/54989020)
* 使用7zip深度压缩
* zipalign.exe(位置:sdk\build-tools\27.0.3)
	* 对齐操作可以让硬件读取时更快.
* 资源混淆
	* res,raw,这些目录用于aapt编译资源文件时,进行区分编译,编译,这部分目录也可以修改.
	* 类似代码混淆,缩短资源名长度以减少存储空间.核心是修改`resources.arsc`资源映射文件.需要了解资源映射文件的编码方式.资源字段包含三个 id,name,地址/值,
	编译后,代码通过id找寻资源,我们修改后面两者的值,改为修改后的资源名,由于代码使用id来找资源,找到的就是我们修改后的资源名.
* [开源的压缩工具AmdResGuard](https://github.com/shwenzhang/AndResGuard)
	
	


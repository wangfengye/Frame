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

##### 压缩对齐,res资源混淆,//TODO 


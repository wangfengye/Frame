## 身份证识别
> 使用opencv分析截取图片需要的部分,tessart识别图片中的文字

##### 使用到的库
* [opencv](https://opencv.org/)图片识别
* [tesseract-ocr](https://github.com/tesseract-ocr/tesseract)文字识别
* [tess-two](https://github.com/rmtheis/tess-two)android端封装
* [jTessBoxEditor](jTessBoxEditor-2.2.0.zip)训练工具.

#### 学习中的问题

* CV_Assert 找不到实现: makeFiles中opencv.so文件路径错误
* 编译失败,清缓存重来,(有时多次失败,需要彻底重启恢复) android stuido 写ndk不友好
* assets文件打包在apk上,无法通过路径访问.复制到缓存目录使用.
* tess-two init训练数据时,'ocr/tessdata/eng.traineddata'对应的输入是`ocr`

##### 训练模型
* 解压 jTessBoxEditor
* 打开jTessBoxEditor.jar.
* 选择 TIFF/Box Generator
* 选择输入目录() language(随便设置,库会按这个给文件命名,),下方随意输入内容, 点击Generate生成模板
* 到输出目录下产生三个文件, 将训练样例按 .tif样式命名,之后删除生成的.tif,.box文件
* 切回选项卡Trainer
* Training data 设置为你放训练样本的目录,设置language(与刚才生成模板时要新相同),选择make box only 模式 点run,开始训练
* 完成后切到选项卡TrainerBox Editor 对训练结果进行调整优化
* open 训练样本,会出现训练的结果,对字符的截取,和对应的char值做调整
* 保存
* 返回选项卡Tariner 选择train with with existing box, 生成最终的训练结果
* 生成结果在  Training data 目录/tessdata下

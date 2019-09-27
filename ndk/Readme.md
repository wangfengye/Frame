## 文件拆分,合并
### 问题记录
*  一个奇葩问题.
   diff函数重复释放同一个jstring.<br/>
   在调用diff后调用combine 函数导致 tmpFiles内容丢失. 运行结果 ,tmpFiles_的值和 out_值相同,都是java层
   传入的out
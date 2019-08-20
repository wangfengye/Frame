* 初步实现插入功能
@Entity注解表名称, @DbField 注解变量名,
BaseDao,初始化(打开数据库,建表,拿到数据库引用,根据注解获取字段名表名实现,增删改查操作.)
DaoFactory,获取各个类的操作对象,(缓存对象,防止重复创建)
* 常见框架ormlite,greendao.
* greendao缓存机制问题
	* greendao对查询结果进行了缓存,在一个页面对结果集修改但不更新表,也会对其他页面查询的同样数据造成污染.典型案例(数据有个select字段,查询后对该字段进行修改,但不保存至数据库,关闭页面,在打开页面,执行加载数据,select状态值有修改的状态)
	
#### 数据库升级

sql语句操作:`alert table 'tableName' add column 'new Column' Integer;` sqlite 只支持在表尾新增字段.

##### 基本流程
0. 操作前备份db文件,防止升级失败.
1. 对要修改的表重命名,通常加个bak_前缀.
2. 新建修改后的表.
3. 原表数据插入新表.	

##### 进阶多版本跨版本升级
> 利用脚本升级,java代码解析xml脚本,读取对应的升级sql语句,进行升级操作

* 本质仍是上述的升级流程,使用xml脚本将升级逻辑和java代码隔离.通过脚本管理跨版本的升级语句隔离.
```
<updateXml>//根节点
	<updateStep versionFrom="1",versionTo="2>//对应一个版本
		<updateDb name="usr">//指定数据库
			<sqlBefore></sqlBefore> //备份表
			<sqlAfter></sqlAfter>//迁移数据
			<sqlAfter></sqlAfter>//移除表
		</updateDb>
	</updateStep>
</updateXml>
```
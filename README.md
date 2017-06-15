# Mybatis-Generator-Tools
可视化界面生mybatis的mapper接口、mapper.xml、po类、example类

### MBG小公举首页

![小公举首页](https://github.com/a1023293003/MarkdownPhotos/blob/master/MBG%E5%B0%8F%E5%85%AC%E4%B8%BE%E9%A6%96%E9%A1%B5.png?raw=true)

### MBG小公举连接数据库页面

![小公举连接数据库](https://github.com/a1023293003/MarkdownPhotos/blob/master/MBG%E5%B0%8F%E5%85%AC%E4%B8%BE%E8%BF%9E%E6%8E%A5%E6%95%B0%E6%8D%AE%E5%BA%93.png?raw=true)

### 实现原理

  连接指定数据库，读取数据库表名，字段名，字段类型等基础信息，根据配置文件中内置的代码模板在指定路径下生成mapper接口类，po类，mapper.xml配置文件以及example类。
  
### 数据库
  
  目前数据库只支持**MySQL**。

### 标注

  小公举是在本人学习mybatis逆向工程的时候兴趣使然做的，界面是用JavaFx写的，因为国内相关资料很少，所以做的时候没少踩坑，好在问题大都能在[stack overflow](https://stackoverflow.com)找到解决方法或提示。

### 参考资料
  小公举的界面借鉴了githud上的一个类似的项目，我会跟你说我是因为看了这个项目才想自己写一个的吗。[https://github.com/astarring/mybatis-generator-gui](https://github.com/astarring/mybatis-generator-gui)

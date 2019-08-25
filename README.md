# Penguin Statistics

<img src="https://penguin-stats.s3-ap-southeast-1.amazonaws.com/penguin_stats_logo.png" width="150" height="150">

## 企鹅物流数据统计
- [https://penguin-stats.io/](https://penguin-stats.io/)
- 明日方舟素材掉落统计与分析
- 根据人工汇报的掉落样本，生成各作战和各素材的统计结果，方便博士们对刷素材的效率进行参考
- 更多功能敬请期待

## 如何食用本repo
此repo包含网站后端，项目基于[Spring Boot 2.1.6](https://spring.io/projects/spring-boot)，数据库使用MongoDB。

### 准备工作
1. 安装maven
2. 安装IDE对应的[lombok](https://projectlombok.org/)插件
3. 安装MongoDB（可选）
4. 如果选择本地运行MongoDB，可以联系作者获取dump得到的BSON文件，并执行以下命令添加测试用数据（可选）
```
mongorestore -h localhost:27017 -d penguin_stats <path of penguin_stats directory>
```

5. 打开`src/main/resources/application.properties`，将`spring.data.mongodb.uri`中的`username`与`password`替换为相应的用户信息。若有自定义需求，也可改变本文件中的其他值

### 启动
1. 在项目根目录下执行`mvn spring-boot:run`，或运行PenguinStatisticsApplication类中的main方法
2. 提示PenguinStats is running后，即完成启动

### Build
 执行`mvn clean package`后，将`target/PenguinStats.war`部署至服务器上即可。

 ## API文档
 1. 已上线版本的API文档请见[这里](https://penguin-stats.io/PenguinStats/swagger/swagger-ui.html)
 2. 本地调试版本可以访问`/PenguinStats/swagger/swagger-ui.html`

### 使用maven构建docker镜像

如果准备使用私有registry,请修改`pom.xml`文件中的`docker.image.prefix`值.

构建命令:

```
mvn jib:dockerBuild
```

构建默认使用的镜像为`tomcat:8.5-jre8-alpine`，默认端口`8080`，docker运行容器命令参考如下：

```
docker run -d -p 8081:8080 penguin-stats/backend:<tag>
```

1. 上述命令中，`<tag>`为构建时的版本号，如`1.3.3`

2. `-p 8081:8080`可以指定暴露内部端口`8080`到外部`8081`，容器外部可以通过`8081`端口访问，适合本地调试时使用，不同环境请根据实际需求修改


## 意见和建议
各种想法欢迎提Issue，也可以通过[网站介绍页面](https://penguin-stats.io/ "网站介绍页面")下方的联系方式找到我和Penguin Stats的其他团队成员。
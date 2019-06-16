# Penguin Statistics

<img src="https://penguin-stats.io/assets/logo.png" width="150" height="150">

## 企鹅物流数据统计
- [https://penguin-stats.io/](https://penguin-stats.io/)
- 明日方舟素材掉落统计与分析
- 根据人工汇报的掉落样本，生成各作战和各素材的统计结果，方便博士们对刷素材的效率进行参考
- 更多功能敬请期待

## 如何食用本repo
此repo包含网站后端，项目由[Maven](https://maven.apache.org/)生成。

### 准备工作
1. 安装Docker CE
2. 确保docker在后台运行

### 运行在开发环境上
1. 执行`docker-compose run mvn_build`编译项目
2. 执行`docker-compose up -d penguin_server`
3. Container创建完成后，通过[http://localhost:8080/](http://localhost:8080/)访问部署好的后端

### 数据库前端工具
执行`docker-compose up -d mongo_express`可以运行[mongo-express](https://github.com/mongo-express/mongo-express-docker)工具。可通过[http://localhost:8081/](http://localhost:8081/)访问并对本地数据库进行修改。

### 部署
编译后，将`target/`目录下的`PenguinStats-0.0.1-SNAPSHOT.war`及同名文件夹部署至服务器上即可。

## 意见和建议
各种想法欢迎提Issue，也可以通过[网站介绍页面](https://penguin-stats.io/ "网站介绍页面")下方的联系方式找到我和Penguin Stats的其他团队成员。

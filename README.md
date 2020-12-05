# Unify-Log
ULog for short. 

Lightweight And Convenient tool for Collecting Log from Microservices.

Support Multiple Transmission Channels  e.g. Http, Kafka, Redis Queue.

Support Multiple DataStore,  e.g. Mongodb, Elasticsearch, Hbase, And will be more in futrue.

### 轻量且方便使用的日志收集工具.
**主要用于解决微服务(多应用服务器)查看日志看的不便**

通常服务器出现异常或业务出现问题时，首先要查看系统的运行日志，来诊断问题，由于多应用之间的相互调用，以及应用的集群部署， 查看日志多有不便，特别是在生产服务器有严格访问权限时，想查看所有日志非常不便. （这也是作者在工作中所遇到问题，所以才决心开发一款工具解决这一问题）。

**ULog工具可将所有日志收集在一起，通过UI页面即可查看所有服务器的日志信息。**

### 是否重复造轮子！！！ 
**有了ELK，为什么还会还要重复开发？**

对于日志查看的不便性，ELK确实可以解决， 但ELK是通过logstash读取日志文件进行日志收集的，每个服务器都需要部署一个logstash，且在读取IO文件时，可能会 产生日志的丢失，其次日志也非实时实时收集。

### 特性 
- 1，0侵入：ULog 对应用系统0侵入，只需对日志工具logback log4j 或log4j2配置一下即可（*将来有计划支持其它语言，如：c#, php, Ruby 等，欢迎感兴趣的朋友一起完善*）； 
- 2，异步：通过异步即时将日志发送到日志汇总服务器，对应用的影响微乎其微； 
- 3，操作日志收集：ULog不仅收集应用的运行日志，也可以通过一句代码将系统的操作日志收集起来，让你更专注于你的业务。


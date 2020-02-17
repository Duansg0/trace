# trace(摘要日志)

#### 前言
    	我们都知道Spring Cloud Sleuth可以解决分布式日志链路的问题，但是它没有携带更直观的表现方式(比如响应耗时,响应结果集,调用地址,方法名称,压测信息等等),我们在排查系统问题用得最多的手段就是查看服务日志，在分布式环境中一般使用ELK来统一收集日志，但是在某种问题下(比如并发大,服务错综复杂)时使用日志定位问题还是比较麻烦，由于大量的其他用户、其他线程、消息、RPC等等的日志也一起输出穿插其中导致很难筛选出指定请求的全部相关日志,以及下游线程、服务等对应的日志，也无法串联起来，最常见的处理方式就是上游提供一个订单号或者唯一标识,下游系统通过这个标识在寻找到指定业务的操作日志,这样不仅繁琐,而且在没有唯一标识的情况下,想要定位某一种业务的操作日志是及其浪费效率的。虽然现在也有很多现成的方案在来解决此类问题，但是大概了解一下,很多就是要改动网关等其他业务代码、增加处理服务等等。

#### 它能解决什么问题?

```java
1:ELK收集
2:统计数据(PV等)
3:耗时优化数据分析(DAO执行耗时,请求耗时,RPC等)
4:问题排查(结合TraceId、耗时、结果标识等,有效排查问题)
5:链路跟踪(TraceId,从网关下发请求到RPC、CRUD操作,共享TraceId,链路唯一)
```

#### 配置

```java
spring:
	boot:
		trace:
			##项目名称
      appName: duansg-test-demo
      ##总开关(默认开启)
      traceSwitch: true
      ##Dao开关(默认关闭)
      traceSwitchDao: true
      ##Pv开关(默认关闭)
      traceSwitchPv: true
      ##Feign(默认关闭)
      traceSwitchFeign: true
      ##拦截位置
      traceDaoExecution: '* com.duansg.demo.dao.mapper..*.*(..)'
```

#### 日志模板

###### logback

```
<?xml version="1.0" encoding="UTF-8"?>  
<configuration>  
    <property name="LOG_HOME" value="c:/log" />  
    <!-- 控制台输出 -->  
    <appender name="STDOUT" class="ch.qos.logback.core.ConsoleAppender">  
        <Encoding>UTF-8</Encoding>  
        <layout class="ch.qos.logback.classic.PatternLayout">  
            <pattern>%d{HH:mm:ss.SSS} [%thread] %-5level %logger{50} - %msg%n  
            </pattern>  
        </layout>  
    </appender>  
    <!-- 按照每天生成日志文件 -->  
    <appender name="FILE"  
        class="ch.qos.logback.core.rolling.RollingFileAppender">  
        <Encoding>UTF-8</Encoding>  
        <rollingPolicy class="ch.qos.logback.core.rolling.TimeBasedRollingPolicy">  
            <FileNamePattern>${LOG_HOME}/myApp.log.%d{yyyy-MM-dd}.log</FileNamePattern>  
            <MaxHistory>30</MaxHistory>  
        </rollingPolicy>  
        <layout class="ch.qos.logback.classic.PatternLayout">  
            <pattern>%d{HH:mm:ss.SSS} [%thread] %-5level %logger{50} - %msg%n  
            </pattern>  
        </layout>  
    </appender>  
    <root level="DEBUG">  
        <appender-ref ref="STDOUT" />  
        <appender-ref ref="FILE" />  
    </root>  
</configuration>  
```

###### log4j

```java
### 配置根 ###
log4j.rootLogger = debug,console ,fileAppender,dailyRollingFile,ROLLING_FILE,MAIL,DATABASE

### 设置输出sql的级别，其中logger后面的内容全部为jar包中所包含的包名 ###
log4j.logger.org.apache=dubug
log4j.logger.java.sql.Connection=dubug
log4j.logger.java.sql.Statement=dubug
log4j.logger.java.sql.PreparedStatement=dubug
log4j.logger.java.sql.ResultSet=dubug

### 配置输出到控制台 ###
log4j.appender.console = org.apache.log4j.ConsoleAppender
log4j.appender.console.Target = System.out
log4j.appender.console.layout = org.apache.log4j.PatternLayout
log4j.appender.console.layout.ConversionPattern =  %d{ABSOLUTE} %5p %c{ 1 }:%L - %m%n

### 配置输出到文件 ###
log4j.appender.fileAppender = org.apache.log4j.FileAppender
log4j.appender.fileAppender.File = logs/log.log
log4j.appender.fileAppender.Append = true
log4j.appender.fileAppender.Threshold = DEBUG
log4j.appender.fileAppender.layout = org.apache.log4j.PatternLayout
log4j.appender.fileAppender.layout.ConversionPattern = %-d{yyyy-MM-dd HH:mm:ss}  [ %t:%r ] - [ %p ]  %m%n

### 配置输出到文件，并且每天都创建一个文件 ###
log4j.appender.dailyRollingFile = org.apache.log4j.DailyRollingFileAppender
log4j.appender.dailyRollingFile.File = logs/log.log
log4j.appender.dailyRollingFile.Append = true
log4j.appender.dailyRollingFile.Threshold = DEBUG
log4j.appender.dailyRollingFile.layout = org.apache.log4j.PatternLayout
log4j.appender.dailyRollingFile.layout.ConversionPattern = %-d{yyyy-MM-dd HH:mm:ss}  [ %t:%r ] - [ %p ]  %m%n

### 配置输出到文件，且大小到达指定尺寸的时候产生一个新的文件 ###
log4j.appender.ROLLING_FILE=org.apache.log4j.RollingFileAppender 
log4j.appender.ROLLING_FILE.Threshold=ERROR 
log4j.appender.ROLLING_FILE.File=rolling.log 
log4j.appender.ROLLING_FILE.Append=true 
log4j.appender.ROLLING_FILE.MaxFileSize=10KB 
log4j.appender.ROLLING_FILE.MaxBackupIndex=1 
log4j.appender.ROLLING_FILE.layout=org.apache.log4j.PatternLayout 
log4j.appender.ROLLING_FILE.layout.ConversionPattern=[framework] %d - %c -%-4r [%t] %-5p %c %x - %m%n

### 配置输出到邮件 ###
log4j.appender.MAIL=org.apache.log4j.net.SMTPAppender
log4j.appender.MAIL.Threshold=FATAL
log4j.appender.MAIL.BufferSize=10
log4j.appender.MAIL.From=chenyl@yeqiangwei.com
log4j.appender.MAIL.SMTPHost=mail.hollycrm.com
log4j.appender.MAIL.Subject=Log4J Message
log4j.appender.MAIL.To=chenyl@yeqiangwei.com
log4j.appender.MAIL.layout=org.apache.log4j.PatternLayout
log4j.appender.MAIL.layout.ConversionPattern=[framework] %d - %c -%-4r [%t] %-5p %c %x - %m%n

### 配置输出到数据库 ###
log4j.appender.DATABASE=org.apache.log4j.jdbc.JDBCAppender
log4j.appender.DATABASE.URL=jdbc:mysql://localhost:3306/test
log4j.appender.DATABASE.driver=com.mysql.jdbc.Driver
log4j.appender.DATABASE.user=root
log4j.appender.DATABASE.password=
log4j.appender.DATABASE.sql=INSERT INTO LOG4J (Message) VALUES ('[framework] %d - %c -%-4r [%t] %-5p %c %x - %m%n')
log4j.appender.DATABASE.layout=org.apache.log4j.PatternLayout
log4j.appender.DATABASE.layout.ConversionPattern=[framework] %d - %c -%-4r [%t] %-5p %c %x - %m%n
log4j.appender.A1=org.apache.log4j.DailyRollingFileAppender
log4j.appender.A1.File=SampleMessages.log4j
log4j.appender.A1.DatePattern=yyyyMMdd-HH'.log4j'
log4j.appender.A1.layout=org.apache.log4j.xml.XMLLayout
```

#### 依赖

###### Dao(可单独使用,但是需要手动初始化上下文,不过一般不会出现单独使用的情况)

```java
<dependency>
    <groupId>org.duansg.trace</groupId>
    <artifactId>duansg-trace-dao-autoconfiguration</artifactId>
    <version>1.0.3</version>
</dependency>
```

###### Feign(可以单独在消费端使用,但是服务端需要引入Pv依赖)

```java
<dependency>
    <groupId>org.duansg.trace</groupId>
    <artifactId>duansg-trace-feign-autoconfiguration</artifactId>
    <version>1.0.3</version>
</dependency>
```

###### Pv(可单独使用)

```java
注意：启动类需要额外扫描@ComponentScan(basePackages={"XX.XXX.XXXX.*","duansg.trace.pv"})
<dependency>
    <groupId>org.duansg.trace</groupId>
    <artifactId>duansg-trace-pv-autoconfiguration</artifactId>
    <version>1.0.3</version>
</dependency>
```

###### Dubbo(可单独使用)

```java
<dependency>
    <groupId>org.duansg.trace</groupId>
    <artifactId>duansg-trace-dubbo-autoconfiguration</artifactId>
    <version>1.0.3</version>
</dependency>
```

###### 自定义日志格式

```java
//TODO 未实现,已有构思
```

###### 热加载

```java
//TODO 已实现,待完善
```

###### 示例

```
//业务日志
	[com.duansg.demo.controller.Test.queryUser(19)][40b82bc28dc54ca98b39d5d560aa8bc6] -> 查询用户,传入的用户ID为:1
//Pv
	(40b82bc28dc54ca98b39d5d560aa8bc6)(duansg,/test,120,springmvc,S)
//Dao
	(8551affab3624fc4b4b8109a5b6db306)(duansg,com.duansg.demo.dao.mapper.UserMapper.selectById,195,S)
//Feign
(52daddfad41d4a4c91018781a4b143af)(duansg1,-,/user/queryUser,feign)
```



###### Pressure test

```java
//压测数据的分析,需要在请求头中加入key:trace-loadTest,value:custorm
/**
 * 日志输入如下示例
 */
(test)(c04338872a8046c689889ec1f4d55d97)(infra-user,/user/queryUser,50,springmvc,S)
```

#### 使用

###### 内部打印操作

```java
LoggerFormatUtil.info(logger,"UserService get user info ,userId:{0} ,userName:{1}",10254, "Duansg");
LoggerFormatUtil.debug(logger,"UserService get user info ,userId:{0} ,userName:{1}",10254, "Duansg");
LoggerFormatUtil.error(logger,"UserService get user info ,userId:{0} ,userName:{1}",10254, "Duansg");
LoggerFormatUtil.warn(logger,"UserService get user info ,userId:{0} ,userName:{1}",10254, "Duansg");
```

###### 上下文操作相关

```java
TraceContext traceContext = TraceUtil.getTraceContext();//获取当前上下文对象
TraceUtil.setTraceContext(new TraceContext());//设置上下文,默认生成traceId
TraceUtil.setTraceContext(new TraceContext("eyJhbGciOiJIUzI1NiJ9"));//设置带TraceId的上下文
TraceInitUtil.initTraceContext();//初始化上下文对象,并设置到全局变量中
TraceContext traceContext = TraceInitUtil.generateTraceContext();//构建上下文对象
TraceUtil.clearTraceContext();//清理上下文
TraceContext traceContext = TraceUtil.cloneTraceContext();//克隆当前上下文对象,为了防止清理操作或者参数篡改
String traceId = TraceUtil.getTraceId();//获取当前上下文中的TraceId
```

###### 扩展信息操作相关

```java
String contextExtendParam = TraceUtil.getContextExtendParam("traceId");//获取指定的扩展信息
Map<String, String> contextExtendField = TraceUtil.getContextExtendField();//获取扩展信息
TraceUtil.putContextExtendParam("traceId","eyJhbGciOiJIUzI1NiJ9");//设置扩展信息
```



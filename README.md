# trace(摘要日志)

#### 前言

```java
我们都知道Spring Cloud Sleuth可以解决分布式日志链路的问题,
但是它没有携带更直观的表现方式(比如响应耗时,响应结果集,调用地址,方法名称,压测信息等等),
我们在排查系统问题用得最多的手段就是查看服务日志,
在分布式环境中一般使用ELK来统一收集日志,但是在某种问题下(比如并发大,服务错综复杂)时使用日志定位问题还是比较麻烦,
由于大量的其他用户、其他线程、消息、RPC等等的日志也一起输出穿插其中导致很难筛选出指定请求的全部相关日志,以及下游线程、服务等对应的日志,
也无法串联起来，最常见的处理方式就是上游提供一个订单号或者唯一标识,
下游系统通过这个标识在寻找到指定业务的操作日志,
这样不仅繁琐,而且在没有唯一标识的情况下,想要定位某一种业务的操作日志是及其浪费效率的。
虽然现在也有很多现成的方案在来解决此类问题，但是大概了解一下,很多就是要改动网关等其他业务代码、增加处理服务等等。
```
    	
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
            traceDaoExecution: '* com.baomidou.mybatisplus.core.mapper..*.*(..)'
```

#### 日志模板

###### logback

```java
<?xml version="1.0" encoding="UTF-8"?>
<configuration>
    <include resource="org/springframework/boot/logging/logback/defaults.xml" />
    <property name="LOG_FILE" value="${LOG_FILE:-${LOG_PATH:-${LOG_TEMP:-${java.io.tmpdir:-/tmp}}}/spring.log}"/>
    <property name="STD_LOG_NAME" value="log"/>
    <property name="ERR_LOG_NAME" value="error"/>
    <property name="STD_LOG_SUFFIX" value=".log"/>
    <!-- 控制台输出 -->
    <appender name="STDOUT" class="ch.qos.logback.core.ConsoleAppender">
        <encoder class="ch.qos.logback.classic.encoder.PatternLayoutEncoder">
            <!--格式化输出：%d表示日期，%thread表示线程名，%-5level：级别从左显示5个字符宽度%msg：日志消息，%n是换行符-->
            <pattern>%d{yyyy-MM-dd HH:mm:ss.SSS} [%thread] %-5level %logger{50} - %msg%n</pattern>
        </encoder>
    </appender>
    <appender name="ROLLING_FILE"
              class="ch.qos.logback.core.rolling.RollingFileAppender">
        <encoder>
            <pattern>${FILE_LOG_PATTERN}</pattern>
        </encoder>
        <file>${LOG_FILE}${STD_LOG_NAME}${STD_LOG_SUFFIX}</file>
        <rollingPolicy class="ch.qos.logback.core.rolling.TimeBasedRollingPolicy">
            <fileNamePattern>${LOG_FILE}${STD_LOG_NAME}${STD_LOG_SUFFIX}.%d{yyyy-MM-dd}.%i</fileNamePattern>
            <maxHistory>30</maxHistory>
            <timeBasedFileNamingAndTriggeringPolicy class="ch.qos.logback.core.rolling.SizeAndTimeBasedFNATP">
                <maxFileSize>100MB</maxFileSize>
            </timeBasedFileNamingAndTriggeringPolicy>
        </rollingPolicy>
    </appender>
    <appender name="ROLLING_ERR_FILE"
              class="ch.qos.logback.core.rolling.RollingFileAppender">
        <filter class="ch.qos.logback.classic.filter.ThresholdFilter">
            <level>ERROR</level>
        </filter>
        <encoder>
            <pattern>${FILE_LOG_PATTERN}</pattern>
        </encoder>
        <file>${LOG_FILE}${ERR_LOG_NAME}${STD_LOG_SUFFIX}</file>
        <rollingPolicy class="ch.qos.logback.core.rolling.TimeBasedRollingPolicy">
            <fileNamePattern>${LOG_FILE}${ERR_LOG_NAME}${STD_LOG_SUFFIX}.%d{yyyy-MM-dd}.%i</fileNamePattern>
            <maxHistory>30</maxHistory>
            <timeBasedFileNamingAndTriggeringPolicy class="ch.qos.logback.core.rolling.SizeAndTimeBasedFNATP">
                <maxFileSize>100MB</maxFileSize>
            </timeBasedFileNamingAndTriggeringPolicy>
        </rollingPolicy>
    </appender>
    <appender name="DAO_DIGEST_FILE"
              class="ch.qos.logback.core.rolling.RollingFileAppender">
        <encoder class="ch.qos.logback.classic.encoder.PatternLayoutEncoder">
            <pattern>%d{yyyy-MM-dd HH:mm:ss.SSS} %logger{50} - %msg%n</pattern>
        </encoder>
        <file>${LOG_FILE}daoDigest${STD_LOG_SUFFIX}</file>
        <rollingPolicy class="ch.qos.logback.core.rolling.TimeBasedRollingPolicy">
            <fileNamePattern>${LOG_FILE}daoDigest${STD_LOG_SUFFIX}.%d{yyyy-MM-dd}.%i</fileNamePattern>
            <maxHistory>30</maxHistory>
            <timeBasedFileNamingAndTriggeringPolicy class="ch.qos.logback.core.rolling.SizeAndTimeBasedFNATP">
                <maxFileSize>100MB</maxFileSize>
            </timeBasedFileNamingAndTriggeringPolicy>
        </rollingPolicy>
    </appender>
    <appender name="PV_DIGEST_FILE"
              class="ch.qos.logback.core.rolling.RollingFileAppender">
        <encoder class="ch.qos.logback.classic.encoder.PatternLayoutEncoder">
            <pattern>%d{yyyy-MM-dd HH:mm:ss.SSS} %logger{50} - %msg%n</pattern>
        </encoder>
        <file>${LOG_FILE}pvDigest${STD_LOG_SUFFIX}</file>
        <rollingPolicy class="ch.qos.logback.core.rolling.TimeBasedRollingPolicy">
            <fileNamePattern>${LOG_FILE}pvDigest${STD_LOG_SUFFIX}.%d{yyyy-MM-dd}.%i</fileNamePattern>
            <maxHistory>30</maxHistory>
            <timeBasedFileNamingAndTriggeringPolicy class="ch.qos.logback.core.rolling.SizeAndTimeBasedFNATP">
                <maxFileSize>100MB</maxFileSize>
            </timeBasedFileNamingAndTriggeringPolicy>
        </rollingPolicy>
    </appender>
    <appender name="FEIGN_DIGEST_FILE"
              class="ch.qos.logback.core.rolling.RollingFileAppender">
        <encoder class="ch.qos.logback.classic.encoder.PatternLayoutEncoder">
            <pattern>%d{yyyy-MM-dd HH:mm:ss.SSS} %logger{50} - %msg%n</pattern>
        </encoder>
        <file>${LOG_FILE}feignDigest${STD_LOG_SUFFIX}</file>
        <rollingPolicy class="ch.qos.logback.core.rolling.TimeBasedRollingPolicy">
            <fileNamePattern>${LOG_FILE}feignDigest${STD_LOG_SUFFIX}.%d{yyyy-MM-dd}.%i</fileNamePattern>
            <maxHistory>30</maxHistory>
            <timeBasedFileNamingAndTriggeringPolicy class="ch.qos.logback.core.rolling.SizeAndTimeBasedFNATP">
                <maxFileSize>100MB</maxFileSize>
            </timeBasedFileNamingAndTriggeringPolicy>
        </rollingPolicy>
    </appender>
    <root level="INFO">
        <appender-ref ref="STDOUT" />
        <appender-ref ref="ROLLING_FILE" />
        <appender-ref ref="ROLLING_ERR_FILE" />
    </root>
    <logger name="DAO_DIGEST" additivity="false" level="INFO">
        <appender-ref ref="DAO_DIGEST_FILE"/>
    </logger>
    <logger name="PV_DIGEST" additivity="false" level="INFO">
        <appender-ref ref="PV_DIGEST_FILE"/>
    </logger>
    <logger name="FEIGN_DIGEST" additivity="false" level="INFO">
        <appender-ref ref="FEIGN_DIGEST_FILE"/>
    </logger>
</configuration>
```

###### log4j

```java
log4j.logger.DAO_DIGEST = INFO,DAO_DIGEST
log4j.appender.DAO_DIGEST.Encoding=UTF-8
log4j.appender.DAO_DIGEST = org.apache.log4j.DailyRollingFileAppender
log4j.appender.DAO_DIGEST.File = logs/daoDigest.log
log4j.appender.DAO_DIGEST.Append = true
log4j.appender.DAO_DIGEST.Threshold = INFO
log4j.appender.DAO_DIGEST.layout = org.apache.log4j.PatternLayout
log4j.appender.DAO_DIGEST.layout.ConversionPattern = %-d{yyyy-MM-dd HH:mm:ss}  [ %t:%r ] - [ %p ]  %m%n

log4j.logger.PV_DIGEST = INFO,PV_DIGEST
log4j.appender.PV_DIGEST.Encoding=UTF-8
log4j.appender.PV_DIGEST = org.apache.log4j.DailyRollingFileAppender
log4j.appender.PV_DIGEST.File = logs/pvDigest.log
log4j.appender.PV_DIGEST.Append = true
log4j.appender.PV_DIGEST.Threshold = INFO
log4j.appender.PV_DIGEST.layout = org.apache.log4j.PatternLayout
log4j.appender.PV_DIGEST.layout.ConversionPattern = %-d{yyyy-MM-dd HH:mm:ss}  [ %t:%r ] - [ %p ]  %m%n

log4j.logger.FEIGN_DIGEST = INFO,FEIGN_DIGEST
log4j.appender.FEIGN_DIGEST.Encoding=UTF-8
log4j.appender.FEIGN_DIGEST = org.apache.log4j.DailyRollingFileAppender
log4j.appender.FEIGN_DIGEST.File = logs/feignDigest.log
log4j.appender.FEIGN_DIGEST.Append = true
log4j.appender.FEIGN_DIGEST.Threshold = INFO
log4j.appender.FEIGN_DIGEST.layout = org.apache.log4j.PatternLayout
log4j.appender.FEIGN_DIGEST.layout.ConversionPattern = %-d{yyyy-MM-dd HH:mm:ss}  [ %t:%r ] - [ %p ]  %m%n
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
@TraceLogFormat
class TraceDaoCustomModel extends DigestModel{
//....
}
```

###### 热加载

```java
//继承TraceRefreshConfigPublish并注册，
public abstract class TraceRefreshConfigPublish extends RefreshConfigPublish {
    /**
     * @desc TraceRefreshConfigPublish's logger.
     */
    private static final Logger logger = LoggerFactory.getLogger(TraceRefreshConfigPublish.class);

    public void publishDao(boolean off) {
        RefreshConfigModel model = new RefreshConfigModel.Builder()
                .buildType(TraceCustomConstants.DAO)
                .buildTraceSwitchDao(off)
                .build(this);
        publish(model);
    }
    //.....
}
```

###### 示例

```java
//业务日志
[com.duansg.demo.controller.Test.queryUser(19)][40b82bc28dc54ca98b39d5d560aa8bc6] -> 查询用户,传入的用户ID为:1
//Pv
(40b82bc28dc54ca98b39d5d560aa8bc6)(duansg,/test,120,springmvc,S)
//Dao
(8551affab3624fc4b4b8109a5b6db306)(duansg,com.duansg.demo.dao.mapper.UserMapper.selectById,195,S)
//Feign
(52daddfad41d4a4c91018781a4b143af)(duansg,-,/user/queryUser,feign)
```



###### Pressure test

```java
//压测数据的分析,需要在请求头中加入key:trace-loadTest,value:custorm
/**
 * 日志输入如下示例
 */
(test-20200818245)(c04338872a8046c689889ec1f4d55d97)(infra-user,/user/queryUser,50,springmvc,S)
(test-20200818245)(f408c77187654d2890eff8353a07910e)(infra-user,com.melot.planet.infra.user.data.repo.InfraUserMapMapper.selectList,18,S)
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
TraceInitUtil.generateTraceContext(traceId);//通过已知的traceId构建上下文对象
TraceContext traceContext = TraceInitUtil.generateTraceContext();//构建上下文对象
TraceUtil.clearTraceContext();//清理上下文
TraceContext traceContext = TraceUtil.cloneTraceContext();//克隆当前上下文对象,为了防止清理操作或者参数篡改
String traceId = TraceUtil.getTraceId();//获取当前上下文中的TraceId
//TODO....持续完善中
```

###### 扩展信息操作相关

```java
String contextExtendParam = TraceUtil.getContextExtendParam("traceId");//获取指定的扩展信息
Map<String, String> contextExtendField = TraceUtil.getContextExtendField();//获取扩展信息
TraceUtil.putContextExtendParam("traceId","eyJhbGciOiJIUzI1NiJ9");//设置扩展信息
//TODO....持续完善中
```

###### 热心小伙伴提供的问题

```java
1:父子线程问题[因为还涉及到线程池等问题,可以通过InheritableThreadLocal来实现,但需要在提交线程前就要设值]【未实现】
2:异步任务[这个可以通过自定义注解来实现,开启任务初始化即可]【未实现】
3:消息透传问题[这个目前可以在消息发送方跟接收方使用手动设置的办法,只需2行代码即可]【已实现】
```
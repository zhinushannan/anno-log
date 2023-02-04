<div style="text-align: center;"><h1>anno-log</h1></div>

# anno-log是什么？

我们平时在写代码的时候，经常需要复制大量的日志配置文件，很容易把我们累死。

在复制文件的时候，如果不小心弄错了一个字符，可能就怎么也调试不对，很容易把我们气死。

有时候我们只需要观察某几个接口，而不需要其他接口的日志，我们需要在一堆像屎一样的日志中找到我们需要的东西，很容易把我们的眼睛看瞎。

于是，我开发了这个基于注解的、为web应用服务的组件。

本项目可以：

- 注解+参数，免去配置
- 编写简单的配置类，不需要编写配置文件
- 需要哪个接口的日志就注解哪个方法，呼之即来挥之即去

本项目不可以：

- 直接在生产环境中使用

# 如何使用？

首先我们要创建一个Spring Boot项目。

在`pom.xml` 的`dependencies`标签内添加：

```xml
        <dependency>
            <groupId>io.github.zhinushannan</groupId>
            <artifactId>annolog</artifactId>
            <version>0.0.2-RELEASE</version>
        </dependency>
```

编写配置类：

```java
import io.github.zhinushannan.annolog.log.LogAspect;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AnnologConfig {

    @Bean
    public LogAspect logAspect() {
        return new LogAspect(true);
    }

}
```

编写一个测试接口：

```java
import io.github.zhinushannan.annolog.annotation.CtlPoint;
import io.github.zhinushannan.annolog.annotation.MappingPoint;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CtlPoint(business = "测试1")
public class DemoController {

    @RequestMapping("demo1")
    @MappingPoint(business = "测试2")
    public String demo1(@RequestParam("str") String str) {
        return str;
    }

}
```

然后我们去访问一下`http://localhost:8080/demo1?str=hello,anno-log`，此时可以看到控制台的日志输出：

```text
2023-02-03 13:25:10.202  INFO 24196 --- [nio-9090-exec-1] i.g.zhinushannan.annolog.log.LogAspect   : 【01db7cc3】业务名称：测试1 - 测试2
2023-02-03 13:25:10.202  INFO 24196 --- [nio-9090-exec-1] i.g.zhinushannan.annolog.log.LogAspect   : 【01db7cc3】请求信息：/demo1 GET io.github.zhinushannan.demo.controller.DemoController.demo1
2023-02-03 13:25:10.203  INFO 24196 --- [nio-9090-exec-1] i.g.zhinushannan.annolog.log.LogAspect   : 【01db7cc3】远程地址：0:0:0:0:0:0:0:1
2023-02-03 13:25:10.203  INFO 24196 --- [nio-9090-exec-1] i.g.zhinushannan.annolog.log.LogAspect   : 【01db7cc3】请求参数：[hello,anno-log]
2023-02-03 13:25:10.203  INFO 24196 --- [nio-9090-exec-1] i.g.zhinushannan.annolog.log.LogAspect   : 【01db7cc3】响应参数：hello,anno-log
2023-02-03 13:25:10.203  INFO 24196 --- [nio-9090-exec-1] i.g.zhinushannan.annolog.log.LogAspect   : 【01db7cc3】接口耗时：5 ms
```

> `@CtlPoint` 是作用在控制类上的，`@MappingPoint`是作用在控制类中带有`@RequestMapping`注解的函数上的。
>
> 日志只会打印出有`@MappingPoint`注解的函数，`@CtlPoint` 必须添加。
>
> 在日志的业务名称中会输出`{CtlPoint.business} - {MappingPoint.business}`。
>
> 最前面的`【01db7cc3】`的含义是该请求的ID，此ID由日志插件生成，用于区分哪些请求是同一个请求。

# 支持自定义

# 自定义日志处理器

如果你觉得默认的日志处理器太拉胯了，你可以选择自定义日志处理器。

通过实现`io.github.zhinushannan.annolog.processor.AnnoLogProcessor`接口，并将该日志处理器添加到配置中（当然你可以选择使用Lambda表达式的写法）：

```java
import io.github.zhinushannan.annolog.log.LogAspect;
import io.github.zhinushannan.annolog.log.LogIdStorage;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AnnologConfig {

    @Bean
    public LogAspect logAspect() {
        LogAspect logAspect = new LogAspect(true);
        logAspect.addProcessor((joinPoint, request, proceed, logger) -> logger.info("【{}】这是一条自定义日志：{}", LogIdStorage.get(), "卧槽太牛逼了"));
        return logAspect;
    }

}
```

此时我们再次访问`http://localhost:8080/demo1?str=hello,anno-log`，可以看到控制台的输出：

```text
2023-02-03 13:31:42.198  INFO 25480 --- [nio-9090-exec-1] i.g.zhinushannan.annolog.log.LogAspect   : 【caffb442】业务名称：测试1 - 测试2
2023-02-03 13:31:42.200  INFO 25480 --- [nio-9090-exec-1] i.g.zhinushannan.annolog.log.LogAspect   : 【caffb442】请求信息：/demo1 GET io.github.zhinushannan.demo.controller.DemoController.demo1
2023-02-03 13:31:42.200  INFO 25480 --- [nio-9090-exec-1] i.g.zhinushannan.annolog.log.LogAspect   : 【caffb442】远程地址：0:0:0:0:0:0:0:1
2023-02-03 13:31:42.200  INFO 25480 --- [nio-9090-exec-1] i.g.zhinushannan.annolog.log.LogAspect   : 【caffb442】请求参数：[hello,anno-log]
2023-02-03 13:31:42.200  INFO 25480 --- [nio-9090-exec-1] i.g.zhinushannan.annolog.log.LogAspect   : 【caffb442】响应参数：hello,anno-log
2023-02-03 13:31:42.200  INFO 25480 --- [nio-9090-exec-1] i.g.zhinushannan.annolog.log.LogAspect   : 【caffb442】这是一条自定义日志：卧槽太牛逼了
2023-02-03 13:31:42.200  INFO 25480 --- [nio-9090-exec-1] i.g.zhinushannan.annolog.log.LogAspect   : 【caffb442】接口耗时：6 ms
```

当然要是你认为默认的日志输出太烦人了，可以在创建配置对象的时候：

```java
        LogAspect logAspect = new LogAspect(false);
```


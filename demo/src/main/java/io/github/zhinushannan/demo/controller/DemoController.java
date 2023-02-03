package io.github.zhinushannan.demo.controller;

import io.github.zhinushannan.annolog.annotation.CtlPoint;
import io.github.zhinushannan.annolog.annotation.MappingPoint;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CtlPoint(business = "test")
public class DemoController {

    @RequestMapping("demo1")
    @MappingPoint(business = "测试接口")
    public String demo1(@RequestParam("str") String str) {
        return str;
    }

}

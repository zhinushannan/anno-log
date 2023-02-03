package io.github.zhinushannan.demo.controller;

import io.github.zhinushannan.annolog.annotation.CtlPoint;
import io.github.zhinushannan.annolog.annotation.MappingPoint;
import org.springframework.web.bind.annotation.*;

@RestController
@CtlPoint(business = "test")
public class DemoController {

    @GetMapping("demo1")
    @MappingPoint(business = "测试接口")
    public String demo1(@RequestParam("str") String str) {
        return str;
    }

    @GetMapping("demo2")
    @MappingPoint(business = "测试接口2")
    public String demo1() {
        return "1111";
    }

}

package io.github.zhinushannan.demo.config;

import io.github.zhinushannan.annolog.log.LogAspect;
import io.github.zhinushannan.annolog.processor.AnnoLogProcessor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.slf4j.Logger;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.servlet.http.HttpServletRequest;

@Configuration
public class AnnologConfig {

    @Bean
    public LogAspect logAspect() {
        LogAspect logAspect = new LogAspect(true);
        logAspect.addProcessor((joinPoint, request, proceed, logger) -> logger.info("牛逼"));
        return logAspect;
    }

}

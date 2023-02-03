package io.github.zhinushannan.demo.config;

import io.github.zhinushannan.annolog.log.LogAspect;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AnnologConfig {

    @Bean
    public LogAspect logAspect() {
        return new LogAspect(false);
    }

}

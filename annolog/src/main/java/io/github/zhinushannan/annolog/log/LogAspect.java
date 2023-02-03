package io.github.zhinushannan.annolog.log;

import io.github.zhinushannan.annolog.processor.AnnoLogProcessor;
import io.github.zhinushannan.annolog.processor.DefaultProcessor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Aspect
public class LogAspect {

    private static final Logger logger = LoggerFactory.getLogger(LogAspect.class);

    private final static List<AnnoLogProcessor> processors = new ArrayList<AnnoLogProcessor>();

    public LogAspect addProcessor(AnnoLogProcessor processor) {
        processors.add(processor);
        return this;
    }

    public LogAspect(boolean openDefault) {
        if (openDefault) {
            addProcessor(new DefaultProcessor());
        }
    }

    @Pointcut("@annotation(io.github.zhinushannan.annolog.annotation.MappingPoint)")
    public void pointcut() {
    }

    @Around("pointcut()")
    public Object around(final ProceedingJoinPoint joinPoint) throws Throwable {
        String logId = UUID.randomUUID().toString().split("-")[0];
        LogIdStorage.save(logId);

        long start = System.currentTimeMillis();
        final Object proceed = joinPoint.proceed();
        long end = System.currentTimeMillis();
        long cost = end - start;

        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        assert attributes != null;
        HttpServletRequest request = attributes.getRequest();
        for (AnnoLogProcessor processor : processors) {
            processor.annoLogProcessor(joinPoint, request, proceed, logger);
        }

        logger.info("【{}】接口耗时：{} ms", LogIdStorage.get(), cost);
        return proceed;
    }

}

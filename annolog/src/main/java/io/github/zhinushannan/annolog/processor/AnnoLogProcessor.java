package io.github.zhinushannan.annolog.processor;

import org.aspectj.lang.ProceedingJoinPoint;
import org.slf4j.Logger;

import javax.servlet.http.HttpServletRequest;

public interface AnnoLogProcessor {

    void annoLogProcessor(ProceedingJoinPoint joinPoint, HttpServletRequest request, Object proceed, Logger logger);

}

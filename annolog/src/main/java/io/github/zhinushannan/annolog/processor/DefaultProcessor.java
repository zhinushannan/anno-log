package io.github.zhinushannan.annolog.processor;

import io.github.zhinushannan.annolog.annotation.CtlPoint;
import io.github.zhinushannan.annolog.annotation.MappingPoint;
import io.github.zhinushannan.annolog.log.LogIdStorage;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.slf4j.Logger;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.Arrays;

public class DefaultProcessor implements AnnoLogProcessor {
    public void annoLogProcessor(ProceedingJoinPoint joinPoint, HttpServletRequest request, Object proceed, Logger logger) {
        // 请求的uri
        String uri = request.getRequestURI();
        // 请求的方法类型
        String method = request.getMethod();
        // 请求的远程地址
        String remoteAddr = request.getRemoteAddr();
        // 请求参数
        Object[] args = joinPoint.getArgs();
        Object[] arguments = new Object[args.length];
        for (int i = 0; i < args.length; i++) {
            if (args[i] instanceof ServletRequest
                    || args[i] instanceof ServletResponse
                    || args[i] instanceof MultipartFile) {
                continue;
            }
            arguments[i] = args[i];
        }
        String params = Arrays.toString(arguments);

        Signature signature = joinPoint.getSignature();
        // 执行的控制类
        Class<?> ctlClass = signature.getDeclaringType();
        // 执行的方法名称
        String function = signature.getName();

        String ctlBusiness = ctlClass.getAnnotation(CtlPoint.class).business();
        String functionBusiness = "";
        for (Method method1 : ctlClass.getMethods()) {
            if (method1.getName().equals(function)) {
                functionBusiness = method1.getAnnotation(MappingPoint.class).business();
            }
        }

        String businessName = "";
        if (!"".equals(ctlBusiness) && !"".equals(functionBusiness)) {
            businessName = ctlBusiness + " - " + functionBusiness;
        } else if (!"".equals(ctlBusiness)) {
            businessName = ctlBusiness;
        } else if (!"".equals(functionBusiness)) {
            businessName = functionBusiness;
        }


        if (businessName.length() != 0) {
            logger.info("【{}】业务名称：{}", LogIdStorage.get(), businessName);
        }
        logger.info("【{}】请求信息：{} {} {}.{}", LogIdStorage.get(), uri, method, ctlClass.getName(), function);
        logger.info("【{}】远程地址：{}", LogIdStorage.get(), remoteAddr);
        logger.info("【{}】请求参数：{}", LogIdStorage.get(), params);
        logger.info("【{}】响应参数：{}", LogIdStorage.get(), proceed);
    }
}

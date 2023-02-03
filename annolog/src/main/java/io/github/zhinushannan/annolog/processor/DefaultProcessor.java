package io.github.zhinushannan.annolog.processor;

import io.github.zhinushannan.annolog.annotation.CtlPoint;
import io.github.zhinushannan.annolog.annotation.MappingPoint;
import io.github.zhinushannan.annolog.log.LogIdStorage;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.slf4j.Logger;
import org.springframework.web.bind.annotation.*;
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

        // =======================================
        // === 获取类业务名和方法业务名，允许方法重载 ===
        // =======================================
        // 获取控制类上的 business
        String ctlBusiness = ctlClass.getAnnotation(CtlPoint.class).business();
        // 获得该请求对应的方法
        String functionBusiness = "";
        Method signMethod = getMethod(ctlClass, uri, method);
        if (null != signMethod) {
            functionBusiness = signMethod.getAnnotation(MappingPoint.class).business();
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

    /**
     * 根据 控制类类型、请求uri、请求方法 获取对应的处理函数
     */
    private Method getMethod(Class<?> ctlClass, String uri, String requestMethod) {
        uri = trimRootDir(uri);

        // 获取类上的 RequestMapping 的路径
        RequestMapping ctlAnnotation = ctlClass.getAnnotation(RequestMapping.class);
        if (null != ctlAnnotation) {
            String[] ctlValue = ctlClass.getAnnotation(RequestMapping.class).value();
            for (String value : ctlValue) {
                // 去除 uri 中前面的类请求路径
                if (uri.startsWith(trimRootDir(value))) {
                    uri = uri.substring(trimRootDir(value).length() + 1);
                }
            }
        }

        // 获取所有方法
        Method[] methods = ctlClass.getMethods();
        for (Method method : methods) {
            // 查找 RequestMapping 注解
            RequestMapping requestMappingAnnotation = method.getAnnotation(RequestMapping.class);
            if (requestMappingAnnotation != null) {
                RequestMethod[] requestMethods = requestMappingAnnotation.method();
                if (requestMethods.length == 0) {
                    if (getMethodByUri(requestMappingAnnotation.value(), uri)) {
                        return method;
                    }
                } else {
                    // 如果 RequestMapping 注解的请求方法存在目标值
                    RequestMethod equalMethod = getEqualMethod(requestMethods, requestMethod);
                    if (equalMethod != null) {
                        if (getMethodByUri(requestMappingAnnotation.value(), uri)) {
                            return method;
                        }
                    }
                }
            }

            // =======================================
            // ========= 查找其他注解 ==================
            // =======================================
            if (requestMethod.equals(RequestMethod.GET.name())) {
                GetMapping annotation = method.getAnnotation(GetMapping.class);
                if (annotation != null && getMethodByUri(annotation.value(), uri)) {
                    return method;
                }
            }

            if (requestMethod.equals(RequestMethod.POST.name())) {
                PostMapping annotation = method.getAnnotation(PostMapping.class);
                if (annotation != null && getMethodByUri(annotation.value(), uri)) {
                    return method;
                }
            }

            if (requestMethod.equals(RequestMethod.PUT.name())) {
                PutMapping annotation = method.getAnnotation(PutMapping.class);
                if (annotation != null && getMethodByUri(annotation.value(), uri)) {
                    return method;
                }
            }

            if (requestMethod.equals(RequestMethod.DELETE.name())) {
                DeleteMapping annotation = method.getAnnotation(DeleteMapping.class);
                if (annotation != null && getMethodByUri(annotation.value(), uri)) {
                    return method;
                }
            }

            if (requestMethod.equals(RequestMethod.PATCH.name())) {
                PatchMapping annotation = method.getAnnotation(PatchMapping.class);
                if (annotation != null && getMethodByUri(annotation.value(), uri)) {
                    return method;
                }
            }

        }

        return null;
    }

    /**
     * 查找请求方法与目标值相同的 RequestMethod 对象
     */
    private RequestMethod getEqualMethod(RequestMethod[] requestMethods, String requestMethod) {
        for (RequestMethod requestMethod1 : requestMethods) {
            if (requestMethod1.name().equals(requestMethod)) {
                return requestMethod1;
            }
        }
        return null;
    }

    /**
     * 删除 uri 中前后的根目录
     * 如 /demo/ ，修改为 demo
     */
    private String trimRootDir(String uri) {
        return uri.startsWith("/") ? uri.substring(1) : uri;
    }

    /**
     * 根据类型判断 method 是否是目标值
     */
    private boolean getMethodByUri(String[] value, String uri) {
        for (String v : value) {
            if (trimRootDir(v).equals(uri)) {
                return true;
            }
        }
        return false;
    }

}

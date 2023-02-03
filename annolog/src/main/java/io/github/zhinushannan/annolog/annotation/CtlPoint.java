package io.github.zhinushannan.annolog.annotation;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface CtlPoint {

    String business();

}

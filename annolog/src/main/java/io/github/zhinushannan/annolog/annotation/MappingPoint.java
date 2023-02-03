package io.github.zhinushannan.annolog.annotation;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface MappingPoint {

    String business() default "";

}

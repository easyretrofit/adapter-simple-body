package io.github.easyretrofit.adapter.simplebody;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
public @interface ErrorResponse {
    String codeFieldName() default "";

    Class<?> codeType() default Object.class;

    String messageFieldName() default "";

    Class<?> messageType() default Object.class;
}

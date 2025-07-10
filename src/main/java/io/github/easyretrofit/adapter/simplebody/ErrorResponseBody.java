package io.github.easyretrofit.adapter.simplebody;

import java.lang.annotation.*;

@Deprecated
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface ErrorResponseBody {

    String codeFieldName() default "";

    Class<?> codeType() default Object.class;

    String messageFieldName() default "";

    Class<?> messageType() default Object.class;
}

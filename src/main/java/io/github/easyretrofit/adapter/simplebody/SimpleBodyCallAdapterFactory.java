package io.github.easyretrofit.adapter.simplebody;

import okhttp3.ResponseBody;
import retrofit2.*;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.util.Arrays;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;


/**
 *
 */
public class SimpleBodyCallAdapterFactory extends CallAdapter.Factory {

    private final Class<?>[] excludeCallTypes;

    private final Function<ErrorParameter, ?> customErrorFunction;


    public SimpleBodyCallAdapterFactory(Class<?>[] excludeCallTypes, Function<ErrorParameter, ?> customErrorFunction) {
        this.excludeCallTypes = excludeCallTypes;
        this.customErrorFunction = customErrorFunction;
    }

    /**
     * create a SimpleBodyCallAdapterFactory <br>
     * When returnType is retrofit2 official call adapter type , return null
     *
     * @return BodyCallAdapterFactory
     */
    public static SimpleBodyCallAdapterFactory create() {
        return new SimpleBodyCallAdapterFactory(null, null);
    }

    /**
     * create a SimpleBodyCallAdapterFactory <br>
     *
     * @param exclude Manually exclude call adapter type
     * @return BodyCallAdapterFactory
     */
    public static SimpleBodyCallAdapterFactory create(Class<?>[] exclude) {
        return new SimpleBodyCallAdapterFactory(exclude, null);
    }

    public static SimpleBodyCallAdapterFactory create(Class<?>[] exclude, Function<ErrorParameter, ?> customErrorFunction) {
        return new SimpleBodyCallAdapterFactory(exclude, customErrorFunction);
    }

    public static SimpleBodyCallAdapterFactory create(Function<ErrorParameter, ?> customErrorFunction) {
        return new SimpleBodyCallAdapterFactory(null, customErrorFunction);
    }

    @Override
    public CallAdapter<?, ?> get(Type returnType, Annotation[] annotations, Retrofit retrofit) {
        Class<?> rawType = getRawType(returnType);
        String typeName = rawType.getName();
        if (Call.class.isAssignableFrom(rawType)) {
            return null;
        }
        if (Response.class.isAssignableFrom(rawType)) {
            return null;
        }
        if (CompletableFuture.class.isAssignableFrom(rawType)) {
            return null;
        }
        if (excludeCallTypes != null) {
            for (Class<?> callType : excludeCallTypes) {
                if (callType == rawType) {
                    return null;
                }
            }
        }
        // if retrofit official async adapters, return null
        if ("io.reactivex.rxjava3.core.Observable".equals(typeName) || "io.reactivex.rxjava3.core.Single".equals(typeName) || "io.reactivex.rxjava3.core.Completable".equals(typeName) || "io.reactivex.rxjava3.core.Flowable".equals(typeName) || "io.reactivex.rxjava3.core.Maybe".equals(typeName)) {
            return null;
        }
        if ("io.reactivex.Observable".equals(typeName) || "io.reactivex.Single".equals(typeName) || "io.reactivex.Completable".equals(typeName) || "io.reactivex.Flowable".equals(typeName) || "io.reactivex.Maybe".equals(typeName)) {
            return null;
        }
        if ("rx.Observable".equals(typeName) || "rx.Single".equals(typeName) || "rx.Completable".equals(typeName)) {
            return null;
        }
        if ("com.google.common.util.concurrent.ListenableFuture".equals(typeName)) {
            return null;
        }
        if ("reactor.core.publisher.Mono".equals(typeName) || "reactor.core.publisher.Flux".equals(typeName)) {
            return null;
        }
        if ("scala.concurrent.Future".equals(typeName) || "scala.concurrent.Promise".equals(typeName)) {
            return null;
        }

        return new BodyCallAdapter<>(rawType, annotations, retrofit, customErrorFunction);
    }


    static final class BodyCallAdapter<R> implements CallAdapter<R, R> {

        private final Type returnType;

        private final Retrofit retrofit;

        private final Annotation[] annotations;

        private final Function<ErrorParameter, ?> customErrorFunction;

        BodyCallAdapter(Type returnType, Annotation[] annotations, Retrofit retrofit, Function<ErrorParameter, ?> customErrorFunction) {
            this.returnType = returnType;
            this.retrofit = retrofit;
            this.annotations = annotations;
            this.customErrorFunction = customErrorFunction;
        }

        @Override
        public Type responseType() {
            return returnType;
        }

        @Override
        public R adapt(Call<R> call) {
            Response<R> response;
            try {
                response = call.execute();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            if (response.isSuccessful()) {
                return response.body();
            }

            ResponseBody errorBody = response.errorBody();
            if (errorBody == null) {
                return null;
            }
            Converter<ResponseBody, R> converter = retrofit.responseBodyConverter(responseType(), annotations);
            try {
                return converter.convert(Objects.requireNonNull(errorBody));
            } catch (IOException e) {
                for (Annotation annotation : annotations) {
                    if (annotation instanceof ErrorResponseBody) {
                        return reflectErrorResponse((ErrorResponseBody) annotation, response, returnType);
                    }
                }
                if (customErrorFunction != null) {
                    Object apply = customErrorFunction.apply(new ErrorParameter(response, returnType));
                    try {
                        return (R) apply;
                    } catch (ClassCastException ec) {
                        throw new RuntimeException(ec);
                    }
                }
                throw new RuntimeException(e);
            }
        }

        private static <R> R reflectErrorResponse(ErrorResponseBody annotation, Response<R> response, Type returnType) throws RuntimeException {
            try {
                Class<?> clazz = Class.forName(returnType.getTypeName());
                Object returnBody = clazz.newInstance();
                if (annotation.codeType() != Objects.class && !annotation.codeFieldName().isEmpty()) {
                    Field codeField = getField(annotation.codeFieldName(), clazz);
                    if (codeField != null) {
                        codeField.setAccessible(true);
                        codeField.set(returnBody, response.code());
                    }
                }
                if (annotation.messageType() != Objects.class && !annotation.messageFieldName().isEmpty()) {
                    Field msgField = getField(annotation.messageFieldName(), clazz);
                    if (msgField != null) {
                        msgField.setAccessible(true);
                        msgField.set(returnBody, response.message());
                    }
                }
                return (R) returnBody;
            } catch (IllegalAccessException | InstantiationException | ClassNotFoundException ex) {
                throw new RuntimeException(ex);
            }
        }

        private static Field getField(String fieldName, Class<?> returnType) {
            try {
                return returnType.getDeclaredField(fieldName);
            } catch (NoSuchFieldException e) {
                return null;
            }
        }

    }
}

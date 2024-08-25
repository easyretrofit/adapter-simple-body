package io.github.easyretrofit.adapter.simplebody;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Converter;
import retrofit2.Retrofit;

import javax.annotation.Nullable;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

public final class ResultErrorBodyConverterFactory extends Converter.Factory {

    public static final ResultErrorBodyConverterFactory INSTANCE = new ResultErrorBodyConverterFactory();

    public static Converter.Factory create() {
        return null;
    }

    @Override
    public Converter<?, RequestBody> requestBodyConverter(Type type, Annotation[] parameterAnnotations, Annotation[] methodAnnotations, Retrofit retrofit) {
        return null;
    }

    @Override
    public Converter<ResponseBody, ?> responseBodyConverter(Type type, Annotation[] annotations, Retrofit retrofit) {
        return new ResultErrorBodyConverter(type, retrofit);
    }

    private static class ResultErrorBodyConverter implements Converter<ResponseBody, Result<?>> {

        private final Type type;
        private final Retrofit retrofit;

        public ResultErrorBodyConverter(Type type, Retrofit retrofit) {
            this.type = type;
            this.retrofit = retrofit;
        }

        @Nullable
        @Override
        public Result<?> convert(ResponseBody responseBody) throws IOException {
            return new Result<>(-1, null, responseBody.string());
        }
    }
}

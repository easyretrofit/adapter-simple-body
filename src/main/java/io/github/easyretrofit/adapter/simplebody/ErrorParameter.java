package io.github.easyretrofit.adapter.simplebody;

import okhttp3.ResponseBody;
import retrofit2.Response;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

public class ErrorParameter {

    private Annotation[] annotations;

    private Response response;

    private Type returnType;

    public <R> ErrorParameter(Response response, Type returnType, Annotation[] annotations) {
        this.response = response;
        this.returnType = returnType;
        this.annotations = annotations;
    }

    public Response getResponse() {
        return response;
    }

    public void setResponse(Response response) {
        this.response = response;
    }

    public Type getReturnType() {
        return returnType;
    }

    public void setReturnType(Type returnType) {
        this.returnType = returnType;
    }

    public Annotation[] getAnnotations() {
        return annotations;
    }

    public void setAnnotations(Annotation[] annotations) {
        this.annotations = annotations;
    }
}

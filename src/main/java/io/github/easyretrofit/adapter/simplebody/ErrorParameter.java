package io.github.easyretrofit.adapter.simplebody;

import okhttp3.ResponseBody;
import retrofit2.Response;

import java.lang.reflect.Type;

public class ErrorParameter {

    private Response response;

    private Type returnType;

    public <R> ErrorParameter(Response response, Type returnType) {
        this.response = response;
        this.returnType = returnType;
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
}

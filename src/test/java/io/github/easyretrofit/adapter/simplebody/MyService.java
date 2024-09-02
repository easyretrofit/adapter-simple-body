package io.github.easyretrofit.adapter.simplebody;

import retrofit2.http.GET;

import java.util.Collections;
import java.util.List;

public class MyService {

    public static final String API_URL = "http://localhost:8080";

    public static class HelloBean {
        public final String name;
        public final int age;

        public HelloBean(String name, int age) {
            this.name = name;
            this.age = age;
        }
    }

    public static class ResultStatic<T> {
        private int code;
        private T data;
        private String msg;

        public ResultStatic(){
        }

        public ResultStatic(int code, T data, String msg) {
            this.code = code;
            this.data = data;
            this.msg = msg;
        }

        public int getCode() {
            return code;
        }

        public void setCode(int code) {
            this.code = code;
        }

        public T getData() {
            return data;
        }

        public void setData(T data) {
            this.data = data;
        }

        public String getMsg() {
            return msg;
        }

        public void setMsg(String msg) {
            this.msg = msg;
        }
    }

    public Result<List<HelloBean>> getHellos() {
        HelloBean helloBean = new HelloBean("hello", 99);
        List<HelloBean> helloBeans = Collections.singletonList(helloBean);
        return new Result<>(200, helloBeans, "success");
    }

    public interface MyServiceApi {
        @GET("/hello")
        @ErrorResponseBody(codeFieldName="code", codeType=int.class , messageFieldName="msg", messageType=String.class)
        Result<List<HelloBean>> getHellos();


        @GET("/hello")
        @ErrorResponseBody(codeFieldName="code", codeType=int.class , messageFieldName="msg", messageType=String.class)
        ResultStatic<List<HelloBean>> getHellos2();

        @GET("/hello")
        Result<List<HelloBean>> getHellos3();
    }
}

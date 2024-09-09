# easy-retrofit-adapter-simple-body
When a synchronization request is made, the response value is a `<T>` that is not wrapped by the Call<T> Class of Retrofit, 
and this `<T>` has the same structure as the returned value of the called API

## Usage

### used with easy-retrofit

#### create a SimpleBodyConverterFactoryBuilder class
```java

public class SimpleBodyConverterFactoryBuilder extends BaseConverterFactoryBuilder {
    @Override
    public Converter.Factory build() {
        return SimpleBodyCallAdapterFactory.create();
    }
}

```

#### add SimpleBodyConverterFactoryBuilder to your project
```java
@RetrofitBuilder(baseUrl = "${app.backend.url}",
        addConverterFactory = {GsonConvertFactoryBuilder.class, SimpleBodyConverterFactoryBuilder.class})
public interface HelloApi {
    
}
```

### used with retrofit2
```java

Retrofit retrofit = new Retrofit.Builder()
        .addCallAdapterFactory(SimpleBodyCallAdapterFactory.create())
        .build();
```

## Advanced Usage

### use error Function
If your response returns an error Body and the body cannot be parsed by the converter(json xml or others), resulting in an exception being returned,you can set global exception handling or customize the handling return value

```java
        Retrofit retrofit = new Retrofit.Builder()
        .baseUrl(server.url("/"))
        .addConverterFactory(GsonConverterFactory.create())
        .addCallAdapterFactory(SimpleBodyCallAdapterFactory.create(errorParameter -> {
            throw new RuntimeException(errorParameter.getResponse().toString());
        }))
        .build();
```

### use @ErrorResponseBody
If your response returns an error Body and the body cannot be parsed by the converter(json xml or others), resulting in an exception being returned, you can use this annotation to map the values in your response. If your response object contains fields such as http status code, error message, etc

```java
public class Result<T> {
    private int code;
    private T data;
    private String msg;

    public Result(){
    }

    public Result(int code, T data, String msg) {
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

public interface MyServiceApi {
    @GET("/hello")
    @ErrorResponseBody(codeFieldName = "code", codeType = int.class, messageFieldName = "msg", messageType = String.class)
    Result<List<HelloBean>> getHellos();
}
```


## What is the difference

### Unused
if you not use `easy-retrofit-adapter-simple-body`, you should use retrofit2 `Call<T>` to get the response value

myServiceApi interface:
```java

public interface MyServiceApi {
    @GET("/hello")
    Call<Result<List<HelloBean>>> getCallHellos();
}

```

Api usage: 
```java
MyServiceApi myServiceApi = retrofit.create(MyServiceApi.class);
Call<Result<List<HelloBean>>> callHellos = myServiceApi.getCallHellos();
Result<List<HelloBean>> body = callHellos.execute().body();

```

### Used

if you use `easy-retrofit-adapter-simple-body`, you can use the `Result<T>` to get the response value

myServiceApi interface:
```java

public interface MyServiceApi {
    @GET("/hello")
    Result<List<HelloBean>> getHellos();
}
```

Api usage: 

```java

Result<List<HelloBean>> hellos = myServiceApi.getHellos();
```


the example code you can see current project [UT](https://github.com/easyretrofit/adapter-simple-body/blob/main/src/test/java/io/github/easyretrofit/adapter/simplebody/SimpleBodyMyServiceTest.java) 



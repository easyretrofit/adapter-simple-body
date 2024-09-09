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



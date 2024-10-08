[![Version](https://img.shields.io/maven-central/v/io.github.easyretrofit/adapter-simple-body?logo=apache-maven&style=flat-square)](https://central.sonatype.com/artifact/io.github.easyretrofit/adapter-simple-body)
[![Build](https://github.com/easyretrofit/adapter-simple-body/actions/workflows/build.yml/badge.svg)](https://github.com/easyretrofit/adapter-simple-body/actions/workflows/build.yml/badge.svg)
[![License](https://img.shields.io/github/license/easyretrofit/adapter-simple-body.svg)](http://www.apache.org/licenses/LICENSE-2.0)



# easy-retrofit-adapter-simple-body
When a synchronization request is made, the response value is a `<T>` that is not wrapped by the `Call<T>` Class of Retrofit, 
and this `<T>` has the same structure as the returned value of the called API

## Usage
Maven:
```xml
<dependency>
    <groupId>io.github.easyretrofit</groupId>
    <artifactId>adapter-simple-body</artifactId>
    <version>${latest.version}</version> <!-- 替换为实际的版本号 -->
</dependency>
```

Gradle:
```groovy
implementation 'io.github.easyretrofit:adapter-simple-body:${latest.version}'
```


### used with easy-retrofit

#### create a SimpleBodyCallAdapterFactoryBuilder class
```java

public class SimpleBodyCallAdapterFactoryBuilder extends BaseCallAdapterFactoryBuilder {
    @Override
    public Converter.Factory build() {
        return SimpleBodyCallAdapterFactory.create();
    }
}

```

#### add SimpleBodyCallAdapterFactoryBuilder to your RetrofitBuilder
```java
@RetrofitBuilder(baseUrl = "${app.backend.url}",
        addConverterFactory = {GsonConvertFactoryBuilder.class},
        addCallAdapterFactory = {SimpleBodyCallAdapterFactoryBuilder.class})
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

### set exclude call adapter type
if you add any other call adapter, and those call adapter maybe has conflict with the current call adapter, you can set the call adapter type to exclude

by default, the current call adapter already exclude the official call adapter type (Call, Flowable, Observable, Single, etc.), 
so current call adapter will not handle the official call adapter type. so, you can use this call adapter with official call adapter.

this library provide two methods to set the call adapter type to exclude.

```java
public static SimpleBodyCallAdapterFactory create(Class<?>[] exclude) {
    return new SimpleBodyCallAdapterFactory(exclude, null);
}

public static SimpleBodyCallAdapterFactory create(Class<?>[] exclude, Function<ErrorParameter, ?> customErrorFunction) {
    return new SimpleBodyCallAdapterFactory(exclude, customErrorFunction);
}
```


### use error Function
If your response returns an error Body and the body cannot be parsed by the converter(json, xml or others), resulting in an exception being returned,you can set global exception handling or customize the handling return value

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
If your response returns an error Body and the body cannot be parsed by the converter(json, xml or others), resulting in an exception being returned, you can use this annotation to map the values in your response. If your response object contains fields such as http status code, error message, etc

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



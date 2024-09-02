package io.github.easyretrofit.adapter.simplebody;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.github.easyretrofit.adapter.simplebody.MyService.MyServiceApi;
import io.github.easyretrofit.adapter.simplebody.MyService.HelloBean;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


import java.io.IOException;
import java.util.*;

import static org.junit.Assert.*;

public class SimpleBodyMyServiceTest {
    @Rule public final MockWebServer server = new MockWebServer();
    private MyService myService;
    private MyServiceApi myServiceApi;
    @Before
    public void setUp() {
        myService = new MyService();
    }

    @After
    public void tearDown() throws IOException {
        server.shutdown();
    }

    @Test
    public void simpleBodySuccess200Test() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(server.url("/"))
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(SimpleBodyCallAdapterFactory.create())
                .build();

        myServiceApi = retrofit.create(MyServiceApi.class);
        Gson gson = new GsonBuilder().create();
        String json = gson.toJson(myService.getHellos());
        server.enqueue(new MockResponse().setBody(json));

        Result<List<HelloBean>> hellos = myServiceApi.getHellos();
        assertEquals(200, hellos.getCode());
    }

    @Test
    public void bodySuccess404() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(server.url("/"))
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(SimpleBodyCallAdapterFactory.create())
                .build();

        myServiceApi = retrofit.create(MyServiceApi.class);
        server.enqueue(new MockResponse()
                .setResponseCode(404)
                .setBody("{\"message\":\"Unable to locate resource\"}"));

        Result<List<HelloBean>> hellos;

        hellos = myServiceApi.getHellos();

        assertEquals(404, hellos.getCode());
    }

    @Test
    public void bodySuccess404Static() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(server.url("/"))
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(SimpleBodyCallAdapterFactory.create())
                .build();
        server.enqueue(new MockResponse()
                .setResponseCode(404));

        MyService.ResultStatic<List<HelloBean>> hellos;
        myServiceApi = retrofit.create(MyServiceApi.class);
        hellos = myServiceApi.getHellos2();

        assertEquals(404, hellos.getCode());
    }

    @Test
    public void bodySuccess404WithErrorResponse() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(server.url("/"))
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(SimpleBodyCallAdapterFactory.create(errorParameter -> {
                    throw new RuntimeException(errorParameter.getResponse().toString());
                }))
                .build();
        server.enqueue(new MockResponse()
                .setResponseCode(404));
        myServiceApi = retrofit.create(MyServiceApi.class);

        Result<List<HelloBean>> hellos;
        try {
            hellos = myServiceApi.getHellos3();
        } catch (RuntimeException e) {
            System.out.println(e.getMessage());
            assertNotNull(e.getMessage());
        }
    }



}

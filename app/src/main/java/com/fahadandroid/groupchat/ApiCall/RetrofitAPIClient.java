package com.fahadandroid.groupchat.ApiCall;

import java.security.cert.X509Certificate;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public  class RetrofitAPIClient {
    public static Retrofit retrofit;
    public static APIMethodInterface createRetrofitApi(String baseUrl){
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        // set your desired log level
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        httpClient.connectTimeout(60, TimeUnit.SECONDS);
        httpClient.readTimeout(60, TimeUnit.SECONDS);
        retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                //.addConverterFactory()
                //.client(httpClient.build())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        return retrofit.create(APIMethodInterface.class);
    }
    private static String getFirstCn(X509Certificate cert) {
        String subjectPrincipal = cert.getSubjectX500Principal().toString();
        for (String token : subjectPrincipal.split(",")) {
            int x = token.indexOf("CN=");
            if (x >= 0) {
                return token.substring(x + 3);
            }
        }
        return null;
    }
    Retrofit getRetrofit() {
        return retrofit;
    }
}

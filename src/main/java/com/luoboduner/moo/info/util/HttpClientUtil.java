package com.luoboduner.moo.info.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import okhttp3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

public class HttpClientUtil {

    private static final Logger LOG = LoggerFactory.getLogger(HttpClientUtil.class);

    private static final OkHttpClient HTTP_CLIENT;

    static {
        HTTP_CLIENT = new OkHttpClient.Builder()
                .readTimeout(5, TimeUnit.SECONDS)
                .writeTimeout(5, TimeUnit.SECONDS)
                .build();
    }

    private HttpClientUtil() {}

    public static <T> Optional<T> get(String url, TypeReference<T> typeReference) {
        Request request = new Request.Builder()
                .get()
                .url(url)
                .build();

        String responseString;
        try {
            Response response = HTTP_CLIENT.newCall(request).execute();
            ResponseBody responseBody = response.body();
            if (responseBody == null) {
                LOG.warn("Request {} get invalid response body", request);
                return Optional.empty();
            }
            responseString = responseBody.string();
        } catch (Exception e) {
            LOG.error("Do http get {} error", request.url(), e);
            return Optional.empty();
        }

        try {
            T data = JSON.parseObject(responseString, typeReference);
            return Optional.of(data);
        } catch (Exception e) {
            LOG.error("Parse response string to json error, {}", responseString, e);
        }
        return Optional.empty();
    }

    public static <T> Optional<T> post(String url, Object data, TypeReference<T> typeReference) {

        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json;charset=utf-8"), JSON.toJSONString(data));
        Request request = new Request.Builder()
                .post(requestBody)
                .url(url)
                .build();

        String responseString;
        try {
            Response response = HTTP_CLIENT.newCall(request).execute();
            ResponseBody responseBody = response.body();
            if (responseBody == null) {
                LOG.warn("Request {} post invalid response body", request);
                return Optional.empty();
            }
            responseString = responseBody.string();
        } catch (Exception e) {
            LOG.error("Do http post {} error", request.url(), e);
            return Optional.empty();
        }

        try {
            T responseData = JSON.parseObject(responseString, typeReference);
            return Optional.of(responseData);
        } catch (Exception e) {
            LOG.error("Parse response string to json error, {}", responseString, e);
        }
        return Optional.empty();
    }
}

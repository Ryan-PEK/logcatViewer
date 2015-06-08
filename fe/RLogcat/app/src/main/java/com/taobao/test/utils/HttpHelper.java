package com.taobao.test.utils;

import com.google.gson.Gson;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import java.io.IOException;

/**
 * Created by hongyanyin on 5/13/15.
 */
public class HttpHelper {

    public static String post(String url, Object param)
    {
        return postData(url, param);
    }

    public static String get(String url)
    {
        HttpParams myParams = new BasicHttpParams();
        HttpConnectionParams.setConnectionTimeout(myParams, 10000);
        HttpConnectionParams.setSoTimeout(myParams, 10000);
        HttpClient httpclient = new DefaultHttpClient(myParams );

        try {
            HttpGet httpGet = new HttpGet(url);
            HttpResponse response = httpclient.execute(httpGet);
            String result = EntityUtils.toString(response.getEntity());
            return result;

        } catch (ClientProtocolException e) {
            return "";
        } catch (IOException e) {
            return "";
        } catch (Throwable e)
        {
            e.printStackTrace();
            return "";
        }
    }

    private static String postData(String url,Object obj) {
        // Create a new HttpClient and Post Header
        HttpParams myParams = new BasicHttpParams();
        HttpConnectionParams.setConnectionTimeout(myParams, 15000);
        HttpConnectionParams.setSoTimeout(myParams, 15000);
        HttpClient httpclient = new DefaultHttpClient(myParams );
        Gson gson = new Gson();
        String json=gson.toJson(obj);

        try {

            HttpPost httppost = new HttpPost(url.toString());
            httppost.setHeader("Content-type", "application/json");

            StringEntity se = new StringEntity(gson.toJson(obj));
            se.setContentEncoding(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
            httppost.setEntity(se);

            HttpResponse response = httpclient.execute(httppost);
            String result = EntityUtils.toString(response.getEntity());
            return result;

        } catch (ClientProtocolException e) {
            return "";
        } catch (IOException e) {
            return "";
        } catch (Throwable e)
        {
            e.printStackTrace();
            return "";
        }
    }


}

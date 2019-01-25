package uk.co.holborn.carparkclient;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicHeader;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Scanner;

public class DBConnection {
    private Gson gson;

    DBConnection() {
        gson = new Gson();
    }

    //    public ArrayList<T> getListOfObjects(String url){
//        String json = jsonGetRequest(url);
//        return gson.fromJson(json, typeToken.getType());
//    }
    private String streamToString(InputStream inputStream) {
        return new Scanner(inputStream, "UTF-8").useDelimiter("\\Z").next();
    }

    public DBResponse sendGETRequest(String url) {
       return sendGETRequest(url, null);
    }
    public DBResponse sendGETRequest(String url, BasicHeader[] headers) {
        String body = null;
        int responseCode = 404;
        HttpClient httpClient = HttpClientBuilder.create().build();
        HttpGet httpGet = new HttpGet(url);
        try {
            httpGet.setHeaders(headers);
            HttpResponse response = httpClient.execute(httpGet);
            responseCode = response.getStatusLine().getStatusCode();
            body = streamToString(response.getEntity().getContent());

        } catch (IOException e) {
            e.printStackTrace();
        }
        return new DBResponse(responseCode, body);
    }

    public DBResponse sendPOSTRequest(String url, List<NameValuePair> urlParameters) {
        String body = null;
        int responseCode = 404;
        HttpClient httpClient = HttpClientBuilder.create().build();
        HttpPost httpPost = new HttpPost(url);
        try {
            httpPost.setEntity(new UrlEncodedFormEntity(urlParameters));
            HttpResponse response = httpClient.execute(httpPost);
            responseCode = response.getStatusLine().getStatusCode();
            body = streamToString(response.getEntity().getContent());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new DBResponse(responseCode, body);
    }

    public <T> T jsonStringToObject(String data, TypeToken<T> typeToken){
       return gson.fromJson(data, typeToken.getType());
    };
    public String jsonGetRequest(String urlQueryString) {
        String json = null;
        try {
            URL url = new URL(urlQueryString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoOutput(true);
            connection.setInstanceFollowRedirects(false);
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("charset", "utf-8");
            connection.connect();
            InputStream inStream = connection.getInputStream();
            json = streamToString(inStream); // input stream to string
            connection.disconnect();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return json;
    }

}

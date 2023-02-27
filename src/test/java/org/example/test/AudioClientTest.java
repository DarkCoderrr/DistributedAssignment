package org.example.test;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.eclipse.jetty.client.HttpClient;
import org.example.controller.ResourceServletAudioItems;

import org.eclipse.jetty.client.api.ContentResponse;
import org.eclipse.jetty.client.api.Request;
import org.eclipse.jetty.http.HttpMethod;

public class AudioClientTest {

    private static final int NUM_CLIENTS = 10;
    private static final int NUM_GET_REQUESTS = 20;
    private static final int NUM_POST_REQUESTS = 10;

    public static void main(String[] args) throws Exception {
        ExecutorService executor = Executors.newFixedThreadPool(NUM_CLIENTS);
        ResourceServletAudioItems servlet = new ResourceServletAudioItems();

        // start the server
        try {
            servlet.init();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // create arrays to store response times
        long[] getResponseTimes = new long[NUM_CLIENTS * NUM_GET_REQUESTS];
        long[] postResponseTimes = new long[NUM_CLIENTS * NUM_POST_REQUESTS];

        // create threads for GET requests
        for (int i = 0; i < NUM_GET_REQUESTS; i++) {
            for (int j = 0; j < NUM_CLIENTS; j++) {
                executor.execute(new GetRequestRunnable(servlet, getResponseTimes, i * NUM_CLIENTS + j));
            }
        }

        // create threads for POST requests
        for (int i = 0; i < NUM_POST_REQUESTS; i++) {
            for (int j = 0; j < NUM_CLIENTS; j++) {
                executor.execute(new PostRequestRunnable(servlet, postResponseTimes, i * NUM_CLIENTS + j));
            }
        }

        // wait for all threads to complete
        executor.shutdown();
        while (!executor.isTerminated()) {
            // do nothing
        }

        // print out the response times
        System.out.println("GET Response Times:");
        for (int i = 0; i < getResponseTimes.length; i++) {
            System.out.println(getResponseTimes[i] + " ms");
        }

        System.out.println("POST Response Times:");
        for (int i = 0; i < postResponseTimes.length; i++) {
            System.out.println(postResponseTimes[i] + " ms");
        }
    }
}

class GetRequestRunnable implements Runnable {
    
    private ResourceServletAudioItems servlet;
    private long[] responseTimes;
    private int index;
    
    public GetRequestRunnable(ResourceServletAudioItems servlet, long[] responseTimes, int index) {
        this.servlet = servlet;
        this.responseTimes = responseTimes;
        this.index = index;
    }
    
    @Override
    public void run() {
        try {
            String url = "http://localhost:9090/coen6317/audioitems?id=" + new Random().nextInt(1000);
            HttpClient httpClient = new HttpClient();
            httpClient.start();
            Request request = httpClient.newRequest(url).method(HttpMethod.GET);
            request.param("id","id_1");
            long startTime = System.currentTimeMillis();
            ContentResponse response = request.send();
            long endTime = System.currentTimeMillis();
            responseTimes[index] = endTime - startTime;
            httpClient.stop();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

class PostRequestRunnable implements Runnable {
    
    private ResourceServletAudioItems servlet;
    private long[] responseTimes;
    private int index;
    
    public PostRequestRunnable(ResourceServletAudioItems servlet, long[] responseTimes, int index) {
this.servlet = servlet;
this.responseTimes = responseTimes;
this.index = index;
}
@Override
public void run() {
    try {
    	Random random = new Random();
        String url = "http://localhost:9090/coen6317/audioitems";
        HttpClient httpClient = new HttpClient();
        httpClient.start();
        Request request = httpClient.newRequest(url).method(HttpMethod.POST);
        request.param("id","id_" + random.nextInt(100));
        request.param("artistName", "artist_name_3");
        request.param("albumTitle", "album_title_3");
        request.param("trackTitle", "track_title_3");
        request.param("numCopiesSold", "100");
        request.param("numReviews", "10");
        request.param("trackNumber", "3");
        request.param("year", "2023");
        long startTime = System.currentTimeMillis();
        ContentResponse response = request.send();
        long endTime = System.currentTimeMillis();
        responseTimes[index] = endTime - startTime;
        httpClient.stop();
    } catch (Exception e) {
        e.printStackTrace();
    }
}
}

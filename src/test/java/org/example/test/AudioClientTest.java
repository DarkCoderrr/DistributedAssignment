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

    private static final int NUM_OF_CLIENTS = 10;
    private static final int NUM_OF_GET_REQUESTS = 20;
    private static final int NUM_OF_POST_REQUESTS = 10;

    public static void main(String[] args) throws Exception {
        ExecutorService executor = Executors.newFixedThreadPool(NUM_OF_CLIENTS);
        ResourceServletAudioItems servlet = new ResourceServletAudioItems();

        // start the server
        try {
            servlet.init();
        } catch (Exception e) {
            e.printStackTrace();
        }

       
        long[] getResponseTimes = new long[NUM_OF_CLIENTS * NUM_OF_GET_REQUESTS];
        long[] postResponseTimes = new long[NUM_OF_CLIENTS * NUM_OF_POST_REQUESTS];

       
        for (int i = 0; i < NUM_OF_GET_REQUESTS; i++) {
            for (int j = 0; j < NUM_OF_CLIENTS; j++) {
                executor.execute(new GetRequest(servlet, getResponseTimes, i * NUM_OF_CLIENTS + j));
            }
        }

        
        for (int i = 0; i < NUM_OF_POST_REQUESTS; i++) {
            for (int j = 0; j < NUM_OF_CLIENTS; j++) {
                executor.execute(new PostRequest(servlet, postResponseTimes, i * NUM_OF_CLIENTS + j));
            }
        }

        
        executor.shutdown();
        while (!executor.isTerminated()) {
            // do nothing
        }

    
        System.out.println("GET Response Times in millisecond:");
        for (int i = 0; i < getResponseTimes.length; i++) {
            System.out.println(getResponseTimes[i] + " ms");
        }

        System.out.println("POST Response Times in millisecond:");
        for (int i = 0; i < postResponseTimes.length; i++) {
            System.out.println(postResponseTimes[i] + " ms");
        }
    }
}

class GetRequest implements Runnable {
    
    private ResourceServletAudioItems servlet;
    private long[] responseTimes;
    private int index;
    
    public GetRequest(ResourceServletAudioItems servlet, long[] responseTimes, int index) {
        this.servlet = servlet;
        this.responseTimes = responseTimes;
        this.index = index;
    }
    
    @Override
    public void run() {
        try {
            String url = "http://155.248.226.126:8080/audioitems?id=" + new Random().nextInt(1000);
            HttpClient httpClient = new HttpClient();
            httpClient.start();
            Request request = httpClient.newRequest(url).method(HttpMethod.GET);
            request.param("artist_name","artist_name_1");
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

class PostRequest implements Runnable {
    
    private ResourceServletAudioItems servlet;
    private long[] responseTimes;
    private int index;
    
    public PostRequest(ResourceServletAudioItems servlet, long[] responseTimes, int index) {
this.servlet = servlet;
this.responseTimes = responseTimes;
this.index = index;
}
@Override
public void run() {
    try {
    	Random random = new Random();
        String url = "http://155.248.226.126:8080/audioitems";
        HttpClient httpClient = new HttpClient();
        httpClient.start();
        Request request = httpClient.newRequest(url).method(HttpMethod.POST);
        request.param("artistName", "artist_name_4"+random.nextInt(100));
        request.param("albumTitle", "album_title_4");
        request.param("trackTitle", "track_title_4");
        request.param("numCopiesSold", "1000");
        request.param("numReviews", "100");
        request.param("trackNumber", "2");
        request.param("year", "2021");
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

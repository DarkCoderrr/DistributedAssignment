package org.example.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.example.model.Audio;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

//@WebServlet(name = "audioitems", value="audioitems")

public class ResourceServletAudioItems extends HttpServlet {

    /*
     * ConcurrentHashMap is thread safe;
     */
    ConcurrentHashMap<String, Audio> audioDB = new ConcurrentHashMap<>();
    private int totalCopiesSold = 0;
    /*
     * simply emulation of in memory database;
     */
    @Override
    public void init() throws ServletException {
        Audio audio1 = new Audio();
        audio1.setArtistName("artist_name_1");
        audio1.setAlbumTitle("album_title_1");
        audio1.setTrackTitle("track_title_1");
        audio1.setTrackNumber(1);
        audio1.setYear(2021);
        audio1.setNumReviews(10);
        audio1.setNumCopiesSold(10000);
        audioDB.put("id_1", audio1);

        Audio audio2 = new Audio();
        audio2.setArtistName("artist_name_2");
        audio2.setAlbumTitle("album_title_2");
        audio2.setTrackTitle("track_title_2");
        audio2.setTrackNumber(2);
        audio2.setYear(2022);
        audio2.setNumReviews(5);
        audio2.setNumCopiesSold(5000);
        audioDB.put("id_2", audio2);
    }

    private int calculateTotalCopiesSold() {
        int total = 0;
        for (Audio audio : audioDB.values()) {
            total += audio.getNumCopiesSold();
        }
        return total;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        String id = request.getParameter("id");
        Audio audio = audioDB.get(id);

        if (audio != null) {
        	
            Gson gson = new Gson();
            JsonElement element = gson.toJsonTree(audio);

            /*
             * response in json with as a data model
             */
            PrintWriter out = response.getWriter();
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            out.println("GET RESPONSE IN JSON - single element: " + gson.toJson(audio));
            out.println("TOTAL NUMBER OF COPIES SOLD TILL NOW: " + gson.toJson(calculateTotalCopiesSold()));

//            out.println("GET RESPONSE IN JSON - all elements " + element.toString());
            out.println("GET RESPONSE IN JSON - all elements ");
            List<Audio> audioList = new ArrayList<>(audioDB.values());
            JsonArray jsonArray = gson.toJsonTree(audioList).getAsJsonArray();
            out.println(jsonArray.toString());

            out.flush();
        } else {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            response.getOutputStream().println("Audio item not found");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String id = request.getParameter("id");

        if (audioDB.containsKey(id)) {
            response.sendError(HttpServletResponse.SC_CONFLICT, "Audio item with id " + id + " already exists.");
        } else {
            Audio audio = new Audio();
//            totalCopiesSold = calculateTotalCopiesSold();
            audio.setArtistName(request.getParameter("artistName"));
            audio.setAlbumTitle(request.getParameter("albumTitle"));
            audio.setTrackTitle(request.getParameter("trackTitle"));
            audio.setNumCopiesSold(Integer.parseInt(request.getParameter("numCopiesSold")));
            audio.setNumReviews(Integer.parseInt(request.getParameter("numReviews")));
            audio.setTrackNumber(Integer.parseInt(request.getParameter("trackNumber")));
            audio.setYear(Integer.parseInt(request.getParameter("year")));
//            audio.setTotalCopiesSold(totalCopiesSold);

            audioDB.put(id, audio);
            

            response.setStatus(HttpServletResponse.SC_CREATED);
            response.getOutputStream().println("Audio item with id " + id + " is added to the database.");
        }
    }
    }

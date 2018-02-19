package com.example.popularmovies.data;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class VideosResponse implements IJsonDeserialize {
    private int id;
    private List<Video> results;

    public int getId() {
        return id;
    }

    public List<Video> getResults() {
        return results;
    }

    @Override
    public void fillPropertiesByJsonObject(JSONObject jsonObject) {
        id = jsonObject.optInt("id");

        results = new ArrayList<>();
        JSONArray resultsJsonArray = jsonObject.optJSONArray("results");
        if (resultsJsonArray == null || resultsJsonArray.length() == 0)
            return;

        for (int i = 0; i < resultsJsonArray.length(); i++) {
            JSONObject resultJsonObject = resultsJsonArray.optJSONObject(i);
            if (resultJsonObject == null)
                continue;

            Video video = new Video();
            video.fillPropertiesByJsonObject(resultJsonObject);

            results.add(video);
        }
    }
}

package com.example.popularmovies.data;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ReviewsResponse implements IJsonDeserialize {
    private int id;
    private int page;
    private int totalResults;
    private int totalPages;
    private List<Review> results;

    public int getId() {
        return id;
    }

    public int getPage() {
        return page;
    }

    public int getTotalResults() {
        return totalResults;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public List<Review> getResults() {
        return results;
    }

    @Override
    public void fillPropertiesByJsonObject(JSONObject jsonObject) {
        id = jsonObject.optInt("id");
        page = jsonObject.optInt("page");
        totalResults = jsonObject.optInt("total_results");
        totalPages = jsonObject.optInt("total_pages");

        results = new ArrayList<>();
        JSONArray resultsJsonArray = jsonObject.optJSONArray("results");
        if (resultsJsonArray == null || resultsJsonArray.length() == 0)
            return;

        for (int i = 0; i < resultsJsonArray.length(); i++) {
            JSONObject resultJsonObject = resultsJsonArray.optJSONObject(i);
            if (resultJsonObject == null)
                continue;

            Review review = new Review();
            review.fillPropertiesByJsonObject(resultJsonObject);

            results.add(review);
        }
    }
}

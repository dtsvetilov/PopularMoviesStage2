package com.example.popularmovies.data;

import org.json.JSONObject;

public class Review implements IJsonDeserialize {
    private String id;
    private String author;
    private String content;
    private String url;

    public String getId() {
        return id;
    }

    public String getAuthor() {
        return author;
    }

    public String getContent() {
        return content;
    }

    public String getUrl() {
        return url;
    }

    @Override
    public void fillPropertiesByJsonObject(JSONObject jsonObject) {
        id = jsonObject.optString("id");
        author = jsonObject.optString("author");
        content = jsonObject.optString("content");
        url = jsonObject.optString("url");
    }
}

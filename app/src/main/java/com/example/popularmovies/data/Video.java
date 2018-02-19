package com.example.popularmovies.data;

import org.json.JSONObject;

public class Video implements IJsonDeserialize {
    private String id;
    private String iso6391;
    private String iso31661;
    private String key;
    private String name;
    private String site;
    private int size;
    private String type;

    public String getId() {
        return id;
    }

    public String getIso6391() {
        return iso6391;
    }

    public String getIso31661() {
        return iso31661;
    }

    public String getKey() {
        return key;
    }

    public String getName() {
        return name;
    }

    public String getSite() {
        return site;
    }

    public int getSize() {
        return size;
    }

    public String getType() {
        return type;
    }

    @Override
    public void fillPropertiesByJsonObject(JSONObject jsonObject) {
        id = jsonObject.optString("id");
        iso6391 = jsonObject.optString("iso_639_1");
        iso31661 = jsonObject.optString("iso_3166_1");
        key = jsonObject.optString("key");
        name = jsonObject.optString("name");
        site = jsonObject.optString("site");
        size = jsonObject.optInt("size");
        type = jsonObject.optString("type");
    }
}


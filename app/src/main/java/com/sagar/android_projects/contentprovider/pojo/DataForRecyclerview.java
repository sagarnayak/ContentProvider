package com.sagar.android_projects.contentprovider.pojo;

/**
 * Created by sagar on 10/27/2017.
 * this pojo is used to encapsulate the data for the recyclerview adaper.
 * as both table used have the same structure this pojo is used for both of them.
 */
public class DataForRecyclerview {

    private String id;
    private String value;

    @SuppressWarnings("unused")
    public DataForRecyclerview() {
    }

    public DataForRecyclerview(String id, String value) {
        this.id = id;
        this.value = value;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getValue() {
        return value;
    }

    @SuppressWarnings("unused")
    public void setValue(String value) {
        this.value = value;
    }
}

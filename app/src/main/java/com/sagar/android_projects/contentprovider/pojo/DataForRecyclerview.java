package com.sagar.android_projects.contentprovider.pojo;

/**
 * Created by sagar on 10/27/2017.
 */
public class DataForRecyclerview {

    private String id;
    private String value;

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

    public void setValue(String value) {
        this.value = value;
    }
}

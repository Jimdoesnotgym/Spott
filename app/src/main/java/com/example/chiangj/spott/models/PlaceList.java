package com.example.chiangj.spott.models;

import java.util.List;

public class PlaceList<T, U> {
    private List<U> html_attributions;
    private List<T> results;
    private String status;

    public List<U> getHtml_attributions() {
        return html_attributions;
    }

    public List<T> getResults() {
        return results;
    }
}

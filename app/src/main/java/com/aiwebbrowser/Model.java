package com.aiwebbrowser;

public class Model {
    private String id;
    private String name;
    private String provider;
    private boolean selected;

    public Model(String id, String name, String provider) {
        this.id = id;
        this.name = name;
        this.provider = provider;
        this.selected = false;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }
}
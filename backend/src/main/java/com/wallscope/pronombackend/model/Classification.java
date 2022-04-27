package com.wallscope.pronombackend.model;

public class Classification {
    private final String id;
    private final String name;

    public Classification(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }

    @Override
    public String toString() {
        return "Classification{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}
